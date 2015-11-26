package Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**	
 *	<code>ExamTableModel</code> is a class which is built to manage a <code>JTable</code> in a tab.
 *	<p>
 *	The class adds sorted entries to the table when you ask it to.
 *      It observes <code>Assessment</code> objects to get rows of data.
 *      When we are ready we can it put the data accumulated on the table.
 *      
 *	@author		Konstantin Terziev
 *      @author         Hristo Tanev
 */
public class ExamTableModel extends DefaultTableModel implements Observer {
    
    /**
     *	The index of the "Year" column in the <code>JTable</code>.
     */
    public static int YEAR_INDEX;
    /**
     *	The index of the "Module" column in the <code>JTable</code>.
     */
    public static int MODULE_INDEX;
    /**
     *	The index of the "Candidate Key" column in the <code>JTable</code>.
     */
    public static int CANDKEY_INDEX;
    /**
     *	The index of the "Mark" column in the <code>JTable</code>.
     */
    public static int MARK_INDEX;
    /**
     *	The index of the "Student" column in the <code>JTable</code>.
     */
    public static int NAME_INDEX;
    /**
     *	The column names of the <code>JTable</code>.
     */
    public static String[] columnNames;
    
    /**
     *	Row data of the <code>JTable</code>.
     */
    private ArrayList<String[]> rows;
    /**
     *	Holds data on how to look within groups of data like years and modules.
     */
    private ArrayList<Integer> diffIndexes;
    /**
     *	Students that we can search through or add information to.
     */
    private StudentArrayList students;
    /**
     *	Scatter plot data:
     *  <ul>
     *  <li>Maximum X value
     *  <li>Minimum X value
     *  </ul>
     */
    private double maxX=0.0,minX=10e+4;
    
    /**
     *	This constructor creates a <code>ExamTableModel</code> that can be used to manage a <code>JTable</code>.
     *  
     *  @param	sAL             <code>StudentArrayList</code> - the students we made from the student data from the Internet
     */
    public ExamTableModel(StudentArrayList sAL) 
    {
        for (int i = 0; i < columnNames.length; ++i) 
        {   
            addColumn(columnNames[i]);
        }
        
        rows = new ArrayList<String[]>();
        diffIndexes = new ArrayList<Integer>();
        students = sAL;
    }
    
    /**
     *	Make the cells in the table not editable.
     *  
     *  @param	row             <code>int</code> - the selected row
     *  @param	column          <code>int</code> - the selected column
     *  
     *  @return                 <code>boolean</code> - the cell is not editable (does this for any cell);
     */
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
    
    /**
     *	This object receives information when we are reading a CSV file within the <code>Assessment</code> class.
     *  <p>
     *  Checked if we have not got this information before adding it this object. This we stopped duplicated data
     *  while allowing the user to load files repeatedly.
     *  
     *  @param	o             <code>Observable</code> - the assessment(s) this object is observing
     *  @param	dataItem      <code>Object</code> - a single exam/assessment result result
     *  
     *  @see                  #notAddedBefore(String[] r)
     *  
     *  {@inheritDoc}
     */
    public void update(Observable o, Object dataItem)
    {
        AssessmentResult ar = (AssessmentResult) dataItem;
        String[] row = {ar.getField("Year"), ar.getField("Module"), ar.getField("Ass"), ar.getField("Name"),
                            ar.getField("Cand Key"), ar.getField("Mark"), ar.getField("Grade")};
        if (notAddedBefore(row))
        {
            rows.add(row);
        }
    }
    
