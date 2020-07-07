import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

class ButtonPanel extends JPanel {

    private final int RIGID_AREA_HEIGHT=36; // I know the height of JPanel northPanel in MonthView.java is 36px after calling pack() in Calendar.java.
    private Dimension buttonDimension;
    private Connection localPostgresConnection;
    private MonthView view; // should be of type CalendarView (program to an interface not an implementation. Just need to figure out how shit should fit together.

    public ButtonPanel(MonthView aView, Connection aLocalPostgresConnection) {
        this.localPostgresConnection=aLocalPostgresConnection;
        this.buttonDimension = DayView.INITIAL_DIMENSION;
        this.view=aView;
        this.initGUI();
    }

    class CustomButton extends JButton{

        public CustomButton(String text){
                                       super(text);
                                                   }

        @Override
        public Dimension getPreferredSize(){
                                          return buttonDimension;
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

        // Add a button to open a JOptionPane to add an event to the calendar.
        JButton newEventButton=new CustomButton("<html><center>New<br />Event</center</html>");
        newEventButton.setMnemonic('N');
        newEventButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent actionEvent){

                new NewEventDialog((JFrame) ButtonPanel.this.getTopLevelAncestor(), view);
            }

        });
        gbc.anchor=GridBagConstraints.NORTH;
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weighty=1;
        gbc.insets=new Insets(0, 4, 4, 4);
        gbag.setConstraints(newEventButton, gbc);
        panel.add(newEventButton);

        // Add a button to exit the Calendar application.
        JButton exitButton=new CustomButton("Exit");
        exitButton.setMnemonic('E');
        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                System.exit(0);
            }
        });
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weighty=0 ;
        gbc.insets=new Insets(0, 4, 0, 4);
        gbag.setConstraints(exitButton, gbc);
        panel.add(exitButton);

        this.add(panel);
    }


}
