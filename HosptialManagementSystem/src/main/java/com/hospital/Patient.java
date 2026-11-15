package com.hospital;

import java.time.LocalDate;

public class Patient {
    private int patientId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String diagnosis;
    private LocalDate admissionDate;

    // Constructor with ID
    public Patient(int patientId, String name, int age, String gender, String phone, String diagnosis, LocalDate admissionDate) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.diagnosis = diagnosis;
        this.admissionDate = admissionDate;
    }

    // Constructor without ID
    public Patient(String name, int age, String gender, String phone, String diagnosis, LocalDate admissionDate) {
        this(0, name, age, gender, phone, diagnosis, admissionDate);
    }

    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
}