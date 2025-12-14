module com.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.expensetracker.controller to javafx.fxml;
    exports com.expensetracker;
}
