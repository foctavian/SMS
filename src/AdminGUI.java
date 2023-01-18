import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.concurrent.locks.ReadWriteLock;

public class AdminGUI {
    Connection connection = new Driver().getConn();
    int adminId;
    private JFrame admin;
    AdminGUI(int adminId){
        this.adminId = adminId;
        this.admin = new GUI(300, 250).createFrame("__ADMIN__");
        this.admin.setVisible(true);
    }

    public void displayGUI(){
        ImageIcon icon = new ImageIcon("resources/AdminIcon.png");
        admin.setIconImage(icon.getImage());

        JMenu menu = new JMenu("Utilizatori");
        JMenuBar bar = new JMenuBar();
        JMenuItem stergere = new JMenuItem("Stergere");
        JMenuItem cautare = new JMenuItem("Cautare");
        cautare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildSearchUser();
            }
        });
        stergere.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildDeleteUser();
            }
        });
        menu.add(cautare);
        menu.add(stergere);
        bar.add(menu);
        JMenu menuCurs = new JMenu("Cursuri");
        JMenuItem cautareCurs = new JMenuItem("Cautare");
        JMenuItem adaugareProfesor = new JMenuItem("Alocare profesor");
        JMenuItem stergereProfesor = new JMenuItem("Dezalocare profesor");
        JMenuItem creareCurs = new JMenuItem("Creare");
        cautareCurs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    buildSearchCourse();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        creareCurs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildCreateCourse();
            }
        });
        menuCurs.add(creareCurs);
        menuCurs.add(cautareCurs);
        menuCurs.add(adaugareProfesor);
        menuCurs.add(stergereProfesor);
        bar.add(menuCurs);
        admin.getContentPane().setBackground(new Color(220,255,255));
        admin.setJMenuBar(bar);
        admin.setResizable(false);
        admin.setVisible(true);
    }

    public void buildSearchUser(){
        try{
            JTextField nume = new JTextField("");
            JTextField prenume = new JTextField();

            Object[] fields = {
                    "Nume",nume,
                    "Prenume", prenume
            };
            Object[] option = {"Cautare","Cancel"};
            int o=JOptionPane.showOptionDialog(null,fields,"Cautare utilizator",
                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[0]);

            if(o == JOptionPane.OK_OPTION){
                String n = nume.getText();
                String p = prenume.getText();
                searchUser(n,p);
            }
        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildCreateCourse(){
        try{
            JTextField nume = new JTextField();
            JTextField max = new JTextField();
            JTextField descriere = new JTextField();

            Object[] fields={
                    "Curs", nume,
                    "Max", max,
                    "Descriere", descriere,
            };

            Object[] options = {
                    "Creare","Cancel"
            };
            int option  = JOptionPane.showOptionDialog(null,fields, "Creare curs",
                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);

            if(option == JOptionPane.OK_OPTION){
                String n = nume.getText();
                int m = Integer.parseInt(max.getText());
                String des = descriere.getText();
                if(createCourse(n,m,des)) {
                    JOptionPane.showMessageDialog(null, "Cursul " + n + " a fost create cu succes!",
                            "Creare curs", JOptionPane.PLAIN_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Operatie invalida!",
                            "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildSearchCourse(){
        try{
            JTextField nume = new JTextField();
            Object fields[] ={
                    "Nume", nume
            };
            Object[] option = {"Cautare","Cancel"};
            int o =JOptionPane.showOptionDialog(null,fields,"Cautare curs",
                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[0]);

            if(o == JOptionPane.OK_OPTION){
                String n = nume.getText();
                searchCourse(n);
            }
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
    }

    public boolean createCourse(String nume, int max, String des){
        try{
            PreparedStatement prstm = connection.prepareStatement("INSERT INTO curs(nume,max,descriere,TIME_TABLE)" +
                    "values(?,?,?,?)");
            prstm.setString(1,nume);
            prstm.setInt(2,max);
            prstm.setString(3,des);
            prstm.setInt(4,1);
            prstm.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchCourse(String nume){
        try{
            PreparedStatement prstm = connection.prepareStatement("SELECT * FROM curs WHERE nume = ? OR descriere = ?");
            prstm.setString(1,nume);
            prstm.setString(2,nume);
            ResultSet rs = prstm.executeQuery();

            if(rs.next()){
                String des = rs.getString("descriere");
                JOptionPane.showMessageDialog(null, "Cursul "+des+" exista!",
                        "Cautare curs",JOptionPane.PLAIN_MESSAGE );
            }else{
                JOptionPane.showMessageDialog(null,  "Cursul "+nume+" nu exista!",
                        "Cautare curs",JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void buildDeleteUser(){
        try{
            JTextField nume = new JTextField("");
            JTextField prenume = new JTextField();

            Object[] fields = {
                    "Nume",nume,
                    "Prenume", prenume
            };
            Object[] option = {"Stergere","Cancel"};
            int o=JOptionPane.showOptionDialog(null,fields,"Stergere utilizator",
                    JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[0]);

            if(o == JOptionPane.OK_OPTION){
                int o2 = JOptionPane.showConfirmDialog(null,  "Sunteti sigur ca doriti sa stergeti acest utilizator?",
                        " ",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
                if(o2 == JOptionPane.YES_OPTION){
                    String n = nume.getText();
                    String p = prenume.getText();
                    int id = checkUser(n, p);
                    PreparedStatement prstm = connection.prepareStatement("select * from utilizator where ID_USER = ?");
                    prstm.setInt(1,id);
                    ResultSet rs = prstm.executeQuery();
                    if(rs.next()){
                        if(checkHierarchy(rs.getInt("ID_USER"))) {
                            int role = rs.getInt("rol");
                            deleteUser(rs.getString("CNP"), role);
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Operatie invalida!",
                                    "Error!", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Utilizator inexistent!",
                                "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchUser(String nume, String prenume){
        try{
            PreparedStatement prstm = connection.prepareStatement("SELECT * FROM utilizator WHERE nume = ? and prenume = ?");
            prstm.setString(1,nume);
            prstm.setString(2,prenume);
            ResultSet rs = prstm.executeQuery();

            if(rs.next()){
                int rol = rs.getInt("rol");
                String role;
                if(rol == 1){
                    role = "Super-Administrator";
                }else if(rol == 2){
                    role = "Administrator";

                }else if(rol == 3){
                    role = "Profesor";

                }else{
                    role = "Student";

                }
                String id = String.valueOf(rs.getInt("ID_USER"));
                JOptionPane.showMessageDialog(null, "Utilizatorul "+nume+" "+prenume+" exista si are ID-ul "+id+" si rolul de "+role+"!",
                        "Cautare Utilizator", JOptionPane.PLAIN_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null, "Utilizatorul "+nume+" "+prenume+" nu a putut fi gasit!",
                        "Cautare Utilizator", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int checkUser(String nume, String prenume){
        try{
            PreparedStatement prstm = connection.prepareStatement("SELECT * FROM utilizator where nume = ? and prenume = ?");
            prstm.setString(1,nume);
            prstm.setString(2,prenume);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                return rs.getInt("ID_USER");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteUser(String CNP, int role){
        try{
            CallableStatement cs = connection.prepareCall("CALL delete_user(?,?)");
            cs.setString(1,CNP);
            cs.setInt(2,role);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkHierarchy(int id){
        try{

            PreparedStatement prstm = connection.prepareStatement("SELECT * FROM utilizator where ID_USER = ?");
            prstm.setInt(1,adminId);
            ResultSet rs = prstm.executeQuery();
            int rolAdmin = 0;
            if(rs.next()){
                rolAdmin = rs.getInt("rol");
            }
            prstm.setInt(1,id);
            rs = prstm.executeQuery();
            if(rs.next()){
                if(rs.getInt("rol") > rolAdmin) return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
