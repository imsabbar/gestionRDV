package panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import dao.AppointmentDAO;
import model.ReportLine;

public class ReportPanel extends JPanel {
    private JDateChooser fromDatePicker;
    private JDateChooser toDatePicker;
    private JButton loadButton;
    private JButton generateBilanButton;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private AppointmentDAO appointmentDAO;

    public ReportPanel() {
        appointmentDAO = new AppointmentDAO();
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        // North: date range selection + load button
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        northPanel.add(new JLabel("From:"));
        fromDatePicker = new JDateChooser();
        fromDatePicker.setDate(new Date());
        northPanel.add(fromDatePicker);
        northPanel.add(new JLabel("To:"));
        toDatePicker = new JDateChooser();
        toDatePicker.setDate(new Date());
        northPanel.add(toDatePicker);

        loadButton = new JButton("Load Bilan");
        northPanel.add(loadButton);

        add(northPanel, BorderLayout.NORTH);

        // Center: report table
        String[] columns = {"Index","Âge","Patient","Couverture","Date & Heure","Médecin","Spécialité","Bâtiment","Observations"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(24);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // South: Generate Bilan button
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generateBilanButton = new JButton("Generate Bilan");
        southPanel.add(generateBilanButton);
        add(southPanel, BorderLayout.SOUTH);

        // Listeners
        loadButton.addActionListener(e -> loadReport());
        generateBilanButton.addActionListener(e -> openSelectedBilan());

        // Double-click row to open bilan dialog
        reportTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedBilan();
                }
            }
        });
    }

    private void loadReport() {
        Date fromDate = fromDatePicker.getDate();
        Date toDate   = toDatePicker.getDate();
        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this,
                "Please select both From and To dates.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalDate from = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to   = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            List<ReportLine> report = appointmentDAO.findReport(from, to);
            tableModel.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (ReportLine line : report) {
                tableModel.addRow(new Object[]{
                    line.getPatientIndex(),
                    line.getAge(),
                    line.getPatientNom(),
                    line.getCouvertureSociale(),
                    line.getDateRdv().format(dtf),
                    line.getMedecin(),
                    line.getSpecialite(),
                    line.getBatiment(),
                    line.getObservations()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to load Bilan: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSelectedBilan() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bilan row to generate bilan.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extract data from selected row (you might want to fetch full Appointment data via DAO for better detail)
        int patientIndex = (int) tableModel.getValueAt(selectedRow, 0);
        String patientNom = (String) tableModel.getValueAt(selectedRow, 2);
        int age = (int) tableModel.getValueAt(selectedRow, 1);
        String couverture = (String) tableModel.getValueAt(selectedRow, 3);
        String dateHeure = (String) tableModel.getValueAt(selectedRow, 4);
        String medecin = (String) tableModel.getValueAt(selectedRow, 5);
        String specialite = (String) tableModel.getValueAt(selectedRow, 6);
        String batiment = (String) tableModel.getValueAt(selectedRow, 7);
        String observations = (String) tableModel.getValueAt(selectedRow, 8);

        // Show the Bilan dialog
        BilanPanel bilanPanel = new BilanPanel(patientIndex, age, patientNom, couverture, dateHeure, medecin, specialite, batiment, observations);
        bilanPanel.setVisible(true);
    }
}
