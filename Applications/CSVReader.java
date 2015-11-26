package Applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.FileSystemException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**	
 *	<code>CSVReader</code> is a tool to read CSV Files.
 *	<p>
 *	There two main modes that the tool can be used. One if to read one field
 *      at at time going left to right first and the top to bottom. If you know
 *      however that that the CSV file contains a header (column names) and the
 *      data is a table format then you select from which columns in a row to read
 *      and the others are skipped.
 *      <p>
 *      If a field data is enclosed in speech marks
 *      the field as it is collected with all character. If the speech marks are
 *      missing some illegal characters are removed. Currently the '#' is not
 *      allowed.
 *      <p>
 *      One know issue a the minute is that tool doesn't figure out when it reaches
 *      the end of file if we reading in selection mode. To work around this
 *      you need to catch a <code>NullPointerException</code> when using the
 *      <code>nextField()</code> method.
 *      
 *	@author		Konstantin Terziev
 */
public class CSVReader {
    
    /**
     *	Gets text input from file including whitespace and other things.
     */
    private BufferedReader lineInput;
    /**
     *	The text data from the file.
     */
    private String data;
    /**
     *	<code>subtring</code> start index. 
     */
    private int startIndex;
    /**
     *	<code>subtring</code> end index or the index of the current character we are processing.
     */
    private int currentPosition;
    /**
     *	We take away from <code>currentPosition</code> and add it startIndex so we don't collect speech marks as data.
     */
    private int doubleQuotesCorrection;
    /**
     *	It tell us whether the file has columns names. This is specified by the user. Cannot really tell from a file.
     */
    private boolean gotHeader;
    /**
     *	A map whose key is a column name that maps onto column index.
     */
    private Map<String, Integer> header;
    /**
     *	We use this to start on the next row (where the data starts) if there are columns in the CSV file.
     */
    private int lengthOfHeader;
    /**
     *	The indexes of the columns the user wants us to select.
     */
    private int[] columnsToRead;
    /**
     *	The current column index we working with.
     */
    private int columnIndex;
    
    /**
     *	This constructor creates a <code>CSVReader</code> instance which can be used on CSV files.
     */
    public CSVReader()
    {
        header = new TreeMap<String, Integer>();
        lineInput = null;
    }
    
    /**
     *	Get the text data from the file. Once we do this we can work on the data from the file.
     *  
     *  @param csvFile  <code>File</code> - the CSV file the user wants us to read.
     *  
     *  @throws java.io.IOException                 You can receive a <code>IOException</code> if there is something illegible in the CSV file
     *  @throws java.io.FileNotFoundException       You can receive a <code>FileNotFoundException</code> if the file path you used doesn't point to a actual
     *  @throws java.nio.file.FileSystemException   You can receive a <code>FileSystemException</code> if the file you are using is empty
     */
    public void extract(File csvFile) throws FileNotFoundException, FileSystemException, IOException 
    {
        lineInput = new BufferedReader(new FileReader(csvFile));
        data = "";
        currentPosition = 0;
        startIndex = 0;
        lengthOfHeader = 0;
        gotHeader = false;
        
        String tempLine;
        while(true)
        {
            tempLine = lineInput.readLine();
            
            if (tempLine == null)
            {
                break;
            }
            else
            {
                data = data + tempLine + "\n";
            }
        }
        
        if (data.equals(""))
        {
            throw new FileSystemException(csvFile.getAbsolutePath(), null,
                                                "This file is empty.");
        }
    }
    
    /**
     *	Get the next piece of information.
     *  
     *  @return   <code>String</code> the next field on the current row or the next row if we are at the end of the current row.
     *  
     *  @see    #moveToNextDelimeter()
     *  @see    #nextField() 
     */
    public String nextPieceOfData()
    {
        moveToNextDelimeter();
        
        String piece = data.substring(startIndex + doubleQuotesCorrection,
                                        currentPosition - doubleQuotesCorrection);
        ++currentPosition;
        startIndex = currentPosition;
        
        if (doubleQuotesCorrection == 0)
        {
            piece = ridIllegalCharacters(piece);
        }
        
        return piece;
    }
    
    /**
     *	Select the next field of information.
     *  
     *  @return   <code>String</code> the next field on the current row at the next column specified column or the next row if we are at the end of the current row.
     *  
     *  @see    #moveToNextDelimeter()
     *  @see    #nextField() 
     */
    public String nextField()
    {
        
        if (columnIndex == columnsToRead.length - 2)
        {
            //Get back to the begginning.
            for(int i = columnsToRead[columnIndex]; i < lengthOfHeader; ++i)
            {
                moveToNextDelimeter();
                ++currentPosition;
            }
            columnIndex = 0;
        }
        
        if (finishedReading())
        {
            return "";
        }
        
        if (gotHeader == true)
        {
            for(int i = columnsToRead[columnIndex] + 1; i < columnsToRead[columnIndex + 1]; ++i)
            {
                moveToNextDelimeter();
                ++currentPosition;
            }
            startIndex = currentPosition;
            moveToNextDelimeter();
            
            ++columnIndex;
        }
        else
        {
            startIndex = currentPosition;
            moveToNextDelimeter();
        }
        
        String field = data.substring(startIndex + doubleQuotesCorrection,
                                        currentPosition - doubleQuotesCorrection);
        
        ++currentPosition;
        if (doubleQuotesCorrection == 0)
        {
            field = ridIllegalCharacters(field);
        }
        
        return field;
    }
    
