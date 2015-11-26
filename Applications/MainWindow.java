package Applications;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.FileSystemException;

import Data.Student;
import Data.StudentArrayList;
import Data.ExamTableModel;
import Data.SchoolYears;
import Data.Results;
import Data.Module;
import Data.Assessment;

import Applications.Email.EmailEditor;
import Applications.Email.EmailSettings;
import Applications.Scrape_KEATS.Scrape;
import Applications.CreatePDF.CreatePDF;

/**
 * <code>Application</code> which receives data from a server to create a suite of components
 * <p>
 * When loaded a JList is populated with the data of <code>Students</code> retrieved from the server.
 * Where each of the students is shown with his name and student number surrounded in brackets. When
 * a student is clicked on a <code>Information Window</code> pops out with student's personal data along
 * with his exam results (if they are loaded) and last time visit on KEATS.
 * <p>
 * Also there is a JMenuBar on the top where you can load the exam results by selecting the files needed.
 * When exam results are fetched from the files, a JTables are populated with the data inside tabs (a different
 * one for each assessment). When the anonymous results are loaded a window pops out giving you information about
 * the students who received Anonymous marking codes and who didn't. When you click on each one of them the same
 * <code>Information window</code> is shown as of the JList.
 * <p>
 * After you load the marking codes you can view the scatter graph generated for the current assessment you are on.
 * There are some extra options like sending email to the students with their results on each of the modules or downloading
 * the exam information as a PDF copy.
 * 
 * @author Hristo Tanev
 * @author Johnson Wong
 * @author Konstantin Terziev
 */

public class MainWindow extends JFrame implements KeyListener {

