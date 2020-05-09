import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.*;

public class NewEventPane {

    Component parent;

    public NewEventPane(Component parent){
        this.parent=parent;
        this.initGUI();
    }

    private void initGUI(){
        JOptionPane.showOptionDialog(parent.getParent(), createMainPanel(), "Add New Event", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,  createButtons(), createButtons()[1]);
    }

    private JPanel createMainPanel(){
        JPanel mainPanel=new JPanel(new BorderLayout());

        JTextComponent eventNameField=new JTextField();
        eventNameField.setLayout(new BorderLayout());

        JLabel addEventPrompt=new JLabel("Add event");
        addEventPrompt.setFont(eventNameField.getFont());
        addEventPrompt.setForeground(eventNameField.getForeground());
        addEventPrompt.setBorder(new EmptyBorder(eventNameField.getInsets()));

        Document eventNameFieldDocument=eventNameField.getDocument();
        eventNameFieldDocument.addDocumentListener(new DocumentListener(){

            public void insertUpdate(DocumentEvent de){
                if(eventNameFieldDocument.getLength()>0){
                    addEventPrompt.setVisible(false);
                }
            }

            public void changedUpdate(DocumentEvent de){
            }

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
