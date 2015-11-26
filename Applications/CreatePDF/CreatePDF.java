package Applications.CreatePDF;

import Applications.FileManager;
import Applications.MainWindow;
import Data.StudentArrayList;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

/**
 * Created by NIghtCrysIs on 2015/03/20.
 *
 * <code>CreatePDF</code> is an input dialog window for creating PDF files.
 * <p>Provides basic interface to users to choose where to save their file
 * and choose if they want students with no exam results to be included in the resultant PDF</p>
 * <p>Extends JDialog and implements ActionListener</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */
public class CreatePDF extends JDialog implements ActionListener{
    private static String currentDir = null, fileName;
    private JButton browse, ok, cancel;
    private JTextField filePath;
    private JCheckBox skipNoMarks;
    private StudentArrayList studentArrayList;
    private JPanel previewPanel;
    private JLabel warning;

    /** Constructor for <code>ColorPicker</code> window. Is modal.
     * <p>Sets up variables and instantiates objects, as well as adding them to their
     * respective JPanels</p>
     *
     *  @param parent an MainWindow, subclass of JFrame, allows relative location to be set to it.
     *  @param studentArrayList A <code>StudentArrayList</code> object passed to retrieve information from by
     *                          <code>SavePDF</code>
     */

    public CreatePDF(MainWindow parent, StudentArrayList studentArrayList){
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLayout(new GridBagLayout());

        //Initializing variables
        this.studentArrayList = studentArrayList;
        if(currentDir == null) {
            currentDir = getClass().getProtectionDomain().getCodeSource().getLocation().toString().replaceFirst("file:/", "").replaceAll("/", "\\\\");
            fileName = currentDir + "Student_Results.pdf";
        }

        //Creating JPanels
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setPreferredSize(new Dimension(200, 400));
        JPanel filePathPanel = new JPanel(new BorderLayout());
        filePathPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("File will be saved at"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JPanel optionsPanel = new JPanel(new GridLayout(4,1));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Options"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewPanel = new JPanel(new BorderLayout()){
            private Image preview = FileManager.getImage("Example.png").getImage();
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(preview, 15, 15, preview.getWidth(null), preview.getHeight(null), null);
            }
        };
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setPreferredSize(new Dimension(300,400));

        //Instantiating components
        warning = new JLabel();
        checkOverwrite();

        filePath = new JTextField(currentDir + fileName);

        skipNoMarks = new JCheckBox("<html>Skip students with no<br>marks provided</html>");

        browse = new JButton("Browse...");
        ok = new JButton("Ok");
        cancel = new JButton("Cancel");

        //Adding action listener
        browse.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);

        //Adding components to their respective panels:
        filePathPanel.add(filePath, BorderLayout.CENTER);
        filePathPanel.add(browse, BorderLayout.EAST);

        optionsPanel.add(skipNoMarks);
        optionsPanel.add(warning);
        optionsPanel.add(new JLabel());
        optionsPanel.add(new JLabel());

        buttonsPanel.add(ok);
        buttonsPanel.add(cancel);

        displayPanel.add(filePathPanel, BorderLayout.NORTH);
        displayPanel.add(optionsPanel, BorderLayout.CENTER);
        displayPanel.add(buttonsPanel, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.45;
        c.weighty = 1;
        add(displayPanel);
        c.weightx = 0.55;
        add(previewPanel);

        previewPanel.repaint();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        //setting the location
        pack();
        setLocation((parent.getX() + parent.getWidth() / 2) - this.getWidth() / 2, (parent.getY() + parent.getHeight() / 2) - this.getHeight() / 2);
        setVisible(true);
    }

    /**
     *  Checks if the file path shown in the file path JTextField already has a file.
     *  If so, it sets a warning message to users to see, in a red font.
     */

    private void checkOverwrite(){
        if(new File(fileName).exists())
            warning.setText("<html><font color = \"red\">NOTE: Your file location<br>will overwrite an<br>existing file!</font></html>");
        else
            warning.setText("");
    }

    /**
     *  Overrides actionPerformed method from ActionListener
     *  @see java.awt.event.ActionListener
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browse){
            JFileChooser fc = new JFileChooser(filePath.getText().replaceAll("\\\\", "/"));
            fc.setSelectedFile(new File(fileName));
            int confirm = fc.showSaveDialog(this);
            if(confirm == JFileChooser.APPROVE_OPTION){
                fileName = fc.getSelectedFile().toString();
                if(!fileName.endsWith(".pdf"))
                    fileName += ".pdf";
                currentDir = fc.getCurrentDirectory().toString();
                filePath.setText(fileName);
            }
            checkOverwrite();
        }
        else if (e.getSource() == ok){
            if(SavePDF.savePDF(this, studentArrayList, skipNoMarks.isSelected(), fileName))
                dispose();
        }
        else if (e.getSource() == cancel)
            dispose();
    }
}
