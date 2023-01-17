import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalTime;
import java.util.Vector;

public class ProfesorGui {
    Connection connection = new Driver().getConn();
    int profesorId;

    public enum TYPE{
        JOIN,
        DROP
    }
    JComboBox<String> cb;
    private JFrame profesor ;


    ProfesorGui( int profesorId) throws SQLException {
        this.profesorId = profesorId;
        profesor = new GUI(800, 600).createFrame(profesorId);
        profesor.setVisible(false);

    }

    public void evaluateInfo() throws SQLException {

    }

    //da display la GUI
    public void displayGUI() throws SQLException {
        ImageIcon icon  = new ImageIcon("resources/TeacherIcon.png");
        profesor.setIconImage(icon.getImage());
        JPanel mainPanel = new JPanel();
        PreparedStatement prep = connection.prepareStatement("select* from intermediar_prof_curs left join curs on intermediar_prof_curs.ID_CURS = curs.curs_id where ID_PROFESOR = ?");
        prep.setInt(1,profesorId);
        ResultSet rs = prep.executeQuery();
        JTable cursuri = new JTable(buildTableModel(rs));
        cursuri.setPreferredScrollableViewportSize(cursuri.getPreferredSize());
        cursuri.setFillsViewportHeight(true);
        mainPanel.add(new JScrollPane(cursuri));

        JMenu menu = new JMenu("Catalog");
        JMenuBar bar = new JMenuBar();
        JMenuItem descarcare = new JMenuItem("Descarcare");
        menu.add(descarcare);
        bar.add(menu);

        JMenu menuCurs = new JMenu("Cursuri");
        JMenuItem setareProcent  = new JMenuItem("Setare procentaje");
        JMenuItem renuntareCurs = new JMenuItem("Renuntare");
        JMenuItem inrolareCurs = new JMenuItem("Predare");
        setareProcent.addActionListener(new ProffesorSetPercentageListener(this));
        renuntareCurs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    buildCourseList("select * from curs where  exists(select * from intermediar_prof_curs where ID_PROFESOR=? and intermediar_PROF_curs.ID_CURS = curs.curs_id)"
                            , TYPE.DROP.ordinal());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        inrolareCurs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    buildCourseList("select * from curs where not exists(select * from intermediar_prof_curs where ID_PROFESOR=? and intermediar_PROF_curs.ID_CURS = curs.curs_id)"
                            , TYPE.JOIN.ordinal());
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        });

        menuCurs.add(setareProcent);
        menuCurs.add(renuntareCurs);
        menuCurs.add(inrolareCurs);
        bar.add(menuCurs);
        profesor.setJMenuBar(bar);
        profesor.setResizable(false);
        profesor.setContentPane(mainPanel);
        profesor.setVisible(true);
    }

    //verificare daca un profesor preda la un curs deja
    public boolean checkCourse(int cursId){
        try{
            Statement statement = connection.createStatement();
            String sql = "select * from intermediar_prof_curs";
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                if(rs.getInt("ID_PROFESOR") == profesorId){
                   if(rs.getInt("ID_CURS") == cursId){
                       return true;
                   }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();

        }
        return false;
    }

    //adaugare profesor la curs
    public void joinCourse(int cursId){
        if(checkCourse(cursId)) return;
        else{
            try{
                PreparedStatement pstmt = connection.prepareStatement("insert into intermediar_prof_curs(ID_PROFESOR, ID_CURS) values(?, ?)");
                pstmt.setInt(1,profesorId);
                pstmt.setInt(2,cursId);
                pstmt.executeUpdate();

            }catch(SQLException ex){
                ex.printStackTrace();
                System.out.println("eroare");
            }
        }
    }


    //renuntare curs
    public void dropCourse(int cursId){
        if(checkCourse(cursId)){
            try{
                PreparedStatement prep = connection.prepareStatement("DELETE FROM intermediar_prof_curs where ID_PROFESOR = ? AND ID_CURS = ?");
                prep.setInt(1,profesorId);
                prep.setInt(2,cursId);
                prep.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    //setare procente curs
    public void setPercentage(int seminar, int lab, int cursId) {
        if (checkCourse(cursId)) {
            try {
                PreparedStatement prep = connection.prepareStatement("update curs set seminar = ?, laborator=?, examen_curs = ? where curs_id = ?");
                prep.setInt(1, seminar);
                prep.setInt(2, lab);
                prep.setInt(3, 100 - seminar - lab);
                prep.setInt(4, cursId);
                prep.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    public boolean checkTimeTable(Date d, Time t){
        try{
            PreparedStatement prstm = connection.prepareStatement("select * from curs");
            ResultSet rs= prstm.executeQuery();
            while(rs.next()){
                int cursId = rs.getInt("curs_id");
                int timeId = rs.getInt("TIME_TABLE");
                prstm = connection.prepareStatement("select * from time_table where ID_TT = ?");
                prstm.setInt(1,timeId);
                ResultSet temp = prstm.executeQuery();
                while(temp.next()){
                    if(temp.getDate("DATE_A").before(d) &&
                    temp.getDate("DATE_B").after(d)){
                        Time actual = temp.getTime("ORA");
                        t = Time.valueOf(t.toLocalTime());
                        LocalTime a = actual.toLocalTime();
                        LocalTime b = t.toLocalTime();
                        if(a.minusHours(2).isBefore(b) || a.plusHours(2).isAfter(b)){
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Time parseTime( LocalTime hour){
        return new Time(hour.getHour(), hour.getMinute(), hour.getSecond());
    }

    //setare data curs
    public void setDate(int cursId,Date dateA, Date dateB,LocalTime hour){
        Time time = parseTime(hour);
        if(checkCourse(cursId)){
            //introducere in time_table
            String sql = "INSERT INTO time_table(DATE_B, DATE_A, ORA) VALUES (?,?,?)";
            try {
                PreparedStatement prep = connection.prepareStatement(sql);
                prep.setDate(1,dateB);
                prep.setDate(2,dateA);
                prep.setTime(3, time);
                prep.executeUpdate();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM TIME_TABLE");
                int index=0;
                //cautare in time_table pentru index
                while(rs.next()){
                    if(rs.getDate("DATE_B").equals(dateB) &&
                            rs.getDate("DATE_A").equals(dateA) &&
                    rs.getTime("ORA").equals(time)){
                        index = rs.getInt("ID_TT");
                    }
                }

                //setare TT la cursul dorit
                sql = "UPDATE curs SET TIME_TABLE = ? WHERE curs_id = ?";
                prep = connection.prepareStatement(sql);
                prep.setInt(1,index);
                prep.setInt(2, cursId);
                prep.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
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
    public void buildCourseList(String sql, int def) throws SQLException {
        JFrame f = new JFrame();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JButton btn ;
        PreparedStatement prep = connection.prepareStatement(sql);
        prep.setInt(1,profesorId);
        ResultSet rs = prep.executeQuery();
        Vector<String>cursVector = retrieveData(rs);
        this.cb = new JComboBox<>(cursVector);
        cb.setLayout(null);
        cb.setBounds(50, 75, 200, 30);
        panel1.add(cb);
        btn = new JButton();
        btn.setLayout(null);
        if(def == TYPE.JOIN.ordinal()) {
            btn.setText("Predare Curs");
            btn.addActionListener(new ProfessorJoinCourseBtn(this));
        }else{
            btn.setText("Renuntare Curs");
            btn.addActionListener(new ProffessorDropCourseBtn(this));

        }
        panel2.add(btn);
        panel3.add(panel1);
        panel3.add(panel2);

        f.add(panel3);
        f.setSize(300, 300);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
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

    public int getCourseId(String description, int def){
        try{
            PreparedStatement prstm = connection.prepareStatement("select curs_id from curs where descriere = ?");
            prstm.setString(1,description);
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

    public JComboBox<String> getCb() {
        return cb;
    }
}
