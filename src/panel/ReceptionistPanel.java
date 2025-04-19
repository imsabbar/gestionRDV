package panel;

import dao.ReceptionistDAO;
import model.Receptionist;
import dialog.ReceptionistDialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

public class ReceptionistPanel extends JPanel {
    private JTable receptionistTable;
    private DefaultTableModel tableModel;
    private ReceptionistDAO receptionistDAO;
    private List<Receptionist> receptionists;
    private JTextField searchField;
    private JComboBox<String> sexeFilter;
    private JButton addReceptionistBtn;

    public ReceptionistPanel() {
        receptionistDAO = new ReceptionistDAO();
        setLayout(new BorderLayout());
        initComponents();
        loadReceptionists();
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
        searchField.addActionListener(e -> filterReceptionists());
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Rechercher");
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.addActionListener(e -> filterReceptionists());
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Add receptionist button
        addReceptionistBtn = new JButton("Nouveau Réceptionniste");
        addReceptionistBtn.setPreferredSize(new Dimension(180, 35));
        addReceptionistBtn.setBackground(new Color(46,125,50));
        addReceptionistBtn.setForeground(Color.WHITE);
        addReceptionistBtn.setFocusPainted(false);
        addReceptionistBtn.addActionListener(e -> addNewReceptionist());
        topPanel.add(addReceptionistBtn, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Sexe filter
        JPanel sexePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sexePanel.add(new JLabel("Sexe:"));
        sexeFilter = new JComboBox<>();
        sexeFilter.addItem("Tous");
        sexeFilter.addItem("M");
        sexeFilter.addItem("F");
        sexeFilter.setPreferredSize(new Dimension(100, 30));
        sexeFilter.addActionListener(e -> filterReceptionists());
        sexePanel.add(sexeFilter);
        filterPanel.add(sexePanel);
        
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);

        // Table configuration
        String[] columnNames = {"ID", "Nom", "Prénom", "Téléphone", "Date Naiss.", "Identifiant", "Sexe", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        receptionistTable = new JTable(tableModel);
        receptionistTable.setRowHeight(35);
        receptionistTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Action column configuration
        TableColumn actionColumn = receptionistTable.getColumnModel().getColumn(7);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());
        actionColumn.setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(receptionistTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addNewReceptionist() {
        ReceptionistDialog dialog = new ReceptionistDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            Receptionist receptionist = dialog.getReceptionist();
            if (receptionistDAO.create(receptionist)) {
                loadReceptionists();
                JOptionPane.showMessageDialog(this,
                    "Réceptionniste ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'ajout du réceptionniste", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadReceptionists() {
        receptionists = receptionistDAO.findAll();
        refreshTable();
    }

    private void filterReceptionists() {
        String query = searchField.getText().trim();
        String selectedSexe = (String) sexeFilter.getSelectedItem();
        
        if (query.isEmpty() && "Tous".equals(selectedSexe)) {
            loadReceptionists();
        } else if (!query.isEmpty() && "Tous".equals(selectedSexe)) {
            receptionists = receptionistDAO.searchReceptionists(query);
            refreshTable();
        } else if (query.isEmpty() && !"Tous".equals(selectedSexe)) {
            receptionists = receptionistDAO.findBySexe(selectedSexe);
            refreshTable();
        } else {
            receptionists = receptionistDAO.searchReceptionists(query);
            receptionists.removeIf(r -> !r.getSexe().equals(selectedSexe));
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        
        if (receptionists == null || receptionists.isEmpty()) {
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        	    .withResolverStyle(ResolverStyle.STRICT);        
        for (Receptionist receptionist : receptionists) {
            Object[] row = {
                receptionist.getId(),
                receptionist.getLastName(),
                receptionist.getFirstName(),
                receptionist.getPhone(),
                receptionist.getDateOfBirth().format(formatter),
                receptionist.getIdentifiant(),
                receptionist.getSexe(),
                ""
            };
            tableModel.addRow(row);
        }
    }

    private void editReceptionist(int row) {
        Receptionist receptionist = receptionists.get(row);
        ReceptionistDialog dialog = new ReceptionistDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            receptionist
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            Receptionist updatedReceptionist = dialog.getReceptionist();
            if (receptionistDAO.update(updatedReceptionist)) {
                loadReceptionists();
                JOptionPane.showMessageDialog(this,
                    "Réceptionniste mis à jour avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la mise à jour du réceptionniste", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteReceptionist(int row) {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer ce réceptionniste?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            Receptionist receptionist = receptionists.get(row);
            if (receptionistDAO.delete(receptionist.getId())) {
                loadReceptionists();
                JOptionPane.showMessageDialog(this,
                    "Réceptionniste supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression du réceptionniste", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
                editReceptionist(clickedRow);
            });
            
            deleteButton = new JButton("Supprimer");
            styleButton(deleteButton, new Color(231, 76, 60));
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteReceptionist(clickedRow);
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
}