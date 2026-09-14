package ni.edu.uam.fact_appp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.fact_appp.model.Cargo;

import java.net.URL;
import java.util.ResourceBundle;

public class CargoController implements Initializable {

    @FXML private TextField txtIdCargo;
    @FXML private TextField txtNombreCargo;
    @FXML private TextField txtDescripcionCargo;

    @FXML private TableView<Cargo> tablaCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    private final ObservableList<Cargo> listaCargos = FXCollections.observableArrayList();
    private int contadorId = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tablaCargos.setItems(listaCargos);

        tablaCargos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarEnFormulario(nuevo);
            }
        });
    }

    @FXML
    private void guardarCargo() {
        String nombre = txtNombreCargo.getText();
        String descripcion = txtDescripcionCargo.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre del cargo es obligatorio.");
            return;
        }

        Cargo cargo = new Cargo(contadorId, nombre.trim(), descripcion == null ? "" : descripcion.trim());
        listaCargos.add(cargo);
        contadorId++;

        limpiarCampos();
    }

    @FXML
    private void modificarCargo() {
        Cargo seleccionado = tablaCargos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Validación", "Selecciona un cargo de la lista para modificar.");
            return;
        }

        String nombre = txtNombreCargo.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Validación", "El nombre del cargo es obligatorio.");
            return;
        }

        seleccionado.setNombre(nombre.trim());
        seleccionado.setDescripcion(txtDescripcionCargo.getText() == null ? "" : txtDescripcionCargo.getText().trim());
        tablaCargos.refresh();

        limpiarCampos();
    }

    @FXML
    private void eliminarCargo() {
        Cargo seleccionado = tablaCargos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Validación", "Selecciona un cargo de la lista para eliminar.");
            return;
        }

        listaCargos.remove(seleccionado);
        limpiarCampos();
    }

    @FXML
    private void limpiarCampos() {
        txtIdCargo.clear();
        txtNombreCargo.clear();
        txtDescripcionCargo.clear();
        tablaCargos.getSelectionModel().clearSelection();
    }

    private void cargarEnFormulario(Cargo cargo) {
        txtIdCargo.setText(String.valueOf(cargo.getId()));
        txtNombreCargo.setText(cargo.getNombre());
        txtDescripcionCargo.setText(cargo.getDescripcion());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
