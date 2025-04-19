package dialog;

import dao.DoctorDAO;
import model.Doctor;

import javax.swing.*;
import java.awt.*;

public class DoctorDialog extends JDialog {

    private Doctor doctor;
    private boolean saved = false;
    
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> specializationCombo;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea scheduleArea;
    private JComboBox<String> workingDaysCombo;  // Changé en ComboBox pour une meilleure UX
    private JSpinner maxPatientsSpinner;
    
    private DoctorDAO doctorDAO = new DoctorDAO();
    
    public DoctorDialog(Window owner, Doctor doctor) {
        super(owner, "Informations du Médecin", ModalityType.APPLICATION_MODAL);
        
        this.doctor = doctor;
        
        initComponents();
        populateFields();
        
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Form panel avec une taille préférée
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setPreferredSize(new Dimension(600, 500));
        
        // Police et style communs
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Prénom:"), gbc);
        
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nom:"), gbc);
        
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);
        
        // Specialization
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Spécialisation:"), gbc);
        
        gbc.gridx = 1;
        specializationCombo = new JComboBox<>();
        String[] specializations = {
            "Médecin généraliste", "Cardiologue", "Dermatologue", "Pédiatre", 
            "Gynécologue", "Ophtalmologue", "ORL", "Psychiatre", "Neurologue",
            "Gastro-entérologue", "Dentiste", "Autre"
        };
        for (String specialization : specializations) {
            specializationCombo.addItem(specialization);
        }
        specializationCombo.setEditable(true); // Allow custom specializations
        formPanel.add(specializationCombo, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Téléphone:"), gbc);
        
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Schedule avec un style amélioré
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel scheduleLabel = new JLabel("Horaires:");
        scheduleLabel.setFont(labelFont);
        formPanel.add(scheduleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        scheduleArea = new JTextArea(5, 30);
        scheduleArea.setFont(fieldFont);
        scheduleArea.setLineWrap(true);
        scheduleArea.setWrapStyleWord(true);
        scheduleArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(scrollPane, gbc);

        // Working Days
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Jours de travail:"), gbc);

        gbc.gridx = 1;
        // Initialisation du JComboBox avant son utilisation
        workingDaysCombo = new JComboBox<>(new String[] {
            "Lundi-Vendredi",
            "Lundi-Samedi",
            "Lundi-Dimanche",
            "Samedi-Dimanche",
            "Autre"
        });
        workingDaysCombo.setEditable(true); // Permet la saisie personnalisée
        formPanel.add(workingDaysCombo, gbc);

        // Mise à jour automatique des horaires en fonction des jours de travail
        workingDaysCombo.addActionListener(e -> updateScheduleBasedOnWorkingDays());
        
        add(formPanel, BorderLayout.CENTER);

        // Max Patients
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Patients max/jour:"), gbc);

        gbc.gridx = 1;
        maxPatientsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        maxPatientsSpinner.setPreferredSize(new Dimension(100, 25));
        formPanel.add(maxPatientsSpinner, gbc);

        // Ajout du listener après l'initialisation
        maxPatientsSpinner.addChangeListener(e -> updateScheduleBasedOnWorkingDays());
        
        // Panel des boutons avec style amélioré
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        Dimension buttonSize = new Dimension(120, 35);
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.setFont(buttonFont);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setPreferredSize(buttonSize);
        saveButton.setFont(buttonFont);
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> saveDoctor());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void populateFields() {
        if (doctor != null) {
            firstNameField.setText(doctor.getFirstName());
            lastNameField.setText(doctor.getLastName());
            phoneField.setText(doctor.getPhone());
            emailField.setText(doctor.getEmail());
            scheduleArea.setText(doctor.getSchedule());
            workingDaysCombo.setSelectedItem(doctor.getWorkingDays());
            maxPatientsSpinner.setValue(doctor.getMaxPatientsPerDay());
            
            // Set specialization if it exists in the combo box, otherwise add it
            String specialization = doctor.getSpecialization();
            boolean found = false;
            for (int i = 0; i < specializationCombo.getItemCount(); i++) {
                if (specializationCombo.getItemAt(i).equals(specialization)) {
                    specializationCombo.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found && specialization != null && !specialization.isEmpty()) {
                specializationCombo.addItem(specialization);
                specializationCombo.setSelectedItem(specialization);
            }
            
            phoneField.setText(doctor.getPhone());
            emailField.setText(doctor.getEmail());
            scheduleArea.setText(doctor.getSchedule());
            
            // Set working days if it exists in the combo box, otherwise add it
            String workingDays = doctor.getWorkingDays();
            boolean foundWorkingDays = false;
            for (int i = 0; i < workingDaysCombo.getItemCount(); i++) {
                if (workingDaysCombo.getItemAt(i).equals(workingDays)) {
                    workingDaysCombo.setSelectedIndex(i);
                    foundWorkingDays = true;
                    break;
                }
            }
            if (!foundWorkingDays && workingDays != null && !workingDays.isEmpty()) {
                workingDaysCombo.addItem(workingDays);
                workingDaysCombo.setSelectedItem(workingDays);
            }
            
            maxPatientsSpinner.setValue(doctor.getMaxPatientsPerDay());
        } else {
            // Default schedule template
            scheduleArea.setText("Lundi: 9h00 - 17h00\n" +
                               "Mardi: 9h00 - 17h00\n" +
                               "Mercredi: 9h00 - 17h00\n" +
                               "Jeudi: 9h00 - 17h00\n" +
                               "Vendredi: 9h00 - 17h00\n" +
                               "Samedi: Fermé\n" +
                               "Dimanche: Fermé");
        }
    }
    
    private void updateScheduleBasedOnWorkingDays() {
        String workingDays = (String) workingDaysCombo.getSelectedItem();
        int maxPatients = (Integer) maxPatientsSpinner.getValue();
        
        StringBuilder schedule = new StringBuilder();
        schedule.append("Jours de travail: ").append(workingDays).append("\n");
        schedule.append("Nombre maximum de patients par jour: ").append(maxPatients).append("\n\n");
        
        // Ajout des horaires par défaut pour chaque jour
        String[] days = workingDays.split("-");
        if (days.length == 2) {
            String[] allDays = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
            boolean isWorkingDay = false;
            
            for (String day : allDays) {
                if (day.equals(days[0])) isWorkingDay = true;
                if (isWorkingDay) {
                    schedule.append(day).append(": 09:00-12:00, 14:00-17:00\n");
                }
                if (day.equals(days[1])) isWorkingDay = false;
            }
        }
        
        scheduleArea.setText(schedule.toString());
    }
    
    private void saveDoctor() {
        // Validate fields
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Les champs Nom et Prénom sont obligatoires!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String workingDays = (String) workingDaysCombo.getSelectedItem();
        if (workingDays == null || workingDays.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Les jours de travail sont obligatoires!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String specialization = (String) specializationCombo.getSelectedItem();
        if (specialization == null || specialization.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La spécialisation est obligatoire!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create or update doctor
        if (doctor == null) {
            doctor = new Doctor();
        }
        
        doctor.setFirstName(firstNameField.getText().trim());
        doctor.setLastName(lastNameField.getText().trim());
        doctor.setSpecialization(specialization.trim());
        doctor.setPhone(phoneField.getText().trim());
        doctor.setEmail(emailField.getText().trim());
        doctor.setSchedule(scheduleArea.getText().trim());
        doctor.setWorkingDays(workingDays.trim());
        doctor.setMaxPatientsPerDay((Integer)maxPatientsSpinner.getValue());
        
        boolean success;
        if (doctor.getId() == 0) {
            success = doctorDAO.create(doctor);
        } else {
            success = doctorDAO.update(doctor);
        }
        
        if (success) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du médecin!", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    public Doctor getDoctor() {
        return doctor;
    }

}