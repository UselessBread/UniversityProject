package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Vector;

import static com.company.AlgorithmMaker.*;
import static com.company.MainWindow.*;



 class NewAlgorithmWindow implements WindowListener{
     //private HashMap<HashMap<String,String>,String> deviceAndDelayAndRelativeDevice=new HashMap<>();
     private JFrame listFrame;
    private JPanel resultButtonPanel=new JPanel();
    private JList<String> resultList;
    private ActionEvent e;
    private DB DBConnection;
    private Vector<String> resultTest;
    private Vector<Vector<String>> historyVector=new Vector<>();
     NewAlgorithmWindow(ActionEvent e, DB db){
         this.e=e;
         DBConnection=db;
         algorithmMakerFrame.setVisible(false);//disable main frame
         algorithmMakerFrame.setEnabled(true);
         placeClassNameHere();
    }
    private void placeClassNameHere() {
        JPanel nextButtonPanel = new JPanel();
        JPanel backButtonPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel delayPanel=new JPanel();
        Vector<String> forLogging=new Vector<>();

        //If the "создать алгоритм" button pressed
        if (e.getActionCommand().equals(AlgorithmMaker.NEW_ALGORITHM)) {
            JPanel listPanel = new JPanel();
            listPanel.setOpaque(true);

            resultTest = DBConnection.firstQuery();
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
                        delayFormatChooser.setSelectedIndex(2);
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
                                    boolean test=delayActivator.isSelected();
                                    //Make someone to enter value in delayField
                                    if (((delayField.getText().equals(""))&&!(delayActivator.isSelected()))||!(delayField.getText().equals(""))&&(delayActivator.isSelected())) {
                                    boolean ifDelayChosen = false;
                                    String chosenDeviceMode = e.getActionCommand();
                                    for (String str : forLogging) {
                                        chosenDeviceAndMode += str + " ";
                                    }
                                    chosenDeviceAndMode += chosenDeviceMode;

                                        String delay;
                                        if (delayActivator.isSelected()) {
                                            delay = delayField.getText() + "/" + delayFormatChooser.getSelectedItem(); //Убрал +" " в конце после delayFormatChooser...
                                            ifDelayChosen = true;
                                        } else {
                                            delay = delayFieldResult;
                                            ifDelayChosen = false;
                                        }
                                        HashMap<String, String> deviceAndDelay = new HashMap<>();
                                        deviceAndDelay.putIfAbsent(chosenDeviceAndMode, delay);
                                        /*String queueChooserResult=(String)queueChooser.getSelectedItem();
                                        if(queueChooserResult.equals(""))
                                            queueChooserResult=AlgorithmMaker.AFTER_ALGORUTHM_STARTS;*/
                                        deviceAndDelayAndRelativeDevice.putIfAbsent(deviceAndDelay, (String) queueChooser.getSelectedItem());
                                        //MainWindow.deviceAndDelayAndRelativeDeviceOpenedVector.add(deviceAndDelayAndRelativeDevice);
                                        String queueSelectedItem = (String) queueChooser.getSelectedItem();
                                        AlgorithmMaker.log.append(chosenDeviceAndMode + " " + delay);
                                        if (ifDelayChosen) {
                                            if (queueSelectedItem.equals(" ")) {
                                                AlgorithmMaker.log.append("После стара алгоритма\n");
                                            } else {
                                                AlgorithmMaker.log.append("После запуска " + queueSelectedItem + "\n");
                                            }
                                        }
                                        else
                                            AlgorithmMaker.log.append("\n");
                                        chosenDeviceAndMode = "";
                                        listPanel.remove(resultButtonPanel);
                                        listFrame.dispose();
                                        algorithmMakerFrame.setVisible(true);
                                        algorithmMakerFrame.setEnabled(true);

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
                    Vector<String> firstHistoryElement=historyVector.firstElement();
                    if(!firstHistoryElement.elementAt(0).contains("Подсистема"))
                        historyVector.add(0,resultTest);
                    if (historyVector.size() > 1) {
                        resultButtonPanel.removeAll();
                        listPanel.remove(resultButtonPanel);
                        listPanel.remove(delayPanel);
                        nextButton.setVisible(true);
                        resultList.setVisible(true);
                        resultList.removeAll();
                        int vecSize=historyVector.size();
                        resultList.setListData(historyVector.elementAt(vecSize-2));
                        listPanel.add(resultList, BoxLayout.Y_AXIS);
                        nextButtonPanel.add(nextButton);
                        historyVector.remove(historyVector.elementAt(vecSize-2));
                        historyVector.remove(historyVector.lastElement());
                        listFrame.repaint();

                    }
                }
            });
            backButtonPanel.add(backButton);
            buttonPanel.add(backButtonPanel);

            listPanel.add(buttonPanel);

            listFrame = new JFrame("Results");
            listFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            listFrame.setContentPane(listPanel);
            listFrame.setPreferredSize(new Dimension(500, 300));
            listFrame.setLocationRelativeTo(algorithmMakerFrame);
            listFrame.addWindowListener(this);
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

     @Override
     public void windowOpened(WindowEvent e) {

     }

     @Override
     public void windowClosing(WindowEvent e) {
        algorithmMakerFrame.setEnabled(true);
        algorithmMakerFrame.setVisible(true);
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
 }
