package dialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import model.Appointment;
import model.Doctor;
import model.Patient;

public class AppointmentDialog extends JDialog {
    private Appointment appointment;
    private boolean saved = false;

    private JTextField cinField;
    private JLabel patientNameLabel;
    private JComboBox<String> specializationCombo;
    private JComboBox<Doctor> doctorCombo;
    private JTextField dateField;
    private JSpinner durationSpinner;
    private JTextField reasonField;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JComboBox<String> statusCombo;
    private JDatePickerImpl datePicker;
    private JTextField timeField;

    private PatientDAO patientDAO = new PatientDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public AppointmentDialog(Window owner, Appointment appointment) {
        super(owner, "Gestion des Rendez-vous", ModalityType.APPLICATION_MODAL);
        this.appointment = appointment;

        initComponents();
        populateFields();

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Panel with Image
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        try {
            ImageIcon appointmentIcon = new ImageIcon(getClass().getResource("/appointment.png"));
            Image img = appointmentIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            headerPanel.add(imageLabel, BorderLayout.CENTER);

            JLabel titleLabel = new JLabel(appointment == null ? "Nouveau Rendez-vous" : "Modifier Rendez-vous", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setForeground(new Color(255,255,255));
            headerPanel.add(titleLabel, BorderLayout.SOUTH);
        } catch (Exception e) {
            JLabel titleLabel = new JLabel(appointment == null ? "Nouveau Rendez-vous" : "Modifier Rendez-vous", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setForeground(new Color(255,255,255));
            headerPanel.add(titleLabel, BorderLayout.CENTER);
        }

        add(headerPanel, BorderLayout.NORTH);

        // Main Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Color labelColor = new Color(70, 70, 70);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // CIN Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel cinLabel = new JLabel("CIN Patient:");
        cinLabel.setFont(labelFont);
        cinLabel.setForeground(labelColor);
        formPanel.add(cinLabel, gbc);

        gbc.gridx = 1;
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setPreferredSize(new Dimension(230, 30));

        cinField = new JTextField();
        cinField.setFont(fieldFont);
        cinField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        cinField.addActionListener(e -> searchPatientByCin());
        searchPanel.add(cinField, BorderLayout.CENTER);

        JButton searchIconButton = new JButton();
        searchIconButton.setPreferredSize(new Dimension(30, 30));
        searchIconButton.setFocusable(false);
        searchIconButton.setContentAreaFilled(false);
        searchIconButton.setBorderPainted(false);
        searchIconButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchIconButton.setToolTipText("Rechercher");

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/search.png"));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            searchIconButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            searchIconButton.setText("🔍");
        }

        searchIconButton.addActionListener(e -> searchPatientByCin());
        searchPanel.add(searchIconButton, BorderLayout.EAST);
        formPanel.add(searchPanel, gbc);

        // Patient Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel patientLabel = new JLabel("Patient:");
        patientLabel.setFont(labelFont);
        patientLabel.setForeground(labelColor);
        formPanel.add(patientLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        patientNameLabel = new JLabel("");
        patientNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        patientNameLabel.setForeground(new Color(44, 62, 80));
        formPanel.add(patientNameLabel, gbc);
        gbc.gridwidth = 1;

        // Specialization
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel specLabel = new JLabel("Spécialité:");
        specLabel.setFont(labelFont);
        specLabel.setForeground(labelColor);
        formPanel.add(specLabel, gbc);

        gbc.gridx = 1;
        specializationCombo = new JComboBox<>();
        specializationCombo.setBackground(Color.WHITE);
        specializationCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        specializationCombo.setFont(fieldFont);
        loadSpecializations();
        specializationCombo.addActionListener(e -> updateDoctorCombo());
        formPanel.add(specializationCombo, gbc);

        // Doctor
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel doctorLabel = new JLabel("Médecin:");
        doctorLabel.setFont(labelFont);
        doctorLabel.setForeground(labelColor);
        formPanel.add(doctorLabel, gbc);

        gbc.gridx = 1;
        doctorCombo = new JComboBox<>();
        doctorCombo.setPreferredSize(new Dimension(250, 30));
        doctorCombo.setFont(fieldFont);
        doctorCombo.setBackground(Color.WHITE);
        doctorCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        updateDoctorCombo();
        formPanel.add(doctorCombo, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel dateLabel = new JLabel("Date (JJ/MM/AAAA):");
        dateLabel.setFont(labelFont);
        dateLabel.setForeground(labelColor);
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        UtilDateModel model = new UtilDateModel();
        model.setSelected(true);

        Properties p = new Properties();
        p.put("text.today", "Aujourd'hui");
        p.put("text.month", "Mois");
        p.put("text.year", "Année");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setPreferredSize(new Dimension(200, 30));

        datePicker.getModel().addChangeListener(e -> {
            java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
            if (selectedDate != null) {
                LocalDate selectedLocalDate = selectedDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                
                if (selectedLocalDate.isBefore(LocalDate.now().plusDays(1))) {
                    JOptionPane.showMessageDialog(this,
                        "La date du rendez-vous doit être à partir de demain !",
                        "Date invalide",
                        JOptionPane.WARNING_MESSAGE);
                    datePicker.getModel().setSelected(false);
                }
            }
        });

        for (Component comp : datePicker.getComponents()) {
            if (comp instanceof JButton) {
                JButton dateButton = (JButton) comp;
                dateButton.setPreferredSize(new Dimension(30, 30));
                dateButton.setContentAreaFilled(false);
                dateButton.setBorderPainted(false);
                dateButton.setFocusPainted(false);
                dateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/calendar.png"));
                    Image scaledImg = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
                    dateButton.setIcon(new ImageIcon(scaledImg));
                    dateButton.setText("");
                } catch (Exception e) {
                    dateButton.setText("📅");
                }
            }
        }

        formPanel.add(datePicker, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel timeLabel = new JLabel("Heure (HH:MM):");
        timeLabel.setFont(labelFont);
        timeLabel.setForeground(labelColor);
        formPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        timeField = new JTextField();
        timeField.setPreferredSize(new Dimension(200, 30));
        timeField.setFont(fieldFont);
        timeField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        timeField.setText("09:00");
        formPanel.add(timeField, gbc);

        // Duration
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel durationLabel = new JLabel("Durée (minutes):");
        durationLabel.setFont(labelFont);
        durationLabel.setForeground(labelColor);
        formPanel.add(durationLabel, gbc);

        gbc.gridx = 1;
        SpinnerNumberModel durationModel = new SpinnerNumberModel(30, 15, 180, 15);
        durationSpinner = new JSpinner(durationModel);
        durationSpinner.setPreferredSize(new Dimension(100, 30));
        durationSpinner.setFont(fieldFont);
        durationSpinner.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(durationSpinner, gbc);

        // Reason
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel reasonLabel = new JLabel("Motif:");
        reasonLabel.setFont(labelFont);
        reasonLabel.setForeground(labelColor);
        formPanel.add(reasonLabel, gbc);

        gbc.gridx = 1;
        reasonField = new JTextField();
        reasonField.setPreferredSize(new Dimension(300, 30));
        reasonField.setFont(fieldFont);
        reasonField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(reasonField, gbc);

        // Historique des rendez-vous
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel historyLabel = new JLabel("Historique du patient:");
        historyLabel.setFont(labelFont);
        historyLabel.setForeground(labelColor);
        formPanel.add(historyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        String[] columnNames = {"Date", "Heure", "Médecin", "Statut"};
        historyModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(historyModel);
        historyTable.setFont(fieldFont);
        historyTable.setRowHeight(24);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        historyTable.setEnabled(false);
        historyTable.setGridColor(new Color(220, 220, 220));

        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setPreferredSize(new Dimension(450, 120));
        historyScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(historyScrollPane, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        // Status
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel statusLabel = new JLabel("Statut:");
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(labelColor);
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        statusCombo = new JComboBox<>();
        statusCombo.setFont(fieldFont);
        statusCombo.setBackground(Color.WHITE);
        statusCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        statusCombo.addItem("SCHEDULED");
        statusCombo.addItem("COMPLETED");
        statusCombo.addItem("CANCELLED");
        statusCombo.addItem("NO_SHOW");
        formPanel.add(statusCombo, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        Dimension buttonSize = new Dimension(140, 40);

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setPreferredSize(buttonSize);
        saveButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        saveButton.addActionListener(e -> saveAppointment());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void searchPatientByCin() {
        String cin = cinField.getText().trim().toUpperCase();

        if (cin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un CIN", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cin.matches("^[A-Z]\\d{6}$|^[A-Z]{2}\\d{5}$")) {
            JOptionPane.showMessageDialog(this, "Le format attendu soit:LNNNNNN (1 lettres + 6 chiffres) "
            		+ "ou LLNNNNN (2 lettres + 5 chiffres ", "Format Cin Invalide", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Patient patient = patientDAO.findByCin(cin);

            if (patient != null) {
                patientNameLabel.setText(patient.getFullName());
                loadAppointmentHistory(patient.getId());
            } else {
                patientNameLabel.setText("Patient non trouvé");
                historyModel.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Aucun patient trouvé avec ce CIN", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la recherche du patient: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDoctorCombo() {
        doctorCombo.removeAllItems();
        String specialization = (String) specializationCombo.getSelectedItem();
        if (specialization != null) {
            List<Doctor> doctors = doctorDAO.findBySpecialization(specialization);
            for (Doctor doctor : doctors) {
                doctorCombo.addItem(doctor);
            }
        }
    }

    private void populateFields() {
        if (appointment != null) {
            cinField.setText(String.valueOf(appointment.getPatient().getId()));
            patientNameLabel.setText(appointment.getPatient().getFullName());

            specializationCombo.setSelectedItem(appointment.getDoctor().getSpecialization());
            updateDoctorCombo();

            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                Doctor doctor = doctorCombo.getItemAt(i);
                if (doctor.getId() == appointment.getDoctor().getId()) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }

            java.util.Date date = java.util.Date.from(appointment.getDateTime().toLocalDate()
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            datePicker.getModel().setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePicker.getModel().setSelected(true);

            timeField.setText(appointment.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            durationSpinner.setValue(appointment.getDuration());
            reasonField.setText(appointment.getReason());
            
            for (int i = 0; i < statusCombo.getItemCount(); i++) {
                if (statusCombo.getItemAt(i).equals(appointment.getStatus())) {
                    statusCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void saveAppointment() {
        if (cinField.getText().trim().isEmpty() || patientNameLabel.getText().isEmpty() || 
            patientNameLabel.getText().equals("Patient non trouvé")) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez rechercher et sélectionner un patient valide!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (doctorCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un médecin!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime dateTime;
        try {
            java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner une date!", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate date = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if (date.isBefore(LocalDate.now().plusDays(1))) {
                JOptionPane.showMessageDialog(this, 
                    "La date du rendez-vous doit être à partir de demain!", 
                    "Date invalide", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            dateTime = LocalDateTime.of(date, time);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Format de date ou d'heure invalide!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (appointment == null) {
            appointment = new Appointment();
        }

        Patient patient = patientDAO.findByCin(cinField.getText().trim());
        if (patient == null) {
            JOptionPane.showMessageDialog(this, 
                "Patient non trouvé!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        appointment.setPatient(patient);
        appointment.setDoctor((Doctor) doctorCombo.getSelectedItem());
        appointment.setDateTime(dateTime);
        appointment.setDuration((Integer) durationSpinner.getValue());
        appointment.setReason(reasonField.getText().trim());
        appointment.setStatus((String) statusCombo.getSelectedItem());

        boolean success = appointment.getId() == 0 ? 
            appointmentDAO.create(appointment) : appointmentDAO.update(appointment);

        if (success) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'enregistrement du rendez-vous!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private void loadSpecializations() {
        List<String> specializations = doctorDAO.findAllSpecializations();
        specializationCombo.removeAllItems();
        for (String spec : specializations) {
            specializationCombo.addItem(spec);
        }
    }

    private void loadAppointmentHistory(int patientId) {
        historyModel.setRowCount(0);

        List<Appointment> appointments = appointmentDAO.findByPatient(patientId);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Appointment a : appointments) {
            String date = a.getDateTime().toLocalDate().format(dateFormatter);
            String time = a.getDateTime().toLocalTime().format(timeFormatter);
            String doctor = a.getDoctor().getFullName();
            String status = a.getStatus();
            historyModel.addRow(new Object[]{date, time, doctor, status});
        }
    }

    class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private static final String DATE_PATTERN = "dd/MM/yyyy";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}