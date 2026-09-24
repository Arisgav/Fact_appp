package ni.edu.uam.fact_appp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para obtener conexiones a la base de datos PostgreSQL.
 * Ajusta URL, USER y PASSWORD según tu configuración local.
 */
public final class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/fact_appp_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "tu_password";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
