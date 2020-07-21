import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class EventListRenderer extends JLabel implements ListCellRenderer<Event> {

    public EventListRenderer() {
        this.setOpaque(true);
        this.setHorizontalAlignment(SwingConstants.LEFT);
        this.setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Event event, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(event.getTitle());
        //Logger.getGlobal().info("Index: " + index);
        //Logger.getGlobal().info("isSelected " + isSelected);

        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        }
        else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        return this;
    }
}
