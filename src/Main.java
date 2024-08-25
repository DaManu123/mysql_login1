import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

//Autor: Manuel Munguia Rubio
//Clase de Desarrollo 2-3
public class Main {

    public static void main(String[] args) {
        //Una disculpa maestro por las dos clases lo hice para tener mas limpio el codigo y separado
        String program_username = "manumr_04@hotmail.com";
        String program_password = "1234567";
        String SQL = "SELECT username FROM users WHERE email = ? AND password = SHA2(?, 256)";
        System.out.println(SQL);

        Connection conexion = Conexion.conexionsv(program_username,program_password,SQL);
        //El if corrobora la conexion en dado caso de no conectar no entra al programa con la sentencia (conexion != null)
        if(conexion != null){
            System.out.println("Welcome " + program_username);
            //Punto de Acceso
            Conexion.Menu(conexion);

        }else {
            System.out.println("Access Denied");
        }


    }
}