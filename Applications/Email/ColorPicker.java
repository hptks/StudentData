package Applications.Email;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by NIghtCrysIs on 16/03/2015.
 *
 * <code>ColorPicker</code> is a color picking window.
 * <p>Provides basic utilities to choose color as requested from <code>EmailEditor</code></p>
 * <p>Extends JDialog and implements ActionListener</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */
public class ColorPicker extends JDialog implements ActionListener{
    private EmailEditor parent;
    private JButton ok, cancel;
    private JColorChooser colorPicker;

    /** Constructor for <code>ColorPicker</code>.
     *
     *  @param parent an EmailEditor, subclass of JFrame, allows relative location to be set to it.
     */

    public ColorPicker(EmailEditor parent){
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());

        this.parent = parent;

        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Choose your text color"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        colorPicker = new JColorChooser();
        colorPanel.add(colorPicker);
        add(colorPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        ok = new JButton("Ok");
        cancel = new JButton("Cancel");
        ok.addActionListener(this);
        cancel.addActionListener(this);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //setting the location
        setLocation((parent.getX() + parent.getWidth() / 2) - this.getWidth() / 2, (parent.getY() + parent.getHeight()/2)-this.getHeight()/2);
        setVisible(true);
    }

    /** An ActionListener method
     *
     *  @param e ActionEvent
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok){
            parent.setColorPicker(String.format("%06x", colorPicker.getColor().getRGB() & 0x00FFFFFF));
            this.dispose();
        }
        else if(e.getSource() == cancel){
            this.dispose();
        }
    }
}
