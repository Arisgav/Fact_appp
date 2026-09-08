package ni.edu.uam.fact_appp.model;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class empleado {
    private Integer id;
    private String nombres;
    private String apellidos;
    private Cargo cargo;
    private LocalDate fechaContratacion;
    private boolean activo;
}