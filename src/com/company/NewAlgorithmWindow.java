package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;

import static com.company.GUI.*;


 class NewAlgorithmWindow {
    private JFrame listFrame;
    private JPanel resultButtonPanel=new JPanel();
    private JList<String> resultList;
    private ActionEvent e;
    private DB DBConnection;
    private Vector<Vector<String>> historyVector=new Vector<>();
     NewAlgorithmWindow(ActionEvent e, DB db){
        this.e=e;
        DBConnection=db;
        placeClassNameHere();
    }
    private void placeClassNameHere() {
        JPanel nextButtonPanel = new JPanel();
        JPanel backButtonPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel delayPanel=new JPanel();
        Vector<String> forLogging=new Vector<>();

        //If the "создать алгоритм" button pressed
        if (e.getActionCommand().equals(GUI.NEW_ALGORITHM)) {
            JPanel listPanel = new JPanel();
            listPanel.setOpaque(true);

            Vector<String> resultTest = DBConnection.firstQuery();
            verifyResult(resultTest);
            historyVector.add(resultTest);
            resultList = new JList<>(resultTest);
            resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resultList.setLayoutOrientation(JList.VERTICAL_WRAP);
            resultList.setVisibleRowCount(-1);
            listPanel.add(resultList);

            JButton nextButton = new JButton("Далее");
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String resultValue = resultList.getSelectedValue().toLowerCase();
                    if (resultValue.contains("подсистема")) {
                        mainFrame.setEnabled(false);//disable main frame
                        int number = resultList.getSelectedIndex() + 1;//Номер подсистемы (PK)
                        forLogging.add("Подсистема"+number);
                        String selectedItem = Integer.toString(number);
                        Vector<String> stringVector = DBConnection.queryToPodsys(selectedItem);
                        verifyResult(stringVector);
                        historyVector.add(stringVector);


                        resultList.removeAll();
                        resultList.setListData(stringVector);
                    }
                    //else and other queries
                    else if (resultValue.contains("прибор") || resultValue.contains("датчик")) {
                        String prevQueryResult = resultList.getSelectedValue();
                        Vector<Vector<String>> stringVectors = DBConnection.queryToDeviceOrSensor(prevQueryResult);
                        Vector<String> listVector = new Vector<>(stringVectors.get(0));
                        Vector<String> buttonVector = new Vector<>(stringVectors.get(1));
                        verifyResult(buttonVector);
                        forLogging.add(resultValue);
                        resultList.removeAll();
                        resultList.setVisible(false);
                        listPanel.remove(resultList);
                        nextButtonPanel.remove(nextButton);
                        //area with delay chooser and queue chooser
                        JTextField delayField=new JTextField(2);
                        delayField.setEditable(true);
                        String logContent=log.getText();
                        String[] formats={"Часы","Минуты","Секунды","Миллисекунды"};
                        JComboBox<String> delayFormatChooser=new JComboBox<>(formats);
                            String[] logContentSplit = logContent.split("\n");
                            Vector<String> deviceToQueueChooser = new Vector<>();
                        deviceToQueueChooser.add(" ");
                        if(logContent.length()>1) {
                            for (String str : logContentSplit) {
                                String[] tempStr = str.split(" ");
                                deviceToQueueChooser.add(tempStr[1]);
                            }
                        }
                        JComboBox<String> queueChooser = new JComboBox<>(deviceToQueueChooser);
                        queueChooser.setSelectedIndex(0);
                        //Set delay activator
                        JCheckBox delayActivator=new JCheckBox("Включить задержку");
                        delayActivator.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if(delayActivator.isSelected()){
                                    delayFormatChooser.setEnabled(true);
                                    queueChooser.setEnabled(true);
                                    delayField.setEnabled(true);
                                }
                                else{
                                    delayFormatChooser.setEnabled(false);
                                    queueChooser.setEnabled(false);
                                    delayField.setEnabled(false);
                                }
                            }
                        });
                        delayFormatChooser.setEnabled(false);
                        queueChooser.setEnabled(false);
                        delayField.setEnabled(false);
                        delayPanel.add(delayActivator);
                        delayPanel.add(delayField);
                        delayPanel.add(delayFormatChooser);
                        delayPanel.add(queueChooser);
                        listPanel.add(delayPanel);


                        //resultList.setListData(listVector);
                        for (String str : buttonVector) {
                            JButton tempButton = new JButton(str);
                            tempButton.setActionCommand(str);
                            tempButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String delayFieldResult = delayField.getText();
                                    //Make someone to enter value in delayField
                                    if (!delayFieldResult.equals("")) {
                                    boolean ifDelayChosen = false;
                                    String chosenDeviceMode = e.getActionCommand();
                                    for (String str : forLogging) {
                                        chosenDeviceAndMode += str + " ";
                                    }
                                    chosenDeviceAndMode += chosenDeviceMode;

                                        String delay;
                                        if (delayActivator.isSelected()) {
                                            delay = delayField.getText() + "/" + delayFormatChooser.getSelectedItem() + " ";
                                            ifDelayChosen = true;
                                        } else {
                                            delay = delayFieldResult;
                                            ifDelayChosen = false;
                                        }
                                        HashMap<String, String> deviceAndDelay = new HashMap<>();
                                        deviceAndDelay.putIfAbsent(chosenDeviceAndMode, delay);
                                        deviceAndDelayAndRelativeDevice.putIfAbsent(deviceAndDelay, (String) queueChooser.getSelectedItem());
                                        String queueSelectedItem = (String) queueChooser.getSelectedItem();
                                        GUI.log.append(chosenDeviceAndMode + " " + delay);
                                        if (ifDelayChosen) {
                                            if (queueSelectedItem.equals(" ")) {
                                                GUI.log.append("После стара алгоритма\n");
                                            } else {
                                                GUI.log.append("После запуска " + queueSelectedItem + "\n");
                                            }
                                        }
                                        chosenDeviceAndMode = "";
                                        listPanel.remove(resultButtonPanel);
                                        listFrame.dispose();
                                        mainFrame.setVisible(true);
                                        mainFrame.setEnabled(true);

                                    }
                                }
                            });
                            resultButtonPanel.add(tempButton);

                        }
                        listPanel.add(resultButtonPanel);

                    }
                }
            });
            nextButtonPanel.add(nextButton);
            buttonPanel.add(nextButtonPanel);

            JButton backButton = new JButton("Назад");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (historyVector.size() > 0) {
                        resultButtonPanel.removeAll();
                        listPanel.remove(resultButtonPanel);
                        nextButton.setVisible(true);
                        resultList.setVisible(true);
                        resultList.removeAll();
                        resultList.setListData(historyVector.lastElement());
                        listPanel.add(resultList, BoxLayout.Y_AXIS);
                        nextButtonPanel.add(nextButton);
                        historyVector.remove(historyVector.lastElement());

                    }
                }
            });
            backButtonPanel.add(backButton);
            buttonPanel.add(backButtonPanel);

            listPanel.add(buttonPanel);

            listFrame = new JFrame("Results");
            listFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            listFrame.setContentPane(listPanel);
            listFrame.setPreferredSize(new Dimension(400, 250));
            listFrame.setLocationRelativeTo(mainFrame);
            listFrame.pack();
            listFrame.setVisible(true);
        }
    }
    void verifyResult(Vector<String> stringVector){
        try {
            if (stringVector.size() != 1) {
                return;
            }
            if (stringVector.size() == 1) {
                String firstResult = stringVector.get(0);
                int testingValue = Integer.parseInt(firstResult);
                if (testingValue == DB.CLASS_NOT_FOUND) {
                    JOptionPane.showMessageDialog(listFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.SQL_EXCEPTION) {
                    JOptionPane.showMessageDialog(listFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.CLASS_CAST_EXCEPTION) {
                    JOptionPane.showMessageDialog(listFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }catch (NumberFormatException exc){
            return;
        }

    }
}
