package dialog;

import dao.PatientDAO;
import model.Patient;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Properties;

import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;

public class PatientDialog extends JDialog {

    private Patient patient;
    private boolean saved = false;

    private JTextField cinField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JDatePickerImpl datePicker;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextArea medicalHistoryArea;
    private JComboBox<String> couvertureCombo;

    private PatientDAO patientDAO = new PatientDAO();

    public PatientDialog(Window owner, Patient patient) {
        super(owner, "Informations du Patient", ModalityType.APPLICATION_MODAL);

        this.patient = patient;

        initComponents();
        populateFields();

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // CIN
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("CIN du patient:"), gbc);

        gbc.gridx = 1;
        cinField = new JTextField(18);
        formPanel.add(cinField, gbc);

        gbc.gridx = 2;
        JButton searchButton = new JButton("Rechercher");
        formPanel.add(searchButton, gbc);

        searchButton.addActionListener(e -> {
            String cin = cinField.getText().trim();
            if (cin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un CIN!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Patient p = patientDAO.findByCin(cin);
            if (p != null) {
                this.patient = p;
                populateFields();
                JOptionPane.showMessageDialog(this, "Patient trouvé et chargé.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Patient non trouvé. Vous pouvez saisir ses informations.", "Information", JOptionPane.INFORMATION_MESSAGE);
                this.patient = null;
                clearFieldsExceptCIN();
            }
        });

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);
        gbc.gridwidth = 1;

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);
        gbc.gridwidth = 1;

        // Couverture
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Couverture:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        couvertureCombo = new JComboBox<>(new String[]{"Payant", "AMO", "CNSS", "Gratuit"});
        formPanel.add(couvertureCombo, gbc);
        gbc.gridwidth = 1;

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Date de naissance (JJ/MM/AAAA):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Aujourd'hui");
        properties.put("text.month", "Mois");
        properties.put("text.year", "Année");
        datePicker = new JDatePickerImpl(new JDatePanelImpl(model, properties), new DateLabelFormatter());
        datePicker.setTextEditable(true); // Activer la saisie manuelle
        datePicker.setShowYearButtons(true); // Afficher les boutons pour changer l'année

        // Personnaliser le bouton du calendrier
        for (Component comp : datePicker.getComponents()) {
            if (comp instanceof JButton) {
                JButton dateButton = (JButton) comp;
                dateButton.setText(""); // Supprimer le texte "..."
                dateButton.setPreferredSize(new Dimension(25, 20));
                dateButton.setContentAreaFilled(false);
                dateButton.setBorderPainted(false);
                dateButton.setFocusPainted(false);
                dateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/calender.png"));
                    if (icon.getIconWidth() == -1) {
                        throw new Exception("Icône non trouvée");
                    }
                    Image scaledImg = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    dateButton.setIcon(new ImageIcon(scaledImg));
                } catch (Exception e) {
                    dateButton.setText("📅"); // Caractère de secours
                    System.err.println("Erreur de chargement de l'icône : " + e.getMessage());
                }
            }
        }

        // Ajouter un écouteur pour valider la date saisie manuellement
        JTextComponent textField = datePicker.getJFormattedTextField();
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                validateManualDateInput();
            }
        });

        formPanel.add(datePicker, gbc);
        gbc.gridwidth = 1;

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Téléphone:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);
        gbc.gridwidth = 1;

        // Address
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Adresse:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        addressField = new JTextField(30);
        formPanel.add(addressField, gbc);
        gbc.gridwidth = 1;

        // Medical History
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Antécédents médicaux:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        medicalHistoryArea = new JTextArea(5, 30);
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(medicalHistoryArea);
        formPanel.add(scrollPane, gbc);
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> savePatient());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void validateManualDateInput() {
        JTextComponent textField = datePicker.getJFormattedTextField();
        String input = textField.getText().trim();
        if (!input.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false); // Désactiver l'interprétation laxiste des dates
                java.util.Date parsedDate = sdf.parse(input);
                LocalDate localDate = parsedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                // Vérifier si la date est dans le futur
                if (localDate.isAfter(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "La date de naissance ne peut pas être dans le futur!", "Erreur", JOptionPane.ERROR_MESSAGE);
                    datePicker.getModel().setSelected(false);
                    textField.setText("");
                } else {
                    datePicker.getModel().setDate(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
                    datePicker.getModel().setSelected(true);
                }
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(this, "Format de date invalide! Utilisez JJ/MM/AAAA", "Erreur", JOptionPane.ERROR_MESSAGE);
                datePicker.getModel().setSelected(false);
                textField.setText("");
            }
        }
    }

    private void populateFields() {
        if (patient != null) {
            firstNameField.setText(patient.getFirstName());
            lastNameField.setText(patient.getLastName());
            if (patient.getDateOfBirth() != null) {
                LocalDate dob = patient.getDateOfBirth();
                datePicker.getModel().setDate(dob.getYear(), dob.getMonthValue() - 1, dob.getDayOfMonth());
                datePicker.getModel().setSelected(true);
            } else {
                datePicker.getModel().setSelected(false);
            }
            phoneField.setText(patient.getPhone());
            addressField.setText(patient.getAddress());
            medicalHistoryArea.setText(patient.getMedicalHistory());
            if (patient.getCin() != null) {
                cinField.setText(patient.getCin());
            }
            if (patient.getCouverture() != null) {
                couvertureCombo.setSelectedItem(patient.getCouverture());
            }
        }
    }

    private void clearFieldsExceptCIN() {
        firstNameField.setText("");
        lastNameField.setText("");
        datePicker.getModel().setSelected(false);
        datePicker.getJFormattedTextField().setText("");
        phoneField.setText("");
        addressField.setText("");
        medicalHistoryArea.setText("");
        couvertureCombo.setSelectedIndex(0);
    }

    private void savePatient() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Les champs Nom et Prénom sont obligatoires!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cinField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le CIN est obligatoire!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        LocalDate dob;
        if (selectedDate != null) {
            dob = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } else {
            JOptionPane.showMessageDialog(this, "La date de naissance est obligatoire!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validation de la date de naissance
        if (dob.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "La date de naissance ne peut pas être dans le futur!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (patient == null) {
            patient = new Patient();
        }

        patient.setCin(cinField.getText().trim());
        patient.setFirstName(firstNameField.getText().trim());
        patient.setLastName(lastNameField.getText().trim());
        patient.setDateOfBirth(dob);
        patient.setPhone(phoneField.getText().trim());
        patient.setAddress(addressField.getText().trim());
        patient.setMedicalHistory(medicalHistoryArea.getText().trim());
        patient.setCouverture((String) couvertureCombo.getSelectedItem());

        boolean success;
        try {
            if (patient.getId() == 0) {
                success = patientDAO.create(patient);
            } else {
                success = patientDAO.update(patient);
            }

            if (success) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du patient!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public void setPreFilledCin(String cin) {
        if (cinField != null) {
            cinField.setText(cin);
        }
    }

    public Patient getPatient() {
        return patient;
    }

    private class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String DATE_PATTERN = "dd/MM/yyyy";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value instanceof java.util.Date) {
                return dateFormatter.format((java.util.Date) value);
            }
            return "";
        }
    }
}