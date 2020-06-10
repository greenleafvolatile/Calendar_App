import java.time.LocalDateTime;

public class Event {

    private LocalDateTime startDateAndTime, endDateAndTime;
    private String title;
    private int id;

    public Event(String aTitle){
        this.title=aTitle;

    }

    public String getTitle(){
        return this.title;
    }

}
