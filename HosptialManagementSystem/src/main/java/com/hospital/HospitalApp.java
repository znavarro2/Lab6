package com.hospital;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class HospitalApp extends Application {

    private final PatientDAO dao = new PatientDAO();
    private final ObservableList<Patient> patientList = FXCollections.observableArrayList();

    // Form Controls
    private TextField txtId;
    private TextField txtName;
    private TextField txtAge;
    private ComboBox<String> cmbGender;
    private TextField txtPhone;
    private TextField txtDiagnosis;
    private DatePicker dpAdmissionDate;

    // Table & Search Controls
    private TextField txtSearch;
    private TableView<Patient> tableView;
    private Label lblStatus;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Title & Subtitle in VBox
        VBox topBox = new VBox(5);
        Label lblTitle = new Label("Hospital Management System");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label lblSubtitle = new Label("Patient Registration & Records Module");
        lblSubtitle.setTextFill(Color.GRAY);
        topBox.getChildren().addAll(lblTitle, lblSubtitle);
        topBox.setPadding(new Insets(0, 0, 15, 0));
        root.setTop(topBox);

        // HBox (Left: Form, Right: Records Area)
        HBox centerBox = new HBox(20);
        VBox leftFormArea = createFormSection();
        VBox rightRecordsArea = createRecordsSection();
        HBox.setHgrow(rightRecordsArea, Priority.ALWAYS);
        centerBox.getChildren().addAll(leftFormArea, rightRecordsArea);
        root.setCenter(centerBox);

        // Status Message
        lblStatus = new Label("Ready.");
        lblStatus.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(lblStatus);

        // Initial load of records from database
        loadAllPatients();

        Scene scene = new Scene(root, 1050, 560);
        primaryStage.setTitle("Hospital Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createFormSection() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        txtId = new TextField();
        txtId.setEditable(false);
        txtId.setPromptText("Auto-generated");
        txtId.setStyle("-fx-background-color: #f0f0f0;");

        txtName = new TextField();
        txtName.setPromptText("Full name");

        txtAge = new TextField();
        txtAge.setPromptText("0 - 125");

        cmbGender = new ComboBox<>();
        cmbGender.getItems().addAll("Female", "Male", "Non-binary", "Prefer not to say");
        cmbGender.setMaxWidth(Double.MAX_VALUE);
        cmbGender.setPromptText("Select gender");

        txtPhone = new TextField();
        txtPhone.setPromptText("e.g., 416-555-0199");

        txtDiagnosis = new TextField();
        txtDiagnosis.setPromptText("Diagnosis / reason");

        dpAdmissionDate = new DatePicker();
        dpAdmissionDate.setMaxWidth(Double.MAX_VALUE);

        grid.addRow(0, new Label("Patient ID:"), txtId);
        grid.addRow(1, new Label("Full Name:"), txtName);
        grid.addRow(2, new Label("Age:"), txtAge);
        grid.addRow(3, new Label("Gender:"), cmbGender);
        grid.addRow(4, new Label("Phone:"), txtPhone);
        grid.addRow(5, new Label("Diagnosis:"), txtDiagnosis);
        grid.addRow(6, new Label("Admission Date:"), dpAdmissionDate);

        // Action Buttons
        Button btnAdd = new Button("Add Patient");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnClear = new Button("Clear");

        btnAdd.setOnAction(e -> handleAdd());
        btnUpdate.setOnAction(e -> handleUpdate());
        btnDelete.setOnAction(e -> handleDelete());
        btnClear.setOnAction(e -> clearForm());

        HBox row1 = new HBox(10, btnAdd, btnUpdate);
        HBox row2 = new HBox(10, btnDelete, btnClear);
        VBox buttonBox = new VBox(8, row1, row2);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox formContainer = new VBox(10, new Label("Patient Details"), grid, buttonBox);
        formContainer.setPrefWidth(330);
        return formContainer;
    }

    private VBox createRecordsSection() {
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by name or phone...");
        HBox.setHgrow(txtSearch, Priority.ALWAYS);

        Button btnSearch = new Button("Search");
        Button btnShowAll = new Button("Show All");

        btnSearch.setOnAction(e -> handleSearch());
        btnShowAll.setOnAction(e -> loadAllPatients());

        HBox searchBar = new HBox(10, txtSearch, btnSearch, btnShowAll);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        tableView = new TableView<>(patientList);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<Patient, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colId.setPrefWidth(50);

        TableColumn<Patient, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(140);

        TableColumn<Patient, Integer> colAge = new TableColumn<>("Age");
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colAge.setPrefWidth(50);

        TableColumn<Patient, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colGender.setPrefWidth(110);

        TableColumn<Patient, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setPrefWidth(110);

        TableColumn<Patient, String> colDiagnosis = new TableColumn<>("Diagnosis");
        colDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colDiagnosis.setPrefWidth(150);

        TableColumn<Patient, LocalDate> colDate = new TableColumn<>("Admission Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("admissionDate"));
        colDate.setPrefWidth(110);

        tableView.getColumns().addAll(colId, colName, colAge, colGender, colPhone, colDiagnosis, colDate);

        // Populate form when row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            }
        });

        return new VBox(10, searchBar, tableView);
    }

    private void handleAdd() {
        Patient patient = validateAndBuildPatient(false);
        if (patient == null) return;

        try {
            dao.addPatient(patient);
            loadAllPatients();
            clearForm();
            setStatus("Patient added successfully.", false);
        } catch (SQLException e) {
            setStatus("Database error adding patient: " + e.getMessage(), true);
        }
    }

    private void handleUpdate() {
        Patient patient = validateAndBuildPatient(true);
        if (patient == null) return;

        try {
            dao.updatePatient(patient);
            loadAllPatients();
            clearForm();
            setStatus("Patient updated successfully.", false);
        } catch (SQLException e) {
            setStatus("Database error updating patient: " + e.getMessage(), true);
        }
    }

    private void handleDelete() {
        Patient selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a patient from the table to delete.", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Patient Record");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + " (ID: " + selected.getPatientId() + ")?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                dao.deletePatient(selected.getPatientId());
                loadAllPatients();
                clearForm();
                setStatus("Patient record deleted.", false);
            } catch (SQLException e) {
                setStatus("Database error deleting patient: " + e.getMessage(), true);
            }
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllPatients();
            return;
        }
        try {
            patientList.setAll(dao.searchPatients(keyword));
            setStatus("Found " + patientList.size() + " matching record(s).", false);
        } catch (SQLException e) {
            setStatus("Search error: " + e.getMessage(), true);
        }
    }

    private void loadAllPatients() {
        try {
            patientList.setAll(dao.getAllPatients());
            setStatus("Loaded " + patientList.size() + " patient record(s).", false);
        } catch (SQLException e) {
            setStatus("Database connection error: " + e.getMessage(), true);
        }
    }

    private Patient validateAndBuildPatient(boolean requireId) {
        int id = 0;
        if (requireId) {
            if (txtId.getText().isEmpty()) {
                setStatus("Please select a patient record from the table to update.", true);
                return null;
            }
            id = Integer.parseInt(txtId.getText());
        }

        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            setStatus("Validation Error: Full name is required.", true);
            return null;
        }

        int age;
        try {
            age = Integer.parseInt(txtAge.getText().trim());
            if (age < 0 || age > 125) {
                setStatus("Validation Error: Age must be between 0 and 125.", true);
                return null;
            }
        } catch (NumberFormatException e) {
            setStatus("Validation Error: Age must be a valid whole number.", true);
            return null;
        }

        String gender = cmbGender.getValue();
        if (gender == null || gender.isEmpty()) {
            setStatus("Validation Error: Please select a gender.", true);
            return null;
        }

        String phone = txtPhone.getText().trim();
        if (phone.isEmpty() || !phone.matches("^[0-9+\\-()\\s]{7,20}$")) {
            setStatus("Validation Error: Enter a valid phone number (7-20 digits/symbols).", true);
            return null;
        }

        String diagnosis = txtDiagnosis.getText().trim();
        if (diagnosis.isEmpty()) {
            setStatus("Validation Error: Diagnosis / reason for visit is required.", true);
            return null;
        }

        LocalDate admissionDate = dpAdmissionDate.getValue();
        if (admissionDate == null) {
            setStatus("Validation Error: Admission date is required.", true);
            return null;
        }
        if (admissionDate.isAfter(LocalDate.now())) {
            setStatus("Validation Error: Admission date cannot be in the future.", true);
            return null;
        }

        return new Patient(id, name, age, gender, phone, diagnosis, admissionDate);
    }

    private void populateForm(Patient p) {
        txtId.setText(String.valueOf(p.getPatientId()));
        txtName.setText(p.getName());
        txtAge.setText(String.valueOf(p.getAge()));
        cmbGender.setValue(p.getGender());
        txtPhone.setText(p.getPhone());
        txtDiagnosis.setText(p.getDiagnosis());
        dpAdmissionDate.setValue(p.getAdmissionDate());
    }

    private void clearForm() {
        txtId.clear();
        txtName.clear();
        txtAge.clear();
        cmbGender.setValue(null);
        txtPhone.clear();
        txtDiagnosis.clear();
        dpAdmissionDate.setValue(null);
        tableView.getSelectionModel().clearSelection();
        setStatus("Form cleared.", false);
    }

    private void setStatus(String message, boolean isError) {
        lblStatus.setText(message);
        lblStatus.setTextFill(isError ? Color.CRIMSON : Color.DARKGREEN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}