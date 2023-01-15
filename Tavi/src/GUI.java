import javax.swing.*;
import java.sql.*;

public class GUI {
    private JFrame frame;
    private  int width ;
    private  int height;
    Connection connection = new Driver().getConn();


    GUI(int w, int h){
        this.height = h;
        this.width = w;
    }
    public JFrame createFrame(int id) throws SQLException {
        this.frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        PreparedStatement statement = connection.prepareStatement("SELECT * from utilizator where ID_USER = ?");
        statement.setInt(1,id);
        ResultSet rs = statement.executeQuery();
        String title = null;
        while(rs.next()){
            title = rs.getString("nume")+" "+rs.getString("prenume");
        }
        frame.setTitle(title);
        return this.frame;
    }

    public JFrame createFrame(String title){
        this.frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        return this.frame;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
