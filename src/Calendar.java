import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Calendar {

    private DayOfWeek preferredFirstDayOfTheWeek;
    private final JFrame calendarFrame;

    private Calendar() {
        calendarFrame=new JFrame();
        LocalDate date = LocalDate.now();
        preferredFirstDayOfTheWeek=DayOfWeek.MONDAY;
        CalendarView monthView = new MonthView(date, preferredFirstDayOfTheWeek);
        calendarFrame.getContentPane().setLayout(new BorderLayout());
        calendarFrame.add(new MonthView(date, preferredFirstDayOfTheWeek), BorderLayout.CENTER);
        createMenuBar();
        calendarFrame.pack();
        calendarFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        calendarFrame.setLocationRelativeTo(null);
        calendarFrame.setVisible(true);
    }

    private void createMenuBar(){
        JMenuBar menuBar=new JMenuBar();
        JMenu calendarMenu=new JMenu("Settings");
        menuBar.add(calendarMenu);
        calendarFrame.setJMenuBar(menuBar);
    }

    public void setPreferredFirstDayOfTheWeek(DayOfWeek thePrefferedDayOfTheWeek){
        this.preferredFirstDayOfTheWeek=thePrefferedDayOfTheWeek;
    }

    public static void main (String[]args){
        SwingUtilities.invokeLater(() -> new Calendar());
    }
}
