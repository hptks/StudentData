package Data;

import java.util.Map;
import java.util.TreeMap;

/**	
 *	<code>Results</code> is a class which represents a collection of <code>Modules</code> objects.
 *	<p>
 *	The purpose of this class is to use to break down year periods into different modules.
 *      
 *	@author		Konstantin Terziev
 */
public class Results {
    
    /**
     *	A map whose key is a year name that maps onto module inside the corresponding year  period.
     */
    private Map<String, Module> modules;
    /**
     *	The name of the year period that this object belongs to.
     */
    private String schoolYear;
    
    /**
     *	This constructor sets up an empty <code>Results</code> object with the name of the year period that this object belongs to.
     *
     *	@param	sY     <code>String</code> - the name of the year period that this module belongs to
     */
    public Results(String sY)
    {
        modules = new TreeMap<String, Module>();
        schoolYear = sY;
    }
    
    /**
     *	Insert a single module inside the <code>Results</code> object.
     *
     *	@param m	<code>Module</code> - student object
     *  
     *  @see            #getModule(String moduleName)
     */
    public void addModule(Module m)
    {
        modules.put(m.getModuleName(), m);
    }
    
    /**
     *	Get a particular module if know the name of it.
     *
     *	@param	moduleName      <code>String</code> - the name of the module
     *
     *	@return			<code>Module</code> - the module inside this year period
     */
    public Module getModule(String moduleName)
    {
        return modules.get(moduleName);
    }
    
    /**
     *	Get the name of the year period that this instance of <code>Results</code> belongs to.
     *
     *	@return			<code>String</code> - the name of the year period of these modules
     */
    public String getSchoolYearName()
    {
        return schoolYear;
    }
}