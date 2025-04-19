/*
 * ReportLine.java
 * Model POJO for report rows
 */
package model;

import java.time.LocalDateTime;

public class ReportLine {
    private final int patientIndex;
    private final int age;
    private final String patientNom;
    private final String couvertureSociale;
    private final LocalDateTime dateRdv;
    private final String medecin;
    private final String specialite;
    private final String batiment;
    private final String observations;

    public ReportLine(int patientIndex, int age, String patientNom,
                      String couvertureSociale, LocalDateTime dateRdv,
                      String medecin, String specialite,
                      String batiment, String observations) {
        this.patientIndex = patientIndex;
        this.age = age;
        this.patientNom = patientNom;
        this.couvertureSociale = couvertureSociale;
        this.dateRdv = dateRdv;
        this.medecin = medecin;
        this.specialite = specialite;
        this.batiment = batiment;
        this.observations = observations;
    }

    public int getPatientIndex() { return patientIndex; }
    public int getAge() { return age; }
    public String getPatientNom() { return patientNom; }
    public String getCouvertureSociale() { return couvertureSociale; }
    public LocalDateTime getDateRdv() { return dateRdv; }
    public String getMedecin() { return medecin; }
    public String getSpecialite() { return specialite; }
    public String getBatiment() { return batiment; }
    public String getObservations() { return observations; }
}
