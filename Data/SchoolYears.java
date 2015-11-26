package Data;

import java.util.Map;
import java.util.TreeMap;

/**	
 *	<code>SchoolYears</code> is a class which represents a collection of <code>Results</code> objects.
 *	<p>
 *	The purpose of this class is to use to break down Exam Results into different year periods.
 *      
 *	@author		Konstantin Terziev
 */
public class SchoolYears {
    /**
     *	A map whose key is a year name that maps onto Exam Results corresponding to that year.
     */
    private Map<String, Results> schoolYearResults;
    
    /**
     *	This constructor sets up an empty <code>SchoolYears</code> object.
     */
    public SchoolYears()
    {
        schoolYearResults = new TreeMap<String, Results>();
    }
    
    /**
     *	Insert a single Exam Results inside the <code>SchoolYears</code> object.
     *
     *	@param r	<code>Results</code> - Exam Results corresponding to the year the object represents.
     *  
     *  @see            #getResults(String schoolYear)
     */
    public void addResults(Results r)
    {
        schoolYearResults.put(r.getSchoolYearName(), r);
    }
    
    /**
     *	Get a particular Exam Results if know the year they happened.
     *
     *	@param	schoolYear     <code>String</code> - year period
     *
     *	@return                <code>Results</code> - the Exam Results from that period
     */
    public Results getResults(String schoolYear)
    {
        return schoolYearResults.get(schoolYear);
    }
}