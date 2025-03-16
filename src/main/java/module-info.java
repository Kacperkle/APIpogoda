module org.example.apipogoda1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.apipogoda1 to javafx.fxml;
    exports org.example.apipogoda1;
}