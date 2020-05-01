import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Logger;

public abstract class CalendarView extends JPanel {

    private LocalDate date;
    private DayOfWeek firstDayOfTheWeek;

    protected CalendarView(LocalDate aDate, DayOfWeek preferredFirstDayOfTheWeek){
        this.date=aDate;
        this.firstDayOfTheWeek=preferredFirstDayOfTheWeek;
    }

    protected void setDate(LocalDate aDate){
        this.date=aDate;
    }

    protected LocalDate getDate(){
        return this.date;
    }

    protected DayOfWeek getFirstDayOfTheWeek(){
        return this.firstDayOfTheWeek;
    }

    protected void setFirstDayOfTheWeek(DayOfWeek preferredFirstDayOfTheWeek){
        this.firstDayOfTheWeek=preferredFirstDayOfTheWeek;
    }
}
