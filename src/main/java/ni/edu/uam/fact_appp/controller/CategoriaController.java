package ni.edu.uam.fact_appp.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ni.edu.uam.fact_appp.dao.CategoriaDAO;
import ni.edu.uam.fact_appp.model.Categoria;

import java.sql.SQLException;

public class CategoriaController {

    @FXML private TextField txtNombreCategoria;
    @FXML private CheckBox chkActivaCategoria;
    @FXML private TableView<Categoria> tblCategorias;

    @FXML private TableColumn<Categoria, String> colNombreCategoria;
    @FXML private TableColumn<Categoria, Boolean> colActivaCategoria;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML
    private void initialize() {
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivaCategoria.setCellValueFactory(new PropertyValueFactory<>("activa"));

        chkActivaCategoria.setSelected(true);
        cargarCategorias();
    }

    private void cargarCategorias() {
        try {
            ObservableList<Categoria> lista = categoriaDAO.listar();
            tblCategorias.setItems(lista);
        } catch (SQLException e) {
            e.printStackTrace();
            mensaje(Alert.AlertType.ERROR,
                    "No fue posible cargar las categorías desde la base de datos.\n" + e.getMessage());
        }
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
