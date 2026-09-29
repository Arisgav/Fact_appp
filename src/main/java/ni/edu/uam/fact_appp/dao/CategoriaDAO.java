package ni.edu.uam.fact_appp.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.uam.fact_appp.model.Categoria;
import ni.edu.uam.fact_appp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO (Data Access Object) para la tabla "categoria".
 * Encapsula todo el SQL relacionado a Categoria usando Connection + PreparedStatement.
 */
public class CategoriaDAO {

    /**
     * Devuelve todas las categorías registradas en la base de datos.
     */
    public ObservableList<Categoria> listar() throws SQLException {
        ObservableList<Categoria> lista = FXCollections.observableArrayList();
        String sql = "SELECT id, nombre, activa FROM categoria ORDER BY nombre";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getBoolean("activa")
                ));
            }
        }
        return lista;
    }

    /**
     * Inserta una nueva categoría y devuelve el id generado.
     */
    public int insertar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?) RETURNING id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, categoria.isActiva());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    categoria.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se pudo obtener el id generado para la categoría.");
    }

    /**
     * Elimina una categoría por su id.
     */
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Indica si ya existe una categoría con ese nombre (sin distinguir mayúsculas).
     */
    public boolean existePorNombre(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM categoria WHERE LOWER(nombre) = LOWER(?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
