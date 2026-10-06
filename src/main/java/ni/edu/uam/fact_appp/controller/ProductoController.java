package ni.edu.uam.fact_appp.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import ni.edu.uam.fact_appp.dao.CategoriaDAO;
import ni.edu.uam.fact_appp.dao.ProductoDAO;
import ni.edu.uam.fact_appp.model.Categoria;
import ni.edu.uam.fact_appp.model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Locale;

public class ProductoController {

    // ---- Misma paleta clara usada en el menú principal ----
    private static final String COLOR_FONDO = "#ffffff";
    private static final String COLOR_BORDE = "#e2e5ea";
    private static final String COLOR_ACENTO = "#2b6777";

    private static final String FILTRO_TODOS = "Todos";
    private static final String FILTRO_ACTIVOS = "Activos";
    private static final String FILTRO_INACTIVOS = "Inactivos";
    private static final String CATEGORIA_TODAS = "Todas";

    @FXML private BorderPane rootPane;
    @FXML private GridPane formPane;
    @FXML private VBox bottomBox;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<String> cmbFiltroCategoria;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnActualizar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCerrar;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    /** Lista maestra: siempre contiene TODOS los productos cargados de la BD. */
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    /** Envuelve a "productos"; su predicado decide qué filas se muestran. */
    private FilteredList<Producto> productosFiltrados;

