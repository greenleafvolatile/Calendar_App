import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.time.*;


public class MonthView extends CalendarView{

    private DayTile tile;
    private DaysOfTheMonthGrid monthGrid;

    protected MonthView(LocalDate date, DayOfWeek aWeekDay){
        super(date, aWeekDay);
        this.monthGrid=new DaysOfTheMonthGrid();
        this.setLayout(new BorderLayout());
        this.add(createNorthPanel(), BorderLayout.PAGE_START);
        this.add(monthGrid, BorderLayout.CENTER);
    }

    static class DayTile extends JPanel{

        final Dimension INITIAL_DIMENSION=new Dimension(100, 100);
        private final int dayNumber;
        private boolean isSelected;

        DayTile(int aDayNumber){
            this.dayNumber=aDayNumber;
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(String.valueOf(dayNumber), g2d);
            g2d.drawString(String.valueOf(dayNumber), 0, (int) Math.ceil(stringBounds.getHeight()));
        }

        @Override
        public Dimension getPreferredSize(){ // Perhaps it shouldn't be the tiles that determine the size of the app. Perhaps the tiles should be made to fit into a frame of a certain size.
            return INITIAL_DIMENSION;
        }
    }


    /**
     * Placeholder.
     */
    private class DaysOfTheMonthGrid extends JPanel{

        static final int NR_OF_DAYS_IN_WEEK=7;
        final int nrOfWeeksInMonth;
        final int prevMonthNrOfDays;
        final int firstDayOfMonthValue;
        final int nrOfDaysInCurrentMonth;
        final int lastDayOfPrevMonthValue;
        DayTile selectedTile;
        int aDayNumber;
        boolean isCurrentMonth;


        private DaysOfTheMonthGrid() {
            nrOfWeeksInMonth = getNrOfWeeksInMonth(YearMonth.of(MonthView.this.getDate().getYear(), MonthView.this.getDate().getMonth()), getFirstDayOfTheWeek());
            Logger.getGlobal().info("nrOfWeeksInMonth: " + nrOfWeeksInMonth);
            prevMonthNrOfDays=MonthView.this.getDate().minusMonths(1).lengthOfMonth();
            Logger.getGlobal().info("prevMonthNrOfDays: " + prevMonthNrOfDays);
            firstDayOfMonthValue=MonthView.this.getDate().withDayOfMonth(1).getDayOfWeek().getValue();
            Logger.getGlobal().info("firstDayOfMonthValue: " + firstDayOfMonthValue);
            nrOfDaysInCurrentMonth=MonthView.this.getDate().lengthOfMonth();
            Logger.getGlobal().info("nrOfDaysInCurrentMonth: " + nrOfDaysInCurrentMonth);
            lastDayOfPrevMonthValue=getPrevMonthLastDayOfMonthWeekDayValue();
            this.initGui();
        }

        private void initGui(){
            this.setLayout(new GridLayout(nrOfWeeksInMonth, NR_OF_DAYS_IN_WEEK));

            // If the first day of the month is also the preferred first day of the week (which can be set by the user) then the first day tile in the month view will have day
            // number 1. Else the first day tile will have the number of the last day of the previous month that is also the preferred first day of the week.
            if(this.firstDayOfMonthValue==MonthView.this.getFirstDayOfTheWeek().getValue()){
                aDayNumber=1;
                isCurrentMonth=true;
            }

            // Else the first day tile will have the number of the last day of the previous month that is also the preferred first day of the week.
            else{
                aDayNumber=prevMonthNrOfDays-getDifferenceInDays(getFirstDayOfTheWeek().getValue(), lastDayOfPrevMonthValue);
                isCurrentMonth=false;
            }

            // Construct a grid of day tiles.
            for(int i = 0; i < nrOfWeeksInMonth; i++) {
                for (int j = 0; j < NR_OF_DAYS_IN_WEEK; j++) {

                    tile = new DayTile(aDayNumber);
                    // When month view GUI is first rendered the current date day tile will have a red border and that tile will be the selected tile..
                    if (MonthView.this.getDate().getMonth()==Calendar.CURRENT_DATE.getMonth() && aDayNumber == getDate().getDayOfMonth()) {
                        tile.setBorder(BorderFactory.createLineBorder(Color.RED));
                        selectedTile=tile;
                    } else {
                        tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    }

                    // If another tile is clicked with the mouse that tile will get a red border and be the selected tile. Also the previously selected tile will get a black border.
                    tile.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent mouseEvent) {
                            DayTile clickedDayTile = (DayTile) mouseEvent.getSource();
                            if(clickedDayTile!=selectedTile){
                                clickedDayTile.setBorder(BorderFactory.createLineBorder(Color.RED));
                                selectedTile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                                selectedTile = clickedDayTile;
                            }
                        }
                    });
                    this.add(tile);
                    aDayNumber++;

