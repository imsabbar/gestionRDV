package model;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private int duration; // in minutes
    private String reason;
    private String notes;
    private String status; // "SCHEDULED", "COMPLETED", "CANCELLED", "NO_SHOW"

    public Appointment() {
    }

    public Appointment(int id, Patient patient, Doctor doctor, LocalDateTime dateTime, 
                      int duration, String reason, String notes, String status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.duration = duration;
        this.reason = reason;
        this.notes = notes;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return dateTime.plusMinutes(duration);
    }

    // Nouvelle méthode pour obtenir le jour en français
    public String getDay() {
        String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        return days[dateTime.getDayOfWeek().getValue() - 1];
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s with Dr. %s", 
                dateTime.toLocalDate(), 
                dateTime.toLocalTime(), 
                doctor.getLastName());
    }
    
}