import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class StudentGui  {
    Connection connection = new Driver().getConn();
    int studentId;
    private final JFrame student ;

    JTable tableCursuri;
    private JComboBox<String> cb;

    StudentGui( int studentId) throws SQLException {
        this.studentId = studentId;
        student = new GUI(800, 600).createFrame(studentId);
        student.setVisible(false);
    }


    //TO DO : inlocuire statement cu preparedStatement
    public void evaluateInfo() throws SQLException {
        //query al tabelei student
        try {
            String sql = "SELECT * FROM STUDENT WHERE ID_STUDENT = ";
            sql = sql.concat(Integer.toString(studentId));
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void displayGUI() throws SQLException {
        //App icon
        ImageIcon icon  = new ImageIcon("resources/StudentIcon.png");
        student.setIconImage(icon.getImage());
        JPanel mainPanel = new JPanel();

        JMenu curs = new JMenu("Cursuri");
        JMenuBar bar = new JMenuBar();

        JMenuItem renuntareCurs = new JMenuItem("Renuntare");
        JMenuItem adaugareCurs = new JMenuItem("Inscriere");

        student.setJMenuBar(bar);

        adaugareCurs.addActionListener(new JoinCourseGui(this));
        curs.add(adaugareCurs);
        curs.add(renuntareCurs);
        bar.add(curs);

        JMenu grup = new JMenu("Grupuri de studiu");
        JMenuItem afisareGrupuri = new JMenuItem("Listare grupuri");
        JMenuItem inscriereGrup  = new JMenuItem("Inscriere");
        JMenuItem renuntareGrup = new JMenuItem("Renuntare");
        grup.add(afisareGrupuri);
        grup.add(inscriereGrup);
        grup.add(renuntareGrup);
        bar.add(grup);

        Statement statement = connection.createStatement();
        PreparedStatement prs = connection.prepareStatement("select* from intermediar_stud_curs left join curs on intermediar_stud_curs.ID_CURS = curs.curs_id where ID_STUDENT = ?");
        prs.setInt(1,studentId);
        ResultSet result = prs.executeQuery();
        tableCursuri = new JTable(buildTableModel(result));
        tableCursuri.setPreferredScrollableViewportSize(tableCursuri.getPreferredSize());
        tableCursuri.setFillsViewportHeight(true);
        mainPanel.add(new JScrollPane(tableCursuri));
        student.setResizable(false);
        student.setContentPane(mainPanel);
        student.setVisible(true);
    }

    public void setVisibility(boolean viz){
        this.student.setVisible(viz);
    }

    public int getStudentId() {
        return studentId;
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 3; column < columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 3; columnIndex < columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }

    public Vector<String> retrieveData(ResultSet rs) throws SQLException {
        Vector<String> data = new Vector<>();
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();

        for(int i=1;i<=count;i++){
            if(rs.next())
                data.add(rs.getString("descriere"));
        }
        return data;
    }

    public void buildComboBox() throws SQLException {
        JFrame f = new JFrame();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JButton btn ;
        PreparedStatement prep = connection.prepareStatement("select * from curs where not exists(select * from intermediar_stud_curs where ID_STUDENT=? and intermediar_stud_curs.ID_CURS = curs.curs_id)");
        prep.setInt(1,studentId);
        ResultSet rs = prep.executeQuery();
        Vector<String>cursVector = retrieveData(rs);
        cb = new JComboBox<>(cursVector);
        cb.setLayout(null);
        cb.setBounds(50, 75, 200, 30);
        panel1.add(cb);
        btn = new JButton("Inscriere");
        btn.setLayout(null);
        btn.addActionListener(new JoinCourseBtn(cursVector, studentId, this));
        panel2.add(btn);
        panel3.add(panel1);
        panel3.add(panel2);
        f.setVisible(true);

        f.add(panel3);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

    public JComboBox<String> getCb() {
        return cb;
    }
}
