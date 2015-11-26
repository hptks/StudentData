package Applications.Scrape_KEATS;

import Applications.MainWindow;
import Data.Student;
import Data.StudentArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * Created by NIghtCrysIs on 2015/03/16.
 *
 * <code>Scrape</code> is a input JDialog window for scraping participation data from KEATS.
 * <p>Provides basic input boxes for the user to enter their username, password and the url to retrieve information from
 * <p>Extends JDialog and implements ActionListener, KeyListener</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */
public class Scrape extends JDialog implements ActionListener, KeyListener {
    private HashMap<String, Map.Entry<String, String>> dates;
    private JTextField usernameInput, urlInput;
    private JPasswordField passwordInput;
    private JButton ok, cancel;
    private String participationString;
    private StudentArrayList studentArrayList;

    /**
     *  Constructor for <code>Scrape</code>. Sets the basic layout and instantiates objects,
     *   as well as adding ActionListener and KeyListener to JTextFields and JButtons.
     *
     *  @param parent      The parent window. instanceof<code>Scrape</code>, to set relative locations to
     *  @param studentArrayList The studentArrayList passed by <code>MainWindow</code>. Used for storing student participation data.
     * */

    public Scrape(MainWindow parent, StudentArrayList studentArrayList){
        this.studentArrayList = studentArrayList;

        setTitle("'Scrape' participation data...");
        setModalityType(ModalityType.APPLICATION_MODAL);
        JPanel container = new JPanel(new BorderLayout());
        JPanel displayPanel = new JPanel(new GridLayout(3,1));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel userInputPanel = new JPanel(new GridLayout(2,1));
        JPanel passwordInputPanel = new JPanel(new GridLayout(2,1));
        JPanel urlInputPanel = new JPanel(new GridLayout(2,1));

        userInputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Username"), BorderFactory.createEmptyBorder(5,5,5,5)));
        passwordInputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Password"), BorderFactory.createEmptyBorder(5,5,5,5)));
        urlInputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("KEATS url"), BorderFactory.createEmptyBorder(5,5,5,5)));

        usernameInput = new JTextField();
        userInputPanel.add(new JLabel("Please your KCL username:"));
        userInputPanel.add(usernameInput);
        userInputPanel.addKeyListener(this);

        passwordInput = new JPasswordField();
        passwordInputPanel.add(new JLabel("Please your password:"));
        passwordInputPanel.add(passwordInput);
        passwordInput.addKeyListener(this);

        urlInput = new JTextField();
        urlInputPanel.add(new JLabel("Please enter the URL of KEATS of which participation data can be fetched:"));
        urlInputPanel.add(urlInput);
        urlInputPanel.addKeyListener(this);

        displayPanel.add(userInputPanel);
        displayPanel.add(passwordInputPanel);
        displayPanel.add(urlInputPanel);

        container.add(displayPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        ok = new JButton("Ok");
        ok.addActionListener(this);
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        setContentPane(container);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocation(getParent().getX() / 2 - getX() / 2, getParent().getY() / 2 - getY() / 2);
        pack();
        setResizable(false);

        //setting the location
        setLocation((parent.getX() + parent.getWidth() / 2) - this.getWidth() / 2, (parent.getY() + parent.getHeight()/2)-this.getHeight()/2);
        setVisible(true);
    }

    /**
     *  Method for producing the a list of Map values with key <String, String>.
     *  After being called and executed, it sends the data to its parent window and disposes itself.
     **/

    private void produceList(){
        dates = new HashMap<String, Map.Entry<String, String>>();
        Scanner inputLine = new Scanner(participationString);
        Scanner input;
        String temp, moduleName="", email, time;

        //Gets module name
        if(inputLine.hasNext())
            moduleName = inputLine.next();

        while(inputLine.hasNextLine()){
            //Reads a new line each time
            String line = inputLine.nextLine();
            input = new Scanner(line);
            temp = "";
            email = "";
            time = "";

            if(input.hasNext())
                if(input.next().equals("unchecked")) {
                    while (input.hasNext()) {
                        temp = input.next();
                        if (temp.endsWith(".com")) {
                            email = temp;
                            break;
                        }
                    }

                    //Checks for integer input to skip location data.
                    //Will exit loop if it reaches end, so it will also include "now" as input.
                    while (input.hasNext()) {
                        try {
                            temp = input.next();
                            Integer.parseInt(temp);
                            break;
                        } catch (Exception e) {
                        }
                    }

                    time += temp;

                    //get time data until end of line
                    while (input.hasNext())
                        time += " " + input.next();

                    //Add map with email and time
                    Map.Entry<String, String> tempEntry = new AbstractMap.SimpleEntry<String, String>(moduleName, time);
                    dates.put(email, tempEntry);

                    //Testing output
                    //System.out.println(email + " " + time);
                }
        }

        addDateToStudentArrayList();
        dispose();
    }

    /**
     *  Method that connects to KEATS by calling Login.login(...).
     * */

    private void addDateToStudentArrayList(){
        String key;
        ArrayList<Student> arrayList = studentArrayList.getList();
        for(Map.Entry<String, Map.Entry<String, String>> temp : dates.entrySet()){
            key = temp.getKey();
            for(Student student : arrayList)
                if(key.equals(student.getSEmail())){
                    student.setLastAccess(temp.getValue());
                    //assuming every student entry is unique
                    break;
                }
        }
    }

    /**
     *  Method that connects to KEATS by calling Login.login(...).
     * */

    private void connectToKeats(){
        if(checkUsername())
            if(checkPassword())
                if(checkURL()){
                    String password = new String(passwordInput.getPassword());
                    participationString = Login.login(this, urlInput.getText(),usernameInput.getText(),password);
                    if(participationString != null)
                        produceList();
                }
    }

    /**
     *  Checks if the username is valid. Returns true if so, false otherwise.
     * */

    private boolean checkUsername(){
        if(usernameInput.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Your username cannot be empty!");
            return false;
        }
        return true;
    }

    /**
     *  Checks if the password is valid. Returns true if so, false otherwise.
     * */

    private boolean checkPassword(){
        if(passwordInput.getPassword().length==0){
            JOptionPane.showMessageDialog(this, "Your password cannot be empty!");
            return false;
        }
        return true;
    }

    /**
     *  Checks if the url is valid. Returns true if starts with http:// or https://, false otherwise.
     * */

    private boolean checkURL(){
        String url = urlInput.getText();
        if(url.equals("")){
            JOptionPane.showMessageDialog(this, "The KEATS url cannot be empty!");
            return false;
        }
        else if(url.startsWith("http://")) {
            return true;
        }
        else if (url.startsWith("https://"))
            return true;
        else
            JOptionPane.showMessageDialog(this, "Link to participation data must begin with 'http://' or 'https://'");
        return false;
    }

    /**
     *  Overrides actionPerformed
     * */

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == ok){
            connectToKeats();
        }
        else if(e.getSource() == cancel){
            dispose();
        }
    }

    /**
     *  Overrides keyTyped
     * */

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     *  Overrides keyPressed
     * */

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_ENTER)
            connectToKeats();
    }

    /**
     *  Overrides keyReleased
     * */

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
