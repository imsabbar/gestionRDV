package dialog;
import model.Receptionist;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import com.toedter.calendar.JCalendar;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;

public class ReceptionistDialog extends JDialog {
    private Receptionist receptionist;
    private boolean saved = false;
    
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> sexeCombo;
    private JFormattedTextField dateOfBirthField;
    private JTextField identifiantField;
    private JButton calendarButton;

    public ReceptionistDialog(Window owner, Receptionist receptionist) {
        super(owner, "Informations Réceptionniste", ModalityType.APPLICATION_MODAL);
        this.receptionist = receptionist;
        
        initComponents();
        populateFields();
        
        setPreferredSize(new Dimension(600, 650)); // Taille agrandie
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel d'en-tête avec image
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Bleu
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        try {
            ImageIcon receptionistIcon = new ImageIcon(getClass().getResource("/sec4.png"));
            // Redimensionner l'image
            Image img = receptionistIcon.getImage();
            Image scaledImg = img.getScaledInstance(180, 120, Image.SCALE_SMOOTH); // Image agrandie
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            headerPanel.add(imageLabel, BorderLayout.CENTER);
            
            JLabel titleLabel = new JLabel("Nouveau Réceptionniste", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Police légèrement agrandie
            titleLabel.setForeground(new Color(255, 255, 255));
            headerPanel.add(titleLabel, BorderLayout.SOUTH);
        } catch (Exception e) {
            JLabel titleLabel = new JLabel("Nouveau Réceptionniste", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(new Color(255, 255, 255));
            headerPanel.add(titleLabel, BorderLayout.CENTER);
        }
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel du formulaire avec plus d'espace
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30)); // Marges augmentées
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8); // Espacement augmenté
        gbc.anchor = GridBagConstraints.WEST;
        
        // Style commun pour les labels
        Font labelFont = new Font("Arial", Font.PLAIN, 13); // Police légèrement agrandie
        Color labelColor = new Color(70, 70, 70);
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("Prénom:");
        firstNameLabel.setFont(labelFont);
        firstNameLabel.setForeground(labelColor);
        formPanel.add(firstNameLabel, gbc);
        
        gbc.gridx = 1;
        firstNameField = new JTextField(25); // Champ légèrement élargi
        firstNameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Nom:");
        lastNameLabel.setFont(labelFont);
        lastNameLabel.setForeground(labelColor);
        formPanel.add(lastNameLabel, gbc);
        
        gbc.gridx = 1;
        lastNameField = new JTextField(25);
        lastNameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(lastNameField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel phoneLabel = new JLabel("Téléphone:");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(labelColor);
        formPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(phoneField, gbc);
        
        // Date de naissance avec calendrier
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel dobLabel = new JLabel("Date de naissance:");
        dobLabel.setFont(labelFont);
        dobLabel.setForeground(labelColor);
        formPanel.add(dobLabel, gbc);

        gbc.gridx = 1;
        JPanel datePanel = new JPanel(new BorderLayout(5, 0)); // Espacement augmenté
        
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/####");
            formatter.setPlaceholderCharacter('_');
            dateOfBirthField = new JFormattedTextField(formatter);
            dateOfBirthField.setColumns(12); // Champ légèrement élargi
            dateOfBirthField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        } catch (ParseException e) {
            dateOfBirthField = new JFormattedTextField();
        }

