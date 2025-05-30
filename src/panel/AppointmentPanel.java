package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.time.LocalDate; 
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import dao.AppointmentDAO;
import dialog.AppointmentDialog;
import model.Appointment;

public class AppointmentPanel extends JPanel {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private AppointmentDAO appointmentDAO;
    private List<Appointment> appointments;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JButton newAppointmentBtn;
    private JButton searchButton;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JButton resetSearchBtn;

    public AppointmentPanel() {
        appointmentDAO = new AppointmentDAO();
        setLayout(new BorderLayout());
        initComponents();
        loadAppointments();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
     // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Rechercher:"));

        // Container champ recherche + icône
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setPreferredSize(new Dimension(280, 30));

        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 8, 5, 28) // espace à droite pour icône
        ));
        searchField.addActionListener(e -> performSearch());
        searchContainer.add(searchField, BorderLayout.CENTER);

        // Panel des boutons icônes à droite du champ
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);

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
        searchIconButton.addActionListener(e -> performSearch());
        buttonsPanel.add(searchIconButton);

        // Bouton Réinitialiser avec icône
        JButton resetIconButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/reset.png")); // adapte le chemin si besoin
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
        resetIconButton.addActionListener(e -> resetFilters());
        buttonsPanel.add(resetIconButton);

        searchContainer.add(buttonsPanel, BorderLayout.EAST);
        searchPanel.add(searchContainer);

        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // New appointment button
        newAppointmentBtn = new JButton("Nouveau Rendez-vous");
        newAppointmentBtn.setPreferredSize(new Dimension(182, 35));
        newAppointmentBtn.setBackground(new Color(46,125,50));
        newAppointmentBtn.setForeground(Color.WHITE);
        newAppointmentBtn.setFocusPainted(false);
        newAppointmentBtn.addActionListener(e -> createNewAppointment());
        topPanel.add(newAppointmentBtn, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Status filter
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.add(new JLabel("Statut:"));
        statusFilter = new JComboBox<>(new String[]{"Tous les statuts", "Planifié", "Terminé", "Annulé", "Absent", "Confirmé"});
        statusFilter.setPreferredSize(new Dimension(150, 30));
        statusFilter.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                filterAppointments();
            }
        });
        statusPanel.add(statusFilter);
        filterPanel.add(statusPanel);
        
        // Date filter with JSpinner
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(new JLabel("Du:"));
        
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setPreferredSize(new Dimension(120, 30));
        datePanel.add(startDateSpinner);

        datePanel.add(new JLabel("Au:"));
        
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setPreferredSize(new Dimension(120, 30));
        datePanel.add(endDateSpinner);
        
        //bouton icône filtrer
        JButton filterIconButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/filter.png")); // chemin vers ton icône filtre
            Image img = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            filterIconButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            filterIconButton.setText("Filtrer");
        }
        filterIconButton.setPreferredSize(new Dimension(32, 32));
        filterIconButton.setToolTipText("Filtrer par date");
        filterIconButton.setFocusPainted(false);
        filterIconButton.setBorderPainted(false);
        filterIconButton.setContentAreaFilled(false);
        filterIconButton.addActionListener(e -> filterByDateRange());
        datePanel.add(filterIconButton);

        filterPanel.add(datePanel);
        
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);

        // Table configuration
        String[] columnNames = {"ID", "Patient", "Médecin", "Date", "Heure", "Durée", "Motif", "Statut", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(35);
        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Action column configuration
        TableColumn actionColumn = appointmentTable.getColumnModel().getColumn(8);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor());
        actionColumn.setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void filterByDateRange() {
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        
        if (startDate == null && endDate == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner au moins une date",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this, 
                "La date de début doit être antérieure à la date de fin",
                "Erreur de dates",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate start = startDate != null ? 
            startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate end = endDate != null ? 
            endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        
        if (start == null && end == null) {
            loadAppointments();
            return;
        }
        
        List<Appointment> filteredAppointments = new ArrayList<>();
        List<Appointment> allAppointments = appointmentDAO.findAll();
        
        for (Appointment app : allAppointments) {
            LocalDate appDate = app.getDateTime().toLocalDate();
            
            if (start != null && end != null) {
                if (!appDate.isBefore(start) && !appDate.isAfter(end)) {
                    filteredAppointments.add(app);
                }
            } else if (start != null) {
                if (!appDate.isBefore(start)) {
                    filteredAppointments.add(app);
                }
            } else if (end != null) {
                if (!appDate.isAfter(end)) {
                    filteredAppointments.add(app);
                }
            }
        }
        
        appointments = filteredAppointments;
        refreshTable();
    }

    private void clearDateFilter() {
        startDateSpinner.setValue(new Date());
        endDateSpinner.setValue(new Date());
        loadAppointments();
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (query.length() < 2 && !query.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez entrer au moins 2 caractères pour la recherche",
                "Recherche invalide",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (query.isEmpty() && selectedStatus.equals("Tous les statuts")) {
            loadAppointments();
        } else if (!query.isEmpty() && selectedStatus.equals("Tous les statuts")) {
            appointments = appointmentDAO.search(query);
            refreshTable();
        } else if (query.isEmpty() && !selectedStatus.equals("Tous les statuts")) {
            filterAppointments();
        } else {
            String statusCode = getStatusCode(selectedStatus);
            appointments = appointmentDAO.searchByStatus(query, statusCode);
            refreshTable();
        }
    }

    private void filterAppointments() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        if (selectedStatus.equals("Tous les statuts")) {
            loadAppointments();
        } else {
            String statusCode = getStatusCode(selectedStatus);
            appointments = appointmentDAO.findByStatus(statusCode);
            refreshTable();
        }
    }

    private String getStatusCode(String statusLabel) {
        switch (statusLabel) {
            case "Planifié": return "SCHEDULED";
            case "Terminé": return "COMPLETED";
            case "Annulé": return "CANCELLED";
            case "Absent": return "NO_SHOW";
            case "Confirmé": return "CONFIRMED";
            default: return "";
        }
    }

    private void createNewAppointment() {
        AppointmentDialog dialog = new AppointmentDialog(
            SwingUtilities.getWindowAncestor(this), 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadAppointments();
        }
    }

    private void loadAppointments() {
        appointments = appointmentDAO.findAll();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        
        if (appointments == null || appointments.isEmpty()) {
            return;
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Appointment appointment : appointments) {
            Object[] row = {
                appointment.getId(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getFullName(),
                appointment.getDateTime().toLocalDate().format(dateFormatter),
                appointment.getDateTime().toLocalTime().format(timeFormatter),
                appointment.getDuration() + " min",
                appointment.getReason(),
                translateStatus(appointment.getStatus()),
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
                editAppointment(clickedRow);
            });
            
            deleteButton = new JButton("Supprimer");
            styleButton(deleteButton, new Color(231, 76, 60));
            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteAppointment(clickedRow);
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

    private void editAppointment(int row) {
        Appointment appointment = appointments.get(row);
        
        // Ouvrir le dialogue de modification du rendez-vous
        AppointmentDialog appointmentDialog = new AppointmentDialog(
            (Window) SwingUtilities.getWindowAncestor(this), 
            appointment
        );
        appointmentDialog.setVisible(true);
        
        // Recharger les données si sauvegardé
        if (appointmentDialog.isSaved()) {
            JOptionPane.showMessageDialog(
                this,
                "Les modifications ont été enregistrées avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadAppointments();
        }
    }

    private void deleteAppointment(int row) {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer ce rendez-vous?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            Appointment appointment = appointments.get(row);
            if (appointmentDAO.delete(appointment.getId())) {
                loadAppointments();
            }
        }
    }

    private String translateStatus(String status) {
        if (status == null) return "Inconnu";
        switch(status) {
            case "SCHEDULED": return "Planifié";
            case "COMPLETED": return "Terminé";
            case "CANCELLED": return "Annulé";
            case "NO_SHOW": return "Absent";
            case "CONFIRMED": return "Confirmé";
            default: return status;
        }
    }
    private void resetFilters() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        startDateSpinner.setValue(new Date());
        endDateSpinner.setValue(new Date());
        loadAppointments();
    }

}