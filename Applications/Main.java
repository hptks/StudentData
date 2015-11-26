package Applications;

/**
 * <code>Window</code> which requires the user to type in his username and password
 * @author Johnson Wong
 */

public class Main {

    private static MainWindow mWindow;
    private static LoginWindow loginWindow;
    
    /**
     * <code>Creates</code> the login window
     * @param args default main function parameter
     */
    public static void main(String[] args) {
        loginWindow = new LoginWindow();
    }
    
    /**
     * <code>Dsiplays</code> the main window after providing the program with the required username and password
     * @param username the username received from the user
     * @param password the password received from the user
     */
    public static void startMainWindow(String username, String password) {
        mWindow = new MainWindow(username, password);
    }
}