package com.ucc.attendance.controller;

import com.ucc.attendance.dao.CourseDao;
import com.ucc.attendance.dao.StudentDao;
import com.ucc.attendance.dao.UserDao;
import com.ucc.attendance.exception.DataAccessException;
import com.ucc.attendance.exception.ValidationException;
import com.ucc.attendance.model.AttendanceRecord;
import com.ucc.attendance.model.AttendanceStatus;
import com.ucc.attendance.model.Course;
import com.ucc.attendance.model.DashboardStatistics;
import com.ucc.attendance.model.Student;
import com.ucc.attendance.model.User;
import com.ucc.attendance.model.UserRole;
import com.ucc.attendance.security.SessionManager;
import com.ucc.attendance.service.AttendanceService;
import com.ucc.attendance.util.InputValidator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * JavaFX controller for the main dashboard and all application tabs.
 */
public class MainController {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final DateTimeFormatter REPORT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final StudentDao studentDao = new StudentDao();
    private final CourseDao courseDao = new CourseDao();
    private final UserDao userDao = new UserDao();
    private final AttendanceService attendanceService = new AttendanceService();

    private final ObservableList<Student> studentItems = FXCollections.observableArrayList();
    private final ObservableList<Course> courseItems = FXCollections.observableArrayList();
    private final ObservableList<User> userItems = FXCollections.observableArrayList();
    private final ObservableList<AttendanceRecord> attendanceItems = FXCollections.observableArrayList();
    private final ObservableList<AttendanceRecord> reportItems = FXCollections.observableArrayList();

    @FXML private Label loggedInUserLabel;
    @FXML private Label loggedInRoleLabel;

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

    @FXML private Tab usersTab;
    @FXML private TextField userFullNameField;
    @FXML private TextField userUsernameField;
    @FXML private PasswordField userPasswordField;
    @FXML private ComboBox<UserRole> userRoleComboBox;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userFullNameColumn;
    @FXML private TableColumn<User, String> userUsernameColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private TableColumn<User, String> userActiveColumn;

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
    @FXML private TableColumn<AttendanceRecord, String> reportMarkedByColumn;
    @FXML private TableColumn<AttendanceRecord, String> reportMarkedAtColumn;
    @FXML private Label reportCountLabel;
    @FXML private Label reportAttendanceRateLabel;

