import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Logger;

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


    public class NewEventPane extends JDialog {

        private JTextField eventNameField, startDateField, endDateField;

        public NewEventPane() {
            super((JFrame) ButtonPanel.this.getTopLevelAncestor(), "Add New Event", true);
            this.initGUI();
        }

        private void initGUI(){
            JOptionPane optionPane=new JOptionPane(createMainPanel(), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, createButtons(), createButtons()[0]);
            this.getContentPane().add(optionPane);
            this.pack();
            this.setLocationRelativeTo(ButtonPanel.this.getTopLevelAncestor());
            this.setVisible(true);
        }

        private JPanel createMainPanel(){
            GridBagLayout gbag = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            JPanel mainPanel = new JPanel(gbag);

            // Add a text field so the user can enter a title for a new event.
            eventNameField=new JTextField(15);
            addPrompt(eventNameField, "Add event");
            gbc.anchor=GridBagConstraints.WEST;
            gbc.gridx=0;
            gbc.gridy=0;
            gbag.setConstraints(eventNameField, gbc);
            mainPanel.add(eventNameField);

            // Add a text field so the user can enter a start date for the event. Also add a label to
            // identify this field.
            JLabel startDateLabel=new JLabel("Start date", SwingConstants.LEFT);
            gbc.insets=new Insets(5, 0, 0, 0);
            gbc.gridx=0;
            gbc.gridy=1;
            gbag.setConstraints(startDateLabel, gbc);
            mainPanel.add(startDateLabel);

            startDateField=new JTextField(15);
            addPrompt(startDateField, "DD/MM/YY");
            startDateField.setText(String.format("%02d/%02d/%d", view.getSelectedView().getDate().getDayOfMonth(), view.getSelectedView().getDate().getMonth().getValue(), view.getSelectedView().getDate().getYear()));
            gbc.gridx=0;
            gbc.gridy=2;
            gbag.setConstraints(startDateField, gbc);
            mainPanel.add(startDateField);

            // Add a text field so the user can enter an end date for the event. Also add a label to
            // identify this field.
            JLabel endDateLabel = new JLabel("End date", SwingConstants.LEFT);
            gbc.insets = new Insets(5, 0, 0, 0);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbag.setConstraints(endDateLabel, gbc);
            mainPanel.add(endDateLabel);

            endDateField = new JTextField(15);
            addPrompt(endDateField, "MM/DD/YY");
            endDateField.setText(String.format("%02d/%02d/%d", view.getSelectedView().getDate().getDayOfMonth(), view.getSelectedView().getDate().getMonth().getValue(), view.getSelectedView().getDate().getYear()));
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbag.setConstraints(endDateField, gbc);
            mainPanel.add(endDateField);


            return mainPanel;
        }

        private void addPrompt(JTextField textField, String prompt){
            textField.setLayout(new BorderLayout());
            JLabel promptLabel=new JLabel(prompt);
            promptLabel.setFont(textField.getFont());
            promptLabel.setForeground(textField.getForeground());
            promptLabel.setBorder(new EmptyBorder(textField.getInsets()));
            Document doc = textField.getDocument();
            doc.addDocumentListener(new DocumentListener(){

                // When the user enters text in the text field the prompt should disappear.
                public void insertUpdate(DocumentEvent de){
                    if(doc.getLength()>0){
                        promptLabel.setVisible(false);
                    }
                }

                public void changedUpdate(DocumentEvent de){
                }

                // If the users removes characters from the text field and the text field is empty the prompt should reappear.
                public void removeUpdate(DocumentEvent de){
                    if(doc.getLength()==0){
                        promptLabel.setVisible(true);
                    }
                }
            });
            textField.add(promptLabel, BorderLayout.CENTER);
            promptLabel.setVisible(true);
        }

        private JButton[] createButtons(){
            JButton[] buttons=new JButton[2];

            JButton addEventButton=new JButton("Add Event");
            addEventButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
                    String eventTitle = eventNameField.getText();


                    Date startDate, endDate;
                    try {

                        startDate = Date.valueOf(LocalDate.parse(startDateField.getText(), format));
                        endDate = Date.valueOf(LocalDate.parse(endDateField.getText(), format));
                        if (endDate.compareTo(startDate) < 0) throw new EndDateBeforeStartDateException("End date before start date!");


                        Event newEvent=new Event(eventTitle, startDate, endDate);
                        insertNewEvent(newEvent);

                    }
                    catch(DateTimeParseException dateTimeParseException) {
                        dateTimeParseException.printStackTrace();
                    }
                    catch(EndDateBeforeStartDateException endDateBeforeStartDateEx){
                        endDateBeforeStartDateEx.printStackTrace();
                    }
                }
            });

            JButton cancelButton=new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    /*Window parent=SwingUtilities.getWindowAncestor(cancelButton);
                    if(parent!=null){
                        parent.dispose();
                    }*/
                    NewEventPane.this.dispose();


                }
            });

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
        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                System.exit(0);
            }
        });
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
        final String insertEventSql="INSERT INTO event(name, startdate, enddate)" + "VALUES(?,?,?)";
        long id = 0;

        try {
            PreparedStatement statement = localPostgresConnection.prepareStatement(insertEventSql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, anEvent.getTitle());
            statement.setObject(2, anEvent.getStartDate());
            statement.setObject(3, anEvent.getEndDate());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) {
                        id = resultSet.getLong(1);
                        Logger.getGlobal().info("id: " + id);
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

    private static class EndDateBeforeStartDateException extends Exception{
        public EndDateBeforeStartDateException(String message){
            super(message);
        }


    }


}
