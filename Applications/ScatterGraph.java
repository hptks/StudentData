package Applications;

import java.awt.Dimension;

import org.jfree.chart.ChartFrame;

import Data.ExamTableModel;

/**	
 *	<code>ScatterGraph</code> is a window which lets you display a Scatter Graph.
 *      
 *	@author		Konstantin Terziev
 *      @author         Hristo Tanev
 */
public class ScatterGraph extends ChartFrame {
    
    /**
     *	This constructor creates new <code>ScatterGraph</code> using data from an <code>ExamTableModel</code>.
     *  
     *  @param	eTM             <code>ExamTableModel</code> - the students we made from the student data from the Internet
     *  @param  assessmentName  <code>String</code> - the title of the Scatter Plot
     */
    public ScatterGraph(ExamTableModel eTM,String assessmentName)
    {
        super("ScatterPlot", eTM.getScatterChart(assessmentName));
        
        try {
            setIconImage(FileManager.getImage("Logo24.png").getImage());
        }
        catch(NullPointerException npe)
        {
            System.out.println("Something is wrong with the Images folder. Could not load image");
        }
        
        setPreferredSize(new Dimension(1200,700));
        pack();
        setVisible(true);
    }
}