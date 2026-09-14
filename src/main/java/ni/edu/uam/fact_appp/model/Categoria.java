package ni.edu.uam.fact_appp.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    public static final ObservableList<Categoria> LISTA = FXCollections.observableArrayList(
            new Categoria(1, "Alimentos", true),
            new Categoria(2, "Bebidas", true),
            new Categoria(3, "Limpieza", true));

    private Integer id;
    private String nombre;
    private boolean activa;

    @Override
    public String toString() {
        return nombre;
    }
}
