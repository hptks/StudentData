package Applications.Email;

import Applications.FileManager;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Date;
import java.util.Scanner;

/**
 * Created by NIghtCrysIs on 2015/03/13.
 * <p>Extends JFrame</p>
 *
 * <code>EmailSettings</code> creates a window for users to
 * change the email settings. Settings are stored in settings.ini
 *
 * @author Johnson Wong
 * @since 1.7
 */

public class EmailSettings extends JFrame implements ActionListener{
    private static String host, port, username = "";
    private static boolean hasPassword = true, hasUsername = true;
    private static String[] knownServers = {"kclsmtp.kcl.ac.uk","smtp.gmail.com","smtp.live.com","smtp.mail.yahoo.com","smtp.virgin.net","smtp.btconnect.com","smtp.o2.com","smtp.orange.net","smtp.aol.com"};
    private static String[] knownPorts = {"587","587","465","587","465","465","25","465","465"};
    private static String[] knownAuthMethods = {"Username and password", "Username and no password", "Password and no username", "No username or password"};

    private JComboBox serverName, authMethod;
    private JTextField usernameInput, portInput;
    private JButton ok, cancel, defaultButton;
    private JLabel portLabel;

    /** Main constructor for <code>EmailSettings</code>.
     *
     *  @param parent the parent JFrame to set relativeLocation
     *
     */

    public EmailSettings(JFrame parent){
        setTitle("Email settings...");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initiate();
        setValues();
        setSize(400, 200);
        setResizable(false);
        setVisible(true);

        //setting the location
        setLocationRelativeTo(parent);
        setLocation((parent.getX() + parent.getWidth() / 2) - this.getWidth() / 2, (parent.getY() + parent.getHeight() / 2) - this.getHeight() / 2);
    }

    /**
     *  Initiates the variables, objects and adds them in their respective components
     */

