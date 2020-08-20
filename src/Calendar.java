import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Calendar extends JFrame {

    private final static long serialVersionUID=1L;
    protected static final LocalDate CURRENT_DATE=LocalDate.now(); // Would rather that currentDate not be static. Will leave static for now until I think of another solution.

    private Calendar() {
        this.initGui();
    }

    private void initGui(){
        final DayOfWeek DEFAULT_FIRST_DAY_OF_WEEK=DayOfWeek.MONDAY;
        MonthView monthView=new MonthView(Calendar.CURRENT_DATE, DEFAULT_FIRST_DAY_OF_WEEK);

        // I want the buttons on the button panel to be the same size as the day tile. But day tile size changes depending on the number of weeks in a month. To-do.
        JPanel buttonPanel=new ButtonPanel();  // To-do: currently setResizable() == false. Do I still need to actively pass tile dimensions?

        this.getContentPane().add(monthView, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.LINE_START);

        this.addMenuBar();
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    private void addMenuBar(){
        JMenuBar menuBar=new JMenuBar();
        JMenu calendarMenu=new JMenu("Settings");
        menuBar.add(calendarMenu);
        this.setJMenuBar(menuBar);
    }

    public static void main (String[]args) {

        try {
            String className = UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(className);
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        EventQueue.invokeLater(() -> new Calendar());
    }
}
