module com.ucc.attendance {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;

    opens com.ucc.attendance to javafx.fxml;
    opens com.ucc.attendance.controller to javafx.fxml;

    exports com.ucc.attendance;
    exports com.ucc.attendance.model;
}
