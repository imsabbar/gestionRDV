package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient {
    private int id;
    private String cin;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;
    private String medicalHistory;
    private String couverture; // <-- Champ ajouté
    private List<Appointment> appointments;

    public Patient() {
        this.appointments = new ArrayList<>();
    }

    public Patient(int id, String cin, String firstName, String lastName, LocalDate dateOfBirth,
                   String phone, String address, String medicalHistory, String couverture) {
        this.id = id;
        this.setCin(cin);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
    
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.couverture = couverture;
        this.appointments = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            this.cin = null;
            return;
        }

        String normalizedCin = cin.trim().toUpperCase();

        if (normalizedCin.length() > 8) {
            normalizedCin = normalizedCin.substring(0, 8);
        }

        this.cin = normalizedCin;
    }

    public static boolean isValidCinFormat(String cin) {
        return cin != null && cin.matches("^[A-Z]{2}\\d{6}$");
    }

    public static String formatCin(String rawCin) {
        if (rawCin == null || rawCin.trim().isEmpty()) {
            return null;
        }
        return rawCin.trim().toUpperCase();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

   


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getCouverture() {
        return couverture;
    }

    public void setCouverture(String couverture) {
        this.couverture = couverture;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        this.appointments.remove(appointment);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
