package ni.edu.uam.fact_appp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();

        if (ADMIN_USER.equals(usuario) && ADMIN_PASS.equals(password)) {
            lblError.setText("");
            abrirMenuPrincipal();
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
        }
    }

    private void abrirMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/ni/edu/uam/fact_appp/fxml/menu-principal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Sistema de facturación");
            stage.setScene(new Scene(root, 900, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir el menú principal.").showAndWait();
        }
    }
}
