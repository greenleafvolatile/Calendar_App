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


    final static Dimension INITIAL_DIMENSION = new Dimension(100, 100);
    private static JList previousList = null;
    private static Event selectedEvent = null;
    private static DayView selectedView = null;

    private DefaultListModel<Event> lm;
    private int selectedEventIndex;

    DayView(LocalDate date) {

        super(date);
        this.initGUI();
    }

    private void initGUI() {

        this.setLayout(new BorderLayout());

        JLabel dateLabel = new JLabel(String.valueOf(this.getDate().getDayOfMonth()), SwingConstants.LEFT);
        dateLabel.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), 10));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.WHITE);
        dateLabel.setBorder(BorderFactory.createEtchedBorder());
        this.add(dateLabel, BorderLayout.NORTH);

        lm = new DefaultListModel<>();

        JList<Event> list = new JList<>(lm) {

            // Override locationToIndex() to prevent the last item on the list to be selected when the user clicks
            // outside the bounds of (below) the last cell of the list.
            @Override
            public int locationToIndex(Point location) {
                int index = super.locationToIndex(location);
                Logger.getGlobal().info("" + index);
                if (index != -1 && !getCellBounds(index, index).contains(location)) {
                    return -1;
                } else {
                    return index;
                }
            }
        };

        // You can only ever select one event at a time.
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Custom cel-renderer to display Event data in list.
        list.setCellRenderer(new EventListRenderer());

        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent mE) {
                maybeShowPopUp(mE);
            }

            @Override
            public void mouseReleased(MouseEvent mE) {
                maybeShowPopUp(mE);
            }

            private void maybeShowPopUp(MouseEvent mE) {


                // If an event is selected on a specific date, the previous selected event is unselected.
                if (previousList != null && mE.getSource() != previousList) {
                    previousList.clearSelection();
                }
                previousList = (JList) mE.getSource();

                //When the user left clicks on a specific date
                //that date will be the selected date with a red border.
                DayView.selectedView.setSelectedView(false);
                DayView.this.setSelectedView(true);

                selectedEventIndex = list.getSelectedIndex();

                if (selectedEventIndex != -1) {

                    // The ButtonPanel needs to know if an event has been selected and if so it needs a reference to it.
                    selectedEvent = lm.getElementAt(selectedEventIndex);

                    // When the user right clicks an event a popup menu will be brought up.
                    // The exact gesture that should bring up a popupmenu varies by look and feel.
                    if (mE.isPopupTrigger()) {

                        //if (selectedEventIndex != -1) {

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

                        JPopupMenu menu = new CustomPopUp();

                        // Show the popup in the JList at the end of the text in a selected cell, underneath that cell.
                        menu.show(list, (int) (point.getX() + width), (int) (point.getY() + bounds.getHeight()));
                    }
                }
                else {
                    selectedEvent = null;
                }
            }
        });

        this.add(list, BorderLayout.CENTER);
        this.getEvents();
    }


    @Override
    public Dimension getPreferredSize() { // Perhaps it shouldn't be the tiles that determine the size of the app. Perhaps the tiles should be made to fit into a frame of a certain size.
        return INITIAL_DIMENSION;
    }

    protected static Dimension getInitialDimension() {
        return INITIAL_DIMENSION;
    }

    protected void setSelectedView(boolean isSelected) {
        if (isSelected) {
            DayView.selectedView = DayView.this;
            this.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        } else {
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    protected static DayView getSelectedView() {
        return DayView.selectedView;
    }


    private void getEvents() {
        String sql = "SELECT * FROM Event WHERE DATE(startDateAndTime)=? ORDER BY startDateAndTime::time;";  // I could order here with ORDER BY or I could add it all to a collection and let the user sort.
        try {
            PreparedStatement statement = DBUtils.getConnection().prepareStatement(sql);
            statement.setObject(1, Date.valueOf(this.getDate()));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Logger.getGlobal().info("Next!");

                Event event = new Event(resultSet.getInt(1), resultSet.getString(2), resultSet.getTimestamp(3).toLocalDateTime(), resultSet.getTimestamp(4).toLocalDateTime());
                lm.addElement(event);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public String toString() {
        return this.getDate().toString();
    }

    public static Event getSelectedEvent() {
        return selectedEvent;
    }

    protected void deleteEvent() {
        DBUtils.deleteEvent(lm.getElementAt(selectedEventIndex).getId());
        lm.removeElementAt(selectedEventIndex);
    }

    protected void reload() {
        lm.clear();
        getEvents();
    }

    public class CustomPopUp extends JPopupMenu {


        public CustomPopUp() {

            this.add(new AbstractAction("Edit") {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new EditEventDialog((JFrame) DayView.this.getTopLevelAncestor(), lm.getElementAt(selectedEventIndex));
                }
            });

            this.add(new AbstractAction("Delete") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    DayView.this.deleteEvent();
                }
            });
        }
    }
}
