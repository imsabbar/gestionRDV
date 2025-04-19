package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        createTables();
        insertSampleData();
    }

    private static void createTables() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Table users
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "active TINYINT(1) NOT NULL DEFAULT 1" +
                ")"
            );

            // Table patients
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS patients (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "first_name VARCHAR(50) NOT NULL, " +
                "last_name VARCHAR(50) NOT NULL, " +
                "date_of_birth DATE NOT NULL, " +
                "phone VARCHAR(20), " +
                "cin VARCHAR(20) NOT NULL UNIQUE, " +
                "address TEXT, " +
                "medical_history TEXT, " +
                "couverture VARCHAR(25), " +
                "created_at DATE" +
                ")"
            );

            // Table doctors
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS doctors (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "first_name VARCHAR(50) NOT NULL, " +
                "last_name VARCHAR(50) NOT NULL, " +
                "specialization VARCHAR(100), " +
                "phone VARCHAR(20), " +
                "email VARCHAR(100), " +
                "schedule TEXT, " +
                "working_days VARCHAR(100), " +
                "max_patients_per_day INT DEFAULT 10, " +
                "created_at DATE" +
                ")"
            );

            // Table appointments
            stmt.execute(
            	    "CREATE TABLE IF NOT EXISTS appointments (" +
            	    "id INT AUTO_INCREMENT PRIMARY KEY, " +
            	    "patient_id INT NOT NULL, " +
            	    "doctor_id INT NOT NULL, " +
            	    "date_time DATETIME NOT NULL, " +
            	    "duration INT NOT NULL DEFAULT 30, " +
            	    "reason TEXT, " +  // 🔧 Champ manquant ajouté ici
            	    "notes TEXT, " +
            	    "status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', " +
            	    "FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE, " +
            	    "FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE" +
            	    ")"
            	);



            // Table receptionists
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS receptionists (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "first_name VARCHAR(50) NOT NULL, " +
                "last_name VARCHAR(50) NOT NULL, " +
                "phone VARCHAR(20), " +
                "date_of_birth DATE NOT NULL, " +
                "identifiant VARCHAR(100) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "sexe VARCHAR(10) NOT NULL" +
                ")"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertSampleData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean hasUsers = false, hasPatients = false, hasDoctors = false;

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) hasUsers = rs.getInt(1) > 0;

            rs = stmt.executeQuery("SELECT COUNT(*) FROM patients");
            if (rs.next()) hasPatients = rs.getInt(1) > 0;

            rs = stmt.executeQuery("SELECT COUNT(*) FROM doctors");
            if (rs.next()) hasDoctors = rs.getInt(1) > 0;

            if (!hasUsers) {
                stmt.execute("INSERT INTO users (username, password, role, active) " +
                             "VALUES ('admin', 'admin123', 'ADMIN', 1)");
                stmt.execute("INSERT INTO users (username, password, role, active) " +
                             "VALUES ('secretary', 'secret123', 'RECEPTIONIST', 1)");
            }

            if (!hasPatients) {
                stmt.execute("INSERT INTO patients (first_name, last_name, date_of_birth, phone, cin, address, medical_history, couverture, created_at) " +
                             "VALUES ('Hossam', 'Dalil', '1980-05-15', '0123456789', 'MC12345', '123 Rue de Paris, ElJadida', 'Allergie au pollen', 'CNSS', '2025-05-17')");
                stmt.execute("INSERT INTO patients (first_name, last_name, date_of_birth, phone, cin, address, medical_history, couverture, created_at) " +
                             "VALUES ('Maram', 'Mikal', '1975-10-20', '0987654321', 'MD45367', '456 Avenue Victor Hugo, CasaBlanca', 'Hypertension', 'AMO', '2025-05-11')");
                stmt.execute("INSERT INTO patients (first_name, last_name, date_of_birth, phone, cin, address, medical_history, couverture, created_at) " +
                             "VALUES ('Badr', 'Khadir', '1990-02-28', '0654321789', 'AB12890', '789 Boulevard Saint-Michel, Rabat', 'Aucun', 'CNOPS', '2025-05-16')");
            }

            if (!hasDoctors) {
                stmt.execute("INSERT INTO doctors (first_name, last_name, specialization, phone, email, schedule, working_days, max_patients_per_day, created_at) " +
                             "VALUES ('Mohammed', 'El Alami', 'Cardiologie', '0661234567', 'elalami.med@gmail.com', '09:00-17:00', 'Lundi-Vendredi', 15, '2024-04-01')");
                stmt.execute("INSERT INTO doctors (first_name, last_name, specialization, phone, email, schedule, working_days, max_patients_per_day, created_at) " +
                             "VALUES ('Fatima', 'Benani', 'Pédiatrie', '0662345678', 'benani.fatima@gmail.com', '08:30-16:30', 'Lundi-Samedi', 20, '2025-03-12')");
                stmt.execute("INSERT INTO doctors (first_name, last_name, specialization, phone, email, schedule, working_days, max_patients_per_day, created_at) " +
                             "VALUES ('Karim', 'Idrissi', 'Médecine générale', '0665678901', 'idrissi.karim@gmail.com', '08:00-16:00', 'Lundi-Samedi', 25, '2023-07-06')");
                stmt.execute("INSERT INTO doctors (first_name, last_name, specialization, phone, email, schedule, working_days, max_patients_per_day, created_at) " +
                             "VALUES ('Laila', 'Chraibi', 'Dermatologie', '0664567890', 'chraibi.laila@gmail.com', '10:00-18:00', 'Mardi-Samedi', 10, '2025-05-12')");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
