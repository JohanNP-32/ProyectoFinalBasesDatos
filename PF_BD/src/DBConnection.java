
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/AvanceProyecto_BD_VitalCare"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "Aphelios#291102"; 

    
    public static Connection getConnection() {
        try {
        
            
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException e) {
            System.err.println("\nERROR!No se pudo conectar a la base de datos.");
            System.err.println("Detalles: " + e.getMessage());
            return null;
        }
    }
}