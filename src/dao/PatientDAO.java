package dao;

import model.Patient;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public boolean create(Patient patient) {
        String sql = "INSERT INTO patients (cin, first_name, last_name, date_of_birth, phone, address, medical_history, couverture) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, patient.getCin());
            pstmt.setString(2, patient.getFirstName());
            pstmt.setString(3, patient.getLastName());
            pstmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(5, patient.getPhone());
            pstmt.setString(6, patient.getAddress());
            pstmt.setString(7, patient.getMedicalHistory());
            pstmt.setString(8, patient.getCouverture()); // nouvelle ligne

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        patient.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPatientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Patient findByCin(String cin) {
        String sql = "SELECT * FROM patients WHERE cin = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cin);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPatientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche patient par CIN: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        String dbCin = rs.getString("cin");
        patient.setCin(dbCin != null ? dbCin : null);
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setPhone(rs.getString("phone"));
        patient.setAddress(rs.getString("address"));
        patient.setMedicalHistory(rs.getString("medical_history"));
        patient.setCouverture(rs.getString("couverture")); // nouvelle ligne
        return patient;
    }

    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }

    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        
        // Vérifier si le terme de recherche est un ID
        try {
            int id = Integer.parseInt(searchTerm);
            Patient patientById = findById(id);
            if (patientById != null) {
                patients.add(patientById);
                return patients;
            }
        } catch (NumberFormatException e) {
            // Ce n'est pas un ID, continuer avec les autres critères
        }

        // Vérifier si le terme de recherche est un CIN
        Patient patientByCin = findByCin(searchTerm);
        if (patientByCin != null) {
            patients.add(patientByCin);
            return patients;
        }

        // Recherche par nom et autres critères
        String sql = "SELECT * FROM patients WHERE " +
                     "LOWER(cin) LIKE ? OR " +
                     "LOWER(first_name) LIKE ? OR " +
                     "LOWER(last_name) LIKE ? " +
                     "ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String term = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(extractPatientFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des patients: " + e.getMessage());
            e.printStackTrace();
        }

        return patients;
    }

    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET cin = ?, first_name = ?, last_name = ?, date_of_birth = ?, " +
                     "phone = ?, address = ?, medical_history = ?, couverture = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getCin());
            pstmt.setString(2, patient.getFirstName());
            pstmt.setString(3, patient.getLastName());
            pstmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(5, patient.getPhone());
            pstmt.setString(6, patient.getAddress());
            pstmt.setString(7, patient.getMedicalHistory());
            pstmt.setString(8, patient.getCouverture()); // nouvelle ligne
            pstmt.setInt(9, patient.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";

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
}

