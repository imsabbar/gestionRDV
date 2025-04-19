package util;

import dao.AppointmentDAO;
import model.Appointment;
import model.Patient;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFGenerator {
    
    public static boolean generatePatientReport(Patient patient) {
        try {
            // Create a simple text-based report first
            // In a real application, use a PDF library like iText or Apache PDFBox
            
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdir();
            }
            
            String fileName = "reports/patient_" + patient.getId() + "_report.txt";
            FileOutputStream fos = new FileOutputStream(fileName);
            
            StringBuilder sb = new StringBuilder();
            sb.append("=========================================\n");
            sb.append("            RAPPORT PATIENT              \n");
            sb.append("=========================================\n\n");
            
            sb.append("Date du rapport: ").append(java.time.LocalDate.now().toString()).append("\n\n");
            
            sb.append("INFORMATIONS PATIENT:\n");
            sb.append("-----------------------------------------\n");
            sb.append("ID: ").append(patient.getId()).append("\n");
            sb.append("Nom: ").append(patient.getLastName()).append("\n");
            sb.append("Prénom: ").append(patient.getFirstName()).append("\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            sb.append("Date de naissance: ").append(patient.getDateOfBirth().format(formatter)).append("\n");
            sb.append("Téléphone: ").append(patient.getPhone()).append("\n");
            //sb.append("Email: ").append(patient.getEmail()).append("\n");
            sb.append("Adresse: ").append(patient.getAddress()).append("\n\n");
            
            sb.append("ANTÉCÉDENTS MÉDICAUX:\n");
            sb.append("-----------------------------------------\n");
            sb.append(patient.getMedicalHistory()).append("\n\n");
            
            // Get appointments for this patient
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            List<Appointment> appointments = appointmentDAO.findByPatient(patient.getId());
            
            sb.append("HISTORIQUE DES RENDEZ-VOUS:\n");
            sb.append("-----------------------------------------\n");
            
            if (appointments.isEmpty()) {
                sb.append("Aucun rendez-vous enregistré pour ce patient.\n");
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                
                for (Appointment appointment : appointments) {
                    sb.append("Date: ").append(appointment.getDateTime().format(dateTimeFormatter)).append("\n");
                    sb.append("Médecin: Dr. ").append(appointment.getDoctor().getFullName());
                    sb.append(" (").append(appointment.getDoctor().getSpecialization()).append(")\n");
                    sb.append("Motif: ").append(appointment.getReason()).append("\n");
                    sb.append("Notes: ").append(appointment.getNotes()).append("\n");
                    sb.append("Statut: ").append(appointment.getStatus()).append("\n");
                    sb.append("-----------------------------------------\n");
                }
            }
            
            sb.append("\n=========================================\n");
            sb.append("Fin du rapport - Généré automatiquement  \n");
            sb.append("=========================================\n");
            
            fos.write(sb.toString().getBytes());
            fos.close();
            
            // Open the file
            try {
                File file = new File(fileName);
                if (file.exists()) {
                    java.awt.Desktop.getDesktop().open(file);
                }
            } catch (Exception e) {
                // Ignore if can't open
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}