    /**
     *	Generate the end index of a data item. Delimeters are new lines (<code>\n</code>) or commas (<code>,</code>).
     *  
     *  @see    #nextPieceOfData()
     *  @see    #nextField() 
     */
    private void moveToNextDelimeter()
    {
        doubleQuotesCorrection = 0;
        
        char currentCharacter = data.charAt(currentPosition);
        
        while(!(currentCharacter == ',') && !(currentCharacter == '\n')){
            if (currentCharacter == '"')
            {
                doubleQuotesCorrection = 1;
            }
            ++currentPosition;
            currentCharacter = data.charAt(currentPosition);
        }
    }
    
    /**
     *	Check if we have read the last information character in the CSV file.
     *  
     *  @return <code>boolean</code> - fact whether we have reached the end of the CSVFile
     */
    public boolean finishedReading()
    {
        return currentPosition >= data.length() - 2;
    }
    
    /**
     *	Work out the column indexes for all columns.
     *  
     *  @see    #setColumnsToRead(String[] columnNames)
     */
    public void indentifyColumns()
    {
        int columnNumber = 0;
        currentPosition = 0;
        
        char currentCharacter = data.charAt(currentPosition);
        
        while(true){
            doubleQuotesCorrection = 0;
            moveToNextDelimeter();
            
            String title = ridIllegalCharacters(data.substring(startIndex + doubleQuotesCorrection,currentPosition - doubleQuotesCorrection));
            if (header.get(title) == null)
            {
                header.put(title, new Integer(++columnNumber));
            }
            
            ++lengthOfHeader;
            
            if (data.charAt(currentPosition) == '\n')
            {
                break;
            }
            
            ++currentPosition;
            currentCharacter = data.charAt(currentPosition);
            startIndex = currentPosition;
        }
        ++currentPosition;
        gotHeader = true;
    }
    
    /**
     *	Set the columns to select field from in rows.
     *  
     *  @param columnNames <code>String[]</code> - a list of column names (e.g. <code>{"Column1", "Column2", ...}</code>)
     */
    public void setColumnsToRead(String[] columnNames)
    {
        columnsToRead = new int[columnNames.length + 2];
        int index = 1;
        
        columnNames = sortColumnNames(columnNames);
        
        columnsToRead[0] = 0;
        for(String name : columnNames){
            columnsToRead[index] = header.get(name).intValue();
            ++index;
        }
        columnsToRead[columnsToRead.length - 1] = columnsToRead[columnsToRead.length - 2] + 1;
        columnIndex = 0;
    }
    
    /**
     *	Match up with the header what the user has specified for the columns we are going to select.
     *  
     *  @param columnNames <code>String[]</code> - a unsorted list of column names (e.g. <code>{"Column2", "Column1", ...}</code>)
     *  
     *  @return            <code>String[]</code> - a sorted list of column names (e.g. <code>{"Column1", "Column2", ...}</code>)
     */
    private String[] sortColumnNames(String[] columnNames)
    {
        String tempName;
        
        int swaps = 1;
        while(swaps != 0){
            swaps = 0;
            for(int i = 0; i < columnNames.length - 1; ++i){
                if (header.get(columnNames[i]).intValue() > header.get(columnNames[i + 1]).intValue())
                {
                    tempName = columnNames[i];
                    columnNames[i] = columnNames[i + 1];
                    columnNames[i + 1] = tempName;
                    ++swaps;
                }
            }
        }
        
        return columnNames;
    }
    
    /**
     *	Check if we a character is not a legal character.
     *  <p>
     *  If you want to include this character you could surround the data field in quotes.
     *  
     *  @param  c   <code>char</code> - the character we are checking.
     *  
     *  @return     <code>boolean</code> - fact whether we have reached the end of the CSVFile
     * 
     *  @see        #ridIllegalCharacters(String field)
     */
    public static boolean notLegalCharacter(char c){
        return c == '#';
    }
    
    /**
     *	Remove all the illegal characters from the data field.
     *  <p>
     *  If you want to include this character you could surround the data field in quotes.
     *  
     *  @param field    <code>String</code> - the data item are removing illegal characters from
     *  
     *  @return         <code>String</code> - the modified free of illegal characters string
     *  
     *  @see            #notLegalCharacter(char c)
     */
    public static String ridIllegalCharacters(String field){
        for(int i = 0; i < field.length(); ++i){
            if (notLegalCharacter(field.charAt(i)))
            {
                field = field.substring(0, i) + field.substring(i + 1, field.length());
            }
        }
        
        return field;
    }
    
    /**
     *	Get a list of all the different occurring data items within a specified column from a CSV file.
     *  
     *  @param c        <code>String</code> - the column we searching through
     *  @param csvFile  <code>File</code> - the file we need the information for
     *  
     *  @return         <code>ArrayList&lt;String&gt;</code> - a list of all the different occurring items
     * 
     *  @throws java.io.IOException                 You can receive a <code>IOException</code> if there is something illegible in the CSV file
     *  @throws java.io.FileNotFoundException       You can receive a <code>FileNotFoundException</code> if the file path you used doesn't point to a actual
     *  @throws java.nio.file.FileSystemException   You can receive a <code>FileSystemException</code> if the file you are using is empty
     */
    public static ArrayList<String> getDifferentNamesFor(String c, File csvFile) throws FileNotFoundException, FileSystemException, IOException
    {
        CSVReader reader = new CSVReader();
        reader.extract(csvFile);
        reader.indentifyColumns();
        final String[] column = {c};
        ArrayList<String> namesForThisInFile = new ArrayList<String>();
        reader.setColumnsToRead(column);
        
        String newName;
        String oldName = "";
        while(!reader.finishedReading())
        {
            newName = reader.nextField();
            
            if(!newName.equals(oldName) && !newName.equals(""))
            {
                namesForThisInFile.add(newName);
                oldName = newName;
            }
            else
            {
                oldName = newName;
            }
        }
        return namesForThisInFile;
    }
}
