import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;
import java.awt.event.FocusEvent;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Logger;

/**
 * This dialog enables the user to change an event's attributes.
 * The code for the EditEventDialog and NewEventDialog classes is largely the same, i.e. not DRY. To-do: merge classes?
 */
public class EditEventDialog extends JDialog {

    // These formatters are also in NewEventDialogClass. Not DRY!
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private JTextField eventTitleField, startDateField, startTimeField, endDateField, endTimeField;
    private JLabel errorLabel;
    private Event event;

    public EditEventDialog(JFrame owner, Event anEvent) {

        super(owner, "Edit Event", true);
        this.event = anEvent;
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

        int fieldWidth = 10;
        final LocalTime currentTime = LocalTime.now();
        final String dateFormat = "dd/MM/yyyy";
        final String timeFormat = "HH:mm";

        JPanel mainPanel = new JPanel(new BorderLayout());

        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel centerPanel = new JPanel(gbag);

        JLabel eventNameLabel = new JLabel("Event title");
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbag.setConstraints(eventNameLabel, gbc);
        centerPanel.add(eventNameLabel);

        eventTitleField = new CustomTextField("Add event title", event.getTitle(), fieldWidth);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbag.setConstraints(eventTitleField, gbc);
        centerPanel.add(eventTitleField);

        // Add a text field so the user can enter a start date for the event. Also add a label to
        // identify this field.
        JLabel startDateLabel = new JLabel("Starts: ", SwingConstants.LEFT);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbag.setConstraints(startDateLabel, gbc);
        centerPanel.add(startDateLabel);

        startDateField = new CustomTextField(dateFormat, DATE_FORMATTER.format(event.getStartDateAndTime().toLocalDate()), fieldWidth);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbag.setConstraints(startDateField, gbc);
        centerPanel.add(startDateField);

        endDateField = new CustomTextField(dateFormat, DATE_FORMATTER.format(event.getEndDateAndTime().toLocalDate()), fieldWidth);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbag.setConstraints(endDateField, gbc);
        centerPanel.add(endDateField);

        fieldWidth = 5; // A smaller field width for the time fields.

        startTimeField = new CustomTextField(timeFormat, TIME_FORMATTER.format(event.getStartDateAndTime().toLocalTime()), fieldWidth);
        gbc.insets = new Insets(0, 20, 0, 0);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbag.setConstraints(startTimeField, gbc);
        centerPanel.add(startTimeField);

        // Add a text field so the user can enter an end date for the event. Also add a label to
        // identify this field.
        JLabel endDateLabel = new JLabel("Ends: ", SwingConstants.LEFT);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbag.setConstraints(endDateLabel, gbc);
        centerPanel.add(endDateLabel);

        endTimeField = new CustomTextField(timeFormat, TIME_FORMATTER.format(event.getEndDateAndTime().toLocalTime()),  fieldWidth);
        gbc.insets = new Insets(0, 20, 0, 0);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbag.setConstraints(endTimeField, gbc);
        centerPanel.add(endTimeField);

        errorLabel = new ErrorLabel();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = 6;
        gbag.setConstraints(errorLabel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(errorLabel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JButton[] createButtons() {
        JButton[] buttons = new JButton[2];

        JButton saveEventButton = new JButton("Save");
        saveEventButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                //LocalDateTime startDateAndTime = LocalDateTime.of(LocalDate.parse(startDateField.getText(), DATE_FORMATTER), LocalTime.parse(startTimeField.getText(), TIME_FORMATTER));
                //LocalDateTime endDateAndTime = LocalDateTime.of(LocalDate.parse(endDateField.getText(), DATE_FORMATTER), LocalTime.parse(endTimeField.getText(), TIME_FORMATTER));
                //Event event = new Event(eventTitleField.getText(), startDateAndTime, endDateAndTime);
                editEvent(event);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                EditEventDialog.this.dispose();
            }
        });

        buttons[0] = saveEventButton;
        buttons[1] = cancelButton;

        return buttons;
    }

    private void editEvent(Event anEvent){

        final String sQL = "SELECT * FROM event WHERE id=?";

        try {
            PreparedStatement statement = DBUtils.getConnection().prepareStatement(sQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setInt(1, anEvent.getId());
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                rs.updateString(2, eventTitleField.getText());
                rs.updateTimestamp(3, Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(startDateField.getText(), DATE_FORMATTER), LocalTime.parse(startTimeField.getText(), TIME_FORMATTER))));
                rs.updateTimestamp(4, Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(endDateField.getText(), DATE_FORMATTER), LocalTime.parse(endTimeField.getText(), TIME_FORMATTER))));;
                rs.updateRow();
            }
            DayView.getSelectedView().reload();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

        /*final String updateEventSQL = "UPDATE event SET title=?, startdateandtime=?, enddateandtime=? WHERE id = ?";
        long id = event.getId();

        try {
            PreparedStatement statement = DBUtils.getConnection().prepareStatement(updateEventSQL);
            statement.setString(1, eventTitleField.getText());
            statement.setObject(2, Timestamp.valueOf(anEvent.getStartDateAndTime()));
            statement.setObject(3, Timestamp.valueOf(anEvent.getEndDateAndTime()));
            statement.setInt(4, anEvent.getId());
            int affectedRows = statement.executeUpdate();
            /*if (affectedRows > 0) {
                try {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    Logger.getGlobal().info("AffectedRows: " + affectedRows);
                    Logger.getGlobal().info("resultset: + " + resultSet.getString(2));

                    if (resultSet.next()) {
                        id = resultSet.getLong(1);
                    }
                } catch (SQLException sqlEx) {
                    System.out.println(sqlEx.getMessage());
                }
            }
            DayView.getSelectedView().reload();
        }

        catch(SQLException sqlEx){
            System.out.println(sqlEx.getMessage());
        }*/

    }

    private boolean isValidDate(String date) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

        boolean isValid = false;

        try {
            LocalDate aDate = LocalDate.parse(date, dateFormatter);
            isValid = true;
        }
        catch(DateTimeParseException dTPEx) {
            dTPEx.printStackTrace();
        }
        return isValid;
    }

    private boolean isValidDateFormat(String date) {
        return date.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})");
    }

    private boolean isValidTime(String time) {
        return time.matches("(0[0-0]|1[0-9]]2[0-3]):[0-5][0-9]");
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("\\d\\d:\\d\\d");
    }

    private static class EndDateBeforeStartDateException extends Exception{
        public EndDateBeforeStartDateException(String message){
            super(message);
        }
    }

    public class ErrorLabel extends JLabel {

        private Font font;

        public ErrorLabel() {

            final int fontSize = 10;
            this.font = new Font("Times New Roman", Font.PLAIN, fontSize);
            this.setFont(font);
            this.setForeground(Color.RED);
            this.setPreferredSize(new Dimension(0,fontSize));
            this.setHorizontalAlignment(SwingConstants.CENTER);

        }
    }
}




