import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProffesorSetPercentageListener implements ActionListener {
    Connection connection = new Driver().getConn();
    ProfesorGui profesor;
    int seminar, lab, cursId;

    ProffesorSetPercentageListener(ProfesorGui profesor){
        this.profesor = profesor;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
         if(input()!=-1) {
             profesor.setPercentage(seminar, lab, cursId);
             profesor.displayGUI();
             PreparedStatement prstm = connection.prepareStatement("SELECT * FROM student");
         }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public int input(){
        JTextField seminar = new JTextField();
        JTextField lab = new JTextField();
        JTextField curs = new JTextField();
        Object[] fields = {
                "Curs", curs,
                "Procent Seminar",seminar,
                "Procent Laborator", lab
        };
        int option  = JOptionPane.showConfirmDialog(null, fields, "test",JOptionPane.OK_CANCEL_OPTION );

        if(option  == JOptionPane.OK_OPTION){
            this.seminar = Integer.parseInt(seminar.getText());
            this.lab = Integer.parseInt(lab.getText());
            if(profesor.checkCourse(this.profesor.getCourseId(curs.getText()))){
                this.cursId = this.profesor.getCourseId(curs.getText());
            }else{
                JOptionPane.showMessageDialog(null,"Nu predati acest curs!","Error!", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        }
        return 1;
    }




}
