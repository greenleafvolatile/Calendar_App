import java.sql.ResultSet;
import java.sql.Date;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

class DayView extends CalendarView {

    final static Dimension INITIAL_DIMENSION=new Dimension(100, 100);
    private CalendarView parentView;
    private ArrayList<Event> events;
    private boolean isSelected;

    DayView(LocalDate date, CalendarView parentView, boolean isSelected){

        super(date, parentView.getLocalPostgresConnection());
        this.events = new ArrayList<>();
        this.parentView=parentView;
        this.setSelected(isSelected);
        this.getEvents();

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent arg0){
                DayView.this.setSelected(true);
                CalendarView parent=DayView.this.parentView;
                DayView currentlySelectedView=(DayView) parent.getSelectedView();
                currentlySelectedView.setSelected(false);
                parent.setSelectedView(DayView.this);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(String.valueOf(this.getDate().getDayOfMonth()), g2d);
        g2d.drawString(String.valueOf(this.getDate().getDayOfMonth()), 0, (int) Math.ceil(stringBounds.getHeight()));
    }

    @Override
    public Dimension getPreferredSize(){ // Perhaps it shouldn't be the tiles that determine the size of the app. Perhaps the tiles should be made to fit into a frame of a certain size.
        return INITIAL_DIMENSION;
    }

    protected static Dimension getInitialDimension(){
        return INITIAL_DIMENSION;
    }

    protected void setSelected(boolean isSelected){
        if(isSelected) {
            this.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        else {
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    protected CalendarView getSelectedView(){
        return this;
    }

    protected void setSelectedView(CalendarView aView){
        // do nothing method. My design needs work.

    }

    private void getEvents() {
        Date date = Date.valueOf(this.getDate());
        String sql = "SELECT * FROM Event WHERE DATE(startDateAndTime)=? ORDER BY";  // I could order here with ORDER BY or I could add it all to a collection and let the user sort.
        try{
            PreparedStatement statement = this.getLocalPostgresConnection().prepareStatement(sql);
            statement.setObject(1, date);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Event event = new Event(resultSet.getInt(1), resultSet.getString(2), resultSet.getTimestamp(3).toLocalDateTime(), resultSet.getTimestamp(4).toLocalDateTime());

                events.add(event);


            }

            Logger.getGlobal().info("Size: " + events.size());


        }
        catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public String toString() {
        return this.getDate().toString();
    }



}