    /** Producto actualmente cargado en el formulario para edición (null = modo "nuevo"). */
    private Producto productoSeleccionado;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tblProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        chkActivo.setSelected(true);

        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
                FILTRO_TODOS, FILTRO_ACTIVOS, FILTRO_INACTIVOS));
        cmbFiltroEstado.getSelectionModel().select(FILTRO_TODOS);

        // ObservableList -> FilteredList -> (SortedList) -> TableView
        productosFiltrados = new FilteredList<>(productos, p -> true);
        SortedList<Producto> productosOrdenados = new SortedList<>(productosFiltrados);
        productosOrdenados.comparatorProperty().bind(tblProductos.comparatorProperty());
        tblProductos.setItems(productosOrdenados);

        // Búsqueda: se reevalúa el filtro en cada tecla
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());

        // Filtros combinados con la búsqueda
        ChangeListener<String> filtroListener = (obs, oldVal, newVal) -> aplicarFiltro();
        cmbFiltroEstado.valueProperty().addListener(filtroListener);
        cmbFiltroCategoria.valueProperty().addListener(filtroListener);

        // Al seleccionar una fila, cargar sus datos en el formulario (para UPDATE/DELETE)
        tblProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> cargarEnFormulario(actual));

        cargarCategorias();
        cargarProductos();
        aplicarTema();
        actualizarEstadoBotones();
    }

    // ---------------------------------------------------------------
    // Carga de datos
    // ---------------------------------------------------------------

    private void cargarCategorias() {
        try {
            ObservableList<Categoria> categorias = categoriaDAO.listar();
            cmbCategoria.setItems(categorias);

            ObservableList<String> nombresCategorias = FXCollections.observableArrayList();
            nombresCategorias.add(CATEGORIA_TODAS);
            for (Categoria c : categorias) {
                nombresCategorias.add(c.getNombre());
            }
            cmbFiltroCategoria.setItems(nombresCategorias);
            cmbFiltroCategoria.getSelectionModel().select(CATEGORIA_TODAS);
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible cargar las categorías desde la base de datos.\n" + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            ObservableList<Producto> lista = productoDAO.listar();
            productos.setAll(lista); // conserva el FilteredList ya conectado a la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible cargar los productos desde la base de datos.\n" + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Búsqueda + filtrado (FilteredList)
    // ---------------------------------------------------------------

    private void aplicarFiltro() {
        String texto = txtBuscar.getText() == null
                ? "" : txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        String estado = cmbFiltroEstado.getValue();
        String categoriaFiltro = cmbFiltroCategoria.getValue();

        productosFiltrados.setPredicate(producto -> {
            // 1) Filtro de búsqueda: código, nombre o categoría (sin distinguir mayúsculas)
            if (!texto.isEmpty()) {
                boolean coincideCodigo = producto.getCodigo() != null
                        && producto.getCodigo().toLowerCase(Locale.ROOT).contains(texto);
                boolean coincideNombre = producto.getNombre() != null
                        && producto.getNombre().toLowerCase(Locale.ROOT).contains(texto);
                boolean coincideCategoria = producto.getCategoria() != null
                        && producto.getCategoria().getNombre() != null
                        && producto.getCategoria().getNombre().toLowerCase(Locale.ROOT).contains(texto);

                if (!coincideCodigo && !coincideNombre && !coincideCategoria) {
                    return false;
                }
            }

            // 2) Filtro de estado (Todos / Activos / Inactivos)
            if (FILTRO_ACTIVOS.equals(estado) && !producto.isActivo()) {
                return false;
            }
            if (FILTRO_INACTIVOS.equals(estado) && producto.isActivo()) {
                return false;
            }

            // 3) Filtro de categoría
            if (categoriaFiltro != null && !CATEGORIA_TODAS.equals(categoriaFiltro)) {
                if (producto.getCategoria() == null
                        || !categoriaFiltro.equals(producto.getCategoria().getNombre())) {
                    return false;
                }
            }

            return true;
        });
    }

    // ---------------------------------------------------------------
    // Selección de fila -> formulario (para UPDATE/DELETE)
    // ---------------------------------------------------------------

    private void cargarEnFormulario(Producto producto) {
        productoSeleccionado = producto;

        if (producto == null) {
            actualizarEstadoBotones();
            return;
        }

        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(producto.getPrecioVenta() != null ? producto.getPrecioVenta().toPlainString() : "");
        txtExistencia.setText(String.valueOf(producto.getExistencia()));
        cmbCategoria.getSelectionModel().select(producto.getCategoria());
        chkActivo.setSelected(producto.isActivo());

        actualizarEstadoBotones();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = productoSeleccionado != null;
        btnActualizar.setDisable(!haySeleccion);
        btnGuardar.setDisable(haySeleccion);
        btnEliminar.setDisable(!haySeleccion);
    }

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------

    @FXML
    private void guardar() {
        if (!validarFormulario()) {
            return;
        }
        try {
            String codigo = txtCodigo.getText().trim();
            if (productoDAO.existePorCodigo(codigo, null)) {
                mensaje(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
                return;
            }

            Producto nuevo = new Producto(
                    null,
                    codigo,
                    txtNombre.getText().trim(),
                    cmbCategoria.getValue(),
                    new BigDecimal(txtPrecio.getText().trim()),
                    Integer.parseInt(txtExistencia.getText().trim()),
                    chkActivo.isSelected()
            );

            productoDAO.insertar(nuevo);

            mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            limpiarSeleccion();
            cargarProductos();
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR, "No fue posible guardar el producto.");
        }
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------

    @FXML
    private void actualizar() {
        if (productoSeleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Selecciona un producto de la tabla para actualizar.");
            return;
        }
        if (!validarFormulario()) {
            return;
        }
        try {
            String codigo = txtCodigo.getText().trim();
            if (productoDAO.existePorCodigo(codigo, productoSeleccionado.getId())) {
                mensaje(Alert.AlertType.WARNING, "Ya existe OTRO producto con ese código.");
                return;
            }

            // Modifica el MISMO objeto seleccionado; no crea uno nuevo.
            productoSeleccionado.setCodigo(codigo);
            productoSeleccionado.setNombre(txtNombre.getText().trim());
            productoSeleccionado.setCategoria(cmbCategoria.getValue());
            productoSeleccionado.setPrecioVenta(new BigDecimal(txtPrecio.getText().trim()));
            productoSeleccionado.setExistencia(Integer.parseInt(txtExistencia.getText().trim()));
            productoSeleccionado.setActivo(chkActivo.isSelected());

            productoDAO.actualizar(productoSeleccionado);

            mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
            limpiarSeleccion();
            cargarProductos();
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR, "No fue posible actualizar el producto.");
        }
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    @FXML
    private void eliminarSeleccionado() {
        if (productoSeleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Selecciona un producto de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el producto \"" + productoSeleccionado.getNombre() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                productoDAO.eliminar(productoSeleccionado.getId());
                limpiarSeleccion();
                cargarProductos();
            } catch (SQLException e) {
                e.printStackTrace();
                mensaje(Alert.AlertType.ERROR, "No fue posible eliminar el producto.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Utilidades de formulario
    // ---------------------------------------------------------------

    @FXML
    private void limpiarSeleccion() {
        tblProductos.getSelectionModel().clearSelection();
        productoSeleccionado = null;

        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);

        actualizarEstadoBotones();
    }

    private boolean validarFormulario() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete todos los campos obligatorios.");
            return false;
        }
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());
            if (precio.signum() <= 0) {
                mensaje(Alert.AlertType.WARNING, "El precio debe ser mayor que cero.");
                return false;
            }
            if (existencia < 0) {
                mensaje(Alert.AlertType.WARNING, "La existencia no puede ser negativa.");
                return false;
            }
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no tienen un formato numérico válido.");
            return false;
        }
        return true;
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }

    // ---------------------------------------------------------------
    // Tema visual
    // ---------------------------------------------------------------

    private void aplicarTema() {
        formPane.setStyle("-fx-background-color: " + COLOR_FONDO + ";");
        bottomBox.setStyle(
                "-fx-background-color: " + COLOR_FONDO + ";"
                        + "-fx-border-color: " + COLOR_BORDE + ";"
                        + "-fx-border-width: 1 0 0 0;"
        );

        btnGuardar.setStyle(
                "-fx-background-color: " + COLOR_ACENTO + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 6;"
                        + "-fx-padding: 6 16 6 16;"
                        + "-fx-cursor: hand;"
        );
        btnActualizar.setStyle(
                "-fx-background-color: #d98e04;"
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
        btnEliminar.setStyle(estiloSecundario);
    }
}
