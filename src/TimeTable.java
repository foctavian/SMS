import java.sql.Date;
import java.sql.Time;

public class TimeTable {
    Date dateA,dateB;
    Time hour;
    int id;
    TimeTable(Date dateA, Date dateB, Time hour, int id){
        this.dateA = dateA;
        this.dateB = dateB;
        this.hour = hour;
        this.id = id;
    }
}
