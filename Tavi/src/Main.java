import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = new Driver().getConn();
        LoginGui login = new LoginGui();
    }
}