package ni.edu.uam.fact_appp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import ni.edu.uam.fact_appp.util.SceneManager;

import java.io.IOException;

public class MenuPrincipalController {

    // ---- Paleta del tema (claro, tono azul-verde tranquilo) ----
    private static final String COLOR_FONDO_BARRA = "#ffffff";
    private static final String COLOR_BORDE = "#e2e5ea";
    private static final String COLOR_HOVER = "#eef1f6";
    private static final String COLOR_PRESIONADO = "#dde3ec";
    private static final String COLOR_TEXTO = "#2f3640";
    private static final String COLOR_ACENTO = "#2b6777";
    private static final String COLOR_FONDO_CENTRO_INICIO = "#f3f6f9";
    private static final String COLOR_FONDO_CENTRO_FIN = "#ffffff";

    @FXML private BorderPane rootPane;
    @FXML private MenuBar menuBar;
    @FXML private ToolBar toolBar;
    @FXML private StackPane centerStack;
    @FXML private Label lblTitulo;

    @FXML private Button btnProductos;
    @FXML private Button btnCategorias;
    @FXML private Button btnSalir;

    @FXML
    private void initialize() {
        aplicarTema();
    }

    private void aplicarTema() {
        menuBar.setStyle(
                "-fx-background-color: " + COLOR_FONDO_BARRA + ";"
                        + "-fx-border-color: " + COLOR_BORDE + ";"
                        + "-fx-border-width: 0 0 1 0;"
        );

        toolBar.setStyle(
                "-fx-background-color: " + COLOR_FONDO_BARRA + ";"
                        + "-fx-border-color: " + COLOR_BORDE + ";"
                        + "-fx-border-width: 0 0 1 0;"
                        + "-fx-padding: 8 14 8 14;"
        );

        centerStack.setStyle(
                "-fx-background-color: linear-gradient(to bottom, "
                        + COLOR_FONDO_CENTRO_INICIO + ", " + COLOR_FONDO_CENTRO_FIN + ");"
        );

        lblTitulo.setStyle(
                "-fx-text-fill: " + COLOR_ACENTO + ";"
                        + "-fx-font-weight: bold;"
        );

        estilizarBoton(btnProductos);
        estilizarBoton(btnCategorias);
        estilizarBoton(btnSalir);
    }

    /**
     * Aplica el mismo look & feel (fondo transparente, hover y presionado)
     * a un botón del ToolBar, sin depender de un archivo CSS externo.
     */
    private void estilizarBoton(Button boton) {
        String base = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-padding: 6 10 6 10; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + COLOR_HOVER + "; -fx-background-radius: 8; -fx-padding: 6 10 6 10; -fx-cursor: hand;";
        String presionado = "-fx-background-color: " + COLOR_PRESIONADO + "; -fx-background-radius: 8; -fx-padding: 6 10 6 10; -fx-cursor: hand;";

        boton.setStyle(base);
        boton.setOnMouseEntered(e -> boton.setStyle(hover));
        boton.setOnMouseExited(e -> boton.setStyle(base));
        boton.setOnMousePressed(e -> boton.setStyle(presionado));
        boton.setOnMouseReleased(e -> boton.setStyle(hover));
    }

    @FXML
    private void abrirProductos() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/uam/fact_appp/fxml/producto-view.fxml",
                    "Gestión de productos");
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Productos.").showAndWait();
        }
    }

    @FXML
    private void abrirCategorias() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/uam/fact_appp/fxml/Categoria.fxml",
                    "Gestión de categorías");
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Categorías.").showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}