import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ProffesorDropCourseListener implements ActionListener {
    ProfesorGui profesor;
    ProffesorDropCourseListener(ProfesorGui profesor){
        this.profesor = profesor;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            profesor.buildCourseList("select * from curs where  exists(select * from intermediar_prof_curs where ID_PROFESOR=? and intermediar_PROF_curs.ID_CURS = curs.curs_id)",
                   ProfesorGui.TYPE.DROP.ordinal());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

