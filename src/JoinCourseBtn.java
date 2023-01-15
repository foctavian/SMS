import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

public class JoinCourseBtn implements ActionListener {

    Connection connection = new Driver().getConn();
    StudentGui student;
    int studentId;
    Vector<String> cursuri;
    HashMap<Integer, TimeTable> map = new HashMap<>();
    JoinCourseBtn(Vector<String> cursuri, int id, StudentGui student) {
        this.cursuri = cursuri;
        this.studentId = id;
        this.student = student;
        try {
            populateMap();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(checkTimeTable()){
               populateMap();
               JOptionPane.showMessageDialog(student.getCb(),"Inscriere la curs efectuata cu succes!");
               student.buildComboBox();
               student.setVisibility(false);
               student.displayGUI();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void insertIntoInterm(int curs, int student){
        try{
            PreparedStatement prstm = connection.prepareStatement("INSERT INTO intermediar_stud_curs" +
                    "(ID_STUDENT, ID_CURS)values(?,?)");
            prstm.setInt(1,student);
            prstm.setInt(2,curs);
            prstm.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void populateMap() throws SQLException {
        Vector<Integer> enrolled = new Vector<Integer>();
        PreparedStatement prstm = connection.prepareStatement("select * from intermediar_stud_curs where ID_STUDENT = ?");
        prstm.setInt(1,studentId);
        ResultSet rs = prstm.executeQuery();

        while(rs.next()){
            enrolled.add(rs.getInt("ID_CURS"));
        }
        //curatare map
        map.clear();
        //reinserare map
        for(int i = 0;i<enrolled.size();i++){
            prstm = connection.prepareStatement("select time_table.*,curs.curs_id from time_table left join curs on curs_id = ? where time_table.ID_TT = curs.TIME_TABLE");
            prstm.setInt(1,enrolled.get(i));
            rs = prstm.executeQuery();

            while(rs.next()){
                map.put(enrolled.get(i), new TimeTable(rs.getDate("DATE_A"),rs.getDate("DATE_B"),
                        rs.getTime("ORA"), rs.getInt("ID_TT")));
            }
        }
    }

    public int getTtId(String description){
        try{
            PreparedStatement prstm = connection.prepareStatement("select TIME_TABLE from curs where descriere = ?");
            prstm.setString(1,description);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                return(rs.getInt("TIME_TABLE"));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            return -1;
        }
        return -1;
    }

    public int getCourseId(String description){
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

    public boolean checkTimeTable() throws SQLException {
        JComboBox<String> temp = student.getCb();
        if(temp != null){
            Object tbi = temp.getSelectedItem();
            if(tbi!= null){
                //query pe tabela de curs pentru a lua time_table id
                int timeId = getTtId(tbi.toString());
                int cursId = getCourseId(tbi.toString());
                PreparedStatement prstm = connection.prepareStatement("SELECT * FROM time_table where " +
                        "ID_TT = ?");
                prstm.setInt(1, timeId);
                ResultSet rs = prstm.executeQuery();
                if(rs.next()) {
                    Date a = rs.getDate("DATE_A");
                    Date b = rs.getDate("DATE_B");
                    Time ora = rs.getTime("ORA");
                    if(!map.containsValue(new TimeTable(a,b,ora,timeId))){
                        map.put(cursId, new TimeTable(a,b,ora,timeId));
                        insertIntoInterm(cursId, this.studentId);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
