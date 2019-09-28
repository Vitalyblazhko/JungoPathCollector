import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PathCollector extends JFrame {

    private JTextField fieldParentFolder;
    private JLabel labelMessage;
    private JButton buttonSubmit;
    private static JCheckBox checkBoxPng;
    private static JCheckBox checkBoxTif;
    private static JCheckBox checkBoxJpg;
    private static String enteredParentDirectoryPath;
    private static File folder;

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
        JPanel panel = null;

        panel = new JPanel();
        getContentPane().add(panel);
        JLabel labelCheckBox = new JLabel("Please Select Required Images Extensions");
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

        JLabel labelDirectory = new JLabel("Please Enter Your Parent Directory");
        labelDirectory.setHorizontalAlignment(JLabel.CENTER);
        labelDirectory.setPreferredSize(new Dimension(450, 30));
        panel.add(labelDirectory);

        fieldParentFolder = new JTextField();
        fieldParentFolder.setPreferredSize(new Dimension(300, 30));
        panel.add(fieldParentFolder);

        buttonSubmit = new JButton("Submit");
        buttonSubmit.addActionListener(new ListFilesActionLitener());
        panel.add(buttonSubmit);

        labelMessage = new JLabel();
        panel.add(labelMessage);

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
                    labelMessage.setText("Please Select At Least One Checkbox");
                    return;
                } else if(enteredParentDirectoryPath.isEmpty()){
                    labelMessage.setText("Parent Directory Path Canoot Be Empty!");
                    return;
                } else if(!folder.exists()){
                    labelMessage.setText("Please Check Entered Path");
                    return;
                } else {labelMessage.setText("");}

                if(fileResults.exists()){
                    fileResults.delete();
                }
                listFiles(enteredParentDirectoryPath, HandlerClass.getResults(extentions));
                labelMessage.setText("Action completed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String detectPathOfResultsFile(){
        String os = System.getProperty("os.name").toLowerCase();
        String resultFile = "";

        if(os.contains("win")) {
            resultFile = "\\jungo_path_collector.txt";
        }else if(os.contains("linux") || os.contains("fedora")){
            resultFile = "/jungo_path_collector.txt";
        }
        return resultFile;
    }
}
