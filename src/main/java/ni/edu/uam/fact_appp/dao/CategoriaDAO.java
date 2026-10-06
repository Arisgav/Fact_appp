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
 */
public class CategoriaDAO {

    /** Devuelve todas las categorías registradas. */
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

    /** Inserta una nueva categoría y devuelve el id generado. */
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

    /** Actualiza una categoría existente. */
    public void actualizar(Categoria categoria) throws SQLException {
        String sql = """
                UPDATE categoria
                SET nombre = ?, activa = ?
                WHERE id = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, categoria.isActiva());
            ps.setInt(3, categoria.getId());
            ps.executeUpdate();
        }
    }

    /** Elimina una categoría por su id. */
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Comprueba si existe una categoría con ese nombre. */
    public boolean existePorNombre(String nombre) throws SQLException {
        return existePorNombre(nombre, null);
    }

    /**
     * Comprueba si existe OTRA categoría con ese nombre.
     * idExcluido se utiliza al actualizar para no contar la categoría actual.
     */
    public boolean existePorNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql;

        if (idExcluido == null) {
            sql = "SELECT 1 FROM categoria WHERE LOWER(nombre) = LOWER(?)";
        } else {
            sql = "SELECT 1 FROM categoria WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        }

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            if (idExcluido != null) {
                ps.setInt(2, idExcluido);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Comprueba si una categoría tiene productos asociados.
     * Se utiliza antes de eliminar una categoría.
     */
    public boolean tieneProductos(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE categoria_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoriaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}
