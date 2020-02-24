import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Calendar extends JFrame {


    private Calendar() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek preferredFirstDayOfTheWeek=DayOfWeek.MONDAY;

        this.getContentPane().setLayout(new BorderLayout());

        this.add(new MonthView(currentDate, preferredFirstDayOfTheWeek), BorderLayout.CENTER);
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
