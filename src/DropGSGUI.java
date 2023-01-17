import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.DrbgParameters;
import java.sql.SQLException;

public class DropGSGUI implements ActionListener {
    StudentGui student;

    DropGSGUI(StudentGui student){
        this.student = student;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            student.buildDropGS();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
