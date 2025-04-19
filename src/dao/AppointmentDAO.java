
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Appointment;
import model.Doctor;
import model.Patient;
import model.ReportLine;
import util.DatabaseConnection;



public class AppointmentDAO {
private final PatientDAO patientDAO = new PatientDAO();
private final DoctorDAO doctorDAO = new DoctorDAO();

public boolean create(Appointment appointment) {
    String sql = "INSERT INTO appointments (patient_id, doctor_id, date_time, duration, reason, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        setAppointmentParameters(pstmt, appointment);
        
        int affectedRows = pstmt.executeUpdate();
        
        if (affectedRows > 0) {
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    appointment.setId(rs.getInt(1));
                    return true;
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur création RDV: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

public List<Appointment> search(String query) {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
            "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
            "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
            "FROM appointments a " +
            "LEFT JOIN patients p ON a.patient_id = p.id " +
            "LEFT JOIN doctors d ON a.doctor_id = d.id " +
            "WHERE a.id = ? OR " +
            "LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE ? OR " +
            "LOWER(CONCAT(d.first_name, ' ', d.last_name)) LIKE ? " +
            "ORDER BY a.date_time DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        try {
            int id = Integer.parseInt(query);
            pstmt.setInt(1, id);
        } catch (NumberFormatException e) {
            pstmt.setInt(1, -1);
        }
        
        String searchPattern = "%" + query.toLowerCase() + "%";
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, searchPattern);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointmentFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur recherche RDV: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public List<Appointment> searchByStatus(String query, String status) {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
                "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
                "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE (a.id = ? OR " +
                "LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE LOWER(?) OR " +
                "LOWER(CONCAT(d.first_name, ' ', d.last_name)) LIKE LOWER(?)) " +
                "AND a.status = ? " +
                "ORDER BY a.date_time DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        try {
            int id = Integer.parseInt(query);
            pstmt.setInt(1, id);
        } catch (NumberFormatException e) {
            pstmt.setInt(1, -1);
        }
        
        String searchPattern = "%" + query.toLowerCase() + "%";
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, searchPattern);
        pstmt.setString(4, status);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointmentFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur recherche RDV avec statut: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public List<Appointment> findByStatus(String status) {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
                "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
                "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.status = ? " +
                "ORDER BY a.date_time DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, status);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointmentFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur recherche RDV par statut: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public List<Appointment> findAll() {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
                "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
                "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.date_time DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            appointments.add(mapAppointmentFromResultSet(rs));
        }
    } catch (SQLException e) {
        System.err.println("Erreur findAll: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public List<Appointment> findByPatient(int patientId) {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
                 "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
                 "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
                 "FROM appointments a " +
                 "LEFT JOIN patients p ON a.patient_id = p.id " +
                 "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                 "WHERE a.patient_id = ? " +
                 "ORDER BY a.date_time DESC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, patientId);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointmentFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur recherche RDVs par patient: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public List<Appointment> findByDate(LocalDate date) {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
                "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
                "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE DATE(a.date_time) = ? " +
                "ORDER BY a.date_time";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setDate(1, Date.valueOf(date));
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointmentFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur recherche RDVs par date: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public List<Appointment> findUpcoming() {
    List<Appointment> appointments = new ArrayList<>();
    String sql = "SELECT a.*, " +
                 "p.first_name as patient_first_name, p.last_name as patient_last_name, " +
                 "d.first_name as doctor_first_name, d.last_name as doctor_last_name, d.specialization " +
                 "FROM appointments a " +
                 "LEFT JOIN patients p ON a.patient_id = p.id " +
                 "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                 "WHERE a.date_time >= ? " +
                 "ORDER BY a.date_time";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                appointments.add(mapAppointmentFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur recherche RDVs à venir: " + e.getMessage());
        e.printStackTrace();
    }
    return appointments;
}

public boolean save(Appointment appointment) {
    if (appointment.getId() == 0) {
        return create(appointment);
    } else {
        return update(appointment);
    }
}


public boolean update(Appointment appointment) {
    String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, date_time = ?, " +
                "duration = ?, reason = ?, notes = ?, status = ? WHERE id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        setAppointmentParameters(pstmt, appointment);
        pstmt.setInt(8, appointment.getId());
        
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Erreur mise à jour RDV: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}



public List<ReportLine> findReport(LocalDate from, LocalDate to) throws SQLException {
    String sql =
        "SELECT p.id AS patientIndex, " +
        "TIMESTAMPDIFF(YEAR, p.date_of_birth, CURDATE()) AS age, " +
        "CONCAT(p.last_name, ' ', p.first_name) AS patientNom, " +
        "p.couverture AS couvertureSociale, " +
        "a.date_time AS dateRdv, " +
        "CONCAT(d.last_name, ' ', d.first_name) AS medecin, " +
        "d.specialization AS specialite, " +
        "d.schedule AS batiment, " +
        "a.reason AS observations " +
        "FROM appointments a " +
        "JOIN patients p ON a.patient_id = p.id " +
        "JOIN doctors d ON a.doctor_id = d.id " +
        "WHERE DATE(a.date_time) BETWEEN ? AND ? " +
        "ORDER BY a.date_time";


    List<ReportLine> list = new ArrayList<>();
    try (Connection c = DatabaseConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ReportLine(
                    rs.getInt("patientIndex"),
                    rs.getInt("age"),
                    rs.getString("patientNom"),
                    rs.getString("couvertureSociale"),
                    rs.getTimestamp("dateRdv").toLocalDateTime(),
                    rs.getString("medecin"),
                    rs.getString("specialite"),
                    rs.getString("batiment"),
                    rs.getString("observations")
                ));
           
           
            }
        }
    }
    return list;
}


public boolean delete(int id) {
    String sql = "DELETE FROM appointments WHERE id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, id);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Erreur suppression RDV: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

private void setAppointmentParameters(PreparedStatement pstmt, Appointment appointment) throws SQLException {
    pstmt.setInt(1, appointment.getPatient().getId());
    pstmt.setInt(2, appointment.getDoctor().getId());
    pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));
    pstmt.setInt(4, appointment.getDuration());
    pstmt.setString(5, appointment.getReason());
    pstmt.setString(6, appointment.getNotes());
    pstmt.setString(7, appointment.getStatus());
}

private Appointment mapAppointmentFromResultSet(ResultSet rs) throws SQLException {
    Appointment appointment = new Appointment();
    appointment.setId(rs.getInt("id"));
    
    Patient patient = new Patient();
    patient.setId(rs.getInt("patient_id"));
    patient.setFirstName(rs.getString("patient_first_name"));
    patient.setLastName(rs.getString("patient_last_name"));
    appointment.setPatient(patient);
    
    Doctor doctor = new Doctor();
    doctor.setId(rs.getInt("doctor_id"));
    doctor.setFirstName(rs.getString("doctor_first_name"));
    doctor.setLastName(rs.getString("doctor_last_name"));
    doctor.setSpecialization(rs.getString("specialization"));
    appointment.setDoctor(doctor);
    
    appointment.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
    appointment.setDuration(rs.getInt("duration"));
    appointment.setReason(rs.getString("reason"));
    appointment.setNotes(rs.getString("notes"));
    appointment.setStatus(rs.getString("status"));
    
    return appointment;
}

}
