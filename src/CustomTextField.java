import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;

/**
 * A textField that displays a prompt when it is empty, and that only allows a specific number of characters to be inputted.
 */

class CustomTextField extends JTextField {

    private final String prompt;
    private String text;

    {
        this.setLayout(new BorderLayout()); // Use an initializer block for code that is run regardless of which constructor is used.
    }

    protected CustomTextField(String aPrompt, String aText, int fieldWidth) {
        super(fieldWidth);
        this.prompt = aPrompt;
        this.text = aText;
        this.setPrompt();
        this.setCharacterMax();
        this.setText(text);
    }

    protected CustomTextField(String aPrompt, int fieldWidth) {
        super(fieldWidth);
        this.prompt = aPrompt;
        this.setPrompt();
    }

    private void setPrompt() {
        JLabel label = new JLabel();
        label.setText(prompt);
        label.setFont(this.getFont());

        this.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (CustomTextField.this.getDocument().getLength() > 0) {
                    label.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (CustomTextField.this.getDocument().getLength() == 0) {
                    label.setVisible(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }
        });

        this.add(label, BorderLayout.CENTER);
    }


    private void setCharacterMax() {
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new DocumentFilter() {

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrSet) {
                try {
                    String totalInput = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                    if (totalInput.length() <= prompt.length()) {
                        super.replace(fb, offset, length, text, attrSet);
                    }

                } catch (BadLocationException bLEx) { // What to do with this exception?
                    bLEx.printStackTrace();
                }
            }
        });
    }
}
