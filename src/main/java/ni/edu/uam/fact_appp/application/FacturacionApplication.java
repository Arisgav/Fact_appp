package ni.edu.uam.fact_appp.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FacturacionApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/ni/edu/uam/fact_appp/fxml/login.fxml"));
        stage.setTitle("Iniciar sesión");
        stage.setResizable(false);
        stage.setScene(new Scene(loader.load(), 380, 420));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
