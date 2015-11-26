package Data;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**	
 *	<code>Student</code> is a class which represents a single student who attends King's College London.
 *      <p>
 *	A student object encapsulates information which identifies a
 *	particular student. At the same time a single student has the ability to know
 *      information about himself/herself like their exam results and anonymous
 *      marking codes.
 *	The information retained by the object (the state of the object) consists of:
 *	<ul>
 *	<li>The full name of the student (e.g. [First Name] [Surname] or [First Name] [Middle Name] [Surname])
 *	<li>The student's KCL ID number (e.g. 1234567)
 *	<li>The student's e-mail address (e.g. [first name].[surname]@kcl.ac.uk)
 *	<li>The student's tutor's e-mail address (e.g. [first name].[surname]@kcl.ac.uk)
 *      <li>Student anonymous marking codes (e.g. P00001, Q00002).
 *	<li>The student's exam results for any year period.
 *	<li>The last time a student has been on KEATS on a particular module (if at all).
 *	</ul>
 *
 *	@author		Johnson Wong
 *	@author		Konstantin Terziev
 *      @author         Hristo Tanev
 */

public class Student {
    
    /**
     *	Basic information about a student:
     *  <ul>
     *  <li>full name of a student
     *  <li>e-mail address
     *  <li>their tutor's e-mail address
     *  </ul>
     */
    private String sEmail, sName, tEmail;
    /**
     *	Contains a list of anonymous marking codes.
     */
    private ArrayList<String> anonymousMarkingCodes;
    /**
     *	Contains the student KCL ID. Its a number with no symbols present.
     */
    private int sNumber;
    /**
     *	Contains a list of modules for which each has time information.
     */
    private HashMap<String, String> lastAccess;
    /**
     *	Way for us track if the object just received anonymous marking codes.
     */
    private boolean receivedNewCodes;
    /**
     *	A list of exam results.
     */
    private ArrayList<AssessmentResult> studentResults;
    
    /**
     *	This constructor initialises the freshly created <code>Student</code> with some basic information.
     *
     *	@param sEmail	<code>String</code> - the student e-mail
     *	@param sName	<code>String</code> - the student name
     *	@param tEmail	<code>String</code> - the student's tutor's e-mail
     *	@param sNumber	<code>int</code> - his or hers student KCL ID number
     */
    public Student(String sEmail, String sName, String tEmail, int sNumber){
        receivedNewCodes=false;
        this.sEmail = sEmail;
        this.sName = sName;
        this.tEmail = tEmail;
        this.sNumber = sNumber;
        anonymousMarkingCodes = new ArrayList<String>();
        studentResults = new ArrayList<AssessmentResult>();
        lastAccess = new HashMap<String, String>();
    }
    
    /**
     *	Allows us use to insert a anonymous marking code inside the student.
     *  Every time we use this the student is flagged as just received anonymous
     *  marking code.
     *
     *	@param aMC	<code>String</code> - anonymous marking code
     *  
     *  @see            gotNewCodes
     */
    public void addAnonymousMarkingCode(String aMC){
        anonymousMarkingCodes.add(aMC);
        receivedNewCodes=true;
    }
    
    /**
     *	Check if the student recently received a anonymous marking code.
     *  
     *	@return		<code>boolean</code> - whether or not student just received anonymous marking code
     * 
     *  @see            #addAnonymousMarkingCode(String aMC)
     */
    public boolean gotNewCodes() {
        return receivedNewCodes;
    }
    
    /**
     *	Remove the flag that the student just received an anonymous marking code.
     *  Good idea to call this method once done with gotNewCodes to avoid
     *  confusion when iteratively checking the flag.
     *  
     *  @see    #addAnonymousMarkingCode(String aMC)
     *  @see    #gotNewCodes
     */
    public void resetNewCodesListener() {
        receivedNewCodes=false;
    }
    
    /**
     *	Insert a single exam result code inside the student.
     *
     *	@param ar	<code>AssessmentResult</code> - exam result
     *  
     *  @see            getStudentExamResults
     */
    public void addAssessmentResult(AssessmentResult ar){
        studentResults.add(ar);
    }
    
    /**
     *	Get all the exam results a student knows about.
     *  
     *	@return		<code>ArrayList&lt;AssessmentResult&gt;</code> - a list of all the exam results a student knows about
     *  
     *  @see            addAnonymousMarkingCode
     *  @see            gotNewCodes
     */
    public ArrayList<AssessmentResult> getStudentExamResults(){
        return studentResults;
    }
    
    /**
     *	Ask for the student's e-mail.
     *  
     *	@return		<code>String</code> - student e-mail
     */
    public String getSEmail(){
        return sEmail;
    }
    
    /**
     *	Ask for the student's name.
     *  
     *	@return		<code>String</code> - student name
     */
    public String getSName(){
        return sName;
    }
    
    /**
     *	Ask for the student's tutor's e-mail.
     *  
     *	@return		<code>String</code> - student's  tutor's e-mail
     */
    public String getTEmail(){
        return tEmail;
    }
    
    /**
     *	Check if the student knows that the this anonymous marking code belongs to them.
     *  
     *  @param markingCode  <code>AssessmentResult</code> - anonymous marking code
     *  
     *	@return             <code>boolean</code> - whether or not student thinks this marking code belongs to them.
     * 
     *  @see                #addAnonymousMarkingCode(String aMC)
     */
    public boolean hasAnomymousMarkingCode(String markingCode){
        for(String code : anonymousMarkingCodes) {
            if (code.equals(markingCode)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     *	Check if the student knows at least one anonymous marking code.
     *  
     *	@return  <code>boolean</code> - whether or not student thinks this marking code belongs to them.
     * 
     *  @see                #addAnonymousMarkingCode(String aMC)
     */
    public boolean hasAnomymousMarkingCode(){
        return anonymousMarkingCodes.size() > 0;
    }
    
    /**
     *	Ask for the student's KCL ID number.
     *  
     *	@return		<code>int</code> - KCL ID number
     */
    public int getSNumber(){
        return sNumber;
    }
    
    /**
     *	Converts the object into a readable format. You make JLists of student
     *  objects where each entry is the list is distinguishable and readable.
     *  
     *	@return		<code>String</code> - concatenation of student name, followed by a space and the KCL ID number surrounded in brackets
     *
     *	{@inheritDoc}
     *
     *	@see		getSName
     *	@see		getSNumber
     */
    public String toString()
    {
        return sName + " (" + sNumber + ")";
    }
    
    /**
     *	Insert a last access time on KEATS for a module.
     *
     *	@param _lastAccess	<code>Map.Entry</code> - key of module name which maps to a KEATS module access time
     *  
     *  @see                    getLastAccess
     */
    public void setLastAccess(Map.Entry _lastAccess){
        boolean added = false;
        String key = (String)_lastAccess.getKey();

        if(lastAccess.size()!=0)
            for(Map.Entry<String, String> temp : lastAccess.entrySet())
                if(key == temp.getKey()){
                    temp.setValue((String)_lastAccess.getValue());
                    added = true;
                    //System.out.println("Date added to " + key + " with " + _lastAccess.getValue());
                }

        if(!added) {
            lastAccess.put((String) _lastAccess.getKey(), (String) _lastAccess.getValue());
            //System.out.println("Date added to " + key + " with " + _lastAccess.getValue());
        }
    }
    
    /**
     *	Get all the last accesses for KEATS a student knows about.
     *  
     *	@return		<code>HashMap</code> - last accesses for KEATS on certain modules
     *  
     *  @see            setLastAccess
     */
    public HashMap getLastAccess(){
        return lastAccess;
    }
}