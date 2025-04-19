package dao;

import model.Doctor;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    public boolean create(Doctor doctor) {
        String sql = "INSERT INTO doctors (first_name, last_name, specialization, phone, email, schedule, working_days, max_patients_per_day) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
        	pstmt.setString(1, doctor.getFirstName());
        	pstmt.setString(2, doctor.getLastName());
        	pstmt.setString(3, doctor.getSpecialization());
        	pstmt.setString(4, doctor.getPhone());
        	pstmt.setString(5, doctor.getEmail());
        	pstmt.setString(6, doctor.getSchedule());
        	pstmt.setString(7, doctor.getWorkingDays());
        	pstmt.setInt(8, doctor.getMaxPatientsPerDay());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        doctor.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public Doctor findById(int id) {
    	System.out.println("Recherche docteur ID: " + id); // Debug
        String sql = "SELECT * FROM doctors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDoctorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    public List<Doctor> searchDoctors(String searchTerm) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE " +
                     "LOWER(first_name) LIKE ? OR " +
                     "LOWER(last_name) LIKE ? OR " +
                     "LOWER(specialization) LIKE ? OR " +
                     "LOWER(email) LIKE ? OR " +
                     "phone LIKE ? " +
                     "ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String term = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            pstmt.setString(4, term);
            pstmt.setString(5, term);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(extractDoctorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    
    
    public boolean update(Doctor doctor) {
        String sql = "UPDATE doctors SET first_name = ?, last_name = ?, specialization = ?, " +
                     "phone = ?, email = ?, schedule = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getFirstName());
            pstmt.setString(2, doctor.getLastName());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setString(4, doctor.getPhone());
            pstmt.setString(5, doctor.getEmail());
            pstmt.setString(6, doctor.getSchedule());
            pstmt.setString(7, doctor.getWorkingDays());
            pstmt.setInt(8, doctor.getMaxPatientsPerDay());
            pstmt.setInt(9, doctor.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setFirstName(rs.getString("first_name"));
        doctor.setLastName(rs.getString("last_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setPhone(rs.getString("phone"));
        doctor.setEmail(rs.getString("email"));
        doctor.setSchedule(rs.getString("schedule"));
        doctor.setWorkingDays(rs.getString("working_days"));
        doctor.setMaxPatientsPerDay(rs.getInt("max_patients_per_day"));
        return doctor;
    }
    public List<Doctor> findBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    doctor.setSpecialization(rs.getString("specialization"));
                    doctors.add(doctor);
                }
            }
  } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médecins par spécialité: " + e.getMessage());
        }

        return doctors;
    }


    public List<String> findAllSpecializations() {
        List<String> specializations = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des spécialités: " + e.getMessage());
        }

        return specializations;
    }

    
}