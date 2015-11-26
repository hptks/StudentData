package Applications.Email;

import Applications.CSVReader;
import Applications.FileManager;
import Data.*;

import javax.mail.MessagingException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by NIghtCrysIs on 2015/03/13.
 *
 * <code>EmailEditor</code> creates a window and handles the functionality of the Email program.
 * <p>Extends JFrame and implements ActionListener, PropertyChangeLIstener, MouseListener</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */
public class EmailEditor extends JFrame implements ActionListener, PropertyChangeListener, MouseListener {
    private JPanel draftDisplay;
    private JPanel previewDisplay;
    private JPanel previewPanel;
    private JPanel statusPanel;
    private JPanel buttonPanel;

    private JTextPane header, footer, preview;
    private JTextField title;
    private JButton toPreview, toDraft, toSend, help, bold, italic, underline, color, emphasis, addLink, studentName, emailSettings;
    private JRadioButton selectAll, selectNone;
    private JTable table, recipientList;
    private JScrollPane listScroller;
    private JLabel statusBar;
    private JComboBox fontSize;
    private JMenuBar menuBar;
    private boolean headerSelected = true;
    private String colorChosen;

    private StudentArrayList studentArrayList;
    private ArrayList<Student> studentObjects;

    /** Constructor for <code>EmailEditor</code> and initializes all objects,
     *  variables and the JFrame settings
     *
     * @see Data.StudentArrayList
     *
     *  @param studentArrayList A StudentArrayList object
     *
     */

    public EmailEditor(StudentArrayList studentArrayList){
        setTitle("E-express V0.1");
        this.studentArrayList = studentArrayList;
        setLayout(new BorderLayout());

        //setup methods
        setupJMenuBar();
        setupDisplay(studentArrayList.produceList());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setVisible(true);
        setMinimumSize(getSize());

        //Loads the email settings from Settings.ini if not already. Fail safe feature.
        EmailSettings.loadSettings();
    }

    /**
     * Sets up the display and initializes all variables and objects,
     * adds components to their respective JPanels, adds relevant listeners,
     * and sets up the format of the display.
     */

