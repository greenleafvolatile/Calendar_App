import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.logging.Logger;

class ButtonPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final int RIGID_AREA_HEIGHT=36; // I know the height of JPanel northPanel in MonthView.java is 36px after calling pack() in Calendar.java.


    public ButtonPanel() {
        this.initGUI();
    }

    class CustomButton extends JButton{

        public CustomButton(String text){
                                       super(text);
                                                   }

        @Override
        public Dimension getPreferredSize() {
            return DayView.INITIAL_DIMENSION;
        }

    }

    public void initGUI(){

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // I want the buttons to line up with the day tiles. For that I need some empty space on top equal to the height of the
        // northern panel in MonthView's BorderLayout.To do that I add a RigidArea.
        this.add(Box.createRigidArea(new Dimension(0, RIGID_AREA_HEIGHT)));

        // Create a panel with GridBagLayout to hold the buttons.
        JPanel panel=new JPanel();
        GridBagLayout gbag=new GridBagLayout();
        GridBagConstraints gbc=new GridBagConstraints();
        panel.setLayout(gbag);

        // Add a button for opening a JOptionPane to add an event to the calendar.
        JButton newEventButton=new CustomButton("<html><center><u>N</u>ew<br />Event");

        newEventButton.setBorder(BorderFactory.createRaisedBevelBorder());
        newEventButton.setMnemonic('N');
        newEventButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent){


                new NewEventDialog((JFrame) ButtonPanel.this.getTopLevelAncestor());
            }

        });

        gbc.gridy=0;
        gbc.insets=new Insets(0, 4, 4, 4);
        gbag.setConstraints(newEventButton, gbc);
        panel.add(newEventButton);

        // Add a button for opening a JOptionPane to edit an event.
        JButton editEventButton = new CustomButton("<html><center><u>E</u>dit<br />Event</center></html>");
        editEventButton.setBorder(BorderFactory.createRaisedBevelBorder());
        editEventButton.setMnemonic('E');

        editEventButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent aE) {
                if (DayView.getSelectedEvent() != null) {
                    new EditEventDialog((JFrame) ButtonPanel.this.getTopLevelAncestor(), DayView.getSelectedEvent());
                }
                else {
                    showSelectEventWarning();
                }
            }
        });

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 4, 4, 4);
        gbag.setConstraints(editEventButton, gbc);
        panel.add(editEventButton);

        JButton deleteEventButton = new CustomButton("<html><center><u>D</u>elete<br />Event</center></html>");

        deleteEventButton.setBorder(BorderFactory.createRaisedBevelBorder());
        deleteEventButton.setMnemonic('D');
        deleteEventButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (DayView.getSelectedEvent() != null) {
                    ((DayView) DayView.getSelectedView()).deleteEvent();
                }
                else {
                    showSelectEventWarning();
                }
            }
        });

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 4, 4, 4);
        gbag.setConstraints(deleteEventButton, gbc);
        panel.add(deleteEventButton);

        // Add a button to exit the Calendar application.
        JButton exitButton=new CustomButton("Exit");

        exitButton.setBorder(BorderFactory.createRaisedBevelBorder());
        exitButton.setMnemonic('x');
        exitButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent event){
                System.exit(0);
            }

        });

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 4, 4, 4);
        gbag.setConstraints(exitButton, gbc);
        panel.add(exitButton);

        this.add(panel);
    }

    private void showSelectEventWarning() {
        JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Please select an event!", "Error: no event selected", JOptionPane.ERROR_MESSAGE, null);
    }


}
