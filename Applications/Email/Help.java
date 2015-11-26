package Applications.Email;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by NIghtCrysIs on 2015/03/15.
 *
 * <code>Help</code> creates a window that displays helpful information to users
 * <p>Extends JDialog</p>
 * @author Johnson Wong
 * @since 1.7
 */
public class Help extends JDialog {

    /** Constructor for <code>Help</code>. Sets the text to be displayed, formatting of the display,
     * settings, adds corresponding components in their JPanel.
     *
     *  @param parent EmailEditor, allows the setting of relative location
     */

    public Help(EmailEditor parent){
        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle("Help");
        setLayout(new BorderLayout());

        JTextArea information = new JTextArea();
        String helpInfo = "E-express V0.1\n\nThis program allows you to send students their assessed marks.\n\n" +
                "This version features:\n- Html support\n- Text editing support\n- Preview support\n" +
                "- Simple, clean GUI layout\n- Fast, efficient dynamic memory management\n\n" +
                "To use the buttons provided, you must select texts from either boxes provided. This excludes the title box.\n\n" +
                "NOTE: It is recommended that HTML editing to come strictly after the drafting of the email. This is to " +
                "maximise the functionality of the program and the time efficiency for the users.\nDue to the program's html support," +
                " you are free to use html codes as you wish. This includes functionality that is not explicitly presented on the editing toolbar.\n\n" +
                "Useful commands:\n<p>This denotes a paragraph.</p>\n<p align\"left\">Aligns the paragraph to the left. " +
                "\"left\" can be replaced with \"center\", \"right\" or \"justify\"\n" +
                "\n\nFor professional conduct reasons, it is highly recommended that you use the King's email account assigned to you. " +
                "If you have lost your password, you can access the automatic Password self help website at: http://www.kcl.ac.uk/it/support/passwords/index.aspx" +
                "\n\nIf you have lost your account name, please contact the IT Service Desk immediately on 020 7848 8888 or e-mail 8888@kcl.ac.uk.";
        information.setText(helpInfo);
        information.setEditable(false);
        information.setLineWrap(true);
        information.setWrapStyleWord(true);

        JScrollPane scroller = new JScrollPane(information);
        add(scroller, BorderLayout.CENTER);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(ok);

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 550);
        setResizable(false);
        //setting the location
        setLocation((parent.getX() + parent.getWidth() / 2) - this.getWidth() / 2, (parent.getY() + parent.getHeight()/2)-this.getHeight()/2);
        setVisible(true);
    }
}
