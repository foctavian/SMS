import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public  class LoginListener  implements ActionListener {
    private static ProfesorGui profesorGui;
    private static StudentGui studentGui;
    private static LoginGui login;
    private boolean found ;
    LoginListener(LoginGui login) {
        this.login = login;
        found = false;
    }

    public boolean isFound() {
        return found;
    }


    public void actionPerformed(ActionEvent e) {
        String user;
        String pass;

        // String check = "SELECT * FROM utilizator where USERNAME="+user+" and PASS="+pass+"";
        Connection connection = new Driver().getConn();
        try {
            user = login.getUsername();
            pass = login.getPassword();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM utilizator");

            while(rs.next()){
                if(rs.getString("USERNAME").equals(user)){
                    if(rs.getString("PASS").equals(pass)){
                        found = true;
                        login.setVisibiliy(false);
                        int role = rs.getInt("rol");
                        switch(role){
                            case 1: break;
                            case 2: break;
                            case 3:
                                profesorGui = new ProfesorGui(rs.getInt("ID_USER"));
                                profesorGui.evaluateInfo();
                                profesorGui.displayGUI();
                                break;
                            case 4:
                                int id = rs.getInt("ID_USER");

                                studentGui = new StudentGui(id);
                                studentGui.evaluateInfo();
                                studentGui.displayGUI();
                                break;
                            default: break;

                        }

                    }
                }
            }
            if(!found){
                JOptionPane.showMessageDialog(login.getLogin(), "Credentiale incorecte!","Error!", JOptionPane.ERROR_MESSAGE);
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
