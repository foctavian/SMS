import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalTime;
import java.util.Vector;

public class ProfesorGui {
    Connection connection = new Driver().getConn();
    int profesorId;

    private JFrame profesor ;


    ProfesorGui( int profesorId) throws SQLException {
        this.profesorId = profesorId;
        profesor = new GUI(800, 600).createFrame(profesorId);
        profesor.setVisible(false);

    }
    //TO DO: evaluarea informatiilor legate de un profesor : la ce cursuri preda, nume, prenume, va fi folosita
    // pentru a afisa dupa log in activitatile din ziua curenta
    public void evaluateInfo() throws SQLException {

    }

    //da display la GUI
    public void displayGUI() throws SQLException {
        ImageIcon icon  = new ImageIcon("resources/TeacherIcon.png");
        profesor.setIconImage(icon.getImage());
        JPanel mainPanel = new JPanel();
        Statement statement = connection.createStatement();
        PreparedStatement prep = connection.prepareStatement("select* from intermediar_prof_curs left join curs on intermediar_prof_curs.ID_CURS = curs.curs_id where ID_PROFESOR = ?");
        prep.setInt(1,profesorId);
        ResultSet rs = prep.executeQuery();
        JTable cursuri = new JTable(buildTableModel(rs));
        cursuri.setPreferredScrollableViewportSize(cursuri.getPreferredSize());
        cursuri.setFillsViewportHeight(true);
        mainPanel.add(new JScrollPane(cursuri));

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

}
