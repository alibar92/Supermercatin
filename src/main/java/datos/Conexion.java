package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author barto
 */
public class Conexion {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/supercomprín?useSSL=false&useTimezone=true"
                + "&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "12557784";
    
    //Ahora creamos el método que obtiene la conexión:
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection  (JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
    
    //se cierran los met abiertos conectando
    public static void close(ResultSet rs) throws SQLException {
        rs.close();
    }
    public static void close(Statement stm) throws SQLException {
        stm.close();
    }
    
    //se anyade la preparacion del stm
    public static void close(PreparedStatement stm) throws SQLException{
        stm.close();
    }
    
    public static void close(Connection cnc) throws SQLException {
        cnc.close();
    }
    
}
