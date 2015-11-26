package Data;

import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

import Applications.CSVReader;

public class AssessmentResult {
    
    Map<String, String> result;
    private StudentArrayList sal;
    
    public AssessmentResult(StudentArrayList sal, CSVReader reader, String[] columnNames)
    {
        this.sal = sal;
        result = new TreeMap<String, String>();
        
        String tempField;
        for(int i = 0; i < columnNames.length; ++i){
            if(!(tempField = reader.nextField()).equals(""))
            {
                result.put(columnNames[i], tempField);
            }
        }
    }
    
    public String getField(String columnName)
    {
        return result.get(columnName);
    }
    
        public boolean changeCandidateCodeToNumber()
    {
        String candidateKey = result.get("Cand Key");
        try {
            candidateKey = cleanCandidateKey(candidateKey);
            
            //Is it a Candidate Number?..
            Integer.parseInt(candidateKey);
            //... Yes it is.
            return true;
        }
        catch(NumberFormatException nfe)
        {
            //... No its not.
            //Look if we know this student from anonymous key loads.
            Student s = sal.findStudent(candidateKey);
            if (s != null)
            {
                //We do...
                result.put("Name", s.getSName());
                result.put("Cand Key", "" + s.getSNumber());
                return true;
            }
            //We don't.
            return false;
        }
    }
    
    public boolean tellStudent()
    {        
        String candidateKey = result.get("Cand Key");
        
        candidateKey = cleanCandidateKey(candidateKey);
        
        try {
            Student s = sal.findStudent(Integer.parseInt(candidateKey));
            //We know its a candidate number:
            if (s != null){
                //We know this student.
                s.addAssessmentResult(AssessmentResult.this);
                return true;
            }
            //We don't recognise this student.
            return false;
        }
        catch(NumberFormatException nfe)
        {
            //This AssessmentResult is not deanonymised.
            return false;
        }
    }
    
    public static String cleanCandidateKey(String s)
    {
        if (s.charAt(0) == '#')
        {
            s = s.substring(1, s.length());
        }
        
        if (s.charAt(s.length() - 2) == '/')
        {
            s = s.substring(0, s.length() - 2);
        }
        
        return s;
    }
    
    public static Comparator<String[]> yearComparator = new Comparator<String[]>() {
        
        public int compare(String[] t, String[] t1)
        {
            int p = Integer.parseInt(t[ExamTableModel.YEAR_INDEX].substring(0, t[ExamTableModel.YEAR_INDEX].length() - 2));
            int q = Integer.parseInt(t1[ExamTableModel.YEAR_INDEX].substring(0, t1[ExamTableModel.YEAR_INDEX].length() - 2));
            
            return q - p;
        }
    };
    
    public static Comparator<String[]> moduleComparator = new Comparator<String[]>() 
    {
        public int compare(String[] t, String[] t1)
        {
            String p = t[ExamTableModel.MODULE_INDEX];
            String q = t1[ExamTableModel.MODULE_INDEX];
            
            return p.compareTo(q);
        }
    };
    
    public static Comparator<String[]> candidateKeyComparator=new Comparator<String[]>() {
        
        public int compare(String[] t, String[] t1) 
        {
            try {
                int p = Integer.parseInt(cleanCandidateKey(t[ExamTableModel.CANDKEY_INDEX]));
                int q = Integer.parseInt(cleanCandidateKey(t1[ExamTableModel.CANDKEY_INDEX]));
                
                return p - q;
            }
            catch(NumberFormatException nfe)
            {
                String p = t[3];
                String q = t1[3];
                
                return p.compareTo(q);
            }
        }
    };
    
    public String toString() {
        String accumulator = getField("Year") + ", " +
                             getField("Module") + ", " +
                             getField("Ass") + ", " +
                             getField("Cand Key") + ", " +
                             getField("Mark") + ", " +
                             getField("Grade");
        
        return accumulator;
    }
}