package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Vector;

public class MainWindow extends JPanel implements ActionListener {
    private final String NEW_ALGORITHM = "Create new algorithm";
    private final String OPEN = "Open algorithm";
    private final String ADD="Add to db";
    private static Vector<SystemInfo> systemInfoVector=new Vector<>();
    private static Vector<SystemInfo> systemInfoVectorForSaving=new Vector<>();
    static Vector<Vector<SystemInfo>> systemInfoVectorVector=new Vector<>();
    private DB DBC=new DB();
    private static JPanel mainPanel = new JPanel();
    static JFrame mainFrame;
    private JTextArea openedAlgorithms=new JTextArea(5,30);
    static JTextArea resourceMonitor=new JTextArea(1,30);
    ArrayList<Double> usedResources=new ArrayList<>();
    static ArrayList<Double> allResources;

    public MainWindow() {
        ArrayList<Double> resources=DBC.getAllResources();
        resourceMonitor.append("0/"+resources.get(0)+" 0/"+resources.get(1)+" 0/"+resources.get(2));
        resourceMonitor.setEditable(false);
        openedAlgorithms.setEditable(false);

        JButton addButton = new JButton("Новый алгоритм");
        addButton.setActionCommand(NEW_ALGORITHM);
        addButton.addActionListener(this);
        JButton openButton = new JButton("Открыть существующий алгоритм");
        openButton.setActionCommand(OPEN);
        openButton.addActionListener(this);
        JButton addToDBButton=new JButton("Добавить");
        addToDBButton.setActionCommand(ADD);
        addToDBButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(openButton);
        buttonPanel.add(addToDBButton);
        JSplitPane splitPane=new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buttonPanel);
        JScrollPane openedScrollPane=new JScrollPane(openedAlgorithms);
        splitPane.setBottomComponent(openedScrollPane);
        JSplitPane withMonitor=new JSplitPane(JSplitPane.VERTICAL_SPLIT,splitPane,resourceMonitor);
        mainPanel.add(withMonitor);

        add(mainPanel);
    }

    static void createAndShowGUI() {
        mainFrame = new JFrame("Главное окно");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainWindow contentPane = new MainWindow();
        contentPane.setOpaque(true);
        mainFrame.setContentPane(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

    @Override
    //Сдежать отслеживание русурсов по изменнению режимов во времени
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(NEW_ALGORITHM)) {
            AlgorithmMaker.createWindow();
        }
        if (e.getActionCommand().equals(OPEN)) {
            JList<String> usedNames=new JList<>(getUsedNames());

            JButton confirmButton=new JButton("Выбрать");
            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //Добавляет напрямую в systemInfoVector
                    DBC.openQuery(usedNames.getSelectedValue());
                    allResources=DBC.getAllResources();
                    resourceMonitor.setText(0+"/"+allResources.get(0)+"\t"+
                            0+"/"+allResources.get(1)+"\t"+
                            0+"/"+allResources.get(2));
                    openedAlgorithms.append(usedNames.getSelectedValue()+"\n");
                    (new ThreadRunWindow()).start();
                }
            });

            JPanel mainOpenPanel = new JPanel();
            mainOpenPanel.add(usedNames);
            mainOpenPanel.add(confirmButton);
            mainOpenPanel.setOpaque(true);
            JFrame openFrame = new JFrame("Открыть");
            openFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            openFrame.setLocationRelativeTo(mainOpenPanel);
            openFrame.setContentPane(mainOpenPanel);
            openFrame.pack();
            openFrame.setVisible(true);

        }
        if(e.getActionCommand().equals(ADD)){
            (new ThreadDBUpdater()).start();
        }

    }
    static Vector<SystemInfo> getSystemInfoVector(){
        return systemInfoVector;
    }
    static Vector<SystemInfo> getSystemInfoVectorForSaving(){return systemInfoVectorForSaving;}
    private Vector<String> getUsedNames() {
        String line;
        Vector<String> usedNames=new Vector<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(AlgorithmMaker.used_namesPath, AlgorithmMaker.charset)) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine=line.split("/");
                for(String str:splitLine){
                    usedNames.add(str);
                }
            }
        }catch(IOException e){}
        return usedNames;
    }
}
