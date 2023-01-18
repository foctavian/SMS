import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

public class StudentGui  {
    Connection connection = new Driver().getConn();
    int studentId;
    private final JFrame student ;
    JTable tableCursuri;
    private JComboBox<String> cb;
    private JComboBox<String> cbd;

    StudentGui( int studentId) throws SQLException {
        this.studentId = studentId;
        student = new GUI(900, 600).createFrame(studentId);
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
        renuntareCurs.addActionListener(new DropCourseGui(this));
        curs.add(adaugareCurs);
        curs.add(renuntareCurs);
        bar.add(curs);

        JMenu grup = new JMenu("Grupuri de studiu");
        JMenuItem afisareGrupuri = new JMenuItem("Listare grupuri");
        JMenuItem inscriereGrup  = new JMenuItem("Inscriere");
        JMenuItem renuntareGrup = new JMenuItem("Renuntare");
        JMenuItem creareGrup = new JMenuItem("Creare");
        grup.add(afisareGrupuri);
        grup.add(inscriereGrup);
        grup.add(renuntareGrup);
        grup.add(creareGrup);
        inscriereGrup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buildJoinGS();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        creareGrup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buildCreateGS();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        renuntareGrup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buildDropGS();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
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

        f.add(panel3);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

    public void buildDropCourse() throws SQLException {
        JFrame f = new JFrame();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JButton btn ;
        PreparedStatement prep = connection.prepareStatement("select * from curs where exists (select * from intermediar_stud_curs where ID_STUDENT = ? and intermediar_stud_curs.ID_CURS = curs.curs_id)");
        prep.setInt(1,studentId);
        ResultSet rs = prep.executeQuery();
        Vector<String>cursVector = retrieveData(rs);
        cbd = new JComboBox<>(cursVector);
        cbd.setLayout(null);
        cbd.setBounds(50, 75, 200, 30);
        panel1.add(cbd);
        btn = new JButton("Renuntare");
        btn.setLayout(null);
        btn.addActionListener(new DropCourseBtn(cursVector, studentId, this));
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

    public void buildCreateGS() throws SQLException{
        JTextField curs = new JTextField();
        JTextField min = new JTextField();
        JTextField max = new JTextField();
        JTextField durata = new JTextField();
        JTextField ora = new JTextField();
        JTextField data = new JTextField("FORMAT : Y-M-D");

        Object[] fields={
            "Curs", curs,
            "Min", min,
            "Max", max,
                "Durata", durata,
                "Data", data,
                "Ora", ora
        };
        int option  = JOptionPane.showConfirmDialog(null, fields, "Creare grup studiu",JOptionPane.OK_CANCEL_OPTION );

        if(option == JOptionPane.OK_OPTION){
            String numeCurs = curs.getText();
            int minim = Integer.parseInt(min.getText());
            int maxim = Integer.parseInt(max.getText());
            if(minim > maxim){
                JOptionPane.showMessageDialog(null, "Error!", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int d = Integer.parseInt(durata.getText());
            Time h = Time.valueOf(LocalTime.parse(ora.getText()));
            Date date = Date.valueOf(data.getText());
            Date temp =Date.valueOf(LocalDate.now());
            if(date.before(temp)){
                JOptionPane.showMessageDialog(null, "Error!", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(h.before(Time.valueOf(LocalTime.now()))){
                JOptionPane.showMessageDialog(null, "Error!", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PreparedStatement prstm = connection.prepareStatement("select * from curs where nume = ?");
            prstm.setString(1, numeCurs);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                int id = rs.getInt("curs_id");

                prstm = connection.prepareStatement("INSERT INTO grup_studiu(CURS,MIN,MAX,DATE_TIME,ORA,DURATA,CONTOR)" +
                        "values(?,?,?,?,?,?,1)");
                prstm.setInt(1,id);
                prstm.setInt(2,minim);
                prstm.setInt(3,maxim);
                prstm.setDate(4, date);
                prstm.setTime(5, h);
                prstm.setInt(6,d);
                prstm.executeUpdate();

                prstm = connection.prepareStatement("select * from grup_studiu where CURS = ?");
                prstm.setInt(1,id);
                ResultSet r = prstm.executeQuery();
                if(r.next()){
                    id = r.getInt("ID_GS");
                }
                prstm = connection.prepareStatement("insert into intermediar_stud_gs(ID_GS,ID_STUD) values(?,?)");
                prstm.setInt(1,id);
                prstm.setInt(2,studentId);
                prstm.executeUpdate();
            }
        }
    }

    public void buildShowGS(){

    }

    public void buildJoinGS() throws SQLException {
        JFrame f = new JFrame();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JButton btn ;
        PreparedStatement prep = connection.prepareStatement
                ("select * from curs where exists(select * from intermediar_stud_curs where ID_STUDENT = ? and curs_id  = ID_CURS);");
        prep.setInt(1,studentId);
        ResultSet rs = prep.executeQuery();
        Vector<Integer>cursVector = new Vector<>();
        //toate cursurile la care sunt inscris
        while(rs.next()){
            cursVector.add(rs.getInt("curs_id"));
        }
        Vector<String>gsVector = new Vector<>();
        //pentru fiecare curs la care sunt inscris
        for(Integer i:cursVector) {
            //verific daca exista un grup de studiu
            prep = connection.prepareStatement("SELECT * FROM grup_studiu WHERE CURS = ?");
            prep.setInt(1,i);
            ResultSet result = prep.executeQuery();
            gsVector.clear();
            if(result.next()){
                int curs = result.getInt("CURS");
                if(!checkEnrollmentGS(curs)) {
                    gsVector.add(getCourseName(curs));
                }
            }
        }
        cb = new JComboBox<>(gsVector);
        cb.setLayout(null);
        cb.setBounds(50, 75, 200, 30);
        panel1.add(cb);
        btn = new JButton("Inscriere");
        btn.setLayout(null);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Object temp = cb.getSelectedItem();
                    String curs = null;
                    if(temp!= null){
                        curs = temp.toString();
                    }
                    int cursId = getCourseId(curs,1);
                    int gsId = checkCourse(cursId);
                    if(gsId != -1){
                        PreparedStatement prstm = connection.prepareStatement("INSERT INTO intermediar_stud_gs(ID_GS, ID_STUD)" +
                                "values(?,?)");
                        prstm.setInt(1,gsId);
                        prstm.setInt(2,studentId);
                        prstm.executeUpdate();
                        prstm = connection.prepareStatement("SELECT * FROM grup_studiu where ID_GS = ?");
                        prstm.setInt(1,gsId);
                        ResultSet result = prstm.executeQuery();
                        int contor = 0;
                        if(result.next()) {
                            contor = result.getInt("CONTOR");
                        }
                        prstm = connection.prepareStatement("UPDATE grup_studiu SET CONTOR=? WHERE ID_GS = ?");
                        prstm.setInt(1,contor+1);
                        prstm.setInt(2,gsId);
                        prstm.executeUpdate();
                    }

                    f.dispose();
                    buildJoinGS();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        panel2.add(btn);
        panel3.add(panel1);
        panel3.add(panel2);

        f.add(panel3);
        f.setSize(300, 300);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

    public void buildDropGS() throws SQLException {
        JFrame f = new JFrame();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JButton btn ;
        PreparedStatement prep = connection.prepareStatement
                ("select * from (((intermediar_stud_curs left join grup_studiu on ID_CURS = CURS)inner join curs on CURS= curs_id)" +
                        "right join intermediar_stud_gs on ID_STUD = ?)");
        prep.setInt(1,studentId);
        ResultSet rs = prep.executeQuery();
        Vector<String>cursVector = retrieveData(rs);
        cb = new JComboBox<>(cursVector);
        cb.setLayout(null);
        cb.setBounds(50, 75, 200, 30);
        panel1.add(cb);
        btn = new JButton("Renuntare grup de studiu");
        btn.setLayout(null);
        btn.addActionListener(new DropGSListener(cursVector, studentId, this));
        panel2.add(btn);
        panel3.add(panel1);
        panel3.add(panel2);

        f.add(panel3);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

    public int getCourseId(String nume){
        try{
            PreparedStatement prstm = connection.prepareStatement("select curs_id from curs where nume = ?");
            prstm.setString(1,nume);
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

    public int getCourseId(String des, int x){
        try{
            PreparedStatement prstm = connection.prepareStatement("select curs_id from curs where descriere = ?");
            prstm.setString(1,des);
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

    public String getCourseName(int id){
        try{
            PreparedStatement prstm = connection.prepareStatement("select *from curs where curs_id = ?");
            prstm.setInt(1,id);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                return(rs.getString("descriere"));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            return null;
        }
        return null;
    }
    public int checkCourse(int cursId){
        try{
            PreparedStatement prstm =connection.prepareStatement("SELECT * FROM grup_studiu where CURS = ?");
            prstm.setInt(1,cursId);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                if(rs.getInt("MAX") > rs.getInt("CONTOR")) {
                    return rs.getInt("ID_GS");
                }else{
                    return -1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  -1;
    }

    public boolean checkEnrollmentGS(int id){
        try{
            int gs = checkCourse(id);
            PreparedStatement prstm = connection.prepareStatement("SELECT * FROM intermediar_stud_gs WHERE ID_STUD = ? AND ID_GS = ?");
            prstm.setInt(1,studentId);
            prstm.setInt(2,gs);
            ResultSet rs = prstm.executeQuery();
            if(rs.next()){
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public JComboBox<String> getCb() {
        return cb;
    }

    public JComboBox<String> getCbd() {
        return cbd;
    }
}