    private void initiate(){
        setLayout(new BorderLayout());

        JPanel display = new JPanel();
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        display.setLayout(new GridLayout(1, 2));
        northPanel.setLayout(new GridLayout(4, 1));
        southPanel.setLayout(new GridLayout(4, 1));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        serverName = new JComboBox(knownServers);
        authMethod = new JComboBox(knownAuthMethods);

        usernameInput = new JTextField();
        portInput = new JTextField();

        ok = new JButton("Ok");
        cancel = new JButton("Cancel");
        defaultButton = new JButton("Default");

        portLabel = new JLabel("Port number:", SwingConstants.LEFT);

        serverName.setEditable(true);

        //Adding listeners
        serverName.addActionListener(this);
        authMethod.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        defaultButton.addActionListener(this);
        serverName.addActionListener(this);

        //Set border
        northPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Settings"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        southPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Security and Authentication"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        //Adding Components
        northPanel.add(new JLabel("Server/Host name:", SwingConstants.LEFT));
        northPanel.add(serverName);
        northPanel.add(portLabel);
        northPanel.add(portInput);

        southPanel.add(new JLabel("Authentication method:", SwingConstants.LEFT));
        southPanel.add(authMethod);
        southPanel.add(new JLabel("Username:", SwingConstants.LEFT));
        southPanel.add(usernameInput);

        display.add(northPanel);
        display.add(southPanel);

        buttonPanel.add(defaultButton);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        add(display, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        //load settings
        if(!loadSettings()) {
            setDefaultPortString();
            serverName.setSelectedIndex(0);
        }
        else{
            for(int x = 0; x < knownServers.length; x++)
                if(port.equals(knownServers[x])){
                    serverName.setSelectedIndex(x);
                    setDefaultPortString();
                    break;
                }
        }
    }

    /**
     *  Loads setting from Settings.ini. <b>This must be called at the beginning of the program.</b>
     *
     *  @return True if successfullu loaded Settings.ini. False otherwise (E.g. Settings.ini doesn't exist).
     */

    public static boolean loadSettings(){
        File file;
        try{
            file = FileManager.getFile("Settings.ini");
            FileReader input = new FileReader(file.getAbsoluteFile());

            //Attempting to read the contents of settings.ini
            String temp;
            BufferedReader write = new BufferedReader(input);

            if((temp = write.readLine())!=null) {
                host = temp;
            }
            if((temp = write.readLine())!=null) {
                port = temp;
            }
            if((temp = write.readLine())!=null) {
                //"Username and password", "Username and no password", "Password and no username", "No username or password"
                switch(temp){
                    case "0": hasUsername = true; hasPassword = true; break;
                    case "1": hasUsername = true; hasPassword = false; break;
                    case "2": hasUsername = false; hasPassword = true; break;
                    case "3": hasUsername = false; hasPassword = false; break;
                }
            }
            if((temp = write.readLine())!=null) {
                if (temp.equals("null"))
                    username = "";
                else
                    username = temp;
            }

            return true;
        }
        catch(Exception e){
            //File is not found. So use default settings.
            //e.printStackTrace();
            return false;
        }
    }

    /**
     *  Sets the values for each JComponent displayed on window
     *  using present private variables
     */

    private void setValues(){
        if(host != null) {
            if (!host.equals("null"))
                serverName.setSelectedItem(host);
            else serverName.setSelectedIndex(0);

            for(int x = 0; x < knownServers.length; x++){
                if(host.equals(knownServers[x])) {
                    setDefaultPortString();
                    break;
                }
                else
                    portLabel.setText("Port number:");
            }
        }

        usernameInput.setText(username);

        if(port != null)
            if(!port.equals("null"))
                portInput.setText(port);
            else portInput.setText(knownPorts[serverName.getSelectedIndex()]);

        if(hasPassword) {
            if(hasUsername)
                authMethod.setSelectedIndex(0);
            else authMethod.setSelectedIndex(2);
        }
        else {
            if(hasUsername)
                authMethod.setSelectedIndex(1);
            else authMethod.setSelectedIndex(3);
        }
    }

    /**
     *  Sets the default port number in respect to the server/host selected
     */

    private void setDefaultPortString(){
         portLabel.setText("<html>Port number: <font color = \"red\">Default " + knownPorts[serverName.getSelectedIndex()] + "</font></html>");
    }

    /**
     *  Saves the settings in a file called Settings.ini. Makes a call to
     *  FileManager for the purpose of saving the file.
     *
     *  @see Applications.FileManager
     */

    private void saveSettings(){
        String tempUsername = usernameInput.getText();
        if(tempUsername.equals(""))
            tempUsername = "null";
        String tempServer = serverName.getSelectedItem().toString();
        if(tempServer.equals(""))
            tempUsername = "null";
        String tempPort = portInput.getText();
        if(tempPort.equals(""))
            tempPort = "null";
        Date date = new Date();

        String content = tempServer + "\n" + tempPort + "\n" + authMethod.getSelectedIndex()
                + "\n" + tempUsername + "\n\nAutomatically generated at: " + date.toString();
        try{
            FileManager.SaveSettingsFile("Settings", content);
        }
        catch(IOException e1){
            JOptionPane.showMessageDialog(this, "Error occured when trying to save your settings.");
            e1.printStackTrace();
        }
    }

    /**
     *  Overrides actionPerformed from ActionListener
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == serverName){
            for(String a : knownServers) {
                if(serverName.getSelectedItem().toString().equals(a)) {
                    setDefaultPortString();
                    portInput.setText(String.valueOf(knownPorts[serverName.getSelectedIndex()]));
                    break;
                }
                else {
                    portLabel.setText("Port number: ");
                    portInput.setText("");
                }
            }
        }
        else if (e.getSource() == authMethod){
            hasPassword = (authMethod.getSelectedIndex() == 0 | authMethod.getSelectedIndex() == 2);
            hasUsername = (authMethod.getSelectedIndex() == 0 | authMethod.getSelectedIndex() == 1);
            usernameInput.setEnabled(hasUsername);
        }
        else if (e.getSource() == ok){
            saveSettings();
            host = serverName.getSelectedItem().toString();
            port = portInput.getText();
            username = usernameInput.getText();
            this.dispose();
        }
        else if (e.getSource() == cancel){
            this.dispose();
        }
        else if (e.getSource() == defaultButton){
            serverName.setSelectedIndex(0);
            host = serverName.getSelectedItem().toString();
            setDefaultPortString();
            username = "";
            hasUsername = true;
            hasPassword = true;
            usernameInput.setText("");
            setValues();
        }
    }

    /**
     *  Getter method for username (String)
     *
     *  @return username string for the username
     */

    public static String getUsername(){
        return username;
    }

    /**
     *  Getter method for port (String)
     *
     *  @return port string for the port
     */

    public static String getPort(){
        return port;
    }

    /**
     *  Getter method for host (String)
     *
     *  @return host string for the host/server address
     */

    public static String getHost(){
        return host;
    }

    /**
     *  Getter method for hasUsername (boolean)
     *
     *  @return hasUsername a boolean value of hasUsername
     */

    public static boolean getHasUsername(){
        return hasUsername;
    }

    /**
     *  Getter method for hasPassword (boolean)
     *
     *  @return hasUsername a boolean value of hasPassword
     */

    public static boolean getHasPassword(){
        return hasPassword;
    }
}