        // Bouton calendrier
        calendarButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/calender.png"));
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                icon = (ImageIcon)UIManager.getIcon("FileView.directoryIcon");
            }
            
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(45, 40, Image.SCALE_SMOOTH); // Bouton légèrement agrandi
            calendarButton.setIcon(new ImageIcon(newImg));
        } catch (Exception e) {
            calendarButton.setText("📅");
        }
        
        calendarButton.setPreferredSize(new Dimension(40, 30)); // Taille augmentée
        calendarButton.setMargin(new Insets(0, 0, 0, 0));
        calendarButton.setBackground(Color.WHITE);
        calendarButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        calendarButton.addActionListener(e -> showCalendar());

        datePanel.add(dateOfBirthField, BorderLayout.CENTER);
        datePanel.add(calendarButton, BorderLayout.EAST);
        formPanel.add(datePanel, gbc);
        
        // Identifiant
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel identifiantLabel = new JLabel("Identifiant:");
        identifiantLabel.setFont(labelFont);
        identifiantLabel.setForeground(labelColor);
        formPanel.add(identifiantLabel, gbc);

        gbc.gridx = 1;
        identifiantField = new JTextField(25);
        identifiantField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(identifiantField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(labelColor);
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(25);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(passwordField, gbc);
        
        // Sexe
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel sexeLabel = new JLabel("Sexe*:");
        sexeLabel.setFont(labelFont);
        sexeLabel.setForeground(labelColor);
        formPanel.add(sexeLabel, gbc);
        
        gbc.gridx = 1;
        sexeCombo = new JComboBox<>(new String[]{"Homme", "Femme"});
        sexeCombo.setPreferredSize(new Dimension(260, 25)); // ComboBox élargi
        sexeCombo.setBackground(Color.WHITE);
        sexeCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(sexeCombo, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Marges augmentées
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20)); // Boutons légèrement agrandis
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 13));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));
        saveButton.addActionListener(e -> saveReceptionist());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showCalendar() {
        JDialog calendarDialog = new JDialog(this, "Sélectionnez une date", true);
        calendarDialog.setSize(350, 350); // Calendrier légèrement agrandi
        calendarDialog.setLayout(new BorderLayout());
        
        JCalendar calendar = new JCalendar();
        calendar.setWeekOfYearVisible(false);
        calendar.setDecorationBackgroundColor(Color.WHITE);
        calendar.setDecorationBordersVisible(true);
        
        JButton okButton = new JButton("OK");
        okButton.setBackground(new Color(76, 175, 80));
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(e -> {
            Date selectedDate = calendar.getDate();
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dateOfBirthField.setText(sdf.format(selectedDate));
            }
            calendarDialog.dispose();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        
        calendarDialog.add(calendar, BorderLayout.CENTER);
        calendarDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setVisible(true);
    }
    
    private void populateFields() {
        if (receptionist != null) {
            firstNameField.setText(receptionist.getFirstName());
            lastNameField.setText(receptionist.getLastName());
            phoneField.setText(receptionist.getPhone());
            identifiantField.setText(receptionist.getIdentifiant());
            passwordField.setText(receptionist.getPassword());
            sexeCombo.setSelectedItem(receptionist.getSexe());
            
            if (receptionist.getDateOfBirth() != null) {
                dateOfBirthField.setText(receptionist.getDateOfBirth()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }
    }
    
    private void saveReceptionist() {
        if (firstNameField.getText().trim().isEmpty() || 
            lastNameField.getText().trim().isEmpty() ||
            identifiantField.getText().trim().isEmpty() ||
            passwordField.getPassword().length == 0 ||
            dateOfBirthField.getText().trim().isEmpty() || 
            dateOfBirthField.getText().equals("__/__/____")) {
            
            JOptionPane.showMessageDialog(this, 
                "Les champs marqués d'un astérisque (*) sont obligatoires!", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate dob;
        try {
            String dateText = dateOfBirthField.getText();
            dob = LocalDate.parse(dateText, 
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .withResolverStyle(ResolverStyle.STRICT));
            receptionist.setDateOfBirth(dob);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Date invalide! Format attendu: jj/mm/aaaa", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (receptionist == null) {
            receptionist = new Receptionist();
        }
        receptionist.setFirstName(firstNameField.getText().trim());
        receptionist.setLastName(lastNameField.getText().trim());
        receptionist.setPhone(phoneField.getText().trim());
        receptionist.setIdentifiant(identifiantField.getText().trim());
        receptionist.setPassword(new String(passwordField.getPassword()));
        receptionist.setDateOfBirth(dob);
        receptionist.setSexe((String) sexeCombo.getSelectedItem());
        saved = true;
        dispose();
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public Receptionist getReceptionist() {
        return receptionist;
    }
}