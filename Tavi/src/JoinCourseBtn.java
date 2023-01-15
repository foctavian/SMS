import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Stack;
import java.util.Vector;

public class JoinCourseBtn implements ActionListener {

    Connection connection = new Driver().getConn();
    StudentGui student;
    int studentId;
    Vector<String> cursuri;
    JoinCourseBtn(Vector<String> cursuri, int id, StudentGui student){
        this.cursuri = cursuri;
        this.studentId = id;
        this.student = student;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void insertIntoInterm(int cursId){
    }

    public boolean checkTimeTable(int cursId, int studentId) throws SQLException {
        Vector<Integer> enrolled = new Vector<Integer>();
        PreparedStatement prstm = connection.prepareStatement("select * from intermediar_stud_curs where ID_STUDENT = ?");
        prstm.setInt(1,studentId);
        ResultSet rs = prstm.executeQuery();

        while(rs.next()){
            enrolled.add(rs.getInt("ID_CURS"));
        }

        //folosirea unui hashmap pentru verificarea mai eficienta a intrarilor
        return true;
    }

}
