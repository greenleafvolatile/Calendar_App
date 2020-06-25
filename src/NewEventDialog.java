import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Logger;

public class NewEventDialog extends JDialog {

    private JTextField eventNameField, startDateField, startTimeField, endDateField, endTimeField;
    private CalendarView view;

    public NewEventDialog(JFrame owner, CalendarView view) {

        super(owner, "Add New Event", true);
        this.view = view;
        this.initGUI(owner);
    }

    private void initGUI(JFrame owner){
        JOptionPane optionPane = new JOptionPane(createMainPanel(), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, createButtons(), createButtons()[0]);
        this.getContentPane().add(optionPane);
        this.pack();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    private JPanel createMainPanel(){
        final int fieldWidth = 10;
        final LocalTime currentTime = LocalTime.now();

        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel mainPanel = new JPanel(gbag);

        // Add a text field so the user can enter a title for a new event. Also add a label to
        // identify this field.
        JLabel eventNameLabel = new JLabel("Event title");
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbag.setConstraints(eventNameLabel, gbc);
        mainPanel.add(eventNameLabel);

        eventNameField = new JTextField(fieldWidth);
        addPrompt(eventNameField, "Add event title");
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbag.setConstraints(eventNameField, gbc);
        mainPanel.add(eventNameField);

        // Add a text field so the user can enter a start date for the event. Also add a label to
        // identify this field.
        JLabel startDateLabel = new JLabel("Starts: ", SwingConstants.LEFT);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbag.setConstraints(startDateLabel, gbc);
        mainPanel.add(startDateLabel);

        startDateField = new JTextField(fieldWidth);
        addPrompt(startDateField, "dd-MM-yyyy");
        startDateField.setText(String.format("%02d-%02d-%d", view.getSelectedView().getDate().getDayOfMonth(), view.getSelectedView().getDate().getMonth().getValue(), view.getSelectedView().getDate().getYear()));
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbag.setConstraints(startDateField, gbc);
        mainPanel.add(startDateField);

        startTimeField = new JTextField(fieldWidth);
        addPrompt(startTimeField, "HH:MM");
        startTimeField.setText(String.format("%02d:%02d", currentTime.getMinute() > 30 ? currentTime.getHour() + 1 : currentTime.getHour(), currentTime.getMinute() > 30 ? 0 : 30));
        gbc.insets = new Insets(0, 20, 0, 0);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbag.setConstraints(startTimeField, gbc);
        mainPanel.add(startTimeField);

        // Add a text field so the user can enter an end date for the event. Also add a label to
        // identify this field.
        JLabel endDateLabel = new JLabel("Ends: ", SwingConstants.LEFT);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbag.setConstraints(endDateLabel, gbc);
        mainPanel.add(endDateLabel);

        endDateField = new JTextField(fieldWidth);
        addPrompt(endDateField, "dd-MM-yyyy");
        endDateField.setText(String.format("%02d-%02d-%d", view.getSelectedView().getDate().getDayOfMonth(), view.getSelectedView().getDate().getMonth().getValue(), view.getSelectedView().getDate().getYear()));
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbag.setConstraints(endDateField, gbc);
        mainPanel.add(endDateField);

        JLabel errorLabel = new JLabel("Error text", SwingConstants.CENTER);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = 6;
        gbag.setConstraints(errorLabel, gbc);
        mainPanel.add(errorLabel);


        return mainPanel;
    }


    private void addPrompt(JTextField textField, String prompt) {

        textField.setLayout(new BorderLayout());
        JLabel promptLabel = new JLabel(prompt);
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
                if(doc.getLength() == 0){
                    promptLabel.setVisible(true);
                }
            }
        });
        textField.add(promptLabel, BorderLayout.CENTER);
        promptLabel.setVisible(true);
    }

    private JButton[] createButtons() {
        JButton[] buttons = new JButton[2];

        JButton addEventButton = new JButton("Add Event");
        addEventButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
                String eventTitle = eventNameField.getText();


                LocalDate startDate;
                LocalTime startTime;
                boolean isValidStartDate=false, isValidEndDate=false, isValidStartTime=false, isValidEndTime=false;
                try {
                    startDate = LocalDate.parse(startDateField.getText(), dateFormatter);
                    isValidStartDate = true;
                    //startDate = LocalDate.parse(String.format(startDateField.getText(), dateFormatter);
                    //endDate = LocalDate.parse(endDateField.getText(), dateFormatter);

                    //endDate = Date.valueOf(LocalDate.parse(endDateField.getText(), dateFormatter));
                    //if (endDate.compareTo(startDate) < 0) throw new EndDateBeforeStartDateException("End date before start date!");


                    //Event newEvent = new Event(eventTitle, startDate, endDate);
                    //insertNewEvent(newEvent);

                }
                catch(DateTimeParseException dateTimeParseException) {
                    dateTimeParseException.printStackTrace();
                    System.out.println("Invalid start date " + isValidStartDate );
                }
                /*catch(EndDateBeforeStartDateException endDateBeforeStartDateEx) {
                    Logger.getGlobal().info("End date before start date!");
                    //endDateBeforeStartDateEx.printStackTrace();
                }*/

                try {

                    startTime = LocalTime.parse(startTimeField.getText(), timeFormatter);
                }
                catch(DateTimeParseException dateTimeParseException) {

                    dateTimeParseException.printStackTrace();
                    System.out.println("Invalid start time");


                }


            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                    /*Window parent = SwingUtilities.getWindowAncestor(cancelButton);
                    if(parent! = null){
                        parent.dispose();
                    }*/
                NewEventDialog.this.dispose();


            }
        });

        buttons[0] = addEventButton;
        buttons[1] = cancelButton;

        return buttons;
    }

    private void insertNewEvent(Event anEvent){
        final String insertEventSql = "INSERT INTO event(name, startdate, enddate)" + "VALUES(?,?,?)";
        long id = 0;

        try {
            PreparedStatement statement = view.getLocalPostgresConnection().prepareStatement(insertEventSql, Statement.RETURN_GENERATED_KEYS);
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

