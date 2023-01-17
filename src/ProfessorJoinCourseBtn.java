import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ProfessorJoinCourseBtn implements ActionListener {
    ProfesorGui profesor;
    String sql = "select * from curs where not exists(select * from intermediar_prof_curs where ID_PROFESOR=? and intermediar_PROF_curs.ID_CURS = curs.curs_id)";

    ProfessorJoinCourseBtn(ProfesorGui profesor){
        this.profesor = profesor;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(profesor.getCb().getSelectedItem() == null){
                JOptionPane.showMessageDialog(null, "ERROR!", "ERROR!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (checker()) {
                profesor.buildCourseList(sql, ProfesorGui.TYPE.JOIN.ordinal());
                profesor.displayGUI();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checker(){
        String temp = profesor.getCb().getSelectedItem().toString();
        int cursId = profesor.getCourseId(temp, 1);
        if(!profesor.checkCourse(cursId)){
            profesor.joinCourse(cursId);
            return true;
        }
        return false;
    }
}
