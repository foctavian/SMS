
import java.sql.*;

public class Driver {
    Connection conn;

    private final String url = "jdbc:mysql://localhost:3306/proiectbd";

    private final String pass = "proiectbd";
    private final String user = "admin";

    public Connection getConn() {
        try{
            conn = DriverManager.getConnection(url, user ,pass);
            return conn;
        }catch(SQLException e) {
            System.out.println("CRASH");
            return null;
        }
    }
}
