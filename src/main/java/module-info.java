module ni.edu.uam.fact_appp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens ni.edu.uam.fact_appp.application to javafx.graphics;
    opens ni.edu.uam.fact_appp.controller to javafx.fxml;
    opens ni.edu.uam.fact_appp.model to javafx.base;

    exports ni.edu.uam.fact_appp.application;
    exports ni.edu.uam.fact_appp.model;
    exports ni.edu.uam.fact_appp.dao;
}