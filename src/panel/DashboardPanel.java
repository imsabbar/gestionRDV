package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import component.AppointmentCard;
import component.StatsCard;
import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import model.Appointment;
import util.ChartUtil;

public class DashboardPanel extends JPanel {

    private JPanel statsPanel;
    private JPanel todayAppointmentsPanel;
    private JPanel upcomingAppointmentsPanel;
    private JPanel chartsPanel;

    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;

    public DashboardPanel() {
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        appointmentDAO = new AppointmentDAO();

        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Welcome banner
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(41, 128, 185));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Bienvenue au Système de Gestion de Rendez-vous");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel("Aujourd'hui: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        welcomePanel.add(dateLabel, BorderLayout.EAST);

        add(welcomePanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 20));

        // Stats panel
        statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainContentPanel.add(statsPanel, BorderLayout.NORTH);

        // Charts panel
        chartsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        chartsPanel.setPreferredSize(new Dimension(0, 250));
        mainContentPanel.add(chartsPanel, BorderLayout.CENTER);

        // Appointments container
        JPanel appointmentsContainer = new JPanel(new GridLayout(1, 2, 20, 0));

        todayAppointmentsPanel = createAppointmentsPanel("Rendez-vous d'aujourd'hui");
        appointmentsContainer.add(todayAppointmentsPanel);

        upcomingAppointmentsPanel = createAppointmentsPanel("Prochains rendez-vous");
        appointmentsContainer.add(upcomingAppointmentsPanel);

        mainContentPanel.add(appointmentsContainer, BorderLayout.SOUTH);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createAppointmentsPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // List panel with vertical BoxLayout inside a scroll pane
        JPanel appointmentsListPanel = new JPanel();
        appointmentsListPanel.setLayout(new BoxLayout(appointmentsListPanel, BoxLayout.Y_AXIS));
        appointmentsListPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Set a preferred size so scroll pane knows how much space it should take
        appointmentsListPanel.setPreferredSize(new Dimension(300, 400));

        JScrollPane scrollPane = new JScrollPane(appointmentsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void refreshData() {
        updateStats();
        updateCharts();
        updateTodayAppointments();
        updateUpcomingAppointments();

        revalidate();
        repaint();
    }

    private void updateStats() {
        statsPanel.removeAll();

        int patientCount = patientDAO.findAll().size();
        statsPanel.add(new StatsCard("Patients", String.valueOf(patientCount), new Color(46, 204, 113)));

        int doctorCount = doctorDAO.findAll().size();
        statsPanel.add(new StatsCard("Médecins", String.valueOf(doctorCount), new Color(52, 152, 219)));

        List<Appointment> todayAppointments = appointmentDAO.findByDate(LocalDate.now());
        statsPanel.add(new StatsCard("RDV aujourd'hui", String.valueOf(todayAppointments.size()), new Color(155, 89, 182)));

        List<Appointment> upcomingAppointments = appointmentDAO.findUpcoming();
        statsPanel.add(new StatsCard("RDV à venir", String.valueOf(upcomingAppointments.size()), new Color(231, 76, 60)));
    }

    private void updateCharts() {
        chartsPanel.removeAll();

        chartsPanel.add(ChartUtil.createAppointmentsPerDoctorChart());
        chartsPanel.add(ChartUtil.createAppointmentsBySpecialtyChart());
        chartsPanel.add(ChartUtil.createAppointmentsByStatusPieChart());

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private void updateTodayAppointments() {
        JScrollPane scrollPane = (JScrollPane) todayAppointmentsPanel.getComponent(1);
        JViewport viewport = scrollPane.getViewport();
        JPanel appointmentsListPanel = (JPanel) viewport.getView();
        appointmentsListPanel.removeAll();

        List<Appointment> todayAppointments = appointmentDAO.findByDate(LocalDate.now());

        if (todayAppointments.isEmpty()) {
            JLabel noAppointmentsLabel = new JLabel("Aucun rendez-vous aujourd'hui");
            noAppointmentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            appointmentsListPanel.add(noAppointmentsLabel);
        } else {
            for (Appointment appointment : todayAppointments) {
                AppointmentCard card = new AppointmentCard(appointment);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
                appointmentsListPanel.add(card);
                appointmentsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        appointmentsListPanel.revalidate();
        appointmentsListPanel.repaint();
    }

    private void updateUpcomingAppointments() {
        JScrollPane scrollPane = (JScrollPane) upcomingAppointmentsPanel.getComponent(1);
        JViewport viewport = scrollPane.getViewport();
        JPanel appointmentsListPanel = (JPanel) viewport.getView();
        appointmentsListPanel.removeAll();

        List<Appointment> upcomingAppointments = appointmentDAO.findUpcoming();

        if (upcomingAppointments.isEmpty()) {
            JLabel noAppointmentsLabel = new JLabel("Aucun rendez-vous à venir");
            noAppointmentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            appointmentsListPanel.add(noAppointmentsLabel);
        } else {
            int count = Math.min(upcomingAppointments.size(), 5);
            for (int i = 0; i < count; i++) {
                AppointmentCard card = new AppointmentCard(upcomingAppointments.get(i));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
                appointmentsListPanel.add(card);
                appointmentsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        appointmentsListPanel.revalidate();
        appointmentsListPanel.repaint();
    }
}
