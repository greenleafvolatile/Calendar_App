import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Calendar extends JFrame {

    private DayOfWeek preferredFirstDayOfTheWeek;
    private CalendarView view;

    private Calendar() {
        LocalDate date = LocalDate.now();
        preferredFirstDayOfTheWeek=DayOfWeek.MONDAY;
        view= new MonthView(date, preferredFirstDayOfTheWeek);
        this.getContentPane().setLayout(new BorderLayout());
        this.add(new MonthView(date, preferredFirstDayOfTheWeek), BorderLayout.CENTER);
        this.addMenuBar();
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addMenuBar(){
        JMenuBar menuBar=new JMenuBar();
        JMenu calendarMenu=new JMenu("Settings");
        menuBar.add(calendarMenu);
        this.setJMenuBar(menuBar);
    }

    public static void main (String[]args){
        SwingUtilities.invokeLater(() -> new Calendar());
    }
}
