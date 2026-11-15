package com.hospital;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, age, gender, phone, diagnosis, admission_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getDiagnosis());
            stmt.setDate(6, Date.valueOf(patient.getAdmissionDate()));
            stmt.executeUpdate();
        }
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, name, age, gender, phone, diagnosis, admission_date FROM patients ORDER BY patient_id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, phone = ?, diagnosis = ?, admission_date = ? WHERE patient_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getDiagnosis());
            stmt.setDate(6, Date.valueOf(patient.getAdmissionDate()));
            stmt.setInt(7, patient.getPatientId());
            stmt.executeUpdate();
        }
    }

    public void deletePatient(int patientId) throws SQLException {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();
        }
    }

    public List<Patient> searchPatients(String keyword) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, name, age, gender, phone, diagnosis, admission_date FROM patients WHERE name LIKE ? OR phone LIKE ?";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getInt("patient_id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("phone"),
            rs.getString("diagnosis"),
            rs.getDate("admission_date").toLocalDate()
        );
    }
}