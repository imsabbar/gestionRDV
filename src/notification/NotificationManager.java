package notification;

import dao.AppointmentDAO;
import model.Appointment;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationManager {
    
    private static final int CHECK_INTERVAL = 1; // minutes
    private Window owner;
    private AppointmentDAO appointmentDAO;
    private ScheduledExecutorService scheduler;
    
    public NotificationManager(Window owner) {
        this.owner = owner;
        this.appointmentDAO = new AppointmentDAO();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void start() {
        scheduler.scheduleAtFixedRate(this::checkUpcomingAppointments, 0, CHECK_INTERVAL, TimeUnit.MINUTES);
    }
    
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
    
    private void checkUpcomingAppointments() {
        List<Appointment> appointments = appointmentDAO.findUpcoming();
        LocalDateTime now = LocalDateTime.now();
        
        for (Appointment appointment : appointments) {
            long minutesUntil = ChronoUnit.MINUTES.between(now, appointment.getDateTime());
            
            // Show notification for appointments in the next 30 minutes
            if (minutesUntil <= 30 && minutesUntil > 0) {
                showNotification(appointment);
            }
        }
    }
    
    private void showNotification(Appointment appointment) {
        SwingUtilities.invokeLater(() -> {
            AppointmentNotification notification = new AppointmentNotification(owner, appointment);
            notification.setVisible(true);
        });
    }
}