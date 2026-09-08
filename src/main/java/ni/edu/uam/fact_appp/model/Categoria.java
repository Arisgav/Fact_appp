package ni.edu.uam.fact_appp.model;

import lombok.*;

public class Categoria {


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaDTO {
        private Integer id;
        private String nombre;
        private boolean activa;



        @Override
        public String  toString() {
            return nombre;
        }

    }
}
