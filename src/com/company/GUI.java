package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Created by Игорь on 03.08.2017.
 */
//Перенести запросы в DB
public class GUI extends JPanel implements ActionListener{
    static final String NEW_ALGORITHM="Create new algorithm";
    static JTextArea log=new JTextArea(30,80);
    static JFrame mainFrame;
    private DB DBConnection;
    static String chosenDeviceAndMode="";
    static HashMap<HashMap<String,String>,String> deviceAndDelayAndRelativeDevice=new HashMap<>();
    private  GUI(){
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
        JPanel buttonPanel=new JPanel();
        buttonPanel.add(listShowButton);

        JScrollPane logPane=new JScrollPane(log);
        log.setEditable(false);

        JPanel main=new JPanel();
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
    static JTextArea getLog(){
        return log;
    }

    static void createAndShowGUI(){
        mainFrame=new JFrame("УА РВ");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GUI contentPane=new GUI();
        contentPane.setOpaque(true);
        mainFrame.setContentPane(contentPane);
        mainFrame.setLocation(750,350);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
