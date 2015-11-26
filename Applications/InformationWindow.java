package Applications;

import Data.AssessmentResult;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import java.awt.Component;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Data.Student;
import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.JScrollPane;

/**
 * <code>Information window</code> which provides the user with a single student's information
 * <p>
 * Creates a window which displays a student's information on the modules he took alongside with his tutor's 
 * details: email and name. Then after that a JPanel is created within a JScrollPane to show the student's 
 * last visit on KEATS, his/her marks and grades for each one of his nodules.
 * 
 * @author Konstantin Terziev
 * @author Hristo Tanev
 */
public class InformationWindow extends JFrame {
    
    private AssessmentResult currentResult;
    
    /**
     * <code>Creates</code> a information window for a single student
     * 
     * @param s the <code>Student</code> we want to create an <code>Informtion window</code> for 
     */
    public InformationWindow(Student s) {
        super("Information Window");
        setIconImage(FileManager.getImage("Logo24.png").getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        try {
            setIconImage(FileManager.getImage("Logo24.png").getImage());
        }
        catch(NullPointerException npe)
        {
            System.out.println("Something is wrong with the Images folder. Could not load image");
        }
        
        add(createHeader("Student:"), BorderLayout.NORTH);
        
        drawWidgets(s);
        
        pack();
        setVisible(true);
    }
    
    /**
     * <code>Draw</code> all the widgets needed for the <code>Information window</code>
     * <p>
     * Gives all the required information for the <code>Student s</code> regarding whether he/she
     * has been on KEATS recently and when. Also what are his/her exam results and grades for each of the 
     * modules he/she is taking.
     * 
     * @param s the current clicked on <code>Student</code>
     */
    private void drawWidgets(Student s){
        JPanel panel1 = createNewContainingPanel();

        JLabel jl_studentName = new JLabel(s.getSName());
        jl_studentName.setFont(new Font("Ariel", Font.BOLD, 28));
        jl_studentName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel jl_studentEmail = new JLabel(s.getSEmail());
        jl_studentEmail.setFont(new Font("Ariel", Font.ITALIC, 24));
        jl_studentEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panel2 = new JPanel(new FlowLayout());

        JButton jb_closeDialog = new JButton("Close");
        ActionListener close = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        jb_closeDialog.addActionListener(close);
        
        panel2.add(jb_closeDialog);
        
        final String[] fieldNames1 = {"Student No.: ", "Tutor:           "};
        panel1.add(jl_studentName);
        panel1.add(jl_studentEmail);
        
        panel1.add(createGroupTitle("Details:"));
        panel1.add(createGroupInformation(s, fieldNames1));
        
        if (s.getStudentExamResults().size()!=0) {
            //Display the exam results.
            setPreferredSize(new Dimension(430, 539));
            panel1.add(createHeader("Exam Results"));
            JPanel examResults=createNewContainingPanel();
            examResults.setMinimumSize(new Dimension(400, 230));
            final String[] fieldNames = {"Year:           ","Assessment: ","Mark:           ","Grade:         "};
            for (AssessmentResult ar:s.getStudentExamResults()) {
                currentResult=ar;
                examResults.add(createGroupTitle(ar.getField("Module")));
                examResults.add(createGroupInformation(s,fieldNames));
                //Scrape functionality goes here.
                HashMap<String, String> participationData = s.getLastAccess();
                if (!participationData.isEmpty())
                {
                    examResults.add(createGroupTitle("Last access on KEATS:"));
                    String lastAccess = participationData.get(CSVReader.ridIllegalCharacters(ar.getField("Module")));
                    if (lastAccess != null)
                    {
                        examResults.add(createGroupTitle(lastAccess));
                        examResults.add(new JLabel(" "));
                    }
                    else
                    {
                        examResults.add(createGroupTitle("Not been on KEATS"));
                        examResults.add(new JLabel(" "));
                    }
                }
                else
                {
                    examResults.add(createGroupTitle("No KEATS scrape information found."));
                    examResults.add(new JLabel(" "));
                }
            }
            JScrollPane scroll=new JScrollPane(examResults);
            panel1.add(scroll);
        }
        else
        {
            //Say exam results are missing.
            panel1.add(createGroupTitle("No exam information found"));
        }
        
        panel1.add(panel2);
        
        add(panel1, BorderLayout.CENTER);
    }
    
    /**
     * <code>Method</code> that gives a piece of information for a <code>Student</code>
     * 
     * @return JPanel which will contain a piece of information for the current student 
     */
    private JPanel createNewContainingPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        return panel;
    }
    
