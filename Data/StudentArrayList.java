package Data;

import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.FileSystemException;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import studentdata.*;

import Applications.CSVReader;

/**	
 *	<code>StudentArrayList</code> is a class which represents a collection of <code>Student</code> objects.
 *	<p>
 *	This is an implementation of a structure to collect a list of students.
 *      <code>Student</code> objects are created with data from the Internet.
 *      Students can never be removed from the list. Manual insertion of new 
 *      legit students is not advised advised. 
 *      The information remembered by object (the state of an object) consists of:
 *	<ul>
 *	<li>An inventory of students collected serially (e.g. [Student 1, Student 2, Student 3, ...])
 *	</ul>
 *
 *	@author		Johnson Wong
 *	@author		Konstantin Terziev
 *      @author		Hristo Tanev
 */
public class StudentArrayList {
    
    /**
     *	A list of <code>Student</code> objects.
     */
    private ArrayList<Student> list;
    
    /**
     *	This constructor sets up <code>StudentArrayList</code> object.
     *  <p>
     *  The data to create each student is collected from the Internet. You just
     *  to provide a valid access information so the data is downloaded.
     *  
     *  @param userName	<code>String</code> - the username of the authorised user to access the data
     *  @param password	<code>String</code> - the password of the authorised user to access the data
     */
    public StudentArrayList(String userName, String password){
        list = new ArrayList<Student>();
        
        Connector server = new Connector();
        boolean success = server.connect(userName, password);
        
        if (!success){
            System.out.println("Fatal error: could not open connection to server.");
            System.exit(1);
        }
        else
        {
            String email = "", name = "", tEmail = "";
            int number = 0, counter = 0;
            DataTable data = server.getData();
            
            int rowCount = data.getRowCount();
            for (int row = 0; row < rowCount; ++row) {
                for (int col = 0; col < 4; ++col) {
                    switch(col){
                        case 3: number = Integer.parseInt(data.getCell(row, col)); break;
                        case 2: tEmail = data.getCell(row,col); break;
                        case 1: name = data.getCell(row,col); break;
                        case 0: email = data.getCell(row,col); break;
                        default: System.out.println("Column value is not between 0 to 3");
                    }
                }
                addStudent(new Student(email, name, tEmail, number));
                counter++;
            }
        }
    }
    
    /**
     *	This constructor sets up an empty <code>StudentArrayList</code> object.
     *  <p>
     *  Should use only to create temporally <code>StudentArrayList</code> objects.
     */
    public StudentArrayList(){
        list = new ArrayList<Student>();
    }
    
    /**
     *	You can use this method to quickly set up a JList.
     *  
     *	@return		<code>String[]</code> - list of student names followed by the student number in brackets
     */
    public String[] produceList(){
        String[] tempList = new String[list.size()];
        for(int x = 0; x < list.size(); x++)
            tempList[x] = (list.get(x).getSName() + " (" + list.get(x).getSNumber() + ")");
        return tempList;
    }
    
    /**
     *	You can use this method get a list of e-mail address which you call upon
     *  with an index.
     *  
     *	@return		<code>String[]</code> - list of student e-mail addresses
     */
    public String[] produceStudentEmailList(){
        String[] tempList = new String[list.size()];
        for(int x = 0; x < list.size(); x++)
            tempList[x] = list.get(x).getSEmail();
        return tempList;
    }
    
    /**
     *	Insert a single student inside the <code>StudentArrayList</code>.
     *
     *	@param s	<code>Student</code> - student object
     *  
     *  @see            #findNames(String name)
     *  @see		#findEmails(String email)
     *	@see		#findTutors(String tutor)
     *  @see		#findStudent(int studentNumber)
     *  @see		#findStudent(String markingCode)
     */
    public void addStudent(Student s){
        list.add(s);
    }
    
