package ni.edu.uam.fact_appp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {


    private Integer id;
    private String nombre;
    private BigDecimal precioVenta;
    private Categoria categoria;
    private int exitencia;
    private String rutaImagen;
    private boolean activo;

}
