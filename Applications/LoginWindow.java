package Applications;

import Data.StudentArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**	
 *	<code>LoginWindow</code> allows the user to provide a valid username and password for access of student data from the Internet.
 *      
 *	@author         Johnson Wong
 *      @author		Konstantin Terziev
 */
public class LoginWindow extends JFrame implements ActionListener {
    /**
     *	Button widjets.
     */
    JButton login, exit;
    /**
     *	User uses this input field to her username.
     */
    JTextField usernameInput;
    /**
     *	User uses this input field to her password.
     */
    JPasswordField passwordInput;
    /**
     *	Error message if something has gone wrong. Most likely wrong username and/or password. Maybe a connection problem.
     */
    private JPanel errorMessage;
    
    /**
     *	This constructor shows a window with two text fields which the user type their username and password.
     */
    public LoginWindow()
    {
        //Initiase frame:
        super("Log In");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        try {
            setIconImage(FileManager.getImage("Logo24.png").getImage());
        }
        catch(NullPointerException npe)
        {
            System.out.println("Something is wrong with the Images folder. Could not load image");
        }
        
        //Initialise variables:
        errorMessage = new JPanel();
        errorMessage.setBackground(Color.WHITE);
        errorMessage.add(new JLabel("<html><div style=\\\"text-align: center;\\\"><font color=\"red\">Log In failed:<br>Your Username and/or Password is probably incorrect.</font></div></html>"));
        errorMessage.setVisible(false);
        
        //Add components:
        add(InformationWindow.createHeader("11OldMen - Major Coursework"), BorderLayout.NORTH);
        add(drawWidgets(), BorderLayout.CENTER);
        
        //Display:
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);// show the window center screen.
    }
    
    /**
     *	Set up the interface.
     */
    private JPanel drawWidgets()
    {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel passwordPanel = new JPanel();
        passwordPanel.setOpaque(false);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        login = new JButton("Log In");
        login.addActionListener(this);
        
        exit = new JButton("Exit");
        exit.addActionListener(this);
        
        KeyAdapter inputChange = new KeyAdapter() {
            
            public void keyReleased(KeyEvent ke)
            {
                if (errorMessage.isVisible() == true)
                {
                    removeErrorMessage();
                }
                else if (ke.getSource() == passwordInput)
                {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER)
                    {
                        login.setEnabled(false);
                        attemptLogIn();
                    }
                }
            }
        };
        
        usernameInput = new JTextField(); //planning to add shadow/ghost text to both
        usernameInput.setPreferredSize(new Dimension(250, 26));
        usernameInput.addKeyListener(inputChange);
        
        passwordInput = new JPasswordField();
        passwordInput.setPreferredSize(new Dimension(250, 26));
        passwordInput.addKeyListener(inputChange);
        
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameInput);
        
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordInput);
        
        buttonPanel.add(login);
        buttonPanel.add(exit);
        
        centerPanel.add(getPicture());
        centerPanel.add(loginPanel);
        centerPanel.add(passwordPanel);
        centerPanel.add(buttonPanel);
        
        return centerPanel;
    }
    
    /**
     *	Returns a logo if its present that will be placed above of the user inputs.
     *  
     *  @return <code>JPanel</code> - logo picture
     */
    private JPanel getPicture()
    {
        JPanel picturePanel = new JPanel(new BorderLayout());
        picturePanel.setBackground(Color.WHITE);
        try {
            picturePanel.add(new JLabel(FileManager.getImage("Logofinal.png")), BorderLayout.CENTER);
        }
        catch(NullPointerException npe)
        {
            System.out.println("Something is wrong with the Images folder. Could not load image");
        }
        return picturePanel;
    }
    
    /**
     *	Try to log in with the information provided. If not successful show an error message.
     *  <p> 
     *  Error is most probably caused by wrong username and password credentials.
     *  Maybe a Internet connection problem.
     */
    public void attemptLogIn()
    {
        char[] a = passwordInput.getPassword(); //must convert to char[] first
        String temp = String.valueOf(a); //converts char[] to String
        if(StudentArrayList.testConnection(usernameInput.getText(), temp)){
            Main.startMainWindow(usernameInput.getText(), temp);
            this.dispose();
        }
        else 
        {
            login.setEnabled(true);
            add(errorMessage, BorderLayout.SOUTH);
            errorMessage.setVisible(true);
            pack();
        }
    }
    
    /**
     *	Call this if the user changes her input.
     *  <p> 
     *  The error message will be removed.
     */
    public void removeErrorMessage()
    {
        remove(errorMessage);
        errorMessage.setVisible(false);
        pack();
    }
    
    /**
     *	User confirms input of username and password or she wants exit the program all together.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login){
            attemptLogIn();
        }
        else if (e.getSource() == exit){
            System.exit(0);
        }
    }
}