    /**
     *	Get a list of students inside this object by querying e-mails. Method uses the state
     *	of the objects inside the data structure to match them up.
     *
     *	@param	name            <code>String</code> - field we are looking for inside <code>Student</code> objects
     *
     *	@return			<code>StudentArrayList</code> - a list of students matching up with the parameter passed (the name in this case)
     *
     *	@see			#findEmails(String email)
     *	@see			#findTutors(String tutor)
     *  @see			#findStudent(int studentNumber)
     *  @see			#findStudent(String markingCode)
     */
    public StudentArrayList findNames(String name){
        StudentArrayList sal = new StudentArrayList();
        
        for(Student s : list){
            if (s.getSName().toLowerCase().equals(name)){
                sal.addStudent(s);
            }
        }
        
        if (sal.getSize() != 0){
            return sal;
        }
        else
        {
            return null;
        }
    }
    
    /**
     *	Get a list of students inside this object by querying e-mails. Method uses the state
     *	of the objects inside the data structure to match them up.
     *
     *	@param	email           <code>String</code> - field we are looking for inside <code>Student</code> objects
     *
     *	@return			<code>StudentArrayList</code> - a list of students matching up with the parameter passed (the e-mail in this case)
     *
     *	@see			#findNames(String name)
     *	@see			#findTutors(String tutor)
     *  @see			#findStudent(int studentNumber)
     *  @see			#findStudent(String markingCode)
     */
    public StudentArrayList findEmails(String email){
        
        StudentArrayList sal = new StudentArrayList();
        
        for(Student s : list){
            if (s.getSEmail().equals(email)){
                sal.addStudent(s);
            }
        }
        
        if (sal.getSize() != 0){
            return sal;
        }
        else
        {
            return null;
        }
    }
    
