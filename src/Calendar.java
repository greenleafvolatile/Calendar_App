import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Logger;

public class Calendar extends JFrame {

    private final static long serialVersionUID=1L;

    protected static final LocalDate CURRENT_DATE=LocalDate.now(); // Would rather that currentDate not be static. Will leave static for now until I think of another solution.

    private Calendar() {
        this.initGui();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    private void initGui(){
        final DayOfWeek DEFAULT_FIRST_DAY_OF_WEEK=DayOfWeek.MONDAY;
        MonthView monthView=new MonthView(CURRENT_DATE, DEFAULT_FIRST_DAY_OF_WEEK);
        JPanel buttonPanel=new ButtonPanel(monthView.getDayTile().getPreferredSize(), this);

        // The content pane of a JFrame is by default a JPanel with a BorderLayout layout.
        this.getContentPane().add(monthView, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.WEST);
        this.addMenuBar();
        this.pack();
    }

    private void addMenuBar(){
        JMenuBar menuBar=new JMenuBar();
        JMenu calendarMenu=new JMenu("Settings");
        menuBar.add(calendarMenu);
        this.setJMenuBar(menuBar);
    }


    public static void main (String[]args) {
        try{
            String className=UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(className);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        EventQueue.invokeLater(() -> new Calendar());
    }
}