    private void setupDisplay(String[] studentNameList){
        //Initializing variables
        JPanel displayPanel = new JPanel();
        JPanel tablePanel = new JPanel();
        JPanel tableOptionsPanel = new JPanel();
        JPanel headerPanel = new JPanel();
        JPanel footerPanel = new JPanel();
        JPanel textDisplayPanel = new JPanel();
        JPanel titlePanel = new JPanel();
        previewPanel = new JPanel();
        statusBar = new JLabel(" ");
        draftDisplay = new JPanel();
        previewDisplay = new JPanel();
        statusPanel = new JPanel();
        buttonPanel = new JPanel();
        selectAll = new JRadioButton("Select all");
        selectNone = new JRadioButton("Select none");
        toPreview = new JButton("Next", FileManager.getImage("RightArrow.png"));
        toDraft = new JButton("Previous", FileManager.getImage("LeftArrow.png"));
        toSend = new JButton("Send", FileManager.getImage("MailIcon.png"));
        emailSettings = new JButton("Email settings...", FileManager.getImage("SettingsIcon.png"));
        header = new JTextPane();
        footer = new JTextPane();
        preview = new JTextPane();
        title = new JTextField("Title");

        header.setText("This text comes before student marks.");
        footer.setText("This text comes after student marks.");

        //Setting up actionListeners
        toPreview.addActionListener(this);
        toDraft.addActionListener(this);
        toSend.addActionListener(this);
        selectAll.addActionListener(this);
        selectNone.addActionListener(this);
        emailSettings.addActionListener(this);
        header.addMouseListener(this);
        footer.addMouseListener(this);

        //Setting layout
        statusPanel.setLayout(new BorderLayout());
        draftDisplay.setLayout(new GridBagLayout());
        previewDisplay.setLayout(new BorderLayout());
        titlePanel.setLayout(new BorderLayout());
        displayPanel.setLayout(new BorderLayout());
        tablePanel.setLayout(new BorderLayout());
        headerPanel.setLayout(new BorderLayout());
        footerPanel.setLayout(new BorderLayout());
        textDisplayPanel.setLayout(new GridLayout(2, 1));
        tableOptionsPanel.setLayout(new GridLayout(1, 2));

        preview.setEditable(false);

        //Sets up the scrollable list display
        String[] columnTabs = {"Student names","Send?"};
        table = new JTable(new CustomTableModel(studentNameList, columnTabs));
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.addPropertyChangeListener(this);

        //Setting up Scroll panes
        JScrollPane tableScroll = new JScrollPane(table);
        JScrollPane headerScroll = new JScrollPane(header);
        JScrollPane footerScroll = new JScrollPane(footer);
        JScrollPane previewScroll = new JScrollPane(preview);

        //Adding components
        titlePanel.add(new JLabel("Title: "), BorderLayout.WEST);
        titlePanel.add(title, BorderLayout.CENTER);
        headerPanel.add(headerScroll, BorderLayout.CENTER);
        headerPanel.add(new JLabel("Header:"), BorderLayout.NORTH);
        footerPanel.add(footerScroll, BorderLayout.CENTER);
        footerPanel.add(new JLabel("Footer:"), BorderLayout.NORTH);
        textDisplayPanel.add(headerPanel);
        textDisplayPanel.add(footerPanel);
        tableOptionsPanel.add(selectAll);
        tableOptionsPanel.add(selectNone);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(tableOptionsPanel, BorderLayout.NORTH);
        displayPanel.add(titlePanel, BorderLayout.NORTH);
        displayPanel.add(textDisplayPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(emailSettings);
        buttonPanel.add(toDraft);
        buttonPanel.add(toSend);
        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(toPreview, BorderLayout.EAST);
        previewDisplay.add(previewScroll, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.weighty = 1;
        draftDisplay.add(tablePanel, c);

        c.gridx = 1;
        c.weightx = 0.8;
        draftDisplay.add(displayPanel, c);

        this.add(draftDisplay, BorderLayout.CENTER);
        this.add(statusPanel, BorderLayout.SOUTH);

        selectAll.setSelected(false);
        selectNone.setSelected(true);
    }

    /**
     *  Sets up the JMenuBar and its corresponding JButtons
     */

    private void setupJMenuBar(){
        //Instances for JMenuBar
        menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        //Instantiating JButtons
        help = new JButton("Help");
        bold = new JButton("<html><b>B</b></html>");
        underline = new JButton("<html><u>u</u></html>");
        italic = new JButton("<html><i>i</i></html>");
        emphasis = new JButton("<html><em>e</em></html>");
        addLink = new JButton("Add hyperlink");
        studentName = new JButton("Add student name");
        color = new JButton("Color");

        String[] fontSizes = {"8", "10", "12", "14", "16", "18", "20", "24", "28", "32", "48", "64", "72"};
        fontSize = new JComboBox(fontSizes);
        fontSize.setEditable(true);

        //Setting background color for JButtons
        help.setBackground(Color.WHITE);
        bold.setBackground(Color.WHITE);
        emphasis.setBackground(Color.WHITE);
        addLink.setBackground(Color.WHITE);
        underline.setBackground(Color.WHITE);
        italic.setBackground(Color.WHITE);
        studentName.setBackground(Color.WHITE);
        color.setBackground(Color.WHITE);

        //Adding action listener
        help.addActionListener(this);
        bold.addActionListener(this);
        underline.addActionListener(this);
        italic.addActionListener(this);
        emphasis.addActionListener(this);
        addLink.addActionListener(this);
        studentName.addActionListener(this);
        color.addActionListener(this);
        fontSize.addActionListener(this);

        menuBar.add(help);
        menuBar.add(bold);
        menuBar.add(underline);
        menuBar.add(italic);
        menuBar.add(emphasis);
        menuBar.add(addLink);
        menuBar.add(color);
        menuBar.add(fontSize);
        menuBar.add(studentName);
        setJMenuBar(menuBar);
    }

    /**
     *  Method for switching to Draft Screen. Removes and adds relevant components.
     */

    private void gotoDraftScreen(){
        setJMenuBar(menuBar);
        recipientList = null;
        remove(previewPanel);
        remove(statusPanel);
        previewPanel.removeAll();
        statusPanel.removeAll();
        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(toPreview, BorderLayout.EAST);
        add(draftDisplay, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        repaint();
        revalidate();
    }

    /**
     *  Method for switching to Preview Screen. Removes and adds relevant components.
     */

    private void gotoPreviewScreen(){
        setJMenuBar(null);
        generateRecipientList();
        listScroller = new JScrollPane(recipientList);
        remove(draftDisplay);
        remove(statusPanel);
        setPreviewText();
        statusPanel.removeAll();
        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(buttonPanel, BorderLayout.EAST);
        statusPanel.revalidate();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(new JLabel("Title "), BorderLayout.WEST);
        titlePanel.add(title, BorderLayout.CENTER);

        previewPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.2;
        c.weighty = 0;
        previewPanel.add(new JLabel("Number of recipients: " + recipientList.getRowCount()));
        c.gridx = 1;
        previewPanel.add(titlePanel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1;
        previewPanel.add(listScroller, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.8;
        //c.gridheight = 2;
        c.weighty = 1;
        previewPanel.add(previewDisplay, c);
        previewPanel.setPreferredSize(previewPanel.getPreferredSize());

        add(previewPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        repaint();
        revalidate();
    }

    /**
     *  Sets the preview text for the JTextPane <code>preview</code>
     */

    private void setPreviewText(){
        preview.setContentType("text/html");
        Date date = new Date();
        preview.setText(header.getText().replaceAll("(\r\n|\n)", "<br>") + "<br><br><i>Example data given:</i><br>Module name: 4CCS1PRA<br>" +
                "Assessment: Assessment 001<br>Candidate number: 001<br>Mark: E.g. 0 out of 100<br>" +
                "Grade: D<br><br>Module name: 4CCS1PEP<br>" +
                "Assessment: Assessment 001<br>Candidate number: 001<br>Mark: E.g. 0 out of 100<br>" +
                "Grade: D<br><br>" + footer.getText().replaceAll("(\r\n|\n)", "<br>") + "<br><br>This e-mail is automatically " +
                "generated by E-express.<br>Created: " + date.toString());
    }

    /**
     *  Generates the recipient list from the JTable <code>table</code> and does
     *  so by checking the boolean values for each student name.
     */

    private void generateRecipientList(){
        ArrayList<String> tempArray = new ArrayList<>();
        ArrayList<Student> tempStudentList = studentArrayList.getList();
        studentObjects = new ArrayList<>();

        for(int a = 0; a < table.getRowCount(); a++){
            if(table.getValueAt(a, 1).toString().equals("true")){
                tempArray.add(table.getValueAt(a, 0).toString());
                studentObjects.add(tempStudentList.get(a));
            }
        }
        tempStudentList = null;
        String[] tempColumn = {"Recipient names"};
        String[] tempList = new String[tempArray.size()];
        tempArray.toArray(tempList);
        recipientList = new JTable(new CustomTableModel(tempList, tempColumn));
        recipientList.setFillsViewportHeight(true);
        recipientList.setPreferredScrollableViewportSize(recipientList.getPreferredSize());
    }

    /**
     *  Method for checking if all values in <code>table</code> is true or false.
     *
     *  @param model the boolean value that is compared against
     *  @return a boolean if all values in <code>table</code> is true or false.
     */

    private boolean tableCheckAll(boolean model){
        String value;
        if(model)
            value = "true";
        else
            value = "false";

        for(int x = 0; x < table.getRowCount(); x++){
            if(!table.getValueAt(x, 1).toString().equals(value))
                return false;
        }
        return true;
    }

    /**
     * Sets all values in the table as the specified boolean value passed
     *
     * @param model the boolean value that will be set to all name entries in <code>table</code>
     */

    private void tableSetAll(boolean model){
        for(int x = 0; x < table.getRowCount(); x++){
            table.setValueAt(model, x, 1);
        }
        table.revalidate();
    }

    /**
     * Method for sending email. Responsible for generating recipient emails,
     * prompting for username and password inputs.
     */

    private void sendEmail(){
        String[] recipients = generateRecipientEmails();
        int confirm = JOptionPane.OK_OPTION;
        JPasswordField passwordField = new JPasswordField();
        JTextField usernameInput = new JTextField();
        JPanel display = new JPanel();
        display.setLayout(new GridLayout(2, 1));
        display.add(new JLabel("Please enter you username or email"));
        display.add(usernameInput);
        String username = EmailSettings.getUsername();
        if(EmailSettings.getHasUsername()) {
            if(username.equals("")) {
                confirm = JOptionPane.showConfirmDialog(this, display, "Enter username", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                username = usernameInput.getText();
            }
        }
        if(confirm == JOptionPane.OK_OPTION) {
            display.removeAll();
            display.setLayout(new GridLayout(2, 1));
            display.add(new JLabel("Please enter your password."));
            display.add(passwordField);
            confirm = JOptionPane.OK_OPTION;
            if(EmailSettings.getHasPassword())
                confirm = JOptionPane.showConfirmDialog(this, display, "Enter password.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.OK_OPTION) {
                new RunningThread(this, username, new String(passwordField.getPassword()), recipients, studentObjects, new SendingScreen(this, recipients.length)).start();
            }
        }
    }

    /**
     *  Generates a list of recipient emails by comparing the
     *  boolean value of each entry in <code>table</code>, gets the email address
     *  by its index and returns the resultant String array list.
     *
     *  @return studentEmail a String array list that stores all email addresses
     *  of selected Student entries in <code>table</code>
     */

    private String[] generateRecipientEmails(){
        String[] studentEmails = studentArrayList.produceStudentEmailList();
        ArrayList<String> recipients = new ArrayList<>();
        for(int x = 0; x < table.getRowCount(); x++){
            if(table.getValueAt(x, 1).toString().equals("true"))
                recipients.add(studentEmails[x]);
            if(recipients.size() == recipientList.getRowCount())
                break;
        }
        studentEmails = recipients.toArray(new String[recipients.size()]);
        return studentEmails;
    }

    /**
     * Appends HTML markup with the type specified.
     *
     * @param type A string that specifies what kind of markup will be added. E.g. b for bold
     */

    private void appendHTMLMarkUp(String type){
        String toAppend;
        if(headerSelected){
            toAppend = header.getSelectedText();
            if(toAppend!=null) {
                int start = header.getSelectionStart();
                String replacement = "<" + type + ">" + toAppend + "</"+type+">";
                header.replaceSelection(replacement);
                header.setSelectionStart(start);
                header.setSelectionEnd(start+replacement.length());
            }
        }
        else {
            toAppend = footer.getSelectedText();
            if(toAppend!=null) {
                int start = footer.getSelectionStart();
                String replacement = "<" + type + ">" + toAppend + "</"+type+">";
                footer.replaceSelection(replacement);
                footer.setSelectionStart(start);
                footer.setSelectionEnd(start + replacement.length());
            }
        }
    }

    /**
     * Appends color markup with the provided color in hexcode format
     *
     * @param hexcode a string, containin the hexadecimal value of a color in RGB format
     */

    private void appendColorMarkUp(String hexcode){
        String toAppend;
        if(headerSelected){
            toAppend = header.getSelectedText();
            if(toAppend != null) {
                int start = header.getSelectionStart();
                String replacement = "<font color = \"" + hexcode + "\">" + toAppend + "</font>";
                header.replaceSelection(replacement);
                header.setSelectionStart(start);
                header.setSelectionEnd(start + replacement.length());
            }
        }
        else {
            toAppend = footer.getSelectedText();
            if(toAppend != null) {
                int start = footer.getSelectionStart();
                String replacement = "<font color = \"" + hexcode + "\">" + toAppend + "</font>";
                footer.replaceSelection(replacement);
                footer.setSelectionStart(start);
                footer.setSelectionEnd(start + replacement.length());
            }
        }
    }

    /**
     * Appends the font size markup with the provided size
     *
     * @param size a string, that denotes the size of the font
     */

    private void appendFontMarkUp(String size){
        String toAppend;
        if(headerSelected){
            toAppend = header.getSelectedText();
            if(toAppend!=null) {
                int start = header.getSelectionStart();
                String replacement = "<font size = \"" + size + "\">" + toAppend + "</font>";
                header.replaceSelection(replacement);
                header.setSelectionStart(start);
                header.setSelectionEnd(start + replacement.length());
            }
        }
        else {
            toAppend = footer.getSelectedText();
            if(toAppend!=null) {
                int start = footer.getSelectionStart();
                String replacement = "<font size = \"" + size + "\">" + toAppend + "</font>";
                footer.replaceSelection(replacement);
                footer.setSelectionStart(start);
                footer.setSelectionEnd(start + replacement.length());
            }
        }
    }

    /**
     *  Appends the html link markup. Prompts the user to enter a link reference
     *  and the representing text of the link. It then appends it to the corresponding
     *   selected JTextPane <code>header</code> or <code>footer</code>.
     */

    private void appendLink(){
        JPanel tempDisplay = new JPanel();
        tempDisplay.setLayout(new GridLayout(4, 1));
        JTextField linkInput = new JTextField("");
        JTextField hyperlinkInput = new JTextField("");
        tempDisplay.add(new JLabel("Link reference: (What website it leads to)"));
        tempDisplay.add(hyperlinkInput);
        tempDisplay.add(new JLabel("Representing text: (How it is represented"));
        tempDisplay.add(linkInput);
        int confirm;
        while(true) {
            confirm = JOptionPane.showConfirmDialog(this, tempDisplay, "Add hyperlink", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(confirm == JOptionPane.CANCEL_OPTION)
                break;
            if(linkInput.getText().equals(""))
                JOptionPane.showMessageDialog(this, "Representing text cannot be empty!");
            else if(hyperlinkInput.getText().equals(""))
                JOptionPane.showMessageDialog(this, "Hyperlink cannot be empty!");
            else break;
        }
        if (confirm == JOptionPane.OK_OPTION){
            if(headerSelected){
                header.replaceSelection("<a href = \"" + hyperlinkInput.getText() + "\">" + linkInput.getText() + "</a>");
            }
            else{
                footer.replaceSelection("<a href = \"" + hyperlinkInput.getText() + "\">" + linkInput.getText() + "</a>");
            }
        }
    }

    /**
     *  Adds a mark for student names that can be later converted as the name of recipient
     */

    public void appendStudentName(){
        if(headerSelected)
            header.replaceSelection("##StudentName##");
        else
            footer.replaceSelection("##StudentName##");
    }

    /**
     *  Overrides actionPerformed from ActionListener
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == selectAll){
            selectAll.setSelected(true);
            selectNone.setSelected(false);
            tableSetAll(true);
            table.repaint();
        }
        else if(e.getSource() == selectNone){
            selectNone.setSelected(true);
            selectAll.setSelected(false);
            tableSetAll(false);
            table.repaint();
        }
        else if(e.getSource() == toDraft)
            gotoDraftScreen();
        else if(e.getSource() == toPreview)
            gotoPreviewScreen();
        else if(e.getSource() == toSend){
            if(recipientList.getRowCount()==0)
                JOptionPane.showMessageDialog(this, "You have not selected any students to send to!");
            else sendEmail();
        }
        else if(e.getSource() == help)
            new Help(this);
        else if(e.getSource() == bold)
            appendHTMLMarkUp("b");
        else if(e.getSource() == italic)
            appendHTMLMarkUp("i");
        else if(e.getSource() == underline)
            appendHTMLMarkUp("u");
        else if(e.getSource() == emphasis)
            appendHTMLMarkUp("em");
        else if(e.getSource() == addLink){
            appendLink();
        }
        else if(e.getSource() == color){
            new ColorPicker(this);
            color.setText("<html><font color = \""+colorChosen+"\">Color</font></html>");
            appendColorMarkUp(colorChosen);
        }
        else if (e.getSource() == studentName)
            appendStudentName();
        else if(e.getSource() == fontSize & e.getActionCommand().equals("comboBoxChanged"))
            appendFontMarkUp((String)fontSize.getSelectedItem());
        else if(e.getSource() == emailSettings)
            new EmailSettings(this);
    }

    /**
     *  Overrides propertyChange from PropertyChangeListener
     */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() == table){
            if(!table.isEditing()) {
                if(selectAll.isSelected()){
                    selectAll.setSelected(false);
                }
                else if(selectNone.isSelected()){
                    selectNone.setSelected(false);
                }

                if(tableCheckAll(true)) selectAll.setSelected(true);
                else if(tableCheckAll(false)) selectNone.setSelected(true);
            }
        }
    }

    /**
     * Overrides mouseCLicked from MouseListener
     */

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Overrides mousePressed from MouseListener
     */

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getSource() == header){
            headerSelected = true;
        }
        if(e.getSource() == footer){
            headerSelected = false;
        }
    }

    /**
     * Overrides mouseReleased from MouseListener
     */

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Overrides mouseEntered from MouseListener
     */

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Overrides mouseExited from MouseListener
     */

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * <code>RunningThread</code> is a private class within EmailEditor.
     * It runs on a separate thread that sends email and updates the "sending" <code>SendingScreen</code>
     *
     * @author Johnson Wong
     * @since 1.7
     */

    private class RunningThread extends Thread{
        private String username, host, port, password;
        private String[] recipients;
        private ArrayList<Student> studentObjects;
        private SendingScreen sending;
        private EmailEditor parent;

        /** Constructor for <code>CustomTableModel</code>.
         *
         *  @param parent EmailEditor, for the purpose in setting relative location
         *  @param username A string, contains the username
         *  @param password A string, contains the password
         *  @param recipients A String array, contains the email addresses of recipients
         *  @param studentObjects ArrayList that contains <code>Students</code> objects
         *  @param sending a <code>SendingScreen</code>, allows the separate thread to update its progress
         *
         */

        public RunningThread(EmailEditor parent, String username, String password, String[] recipients, ArrayList<Student> studentObjects, SendingScreen sending){
            this.parent = parent;
            this.username = username;
            this.password = password;
            this.recipients = recipients;
            this.sending = sending;
            this.studentObjects = studentObjects;

            host = EmailSettings.getHost();
            port = EmailSettings.getPort();
        }

        /**
         * Overrides run. Creates the content of the email in the form of a String, then sends the email
         *  using <code>SendEmail</code>'s static methods.
         */

        @Override
        public void run() {
            int counter = 0;
            int noResults = 0;
            try {
                //Sending emails
                for (int x = 0; x < recipients.length; x++) {
                    String toSend = "";


                    ArrayList<AssessmentResult> tempResults = studentObjects.get(x).getStudentExamResults();


                    toSend += header.getText();
                    if(tempResults.size()!=0){
                        toSend += "\nCandidate Number: " + tempResults.get(0).getField("Cand Key") + "\n";
                        toSend += "\nYear: " + tempResults.get(0).getField("Year") + "\n\n";

                        for(AssessmentResult a : tempResults){
                            toSend += "-------------------------------------\n";
                            toSend += "Module: " + a.getField("Module") + "\n";
                            toSend += "Assessment: " + a.getField("Ass") + "\n";
                            toSend += "Mark: " + a.getField("Mark") + "\n";
                            if(a.getField("Grade") != null)
                                toSend += "Grade: " + a.getField("Grade") + "\n";
                            else
                                toSend += "Grade: Not applicable\n";
                        }


                        toSend += footer.getText();

                        JTextPane container = new JTextPane();
                        container.setContentType("text/html");
                        Date date = new Date();

                        toSend = toSend.replaceAll("##StudentName##", recipientList.getValueAt(x, 0).toString());

                        container.setText(toSend.replaceAll("(\r\n|\n)", "<br>") + "<br><br>This e-mail is automatically " +
                                "generated by E-express.<br>Created: " + date.toString());
                        toSend = container.getText();

                        //You can replace recipients[x] with a string of your email to test if it works or not. E.g. "youraddress@gmail.com"
                        SendMail.SendMail(username, password, title.getText(), toSend, recipients[x], host, port);
                    }
                    else{
                        counter--;
                        noResults++;
                    }
                    sending.update();
                    counter++;
                }
                if(noResults!=0)
                    JOptionPane.showMessageDialog(parent, noResults + " student(s) has no assessment results available to be sent!");
                JOptionPane.showMessageDialog(parent, counter + " message(s) successfully sent!");
            }
            catch(MessagingException ex){
                JOptionPane.showMessageDialog(parent, "Authentication failed! Please check your email settings and re-enter your username/email and password!");
                if(counter==0)
                    JOptionPane.showMessageDialog(parent, "Failed to send messages.");
                else
                    JOptionPane.showMessageDialog(parent, counter + " message(s) successfully sent!");
                sending.dispose();
                ex.printStackTrace();
            }
        }
    }

    /**
     * Stores the chosen color in hexcode format to colorChosen
     *
     * @param hexcode String object, shows the color chosen, presented in hexcode format.
     */

    public void setColorPicker(String hexcode){
        colorChosen = hexcode;
    }
}
