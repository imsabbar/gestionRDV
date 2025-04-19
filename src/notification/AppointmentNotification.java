package notification;

import model.Appointment;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class AppointmentNotification extends JDialog {
    
    private static final int DISPLAY_TIME = 5000; // 5 seconds
    
    public AppointmentNotification(Window owner, Appointment appointment) {
        super(owner);
        setUndecorated(true);
        setAlwaysOnTop(true);
        
        initComponents(appointment);
        positionWindow(owner);
        
        // Auto-close after display time
        Timer timer = new Timer(DISPLAY_TIME, e -> dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void initComponents(Appointment appointment) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("Rendez-vous à venir");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        JLabel dateLabel = new JLabel("Date: " + appointment.getDateTime().format(dateFormatter));
        JLabel patientLabel = new JLabel("Patient: " + appointment.getPatient().getFullName());
        JLabel doctorLabel = new JLabel("Médecin: Dr. " + appointment.getDoctor().getFullName());
        JLabel reasonLabel = new JLabel("Motif: " + appointment.getReason());
        
        contentPanel.add(dateLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(patientLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(doctorLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(reasonLabel);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("×");
        closeButton.setForeground(Color.GRAY);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(mainPanel);
        pack();
    }
    
    private void positionWindow(Window owner) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth() - 20;
        int y = screenSize.height - getHeight() - 50;
        setLocation(x, y);
    }
}