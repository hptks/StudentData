package Applications.CreatePDF;

import Data.AssessmentResult;
import Data.Student;
import Data.StudentArrayList;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by NIghtCrysIs on 2015/03/20.
 *
 * <code>SavePDF</code> is a color picking window.
 * <p>Provides the ability to save a PDF file</p>
 * <p>This class utilizes PDFBox. This includes dependencies fontbox, pdfbox, jempbox and j-commons</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */

public class SavePDF {
    private static boolean hasEntry;
    private static final int maximumModulesPerColumn = 8;
    private static final int titleSize = 14;
    private static final int textSize = 10;
    private static final int resultTextSize = 6;
    private static final int pageScreenOffset = 72;
    private static final PDFont textFont = PDType1Font.COURIER;
    private static final PDFont textFontBold = PDType1Font.COURIER_BOLD;

    /**
     *  A public static method to save the PDF file.
     *
     *  @param parent              An instance of <code>CreatePDF</code>, allows the ability to show
     *                             JOptionPanes if an error arises with its location relative to it.
     *  @param studentArrayList    An instance of StudentArrayList passed from <code>MainWindow</code>,
     *                             then <code>CreatePDF</code> to retrieve information from.
     *  @param skipNoMarks         A boolean, if true the resultant PDF will skip all student entries
     *                             with no assessment results provided. Includes all student entries
     *                             otherwise.
     *  @param fileName            A String, the file name of which the file will take and the path
     *                             to be stored under.
     *
     *  @return true/false         Returns true to close the <code>CreatePDF</code> window if success.
     *      `                      Returns false to keep it open otherwise.
     */

    public static boolean savePDF(CreatePDF parent, StudentArrayList studentArrayList, boolean skipNoMarks, String fileName){
        PDDocument document;
        hasEntry = false;
        try{
            document = new PDDocument();

            ArrayList<Student> array = studentArrayList.getList();

            if (skipNoMarks)
                for (Student student : array) {
                    if (student.getStudentExamResults().size() != 0) {
                        createPage(student, document);
                        hasEntry = true;
                    }
                }
            else {
                hasEntry = true;
                for (Student student : array) {
                    createPage(student, document);
                }
            }

            if(!hasEntry) {
                JOptionPane.showMessageDialog(parent, "There are no student entries with marks. A file will not be created.\n" +
                        "Please load the student assessment results and try again.", "No entries found", JOptionPane.ERROR_MESSAGE);
                return true;
            }

            File file = new File(fileName);
            if(!file.exists())
                file.createNewFile();
            document.save(fileName);
            document.close();
            return true;
        }
        catch(COSVisitorException e){
            JOptionPane.showMessageDialog(parent, "Error occurred when creating a PDF file.", "COSVisitorException", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e){
            JOptionPane.showMessageDialog(parent, "Error occurred while saving file.", "IOException", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  A method for creating a page in the PDF document. Prints out text in a readily-laid-out format
     *  in the page.
     *
     *  @param student     A Student object, provides the student object to retrieve information from
     *  @param document    A document object that represents the PDF document, it allows the
     *                     PDPageContentStream to stream input information to the PDF document
     */

    private static void createPage(Student student, PDDocument document) throws IOException{
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);

        //Calculating coordinates
        PDRectangle mediabox = page.findMediaBox();
        float startX = mediabox.getLowerLeftX() + pageScreenOffset;
        float startY = mediabox.getUpperRightY() - pageScreenOffset;

        content.beginText();
        //Begin writing text
        //Starting position
        content.moveTextPositionByAmount(startX, startY);

        //Where text next to profile ends
        //Profile picture takes dimension 80 x 140. With offset from text of 20.
        content.setFont(textFontBold, titleSize);
        writeLine("Student information:", content, resultTextSize);
        newLine(content, resultTextSize);
        content.setFont(textFont, textSize);
        writeLine("Student name: " + student.getSName(), content, resultTextSize);
        writeLine("Student email: " + student.getSEmail(), content, resultTextSize);
        writeLine("Student number: " + student.getSNumber(), content, resultTextSize);
        writeLine("Tutor email: " + student.getTEmail(), content, resultTextSize);
        newLine(content, textSize);
        newLine(content, textSize);

        //Writing exam results
        content.setFont(textFontBold, titleSize);
        writeLine("Exam results:", content, resultTextSize);
        newLine(content, resultTextSize);
        content.setFont(textFont, textSize);
        ArrayList<AssessmentResult> tempResults = student.getStudentExamResults();
        int counter = 0;
        float columnOffSet = (resultTextSize * textFont.getStringWidth("Module  : #4CCS1CS1") / 1000) + 82;
        if (student.getStudentExamResults().size() != 0)
            for(AssessmentResult a : tempResults) {
                counter++;
                writeLine("Year    : " + a.getField("Year"), content, resultTextSize);
                writeLine("Module  : " + a.getField("Module"), content, resultTextSize);
                writeLine("Assess. : " + a.getField("Ass"), content, resultTextSize);
                writeLine("Mark    : " + a.getField("Mark"), content, resultTextSize);
                if(a.getField("Grade") != null)
                    writeLine("Grade   : " + a.getField("Grade"), content, resultTextSize);
                else
                    writeLine("Grade   : Not applicable", content, resultTextSize);
                if(counter == maximumModulesPerColumn) {
                    counter = 0;
                    content.moveTextPositionByAmount(columnOffSet, (float)(1.5*resultTextSize*52)-3.5f);
                }
                else
                    newLine(content, textSize);
            }
        else {
            writeLine("No exam results was found for this student.", content, resultTextSize);
            newLine(content, textSize);
            newLine(content, textSize);
            newLine(content, textSize);
        }

        //End of writing text
        content.endText();
        content.close();
    }

    /**
     *  A method for writing a line of text, then creating a new line.
     *  @param text        The input text.
     *  @param content     The input stream to draw string into a page of a document
     *  @param fontSize    An integer, allows to set the font size when streaming data into the page.
     */

    private static void writeLine(String text, PDPageContentStream content, int fontSize) throws IOException{
        content.drawString(text);
        //x doesn't move because the whole string is drawn from that position.
        newLine(content, fontSize);
    }

    /**
     * This method moves the Text position to the next line, relative to the font size
     * */

    private static void newLine(PDPageContentStream content, int fontSize) throws IOException{
        content.moveTextPositionByAmount(0, -(int)(1.5*fontSize));
    }
}
