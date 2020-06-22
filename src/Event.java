import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event {

    private Date startDate, endDate;
    private String title;
    private int id;

    public Event(String aTitle, Date startDate, Date endDate){
        this.title=aTitle;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTitle(){
        return this.title;
    }

    public Date getStartDate(){
        return this.startDate;
    }

    public Date getEndDate(){
        return this.endDate;
    }

}