    /**
     *	Check if the current object does not already have this row in its inventory of rows.
     *  
     *  @param	r             <code>String[]</code> - new row we read from the CSV file
     *  
     *  @return               <code>boolean</code> - fact whether it was added before.
     */
    public boolean notAddedBefore(String[] r)
    {
        for(String[] row : rows)
        {
            if (r[0].equals(row[0]) && r[1].equals(row[1]) && r[2].equals(row[2]) && r[3].equals(row[3]) && r[4].equals(row[4]) && r[5].equals(row[5]) && r[6].equals(row[6]))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     *	Sort the data for the <code>JTable</code>.
     *  <p>
     *  The data is always sorted before it is put in the table (and resorted if
     *  it is already present). The data is sorted first by the "Year" column.
     *  Then within each "Year" the modules are sorted. Finally within each module
     *  the KCL ID numbers are sorted.
     */
    public void sortAllData()
    {
        //Sort everything by Years.
        Collections.sort(rows, AssessmentResult.yearComparator);
        
        //Find all the different Years.
        diffIndexes.add(-1);
        for (int i = 0; i < rows.size() - 1; ++i)
        {
            String p = rows.get(i)[YEAR_INDEX];
            String q = rows.get(i + 1)[YEAR_INDEX];
            
            if (!p.equals(q))
            {
                diffIndexes.add(i);
            }
        }
        
        if (diffIndexes.size() == 1)
        {
            //If there is only Year present just sort the modules.
            Collections.sort(rows, AssessmentResult.moduleComparator);
        }
        else
        {
            //Sort the Modules within Years.
            for (int i = 0; i < diffIndexes.size() - 1; ++i)
            {
                ArrayList<String[]> t = new ArrayList<String[]>();
                //Get the section of one Year.
                for (int j = diffIndexes.get(i) + 1; j <= diffIndexes.get(i + 1); ++j)
                {
                    t.add(rows.get(j));
                }
                
                //Sort by Modules within it.
                Collections.sort(t, AssessmentResult.moduleComparator);
                
                //Reinsert the sorted section.
                for (int j = diffIndexes.get(i) + 1; j <= diffIndexes.get(i + 1); ++j)
                {
                    rows.set(j, t.get(j - (diffIndexes.get(i) + 1)));
                }
            }
        }
        
        diffIndexes.clear();
        
        //Find all the different Modules;
        diffIndexes.add(-1);
        for (int i = 0; i < rows.size() - 1; ++i)
        {
            String p = rows.get(i)[MODULE_INDEX];
            String q = rows.get(i + 1)[MODULE_INDEX];
            if (!p.equals(q)) 
            {
                diffIndexes.add(i);
            }
        }
        
        
        if (diffIndexes.size() == 1)
        {
            //If there is only Module present just sort the Candidate Keys
            Collections.sort(rows, AssessmentResult.candidateKeyComparator);
        }
        else
        {
            //Sort the Candidate Keys within the Modules.
            for (int i = 0; i < diffIndexes.size() - 1; ++i)
            {
                ArrayList<String[]> t = new ArrayList<String[]>();
                //Get the section of one Module.
                for (int j = diffIndexes.get(i) + 1; j <= diffIndexes.get(i + 1); ++j)
                {
                    t.add(rows.get(j));
                }
                
                //Sort by modules within it.
                Collections.sort(t, AssessmentResult.candidateKeyComparator);
                
                //Reinsert the sorted section.
                for (int j = diffIndexes.get(i) + 1; j <= diffIndexes.get(i + 1); ++j)
                {
                    rows.set(j, t.get(j - (diffIndexes.get(i) + 1)));
                }
            }
        }
        diffIndexes.clear();
    }
    
    /**
     *	Removes the old rows in <code>JTable</code> and adds the rows again after it sorts.
     */
    public void putOnTable()
    {
        //Remove all the old entries
        setRowCount(0);
        
        sortAllData();
        
        //Add the sorted entries (old + new).
        for (int i = 0; i < rows.size(); ++i)
        {
            addRow(rows.get(i));
        }
    }
    
    /**
     *	Check if the <code>JTable</code> has rows.
     *  
     *  @return     <code>boolean</code> - number of rows greater than 0
     */
    public boolean hasTableGotData() {
        return (rows.size()>0);
    }
    
    /**
     *	Get a scatter plot that you can display. 
     *  <p>
     *  The scatter plot plots a student's assessment mark on the x axis
     *  and the average mark on all other assessments on the y axis. 
     *  The plot contains a line of best fit as well.
     *  
     * @param assName           <code>String</code> - the name of the assessment (its the assessment of the clicked tab)
     *  
     *  @return                 <code>JFreeChart</code> - the scatter plot with the line of best fit.
     */
    public JFreeChart getScatterChart(String assName)
    {   
        XYDataset r=getCompareData(assName);
        double[] aB=Regression.getOLSRegression(r,0);
        JFreeChart chartWithLine=ChartFactory.createScatterPlot(assName, "Student's", "Average",r);
        XYPlot plot=chartWithLine.getXYPlot();
        XYLineAnnotation line=new XYLineAnnotation(minX,aB[0],maxX+2,aB[0]+aB[1]*(maxX+2));
        plot.addAnnotation(line);
        return chartWithLine;
    }
    
    /**
     *	Get the data to plot on a Scatter Plot 
     *  <p>
     *  
     * @param assName           <code>String</code> - the name of the assessment (its the assessment of the clicked tab)
     *  
     *  @return                 <code>XYDataset</code> - the points on the scatter plot.
     */
    public XYDataset getCompareData(String assName)
    {
        XYSeriesCollection graph = new XYSeriesCollection();
        XYSeries points = new XYSeries("Student mark assessment against average mark on other assessments");
        for (String[] row : rows)
        {
            double x = Double.parseDouble(row[MARK_INDEX]);
            int studentNumber = Integer.parseInt(AssessmentResult.cleanCandidateKey(row[CANDKEY_INDEX]));
            Student s = students.findStudent(studentNumber);
            boolean okay = false;
            ArrayList<AssessmentResult> assessments = s.getStudentExamResults();
            double accumulator = 0;
            for(AssessmentResult ar : assessments)
            {
                double tempMark = Double.parseDouble(ar.getField("Mark"));
                if (!okay && tempMark == x)
                {
                    //Don't  allow one equality.
                    okay = true;
                }
                else
                {
                    accumulator += tempMark;
                }
            }
            double y = accumulator / (assessments.size()-1);
            points.add(x, y);
            if (maxX<x) {
                maxX=x;
            }
            if (minX>x) {
                minX=x;
            }
        }
        graph.addSeries(points);
        return graph;
    }
    
    /**
     *	Get the student's KCL ID number <code>JTable</code>.
     *  
     *  @param row              <code>int</code> - the selected row
     *  
     *  @return                 <code>int</code> - the student's KCL ID number from the selected row
     */
    public int getCandidateNumberFromRow(int row) {
        return Integer.parseInt(AssessmentResult.cleanCandidateKey(rows.get(row)[CANDKEY_INDEX]));
    }
    
    /**
     *	Store the indexes of the column names from the <code>JTable</code>.
     *  
     *  @param cN              <code>int</code> - the column names of the table row
     */
    public static void setColumns(String[] cN)
    {
        columnNames = cN;
        
        for (int i = 0; i < columnNames.length; ++i) 
        {
            if (columnNames[i].equals("Year"))
            {
                YEAR_INDEX = i;
            }
            else if (columnNames[i].equals("Module"))
            {
                MODULE_INDEX = i;
            }
            else if (columnNames[i].equals("Candidate Key"))
            {
                CANDKEY_INDEX = i;
            } else if (columnNames[i].equals("Mark"))
            {
                MARK_INDEX = i;
            } else if (columnNames[i].equals("Student")) {
                
                NAME_INDEX=i;
            }
        }
    }
}