package Applications.Email;

import javax.swing.table.AbstractTableModel;

/**
 * Created by NIghtCrysIs on 2015/03/13.
 *
 * <code>CustomTableModel</code> creates a custom table model for the purpose of EmailEditor
 * <p>Extends AbstractTableModel</p>
 *
 * @author Johnson Wong
 * @since 1.7
 */

public class CustomTableModel extends AbstractTableModel{
    String[] columnTabs;
    Object[][] rowList;

    /** Constructor for <code>CustomTableModel</code>.
     *
     *  @param studentNameList list of student names
     *  @param columnArray String array of text, each entry denotes the name and entry in
     *                     the table model.
     */

    public CustomTableModel(String[] studentNameList, String[] columnArray){
        rowList = createDataList(studentNameList);
        columnTabs = columnArray;
    }

    /** Creates and returns a 2-dimensional array, with each row entry
     * consisting of a student name, and a <b>boolean</b> false.
     *
     *  @param studentNameList list of student names
     *
     *  @return tempList The created 2-dimensional array, type object
     */

    private Object[][] createDataList(String[] studentNameList){
        Object[][] tempList = new Object[studentNameList.length][2];
        for(int x = 0; x < studentNameList.length; x++){
            tempList[x][0] = studentNameList[x];
            tempList[x][1] = Boolean.FALSE;
        }
        return tempList;
    }

    /**
     * Overrides setValueAt
     */

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
        if(columnIndex==1){
            rowList[rowIndex][1] = aValue;
        }
    }

    /**
     * Overrides getColumnName
     */

    @Override
    public String getColumnName(int column){
        return columnTabs[column];
    }

    /**
     * Overrides getColumnClass
     */

    @Override
    public Class getColumnClass(int column){
        return (getValueAt(0, column)).getClass();
    }

    /**
     * Overrides getRowCount
     */

    @Override
    public int getRowCount() {
        return rowList.length;
    }

    /**
     * Overrides getColumnCount
     */

    @Override
    public int getColumnCount() {
        return columnTabs.length;
    }

    /**
     * Overrides getValueAt
     */

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowList[rowIndex][columnIndex];
    }

    /**
     * Overrides isCellEditable
     */

    @Override
    public boolean isCellEditable(int row, int column){
        return (column==1);
    }
}
