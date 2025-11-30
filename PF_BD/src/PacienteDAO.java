import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class PacienteDAO {

    // ------------------------------------------------------------
    // OPCIÓN A - Vistas 
    // ------------------------------------------------------------
    // Este método solo lee datos ya organizados en la base de datos.
    public void ejecutarVista(int opcion) {
        String vista = "";
        // Elige qué vista usar según el número del menú
        switch (opcion) {
            case 1: vista = "ingreso_especialidad"; break;
            case 2: vista = "citas_antiguedad"; break;
            case 3: vista = "directorio_usuarios"; break;
            case 4: vista = "carga_trabajo_doctor"; break;
            default: System.out.println("Vista no reconocida."); return;
        }

        String sql = "SELECT * FROM " + vista;
        // Conectamos y ejecutamos la consulta
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (conn != null) {
                // Usamos el Manager para imprimir la tabla con un buen formato
                ResultSetManager.printResultSet(rs, "VISTA: " + vista);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar vista: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // OPCIONES B, C, D - Procedimientos
    // ------------------------------------------------------------
    public void ejecutarProcedimiento(int opcion, Scanner scanner) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return; // Si no hay conexión, no hacemos nada


        try {
            switch (opcion) {
                // Caso 1: Calcular cuánto dinero entró en un día específico
                case 1: 
                    System.out.print("Ingrese fecha (YYYY-MM-DD): ");
                    String fecha = scanner.next();
                    // Llamamos al procedimiento que pide fecha y devuelve un total
                    String sql1 = "{CALL Reporte_Ingresos_Diarios(?, ?)}";
                    
                    try (CallableStatement stmt = conn.prepareCall(sql1)) {
                        stmt.setString(1, fecha); // Ponemos la fecha
                        stmt.registerOutParameter(2, Types.DECIMAL); // Preparamos para recibir el total
                        
                        boolean tieneResultados = stmt.execute();
                        
                        if (tieneResultados) {
                              // Imprimir la tabla de detalles
                            ResultSetManager.printResultSet(stmt.getResultSet(), "Desglose de Ventas (" + fecha + ")");
                        }
                        // Imprimir el total de dinero
                        BigDecimal total = stmt.getBigDecimal(2);
                        System.out.println("TOTAL INGRESOS DEL DÍA: $" + (total != null ? total : "0.00"));
                    }
                    break;

                case 2:
                // Reportar pacientes activos en noviembre
                    String sql2 = "{CALL Reporte_Pacientes_Activos_Noviembre()}";
                    try (CallableStatement stmt = conn.prepareCall(sql2)) {
                        stmt.execute();
                        ResultSetManager.printResultSet(stmt.getResultSet(), "Pacientes Activos en Noviembre");
                    }
                    break;

                case 3:
                // Agregar nuevo paciente con validación
                    System.out.println("\n--- Nuevo Paciente ---");
                    System.out.print("ID (Ej: P050): "); String id = scanner.next();
                    System.out.print("Nombre: "); String nom = scanner.next();
                    System.out.print("Apellido: "); String ape = scanner.next();
                    System.out.print("Fecha Nac (YYYY-MM-DD): "); String nac = scanner.next();
                    System.out.print("Sexo (M/F): "); String sex = scanner.next();
                    System.out.print("Teléfono: "); String tel = scanner.next();
                    System.out.print("Correo : "); String mail = scanner.next();
                    System.out.print("Dirección: "); scanner.nextLine(); String dir = scanner.nextLine();

                    String sql3 = "{CALL Agregar_Nuevo_Paciente(?, ?, ?, ?, ?, ?, ?, ?)}";
                    try (CallableStatement stmt = conn.prepareCall(sql3)) {
                        stmt.setString(1, id); stmt.setString(2, nom); stmt.setString(3, ape);
                        stmt.setString(4, nac); stmt.setString(5, sex); stmt.setString(6, tel);
                        stmt.setString(7, mail); stmt.setString(8, dir);
                        
                        stmt.execute();
                        // El procedimiento nos devuelve un mensaje de texto (Éxito o Error)
                        ResultSet rs = stmt.getResultSet();
                        if (rs != null && rs.next()) {
                            System.out.println("\nRESPUESTA DB: " + rs.getString(1));
                        }
                    }
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception e) {}
        }
    }

    // ------------------------------------------------------------
    // OPCIÓN E - Triggers
    // ------------------------------------------------------------
    // Insertar datos que activan triggers
    public void insertarDatosTrigger(Scanner scanner) {
        System.out.println("\n--- Insertar Cita ---");
        System.out.print("ID Cita (Ej: C99): "); String idCita = scanner.next();
        System.out.print("Fecha (YYYY-MM-DD): "); String fecha = scanner.next();
        System.out.print("Hora (HH:MM:SS): "); String hora = scanner.next();
        scanner.nextLine(); 
        System.out.print("Motivo: "); String motivo = scanner.nextLine();
        System.out.print("ID Paciente (Ej: P001): "); String idPac = scanner.next();
        System.out.print("ID Doctor (Ej: D001): "); String idDoc = scanner.next();

        String sql = "INSERT INTO Citas (idCita, fechaCita, horaCita, motivo, estado, idPaciente, idDoctor) VALUES (?, ?, ?, ?, 'Pendiente', ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return;
            pstmt.setString(1, idCita);
            pstmt.setString(2, fecha);
            pstmt.setString(3, hora);
            pstmt.setString(4, motivo);
            pstmt.setString(5, idPac);
            pstmt.setString(6, idDoc);

            int filas = pstmt.executeUpdate(); // Ejecuta la inserción
            if (filas > 0) {
                System.out.println("\n¡ÉXITO! Cita registrada.");
                System.out.println("Trigger 'Seguimiento_Paciente_Cita' ejecutado (Revisa tabla Seguimiento).");
            }

        } catch (SQLException e) {
            System.err.println("\n¡ERROR SQL! No se pudo insertar.");
            System.err.println("Mensaje: " + e.getMessage());
            // Verificamos si el error es por el trigger de horario duplicado
            if (e.getSQLState().equals("45000")) {
                System.err.println("¡ALERTA! El Trigger detectó un HORARIO DUPLICADO para este doctor.");
            }
        }
    }

    // ------------------------------------------------------------
    // OPCIÓN F - Eliminar Registro 
    // ------------------------------------------------------------
    // Eliminar una cita por su ID
    public void eliminarRegistro(Scanner scanner) {
        System.out.println("\n--- Eliminar Cita ---");
        System.out.print("ID de la Cita a eliminar (Ej: C99): ");
        String id = scanner.next();

        String sql = "DELETE FROM Citas WHERE idCita = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return;

            pstmt.setString(1, id);
            int filas = pstmt.executeUpdate();

            if (filas > 0) {
                System.out.println("¡ÉXITO! Registro eliminado.");
            } else {
                System.out.println("No se encontró esa cita.");
            }

        } catch (SQLException e) {
            System.err.println("\n¡ERROR!");
            System.err.println("No se puede eliminar este registro porque tiene datos dependientes.");
            System.err.println("Detalle técnico: " + e.getMessage());
        }
    }
}
