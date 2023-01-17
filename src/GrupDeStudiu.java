import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.sql.*;
import java.util.Vector;

public class GrupDeStudiu {
    Connection connection = new Driver().getConn();
    private int id;
    private int cursId;
    public int min, max;
    Date data;
    Time ora;
    private int durata;
    private int contor;
    Vector<String> message;

    GrupDeStudiu(int cursId, int min, int max, Time time, Date data, int durata){
        //dau cursul, si creez grupul de studiu pentru el
        this.cursId = cursId;
        this.min = min;
        this.max = max;
        this.ora  = time;
        this.data  = data;
        this.durata = durata;
    }

    public void create(){
        try{
            PreparedStatement prstm = connection.prepareStatement("INSERT INTO grup_studiu(CURS, MIN, MAX, DATE_TIME, ORA, DURATA, CONTOR) values(?,?,?,?,?,?,1)");
            prstm.setInt(1,cursId);
            prstm.setInt(2,min);
            prstm.setInt(3,max);
            prstm.setDate(4, data);
            prstm.setTime(5, ora);
            prstm.setInt(6,durata);
            prstm.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

}