                    if (!isCurrentMonth && aDayNumber > prevMonthNrOfDays) {
                        aDayNumber = 1;
                        isCurrentMonth = true;
                    } else if (isCurrentMonth && aDayNumber > nrOfDaysInCurrentMonth) {
                        aDayNumber = 1;
                        isCurrentMonth = false;
                    }
                }
            }
        }

        /**
         * Counts the first few days of the month before reaching the desired starting day, then counts how many
         * full weeks there are, then adds 1 more week for any remaining days (should perhaps be in a different class, maybe a Utils Class).
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
            if (firstWeekDayOfMonth != firstDayOfWeek.getValue()) {
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
            // Divide the remaining days in the month by 7 to get the number weeks left in the month. Add 1 week if there is a remainder.
            nrOfWeeksInMonth+=nrOfDaysInCurrentMonth%7==0?nrOfDaysInCurrentMonth/7:nrOfDaysInCurrentMonth/7+1;

            return nrOfWeeksInMonth;
        }

        /**
         * This methods returns the number of days between the value of the first day of the week of the previous moth and the week value of the first day of the current month.
         * @param firstDayOfWeek the preferred first day of the week.
         * @param prevMonthLastDayOfMonthValue the week value of the last day of the previous month.
         * of the previous month.
         * @return the difference in days.
         */
        private int getDifferenceInDays(int firstDayOfWeek, int prevMonthLastDayOfMonthValue){
            int nrOfDays=0;
            while(!(firstDayOfWeek==prevMonthLastDayOfMonthValue)){
                if (firstDayOfWeek== DayOfWeek.SUNDAY.getValue()) {
                    firstDayOfWeek=0;
                }
                nrOfDays++;
                firstDayOfWeek++;
            }
            return nrOfDays;
        }

        private int getPrevMonthLastDayOfMonthWeekDayValue(){
            YearMonth ym=YearMonth.of(MonthView.this.getDate().getYear(), MonthView.this.getDate().getMonth().minus(1));
            int numberOfDaysInMonth=ym.lengthOfMonth();
            DayOfWeek lastDayOfMonth=LocalDate.of(ym.getYear(), ym.getMonth(), numberOfDaysInMonth).getDayOfWeek();
            return lastDayOfMonth.getValue();
        }
    }

    private JPanel createNorthPanel(){

        DayOfWeek[] daysOfTheWeek=DayOfWeek.values();

        JLabel monthLabel=new JLabel(String.format("%s %s", getDate().getMonth().toString(), getDate().getYear()), SwingConstants.CENTER);

        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel topPanel=new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        BasicArrowButton previousMonthArrowButton=new BasicArrowButton(BasicArrowButton.WEST);
        BasicArrowButton nextMonthArrowButton=new BasicArrowButton(BasicArrowButton.EAST);

        nextMonthArrowButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                MonthView.this.setDate(MonthView.this.getDate().plusMonths(1));

                setNewGrid();
                setNewLabel(monthLabel);
            }
        });

        previousMonthArrowButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                MonthView.this.setDate(MonthView.this.getDate().minusMonths(1));
                setNewGrid();
                setNewLabel(monthLabel);
            }
        });

        topPanel.add(Box.createHorizontalStrut(monthGrid.getPreferredSize().width/7));
        topPanel.add(previousMonthArrowButton);
        topPanel.add(Box.createHorizontalStrut(monthGrid.getPreferredSize().width/7/2));

        topPanel.add(monthLabel);
        topPanel.add(Box.createHorizontalStrut(monthGrid.getPreferredSize().width/7/2));
        topPanel.add(nextMonthArrowButton);
        topPanel.add(Box.createHorizontalStrut(monthGrid.getPreferredSize().width/7));

        JPanel bottomPanel=new JPanel(new GridLayout(1, 7));


        int dayOfTheWeekIndex=getFirstDayOfTheWeek().getValue()-1;
        int nrOfIterations=0;
        if(getFirstDayOfTheWeek()==DayOfWeek.MONDAY) {

            while(nrOfIterations<daysOfTheWeek.length){
                bottomPanel.add(new JLabel(daysOfTheWeek[dayOfTheWeekIndex].toString()));
                nrOfIterations++;
                dayOfTheWeekIndex++;
            }
        }
        else if(getFirstDayOfTheWeek()==DayOfWeek.SUNDAY){

            while(nrOfIterations<daysOfTheWeek.length){
                bottomPanel.add(new JLabel(daysOfTheWeek[dayOfTheWeekIndex].toString()));
                if(dayOfTheWeekIndex==DayOfWeek.SUNDAY.getValue()-1) {
                    dayOfTheWeekIndex=0;
                    nrOfIterations++;
                    continue;
                }
                nrOfIterations++;
                dayOfTheWeekIndex++;
            }
        }

        northPanel.add(topPanel);
        northPanel.add(bottomPanel);
        return northPanel;
    }

    /**
     * This method changes the month grid when the month is changed.
     */
    private void setNewGrid(){
        MonthView.this.remove(monthGrid);
        monthGrid=new DaysOfTheMonthGrid();
        MonthView.this.add(monthGrid, BorderLayout.CENTER);
        MonthView.this.revalidate();
    }

    /**
     * This method sets the month label when the month is changed.
     * @param monthLabel a JLabel with a month name as text.
     */
    private void setNewLabel(JLabel monthLabel){
        monthLabel.setText(String.format("%s %s", getDate().getMonth().toString(), getDate().getYear()));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    protected DayTile getDayTile(){
        return this.tile;
    }
}


