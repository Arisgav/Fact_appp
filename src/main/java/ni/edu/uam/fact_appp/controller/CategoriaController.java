package ni.edu.uam.fact_appp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ni.edu.uam.fact_appp.model.Categoria;

public class CategoriaController {

    @FXML private TextField txtNombreCategoria;
    @FXML private CheckBox chkActivaCategoria;
    @FXML private TableView<Categoria> tblCategorias;

    @FXML private TableColumn<Categoria, String> colNombreCategoria;
    @FXML private TableColumn<Categoria, Boolean> colActivaCategoria;

    @FXML
    private void initialize() {
        colNombreCategoria.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivaCategoria.setCellValueFactory(new PropertyValueFactory<>("activa"));

        tblCategorias.setItems(Categoria.LISTA);
        chkActivaCategoria.setSelected(true);
    }

    @FXML
    private void guardarCategoria() {
        String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isBlank()) {
            mensaje(Alert.AlertType.WARNING, "El nombre de la categoría es obligatorio.");
            return;
        }

        boolean yaExiste = Categoria.LISTA.stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));
        if (yaExiste) {
            mensaje(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.");
            return;
        }

        int nuevoId = Categoria.LISTA.stream()
                .mapToInt(Categoria::getId)
                .max()
                .orElse(0) + 1;

        Categoria.LISTA.add(new Categoria(nuevoId, nombre, chkActivaCategoria.isSelected()));

        mensaje(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
        limpiar();
    }

    @FXML
    private void cerrarCategoria() {
        ((Stage) txtNombreCategoria.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtNombreCategoria.clear();
        chkActivaCategoria.setSelected(true);
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}
