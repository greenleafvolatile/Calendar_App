import javax.swing.border.EmptyBorder;
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
        JOptionPane.showOptionDialog(.getParent(), createMainPanel(), "Add New Event", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,  createButtons(), createButtons()[1]);
    }

    private JPanel createMainPanel(){
        JPanel mainPanel=new JPanel(new BorderLayout());


        JTextComponent titleField=new JTextField();
        titleField.setLayout(new BorderLayout());
        Document titleFieldDocument=titleField.getDocument();
        titleField.setCaretPosition(0); // Places the cursor at the beginning of the JTextField.


        JLabel addEventPrompt=new JLabel("Add event");
        addEventPrompt.setFont(titleField.getFont());
        addEventPrompt.setForeground(titleField.getForeground());
        addEventPrompt.setBorder(new EmptyBorder(titleField.getInsets()));
        titleField.add(addEventPrompt);
        addEventPrompt.setVisible(true);




        mainPanel.add(titleField);
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
