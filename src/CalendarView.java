import javax.swing.*;
import java.time.LocalDate;

public abstract class CalendarView extends JPanel {



    protected LocalDate date;

    protected CalendarView(LocalDate aDate){
        this.date=aDate;
    }

    protected void setDate(LocalDate aDate){
        this.date=aDate;
    }

    protected LocalDate getDate(){
        return this.date;
    }

}
