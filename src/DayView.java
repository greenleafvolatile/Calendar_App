import java.awt.event.MouseListener;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Date;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
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
import java.util.List;

class DayView extends CalendarView {


    final static Dimension INITIAL_DIMENSION=new Dimension(100, 100);
    public static JList previousList = null;

    private CalendarView parentView;
    private List<Event> events;
    private DefaultListModel<Event> lm;

    private boolean isSelected;

    DayView(LocalDate date, CalendarView parentView, boolean isSelected){

        super(date, parentView.getLocalPostgresConnection());
        this.setLayout(new BorderLayout());
        this.events = new ArrayList<Event>();
        this.parentView=parentView;
        this.setSelected(isSelected);

        JLabel dateLabel = new JLabel(String.valueOf(this.getDate().getDayOfMonth()), SwingConstants.LEFT);
        dateLabel.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), 10));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.WHITE);
        this.add(dateLabel, BorderLayout.NORTH);

        lm = new DefaultListModel<Event>();
        JList<Event> list = new JList<Event>(lm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.setCellRenderer(new EventListRenderer());


        // Add a MouseListener so that when the user clicks on a specific date
        // that date will be the selected date with a red border.
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent mE) {
                maybeShowPopUp(mE);

            }

            @Override
            public void mouseReleased(MouseEvent mE) {
                maybeShowPopUp(mE);

            }
            @Override
            public void mouseClicked(MouseEvent mE){
                DayView.this.setSelected(true);
                CalendarView parent=DayView.this.parentView;
                DayView currentlySelectedView=(DayView) parent.getSelectedView();
                currentlySelectedView.setSelected(false);
                parent.setSelectedView(DayView.this);
            }

            private void maybeShowPopUp(MouseEvent mE) {

                int selectedIndex = list.getSelectedIndex();

                // I need the width in pixels of the text in a cell of the list for the location of the popup.
                // For that I need a FontMetrics object.
                EventListRenderer renderer = (EventListRenderer) list.getCellRenderer();
                Font font = renderer.getFont();
                FontMetrics fm = renderer.getFontMetrics(font);

                // And the text.
                String text = lm.getElementAt(selectedIndex).getTitle();

                // Get the width of the text.
                int width = fm.stringWidth(text);

                // The popup should only render when the user clicks on a selected cell.
                // To compare the location where the user clicked to the location of the selected cell, I need the
                // coordinates of that cell in the list.
                Rectangle bounds = list.getCellBounds(selectedIndex, selectedIndex);
                Point point = bounds.getLocation();

                if(mE.isPopupTrigger() && list.getSelectedIndex() == list.locationToIndex(mE.getPoint())) {

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem("Test");
                    menu.add(item);

                    // Show the popup in the JList at the end of the text in a selected cell, underneath that cell.
                    menu.show(list, (int) (point.getX() + width), (int) (point.getY() + bounds.getHeight()));

                }
            }
        });

        // Add a ListSelectionListener so that when an event is selected on a specific date
        // events that were selected on another date are unselected.
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lSE) {
                if (DayView.previousList != null && lSE.getSource() != previousList) {

                    previousList.getSelectedIndex();
                    previousList.clearSelection();
                }


                previousList = (JList) lSE.getSource();
            }
        });



        this.add(list, BorderLayout.CENTER);

        this.getEvents();

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
        // do nothing method.

    }

    private void getEvents() {
        String sql = "SELECT * FROM Event WHERE DATE(startDateAndTime)=? ORDER BY startDateAndTime::time;";  // I could order here with ORDER BY or I could add it all to a collection and let the user sort.
        try{
            PreparedStatement statement = this.getLocalPostgresConnection().prepareStatement(sql);
            statement.setObject(1, Date.valueOf(this.getDate()));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Event event = new Event(resultSet.getInt(1), resultSet.getString(2), resultSet.getTimestamp(3).toLocalDateTime(), resultSet.getTimestamp(4).toLocalDateTime());
                lm.addElement(event);
            }
        }
        catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public String toString() {
        return this.getDate().toString();
    }



}
