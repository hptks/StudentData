package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.FileSystemException;

import java.util.Map;
import java.util.TreeMap;

import Applications.CSVReader;

/**	
 *	<code>Module</code> is a class which represents a collection of <code>Assessment</code> objects.
 *	<p>
 *	The purpose of this class is to use to break down modules into different assessments.
 *      
 *	@author		Konstantin Terziev
 */
public class Module {
    
    /**
     *	The name of the module that this object belongs to.
     */
    private String moduleName;
    /**
     *	A map whose key is a module name that maps onto assessments of the module.
     */
    private Map<String, Assessment> assessments;
    /**
     *	Students that we can search through or add information to.
     */
    private StudentArrayList sal;
    
    /**
     *	This constructor sets up an empty <code>Module</code> object with the name of the module that this object belongs to.
     *  
     *  @param	sal     <code>StudentArrayList</code> - the students we made from the student data from the Internet
     *	@param	mn      <code>String</code> - the name of the module that this module object belongs to
     */
    public Module(StudentArrayList sal, String mn)
    {
        this.sal = sal;
        moduleName = mn;
        assessments = new TreeMap<String, Assessment>();
    }
    
    /**
     *	Insert a single assessment inside the <code>Module</code> object if you know the name of it.
     *
     *	@param aN	<code>String</code> - the assessment name we are going get
     *  @param csvFile	<code>File</code> - the CSV file we are going to read the assessments data from
     *  
     *  @return         <code>Assessment</code> - the assessment object just created
     *  
     *  @throws java.io.IOException                 You can receive a <code>IOException</code> if there is something illegible in the CSV file
     *  @throws java.io.FileNotFoundException       You can receive a <code>FileNotFoundException</code> if the file path you used doesn't point to a actual
     *  @throws java.nio.file.FileSystemException   You can receive a <code>FileSystemException</code> if the file you are using is empty
     *  
     *  @see            #getAssessment(String assessmentName)
     */
    public Assessment addAssessment(String aN, File csvFile) throws FileNotFoundException, FileSystemException, IOException
    {
        CSVReader reader = new CSVReader();
        reader.extract(csvFile);
        reader.indentifyColumns();
        
        Assessment a = new Assessment(sal, reader, moduleName, aN);
        assessments.put(aN, a);
        
        return a;
    }
    
    /**
     *	Get the name of the module that this object represents.
     *
     *	@return		<code>String</code> - the module name that this object represents
     */
    public String getModuleName()
    {
        return moduleName;
    }
    
    /**
     *	Get a particular assessment if know the name of it.
     *
     *	@param	assessmentName  <code>String</code> - the name of the module
     *
     *	@return			<code>Assessment</code> - the assessment inside this module corresponding the name of the assessment
     */
    public Assessment getAssessment(String assessmentName)
    {
        return assessments.get(assessmentName);
    }
}