    JTabbedPane assTab=new JTabbedPane();
    JButton searchButton;
    JComboBox searchElements;
    JList studentList;
    JMenuItem lExamResults;
    JTextField searchInput; //planning to add ghostText. Do not delete unless implemented.
    JLabel statusBar;
    StudentArrayList students;
    private JFileChooser fileChooser;
    DefaultListModel<Student> model;
    ArrayList<String> diffAssessment;
    Map<String, ExamTableModel> observers;
    int indexChangeTab=0;
    DefaultListModel<Student> tempModel=new DefaultListModel<Student>();
    /**
     * Loading and showing all the functions described above using two parameters: username and password
     *
     * @param username the received username from the Login window
     * @param password the received password from the Login window
     */
    public MainWindow(String username, String password) {
        String[] columnNames = {"Year", "Module", "Assessment", "Student", "Candidate Key", "Mark", "Grade"};
        ExamTableModel.setColumns(columnNames);
        
        observers = new TreeMap<String, ExamTableModel>();
        String[] searchOptions = {"Filter","Student name", "Student number", "Student email", "Tutor email"};
        searchButton = new JButton("Search");
        searchInput = new JTextField();
        searchElements = new JComboBox(searchOptions);
        students = new StudentArrayList(username, password);
        statusBar = new JLabel("Status: Last Action" + students.getSize() + " students collected from the Internet.");
        model=new DefaultListModel<Student>();
        students.produceList();
        ArrayList<Student> arrS=students.getList();
        for (Student arr : arrS) {
            model.addElement(arr);
            tempModel.addElement(arr);
        }
        studentList=new JList(model);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lExamResults = new JMenuItem("Load exam results");
        JMenuItem lAMarkingCodes = new JMenuItem("Load anonymous marking codes");
        JMenuItem exit = new JMenuItem("Exit");
        JPanel displayPanel = new JPanel(new GridLayout()), listPanel = new JPanel(), searchPanel = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        JScrollPane scrollPane1 = new JScrollPane(studentList);
        JMenu menuFile = new JMenu("File");
        JMenu data=new JMenu("Data");
        final JMenuItem cta=new JMenuItem("Compare to Average");
        JMenuItem ets=new JMenuItem("Email to Students");
        JMenuItem es=new JMenuItem("Email settings");
        JMenuItem fPD = new JMenuItem("Fetch Participation");
        JMenuItem createPDF = new JMenuItem("Create PDF");
        data.add(cta);
        data.addSeparator();
        data.add(ets);
        data.add(es);
        data.addSeparator();
        data.add(fPD);
        data.addSeparator();
        data.add(createPDF);
        assTab.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JTabbedPane t = (JTabbedPane) ce.getSource();
                indexChangeTab = t.getSelectedIndex();
            }
        });
        cta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (observers.size() > 0) {

                    String t = assTab.getTitleAt(indexChangeTab), s = "";
                    for (int i = 11; i < t.length(); i++) {
                        s += t.charAt(i);
                    }

                    ExamTableModel v = observers.get(s);
                    if (v.hasTableGotData()) {
                        new ScatterGraph(observers.get(s), t).setLocationRelativeTo(MainWindow.this);
                    }
                }
            }
        });
        ets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new EmailEditor(students).setLocationRelativeTo(MainWindow.this);
            }
        });
        es.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new EmailSettings(MainWindow.this).setLocationRelativeTo(MainWindow.this);
            }
        });
        fPD.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new Scrape(MainWindow.this, students);
            }
        });
        createPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreatePDF(MainWindow.this, students);
            }
        });
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        searchElements.setSelectedItem(0);
        searchButton.setVisible(false);
        menuBar.add(menuFile);
        menuBar.add(data);
        menuFile.add(lAMarkingCodes);
        menuFile.add(lExamResults);
        menuFile.addSeparator();
        menuFile.add(exit);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchElements, BorderLayout.NORTH);
        searchPanel.add(searchInput, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        listPanel.setLayout(new BorderLayout());
        listPanel.add(searchPanel, BorderLayout.NORTH);
        listPanel.add(scrollPane1, BorderLayout.CENTER);
        searchInput.setEditable(true);
        searchInput.addKeyListener(this);
        studentList.setModel(tempModel);
        fileChooser = new JFileChooser(new File(".").getAbsolutePath());
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        fileChooser.setFileFilter(filter);
        diffAssessment=new ArrayList<String>();
        lAMarkingCodes.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) 
            {
                int response = fileChooser.showOpenDialog(MainWindow.this);
                
                if (response == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        File[] chosenFiles = fileChooser.getSelectedFiles();
                        for (File csvFile : chosenFiles)
                        {
                            statusBar.setText("<html>Status: <b>Last Action:</b> " + students.addAnonymousMarkingCodes(csvFile) + "</html>");
                        }
                    }
                    catch(IOException ioe)
                    {
                        System.out.println(ioe.getMessage());
                        System.exit(-1);
                    }
                    students.produceList();
                    JOptionPane.showMessageDialog(MainWindow.this, students.getFileResults(), "Results", JOptionPane.PLAIN_MESSAGE);
                    for (int i=0;i<students.getList().size();i++) {
                        students.getList().get(i).resetNewCodesListener();
                    }
                }
            }
        });
        lExamResults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int response = fileChooser.showOpenDialog(MainWindow.this);
                
                if (response == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        File[] chosenFiles = fileChooser.getSelectedFiles();
                        
                        SchoolYears schoolYears = new SchoolYears();
                        Results examResults;
                        Module module;
                        
                        for (File csvFile : chosenFiles)
                        {
                            ArrayList<String> schoolYearsInFile = CSVReader.getDifferentNamesFor("Year", csvFile);
                            for (String schoolYear : schoolYearsInFile)
                            {
                                if ((examResults = schoolYears.getResults(schoolYear)) == null)
                                {
                                    examResults = new Results(schoolYear);
                                }
                                
                                ArrayList<String> modulesInFile = CSVReader.getDifferentNamesFor("Module", csvFile);
                                for (String moduleName : modulesInFile)
                                {
                                    if ((module = examResults.getModule(moduleName)) == null)
                                    {
                                        module = new Module(students, moduleName);
                                    }
                                    ArrayList<String> assessmentsInFile = CSVReader.getDifferentNamesFor("Ass", csvFile);
                                    for(String assessmentName : assessmentsInFile)
                                    {
                                        Assessment currentA = module.addAssessment(assessmentName, csvFile);
                                        if (diffAssessment.size()==0) {
                                            makeNewTab(currentA);
                                        }
                                        else {
                                            boolean ok=false;
                                            for (String t : diffAssessment) {
                                                if (assessmentName.equals(t)) {
                                                    ok = true;
                                                    break;
                                                }
                                            }
                                            if (!ok) {
                                                makeNewTab(currentA);
                                            }
                                            else
                                            {
                                                currentA.setObserver(observers.get(currentA.getAssessmentName()));
                                            }
                                        }
                                        currentA.makeAssessmentResults();
                                        observers.get(currentA.getAssessmentName()).putOnTable();
                                    }
                                    examResults.addModule(module);
                                }
                                schoolYears.addResults(examResults);
                            }
                        }
                    }
                    catch(FileNotFoundException | FileSystemException e)
                    {
                        System.out.println(e.getMessage());
                        System.exit(-1);
                    }
                    catch(IOException ioe)
                    {
                        System.out.println(ioe.getMessage());
                        System.exit(-1);
                    }
                    if (!(MainWindow.this.getExtendedState() == JFrame.MAXIMIZED_BOTH)) {
                        pack();
                    }
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                model.clear();
                String sRes=searchInput.getText().toLowerCase();
                if (!sRes.isEmpty()) {
                    StudentArrayList sAl=new StudentArrayList();
                    switch (searchElements.getSelectedItem().toString()) {
                        case "Student name": {
                            try {
                                sAl=students.findNames(sRes);
                                sAl.produceList();
                                ArrayList<Student> arrS = sAl.getList();
                                for (Student arr : arrS) {
                                    model.addElement(arr);
                                }

                                studentList.setModel(model);
                            } catch (NullPointerException e) {
                                searchInput.setText("");
                                JOptionPane.showMessageDialog(MainWindow.this,"Not found. Enter a valid name.","Search",JOptionPane.INFORMATION_MESSAGE);
                            }
                            break;
                        }
                        case "Student number": {
                            try {
                                Student s=students.findStudent(Integer.parseInt(sRes));
                                sAl.addStudent(s);
                                sAl.produceList();
                                ArrayList<Student> arrS = sAl.getList();
                                for (Student arr : arrS) {
                                    model.addElement(arr);
                                }

                                studentList.setModel(model);
                            } catch (NullPointerException e) {
                                searchInput.setText("");
                                JOptionPane.showMessageDialog(MainWindow.this,"Not found. Enter a valid number.","Search",JOptionPane.INFORMATION_MESSAGE);
                            }
                            break;
                        }
                        case "Student email": {
                            try {
                                sAl=students.findEmails(sRes);
                                sAl.produceList();
                                ArrayList<Student> arrS = sAl.getList();
                                for (Student arr : arrS) {
                                    model.addElement(arr);
                                }

                                studentList.setModel(model);
                            } catch (NullPointerException e) {
                                searchInput.setText("");
                                JOptionPane.showMessageDialog(MainWindow.this,"Not found. Enter a valid email.","Search",JOptionPane.INFORMATION_MESSAGE);
                            }
                            break;
                        }
                        case "Tutor email": {
                            try {
                                sAl=students.findTutors(sRes);
                                sAl.produceList();
                                ArrayList<Student> arrS = sAl.getList();
                                for (Student arr : arrS) {
                                    model.addElement(arr);
                                }

                                studentList.setModel(model);
                            } catch (NullPointerException e) {
                                searchInput.setText("");
                                JOptionPane.showMessageDialog(MainWindow.this,"Not found. Enter a valid tutor email.","Search",JOptionPane.INFORMATION_MESSAGE);
                            }
                            break;
                        }
                        default: { break; }
                    }
                }
            }
        });
        
        searchElements.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (searchElements.getSelectedItem().equals("Filter")) {
                    searchButton.setVisible(false);
                    searchInput.setText(null);
                    searchInput.addKeyListener(MainWindow.this);
                    studentList.setModel(tempModel);
                }
                else {
                    searchInput.setText(null);
                    studentList.setModel(tempModel);
                    searchButton.setVisible(true);
                }
            }
        });
        ListSelectionModel listSM=studentList.getSelectionModel();
        listSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    ListSelectionModel lsm=(ListSelectionModel)lse.getSource();
                    int indexChanged=0;
                    if (!lsm.isSelectionEmpty()) {
                        for (int i=0;i<model.size();i++) {
                            if (lsm.isSelectedIndex(i)) {
                                indexChanged=i;
                                break;
                            }
                        }

                        String s=model.getElementAt(indexChanged).toString();
                        int i=s.length()-1,num=0;
                        while (s.charAt(i)!='(') {
                            i--;
                        }

                        for (int j=i+1;j<s.length()-1;j++) {
                            num=(10*num)+(int)s.charAt(j)-'0';
                        }

                        Student st=students.findStudent(num);
                        new InformationWindow(st).setLocationRelativeTo(MainWindow.this);
                    }
                }
            }
        });
        try {
            setIconImage(FileManager.getImage("Logo24.png").getImage());
        }
        catch(NullPointerException npe)
        {
            System.out.println("Something is wrong with the Images folder. Could not load image");
        }
        this.setJMenuBar(menuBar);
        this.setLayout(new BorderLayout());
        this.setTitle("PRA Coursework - 11OldMen");
        displayPanel.add(BorderLayout.CENTER,assTab);
        this.add(displayPanel, BorderLayout.CENTER);
        this.add(listPanel, BorderLayout.WEST);
        this.add(statusBar, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(1000,721));
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        setLocationRelativeTo(null);
    }

    public void keyTyped(KeyEvent ke) {}
    public void keyPressed(KeyEvent ke) {}

    public void keyReleased(KeyEvent ke) {
        if (!searchButton.isVisible()) {
            String searchInfo=searchInput.getText().toLowerCase();
            DefaultListModel<Student> mod=new DefaultListModel<Student>();
            for (int i=0;i<tempModel.getSize();i++) {
                mod.addElement(tempModel.elementAt(i));
            }
            ArrayList<Student> res=new ArrayList<Student>();
            for (int i=0;i<mod.getSize();i++) {
                Student t=mod.getElementAt(i);
                String p=t.getSName().toLowerCase();
                boolean ok=false;
                for (int j=0;j<p.length()-searchInfo.length();j++) {
                    String com="";
                    for (int k=j;k<j+searchInfo.length();k++) {
                        com+=p.charAt(k);
                    }
                    if (searchInfo.equals(com)) {
                        ok=true;
                        break;
                    }
                }

                if (ok) {
                    res.add(t);
                }
            }

            mod.clear();
            for (Student re : res) {
                mod.addElement(re);
            }
            studentList.setModel(mod);
        }
    }
    
    private void makeNewTab(Assessment a)
    {
        diffAssessment.add(a.getAssessmentName());
        
        ExamTableModel eTM = new ExamTableModel(students);
        a.setObserver(eTM);
        observers.put(a.getAssessmentName(), eTM);
        
        final JTable table=new JTable(eTM);
        table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent me) {
                int row=table.rowAtPoint(me.getPoint());
                int column=table.columnAtPoint(me.getPoint());
                if (row>=0&&column>=0&&column==ExamTableModel.NAME_INDEX) {
                    String tab=assTab.getTitleAt(indexChangeTab);
                    String res=tab.substring(11,tab.length());
                    int p=observers.get(res).getCandidateNumberFromRow(row);
                    Student s = students.findStudent(p);
                    new InformationWindow(s).setLocationRelativeTo(MainWindow.this);
                }
            }

            public void mousePressed(MouseEvent me) {}

            public void mouseReleased(MouseEvent me) {}

            public void mouseEntered(MouseEvent me) {}

            public void mouseExited(MouseEvent me) {}
            
        });
        JScrollPane scroll = new JScrollPane(table);
        assTab.addTab("Assessment " + a.getAssessmentName(),scroll);
    }
    
    public StudentArrayList getStudentArrayListField()
    {
        return students;
    }
}

/** Change log (Everyone is free to contribute ^^)
 07/02/2015 - Johnson Wong
 Changed Constructor
 Renamed for easier code legibility
 Expanded imports (* is bad practice?)
 Restructured, optimized codes
 Restructured window layouts
 Fixed bugs
 Removed unneeded statements
 Added JComponents, actionListener
 Placed studentList in JScrollPane
 Made studentList functional
 Added search skeleton
 Added status Bar
-------------------------------------
 */