import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.time.*;
import java.sql.Connection;


public class MonthView extends CalendarView{

    private CalendarView selectedDay;
    private DaysOfTheMonthGrid monthGrid;

    protected MonthView(LocalDate date, DayOfWeek aWeekDay, Connection localPostgresConnection){
        super(date, aWeekDay, localPostgresConnection);
        this.initGui();
    }

    private void initGui(){
        this.monthGrid=new DaysOfTheMonthGrid();
        this.setLayout(new BorderLayout());
        this.add(createNorthPanel(), BorderLayout.PAGE_START);
        this.add(monthGrid, BorderLayout.CENTER);
    }

    private class DaysOfTheMonthGrid extends JPanel{


        static final int NR_OF_DAYS_IN_WEEK=7;
        final int nrOfWeeksInMonth;
        final int firstDayOfMonthValue;


        private DaysOfTheMonthGrid() {
            nrOfWeeksInMonth = getNrOfWeeksInMonth(YearMonth.of(MonthView.this.getDate().getYear(), MonthView.this.getDate().getMonth()), getFirstDayOfTheWeek());
            firstDayOfMonthValue=MonthView.this.getDate().withDayOfMonth(1).getDayOfWeek().getValue();
            this.initGui();
        }

        private void initGui(){
            this.setLayout(new GridLayout(nrOfWeeksInMonth, NR_OF_DAYS_IN_WEEK));

            LocalDate date;
            if(this.firstDayOfMonthValue==MonthView.this.getFirstDayOfTheWeek().getValue()){
                date =LocalDate.of(MonthView.this.getDate().getYear(), MonthView.this.getDate().getMonthValue(), 1);
                //isCurrentMonth=true;
            }
            else{
                date =YearMonth.from(MonthView.this.getDate()).minusMonths(1).atEndOfMonth().with(TemporalAdjusters.previousOrSame(MonthView.this.getFirstDayOfTheWeek()));
                //isCurrentMonth=false;
            }

            // Construct a grid of DayView objects.
            for(int i = 0; i < nrOfWeeksInMonth; i++) {
                for (int j = 0; j < NR_OF_DAYS_IN_WEEK; j++) {

                    CalendarView dayView;
                    if(date.equals(Calendar.CURRENT_DATE)){
                        dayView = new DayView(date, MonthView.this, true);
                        selectedDay= dayView;
                    }
                    else{
                        dayView = new DayView(date, MonthView.this, false);
                    }
                    this.add(dayView);
                    date = date.plusDays(1);
                }
            }
        }

        /**
         * Counts the first few days of the month before reaching the desired starting day, then counts how many
         * full weeks there are, then adds 1 more week for any remaining days (should perhaps be in a different class, maybe a Utils Class).
         * @param yearMonth a specific year-month combination. Since sometimes a month has a different number of days depending
         * on which year it is, the parameter can not just be a month.
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

    @Override
    protected CalendarView getSelectedView(){
        return selectedDay;
    }

    protected void setSelectedView(CalendarView aDayView){
        this.selectedDay=aDayView;
    }
}