    /**
     *	Get a list of students inside this object by querying student's tutor's e-mails. Method uses the state
     *	of the objects inside the data structure to match them up.
     *
     *	@param	tutor           <code>String</code> - field we are looking for inside <code>Student</code> objects
     *
     *	@return			<code>StudentArrayList</code> - a list of students matching up with the parameter passed (the tutor e-mail in this case)
     *
     *	@see			#findNames(String name)
     *	@see			#findEmails(String email)
     *  @see			#findStudent(int studentNumber)
     *  @see			#findStudent(String markingCode)
     */
    public StudentArrayList findTutors(String tutor){
        
        StudentArrayList sal = new StudentArrayList();
        
        for(Student s : list){
            if (s.getTEmail().equals(tutor)){
                sal.addStudent(s);
            }
        }
        
        if (sal.getSize() != 0){
            return sal;
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     *	Get a list of students inside this object which know about their Exam Results.
     *
     *	@return     <code>StudentArrayList</code> - a list of students which know about their Exam Results
     */
    public StudentArrayList getStudentsWithMarks()
    {
        StudentArrayList sal = new StudentArrayList();
        
        for(Student s : list){
            if (s.hasAnomymousMarkingCode() == true){
                sal.addStudent(s);
            }
        }
        
        return sal;
    }
    
    /**
     *	Get a student if you know their KCL ID number.
     *  <p>
     *  Method uses the state of the objects inside the data structure to match them up.
     *  Quite powerful assuming KCL IDs are always unique and you don't have the same
     *  student added more than once.
     *
     *	@param	studentNumber   <code>int</code> - field we are looking for inside <code>Student</code> objects
     *
     *	@return			<code>Student</code> - a student from KCL or null if we can find one
     *
     *	@see			#findNames(String name)
     *	@see			#findEmails(String email)
     *  @see			#findStudent(String markingCode)
     */
    public Student findStudent(int studentNumber){
        
        StudentArrayList sal = new StudentArrayList();
        
        for(Student s : list){
            if (s.getSNumber() == studentNumber){
                return s;
            }
        }
        return null;
    }
    
    /**
     *	Get a student if you know some anonymous code.
     *  <p>
     *  Method uses the state of the objects inside the data structure to match them up.
     *  Will return on first match.
     *
     *	@param	markingCode     <code>String</code> - field we are looking for inside <code>Student</code> objects
     *
     *	@return			<code>Student</code> - a student from KCL or null if we cannot find one
     *
     *	@see			#findNames(String name)
     *	@see			#findEmails(String email)
     *  @see			#findStudent(String markingCode)
     */
    public Student findStudent(String markingCode){
        
        StudentArrayList sal = new StudentArrayList();
        
        for(Student s : list){
            if (s.hasAnomymousMarkingCode(markingCode)){
                return s;
            }
        }
        return null;
    }
    
    /**
     *	Insert anonymous marking codes into relevant student objects from a CSV file.
     *
     *	@param	csvFile         <code>File</code> - a CSV file with anonymous marking codes data
     *
     *	@return			<code>String</code> - a message of number of known codes and number of not know codes
     *  
     *  @throws java.io.IOException                 You can receive a <code>IOException</code> if there is something illegible in the CSV file
     *  @throws java.io.FileNotFoundException       You can receive a <code>FileNotFoundException</code> if the file path you used doesn't point to a actual
     *  @throws java.nio.file.FileSystemException   You can receive a <code>FileSystemException</code> if the file you are using is empty
     */
    public String addAnonymousMarkingCodes(File csvFile) throws FileNotFoundException, FileSystemException, IOException{
        int studentNumber;
        String code = "";
        int found = 0;
        int total = 0;
        Student s = null;
        
        CSVReader reader = new CSVReader();
        reader.extract(csvFile);
        
        while(!reader.finishedReading())
        {
            try {
                code = reader.nextPieceOfData();
                ++total;
                
                studentNumber = Integer.parseInt(code); //Test for candidate key or anonymous code.
                
                s = findStudent(studentNumber);
                if(s != null)
                {
                    ++found;
                    s.addAnonymousMarkingCode(reader.nextPieceOfData());
                }
                else
                {
                    reader.nextPieceOfData();
                }
            }
            catch(NumberFormatException e)
            {
                //If we are still reading codes and already found a student...
                //so reading a line in the form (111, Q00001, Q00002..)
                if (s != null)
                {
                    ++found;
                    s.addAnonymousMarkingCode(code);
                }
            }    
        }
        return "Loaded " + found + " known anonymous marking codes for students, " + (total - found) + " codes for unknown students.";
    }
    
    /**
     *	Get a display of the results from the action of loading anonymous marking codes from a CSV.
     *
     *	@return     <code>JPanel</code> - a display of the results from the action of loading anonymous marking codes from CSV file
     */
    public JPanel getFileResults() {
        JList jlist=new JList();
        jlist.setCellRenderer(new StudentCellRenderer());
        DefaultListModel<Student> model=new DefaultListModel<Student>();
        for (Student s:list) {
            model.addElement(s);
        }
        jlist.setModel(model);
        JScrollPane scroll=new JScrollPane(jlist);
        JPanel panel=new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,scroll);
        panel.add(BorderLayout.SOUTH,StudentCellRenderer.getColorMap());
        return panel;
    }
    
    /**
     *	Get get the pure <code>ArrayStudent&lt;Student&gt;</code> from the instance of <code>StudentArrayList</code>.
     *
     *	@return     <code>ArrayList&lt;Student&gt;</code> - a list of student objects
     */
    public ArrayList<Student> getList(){
        return list;
    }
    
    /**
     *	Get the number of students collected here.
     *
     *	@return     <code>int</code> - the number of student objects stored in this object
     */
    public int getSize(){
        return list.size();
    }
    
    /**
     *	Test the connection to the Internet where we get the student data to make <code>Student</code> objects.
     *  
     *  @param userName	<code>String</code> - the username of the authorised user to access the data
     *  @param password	<code>String</code> - the password of the authorised user to access the data
     *  
     *	@return     <code>boolean</code> - successful of unsuccessful
     */
    public static boolean testConnection(String userName, String password){
        Connector server = new Connector();
        if(server.connect(userName, password)) return true;
        else return false;
    }
}