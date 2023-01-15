import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Controller {
    private static LoginGui login;
    private static StudentGui studentGui;
    private static ProfesorGui profesorGui;
    Controller(LoginGui login){
        this.login = login;
    }
    public static class LoginListener extends Controller implements ActionListener {
        private boolean found ;
        private ResultSet info;
        LoginListener() {
            super(login);
            found = false;
        }

        public boolean isFound() {
            return found;
        }

        public ResultSet getInfo() {
            return info;
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
                    JOptionPane.showMessageDialog(login.getLogin(), "Credentiale incorecte!");
                }


            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public class InscriereCurs implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public class RenuntareCurs implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public class AfisareCurs implements  ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public class InscriereGrupDeStudiu implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
