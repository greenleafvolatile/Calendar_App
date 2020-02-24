import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.time.*;


public class MonthView extends CalendarView{

    private Tile previousTile;
    private DaysOfTheMonthGrid MonthGrid;
    private DayOfWeek firstDayOfTheWeek;
    private Month currentMonth;

    protected MonthView(LocalDate date, DayOfWeek preferredFirstDayOfTheWeek){
        super(date, preferredFirstDayOfTheWeek);
        this.firstDayOfTheWeek=preferredFirstDayOfTheWeek;
        this.MonthGrid=new DaysOfTheMonthGrid();
        this.setLayout(new BorderLayout());
        this.add(createNorthPanel(), BorderLayout.NORTH);
        this.add(MonthGrid, BorderLayout.CENTER);
    }

    class Tile extends JPanel{

        private final int dayNumber;

        Tile(int aDayNumber){
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
            return new Dimension(100, 100);
        }
    }

    /**
     * Placeholder.
     */
    private class DaysOfTheMonthGrid extends JPanel{


        private DaysOfTheMonthGrid() {
            this.createGrid();
        }

        private void createGrid(){

            final int nrOfDaysInWeek=7;
            final int nrOfWeeksInMonth = getNrOfWeeksInMonth(YearMonth.of(MonthView.this.getDate().getYear(), MonthView.this.getDate().getMonth()), firstDayOfTheWeek);

            this.setLayout(new GridLayout(nrOfWeeksInMonth, nrOfDaysInWeek));


            final int prevMonthNrOfDays=MonthView.this.getDate().minusMonths(1).lengthOfMonth();
            final int firstDayOfMonthWeekValue=getDate().withDayOfMonth(1).getDayOfWeek().getValue();
            final int nrOfDaysInCurrentMonth=getDate().lengthOfMonth();

            final int prevMonthLastDayOfMonthWeekValue=MonthView.this.getDate().minusMonths(1).getDayOfWeek().getValue();

            int aDayNumber;

            boolean isCurrentMonth;

            if(firstDayOfMonthWeekValue==firstDayOfTheWeek.getValue()){
                aDayNumber=1;
                isCurrentMonth=true;
            }
            else{
                aDayNumber=prevMonthNrOfDays-getDifferenceInDays(firstDayOfTheWeek.getValue(), prevMonthLastDayOfMonthWeekValue);
                isCurrentMonth=false;
            }
            for(int i = 0; i<nrOfWeeksInMonth; i++) {
                for (int j = 0; j<nrOfDaysInWeek; j++) {

                    Tile tile = new Tile(aDayNumber);

                    if (isCurrentMonth && aDayNumber == getDate().getDayOfMonth()) {
                        tile.setBorder(BorderFactory.createLineBorder(Color.RED));
                        previousTile = tile;
                    } else {
                        tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    }

                    tile.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent me) {
                            Tile clickedTile = (Tile) me.getSource();
                            if (clickedTile != previousTile) {
                                clickedTile.setBorder(BorderFactory.createLineBorder(Color.RED));
                                previousTile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                                previousTile = clickedTile;
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

    }

    private JPanel createNorthPanel(){

        String[] daysOfTheWeek={"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        JLabel monthLabel=new JLabel(String.format("%s %s", getDate().getMonth().toString(), getDate().getYear()), SwingConstants.CENTER);

        JPanel northPanel=new JPanel(new GridLayout(2, 1));
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



        topPanel.add(Box.createHorizontalStrut(MonthGrid.getPreferredSize().width/7));
        topPanel.add(previousMonthArrowButton);
        topPanel.add(Box.createHorizontalStrut(MonthGrid.getPreferredSize().width/7/2));

        topPanel.add(monthLabel);
        topPanel.add(Box.createHorizontalStrut(MonthGrid.getPreferredSize().width/7/2));
        topPanel.add(nextMonthArrowButton);
        topPanel.add(Box.createHorizontalStrut(MonthGrid.getPreferredSize().width/7));

        JPanel bottomPanel=new JPanel(new GridLayout(1, 7));


        int dayOfTheWeekIndex=firstDayOfTheWeek.getValue()-1;
        int nrOfIterations=0;
        if(firstDayOfTheWeek==DayOfWeek.MONDAY) {

            while(nrOfIterations<daysOfTheWeek.length){
                bottomPanel.add(new JLabel(daysOfTheWeek[dayOfTheWeekIndex]));
                nrOfIterations++;
                dayOfTheWeekIndex++;
            }
        }
        else if(firstDayOfTheWeek==DayOfWeek.SUNDAY){

            while(nrOfIterations<daysOfTheWeek.length){
                bottomPanel.add(new JLabel(daysOfTheWeek[dayOfTheWeekIndex]));
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

    private void setNewGrid(){
        MonthView.this.remove(MonthGrid);
        MonthGrid=new DaysOfTheMonthGrid();
        MonthView.this.add(MonthGrid, BorderLayout.CENTER);
        MonthView.this.revalidate();
    }

    private void setNewLabel(JLabel monthLabel){
        monthLabel.setText(String.format("%s %s", getDate().getMonth().toString(), getDate().getYear()));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

}


