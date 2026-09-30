package ni.edu.uam.fact_appp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para obtener conexiones a la base de datos PostgreSQL.
 * Ajusta URL, USER y PASSWORD según tu configuración local (pgAdmin / psql).
 */
public final class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/fact_appp_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private DatabaseConnection() {
    }

    /**
     * Abre una nueva conexión a PostgreSQL.
     * Se recomienda usarla dentro de un try-with-resources en cada DAO,
     * para que la conexión se cierre automáticamente al terminar la consulta.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de PostgreSQL en el classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
