import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Logger;

class ButtonPanel extends JPanel {


    private final int RIGID_AREA_HEIGHT=36; // I know the height of JPanel northPanel in MonthView.java is 36px after calling pack() in Calendar.java.
    private Dimension buttonDimension;


    public ButtonPanel(Dimension tileDimension){

        class CustomButton extends JButton{

            public CustomButton(String text){
                super(text);
            }

            @Override
            public Dimension getPreferredSize(){
                return buttonDimension;
            }

        }

        this.buttonDimension=tileDimension;

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
        JButton addEventButton=new CustomButton("<html><center>New<br />Event</center</html>");
        addEventButton.setMnemonic('N');
        addEventButton.addComponentListener(new ComponentAdapter(){




            public void componentResized(ComponentEvent ce){
                Logger.getGlobal().info(String.format("Button width: %d Button height: %d", addEventButton.getWidth(), addEventButton.getHeight()));
            }


        });
        gbc.anchor=GridBagConstraints.NORTH;
        gbc.gridx=0;
        gbc.gridy=0;

        gbc.weightx=0;
        gbc.weighty=0;
        gbc.insets=new Insets(0, 4, 4, 4);
        gbag.setConstraints(addEventButton, gbc);
        panel.add(addEventButton);

        // Add a button to exit the Calendar application.
        JButton exitButton=new CustomButton("Exit");
        exitButton.setMnemonic('E');
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=0;
        gbc.weighty=1;
        gbc.insets=new Insets(0, 4, 4, 4);
        //gbc.anchor=GridBagConstraints.NORTH;
        gbag.setConstraints(exitButton, gbc);
        panel.add(exitButton);

        this.add(panel);
    }
}
