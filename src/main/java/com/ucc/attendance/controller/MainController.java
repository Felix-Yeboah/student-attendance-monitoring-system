package com.ucc.attendance.controller;

import com.ucc.attendance.dao.CourseDao;
import com.ucc.attendance.dao.StudentDao;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.exception.ValidationException;
import com.ucc.attendance.model.AttendanceRecord;
import com.ucc.attendance.model.AttendanceStatus;
import com.ucc.attendance.model.Course;
import com.ucc.attendance.model.DashboardStatistics;
import com.ucc.attendance.model.Student;
import com.ucc.attendance.service.AttendanceService;
import com.ucc.attendance.util.InputValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX controller for the main dashboard and all application tabs.
 * It reacts to button clicks, validates form input, and delegates persistence to DAO/service classes.
 */
public class MainController {
    private final StudentDao studentDao = new StudentDao();
    private final CourseDao courseDao = new CourseDao();
    private final AttendanceService attendanceService = new AttendanceService();

    private final ObservableList<Student> studentItems = FXCollections.observableArrayList();
    private final ObservableList<Course> courseItems = FXCollections.observableArrayList();
    private final ObservableList<AttendanceRecord> attendanceItems = FXCollections.observableArrayList();
    private final ObservableList<AttendanceRecord> reportItems = FXCollections.observableArrayList();

    @FXML private Label totalStudentsLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label todayPresentLabel;
    @FXML private Label todayLateLabel;
    @FXML private Label todayAbsentLabel;
    @FXML private Label todayMarkedLabel;
    @FXML private Label todayAttendanceRateLabel;
    @FXML private Label todayRiskLabel;

    @FXML private TextField studentNumberField;
    @FXML private TextField studentFirstNameField;
    @FXML private TextField studentLastNameField;
    @FXML private TextField studentEmailField;
    @FXML private TextField studentProgrammeField;
    @FXML private ComboBox<String> studentGenderComboBox;
    @FXML private TextField studentSearchField;
    @FXML private Button studentSaveButton;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNumberColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentEmailColumn;
    @FXML private TableColumn<Student, String> studentProgrammeColumn;
    @FXML private TableColumn<Student, String> studentGenderColumn;

    @FXML private TextField courseCodeField;
    @FXML private TextField courseTitleField;
    @FXML private TextField courseLecturerField;
    @FXML private TextField courseSemesterField;
    @FXML private Button courseSaveButton;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseIdColumn;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseTitleColumn;
    @FXML private TableColumn<Course, String> courseLecturerColumn;
    @FXML private TableColumn<Course, String> courseSemesterColumn;

    @FXML private ComboBox<Course> attendanceCourseComboBox;
    @FXML private DatePicker attendanceDatePicker;
    @FXML private TableView<AttendanceRecord> attendanceTable;
    @FXML private TableColumn<AttendanceRecord, String> attendanceStudentNumberColumn;
    @FXML private TableColumn<AttendanceRecord, String> attendanceStudentNameColumn;
    @FXML private TableColumn<AttendanceRecord, AttendanceStatus> attendanceStatusColumn;

    @FXML private ComboBox<Course> reportCourseComboBox;
    @FXML private DatePicker reportDatePicker;
    @FXML private ComboBox<AttendanceStatus> reportStatusComboBox;
    @FXML private TableView<AttendanceRecord> reportTable;
    @FXML private TableColumn<AttendanceRecord, String> reportCourseColumn;
    @FXML private TableColumn<AttendanceRecord, String> reportStudentNumberColumn;
    @FXML private TableColumn<AttendanceRecord, String> reportStudentNameColumn;
    @FXML private TableColumn<AttendanceRecord, LocalDate> reportDateColumn;
    @FXML private TableColumn<AttendanceRecord, AttendanceStatus> reportStatusColumn;
    @FXML private Label reportCountLabel;
    @FXML private Label reportAttendanceRateLabel;

