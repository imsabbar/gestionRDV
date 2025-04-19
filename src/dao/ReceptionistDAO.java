package dao;

import model.Receptionist;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDAO {
    
    public boolean create(Receptionist receptionist) {
        String sql = "INSERT INTO receptionists (first_name, last_name, phone, date_of_birth, identifiant, password, sexe) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setReceptionistParameters(pstmt, receptionist);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        receptionist.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la création du réceptionniste");
        }
        return false;
    }
    
    public Receptionist findById(int id) {
        String sql = "SELECT * FROM receptionists WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractReceptionistFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la recherche du réceptionniste par ID");
        }
        return null;
    }
    
    public List<Receptionist> findAll() {
        List<Receptionist> receptionists = new ArrayList<>();
        String sql = "SELECT * FROM receptionists ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                receptionists.add(extractReceptionistFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la récupération de tous les réceptionnistes");
        }
        return receptionists;
    }
    
    public List<Receptionist> searchReceptionists(String searchTerm) {
        List<Receptionist> receptionists = new ArrayList<>();
        String sql = "SELECT * FROM receptionists WHERE " +
                     "LOWER(first_name) LIKE ? OR " +
                     "LOWER(last_name) LIKE ? OR " +
                     "phone LIKE ? OR " +
                     "LOWER(identifiant) LIKE ? " +
                     "ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String term = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            pstmt.setString(4, term);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    receptionists.add(extractReceptionistFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la recherche des réceptionnistes");
        }
        return receptionists;
    }
    
    public List<Receptionist> findBySexe(String sexe) {
        List<Receptionist> receptionists = new ArrayList<>();
        String sql = "SELECT * FROM receptionists WHERE sexe = ? ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sexe);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    receptionists.add(extractReceptionistFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la recherche des réceptionnistes par sexe");
        }
        return receptionists;
    }
    
    public boolean update(Receptionist receptionist) {
        String sql = "UPDATE receptionists SET first_name = ?, last_name = ?, " +
                     "phone = ?, date_of_birth = ?, identifiant = ?, password = ?, sexe = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            setReceptionistParameters(pstmt, receptionist);
            pstmt.setInt(8, receptionist.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la mise à jour du réceptionniste");
            return false;
        }
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM receptionists WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la suppression du réceptionniste");
            return false;
        }
    }
    
    public Receptionist findByUsername(String identifiant) {
        String sql = "SELECT * FROM receptionists WHERE identifiant = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, identifiant);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractReceptionistFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Erreur lors de la recherche du réceptionniste par identifiant");
        }
        return null;
    }
    
    private void setReceptionistParameters(PreparedStatement pstmt, Receptionist receptionist) throws SQLException {
        pstmt.setString(1, receptionist.getFirstName());
        pstmt.setString(2, receptionist.getLastName());
        pstmt.setString(3, receptionist.getPhone());
        pstmt.setDate(4, Date.valueOf(receptionist.getDateOfBirth()));
        pstmt.setString(5, receptionist.getIdentifiant());
        pstmt.setString(6, receptionist.getPassword());
        pstmt.setString(7, receptionist.getSexe());
    }
    
    private Receptionist extractReceptionistFromResultSet(ResultSet rs) throws SQLException {
        Receptionist receptionist = new Receptionist();
        receptionist.setId(rs.getInt("id"));
        receptionist.setFirstName(rs.getString("first_name"));
        receptionist.setLastName(rs.getString("last_name"));
        receptionist.setPhone(rs.getString("phone"));
        receptionist.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        receptionist.setIdentifiant(rs.getString("identifiant"));
        receptionist.setPassword(rs.getString("password"));
        receptionist.setSexe(rs.getString("sexe"));
        return receptionist;
    }
    
    private void handleSQLException(SQLException e, String message) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}