    import javax.swing.*;
    import java.sql.*;
    import java.util.Scanner;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    public class Conexion {
        //Realize pruebas con phpmyadmin en local host y funciono con esta direccion "jdbc:mysql://127.0.0.1:3306/db_books"
        static final String URL = "jdbc:mysql://10.10.131.180/db_books?useSSL=false&useTimezone=true&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        public static Connection conexionsv(String prog_user,String prog_pass,String SQL){
            //user use "root"
            String user = "library";
            //Y en pass lo deje vacio ""
            String password = "zE3F3BPsxG8ccteX@";
            Connection conec = null;
            //Pude importar las bases de datos del servidor original y colocar
            //mis datos para corrobar de manera practica que hacia la conexion
            try {
                conec = DriverManager.getConnection(URL, user, password);
                System.out.println("Successful connection to the database");
                PreparedStatement psu = conec.prepareStatement(SQL);
                psu.setString(1, prog_user);
                psu.setString(2, prog_pass);
                System.out.println("SQL final: " + psu.toString());
                ResultSet rsu = psu.executeQuery();

                if (rsu.next()) {
                    return conec;
                }
                else {
                    conec=null;
                }

            }
            catch (SQLException ex) {
                System.out.println("SQL Exception: " + ex.getMessage());
                System.out.println("SQL State: " + ex.getSQLState());
                System.out.println("Vendor Error: " + ex.getErrorCode());
            }
            catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return conec;
        }
        public static void Menu(Connection conec) {
            Scanner scanner = new Scanner(System.in);
            int opcion = -1;

            do {
                System.out.println("----- Menú Principal -----");
                System.out.println("1. Libros por Categoria 1");
                System.out.println("2. Libros por año de publicacion 2");
                System.out.println("3. Libros que cuenten con una palabra en el titulo 3");
                System.out.println("4. Libros por un autor 4");
                System.out.println("5. Salir 5");
                System.out.print("Seleccione una opción: ");
                String entrada = scanner.nextLine();

                try {
                    opcion = Integer.parseInt(entrada);
                    switch (opcion) {
                        case 1:
                            System.out.println("Has seleccionado buscar por Categoria.");
                            buscarPorCategoria(conec);
                            break;
                        case 2:
                            System.out.println("Has seleccionado buscar por año de publicacion.");
                            buscarPorAnoPublicacion(conec);
                            break;
                        case 3:
                            System.out.println("Has seleccionado buscar por palabra.");
                            System.out.println("Introduce la palabra a buscar a continuacion: ");
                            String palabra = scanner.nextLine();
                            buscarPorPalabraTitulo(conec,palabra);
                            break;
                        case 4:
                            System.out.println("Has seleccionado buscar por autor.");
                            System.out.println("Escribe el nombre del autor: ");
                            String autor = scanner.nextLine();
                            buscarPorAutor(conec,autor);
                            break;
                        case 5:
                            System.out.println("Saliendo del programa..");
                            break;
                        default:
                            System.out.println("Opción no válida. Intente de nuevo.");
                    }

                } catch (NumberFormatException e) {

                    System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 5.");
                }

                System.out.println();
            } while (opcion != 5);

            scanner.close();
        }
//        public static void desplegarRegistros(ResultSet rs) throws SQLException {
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//            for (int i = 1; i <= columnCount; i++) {
//                System.out.print(metaData.getColumnName(i) + "\t");
//            }
//            System.out.println();
//            while (rs.next()) {
//                for (int i = 1; i <= columnCount; i++) {
//                    System.out.print(rs.getString(i) + "\t");
//                }
//                System.out.println();
//            }
//        }
        public static void desplegarRegistros(ResultSet rs) throws SQLException {
            try {
                ResultSetMetaData metaDatos = rs.getMetaData();
                int numCol = metaDatos.getColumnCount();
                for (int i = 1; i <= numCol ; i++) {
                    System.out.printf("%-8s\t",metaDatos.getColumnName(i));
                }
                if (rs.next() == true) {
                    while (rs.next()) {
                        for (int i = 1; i <= numCol ; i++) {
                            System.out.printf("%-64s",rs.getObject(i));
                        }
                    }
                    System.out.println("");
                }

            }catch (Exception e){
                System.out.println("despliegaRegistros Error: " + e.getMessage());
            }
        }
        public static ResultSet obtenRegistros(PreparedStatement psu){
            ResultSet rsu = null;
            try {
                rsu = psu.executeQuery();
            } catch (Exception e){
                System.out.println("obtenRegistros Error: " + e.getMessage());
            }
            return rsu;
        }
        public static void buscarPorCategoria(Connection conec) {
            String SQL = "SELECT `categories`, COUNT(*) AS total FROM books GROUP BY categories ";
//                    "FROM books " +
//                    "GROUP BY `categories` " +
//                    "ORDER BY cantidad DESC " +
//                    "LIMIT 0,15";
            try {
                PreparedStatement psu = conec.prepareStatement(SQL);
                System.out.println("Categorías de libros y su cantidad:");
                ResultSet rsu = obtenRegistros(psu);
                desplegarRegistros(rsu);
            } catch (SQLException ex) {
                System.out.println("Error ejecutando la consulta: " + ex.getMessage());
            }
        }

        public static void buscarPorAnoPublicacion(Connection conec) {
            String SQL = "SELECT `published_year`, COUNT(*) AS cantidad " +
                    "FROM books " +
                    "GROUP BY `published_year` " +
                    "ORDER BY cantidad DESC " +
                    "LIMIT 0,15";
            try (Statement stmt = conec.createStatement(); ResultSet rs = stmt.executeQuery(SQL)) {
                System.out.println("Años de publicación y cantidad de libros:");
                desplegarRegistros(rs);
            } catch (SQLException ex) {
                System.out.println("Error ejecutando la consulta: " + ex.getMessage());
            }
        }
        public static void buscarPorPalabraTitulo(Connection conec, String palabra) {
            String SQL = "SELECT books.title, books.subtitle, books.authors " +
                    "FROM books " +
                    "WHERE books.title LIKE ? " +
                    "LIMIT 0,15";
            try (PreparedStatement ps = conec.prepareStatement(SQL)) {
                ps.setString(1, "%" + palabra + "%");
                ResultSet rs = ps.executeQuery();
                System.out.println("Libros que contienen la palabra '" + palabra + "' en el título:");
                desplegarRegistros(rs);
            } catch (SQLException ex) {
                System.out.println("Error ejecutando la consulta: " + ex.getMessage());
            }
        }
        public static void buscarPorAutor(Connection conec, String autor) {
            String SQL = "SELECT books.title, books.subtitle, books.authors " +
                    "FROM books " +
                    "WHERE books.authors LIKE ? " +
                    "LIMIT 0,15";
            try (PreparedStatement ps = conec.prepareStatement(SQL)) {
                ps.setString(1, "%" + autor + "%");
                ResultSet rs = ps.executeQuery();
                System.out.println("Libros escritos por autores que contienen '" + autor + "':");
                desplegarRegistros(rs);
            } catch (SQLException ex) {
                System.out.println("Error ejecutando la consulta: " + ex.getMessage());
            }
        }






    }
