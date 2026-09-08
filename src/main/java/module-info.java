module ni.edu.uam.fact_appp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.uam.fact_appp to javafx.fxml;
    exports ni.edu.uam.fact_appp;
}