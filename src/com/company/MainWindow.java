package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

public class MainWindow extends JPanel implements ActionListener{
    //static HashMap<HashMap<String,String>,String> deviceAndDelayAndRelativeDevice=new HashMap<>();
    static Vector<HashMap<HashMap<String,String>,String>> deviceAndDelayAndRelativeDeviceOpenedVector=new Vector<>();
    private final String NEW_ALGORITHM="Create new algorithm";
    private final String OPEN="Open algorithm";

    static JPanel mainPanel=new JPanel();
    static JFrame mainFrame;
    public MainWindow(){
        JButton addButton=new JButton("Новый алгоритм");
        addButton.setActionCommand(NEW_ALGORITHM);
        addButton.addActionListener(this);
        JButton openButton=new JButton("Открыть существующий алгоритм");
        openButton.setActionCommand(OPEN);
        openButton.addActionListener(this);

        JPanel buttonPanel=new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(openButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }
    static void createAndShowGUI() {
        mainFrame=new JFrame("Главное окно");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainWindow contentPane=new MainWindow();
        contentPane.setOpaque(true);
        mainFrame.setContentPane(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

        @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(NEW_ALGORITHM)){
            AlgorithmMaker.createWindow();
        }

    }
}
