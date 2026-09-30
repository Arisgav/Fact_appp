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

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    /** Lista maestra: siempre contiene TODAS las categorías cargadas de la BD. */
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    /** Envuelve a "categorias"; su predicado decide qué filas se muestran. */
    private FilteredList<Categoria> categoriasFiltradas;

    @FXML
    private void initialize() {
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivaCategoria.setCellValueFactory(new PropertyValueFactory<>("activa"));

        chkActivaCategoria.setSelected(true);

        // ObservableList -> FilteredList -> (SortedList) -> TableView
        categoriasFiltradas = new FilteredList<>(categorias, c -> true);
        SortedList<Categoria> categoriasOrdenadas = new SortedList<>(categoriasFiltradas);
        categoriasOrdenadas.comparatorProperty().bind(tblCategorias.comparatorProperty());
        tblCategorias.setItems(categoriasOrdenadas);

        // Búsqueda por nombre: se reevalúa en cada tecla
        txtBuscarCategoria.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());

        cargarCategorias();
    }

    private void cargarCategorias() {
        try {
            ObservableList<Categoria> lista = categoriaDAO.listar();
            categorias.setAll(lista); // conserva el FilteredList ya conectado a la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible cargar las categorías desde la base de datos.\n" + e.getMessage());
        }
    }

    private void aplicarFiltro() {
        String texto = txtBuscarCategoria.getText() == null
                ? "" : txtBuscarCategoria.getText().trim().toLowerCase(Locale.ROOT);

        categoriasFiltradas.setPredicate(categoria -> {
            if (texto.isEmpty()) {
                return true;
            }
            return categoria.getNombre() != null
                    && categoria.getNombre().toLowerCase(Locale.ROOT).contains(texto);
        });
    }

    @FXML
    private void guardarCategoria() {
        String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isBlank()) {
            mensaje(Alert.AlertType.WARNING, "El nombre de la categoría es obligatorio.");
            return;
        }

        try {
            if (categoriaDAO.existePorNombre(nombre)) {
                mensaje(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.");
                return;
            }

            Categoria nueva = new Categoria(null, nombre, chkActivaCategoria.isSelected());
            categoriaDAO.insertar(nueva);

            mensaje(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
            limpiar();
            cargarCategorias();
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible guardar la categoría.\n" + e.getMessage());
        }
    }

    @FXML
    private void quitarCategoria() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensaje(Alert.AlertType.WARNING, "Selecciona una categoría de la lista para quitar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas quitar la categoría \"" + seleccionada.getNombre() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                categoriaDAO.eliminar(seleccionada.getId());
                limpiar();
                cargarCategorias();
            } catch (SQLException e) {
                e.printStackTrace();
                mensaje(Alert.AlertType.ERROR,
                        "No fue posible eliminar la categoría (verifica que no tenga productos asociados).\n"
                                + e.getMessage());
            }
        }
    }

    @FXML
    private void cerrarCategoria() {
        ((Stage) txtNombreCategoria.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtNombreCategoria.clear();
        chkActivaCategoria.setSelected(true);
        tblCategorias.getSelectionModel().clearSelection();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
