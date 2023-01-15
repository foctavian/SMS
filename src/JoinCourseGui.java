import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public  class JoinCourseGui implements ActionListener {
    StudentGui student;
    JoinCourseGui(StudentGui student){
        this.student = student;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            student.buildComboBox();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
