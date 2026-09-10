module ni.edu.uam.fact_appp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens ni.edu.uam.fact_appp.application to javafx.graphics;
    opens ni.edu.uam.fact_appp.controller to javafx.fxml;
    opens ni.edu.uam.fact_appp.model to javafx.base;

    exports ni.edu.uam.fact_appp.application;
}