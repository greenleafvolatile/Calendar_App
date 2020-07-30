import java.awt.event.ActionEvent;
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

    private MonthView currentView;
    private DefaultListModel<Event> lm;



    DayView(LocalDate date, MonthView view){

        super(date);
        this.currentView = view;

        this.setLayout(new BorderLayout());

        JLabel dateLabel = new JLabel(String.valueOf(this.getDate().getDayOfMonth()), SwingConstants.LEFT);
        dateLabel.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), 10));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.WHITE);
        dateLabel.setBorder(BorderFactory.createEtchedBorder());
        this.add(dateLabel, BorderLayout.NORTH);


        lm = new DefaultListModel<>();

        JList<Event> list = new JList<>(lm) {
            // Override locationToIndex to prevent the last item on the list to be selected when the user clicks
            // outside the bounds of (below) the last cell of the list.
            @Override
            public int locationToIndex(Point location) {
                int index = super.locationToIndex(location);
                Logger.getGlobal().info("" + index);
                if (index != -1 && !getCellBounds(index, index).contains(location)) {
                    return -1;
                }
                else {
                    return index;
                }
            }
        };
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new EventListRenderer());

        // Add a MouseListener so that when the user left clicks on a specific date
        // that date will be the selected date with a red border.
        // When the user right clicks an event a popup menu will render.
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
                DayView.this.currentView.getSelectedDay().setSelected(false);
                DayView.this.setSelected(true);
                DayView.this.currentView.setSelectedDay(DayView.this);
            }

            private void maybeShowPopUp(MouseEvent mE) {

                if (mE.isPopupTrigger()) {

                    int selectedEventIndex = list.getSelectedIndex();

                    if (selectedEventIndex != -1) {

                        // I need the width in pixels of the text in a cell of the list for the location of the popup.
                        // For that I need a FontMetrics object.
                        EventListRenderer renderer = (EventListRenderer) list.getCellRenderer();
                        Font font = renderer.getFont();
                        FontMetrics fm = renderer.getFontMetrics(font);

                        // And the text.
                        String text = lm.getElementAt(selectedEventIndex).getTitle();

                        // Get the width of the text.
                        int width = fm.stringWidth(text);

                        // The popup should only render when the user clicks on a selected cell.
                        // To compare the location where the user clicked to the location of the selected cell, I need the
                        // coordinates of that cell in the list.
                        Rectangle bounds = list.getCellBounds(selectedEventIndex, selectedEventIndex);
                        Point point = bounds.getLocation();


                        JPopupMenu menu = new CustomPopUp(lm.getElementAt(selectedEventIndex));

                        // Show the popup in the JList at the end of the text in a selected cell, underneath that cell.
                        menu.show(list, (int) (point.getX() + width), (int) (point.getY() + bounds.getHeight()));

                    }
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

        //JScrollPane scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        // do nothing method. Design flaw?
    }

    private void getEvents() {
        //lm.removeAllElements();
        String sql = "SELECT * FROM Event WHERE DATE(startDateAndTime)=? ORDER BY startDateAndTime::time;";  // I could order here with ORDER BY or I could add it all to a collection and let the user sort.
        try{
            PreparedStatement statement = DBUtils.getConnection().prepareStatement(sql);
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


    public class CustomPopUp extends JPopupMenu {

        private Event event;

        public CustomPopUp(Event event) {
            this.event = event;

            this.add(new AbstractAction("Edit") {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new EditEventDialog(event);
                }
            });

            this.add(new AbstractAction("Delete") {

                public void actionPerformed(ActionEvent ae) {
                    Logger.getGlobal().info("" + DBUtils.deleteEvent(event.getId()));
                    lm.removeAllElements();
                    DayView.this.getEvents();

                }
            });
        }
    }
}
