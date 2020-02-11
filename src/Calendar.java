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

    private JPanel previousTile;
    private LocalDate date;
    private DayOfWeek firstDayOfTheWeek;
    private JFrame calendarFrame;

    public Calendar() {
        calendarFrame=new JFrame();
        firstDayOfTheWeek=DayOfWeek.MONDAY;
        date=LocalDate.now();
        calendarFrame.getContentPane().setLayout(new BorderLayout());
        calendarFrame.add(createCenterPanel(), BorderLayout.CENTER);
        calendarFrame.add(createNorthPanel(), BorderLayout.NORTH);
        createMenuBar();
        calendarFrame.pack();
        calendarFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        calendarFrame.setLocationRelativeTo(null);
        calendarFrame.setVisible(true);
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
                // I need to get the height of dayNumber to determine the y-coordinate for placing it precisely in the upper left
                // corner of the tile.
                Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(String.valueOf(dayNumber), g2d);
                g2d.drawString(String.valueOf(dayNumber), 0, (int) Math.ceil(stringBounds.getHeight()));
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 100); // To do: get rid of magic numbers.
            }

            public int getDayNumber() {
                return this.dayNumber;
            }
        }

        int rows =getNrOfWeeksInMonth(YearMonth.of(date.getYear(), date.getMonth()), firstDayOfTheWeek);
        int columns=7; // 7 days in a week. One week per row.
        JPanel centerPanel=new JPanel(new GridLayout(rows, columns));

        int nrOfDaysInCurrentMonth=date.lengthOfMonth();

        int firstDayOfMonthWeekValue=date.withDayOfMonth(1).getDayOfWeek().getValue();

        int prevMonthNrOfDays=YearMonth.of(date.getYear(), getPreviousMonth(date.getMonthValue())).lengthOfMonth();

        int prevMonthLastDayOfMonthWeekValue=LocalDate.of(date.getYear(), getPreviousMonth(date.getMonthValue()), prevMonthNrOfDays).getDayOfWeek().getValue();

        int dayNumber=0;
        boolean isPrevMonth=false;
        boolean isNextMonth=false;
        if(firstDayOfMonthWeekValue==firstDayOfTheWeek.getValue()){
            dayNumber=1;
        }
        else{
            dayNumber=prevMonthNrOfDays-getDifferenceInDays(firstDayOfTheWeek.getValue(), prevMonthLastDayOfMonthWeekValue);
            isPrevMonth=true;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Tile tile = new Tile(dayNumber);
                if(!isPrevMonth && !isNextMonth && tile.getDayNumber()==date.getDayOfMonth()){
                    tile.setBorder(BorderFactory.createLineBorder(Color.RED));
                    previousTile=tile;
                }
                else{
                    tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }
                // When a tile is selected with a mouse click that tile should get the red border.
                tile.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent me){
                        JPanel tile=(JPanel)me.getSource();
                        tile.setBorder(BorderFactory.createLineBorder(Color.RED));
                        previousTile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        previousTile=tile;
                    }
                });
                centerPanel.add(tile);
                dayNumber++;

                if(isPrevMonth && dayNumber>prevMonthNrOfDays){
                    dayNumber=1;
                    isPrevMonth=false;
                }
                else if(!isPrevMonth && dayNumber>nrOfDaysInCurrentMonth){
                    dayNumber=1;
                    isNextMonth=true;
                }
            }
        }
        return centerPanel;
    }


    private JPanel createNorthPanel(){

        String[] daysOfTheWeek={"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        //JPanel northPanel=new JPanel(new GridLayout(2, 1));
        //JPanel topPanel=new JPanel(new GridLayout(1, 1));
        JPanel bottomPanel=new JPanel(new GridLayout(1, 7));


        int dayOfTheWeek;
        int nrOfIterations;
        if(firstDayOfTheWeek==DayOfWeek.MONDAY) {

            dayOfTheWeek=firstDayOfTheWeek.getValue()-1;
            nrOfIterations=0;
            while(nrOfIterations<7){
                bottomPanel.add(new JLabel(daysOfTheWeek[dayOfTheWeek]));
                nrOfIterations++;

            }
            /*bottomPanel.add(new JLabel("Monday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Tuesday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Wednesday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Thursday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Friday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Saturday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Sunday", SwingConstants.CENTER));*/
        }
        else if(firstDayOfTheWeek==DayOfWeek.SUNDAY){

            dayOfTheWeek=DayOfWeek.SUNDAY.getValue()-1;
            nrOfIterations=0;
            while(nrOfIterations<7){
                bottomPanel.add(new JLabel(daysOfTheWeek[dayOfTheWeek]));
                if(dayOfTheWeek==DayOfWeek.SUNDAY.getValue()){
                       dayOfTheWeek=1;
                }
                nrOfIterations++;
            }
            /*bottomPanel.add(new JLabel("Sunday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Monday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Tuesday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Wednesday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Thursday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Friday", SwingConstants.CENTER));
            bottomPanel.add(new JLabel("Saturday", SwingConstants.CENTER));*/
        }
        //northPanel.add(topPanel);
        //northPanel.add(bottomPanel);
        return bottomPanel;
    }

    private void createMenuBar(){
        JMenuBar menuBar=new JMenuBar();
        JMenu calendarMenu=new JMenu("Settings");
        menuBar.add(calendarMenu);
        calendarFrame.setJMenuBar(menuBar);
    }

    /**
     * This method determines the number of weeks in a specific month (should perhaps be in a different Utils Class.
     * @param yearMonth a specific year-month combination. Since sometimes a month has a different number of days depending
     * on which year it is the parameter can not just be a month.
     * @param firstDayOfWeek the desired first day of the week. The number of weeks in a month is dependent on the first
     * day of the week.
     * @return the number of days in the week.
     */
    private int getNrOfWeeksInMonth(YearMonth yearMonth, DayOfWeek firstDayOfWeek){
        int nrOfWeeksInMonth=0;

        // Provide an integer value for the last day of the week.
        int lastDayOfWeek=firstDayOfWeek.getValue()-1==0?7:firstDayOfWeek.getValue()-1;

        // Determine whether the first day of the month is a monday or a tuesday or etc..
        int firstWeekDayOfMonth=yearMonth.atDay(1).getDayOfWeek().getValue();

        // Determine how many days the month has.
        int nrOfDaysInCurrentMonth=yearMonth.lengthOfMonth();

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
            nrOfDaysInCurrentMonth-=nrOfDaysInFirstWeek;
            // Increase the number of weeks in the month by one.
            nrOfWeeksInMonth++;
        }
        // If the first day of the month is also the preferred first day of the week then the number of weeks in the month
        // is equal to the number of days in the month divided by 7 if it's a multiple of 7, else you have to add 1
        // (e.g. if the preferred first day of the week is Sunday and the first day of February is a Sunday and February
        // has 28 days, there'd be 28/7=4 weeks in February. If February has 29 days there'd be 29/7=4 + 1 weeks.
        nrOfWeeksInMonth+=nrOfDaysInCurrentMonth%7==0?nrOfDaysInCurrentMonth/7:nrOfDaysInCurrentMonth/7+1;

        return nrOfWeeksInMonth;
    }


    /**
     * This methods returns the number of days between the value of the first day of the week and the week value of the first day of the month.
     * @param firstDayOfWeek the preferred first day of the week.
     * @param prevMonthLastDayOfMonthValue the week value of the last day of the previous month.
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
