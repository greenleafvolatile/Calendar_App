import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.*;
import java.util.logging.Logger;

class ButtonPanel extends JPanel {


    private final int RIGID_AREA_HEIGHT=36; // I know the height of JPanel northPanel in MonthView.java is 36px after calling pack() in Calendar.java.
    private Dimension buttonDimension;
    private Connection localPostgresConnection;

    public ButtonPanel(Dimension tileDimension, Connection aLocalPostgresConnection) {

        this.localPostgresConnection=aLocalPostgresConnection;
        this.buttonDimension = tileDimension;
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


    public class NewEventPane {

        private JTextField eventNameField;

        public NewEventPane(){
            this.initGUI();
        }

        private void initGUI(){
            JOptionPane.showOptionDialog(ButtonPanel.this.getParent(), createMainPanel(), "Add New Event", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,  createButtons(), createButtons()[1]);
        }

        private JPanel createMainPanel(){
            JPanel mainPanel=new JPanel(new BorderLayout());

            // Add a text field so the user can enter a title for a new event.
            eventNameField=new JTextField();
            eventNameField.setLayout(new BorderLayout());

            // Add a prompt to the text field.
            JLabel addEventPrompt=new JLabel("Add event");
            addEventPrompt.setFont(eventNameField.getFont());
            addEventPrompt.setForeground(eventNameField.getForeground());
            addEventPrompt.setBorder(new EmptyBorder(eventNameField.getInsets()));

            Document eventNameFieldDocument=eventNameField.getDocument();
            eventNameFieldDocument.addDocumentListener(new DocumentListener(){


                // When the user enters text in the text field the prompt should disappear.
                public void insertUpdate(DocumentEvent de){
                    if(eventNameFieldDocument.getLength()>0){
                        addEventPrompt.setVisible(false);
                    }
                }

                public void changedUpdate(DocumentEvent de){
                }

                // If the users removes characters from the text field and the text field is empty the prompt should reappear.
                public void removeUpdate(DocumentEvent de){
                    if(eventNameFieldDocument.getLength()==0){
                        addEventPrompt.setVisible(true);
                    }
                }
            });

            eventNameField.add(addEventPrompt);
            addEventPrompt.setVisible(true);

            mainPanel.add(eventNameField);
            return mainPanel;
        }

        private JButton[] createButtons(){
            JButton[] buttons=new JButton[2];

            JButton addEventButton=new JButton("Add Event");
            addEventButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    String eventTitle=eventNameField.getText();
                    Event newEvent=new Event(eventTitle);
                    insertNewEvent(newEvent);
                }
            });

            JButton cancelButton=new JButton("Cancel");

            buttons[0]=addEventButton;
            buttons[1]=cancelButton;

            return buttons;
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
                new NewEventPane();


            }

        });
        gbc.anchor=GridBagConstraints.NORTH;
        gbc.gridx=0;
        gbc.gridy=0;

        gbc.weightx=0;
        gbc.weighty=0;
        gbc.insets=new Insets(0, 4, 4, 4);
        gbag.setConstraints(newEventButton, gbc);
        panel.add(newEventButton);

        // Add a button to exit the Calendar application.
        JButton exitButton=new CustomButton("Exit");
        exitButton.setMnemonic('E');
        gbc.anchor=GridBagConstraints.SOUTH;
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=0;
        gbc.weighty=1;
        gbc.insets=new Insets(0, 4, 4, 4);
        gbag.setConstraints(exitButton, gbc);
        panel.add(exitButton);

        this.add(panel);
    }

    private void insertNewEvent(Event anEvent){
        final String insertEventSql="INSERT INTO event " + "(name) VALUES " + "(?);";
        long id=0;

        try {
            PreparedStatement statement = localPostgresConnection.prepareStatement(insertEventSql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, anEvent.getTitle());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) {
                        id = resultSet.getLong(1);
                    }
                } catch (SQLException sqlEx) {
                    System.out.println(sqlEx.getMessage());
                }
            }
        }
        catch(SQLException sqlEx){
                System.out.println(sqlEx.getMessage());
        }

    }
}
