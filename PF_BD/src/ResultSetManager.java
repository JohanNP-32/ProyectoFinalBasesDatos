import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetManager {
    
    /**
     * Imprime el contenido de un ResultSet en formato tabular en la consola.
     */
    public static void printResultSet(ResultSet rs, String titulo) throws SQLException {
        if (rs == null) return;
        // Obtiene información sobre las columnas 
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        System.out.println("\nREPORTE: " + titulo + " <<<");
        
        // Imprimir titulos de la columnas
        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%-30s", metaData.getColumnLabel(i));
        }
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        // Imprimir los datos fila por fila 
        boolean hayDatos = false;
        while (rs.next()) {
            hayDatos = true;
            for (int i = 1; i <= columnCount; i++) {
                String valor = rs.getString(i);
                System.out.printf("%-30s", valor != null ? valor : "NULL");
            }
            System.out.println();
        }
        
        if (!hayDatos) {
            System.out.println("(La consulta se ejecutó correctamente pero no devolvió resultados)");
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------\n");
    }
}


