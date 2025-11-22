import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/klp3_db", "root", "cultural123")) {
            if (conn != null) {
                System.out.println("Conexión exitosa a la base de datos.");
            } else {
                System.out.println("nFallo en la conexión a la base de datos.");
            }
        }
    
        catch (SQLException e) {
        System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }   
    }
}
