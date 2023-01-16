import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class DropCourseBtn implements ActionListener {
    Connection connection = new Driver().getConn();
    StudentGui student;
    int studentId;

    Vector<String> cursuri;
    DropCourseBtn(Vector<String> cursuri,int studentId, StudentGui student){
        this.studentId = studentId;
        this.student =student;
        this.cursuri = cursuri;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (checkCourse(getCourseId())) {
                dropCourse(getCourseId());
                JOptionPane.showMessageDialog(student.getCbd(), "Ati renuntat la curs cu succes!");
                student.buildDropCourse();
                student.setVisibility(false);
                student.displayGUI();
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public boolean checkCourse(int cursId){
        try{
            Statement statement = connection.createStatement();
            String sql = "select * from intermediar_stud_curs";
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                if(rs.getInt("ID_STUDENt") == studentId){
                    if(rs.getInt("ID_CURS") == cursId){
                        return true;
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();

        }
        return false;
    }

    public void dropCourse(int cursId){
        if(checkCourse(cursId)){
            try{
                PreparedStatement prep = connection.prepareStatement("DELETE FROM intermediar_stud_curs where ID_STUDENT = ? AND ID_CURS = ?");
                prep.setInt(1,studentId);
                prep.setInt(2,cursId);
                prep.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
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
}