    @FXML
    public void initialize() {
        configureStudentTable();
        configureCourseTable();
        configureUserTable();
        configureAttendanceTable();
        configureReportTable();

        studentGenderComboBox.getItems().addAll("Female", "Male", "Prefer not to say");
        userRoleComboBox.getItems().addAll(UserRole.values());
        reportStatusComboBox.getItems().addAll(AttendanceStatus.values());
        attendanceDatePicker.setValue(LocalDate.now());

        studentSearchField.textProperty().addListener((observable, oldValue, newValue) -> loadStudents());

        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedStudent) -> populateStudentForm(selectedStudent)
        );

        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedCourse) -> populateCourseForm(selectedCourse)
        );

        applyRoleBasedAccess();
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

    private void configureUserTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        userRoleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().name()));

        userActiveColumn.setCellValueFactory(cellData -> {
            boolean active = cellData.getValue().isActive();
            return new SimpleStringProperty(active ? "Active" : "Inactive");
        });

        userTable.setItems(userItems);
    }

    private void configureAttendanceTable() {
        attendanceStudentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        attendanceStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        attendanceStatusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(AttendanceStatus.values())
        ));

        attendanceStatusColumn.setOnEditCommit(event ->
                event.getRowValue().setStatus(event.getNewValue()));

        attendanceTable.setEditable(true);
        attendanceTable.setItems(attendanceItems);
    }

    private void configureReportTable() {
        reportCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        reportStudentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        reportStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceDate"));
        reportStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        reportMarkedByColumn.setCellValueFactory(cellData -> {
            String username = cellData.getValue().getMarkedByUsername();

            if (username == null || username.isBlank()) {
                return new SimpleStringProperty("Not recorded");
            }

            return new SimpleStringProperty(username);
        });

        reportMarkedAtColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatMarkedAt(cellData.getValue()))
        );

        reportTable.setItems(reportItems);
    }

    private void applyRoleBasedAccess() {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            loggedInUserLabel.setText("Unknown User");
            loggedInRoleLabel.setText("NO SESSION");
            disableAdministrativeFeatures(true);
            usersTab.setDisable(true);
            return;
        }

        loggedInUserLabel.setText(currentUser.getFullName());
        loggedInRoleLabel.setText(currentUser.getRole().name());

        boolean lecturerUser = currentUser.getRole() == UserRole.LECTURER;

        disableAdministrativeFeatures(lecturerUser);
        usersTab.setDisable(lecturerUser);
    }

    private void disableAdministrativeFeatures(boolean disabled) {
        studentNumberField.setDisable(disabled);
        studentFirstNameField.setDisable(disabled);
        studentLastNameField.setDisable(disabled);
        studentEmailField.setDisable(disabled);
        studentProgrammeField.setDisable(disabled);
        studentGenderComboBox.setDisable(disabled);
        studentSearchField.setDisable(disabled);
        studentSaveButton.setDisable(disabled);
        studentTable.setDisable(disabled);

        courseCodeField.setDisable(disabled);
        courseTitleField.setDisable(disabled);
        courseLecturerField.setDisable(disabled);
        courseSemesterField.setDisable(disabled);
        courseSaveButton.setDisable(disabled);
        courseTable.setDisable(disabled);
    }

    private boolean isAdminUser() {
        User currentUser = SessionManager.getCurrentUser();
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    private boolean isLecturerUser() {
        User currentUser = SessionManager.getCurrentUser();
        return currentUser != null && currentUser.getRole() == UserRole.LECTURER;
    }

    private boolean requireAdminAccess() {
        if (!isAdminUser()) {
            showError("This action is restricted to administrators only.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleSaveStudent() {
        if (!requireAdminAccess()) {
            return;
        }

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
        if (!requireAdminAccess()) {
            return;
        }

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
        if (!requireAdminAccess()) {
            return;
        }

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
        if (!requireAdminAccess()) {
            return;
        }

        try {
            Course course = Optional.ofNullable(courseTable.getSelectionModel().getSelectedItem())
                    .orElseGet(Course::new);

            String lecturerUsername = InputValidator.requireText(
                    courseLecturerField.getText(),
                    "Lecturer username"
            );

            User lecturer = userDao.findByUsername(lecturerUsername)
                    .orElseThrow(() -> new ValidationException(
                            "No lecturer account was found with username: " + lecturerUsername
                    ));

            if (lecturer.getRole() != UserRole.LECTURER) {
                throw new ValidationException("The selected user must have the LECTURER role.");
            }

            course.setCourseCode(InputValidator.requireCourseCode(courseCodeField.getText()));
            course.setCourseTitle(InputValidator.requireText(courseTitleField.getText(), "Course title"));
            course.setLecturerName(lecturer.getFullName());
            course.setLecturerUsername(lecturer.getUsername());
            course.setLecturerUserId(lecturer.getId());
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
        if (!requireAdminAccess()) {
            return;
        }

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
        if (!requireAdminAccess()) {
            return;
        }

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

        if (course.getLecturerUsername() == null || course.getLecturerUsername().isBlank()) {
            courseLecturerField.setText(course.getLecturerName());
        } else {
            courseLecturerField.setText(course.getLecturerUsername());
        }

        courseSemesterField.setText(course.getSemester());
        courseSaveButton.setText("Update Course");
    }

    @FXML
    private void handleCreateUser() {
        if (!requireAdminAccess()) {
            return;
        }

        try {
            String fullName = InputValidator.requireText(userFullNameField.getText(), "Full name");
            String username = InputValidator.requireText(userUsernameField.getText(), "Username");
            String password = userPasswordField.getText();
            UserRole role = userRoleComboBox.getValue();

            if (password == null || password.isBlank()) {
                throw new ValidationException("Temporary password is required.");
            }

            if (password.length() < MIN_PASSWORD_LENGTH) {
                throw new ValidationException("Password must be at least 8 characters.");
            }

            if (role == null) {
                throw new ValidationException("Select a user role.");
            }

            userDao.createUser(fullName, username, password, role);

            clearUserForm();
            loadUsers();

            showInformation("User account created successfully.");

        } catch (ValidationException | DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleClearUserForm() {
        if (!requireAdminAccess()) {
            return;
        }

        clearUserForm();
    }

    @FXML
    private void handleDeactivateUser() {
        if (!requireAdminAccess()) {
            return;
        }

        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError("Select a user before choosing Deactivate.");
            return;
        }

        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null && currentUser.getId() == selectedUser.getId()) {
            showError("You cannot deactivate the account you are currently using.");
            return;
        }

        if (!selectedUser.isActive()) {
            showInformation("This user is already inactive.");
            return;
        }

        userDao.updateActiveStatus(selectedUser.getId(), false);
        loadUsers();

        showInformation("User account deactivated successfully.");
    }

    @FXML
    private void handleActivateUser() {
        if (!requireAdminAccess()) {
            return;
        }

        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showError("Select a user before choosing Activate.");
            return;
        }

        if (selectedUser.isActive()) {
            showInformation("This user is already active.");
            return;
        }

        userDao.updateActiveStatus(selectedUser.getId(), true);
        loadUsers();

        showInformation("User account activated successfully.");
    }

    private void clearUserForm() {
        userTable.getSelectionModel().clearSelection();
        userFullNameField.clear();
        userUsernameField.clear();
        userPasswordField.clear();
        userRoleComboBox.setValue(null);
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

        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            showError("Your session has expired. Please log in again.");
            return;
        }

        try {
            attendanceService.saveAttendance(attendanceItems, currentUser.getId());
            loadDashboard();

            showInformation("Attendance saved for " + attendanceItems.size()
                    + " student(s) by " + currentUser.getFullName() + ".");

        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void handleRunReport() {
        try {
            Course selectedCourse = reportCourseComboBox.getValue();

            if (isLecturerUser() && selectedCourse == null) {
                showError("Lecturers must select one of their assigned courses before running a report.");
                return;
            }

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
                writer.write("Course Code,Student Number,Student Name,Attendance Date,Status,Marked By,Marked At");
                writer.newLine();

                for (AttendanceRecord record : reportItems) {
                    writer.write(csv(record.getCourseCode()) + ","
                            + csv(record.getStudentNumber()) + ","
                            + csv(record.getStudentName()) + ","
                            + csv(record.getAttendanceDate().toString()) + ","
                            + csv(record.getStatus().getDisplayName()) + ","
                            + csv(record.getMarkedByUsername()) + ","
                            + csv(formatMarkedAt(record)));
                    writer.newLine();
                }

                showInformation("CSV report saved successfully.");

            } catch (IOException exception) {
                showError("Could not export the report: " + exception.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        refreshAllData();
    }

    @FXML
    private void handleRefreshDashboard() {
        refreshAllData();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ucc/attendance/fxml/login-view.fxml")
            );

            Pane root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            LoginController controller = loader.getController();
            controller.setPrimaryStage(stage);

            Scene scene = new Scene(
                    root,
                    stage.getScene().getWidth(),
                    stage.getScene().getHeight()
            );

            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/com/ucc/attendance/css/app.css")
            ).toExternalForm());

            stage.setTitle("Student Attendance Monitoring System - Login");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException exception) {
            showError("Could not log out. Please restart the application.");
        }
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

        if (isAdminUser()) {
            loadUsers();
        } else {
            userItems.clear();
        }

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

            List<Course> courses;
            User currentUser = SessionManager.getCurrentUser();

            if (currentUser != null && isLecturerUser()) {
                courses = courseDao.findByLecturerUserId(currentUser.getId());
            } else {
                courses = courseDao.findAll();
            }

            courseItems.setAll(courses);
            attendanceCourseComboBox.setItems(FXCollections.observableArrayList(courses));
            reportCourseComboBox.setItems(FXCollections.observableArrayList(courses));

            selectCourseById(attendanceCourseComboBox, attendanceCourseId);
            selectCourseById(reportCourseComboBox, reportCourseId);

        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private void loadUsers() {
        try {
            userItems.setAll(userDao.findAll());
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private int getSelectedCourseId(ComboBox<Course> comboBox) {
        return comboBox.getValue() == null ? -1 : comboBox.getValue().getId();
    }

    private void selectCourseById(ComboBox<Course> comboBox, int courseId) {
        comboBox.getItems()
                .stream()
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
        String safeValue = value == null ? "" : value;
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }

    private String formatMarkedAt(AttendanceRecord record) {
        if (record.getMarkedAt() == null) {
            return "Not recorded";
        }

        return record.getMarkedAt().format(REPORT_DATE_TIME_FORMATTER);
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