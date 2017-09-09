package com.company;


import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Vector;

public class ThreadDBUpdater extends Thread
implements ActionListener,WindowListener{
    private DB DBConnection=new DB();

    private final static String ADD_ARTICLE_COMMAND ="Добавить изделие";
    private final static String ADD_SUBSYSTEM_TO_EXISTING_COMMAND ="Добавить подсистему к существующему изделию";
    private final static String ADD_DEVICE_TO_EXISTING_COMMAND ="Добавить устройство к существующему режиму";
    private final static String ADD_MODE_TO_EXISTING_COMMAND ="Добавить режим к существующему устройству";
    private final static String DEVICE="Устройство";
    private final static String SENSOR="Датчик";
    private JFrame DBUpdaterFrame;
    private JPanel mainPanel,startPanel;
    //Для контроля за вводимыми значениями и получением данных из полей
    private Vector<TextFieldAndDouble> resourceTextFieldAndMaxVal=new Vector<>();
    private int state;
    private String selectedArticle,selectedSubsystem,selectedDevice;
    public void run(){
        setUI();

    }
    private void setUI(){
        mainPanel=new JPanel();
        startPanel=new JPanel();
        JRadioButton addArticle=new JRadioButton(ADD_ARTICLE_COMMAND);
        addArticle.setActionCommand(ADD_ARTICLE_COMMAND);
        addArticle.addActionListener(this);
        JRadioButton addSubsystemToExisting=new JRadioButton(ADD_SUBSYSTEM_TO_EXISTING_COMMAND);
        addSubsystemToExisting.setActionCommand(ADD_SUBSYSTEM_TO_EXISTING_COMMAND);
        addSubsystemToExisting.addActionListener(this);
        JRadioButton addDeviceToExisting=new JRadioButton(ADD_DEVICE_TO_EXISTING_COMMAND);
        addDeviceToExisting.setActionCommand(ADD_DEVICE_TO_EXISTING_COMMAND);
        addDeviceToExisting.addActionListener(this);
        JRadioButton addModeToExisting=new JRadioButton(ADD_MODE_TO_EXISTING_COMMAND);
        addModeToExisting.setActionCommand(ADD_MODE_TO_EXISTING_COMMAND);
        addModeToExisting.addActionListener(this);
        ButtonGroup radioButtonGroup=new ButtonGroup();
        radioButtonGroup.add(addArticle);
        radioButtonGroup.add(addSubsystemToExisting);
        radioButtonGroup.add(addDeviceToExisting);
        radioButtonGroup.add(addModeToExisting);
        startPanel.add(addArticle);
        startPanel.add(addSubsystemToExisting);
        startPanel.add(addDeviceToExisting);
        startPanel.add(addModeToExisting);
        mainPanel.add(startPanel);

        mainPanel.setOpaque(true);
        DBUpdaterFrame =new JFrame("Добавление");
        DBUpdaterFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        DBUpdaterFrame.setContentPane(mainPanel);
        DBUpdaterFrame.addWindowListener(this);
        DBUpdaterFrame.setLocationRelativeTo(null);
        DBUpdaterFrame.pack();
        DBUpdaterFrame.setVisible(true);

    }
    private void resetUI(){
        mainPanel.removeAll();
        mainPanel.add(startPanel);
        DBUpdaterFrame.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD_ARTICLE_COMMAND)){
            addArticle();

        }
        if(e.getActionCommand().equals(ADD_SUBSYSTEM_TO_EXISTING_COMMAND)){
            addSubsystem();
        }
        if(e.getActionCommand().equals(ADD_DEVICE_TO_EXISTING_COMMAND)){
            addDevice();
        }
        if(e.getActionCommand().equals(ADD_MODE_TO_EXISTING_COMMAND)){
            addMode();
        }
    }
    private void addArticle(){
        ArrayList<Integer> intList=new ArrayList<>(1);
        Vector<String> usedArticleNames=DBConnection.queryToArticles(intList);
        Integer lastIndex=intList.get(0);
        mainPanel.removeAll();
        JPanel enterPanel=new JPanel();
        JTextField newArticleName=new JTextField(20);
        JLabel nameLabel=new JLabel("Введите имя нового изделия");
        nameLabel.setLabelFor(newArticleName);
        JButton confirmButton=new JButton("Подтвердить");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chosenName=newArticleName.getText();
                boolean matching=testForMatching(usedArticleNames,chosenName);
                if(!matching&&!chosenName.isEmpty()){
                    int result=DBConnection.addArticle(chosenName,lastIndex);
                    verifyResult(result);
                    resetUI();
                }
            }
        });
        enterPanel.add(newArticleName);
        enterPanel.add(nameLabel);
        enterPanel.add(confirmButton);
        mainPanel.add(enterPanel);
        DBUpdaterFrame.repaint();
    }
    private void addSubsystem(){
        mainPanel.removeAll();
        JList<String> resultList=new JList<>();
        Vector<String> resultTest=DBConnection.queryToArticles();
        NewAlgorithmWindow.verifyResult(resultTest, DBUpdaterFrame);
        resultList.setListData(resultTest);
        JButton nextButton=new JButton("Продолжить");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Integer> intList=new ArrayList<>(1);
                String selectedArticle=resultList.getSelectedValue().toLowerCase();
                //Использованные имена подсистем
                Vector<String> result=DBConnection.queryToArticle(selectedArticle,intList);
                Integer lastIndex=intList.get(0);
                NewAlgorithmWindow.verifyResult(result,DBUpdaterFrame);
                mainPanel.removeAll();
                JPanel enterPanel=new JPanel();
                JTextField newSubsystemName=new JTextField(20);
                JButton submitButton=new JButton("Подтвердить");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String chosenName=newSubsystemName.getText();
                        boolean matching=testForMatching(result,chosenName);
                        if(!chosenName.isEmpty()&&!matching){
                            //Если все ок, то запрос
                            int result=DBConnection.addSubsystem(selectedArticle,chosenName,lastIndex);
                            verifyResult(result);
                            resetUI();
                        }
                        if(chosenName.isEmpty()){
                            JOptionPane.showMessageDialog(DBUpdaterFrame,"Введите имя","Введите имя",JOptionPane.ERROR_MESSAGE);
                        }
                        if(matching){
                            JOptionPane.showMessageDialog(DBUpdaterFrame,"Имя","Это имя уже занято",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                enterPanel.add(newSubsystemName);
                enterPanel.add(submitButton);
                mainPanel.add(enterPanel);
                DBUpdaterFrame.repaint();
            }
        });
        JPanel listPanel=new JPanel();
        listPanel.add(resultList);
        listPanel.add(nextButton);
        mainPanel.add(listPanel);
        DBUpdaterFrame.repaint();
    }
    private void addDevice(){
        final int QUERY_TO_ARTICLES=11;
        final int QUERY_TO_ARTICLE=12;
        mainPanel.removeAll();
        JList<String> resultList=new JList<>();
        Vector<String> resultTest=DBConnection.queryToArticles();
        NewAlgorithmWindow.verifyResult(resultTest, DBUpdaterFrame);
        resultList.setListData(resultTest);
        JButton nextButton=new JButton("Продолжить");
        state=QUERY_TO_ARTICLES;
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(state==QUERY_TO_ARTICLE){
                    //0-device,1-sensor
                    ArrayList<Integer> intList=new ArrayList<>();
                    selectedSubsystem=resultList.getSelectedValue().toLowerCase();
                    mainPanel.removeAll();
                    Vector<String> result=DBConnection.queryToSubsys(selectedArticle,selectedSubsystem,intList);
                    if(intList.size()==0){
                        intList.add(0);
                        intList.add(0);
                    }
                    NewAlgorithmWindow.verifyResult(result,DBUpdaterFrame);
                    JPanel enterPanel=new JPanel();
                    //Датчик или устройство
                    String[] checkBoxVariants={DEVICE,SENSOR};
                    JComboBox<String> deviceChooser=new JComboBox<>(checkBoxVariants);

                    JTextField newDeviceName=new JTextField(20);
                    JButton submitButton=new JButton("Подтвердить");
                    submitButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String chosenName=newDeviceName.getText();
                            boolean matching=testForMatching(result,chosenName);
                            if(!chosenName.isEmpty()&&!matching){
                                //Если все ок, то запрос
                                if(deviceChooser.getSelectedItem().equals(DEVICE)) {
                                    int result = DBConnection.addDevice(selectedArticle, selectedSubsystem, chosenName, intList.get(0));
                                    verifyResult(result);
                                    resetUI();
                                }
                                if(deviceChooser.getSelectedItem().equals(SENSOR)){
                                    int result = DBConnection.addSensor(selectedArticle, selectedSubsystem, chosenName, intList.get(1));
                                    verifyResult(result);
                                    resetUI();
                                }
                            }
                            if(chosenName.isEmpty()){
                                JOptionPane.showMessageDialog(DBUpdaterFrame,"Введите имя","Введите имя",JOptionPane.ERROR_MESSAGE);
                            }
                            if(matching){
                                JOptionPane.showMessageDialog(DBUpdaterFrame,"Имя","Это имя уже занято",JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    enterPanel.add(newDeviceName);
                    enterPanel.add(deviceChooser);
                    enterPanel.add(submitButton);
                    mainPanel.add(enterPanel);

                }
                if(state==QUERY_TO_ARTICLES) {
                    selectedArticle = resultList.getSelectedValue().toLowerCase();
                    Vector<String> result = DBConnection.queryToArticle(selectedArticle);
                    NewAlgorithmWindow.verifyResult(result, DBUpdaterFrame);
                    resultList.setListData(result);
                    state=QUERY_TO_ARTICLE;
                }
            }
        });
        JPanel listPanel=new JPanel();
        listPanel.add(resultList);
        listPanel.add(nextButton);
        mainPanel.add(listPanel);
        DBUpdaterFrame.repaint();
        mainPanel.repaint();
    }
    private void addMode(){
        final int QUERY_TO_ARTICLES=11;
        final int QUERY_TO_ARTICLE=12;
        final int QUERY_TO_SUBSYSTEM=13;
        mainPanel.removeAll();
        JList<String> resultList=new JList<>();
        Vector<String> resultTest=DBConnection.queryToArticles();
        NewAlgorithmWindow.verifyResult(resultTest, DBUpdaterFrame);
        resultList.setListData(resultTest);
        JButton nextButton=new JButton("Продолжить");
        state=QUERY_TO_ARTICLES;
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(state==QUERY_TO_SUBSYSTEM){
                    ArrayList<Integer> intList=new ArrayList<>();
                    selectedDevice=resultList.getSelectedValue().toLowerCase();
                    Vector<String> result=DBConnection.queryToDevice(selectedArticle,selectedSubsystem,selectedDevice,intList);
                    if(intList.size()==0){
                        result=DBConnection.queryToSensor(selectedArticle,selectedSubsystem,selectedDevice,intList);
                    }
                    NewAlgorithmWindow.verifyResult(result,DBUpdaterFrame);
                    Integer lastIndex=intList.get(0);
                    Vector<String> nameTest=new Vector<>();
                    for(String str:result){
                        String modeName=str.split("\t")[0];
                        nameTest.add(modeName);
                    }

                    JPanel enterPanel=new JPanel();

                    JTextField newModeName=new JTextField(20);
                    JLabel newModeNameLabel=new JLabel("Введите имя режима");
                    newModeNameLabel.setLabelFor(newModeName);
                    enterPanel.add(newModeName);
                    enterPanel.add(newModeNameLabel);
                    Vector<Vector<String>> resourceNamesAndValues=DBConnection.getResourcesCountAndNamesAndMaxValue();
                    verifyResult(resourceNamesAndValues);
                    Vector<String> resourceNames=resourceNamesAndValues.get(0);
                    for(int i=0;i<resourceNames.size();++i){
                        JTextField resourceTextField=new JTextField(20);
                        JLabel resourceLabel=new JLabel(resourceNames.get(i));
                        resourceLabel.setLabelFor(resourceTextField);
                        enterPanel.add(resourceTextField);
                        enterPanel.add(resourceLabel);
                        Double maxVal=Double.parseDouble(resourceNamesAndValues.get(1).get(i));
                        TextFieldAndDouble temp=new TextFieldAndDouble(resourceTextField,maxVal);
                        resourceTextFieldAndMaxVal.add(temp);
                    }
                    JButton submitButton=new JButton("Подтвердить");
                    submitButton.addActionListener(new ActionListener() {
                        //Добавить matching, получать имена режимов из запроса
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Vector<String> modeNames = DBConnection.getModeNames();
                            String chosenName = newModeName.getText();
                            boolean matching = testForMatching(modeNames, chosenName);
                            if (!chosenName.isEmpty()&&!matching) {
                                Vector<Double> resources = new Vector<>();
                                Vector<Double> maxValaues = new Vector<>();
                                for (TextFieldAndDouble tf : resourceTextFieldAndMaxVal) {
                                    resources.add(Double.parseDouble(tf.getTextField().getText()));
                                    maxValaues.add(tf.getDoubleValue());
                                }
                                if (resources.get(0) < maxValaues.get(0) && resources.get(1) < maxValaues.get(1) && resources.get(2) < maxValaues.get(2)) {
                                    int result=DBConnection.addMode(selectedArticle, selectedSubsystem, selectedDevice, chosenName, resources.get(0), resources.get(1), resources.get(2),lastIndex);
                                    verifyResult(result);
                                    resetUI();
                                }
                            }
                        }
                    });
                    mainPanel.removeAll();
                    enterPanel.add(submitButton);
                    mainPanel.add(enterPanel);
                }
                if(state==QUERY_TO_ARTICLE){
                    selectedSubsystem=resultList.getSelectedValue().toLowerCase();
                    Vector<String> result=DBConnection.queryToSubsys(selectedArticle,selectedSubsystem);
                    NewAlgorithmWindow.verifyResult(result,DBUpdaterFrame);
                    resultList.setListData(result);
                    state=QUERY_TO_SUBSYSTEM;

                }
                if(state==QUERY_TO_ARTICLES) {
                    selectedArticle = resultList.getSelectedValue().toLowerCase();
                    Vector<String> result = DBConnection.queryToArticle(selectedArticle);
                    NewAlgorithmWindow.verifyResult(result, DBUpdaterFrame);
                    resultList.setListData(result);
                    state=QUERY_TO_ARTICLE;
                }
            }
        });
        JPanel panel=new JPanel();
        panel.add(resultList);
        panel.add(nextButton);
        mainPanel.add(panel);
    }
    //true if this name already exists
    private boolean testForMatching(Vector<String> usedNames,String name){
        for(String str:usedNames){
            if(str.equals(name)){
                return true;
            }
        }
        return false;
    }
    private void verifyResult(int testingValue) {
        if (testingValue == DB.CLASS_NOT_FOUND) {
            JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (testingValue == DB.SQL_EXCEPTION) {
            JOptionPane.showMessageDialog(DBUpdaterFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (testingValue == DB.CLASS_CAST_EXCEPTION) {
            JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    private void verifyResult(Vector<Vector<String>> vectorStringVector){
        if(vectorStringVector.get(0).size()>1){
            return;
        }
        try{
            int testingValue=Integer.parseInt(vectorStringVector.get(0).get(0));
            if (testingValue == DB.CLASS_NOT_FOUND) {
                JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (testingValue == DB.SQL_EXCEPTION) {
                JOptionPane.showMessageDialog(DBUpdaterFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (testingValue == DB.CLASS_CAST_EXCEPTION) {
                JOptionPane.showMessageDialog(DBUpdaterFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch (ClassCastException cCExc){
            return;
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e1) {
            System.out.print("ThreadDBUpdater Interrupted Exception");
        }

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
