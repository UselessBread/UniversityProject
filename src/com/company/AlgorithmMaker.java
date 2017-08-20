package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import static com.company.MainWindow.mainFrame;

/**
 * Created by Игорь on 03.08.2017.
 */
//Перенести запросы в DB
public class AlgorithmMaker extends JPanel implements ActionListener{
    static final String NEW_ALGORITHM="Create new algorithm";
    //static final String AFTER_ALGORUTHM_STARTS="After beginning";//in queueChooser
    private JButton runButton;
    static JTextArea log=new JTextArea(30,80);
    static JFrame algorithmMakerFrame;
    private JPanel main,buttonPanel;
    private DB DBConnection;
    static String chosenDeviceAndMode="";
    static HashMap<HashMap<String,String>,String> deviceAndDelayAndRelativeDevice=new HashMap<>();
    private AlgorithmMaker(){
        DBConnection=new DB();
        int state=DBConnection.state;
        if(state!= DB.OK){
            if(state== DB.CLASS_NOT_FOUND){
                JOptionPane.showMessageDialog(this,"ClassNotFound exception","ClassNotFound",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(state== DB.SQL_EXCEPTION){
                JOptionPane.showMessageDialog(this,"SQLException","SQLException",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        JLabel labelPanel=new JLabel("УА РВ");
        labelPanel.setVerticalAlignment(JLabel.CENTER);
        labelPanel.setHorizontalAlignment(JLabel.CENTER);
        labelPanel.setVerticalTextPosition(JLabel.CENTER);
        labelPanel.setHorizontalTextPosition(JLabel.CENTER);

        JButton listShowButton=new JButton("Новый алгоритм");
        listShowButton.setActionCommand(NEW_ALGORITHM);
        listShowButton.addActionListener(this);
        buttonPanel=new JPanel();
        buttonPanel.add(listShowButton);

        JScrollPane logPane=new JScrollPane(log);
        log.setEditable(false);

        createRunButton();

        main=new JPanel();
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
        main.add(labelPanel);
        main.add(buttonPanel);
        main.add(logPane);
        add(main);

    }

    @Override
    public void actionPerformed(ActionEvent e){
        NewAlgorithmWindow algorithmWindow=new NewAlgorithmWindow(e,DBConnection);
    }


    private void createRunButton(){
        runButton=new JButton ("Запустить");
        buttonPanel.add(runButton);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RunWindow rw=new RunWindow();
            }
        });

    }
    static void createWindow(){
        mainFrame.setVisible(false);
        mainFrame.setEnabled(false);
        algorithmMakerFrame =new JFrame("УА РВ");
        algorithmMakerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        AlgorithmMaker contentPane=new AlgorithmMaker();
        contentPane.setOpaque(true);
        algorithmMakerFrame.setContentPane(contentPane);
        //algorithmMakerFrame.setLocation(750,350);
        algorithmMakerFrame.setLocationRelativeTo(MainWindow.mainFrame);
        algorithmMakerFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
                mainFrame.setEnabled(true);
                log.setText("");
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        algorithmMakerFrame.pack();
        algorithmMakerFrame.setVisible(true);
    }


}
