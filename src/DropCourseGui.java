import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class DropCourseGui implements ActionListener {
    StudentGui student;
    DropCourseGui(StudentGui student){
        this.student = student;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            student.buildDropCourse();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
