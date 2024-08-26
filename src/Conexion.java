import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {
    static final String URL = "jdbc:mysql://10.10.131.180/db_books?useSSL=false&useTimezone=true&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    //url: jdbc:mysql://10.10.131.180/db_books?useSSL=false&useTimezone=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
    //local url: jdbc:mysql://127.0.0.1:3306/db_books
    public static Connection conexionsv(String prog_user, String prog_pass, String SQL) {
        String user = "library";
        //user:library
        //userlocal:root
        String password = "zE3F3BPsxG8ccteX@";
        //passlocal:""
        //pass:zE3F3BPsxG8ccteX@

        Connection conec = null;
        try {
            conec = DriverManager.getConnection(URL, user, password);

            PreparedStatement psu = conec.prepareStatement(SQL);
            psu.setString(1, prog_user);
            psu.setString(2, prog_pass);
            ResultSet rsu = psu.executeQuery();
            if (rsu.next()) {
                return conec;
            } else {
                conec = null;
            }
        } catch (SQLException ex) {
            System.out.println("SQL Exception: " + ex.getMessage());
        }
        return conec;
    }

    public static void Menu(Connection conec) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("----- Menú Principal -----");
            System.out.println("1. Libros por Categoría");
            System.out.println("2. Libros por Año de Publicación");
            System.out.println("3. Libros por Palabra en Título");
            System.out.println("4. Libros por Autor");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    buscarPorCategoria(conec);
                    break;
                case 2:
                    buscarPorAnoPublicacion(conec);
                    break;
                case 3:
                    System.out.print("Introduce la palabra a buscar: ");
                    String palabra = scanner.nextLine();
                    buscarPorPalabraTitulo(conec, palabra);
                    break;
                case 4:
                    System.out.print("Escribe el nombre del autor: ");
                    String autor = scanner.nextLine();
                    buscarPorAutor(conec, autor);
                    break;
                case 5:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 5);

        scanner.close();
    }

    public static void desplegarRegistros(ResultSet rs) throws SQLException {
        ResultSetMetaData metaDatos = rs.getMetaData();
        int numCol = metaDatos.getColumnCount();
        int columnWidth = 50;
        for (int i = 1; i <= numCol; i++) {
            System.out.printf("%-" + columnWidth + "s", metaDatos.getColumnName(i));
        }
        System.out.println();
        while (rs.next()) {
            for (int i = 1; i <= numCol; i++) {
                String value = rs.getString(i);
                if (value != null && value.length() > columnWidth) {
                    value = value.substring(0, columnWidth - 3) + "...";
                }
                System.out.printf("%-" + columnWidth + "s", value);
            }
            System.out.println();
        }
    }


    public static ResultSet obtenRegistros(PreparedStatement psu) {
        try {
            return psu.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error obteniendo registros: " + e.getMessage());
            return null;
        }
    }

    public static void buscarPorCategoria(Connection conec) {
        String SQL = "SELECT categories, COUNT(*) AS total FROM books GROUP BY categories LIMIT 0, 15";
        try (PreparedStatement psu = conec.prepareStatement(SQL)) {
            ResultSet rsu = obtenRegistros(psu);
            if (rsu != null) {
                System.out.println("Categorías de libros y su cantidad:");
                desplegarRegistros(rsu);
            }
        } catch (SQLException ex) {
            System.out.println("Error ejecutando la consulta: " + ex.getMessage());
        }
    }

    public static void buscarPorAnoPublicacion(Connection conec) {
        String SQL = "SELECT published_year, COUNT(*) AS cantidad FROM books GROUP BY published_year ORDER BY cantidad DESC LIMIT 0, 15";
        try (Statement stmt = conec.createStatement(); ResultSet rs = stmt.executeQuery(SQL)) {
            System.out.println("Años de publicación y cantidad de libros:");
            desplegarRegistros(rs);
        } catch (SQLException ex) {
            System.out.println("Error ejecutando la consulta: " + ex.getMessage());
        }
    }

    public static void buscarPorPalabraTitulo(Connection conec, String palabra) {
        String SQL = "SELECT books.title, books.subtitle, books.authors FROM books WHERE books.title LIKE ? LIMIT 0, 15";
        try (PreparedStatement ps = conec.prepareStatement(SQL)) {
            ps.setString(1, "%" + palabra + "%");
            ResultSet rs = obtenRegistros(ps);
            if (rs != null) {
                System.out.println("Libros que contienen la palabra '" + palabra + "' en el título:");
                desplegarRegistros(rs);
            }
        } catch (SQLException ex) {
            System.out.println("Error ejecutando la consulta: " + ex.getMessage());
        }
    }

    public static void buscarPorAutor(Connection conec, String autor) {
        String SQL = "SELECT books.title, books.subtitle, books.authors FROM books WHERE books.authors LIKE ? LIMIT 0, 15";
        try (PreparedStatement ps = conec.prepareStatement(SQL)) {
            ps.setString(1, "%" + autor + "%");
            ResultSet rs = obtenRegistros(ps);
            if (rs != null) {
                System.out.println("Libros escritos por autores que contienen '" + autor + "':");
                desplegarRegistros(rs);
            }
        } catch (SQLException ex) {
            System.out.println("Error ejecutando la consulta: " + ex.getMessage());
        }
    }


}
