package Data;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * <code>Window</code> that displays the students who received Anonymous marking codes and who didn't.
 * <p>
 * Creates a window which displays whether the students have received marking codes or not by
 * highlighting them using different colours. 
 * 
 * @author Hristo Tanev
 * @author Konstantin Terziev
 */

class StudentCellRenderer extends JLabel implements ListCellRenderer<Object> {
    /**
     * Allows you to use colours
     */
    public StudentCellRenderer() {
        setOpaque(true);
    }
    
    /**
     * <code>Window</code> that displays the information data
     * <p>
     * Populating the JList with students colouring them in the required colour of whether being
     * given marking codes (white) or not (orange). And by clicking on them an <code>Information window</code>
     * appears with current student's information
     * @param jlist
     * @param e
     * @param i
     * @param bln
     * @param bln1
     * @return Returns <code>ListCellRenderer</code> with the JList of students
     */
    public Component getListCellRendererComponent(JList<? extends Object> jlist, Object e, int i, boolean bln, boolean bln1) {
        setText(e.toString());
        JList.DropLocation dropLocation=jlist.getDropLocation();
        if (dropLocation!=null&&!dropLocation.isInsert()&&dropLocation.getIndex()==i) {
            Student s=(Student)e;
            if (!s.gotNewCodes()) {
                setBackground(new Color(248,152,29));
            }
            else {
                setBackground(Color.WHITE);
            }
        }
        else if (bln) {
            Student s=(Student)e;
            if (!s.gotNewCodes()) {
                setBackground(Color.PINK);
            }
            else {
                setBackground(new Color(14,207,229));
            }
        }
        else {
            Student s=(Student)e;
            if (!s.gotNewCodes()) {
                setBackground(new Color(248,152,29));
            }
            else {
                setBackground(Color.WHITE);
            }
        }
        setBorder(BorderFactory.createLineBorder(Color.black));

        return StudentCellRenderer.this;
    }
    
    /**
     * <code>Container</code> packing the student's information
     * <p>
     * Colours the sent student data (white) if there exists a marking code
     * for the student, (orange) if not and when clicked on the former type of student
     * it highlights the row in cyan, and the latter type in pink.
     * 
     * @return Returns a JPanel with the displayed student data
     */
    public static JPanel getColorMap() {
        JPanel colorMap=new JPanel();
        colorMap.setLayout(new BoxLayout(colorMap,BoxLayout.Y_AXIS));
        JLabel universal1=new JLabel();
        JPanel whiteColor=new JPanel();
        JLabel white=new JLabel("     ");
        white.setOpaque(true);
        white.setBackground(Color.white);
        universal1.setText("Not selected and received a code.");
        whiteColor.add(white);
        whiteColor.add(universal1);
        JLabel universal2=new JLabel();
        JPanel orangeColor=new JPanel();
        JLabel orange=new JLabel("     ");
        orange.setOpaque(true);
        orange.setBackground(Color.orange);
        universal2.setText("Not selected and didn't receive a code.");
        orangeColor.add(orange);
        orangeColor.add(universal2);
        JLabel universal3=new JLabel();
        JPanel pinkColor=new JPanel();
        JLabel pink=new JLabel("     ");
        pink.setOpaque(true);
        pink.setBackground(Color.pink);
        universal3.setText("Selected and didn't receive a code.");
        pinkColor.add(pink);
        pinkColor.add(universal3);
        JLabel universal4=new JLabel();
        JPanel blueColor=new JPanel();
        JLabel blue=new JLabel("     ");
        blue.setOpaque(true);
        blue.setBackground(Color.cyan);
        universal4.setText("Selected and received a code.");
        blueColor.add(blue);
        blueColor.add(universal4);
        colorMap.add(whiteColor);
        colorMap.add(pinkColor);
        colorMap.add(orangeColor);
        colorMap.add(blueColor);
        
        return colorMap;
    }
}
