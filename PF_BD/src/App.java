import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        PacienteDAO dao = new PacienteDAO();
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        System.out.println("Iniciando sistema VitalCare");

        while (opcion != 0) {
            System.out.println("\n----------------- MENÚ VITALCARE -----------------");
            System.out.println("VISTAS SQL");
            System.out.println("  1. Ingresos por Especialidad");
            System.out.println("  2. Antigüedad de Citas");
            System.out.println("  3. Directorio Unificado");
            System.out.println("  4. Carga Trabajo Doctores");
            System.out.println("Procedimientos Almacenados");
            System.out.println("  5. Reporte Ingresos Diarios");
            System.out.println("  6. Pacientes Activos Noviembre");
            System.out.println("  7. Agregar Paciente con Validación");
            System.out.println("Triggers y CRUD");
            System.out.println("  8. Insertar Cita");
            System.out.println("  9. Eliminar Cita");
            System.out.println("  0. Salir");
            System.out.println("--------------------------------------------------------------");
            System.out.print("Selecciona una opción: ");

            try {
                String input = scanner.next();
                opcion = Integer.parseInt(input);

                switch (opcion) {
                    // VISTAS SQL
                    case 1: dao.ejecutarVista(1); break;
                    case 2: dao.ejecutarVista(2); break;
                    case 3: dao.ejecutarVista(3); break;
                    case 4: dao.ejecutarVista(4); break;
                    // PROCEDIMIENTOS 
                    case 5: dao.ejecutarProcedimiento(1, scanner); break;
                    case 6: dao.ejecutarProcedimiento(2, scanner); break;
                    case 7: dao.ejecutarProcedimiento(3, scanner); break;
                    // TRIGGERS Y CRUD
                    case 8: dao.insertarDatosTrigger(scanner); break;
                    case 9: dao.eliminarRegistro(scanner); break;
                    case 0: System.out.println("Cerrando sistema"); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: Ingresa un número válido.");
                scanner.nextLine(); 
            }
        }
        scanner.close();
    }
}
