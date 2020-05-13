import java.sql.Connection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class NewEventPane {

    public NewEventPane(){
        this.initGUI();
    }

    private void initGUI(){
        JOptionPane.showOptionDialog(null, createMainPanel(), "Add New Event", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,  createButtons(), createButtons()[1]);
    }

    private JPanel createMainPanel(){
        JPanel mainPanel=new JPanel(new BorderLayout());

        // Add a text field so the user can enter a title for a new event.
        JTextComponent eventNameField=new JTextField();
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
        JButton cancelButton=new JButton("Cancel");

        buttons[0]=addEventButton;
        buttons[1]=cancelButton;

        return buttons;
    }
}
