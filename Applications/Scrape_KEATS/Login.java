package Applications.Scrape_KEATS;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by NIghtCrysIs on 2015/03/16.
 *
 * <code>Login</code> is a class for logging into Keats and retrieving information from it.
 * <p>It is called by the <code>Scrape</code> input dialog, where it provides this with
 * username, password, and the url link to retrieve information from.</p>
 * <p>Extends JDialog and implements ActionListener</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */
public class Login {
    /**
     *  Static method for <code>Login</code>. Uses dependencies <b>HttpUnit</b> in connecting to Keats.
     *
     *  @see com.gargoylesoftware.htmlunit
     *
     *  @param parent      The parent window. instanceof<code>Scrape</code>, to set relative locations to
     *  @param link        The url of which to retrieve information from.
     *  @param username    The username to log in KEATS with.
     *  @param password    The password to log in KEATS with.
     *
     *  @return results.asText() Returns the content of the url if login successful. Returns null otherwise.
     * */
    public static String login(Scrape parent, String link, String username, String password){
        try{
            WebClient client = new WebClient();

            //Settings
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setThrowExceptionOnFailingStatusCode(false);
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setCssEnabled(false);
            client.getOptions().setRedirectEnabled(true);
            client.getOptions().setUseInsecureSSL(true);
            client.getCookieManager().setCookiesEnabled(true);

            HtmlPage page = client.getPage("https://login-keats.kcl.ac.uk/");

            HtmlForm form = page.getFirstByXPath("//form[@action='https://keats.kcl.ac.uk/login/index.php']");

            HtmlInput usernameInput = form.getInputByName("username");
            usernameInput.setValueAttribute(username);
            HtmlInput passwordInput = form.getInputByName("password");
            passwordInput.setValueAttribute(password);

            page = form.getInputByValue("Log in").click();

            HtmlPage results = client.getPage(link);

            client.closeAllWindows();
            return results.asText();
        }
        catch(MalformedURLException e){
            JOptionPane.showMessageDialog(parent, "The URL you have provided is not recognised. Please double check your " +
                    "input and try again.", "MalformedURLException found.", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(parent, "An error has occurred when attempting to read information from the " +
                    "server. The input could be interrupted by external processes.", "IOException found.", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }
}
