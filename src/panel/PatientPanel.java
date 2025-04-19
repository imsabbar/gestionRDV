package panel;

import dao.PatientDAO;
import model.Patient;
import dialog.PatientDialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PatientPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private PatientDAO patientDAO;
    private List<Patient> patients;
    private JTextField searchField;
    private JButton addPatientBtn;

    public PatientPanel() {
        patientDAO = new PatientDAO();
        setLayout(new BorderLayout());
        initComponents();
        loadPatients();
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
        searchField.addActionListener(e -> searchPatients());
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
        searchIconButton.addActionListener(e -> searchPatients());
        searchPanel.add(searchIconButton);

        // Bouton Réinitialiser avec icône
        JButton resetIconButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/reset.png")); // adapte le chemin
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            resetIconButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            resetIconButton.setText("↺");
        }
        resetIconButton.setPreferredSize(new Dimension(30, 30));
        resetIconButton.setToolTipText("Réinitialiser");
        resetIconButton.setFocusPainted(false);
        resetIconButton.setBorderPainted(false);
        resetIconButton.setContentAreaFilled(false);
        resetIconButton.addActionListener(e -> {
            searchField.setText("");
            loadPatients();
        });
        searchPanel.add(resetIconButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Add patient button
        addPatientBtn = new JButton("Nouveau Patient");
        addPatientBtn.setPreferredSize(new Dimension(150, 35));
        addPatientBtn.setBackground(new Color(46,125,50));
        addPatientBtn.setForeground(Color.WHITE);
        addPatientBtn.setFocusPainted(false);
        addPatientBtn.addActionListener(e -> addNewPatient());
        topPanel.add(addPatientBtn, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        add(mainPanel, BorderLayout.NORTH);

        // Table configuration
        String[] columnNames = {"ID", "Nom", "Prénom", "Date de Naissance", "Téléphone", "CIN", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(35);
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Action column configuration
        TableColumn actionColumn = patientTable.getColumnModel().getColumn(6);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());
        actionColumn.setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addNewPatient() {
        PatientDialog dialog = new PatientDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadPatients();
        }
    }

    private void loadPatients() {
        patients = patientDAO.findAll();
        refreshTable();
    }

    private void searchPatients() {
        String query = searchField.getText().trim();
        
        if (query.isEmpty()) {
            loadPatients();
            return;
        }

        // Vérifier si la recherche est un ID
        try {
            int searchId = Integer.parseInt(query);
            if (searchId <= 0) {
                JOptionPane.showMessageDialog(this,
                    "L'ID du patient doit être supérieur à 0",
                    "ID invalide",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Recherche par ID
            patients = patientDAO.findAll().stream()
                .filter(p -> p.getId() == searchId)
                .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            // Si ce n'est pas un ID valide, recherche normale
            patients = patientDAO.searchPatients(query);
        }
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        
        if (patients == null || patients.isEmpty()) {
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Patient patient : patients) {
            Object[] row = {
                patient.getId(),
                patient.getLastName(),
                patient.getFirstName(),
                patient.getDateOfBirth().format(formatter),
                patient.getPhone(),
                patient.getCin(),
                ""
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
                editPatient(clickedRow);
            });
            
            deleteButton = new JButton("Supprimer");
            styleButton(deleteButton, new Color(231, 76, 60));
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deletePatient(clickedRow);
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

    private void editPatient(int row) {
        Patient patient = patients.get(row);
        PatientDialog dialog = new PatientDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            patient
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadPatients();
        }
    }

    private void deletePatient(int row) {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer ce patient?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            Patient patient = patients.get(row);
            if (patientDAO.delete(patient.getId())) {
                loadPatients();
            }
        }
    }
}