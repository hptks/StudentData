package Applications;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

/**
 * Created by NIghtCrysIs on 16/03/2015.
 *
 * <code>FileManager</code> is a file management class.
 * <p>This class provides public static methods that can be accessed publicly.</p>
 *
 * @author Johnson Wong
 */
public class FileManager {

    /** Creates a .ini file along with the absolute addresses, title and its content provided.
     *
     *  @param title a string, name of file. (E.g. "Settings.ini")
     *  @param content a string, contents of the file
     *
     *  @throws IOException May be thrown during file output.
     */

    public static void SaveSettingsFile(String title, String content) throws IOException {
        // To test the output of the absolute path generated:
        // System.out.println(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()+title+".ini");

        File file = new File(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()+title+".ini");
        if(!file.exists())
            file.createNewFile();
        FileWriter output = new FileWriter(file.getAbsoluteFile());
        BufferedWriter write = new BufferedWriter(output);
        write.write(content);
        write.flush();
    }

    /**
     * Returns the path of the .ini file along with its absolute address
     *
     * @param filename a string, denotes the file name
     * @throws java.io.FileNotFoundException Occurs when the specified file path is non-existent
     * @return the absolute path of the file outside the src location, relative to the corresponding OS file path.
     */

    public static File getFile(String filename) throws FileNotFoundException{
        File file = new File(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()+filename);
        if(!file.exists())
            throw new FileNotFoundException();

        return file;
    }


    /** Loads the corresponding filename from the absolute path src/Images/
     *
     *  @param fileName a string, name of file. (E.g. "Image.png")
     *  @return an ImageIcon, the image loaded. Returns null if not found.
     */

    public static ImageIcon getImage(String fileName){
        try{
            Image image = ImageIO.read(new File(FileManager.class.getClass().getResource("/Images/"+fileName).getPath()));
            return new ImageIcon(image);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
