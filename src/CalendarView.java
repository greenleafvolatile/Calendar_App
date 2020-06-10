import java.awt.Color;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.sql.Connection;

public abstract class CalendarView extends JPanel {

    private boolean isSelectable;
    private LocalDate selectedDate;
    private LocalDate date;
    private DayOfWeek firstDayOfTheWeek;
    private Connection localPostgresConnection;

    protected CalendarView(LocalDate aDate, DayOfWeek aWeekDay, Connection aLocalPostgresConnection){
        this.date=aDate;
        this.selectedDate=aDate;
        this.firstDayOfTheWeek=aWeekDay;
        this.localPostgresConnection=aLocalPostgresConnection;
    }

    protected CalendarView(LocalDate aDate, Connection aLocalPostgresConnection){
        this.date=aDate;
        this.localPostgresConnection=aLocalPostgresConnection;
    }

    protected void setSelectedDate(LocalDate aDate){
        this.selectedDate=aDate;
    }

    protected LocalDate getSelectedDate(){
        return this.selectedDate;
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

    protected Connection getLocalPostgresConnection(){
        return localPostgresConnection;
    }

    protected boolean getSelected(){
        return isSelectable;
    }

    protected void setSelected(boolean isSelected){
        if(isSelected){
            setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        else{
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    protected abstract CalendarView getSelectedView();

    protected abstract void setSelectedView(CalendarView aView);

}
