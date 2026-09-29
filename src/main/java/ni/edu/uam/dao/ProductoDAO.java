package ni.edu.uam.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.uam.fact_appp.model.Categoria;
import ni.edu.uam.fact_appp.model.Producto;
import ni.edu.uam.fact_appp.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO (Data Access Object) para la tabla "producto".
 * Relaciona cada Producto con su Categoria mediante un JOIN,
 * usando Connection + PreparedStatement.
 */
public class ProductoDAO {

    /**
     * Devuelve todos los productos, cada uno con su Categoria ya cargada (relación).
     */
    public ObservableList<Producto> listar() throws SQLException {
        ObservableList<Producto> lista = FXCollections.observableArrayList();

        String sql = "SELECT p.id, p.codigo, p.nombre, p.precio_venta, p.existencia, "
                + "p.ruta_imagen, p.activo, "
                + "c.id AS categoria_id, c.nombre AS categoria_nombre, c.activa AS categoria_activa "
                + "FROM producto p "
                + "JOIN categoria c ON c.id = p.categoria_id "
                + "ORDER BY p.nombre";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nombre"),
                        rs.getBoolean("categoria_activa")
                );

                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio_venta"),
                        categoria,
                        rs.getInt("existencia"),
                        rs.getString("ruta_imagen"),
                        rs.getBoolean("activo")
                );

                lista.add(producto);
            }
        }
        return lista;
    }

    /**
     * Inserta un nuevo producto, relacionándolo con su categoría (categoria_id).
     */
    public int insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto "
                + "(codigo, nombre, precio_venta, categoria_id, existencia, ruta_imagen, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setBigDecimal(3, producto.getPrecioVenta());
            ps.setInt(4, producto.getCategoria().getId());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    producto.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se pudo obtener el id generado para el producto.");
    }

    /**
     * Elimina un producto por su id.
     */
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Indica si ya existe un producto con ese código.
     */
    public boolean existePorCodigo(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM producto WHERE LOWER(codigo) = LOWER(?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
