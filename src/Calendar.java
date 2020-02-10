import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.logging.Logger;

public class Calendar {

    private boolean isFirstSelection=true;
    private JPanel previousTile;
    LocalDate date=LocalDate.of(2019, Month.DECEMBER, 22);
    DayOfWeek firstDayOfTheWeek=DayOfWeek.MONDAY;

    public Calendar() {
        JFrame frame=new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(createCenterPanel(), BorderLayout.CENTER);
        frame.add(createTopPanel(), BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public static void main (String[]args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Calendar();
            }
        });
    }

    private JPanel createCenterPanel() {

        // Each day of the week is represented by a tile in a grid of 7 x the number of weeks in a particular month.
        class Tile extends JPanel {

            private int dayNumber;

            public Tile(int aDayNumber) {
                dayNumber = aDayNumber;
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(String.valueOf(dayNumber), g2d);
                g2d.drawString(String.valueOf(dayNumber), 0, (int) Math.ceil(stringBounds.getHeight()));
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }

            public int getDayNumber() {
                return this.dayNumber;
            }
        }




        int rows =getNrOfWeeksInMonth(YearMonth.of(date.getYear(), date.getMonth()), firstDayOfTheWeek);
        //Logger.getGlobal().info("Rows: " + rows);
        int columns=7; // 7 days in a week. One week per row.
        JPanel centerPanel=new JPanel(new GridLayout(rows, columns));

        int nrOfDaysInMonth=date.lengthOfMonth(); //date.getMonth().maxLength();
        //Logger.getGlobal().info("Number of days in month: " + nrOfDaysInMonth);

        int firstDayOfMonthWeekValue=date.withDayOfMonth(1).getDayOfWeek().getValue();
        Logger.getGlobal().info("First day of the month week value: " + firstDayOfMonthWeekValue);

        int prevMonthNrOfDays=YearMonth.of(date.getYear(), getPreviousMonth(date.getMonthValue())).lengthOfMonth();
        Logger.getGlobal().info("Previous month number of days: " + prevMonthNrOfDays);

        int prevMonthLastDayOfMonthWeekValue=LocalDate.of(date.getYear(), getPreviousMonth(date.getMonthValue()), prevMonthNrOfDays).getDayOfWeek().getValue();

        int dayNumber=firstDayOfMonthWeekValue==firstDayOfTheWeek.getValue()?1:prevMonthNrOfDays-getDifferenceInDays(firstDayOfTheWeek.getValue(), prevMonthLastDayOfMonthWeekValue);
        Logger.getGlobal().info("dayNumber: " + dayNumber);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                //Logger.getGlobal().info("" + calendar.get(Calendar.MONTH));
                Tile tile = new Tile(dayNumber);
                // Today's tile should have a red border.
                if(tile.getDayNumber()==date.getDayOfWeek().getValue()){
                    tile.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
                else{
                    tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }
                // When a tile is selected with a mouse click that tile should get the red border.
                tile.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent me){
                        JPanel tile=(JPanel)me.getSource();
                        tile.setBorder(BorderFactory.createLineBorder(Color.RED));
                        if(!isFirstSelection){

                            previousTile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            previousTile=tile;
                        }
                        else if(isFirstSelection){
                            previousTile=tile;
                            isFirstSelection=false;
                        }
                    }
                });
                centerPanel.add(tile);
                dayNumber++;
                if(dayNumber>nrOfDaysInMonth){
                    dayNumber=1;
                }
            }
        }
        return centerPanel;
    }


    private JPanel createTopPanel(){

        JPanel panel=new JPanel(new GridLayout(1, 7));
        panel.add(new JLabel("Monday", SwingConstants.CENTER));
        panel.add(new JLabel("Tuesday", SwingConstants.CENTER));
        panel.add(new JLabel("Wednesday", SwingConstants.CENTER));
        panel.add(new JLabel("Thursday", SwingConstants.CENTER));
        panel.add(new JLabel("Friday", SwingConstants.CENTER));
        panel.add(new JLabel("Saturday", SwingConstants.CENTER));
        panel.add(new JLabel("Sunday", SwingConstants.CENTER));
        return panel;
    }

    private int getNrOfWeeksInMonth(YearMonth yearMonth, DayOfWeek firstDayOfWeek){
        int nrOfWeeksInMonth=0;

        // Provide an integer value for the last day of the week.
        int lastDayOfWeek=firstDayOfWeek.getValue()-1==0?7:firstDayOfWeek.getValue()-1;

        // Determine whether the first day of the month is a monday or a tuesday or etc..
        int firstWeekDayOfMonth=yearMonth.atDay(1).getDayOfWeek().getValue();

        // Determine how many days the month has.
        int nrOfDaysInMonth=yearMonth.lengthOfMonth();

        // If the first day of the month is not also the preferred first day of the week, determine how many days are in
        // the first week.
        if (!(firstWeekDayOfMonth == firstDayOfWeek.getValue())) {
            int nrOfDaysInFirstWeek=1;
            while(!(firstWeekDayOfMonth==lastDayOfWeek)){
                if(firstWeekDayOfMonth==DayOfWeek.SUNDAY.getValue()){
                    firstWeekDayOfMonth=0;
                }
                firstWeekDayOfMonth++;
                nrOfDaysInFirstWeek++;
            }
            // Subtract the number of days in the first week from the total number of days in the month.
            nrOfDaysInMonth-=nrOfDaysInFirstWeek;
            // Increase the number of weeks in the month by one.
            nrOfWeeksInMonth++;
        }
        // If the first day of the month is also the preferred first day of the week then the number of weeks in the month
        // is equal to the number of days in the month divided by 7 if it's a multiple of 7, else you have to add 1
        // (e.g. if the preferred first day of the week is Sunday and the first day of February is a Sunday and February
        // has 28 days, there'd be 28/7=4 weeks in February. If February has 29 days there'd be 29/7=4 + 1 weeks.
        nrOfWeeksInMonth+=nrOfDaysInMonth%7==0?nrOfDaysInMonth/7:nrOfDaysInMonth/7+1;

        return nrOfWeeksInMonth;
    }


    /**
     * This methods returns the number of days between the value of the first day of the week and the week value of the first day of the month.
     * @param firstDayOfWeek the preferred first day of the week, prevMonthLastDayOfMonthWeekValue the day-of-the-week value
     * of the previous month.
     * @return the difference in days.
     */
    private int getDifferenceInDays(int firstDayOfWeek, int prevMonthLastDayOfMonthValue){
        Logger.getGlobal().info("First day of the week value: " + firstDayOfTheWeek.getValue());
        int nrOfDays=0;
        while(!(firstDayOfWeek==prevMonthLastDayOfMonthValue)){
            if (firstDayOfWeek== DayOfWeek.SUNDAY.getValue()) {
                firstDayOfWeek=0;
            }
            nrOfDays++;
            firstDayOfWeek++;
        }

        Logger.getGlobal().info("Difference in days: " + nrOfDays);
        return nrOfDays;
    }

    private int getPreviousMonth(int monthValue){
        int previousMonth=0;
        if(monthValue==1){
            previousMonth=12;
        }
        else{
            previousMonth=monthValue-1;
        }
        return previousMonth;
    }
}
