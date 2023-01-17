import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ProffessorDropCourseBtn implements ActionListener {
    ProfesorGui profesor;
    int cursId;

    ProffessorDropCourseBtn(ProfesorGui profesor){
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
                profesor.dropCourse(cursId);
                profesor.buildCourseList("select * from curs where  exists(select * from intermediar_prof_curs where ID_PROFESOR=? and intermediar_PROF_curs.ID_CURS = curs.curs_id)",
                        ProfesorGui.TYPE.DROP.ordinal());
                profesor.displayGUI();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checker(){
        String temp = profesor.getCb().getSelectedItem().toString();
        if(temp == null) return false;
        this.cursId = profesor.getCourseId(temp, 1);
        if(profesor.checkCourse(cursId)){
            return true;
        }
        return false;
    }
}
