package Applications.Email;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JFrame;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Created by NIghtCrysIs on 2015/03/15.
 *
 * <code>SendingScreen</code> creates a window that displays the progress bar of sending email
 * <p>Extends JDialog</p>
 * @author Johnson Wong
 * @since 1.7
 */

public class SendingScreen extends JDialog{
    private JProgressBar progressBar;
    private int recipientNumber, counter;

    /** Constructor for <code>SendingScreen</code>. Initializes the
     * JColorPicker, variables and objects, and adds them to the
     * corresponding position in the JDialog
     *
     *  @param c Component, that allows the setting of relative location
     *  @param recipientNumber the number of recipients, acts as the higher limit of when
     *                         should this window be disposed.
     */

    public SendingScreen(Component c, int recipientNumber){
        setLocationRelativeTo(c);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.recipientNumber = recipientNumber;
        counter = 0;

        changeTitle();

        progressBar = new JProgressBar(0, recipientNumber);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 50));

        add(progressBar, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    /**
     *  updates the progressBar. When the counter reaches the same value as the upper limit
     *  (i.e. progress is 100%) it disposes this window.
     */

    public void update(){
        counter++;
        progressBar.setValue(counter);
        changeTitle();
        repaint();
        revalidate();
        if(counter == recipientNumber)
            dispose();
    }

    /**
     *  Method for the purpose in changing the title of the window.
     *  Called for each email sent.
     */

    private void changeTitle(){
        setTitle("Sending " + counter + " out of " + recipientNumber + " messages.");
    }
}
