package Data;

import java.util.ArrayList;
import java.util.Observable;

import Applications.CSVReader;


public class Assessment extends Observable {
    
    private final String[] columnNames = {"Year", "Module", "Ass", "Name", "Mark", "Grade", "Cand Key"};
    private ArrayList<AssessmentResult> assessmentResults;
    private String assessmentName;
    private String moduleName;
    private StudentArrayList sal;
    private CSVReader reader;
    private boolean beingObserved;
    
    public Assessment(StudentArrayList sAL, CSVReader r, String mN, String aN)
    {
        assessmentResults = new ArrayList<AssessmentResult>();
        sal = sAL;
        reader = r;
        assessmentName = aN;
        moduleName = mN;
        
        beingObserved = false;
        
        reader.setColumnsToRead(columnNames);
    }
    
    public String getAssessmentName()
    {
        return assessmentName;
    }
    
    public void makeAssessmentResults()
    {
        if (beingObserved)
        {
            while(!reader.finishedReading())
            {
                AssessmentResult aResult = new AssessmentResult(sal, reader, columnNames);
                
                try
                {
                    if (aResult.getField("Module").equals(moduleName)
                            && aResult.getField("Ass").equals(assessmentName))
                    {
                        if (aResult.changeCandidateCodeToNumber() == true)
                        {
                            assessmentResults.add(aResult);
                            aResult.tellStudent();
                            setChanged();
                            notifyObservers(aResult);
                        }
                    }
                }
                catch(NullPointerException npe)
                {
                    //Work around for CSVReader not identifying the end of file by using nextField().
                }
            }
        }
    }
    
    public void setObserver(ExamTableModel eTM)
    {
        if (!beingObserved)
        {
            addObserver(eTM);
            beingObserved = true;
        }
    }
    
    public String toString()
    {
        String accumulator = "Assessment " + assessmentName + ":\n";
        
        for (AssessmentResult ar : assessmentResults) {
            accumulator += ar + "\n";
        } 
        
        return accumulator;
    }
}