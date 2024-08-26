import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Scanner;

// Autor: Manuel Munguia Rubio
public class Main {
    public static void main(String[] args) throws IOException {
        boolean seguir=true;
        while (seguir){
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Scanner sc = new Scanner(System.in);
            System.out.print("Ingrese su correo: ");
            String program_username = sc.nextLine();
            //No pude hacer lo de la contraseña profe probe de muchas maneras :(
            System.out.print("Ingrese su contrasena: ");
            String program_password = sc.nextLine();
            String SQL = "SELECT username FROM users WHERE email = ? AND password = SHA2(?, 256)";
            //System.out.println(SQL);
            Connection conexion = Conexion.conexionsv(program_username, program_password, SQL);

            if (conexion != null) {
                System.out.println("Successful connection to the database");
                System.out.println("Welcome " + program_username);
                //Punto de acceso
                Conexion.Menu(conexion);
                seguir = false;

            } else {
                System.out.println("Acceso denegado");
                System.out.println("¿Quieres volver a intentar?\t" + "Selecciona S para si y N para no");

                String res = sc.next().toUpperCase();
                if (res.equals("N")) seguir = false;

            }
        }

    }


}
