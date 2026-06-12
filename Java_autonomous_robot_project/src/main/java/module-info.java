module com.fiona_project {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.fiona_project to javafx.fxml;
    exports com.fiona_project;
}
