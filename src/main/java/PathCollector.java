import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class PathCollector extends JFrame {

    private JTextField fieldParentFolder;
    private static JTextField fileCreatedNameField;
    private JLabel labelMessage;
    private JButton buttonSelectPath;
    private JButton buttonSubmit;
    private JButton buttonBrowseFile;
    private JButton buttonSelectFileName;
    private static JCheckBox checkBoxPng;
    private static JCheckBox checkBoxTif;
    private static JCheckBox checkBoxJpg;
    private static String enteredParentDirectoryPath;
    private static String enteredFileName;
    private static File scannedDirectory;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    static ArrayList<String> extentions = new ArrayList<String>();
    static JFileChooser selectDirectory = new JFileChooser();

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PathCollector().setVisible(true);
            }
        });
    }

    public PathCollector(){
        createView();
        setTitle("Jungo's Path Collector");
        //Set icon in Main window
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        //Make window exit application on close
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Set display size
        //setPreferredSize(new Dimension(450, 250));
        pack();
        //Center the frame to middle of screen
        setLocationRelativeTo(null);
        //Disable resize
        setResizable(false);
    }

    private void createView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel checkBoxLabelPanel = new JPanel(new GridLayout(0,1));
        checkBoxLabelPanel.setPreferredSize(new Dimension(450, 25));
        JPanel checkBoxPanel = new JPanel(new GridLayout(1,1));
        checkBoxPanel.setPreferredSize(new Dimension(450, 25));
        JPanel pathLabelPanel = new JPanel(new GridLayout(0,1));
        pathLabelPanel.setPreferredSize(new Dimension(450, 25));
        JPanel pathPanel = new JPanel(new FlowLayout());
        pathPanel.setPreferredSize(new Dimension(450, 40));
        JPanel fileCreatedNameLabel = new JPanel(new GridLayout(0,1));
        fileCreatedNameLabel.setPreferredSize(new Dimension(450, 25));
        JPanel fileCreatedNamePanel = new JPanel(new FlowLayout());
        fileCreatedNamePanel.setPreferredSize(new Dimension(450, 40));
        JPanel labelMessagePanel = new JPanel(new FlowLayout());
        labelMessagePanel.setPreferredSize(new Dimension(450, 30));

        getContentPane().add(mainPanel);

        mainPanel.add(checkBoxLabelPanel);
        mainPanel.add(checkBoxPanel);
        mainPanel.add(pathLabelPanel);
        mainPanel.add(pathPanel);
        mainPanel.add(fileCreatedNameLabel);
        mainPanel.add(fileCreatedNamePanel);
        mainPanel.add(labelMessagePanel);

        JLabel labelCheckBox = new JLabel("Select required images extentions");
        labelCheckBox.setHorizontalAlignment(JLabel.CENTER);
        checkBoxLabelPanel.add(labelCheckBox);

        checkBoxPng = new JCheckBox("PNG");
        checkBoxPng.setHorizontalAlignment(JCheckBox.RIGHT);
        checkBoxJpg = new JCheckBox("JPG");
        checkBoxJpg.setHorizontalAlignment(JCheckBox.CENTER);
        checkBoxTif = new JCheckBox("TIF");
        checkBoxTif.setHorizontalAlignment(JCheckBox.LEFT);
        checkBoxPanel.add(checkBoxPng);
        checkBoxPanel.add(checkBoxJpg);
        checkBoxPanel.add(checkBoxTif);
        HandlerClass handler = new HandlerClass();
        checkBoxPng.addItemListener(handler);
        checkBoxJpg.addItemListener(handler);
        checkBoxTif.addItemListener(handler);

        JLabel labelDirectory = new JLabel("Enter your parent directory");
        labelDirectory.setHorizontalAlignment(JLabel.CENTER);
        pathLabelPanel.add(labelDirectory);

        fieldParentFolder = new JTextField();
        fieldParentFolder.setPreferredSize(new Dimension(350, 30));
        pathPanel.add(fieldParentFolder);

        buttonSelectPath = new JButton("...");
        buttonSelectPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int ret = selectDirectory.showDialog(null, "Select Path");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    enteredParentDirectoryPath = selectDirectory.getSelectedFile().getAbsolutePath();
                    fieldParentFolder.setText(enteredParentDirectoryPath);
                }
            }
        });
        setCursorStyle(buttonSelectPath);
        buttonSelectPath.setPreferredSize(new Dimension(40, 29));
        pathPanel.add(buttonSelectPath);


        JLabel labelFileName = new JLabel("Enter name of result's file or select file");
        labelFileName.setHorizontalAlignment(JLabel.CENTER);
        fileCreatedNameLabel.add(labelFileName);

        fileCreatedNameField = new JTextField();
        fileCreatedNameField.setText("jungo_path_collector.txt");
        fileCreatedNameField.setPreferredSize(new Dimension(200, 30));
        fileCreatedNamePanel.add(fileCreatedNameField);

        buttonSelectFileName = new JButton("...");
        buttonSelectFileName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectDirectory.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int ret = selectDirectory.showDialog(null, "Open");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    enteredFileName = selectDirectory.getSelectedFile().getAbsolutePath();
                    fileCreatedNameField.setText(enteredFileName);
                }
            }
        });
        setCursorStyle(buttonSelectFileName);
        buttonSelectFileName.setPreferredSize(new Dimension(40, 29));
        fileCreatedNamePanel.add(buttonSelectFileName);


        buttonSubmit = new JButton("Submit");
        buttonSubmit.addActionListener(new ListFilesActionLitener());
        setCursorStyle(buttonSubmit);
        buttonSubmit.setPreferredSize(new Dimension(80, 29));
        fileCreatedNamePanel.add(buttonSubmit);

        labelMessage = new JLabel();
        labelMessage.setHorizontalAlignment(JLabel.CENTER);
        labelMessagePanel.add(labelMessage);

        buttonBrowseFile = new JButton("<HTML>Action completed: You can find created file <FONT color=\"#000099\"><U>here</U></FONT></HTML>");
        buttonBrowseFile.setHorizontalAlignment(SwingConstants.CENTER);
        buttonBrowseFile.setBorderPainted(false);
        buttonBrowseFile.setOpaque(false);
        buttonBrowseFile.setBackground(Color.WHITE);
        buttonBrowseFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if(OS.contains("win")) {
                        Runtime.getRuntime().exec("explorer.exe /select," + getFileResult().toString());
                    } else if(OS.contains("linux") || OS.contains("fedora")){
                        Runtime.getRuntime().exec("nautilus " + getFileResult().toString());
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        setCursorStyle(buttonBrowseFile);
        buttonBrowseFile.setVisible(false);
        labelMessagePanel.add(buttonBrowseFile);
    }

    public void listFiles(String path, ArrayList<String> arrayList) throws IOException {
        scannedDirectory = new File(path);
        File[] files = scannedDirectory.listFiles();
        BufferedWriter writer = new BufferedWriter(new FileWriter(getFileResult().toString(), true));
        int arraySize = arrayList.size();
        for (File file : files){
            if(file.isFile()) {
                for(int i=0; i<arraySize; i++){
                    if(file.toString().toLowerCase().endsWith(arrayList.get(i))){
                        writer.write(file.getPath()+"\n");
                    }
                }
            } else if(file.isDirectory()){
                listFiles(file.getAbsolutePath(), arrayList);
            }
        }
        writer.close();
    }

    public void setCursorStyle(JButton jbutton){
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        jbutton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent me) {
                setCursor(handCursor);
            }
            public void mouseExited(MouseEvent me) {
                setCursor(defaultCursor);
            }
        });
    }

    public static class HandlerClass implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if(checkBoxPng.isSelected() && checkBoxJpg.isSelected() && checkBoxTif.isSelected()){
                refreshResults(extentions);
                extentions.add(".png");
                extentions.add(".jpg");
                extentions.add(".tif");
                getResults(extentions);
            } else if(checkBoxPng.isSelected() && checkBoxJpg.isSelected()){
                refreshResults(extentions);
                extentions.add(".png");
                extentions.add(".jpg");
                getResults(extentions);
            } else if(checkBoxPng.isSelected() && checkBoxTif.isSelected()){
                refreshResults(extentions);
                extentions.add(".png");
                extentions.add(".tif");
                getResults(extentions);
            } else if(checkBoxTif.isSelected() && checkBoxJpg.isSelected()){
                refreshResults(extentions);
                extentions.add(".tif");
                extentions.add(".jpg");
                getResults(extentions);
            } else if(checkBoxTif.isSelected()){
                refreshResults(extentions);
                extentions.add(".tif");
                getResults(extentions);
            } else if(checkBoxPng.isSelected()){
                refreshResults(extentions);
                extentions.add(".png");
                getResults(extentions);
            } else if(checkBoxJpg.isSelected()){
                refreshResults(extentions);
                extentions.add(".jpg");
                getResults(extentions);
            } else if(itemEvent.DESELECTED == 2){
                refreshResults(extentions);
                getResults(extentions);
            }
        }

        public static ArrayList<String> getResults(ArrayList<String> arrayList){
            return arrayList;
        }
        public static void refreshResults(ArrayList<String> arrayList){
            arrayList.clear();
        }
    }

    public class ListFilesActionLitener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                enteredParentDirectoryPath = fieldParentFolder.getText();
                scannedDirectory = new File(enteredParentDirectoryPath);

                if(HandlerClass.getResults(extentions).isEmpty()){
                    buttonBrowseFile.setVisible(false);
                    labelMessage.setText("Please select at least one checkbox");
                    return;
                } else if(enteredParentDirectoryPath.isEmpty()){
                    buttonBrowseFile.setVisible(false);
                    labelMessage.setText("Parent directory path cannot be empty!");
                    return;
                } else if(!scannedDirectory.exists()){
                    buttonBrowseFile.setVisible(false);
                    labelMessage.setText("Please check entered path");
                    return;
                } else {
                    buttonBrowseFile.setVisible(false);
                    labelMessage.setText("");
                }

                if(getFileResult().exists()){
                    getFileResult().delete();
                }

                listFiles(enteredParentDirectoryPath, HandlerClass.getResults(extentions));

                if(isFileEmpty()){
                    buttonBrowseFile.setVisible(false);
                    labelMessage.setText("Action completed: Result's file doesn't contain any data");
                } else {
                    buttonBrowseFile.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getFileResult(){
        String resultsFileName = getNameOfResultsFile();
        String pathResultResults = "";
        if(resultsFileName.isEmpty()){
            pathResultResults = fileCreatedNameField.getText();
        } else {
            File jarFile = new File(PathCollector.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            pathResultResults = jarFile.getParentFile().getPath() + resultsFileName;
        }
        if(!pathResultResults.endsWith(".txt")){
            pathResultResults = pathResultResults + ".txt";
        }
        File fileResults = new File(pathResultResults);
        return fileResults;
    }

    public static String getNameOfResultsFile(){
        String resultsFileName = fileCreatedNameField.getText();
        String resultFile = "";
        if(resultsFileName.contains("/") || resultsFileName.contains("\\")){
            return resultFile;
        }

        if(OS.contains("win")) {
            resultFile = "\\" + resultsFileName;
        }else if(OS.contains("linux") || OS.contains("fedora")){
            resultFile = "/" + resultsFileName;
        }
        return resultFile;
    }

    public boolean isFileEmpty(){
        boolean flag = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(getFileResult().toString()));
            String currentReadingLine = reader.readLine();
            if(currentReadingLine == null){
                flag = true;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
