import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CreateGSListener implements ActionListener {
    StudentGui student;
    CreateGSListener(StudentGui student){
        this.student = student;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            this.student.buildCreateGS();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
