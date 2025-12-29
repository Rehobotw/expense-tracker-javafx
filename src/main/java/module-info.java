module com.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.expensetracker.controller to javafx.fxml;
    opens com.expensetracker.model to javafx.base;

    exports com.expensetracker;
}
