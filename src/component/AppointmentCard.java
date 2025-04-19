package component;

import model.Appointment;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class AppointmentCard extends JPanel {
    
    private Appointment appointment;
    
    public AppointmentCard(Appointment appointment) {
        this.appointment = appointment;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);
        
        // Time label in the left panel
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(Color.WHITE);
        timePanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        timePanel.setPreferredSize(new Dimension(80, 80));
        
        JLabel timeLabel = new JLabel(appointment.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setForeground(new Color(41, 128, 185));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel durationLabel = new JLabel(appointment.getDuration() + " min");
        durationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        durationLabel.setForeground(Color.GRAY);
        durationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        timePanel.add(timeLabel, BorderLayout.CENTER);
        timePanel.add(durationLabel, BorderLayout.SOUTH);
        
        add(timePanel, BorderLayout.WEST);
        
        // Center panel with appointment details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel patientLabel = new JLabel("Patient: " + appointment.getPatient().getFullName());
        patientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel doctorLabel = new JLabel("Dr. " + appointment.getDoctor().getFullName());
        doctorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        doctorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel reasonLabel = new JLabel(appointment.getReason());
        reasonLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        reasonLabel.setForeground(Color.GRAY);
        reasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailsPanel.add(patientLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(doctorLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(reasonLabel);
        
        add(detailsPanel, BorderLayout.CENTER);
        
        // Status label in the right panel
        JLabel statusLabel = new JLabel(getStatusLabel(appointment.getStatus()));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(getStatusColor(appointment.getStatus()));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new LineBorder(getStatusColor(appointment.getStatus()), 1, true));
        statusLabel.setPreferredSize(new Dimension(80, 25));
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel);
        
        add(statusPanel, BorderLayout.EAST);
    }
    
    private String getStatusLabel(String status) {
        switch (status) {
            case "SCHEDULED": return "Planifié";
            case "COMPLETED": return "Terminé";
            case "CANCELLED": return "Annulé";
            case "NO_SHOW": return "Absent";
            default: return status;
        }
    }
    
    private Color getStatusColor(String status) {
        switch (status) {
            case "SCHEDULED": return new Color(52, 152, 219); // Blue
            case "COMPLETED": return new Color(46, 204, 113); // Green
            case "CANCELLED": return new Color(231, 76, 60); // Red
            case "NO_SHOW": return new Color(211, 84, 0); // Orange
            default: return Color.GRAY;
        }
    }
}
