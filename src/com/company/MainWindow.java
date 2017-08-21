package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

import java.util.Vector;

public class MainWindow extends JPanel implements ActionListener {
    static Vector<SystemInfo> systemInfoVector=new Vector<>();
    static Vector<Vector<SystemInfo>> systemInfoVectorVector=new Vector<>();
    private final String NEW_ALGORITHM = "Create new algorithm";
    private final String OPEN = "Open algorithm";
    private DB DBC=new DB();
    static JPanel mainPanel = new JPanel();
    static JFrame mainFrame;

    public MainWindow() {
        JButton addButton = new JButton("Новый алгоритм");
        addButton.setActionCommand(NEW_ALGORITHM);
        addButton.addActionListener(this);
        JButton openButton = new JButton("Открыть существующий алгоритм");
        openButton.setActionCommand(OPEN);
        openButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(openButton);
        mainPanel.add(buttonPanel);

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
                    RunWindow runWindow=new RunWindow();
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

    }

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