    @FXML
    public void initialize() {
        configureStudentTable();
        configureCourseTable();
        configureAttendanceTable();
        configureReportTable();

        studentGenderComboBox.getItems().addAll("Female", "Male", "Prefer not to say");
        reportStatusComboBox.getItems().addAll(AttendanceStatus.values());
        attendanceDatePicker.setValue(LocalDate.now());

        studentSearchField.textProperty().addListener((observable, oldValue, newValue) -> loadStudents());
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedStudent) -> populateStudentForm(selectedStudent)
        );
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedCourse) -> populateCourseForm(selectedCourse)
        );

        refreshAllData();
    }

    private void configureStudentTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentProgrammeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));
        studentGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        studentTable.setItems(studentItems);
    }

    private void configureCourseTable() {
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        courseLecturerColumn.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        courseTable.setItems(courseItems);
    }

    private void configureAttendanceTable() {
        attendanceStudentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        attendanceStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        attendanceStatusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(AttendanceStatus.values())
        ));
        attendanceStatusColumn.setOnEditCommit(event -> event.getRowValue().setStatus(event.getNewValue()));
        attendanceTable.setEditable(true);
        attendanceTable.setItems(attendanceItems);
    }

    private void configureReportTable() {
        reportCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        reportStudentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        reportStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceDate"));
        reportStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reportTable.setItems(reportItems);
    }

    @FXML
    private void handleSaveStudent() {
        try {
            Student student = Optional.ofNullable(studentTable.getSelectionModel().getSelectedItem())
                    .orElseGet(Student::new);

            student.setStudentNumber(InputValidator.requireStudentNumber(studentNumberField.getText()));
            student.setFirstName(InputValidator.requireText(studentFirstNameField.getText(), "First name"));
            student.setLastName(InputValidator.requireText(studentLastNameField.getText(), "Last name"));
            student.setEmail(InputValidator.requireEmail(studentEmailField.getText()));
            student.setProgramme(InputValidator.requireText(studentProgrammeField.getText(), "Programme"));
            student.setGender(InputValidator.requireText(studentGenderComboBox.getValue(), "Gender"));
            student.setActive(true);

            boolean newStudent = student.getId() == 0;
            studentDao.save(student);
            clearStudentForm();
            loadStudents();
            loadDashboard();
            showInformation(newStudent ? "Student added successfully." : "Student updated successfully.");
        } catch (ValidationException | DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Select a student row before choosing Delete.");
            return;
        }

        if (confirm("Delete Student", "Delete " + selectedStudent.getFullName() + "?")) {
            try {
                studentDao.deleteById(selectedStudent.getId());
                clearStudentForm();
                loadStudents();
                loadDashboard();
                showInformation("Student deleted successfully.");
            } catch (DataAccessException exception) {
                showError(exception.getMessage());
            }
        }
    }

    @FXML
    private void handleClearStudentForm() {
        clearStudentForm();
    }

    private void clearStudentForm() {
        studentTable.getSelectionModel().clearSelection();
        studentNumberField.clear();
        studentFirstNameField.clear();
        studentLastNameField.clear();
        studentEmailField.clear();
        studentProgrammeField.clear();
        studentGenderComboBox.setValue(null);
        studentSaveButton.setText("Save Student");
    }

    private void populateStudentForm(Student student) {
        if (student == null) {
            return;
        }
        studentNumberField.setText(student.getStudentNumber());
        studentFirstNameField.setText(student.getFirstName());
        studentLastNameField.setText(student.getLastName());
        studentEmailField.setText(student.getEmail());
        studentProgrammeField.setText(student.getProgramme());
        studentGenderComboBox.setValue(student.getGender());
        studentSaveButton.setText("Update Student");
    }

    @FXML
    private void handleSaveCourse() {
        try {
            Course course = Optional.ofNullable(courseTable.getSelectionModel().getSelectedItem())
                    .orElseGet(Course::new);

            course.setCourseCode(InputValidator.requireCourseCode(courseCodeField.getText()));
            course.setCourseTitle(InputValidator.requireText(courseTitleField.getText(), "Course title"));
            course.setLecturerName(InputValidator.requireText(courseLecturerField.getText(), "Lecturer name"));
            course.setSemester(InputValidator.requireText(courseSemesterField.getText(), "Semester"));

            boolean newCourse = course.getId() == 0;
            courseDao.save(course);
            clearCourseForm();
            loadCourses();
            loadDashboard();
            showInformation(newCourse ? "Course added successfully." : "Course updated successfully.");
        } catch (ValidationException | DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("Select a course row before choosing Delete.");
            return;
        }

        if (confirm("Delete Course", "Delete " + selectedCourse.getCourseCode() + "?")) {
            try {
                courseDao.deleteById(selectedCourse.getId());
                clearCourseForm();
                loadCourses();
                loadDashboard();
                showInformation("Course deleted successfully.");
            } catch (DataAccessException exception) {
                showError(exception.getMessage());
            }
        }
    }

    @FXML
    private void handleClearCourseForm() {
        clearCourseForm();
    }

    private void clearCourseForm() {
        courseTable.getSelectionModel().clearSelection();
        courseCodeField.clear();
        courseTitleField.clear();
        courseLecturerField.clear();
        courseSemesterField.clear();
        courseSaveButton.setText("Save Course");
    }

    private void populateCourseForm(Course course) {
        if (course == null) {
            return;
        }
        courseCodeField.setText(course.getCourseCode());
        courseTitleField.setText(course.getCourseTitle());
        courseLecturerField.setText(course.getLecturerName());
        courseSemesterField.setText(course.getSemester());
        courseSaveButton.setText("Update Course");
    }

    @FXML
    private void handleLoadRegister() {
        Course selectedCourse = attendanceCourseComboBox.getValue();
        LocalDate selectedDate = attendanceDatePicker.getValue();
        if (selectedCourse == null || selectedDate == null) {
            showError("Select both a course and attendance date before loading the register.");
            return;
        }

        try {
            attendanceItems.setAll(attendanceService.loadRegister(selectedCourse, selectedDate));
            if (attendanceItems.isEmpty()) {
                showInformation("No active students are available. Add students first.");
            }
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleSaveAttendance() {
        if (attendanceItems.isEmpty()) {
            showError("Load an attendance register before saving.");
            return;
        }

        try {
            attendanceService.saveAttendance(attendanceItems);
            loadDashboard();
            showInformation("Attendance saved for " + attendanceItems.size() + " student(s).");
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleRunReport() {
        try {
            Course selectedCourse = reportCourseComboBox.getValue();
            Integer courseId = selectedCourse == null ? null : selectedCourse.getId();
            List<AttendanceRecord> records = attendanceService.getReport(
                    courseId,
                    reportDatePicker.getValue(),
                    reportStatusComboBox.getValue()
            );
            reportItems.setAll(records);
            updateReportSummary(records);
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleClearReportFilters() {
        reportCourseComboBox.setValue(null);
        reportDatePicker.setValue(null);
        reportStatusComboBox.setValue(null);
        reportItems.clear();
        updateReportSummary(List.of());
    }

    @FXML
    private void handleExportReport() {
        if (reportItems.isEmpty()) {
            showError("Run a report with at least one result before exporting CSV.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Attendance Report");
        fileChooser.setInitialFileName("attendance-report-" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        Window window = reportTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                writer.write("Course Code,Student Number,Student Name,Attendance Date,Status");
                writer.newLine();
                for (AttendanceRecord record : reportItems) {
                    writer.write(csv(record.getCourseCode()) + ","
                            + csv(record.getStudentNumber()) + ","
                            + csv(record.getStudentName()) + ","
                            + csv(record.getAttendanceDate().toString()) + ","
                            + csv(record.getStatus().getDisplayName()));
                    writer.newLine();
                }
                showInformation("CSV report saved successfully.");
            } catch (IOException exception) {
                showError("Could not export the report: " + exception.getMessage());
            }
        }
    }

    @FXML
    private void handleRefreshDashboard() {
        refreshAllData();
    }

    @FXML
    private void handleExit() {
        if (confirm("Exit Application", "Do you want to close the Student Attendance Monitoring System?")) {
            javafx.application.Platform.exit();
        }
    }

    private void refreshAllData() {
        loadStudents();
        loadCourses();
        loadDashboard();
        handleClearReportFilters();
    }

    private void loadStudents() {
        try {
            String searchText = studentSearchField == null ? "" : studentSearchField.getText();
            List<Student> students = searchText == null || searchText.isBlank()
                    ? studentDao.findAll()
                    : studentDao.search(searchText);
            studentItems.setAll(students);
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private void loadCourses() {
        try {
            int attendanceCourseId = getSelectedCourseId(attendanceCourseComboBox);
            int reportCourseId = getSelectedCourseId(reportCourseComboBox);
            List<Course> courses = courseDao.findAll();
            courseItems.setAll(courses);
            attendanceCourseComboBox.setItems(FXCollections.observableArrayList(courses));
            reportCourseComboBox.setItems(FXCollections.observableArrayList(courses));
            selectCourseById(attendanceCourseComboBox, attendanceCourseId);
            selectCourseById(reportCourseComboBox, reportCourseId);
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private int getSelectedCourseId(ComboBox<Course> comboBox) {
        return comboBox.getValue() == null ? -1 : comboBox.getValue().getId();
    }

    private void selectCourseById(ComboBox<Course> comboBox, int courseId) {
        comboBox.getItems().stream()
                .filter(course -> course.getId() == courseId)
                .findFirst()
                .ifPresent(comboBox::setValue);
    }

    private void loadDashboard() {
        try {
            DashboardStatistics statistics = attendanceService.getDashboardStatistics(LocalDate.now());

            int present = statistics.getTodayPresent();
            int late = statistics.getTodayLate();
            int absent = statistics.getTodayAbsent();
            int markedToday = present + late + absent;

            double attendanceRate = markedToday == 0
                    ? 0.0
                    : ((present + late) * 100.0) / markedToday;

            totalStudentsLabel.setText(String.valueOf(statistics.getTotalStudents()));
            totalCoursesLabel.setText(String.valueOf(statistics.getTotalCourses()));
            todayPresentLabel.setText(String.valueOf(present));
            todayLateLabel.setText(String.valueOf(late));
            todayAbsentLabel.setText(String.valueOf(absent));
            todayMarkedLabel.setText(String.valueOf(markedToday));
            todayAttendanceRateLabel.setText(String.format("%.1f%%", attendanceRate));

            if (markedToday == 0) {
                todayRiskLabel.setText("No register");
            } else if (absent > 0) {
                todayRiskLabel.setText("Review absences");
            } else {
                todayRiskLabel.setText("Good standing");
            }

        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private void updateReportSummary(List<AttendanceRecord> records) {
        long presentOrLate = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.PRESENT
                        || record.getStatus() == AttendanceStatus.LATE)
                .count();
        double percentage = records.isEmpty() ? 0.0 : (presentOrLate * 100.0) / records.size();
        reportCountLabel.setText(records.size() + " record(s)");
        reportAttendanceRateLabel.setText(String.format("Attendance rate: %.1f%%", percentage));
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Attendance Monitoring System");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Action Required");
        alert.setHeaderText("The action could not be completed.");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
