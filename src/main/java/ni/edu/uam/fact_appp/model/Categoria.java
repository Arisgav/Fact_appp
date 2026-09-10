package ni.edu.uam.fact_appp.model;

import lombok.*;

public class Categoria {


    public Categoria(int i, String limpieza, boolean b) {
    }

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