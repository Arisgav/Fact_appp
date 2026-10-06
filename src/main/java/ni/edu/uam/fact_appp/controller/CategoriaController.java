package ni.edu.uam.fact_appp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ni.edu.uam.fact_appp.dao.CategoriaDAO;
import ni.edu.uam.fact_appp.model.Categoria;

import java.sql.SQLException;
import java.util.Locale;

public class CategoriaController {

    @FXML private TextField txtNombreCategoria;
    @FXML private CheckBox chkActivaCategoria;
    @FXML private TextField txtBuscarCategoria;
    @FXML private TableView<Categoria> tblCategorias;

    @FXML private TableColumn<Categoria, String> colNombreCategoria;
    @FXML private TableColumn<Categoria, Boolean> colActivaCategoria;

    @FXML private Button btnGuardarCategoria;
    @FXML private Button btnActualizarCategoria;
    @FXML private Button btnQuitarCategoria;
    @FXML private Button btnCerrarCategoria;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    /** Lista maestra: siempre contiene todas las categorías cargadas de la BD. */
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    /** Lista filtrada utilizada por el TableView. */
    private FilteredList<Categoria> categoriasFiltradas;

    /** Categoría seleccionada actualmente. null = modo nuevo. */
    private Categoria categoriaSeleccionada;

    @FXML
    private void initialize() {
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivaCategoria.setCellValueFactory(new PropertyValueFactory<>("activa"));

        chkActivaCategoria.setSelected(true);

        categoriasFiltradas = new FilteredList<>(categorias, c -> true);
        SortedList<Categoria> categoriasOrdenadas = new SortedList<>(categoriasFiltradas);
        categoriasOrdenadas.comparatorProperty().bind(tblCategorias.comparatorProperty());
        tblCategorias.setItems(categoriasOrdenadas);

        txtBuscarCategoria.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());

        tblCategorias.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> cargarEnFormulario(actual));

        cargarCategorias();
        actualizarEstadoBotones();
    }

    private void cargarCategorias() {
        try {
            ObservableList<Categoria> lista = categoriaDAO.listar();
            categorias.setAll(lista);
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR, "No fue posible cargar las categorías.");
        }
    }

    private void aplicarFiltro() {
        String texto = txtBuscarCategoria.getText() == null
                ? ""
                : txtBuscarCategoria.getText().trim().toLowerCase(Locale.ROOT);

        categoriasFiltradas.setPredicate(categoria -> {
            if (texto.isEmpty()) {
                return true;
            }

            return categoria.getNombre() != null
                    && categoria.getNombre().toLowerCase(Locale.ROOT).contains(texto);
        });
    }

    private void cargarEnFormulario(Categoria categoria) {
        categoriaSeleccionada = categoria;

        if (categoria == null) {
            txtNombreCategoria.clear();
            chkActivaCategoria.setSelected(true);
            actualizarEstadoBotones();
            return;
        }

        txtNombreCategoria.setText(categoria.getNombre());
        chkActivaCategoria.setSelected(categoria.isActiva());
        actualizarEstadoBotones();
    }

    private void actualizarEstadoBotones() {
        boolean seleccionada = categoriaSeleccionada != null;

        btnGuardarCategoria.setDisable(seleccionada);
        btnActualizarCategoria.setDisable(!seleccionada);
        btnQuitarCategoria.setDisable(!seleccionada);
    }

    /** INSERT */
    @FXML
    private void guardarCategoria() {
        String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isEmpty()) {
            mensaje(Alert.AlertType.WARNING,
                    "El nombre de la categoría es obligatorio.");
            txtNombreCategoria.requestFocus();
            return;
        }

        try {
            if (categoriaDAO.existePorNombre(nombre)) {
                mensaje(Alert.AlertType.WARNING,
                        "Ya existe una categoría con ese nombre.");
                txtNombreCategoria.requestFocus();
                return;
            }

            Categoria nueva = new Categoria(
                    null,
                    nombre,
                    chkActivaCategoria.isSelected()
            );

            categoriaDAO.insertar(nueva);

            mensaje(Alert.AlertType.INFORMATION,
                    "Categoría agregada correctamente.");

            limpiar();
            cargarCategorias();

        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible guardar la categoría.");
        }
    }

    /** UPDATE */
    @FXML
    private void actualizarCategoria() {
        if (categoriaSeleccionada == null) {
            mensaje(Alert.AlertType.WARNING,
                    "Debe seleccionar una categoría para actualizar.");
            return;
        }

        String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isEmpty()) {
            mensaje(Alert.AlertType.WARNING,
                    "El nombre de la categoría es obligatorio.");
            txtNombreCategoria.requestFocus();
            return;
        }

        try {
            if (categoriaDAO.existePorNombre(
                    nombre,
                    categoriaSeleccionada.getId())) {

                mensaje(Alert.AlertType.WARNING,
                        "Ya existe otra categoría con ese nombre.");
                txtNombreCategoria.requestFocus();
                return;
            }

            categoriaSeleccionada.setNombre(nombre);
            categoriaSeleccionada.setActiva(chkActivaCategoria.isSelected());

            categoriaDAO.actualizar(categoriaSeleccionada);

            mensaje(Alert.AlertType.INFORMATION,
                    "Categoría actualizada correctamente.");

            limpiar();
            cargarCategorias();

        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible actualizar la categoría.");
        }
    }

    /** DELETE */
    @FXML
    private void quitarCategoria() {
        if (categoriaSeleccionada == null) {
            mensaje(Alert.AlertType.WARNING,
                    "Selecciona una categoría de la lista para quitar.");
            return;
        }

        // Integridad referencial: no eliminar si tiene productos.
        try {
            if (categoriaDAO.tieneProductos(categoriaSeleccionada.getId())) {
                mensaje(Alert.AlertType.WARNING,
                        "No puede eliminar la categoría porque tiene productos asociados.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible comprobar los productos asociados.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Deseas quitar la categoría \""
                        + categoriaSeleccionada.getNombre()
                        + "\"?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                categoriaDAO.eliminar(categoriaSeleccionada.getId());

                mensaje(Alert.AlertType.INFORMATION,
                        "Categoría eliminada correctamente.");

                limpiar();
                cargarCategorias();

            } catch (SQLException e) {
                e.printStackTrace();
                mensaje(Alert.AlertType.ERROR,
                        "No fue posible eliminar la categoría.");
            }
        }
    }

    @FXML
    private void cerrarCategoria() {
        ((Stage) txtNombreCategoria.getScene().getWindow()).close();
    }

    private void limpiar() {
        tblCategorias.getSelectionModel().clearSelection();
        categoriaSeleccionada = null;
        txtNombreCategoria.clear();
        chkActivaCategoria.setSelected(true);
        actualizarEstadoBotones();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
