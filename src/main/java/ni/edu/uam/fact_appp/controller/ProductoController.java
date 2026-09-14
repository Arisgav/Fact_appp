package ni.edu.uam.fact_appp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ni.edu.uam.fact_appp.model.Categoria;
import ni.edu.uam.fact_appp.model.Producto;

import java.io.File;
import java.math.BigDecimal;

public class ProductoController {

    // ---- Misma paleta clara usada en el menú principal ----
    private static final String COLOR_FONDO = "#ffffff";
    private static final String COLOR_BORDE = "#e2e5ea";
    private static final String COLOR_ACENTO = "#2b6777";

    @FXML private BorderPane rootPane;
    @FXML private GridPane formPane;
    @FXML private VBox previewBox;
    @FXML private VBox bottomBox;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;
    @FXML private TableView<Producto> tblProductos;

    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    @FXML private Button btnImagen;
    @FXML private Button btnLimpiar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCerrar;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private String rutaImagen;

    @FXML
    private void initialize() {
        cmbCategoria.setItems(Categoria.LISTA);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Las columnas se reparten todo el ancho de la tabla, sin espacio muerto
        tblProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblProductos.setItems(productos);

        chkActivo.setSelected(true);

        aplicarTema();
    }

    private void aplicarTema() {
        formPane.setStyle("-fx-background-color: " + COLOR_FONDO + ";");
        bottomBox.setStyle(
                "-fx-background-color: " + COLOR_FONDO + ";"
                        + "-fx-border-color: " + COLOR_BORDE + ";"
                        + "-fx-border-width: 1 0 0 0;"
        );

        // Marco visible para la vista previa, aunque todavía no haya imagen
        previewBox.setStyle(
                "-fx-background-color: #f7f8fa;"
                        + "-fx-border-color: " + COLOR_BORDE + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
        );

        btnGuardar.setStyle(
                "-fx-background-color: " + COLOR_ACENTO + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 6;"
                        + "-fx-padding: 6 16 6 16;"
                        + "-fx-cursor: hand;"
        );

        String estiloSecundario =
                "-fx-background-color: transparent;"
                        + "-fx-border-color: " + COLOR_BORDE + ";"
                        + "-fx-border-radius: 6;"
                        + "-fx-background-radius: 6;"
                        + "-fx-padding: 6 16 6 16;"
                        + "-fx-cursor: hand;";
        btnLimpiar.setStyle(estiloSecundario);
        btnCerrar.setStyle(estiloSecundario);
        btnImagen.setStyle(estiloSecundario);
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());
            if (precio.signum() <= 0 || existencia < 0) {
                mensaje(Alert.AlertType.WARNING,
                        "Precio mayor que cero y existencia no negativa.");
                return;
            }
            productos.add(new Producto(null, txtCodigo.getText().trim(),
                    txtNombre.getText().trim(), precio, cmbCategoria.getValue(),
                    existencia, rutaImagen, chkActivo.isSelected()));
            mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            limpiarFormulario();
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
        }
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    @FXML
    private void eliminarSeleccionado() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Selecciona un producto de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el producto \"" + seleccionado.getNombre() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            productos.remove(seleccionado);
        }
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
