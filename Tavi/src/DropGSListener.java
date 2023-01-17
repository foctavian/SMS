import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class DropGSListener implements ActionListener {
    Connection connection = new Driver().getConn();
    StudentGui student;
    int studentId;
    Vector<String> gs;

    int id = 0;
    DropGSListener(Vector<String> gs, int studentId, StudentGui student){
        this.studentId = studentId;
        this.student =student;
        this.gs = gs;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (checkGS(getCourseId())) {
                deleteGS(getCourseId());
                JOptionPane.showMessageDialog(student.getCb(), "Grup de studiu sters cu succes!");
                student.getCb().setVisible(false);
                student.buildDropGS();
            }
        } catch (SQLException ex) {
            System.out.println("a");
        }
    }

    public void deleteGS(int cursId){
        try{
            PreparedStatement statement = connection.prepareStatement("DELETE FROM intermediar_stud_gs WHERE ID_GS = ? AND ID_STUD = ?");
            statement.setInt(1,id);
            statement.setInt(2,studentId);
            statement.executeUpdate();
             statement = connection.prepareStatement("DELETE FROM grup_studiu WHERE ID_GS = ? AND CURS = ?");
            statement.setInt(1,id);
            statement.setInt(2,cursId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean enrolledStudent(int gsId){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT* FROM intermediar_stud_gs where ID_GS = ? and ID_STUD = ?");
            statement.setInt(1,gsId);
            statement.setInt(2,studentId);
            ResultSet r = statement.executeQuery();
            if(r.next()) {
                this.id = gsId;
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean checkGS(int cursId){
        try {
            //verific daca exista un gr de studiu
                    PreparedStatement prstm = connection.prepareStatement("SELECT * FROM grup_studiu WHERE curs = ?");
                    prstm.setInt(1,cursId);
                    ResultSet rs = prstm.executeQuery();

                    //verific daca studentul e inscris
                    while(rs.next()){
                        int gsId = rs.getInt("ID_GS");
                        if(enrolledStudent(gsId)){
                            return true;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        return false;
    }

    public int getId(String description){
        try{
            PreparedStatement prstm = connection.prepareStatement("select curs_id from curs where descriere = ?");
            prstm.setString(1,description);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                return(rs.getInt("curs_id"));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            return -1;
        }
        return -1;
    }

    public int getCourseId(){
        JComboBox<String> temp = student.getCbd();
        if(temp!= null){
            Object tbd = temp.getSelectedItem();
            if(tbd!=null){
                int cursId = getId(tbd.toString());
                return cursId;
            }
        }
        return -1;
    }

}
