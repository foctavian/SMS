import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class JoinGSGUI implements ActionListener {
    StudentGui student;
    JoinGSGUI(StudentGui student){
        this.student = student;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            student.buildJoinGS();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
