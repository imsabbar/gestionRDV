package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import model.User;
import notification.NotificationManager;
import panel.AppointmentPanel;
import panel.DashboardPanel;
import panel.DoctorPanel;
import panel.PatientPanel;
import panel.ReportPanel;
import rdv.StylishLoginFrame;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private PatientPanel patientPanel;
    private DoctorPanel doctorPanel;
    private AppointmentPanel appointmentPanel;
    private ReportPanel reportPanel;
    private NotificationManager notificationManager;
    private User currentUser;
    
    public MainFrame(User user) {
        this.currentUser = user;
        this.notificationManager = new NotificationManager(this);
        
        initComponents();
        setupLayout();
        setupListeners();
        
        setTitle("Système de Gestion de Rendez-vous - " + currentUser.getUsername());
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Start notification manager
        notificationManager.start();
    }
    
    private void initComponents() {
        // Initialize panels
        dashboardPanel = new DashboardPanel();
        patientPanel = new PatientPanel();
        doctorPanel = new DoctorPanel();
        appointmentPanel = new AppointmentPanel();
        reportPanel = new ReportPanel();
        
        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tableau de Bord", dashboardPanel);
        tabbedPane.addTab("Patients", patientPanel);
        tabbedPane.addTab("Médecins", doctorPanel);
        tabbedPane.addTab("Rendez-vous", appointmentPanel);
        tabbedPane.addTab("Bilan", reportPanel);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        add(createHeader(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Logo and title
        JLabel titleLabel = new JLabel("Système de Gestion de Rendez-vous");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel(currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        userPanel.add(userLabel);
        userPanel.add(new JSeparator(JSeparator.VERTICAL));
        userPanel.add(logoutButton);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFooter() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 20));
        
        JLabel copyrightLabel = new JLabel("© 2025 Système de Gestion RDV - Tous droits réservés");
        copyrightLabel.setForeground(Color.WHITE);
        footerPanel.add(copyrightLabel);
        
        return footerPanel;
    }
    
    private void setupListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                notificationManager.stop();
                int confirm = JOptionPane.showOptionDialog(
                    MainFrame.this,
                    "Êtes-vous sûr de vouloir quitter l'application?",
                    "Confirmation de sortie",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        
        tabbedPane.addChangeListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            if (selectedTab == 0) {
                dashboardPanel.refreshData();
            }
        });
    }
    
    private void logout() {
        notificationManager.stop();
        dispose();
        EventQueue.invokeLater(() -> {
        	StylishLoginFrame stylishLoginFrame = new StylishLoginFrame();
        	stylishLoginFrame.setVisible(true);
        });
    }
}