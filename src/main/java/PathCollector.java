import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;

public class PathCollector extends JFrame {

    private JTextField fieldParentFolder;
    private JLabel labelMessage;
    private JButton buttonSelectPath;
    private JButton buttonSubmit;
    private JButton goToFile;
    private static JCheckBox checkBoxPng;
    private static JCheckBox checkBoxTif;
    private static JCheckBox checkBoxJpg;
    private static String enteredParentDirectoryPath;
    private static File folder;
    private static JTextArea ta;
    private static JTextPane textPane;
    private static StyledDocument doc;
    private static SimpleAttributeSet center;
    private static JPanel panel;
    private static final String os = System.getProperty("os.name").toLowerCase();

    File jarFile = new File(PathCollector.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    private final String FILE_RESULT = jarFile.getParentFile().getPath() + detectPathOfResultsFile();

    private File fileResults = new File(FILE_RESULT);
    static ArrayList<String> extentions = new ArrayList<String>();

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
        //Make window exit application on close
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Set display size
        setPreferredSize(new Dimension(450, 220));
        //setPreferredSize(new Dimension(450, 220));
        pack();
        //Center the frame to middle of screen
        setLocationRelativeTo(null);
        //Disable resize
        setResizable(false);

    }

    private void createView() {
        setLayout(new GridLayout(0,1));


        panel = new JPanel();
        getContentPane().add(panel);
        JLabel labelCheckBox = new JLabel("Please select required images extentions");
        labelCheckBox.setHorizontalAlignment(JLabel.CENTER);
        labelCheckBox.setPreferredSize(new Dimension(450, 30));
        panel.add(labelCheckBox);

        checkBoxPng = new JCheckBox("PNG");
        checkBoxJpg = new JCheckBox("JPG");
        checkBoxTif = new JCheckBox("TIF");
        panel.add(checkBoxPng);
        panel.add(checkBoxJpg);
        panel.add(checkBoxTif);
        HandlerClass handler = new HandlerClass();
        checkBoxPng.addItemListener(handler);
        checkBoxJpg.addItemListener(handler);
        checkBoxTif.addItemListener(handler);

        JLabel labelDirectory = new JLabel("Please enter your parent directory");
        labelDirectory.setHorizontalAlignment(JLabel.CENTER);
        labelDirectory.setPreferredSize(new Dimension(450, 30));
        panel.add(labelDirectory);

        fieldParentFolder = new JTextField();
        fieldParentFolder.setPreferredSize(new Dimension(300, 30));
        panel.add(fieldParentFolder);

        buttonSelectPath = new JButton("...");
        buttonSelectPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser selectDirectory = new JFileChooser();
                selectDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int ret = selectDirectory.showDialog(null, "Select Path");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    enteredParentDirectoryPath = selectDirectory.getSelectedFile().getAbsolutePath();
                    fieldParentFolder.setText(enteredParentDirectoryPath);
                }
            }
        });
        panel.add(buttonSelectPath);

        buttonSubmit = new JButton("Submit");
        buttonSubmit.addActionListener(new ListFilesActionLitener());
        panel.add(buttonSubmit);

        labelMessage = new JLabel();
        panel.add(labelMessage);

        goToFile = new JButton("<HTML>Action completed: You can find created file <FONT color=\"#000099\"><U>here</U></FONT></HTML>");
        goToFile.setHorizontalAlignment(SwingConstants.LEFT);
        goToFile.setBorderPainted(false);
        goToFile.setOpaque(false);
        goToFile.setBackground(Color.WHITE);
        goToFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if(os.contains("win")) {
                        Runtime.getRuntime().exec("explorer.exe /select," + FILE_RESULT);
                    } else if(os.contains("linux") || os.contains("fedora")){
                        Runtime.getRuntime().exec("nautilus" + FILE_RESULT);
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        goToFile.setVisible(false);
        panel.add(goToFile);

    }

    public void listFiles(String path, ArrayList<String> arrayList) throws IOException {
        folder = new File(path);
        File[] files = folder.listFiles();
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_RESULT, true));

        for (File file : files){
            if(file.isFile()) {
                for(int i=0; i<arrayList.size(); i++){
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
                folder = new File(enteredParentDirectoryPath);

                if(HandlerClass.getResults(extentions).isEmpty()){
                    goToFile.setVisible(false);
                    labelMessage.setText("Please select at least one checkbox");
                    return;
                } else if(enteredParentDirectoryPath.isEmpty()){
                    goToFile.setVisible(false);
                    labelMessage.setText("Parent directory path cannot be empty!");
                    return;
                } else if(!folder.exists()){
                    goToFile.setVisible(false);
                    labelMessage.setText("Please check entered path");
                    return;
                } else {
                    goToFile.setVisible(false);
                    labelMessage.setText("");
                }

                if(fileResults.exists()){
                    fileResults.delete();
                }
                listFiles(enteredParentDirectoryPath, HandlerClass.getResults(extentions));
                if(isFileEmpty()){
                    goToFile.setVisible(false);
                    labelMessage.setText("Action completed: Result's file doesn't contain any data");
                } else {
                    goToFile.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String detectPathOfResultsFile(){
        String resultFile = "";
        if(os.contains("win")) {
            resultFile = "\\jungo_path_collector.txt";
        }else if(os.contains("linux") || os.contains("fedora")){
            resultFile = "/jungo_path_collector.txt";
        }
        return resultFile;
    }

    public boolean isFileEmpty(){
        boolean flag = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FILE_RESULT));
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