    /**
     * <code>Creates</code> a JPanel with a single module's information
     * <p>
     * Using the field names provided as a parameter in the function we can extract
     * the required data for the module
     * 
     * @param s the current <code>Student</code>
     * @param fieldNames the field names with which we want to describe the data 
     * @return Returns a JPanel with the information for a single module
     */
    private JPanel createGroupInformation(Student s, String[] fieldNames) {
        JPanel panel1 = new JPanel(new GridLayout(fieldNames.length, 1));
        panel1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        
        for (int i = 0; i < fieldNames.length; ++i)
        {
            JPanel panel2 = new JPanel(new BorderLayout());
            
            JLabel jl_fieldName = createFieldName(fieldNames[i]);
            JLabel jl_fieldInfo;
            
            switch (fieldNames[i])
            {
                case "Student No.: ": jl_fieldInfo = createFieldInfo("" + s.getSNumber()); break;
                case "Tutor:           ": jl_fieldInfo = createFieldInfo(s.getTEmail()); break;
                case "Mark:           ": jl_fieldInfo = createFieldInfo(currentResult.getField("Mark")); break;
                case "Grade:         ": 
                    String grade = currentResult.getField("Grade");
                    if (grade != null)
                    {
                        jl_fieldInfo = createFieldInfo(grade);
                    }
                    else
                    {
                        jl_fieldInfo = createFieldInfo("n/a"); 
                    }
                    break;
                case "Assessment: ": jl_fieldInfo = createFieldInfo(currentResult.getField("Ass")); break;
                case "Year:           ": jl_fieldInfo = createFieldInfo(currentResult.getField("Year")); break;
                default: jl_fieldInfo = createFieldInfo("Something has gone wrong :(");
            }
            
            panel2.add(jl_fieldName, BorderLayout.WEST);
            panel2.add(jl_fieldInfo, BorderLayout.CENTER);
            
            panel1.add(panel2);
        }
        
        return panel1;
    }
    
    /**
     * <code>Creates</code> a label with a given field name
     * 
     * @param fieldName the name of the wanted field
     * @return A JLabel with the name we want for the field
     */
    private JLabel createFieldName(String fieldName) {
        JLabel jl_fieldName = new JLabel(fieldName);
        jl_fieldName.setFont(new Font("Ariel", Font.PLAIN, 20));
        
        return jl_fieldName;
    }
    
    /**
     * <code>Creates</code> a JLable with the information of a field
     * 
     * @param info the information about a field
     * @return A JLabel with the information of a field 
     */
    private JLabel createFieldInfo(String info) {
        JLabel jl_info = new JLabel(info);
        jl_info.setFont(new Font("Ariel", Font.BOLD, 20));
        
        return jl_info;
    }
    
    /**
     * <code>Sets</code> the title of a group of information
     * 
     * @param titleName the preferred title of the group
     * @return a JLabel with the preferred title of the group
     */
    private JLabel createGroupTitle(String titleName) {
        JLabel jl_title = new JLabel(titleName);
        jl_title.setFont(new Font("Ariel", Font.BOLD, 16));
        jl_title.setForeground(Color.WHITE);
        jl_title.setOpaque(true);
        jl_title.setBackground(Color.DARK_GRAY);
        jl_title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return jl_title;
    }
    
    /**
     * <code>Sets</code> the name of a section
     * 
     * @param headerName the preferred title of the section
     * @return Returns a JPanel where you can put information about a <code>Student</code>
     */
    public static JPanel createHeader(String headerName){
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(true);
        headerPanel.setBackground(Color.DARK_GRAY);
        
        JLabel jl_headerTitle = new JLabel(headerName);
        jl_headerTitle.setFont(new Font("Ariel", Font.BOLD, 24));
        jl_headerTitle.setForeground(Color.WHITE);
        jl_headerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(jl_headerTitle);
        
        return headerPanel;
    }
}