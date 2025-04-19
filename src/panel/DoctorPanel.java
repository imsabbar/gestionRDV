package panel;

import dao.DoctorDAO;
import model.Doctor;
import dialog.DoctorDialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorPanel extends JPanel {
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private List<Doctor> doctors;
    private JTextField searchField;
    private JComboBox<String> specializationFilter;
    private JButton addDoctorBtn;

    public DoctorPanel() {
        doctorDAO = new DoctorDAO();
        setLayout(new BorderLayout());
        initComponents();
        loadDoctors();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Rechercher:"));
        searchField = new JTextField(25);
        searchField.setPreferredSize(new Dimension(250, 30));
        searchPanel.add(searchField);

     // Bouton Recherche avec icône
        JButton searchIconButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/search.png"));
            Image img = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            searchIconButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            searchIconButton.setText("🔍");
        }
        searchIconButton.setPreferredSize(new Dimension(30, 30));
        searchIconButton.setToolTipText("Rechercher");
        searchIconButton.setFocusPainted(false);
        searchIconButton.setBorderPainted(false);
        searchIconButton.setContentAreaFilled(false);
        searchIconButton.addActionListener(e -> filterDoctors());
        searchPanel.add(searchIconButton);

        // Bouton Réinitialiser avec icône
        JButton resetIconButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/reset.png")); // à adapter selon icône reset
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            resetIconButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            resetIconButton.setText("↺"); // emoji reset
        }
        resetIconButton.setPreferredSize(new Dimension(30, 30));
        resetIconButton.setToolTipText("Réinitialiser");
        resetIconButton.setFocusPainted(false);
        resetIconButton.setBorderPainted(false);
        resetIconButton.setContentAreaFilled(false);
        resetIconButton.addActionListener(e -> resetFilters());
        searchPanel.add(resetIconButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Add doctor button
        addDoctorBtn = new JButton("Nouveau Médecin");
        addDoctorBtn.setPreferredSize(new Dimension(150, 35));
        addDoctorBtn.setBackground(new Color(46,125,50));
        addDoctorBtn.setForeground(Color.WHITE);
        addDoctorBtn.setFocusPainted(false);
        addDoctorBtn.addActionListener(e -> addNewDoctor());
        topPanel.add(addDoctorBtn, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Specialization filter
        JPanel specializationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        specializationPanel.add(new JLabel("Spécialisation:"));
        specializationFilter = new JComboBox<>();
        specializationFilter.addItem("Toutes spécialisations");
        String[] specializations = {"Médecin généraliste", "Cardiologue", "Dermatologue", "Pédiatre", "Gynécologue", "Ophtalmologue", "ORL", "Psychiatre"};
        for (String specialization : specializations) {
            specializationFilter.addItem(specialization);
        }
        specializationFilter.setPreferredSize(new Dimension(150, 30));
        specializationFilter.addActionListener(e -> filterDoctors());
        specializationPanel.add(specializationFilter);
        filterPanel.add(specializationPanel);
        
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);

        // Table configuration
        String[] columnNames = {"ID", "Nom", "Prénom", "Spécialisation", "Téléphone", "Email", "Jours de travail", "Patients max/jour", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Colonne Actions
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setRowHeight(35);
        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Action column configuration
        TableColumn actionColumn = doctorTable.getColumnModel().getColumn(8); // Dernière colonne pour les actions
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());
        actionColumn.setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addNewDoctor() {
        DoctorDialog dialog = new DoctorDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadDoctors();
        }
    }

    private void loadDoctors() {
        doctors = doctorDAO.findAll();
        refreshTable();
    }

    

    private void filterDoctors() {
        String query = searchField.getText().trim();
        String selectedSpecialization = (String) specializationFilter.getSelectedItem();

        // Vérifier si la recherche est un ID
        try {
            int searchId = Integer.parseInt(query);
            if (searchId <= 0) {
                JOptionPane.showMessageDialog(this,
                    "L'ID du médecin doit être supérieur à 0",
                    "ID invalide",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Recherche par ID
            doctors = doctorDAO.findAll().stream()
                .filter(d -> d.getId() == searchId)
                .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            // Recherche par nom, prénom, email, téléphone
            final String searchQuery = query.toLowerCase();
            List<Doctor> allDoctors = doctorDAO.findAll();

            doctors = allDoctors.stream()
                .filter(d -> {
                    boolean matchesQuery = query.isEmpty() || 
                        d.getFirstName().toLowerCase().contains(query) ||
                        d.getLastName().toLowerCase().contains(query) ||
                        d.getEmail().toLowerCase().contains(query) ||
                        d.getPhone().toLowerCase().contains(query);
                    boolean matchesSpec = selectedSpecialization.equals("Toutes spécialisations") || 
                        d.getSpecialization().equalsIgnoreCase(selectedSpecialization);
                    return matchesQuery && matchesSpec;
                })
                .collect(Collectors.toList());
        }

        refreshTable();
    }


    private void refreshTable() {
        tableModel.setRowCount(0);
        
        if (doctors == null || doctors.isEmpty()) {
            return;
        }
        
        for (Doctor doctor : doctors) {
            // Formatage du numéro de téléphone
            String phoneNumber = doctor.getPhone();
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                // Supprime tous les caractères non numériques
                phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                // Format: 06 12 34 56 78
                if (phoneNumber.length() == 10) {
                    phoneNumber = phoneNumber.replaceFirst("(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1 $2 $3 $4 $5");
                }
            }

            Object[] row = {
                doctor.getId(),
                doctor.getLastName(),
                doctor.getFirstName(),
                doctor.getSpecialization(),
                phoneNumber,
                doctor.getEmail(),
                doctor.getWorkingDays(),
                doctor.getMaxPatientsPerDay(),
                "" // Colonne Actions
            };
            tableModel.addRow(row);
        }
    }

    class ButtonRenderer extends DefaultTableCellRenderer {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;

        public ButtonRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            editButton = new JButton("Modifier");
            styleButton(editButton, new Color(52, 152, 219));
            
            deleteButton = new JButton("Supprimer");
            styleButton(deleteButton, new Color(231, 76, 60));
            
            panel.add(editButton);
            panel.add(deleteButton);
        }

        private void styleButton(JButton button, Color color) {
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(80, 25));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private int clickedRow;

        public ButtonEditor() {
            super(new JTextField());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            editButton = new JButton("Modifier");
            styleButton(editButton, new Color(52, 152, 219));
            editButton.addActionListener(e -> {
                fireEditingStopped();
                editDoctor(clickedRow);
            });
            
            deleteButton = new JButton("Supprimer");
            styleButton(deleteButton, new Color(231, 76, 60));
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteDoctor(clickedRow);
            });
            
            panel.add(editButton);
            panel.add(deleteButton);
        }

        private void styleButton(JButton button, Color color) {
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(80, 25));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setFocusPainted(false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                   boolean isSelected, int row, int column) {
            clickedRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void editDoctor(int row) {
        Doctor doctor = doctors.get(row);
        DoctorDialog dialog = new DoctorDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            doctor
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadDoctors();
        }
    }

    private void deleteDoctor(int row) {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer ce médecin?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            Doctor doctor = doctors.get(row);
            if (doctorDAO.delete(doctor.getId())) {
                loadDoctors();
            }
        }
    }
    private void resetFilters() {
        searchField.setText("");
        specializationFilter.setSelectedIndex(0);
        loadDoctors();  // recharge la liste complète
    }
    
}