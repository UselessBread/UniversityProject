package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by Игорь on 03.08.2017.
 */
//Перенести запросы в DB
public class GUI extends JPanel implements ActionListener{
    private static final String NEW_ALGORITHM="Create new algorithm";
    private static JFrame mainFrame;
    private JFrame listFrame;
    private DB DBConnection;
    private JList<String> resultList;
    private  GUI(){
        DBConnection=new DB();
        int state=DBConnection.state;
        if(state!=DB.OK){
            if(state==DB.CLASS_NOT_FOUND){
                JOptionPane.showMessageDialog(this,"ClassNotFound exception","ClassNotFound",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(state==DB.SQL_EXCEPTION){
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

        JPanel main=new JPanel();
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
        main.add(labelPanel);
        main.add(buttonPanel);
        add(main);

    }

    @Override
    public void actionPerformed(ActionEvent e){

        //If the "создать алгоритм" button pressed
        if (e.getActionCommand().equals(NEW_ALGORITHM)) {
            JPanel listPanel = new JPanel();
            listPanel.setOpaque(true);

            Vector<String> resultTest=DBConnection.firstQuery();
            verifyResult(resultTest);
            resultList = new JList<>(resultTest);
            resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resultList.setLayoutOrientation(JList.VERTICAL_WRAP);
            resultList.setVisibleRowCount(-1);
            listPanel.add(resultList);

            JButton nextButton=new JButton("Далее");
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String resultValue=resultList.getSelectedValue().toLowerCase();
                    if(resultValue.contains("подсистема")) {
                        mainFrame.setEnabled(false);//disable main frame
                        int number = resultList.getSelectedIndex() + 1;//Номер подсистемы (PK)
                        String selectedItem = Integer.toString(number);
                        Vector<String> stringVector = DBConnection.queryToPodsys(selectedItem);
                        verifyResult(stringVector);

                        resultList.removeAll();
                        resultList.setListData(stringVector);
                    }
                    //else and other queries
                    else if (resultValue.contains("прибор")||resultValue.contains("датчик")){
                        String prevQueryResult=resultList.getSelectedValue();
                        Vector<String> stringVector=DBConnection.queryToDeviceOrSensor(prevQueryResult);
                        verifyResult(stringVector);

                        resultList.removeAll();
                        resultList.setListData(stringVector);
                    }
                }
            });
            listPanel.add(nextButton);

            listFrame = new JFrame("Results");
            listFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            listFrame.setContentPane(listPanel);
            listFrame.setPreferredSize(new Dimension(300,150));
            listFrame.pack();
            listFrame.setVisible(true);
        }
    }

    private void verifyResult(Vector<String> stringVector){
        try {
            if (stringVector.size() != 1) {
                return;
            }
            if (stringVector.size() == 1) {
                String firstResult = stringVector.get(0);
                int testingValue = Integer.parseInt(firstResult);
                if (testingValue == DB.CLASS_NOT_FOUND) {
                    JOptionPane.showMessageDialog(this, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.SQL_EXCEPTION) {
                    JOptionPane.showMessageDialog(this, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.CLASS_CAST_EXCEPTION) {
                    JOptionPane.showMessageDialog(this, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }catch (NumberFormatException exc){
            return;
        }

    }
    static void createAndShowGUI(){
        mainFrame=new JFrame("УА РВ");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUI contentPane=new GUI();
        contentPane.setOpaque(true);
        mainFrame.setContentPane(contentPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
