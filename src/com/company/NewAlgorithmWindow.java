package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;


public class NewAlgorithmWindow implements ActionListener{
    private final static int QUERY_TO_ARTICLES_HAS_BEEN_DONE=13;
    private final static int QUERY_TO_ARTICLE_HAS_BEEN_DONE=14;
    private final static int QUERY_TO_SUBSYSTEM_HAS_BEEN_DONE=15;
    private final static int LAST_QUERY_HAS_BEEN_DONE=16;

    //затычка
    private boolean flag=true;
    private JTextField delayField=new JTextField(2);
    private JCheckBox delayActivator;
    private JComboBox<String> delayFormatChooser;
    private JComboBox<String> queueChooser;
    private int state;
    private static JFrame listFrame;
    private JPanel resultButtonPanel = new JPanel();
    private static JList<String> resultList;
    private ActionEvent e;
    private DB DBConnection;
    private static Vector<String> resultTest;
    private static Vector<Vector<String>> historyVector = new Vector<>();
    private Vector<String> firstResultTest=new Vector<>();
    private String toUsedModes;
    private String resultArticle,resultSubsys,resultDevice;
    private JPanel listPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JPanel delayPanel=new JPanel();
    //Необходимо в дальнейшем заменить, ибо обеспечивает связь с методом из старой версии класса
    private Vector<String> forAddingToUsedModes=new Vector<>();



    NewAlgorithmWindow(ActionEvent e, DB db) {
        MainWindow.getSystemInfoVectorForSaving().clear();
        this.e = e;
        DBConnection = db;
        AlgorithmMaker.getAlgorithmMakerFrame().setVisible(false);//disable main frame
        AlgorithmMaker.getAlgorithmMakerFrame().setEnabled(true);
        makeItHappen();
    }

    private void makeItHappen () {
        JButton nextButton;
        JPanel nextButtonPanel = new JPanel();
        JPanel backButtonPanel = new JPanel();

        //If the "создать алгоритм" button pressed
        if (e.getActionCommand().equals(AlgorithmMaker.NEW_ALGORITHM)) {

            resultTest = DBConnection.queryToArticles();
            verifyResult(resultTest,listFrame);
            historyVector.add(resultTest);
            //Когда вернулись в самое начало, добавить его через listener кнопки "Назад"
            firstResultTest=resultTest;
            resultList = new JList<>(resultTest);
            resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resultList.setLayoutOrientation(JList.VERTICAL_WRAP);
            resultList.setVisibleRowCount(-1);
            listPanel.add(resultList);
            state=QUERY_TO_ARTICLES_HAS_BEEN_DONE;
            //Следуя моему плану, нужно менять ActionListener'ы, дабы одной кнопкой управлять всеми вызовами
            nextButton = new JButton("Далее");
            nextButton.addActionListener(this);
            nextButtonPanel.add(nextButton);

            listPanel.add(nextButtonPanel);
            listPanel.setOpaque(true);
            //Кнопка "Наза  д"
            addBackButton(backButtonPanel);

            listPanel.add(backButtonPanel);
            //Главная рамка
            initFrame(listPanel);
        }
    }


    static void verifyResult(Vector<String> stringVector,JFrame parentFrame){
        try {
            if (stringVector.size() != 1) {
                return;
            }
            if (stringVector.size() == 1) {
                String firstResult = stringVector.get(0);
                int testingValue = Integer.parseInt(firstResult);
                if (testingValue == DB.CLASS_NOT_FOUND) {
                    JOptionPane.showMessageDialog(parentFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.SQL_EXCEPTION) {
                    JOptionPane.showMessageDialog(parentFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (testingValue == DB.CLASS_CAST_EXCEPTION) {
                    JOptionPane.showMessageDialog(parentFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }catch (NumberFormatException exc){
            return;
        }

    }
    private void buttonTest(ArrayList<JButton> buttonList) {
        if (AlgorithmMaker.getUsedModes().size() == 0) {
            for (JButton button : buttonList) {
                String command = button.getActionCommand();
                String[] splittedCommand = command.split("\t");
                if (splittedCommand[0].equals("ВКЛ")) {
                    button.setEnabled(true);
                }
                if (splittedCommand[0].equals("ВЫКЛ")) {
                    button.setEnabled(false);
                }
            }
        } else {
            for (String usedMode : AlgorithmMaker.getUsedModes()) {
                String[] splitedMode = usedMode.split(" ");
                if ((splitedMode[0] + " " + splitedMode[1]+" "+splitedMode[2]).equals(resultArticle + " " + resultSubsys+" "+resultDevice)) {

                    String usedCommand = splitedMode[3];
                    String usedCommandName=usedCommand.split("\t")[0];
                    if (usedCommandName.equals("ВКЛ")) {
                        for (JButton button : buttonList) {
                            String buttonCmd = button.getActionCommand();
                            String[] splittedButtonCmd = buttonCmd.split("\t");
                            if (splittedButtonCmd[0].equals("ВКЛ"))
                                button.setEnabled(false);
                            if (splittedButtonCmd[0].equals("ВЫКЛ"))
                                button.setEnabled(true);
                        }
                        return;
                    } else if (usedCommandName.equals("ВЫКЛ")) {
                        for (JButton button : buttonList) {
                            String buttonCmd = button.getActionCommand();
                            String[] splittedButtonCmd = buttonCmd.split("\t");
                            if (splittedButtonCmd[0].equals("ВКЛ"))
                                button.setEnabled(true);
                            if (splittedButtonCmd[0].equals("ВЫКЛ"))
                                button.setEnabled(false);
                        }
                        return;
                    } else {
                        for (JButton button : buttonList) {
                            String buttonCmd = button.getActionCommand();
                            String[] splittedButtonCmd = buttonCmd.split("\t");
                            if(usedCommand.equals(buttonCmd))
                                button.setEnabled(false);
                        }
                    }
                } else {
                    for (JButton button : buttonList) {
                        String command = button.getActionCommand();
                        String[] splittedCommand = command.split("\t");
                        if (splittedCommand[0].equals("ВКЛ")) {
                            button.setEnabled(true);
                        }
                        if (splittedCommand[0].equals("ВЫКЛ")) {
                            button.setEnabled(false);
                        }
                    }
                }
            }
        }
    }
    private void addToUsedModes(){
        int i=0;
        if(AlgorithmMaker.getUsedModes().size()==0)
            AlgorithmMaker.getUsedModes().add(toUsedModes);
        else {
            for (String str : AlgorithmMaker.getUsedModes()) {
                String[] splittedStr = str.split(" ");
                String test = splittedStr[0] + " " + splittedStr[1]+" "+splittedStr[2];
                if (test.equals(resultArticle + " " + resultSubsys+" "+resultDevice ) && !splittedStr[3].split("\t")[0].equals("ВКЛ") &&
                        !splittedStr[3].split("\t")[0].equals("ВЫКЛ")) {
                    AlgorithmMaker.getUsedModes().remove(i);
                    AlgorithmMaker.getUsedModes().add(toUsedModes);
                    break;
                }
                if (test.equals(resultArticle + " " + resultSubsys+" "+resultDevice) && splittedStr[3].split("\t")[0].equals("ВКЛ") ||
                        splittedStr[3].split("\t")[0].equals("ВЫКЛ")) {
                    String toUsedCmdName = toUsedModes.split(" ")[3].split("\t")[0];
                    if (toUsedCmdName.equals("ВКЛ") || toUsedCmdName.equals("ВЫКЛ")) {
                        AlgorithmMaker.getUsedModes().remove(i);
                        AlgorithmMaker.getUsedModes().add(toUsedModes);
                    }
                    break;
                }
                if(!test.equals(resultArticle + " " + resultSubsys+" "+resultDevice)) {
                    AlgorithmMaker.getUsedModes().add(toUsedModes);
                    break;
                }
                i++;
            }
        }
    }
    private void initFrame(JPanel listPanel){
        listFrame = new JFrame("Results");
        listFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        listFrame.setContentPane(listPanel);
        listFrame.setPreferredSize(new Dimension(500, 300));
        listFrame.setLocationRelativeTo(AlgorithmMaker.getAlgorithmMakerFrame());
        //listFrame.addWindowListener(this);
        listFrame.pack();
        listFrame.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(state==QUERY_TO_ARTICLES_HAS_BEEN_DONE){
            resultArticle=resultList.getSelectedValue().toLowerCase();
            resultTest=DBConnection.queryToArticle(resultArticle);
            verifyResult(resultTest,listFrame);
            resultList.setListData(resultTest);
            state=QUERY_TO_ARTICLE_HAS_BEEN_DONE;
            return;
        }
        if(state==QUERY_TO_ARTICLE_HAS_BEEN_DONE){
            historyVector.add(resultTest);
            resultSubsys= resultList.getSelectedValue().toLowerCase();
            resultTest=DBConnection.queryToSubsys(resultArticle,resultSubsys);
            verifyResult(resultTest,listFrame);
            resultList.setListData(resultTest);
            state=QUERY_TO_SUBSYSTEM_HAS_BEEN_DONE;
            return;
        }
        if(state==QUERY_TO_SUBSYSTEM_HAS_BEEN_DONE){
            ArrayList<JButton> currentButtons = new ArrayList<>();
            if(flag) {
                historyVector.add(resultTest);
            }
            flag=false;
            try {
                resultDevice = resultList.getSelectedValue().toLowerCase();
                resultTest = DBConnection.queryToDevice(resultArticle, resultSubsys, resultDevice);
                int testForExctption = Integer.parseInt(resultTest.get(0));
                resultTest = DBConnection.queryToSensor(resultArticle, resultSubsys, resultDevice);
                verifyResult(resultTest,listFrame);
                state = LAST_QUERY_HAS_BEEN_DONE;
                delayPanel=delayPanelInit();
                delayPanel.setVisible(true);
                listPanel.add(delayPanel);
                //currentButtons-тоже пережиток прошлого
                //Заменить
                forAddingToUsedModes.add(resultArticle);
                forAddingToUsedModes.add(resultSubsys);
                forAddingToUsedModes.add(resultDevice);
                makeFinalButtons(resultTest,currentButtons,forAddingToUsedModes);
                resultList.setVisible(false);
                listFrame.repaint();
                listPanel.add(buttonPanel);
                currentButtons.clear();
                forAddingToUsedModes.clear();
                return;
            }catch (NumberFormatException numberFormatEx){
                verifyResult(resultTest,listFrame);
                state = LAST_QUERY_HAS_BEEN_DONE;
                delayPanel=delayPanelInit();
                delayPanel.setVisible(true);
                listPanel.add(delayPanel);
                //currentButtons-тоже пережиток прошлого
                //Заменить
                forAddingToUsedModes.add(resultArticle);
                forAddingToUsedModes.add(resultSubsys);
                forAddingToUsedModes.add(resultDevice);
                makeFinalButtons(resultTest,currentButtons,forAddingToUsedModes);
                resultList.setVisible(false);
                listFrame.repaint();
                listPanel.add(buttonPanel);
                currentButtons.clear();
                forAddingToUsedModes.clear();
                return;
            }
        }
    }
    private void addBackButton(JPanel panel){
        JButton backButton=new JButton("Назад");
        panel.add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(state==LAST_QUERY_HAS_BEEN_DONE){
                    resultList.setListData(historyVector.get(historyVector.size()-1));
                    historyVector.remove(historyVector.size()-1);
                    state=QUERY_TO_SUBSYSTEM_HAS_BEEN_DONE;
                    resultList.setVisible(true);
                    delayPanel.setVisible(false);
                    listPanel.remove(buttonPanel);
                    buttonPanel.removeAll();
                    listFrame.repaint();
                    return;
                }
                if(state==QUERY_TO_SUBSYSTEM_HAS_BEEN_DONE){
                    resultList.setListData(historyVector.get(historyVector.size()-1));
                    historyVector.remove(historyVector.size()-1);
                    state=QUERY_TO_ARTICLE_HAS_BEEN_DONE;
                    return;
                }
                if(state==QUERY_TO_ARTICLE_HAS_BEEN_DONE){
                    resultList.setListData(historyVector.get(historyVector.size()-1));
                    historyVector.remove(historyVector.size()-1);
                    state=QUERY_TO_ARTICLES_HAS_BEEN_DONE;
                    if(historyVector.size()==0){
                        historyVector.add(firstResultTest);
                    }
                    return;
                }

            }
        });
    }
    private void makeFinalButtons(Vector<String> resultList,ArrayList<JButton> currentButtons,Vector<String>forLogging){
        for(String str:resultList){
            JButton finalButton=new JButton(str);
            finalButton.setActionCommand(str);
            buttonPanel.add(finalButton);
            currentButtons.add(finalButton);
            finalButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String mode = e.getActionCommand();
                    String delayFieldResult = delayField.getText();
                    if (((delayField.getText().equals("")) && !(delayActivator.isSelected())) || !(delayField.getText().equals("")) && (delayActivator.isSelected())) {
                        boolean delayChosen=false;
                        String delay;
                        if (delayActivator.isSelected()) {
                            delay = delayField.getText() + "/" + delayFormatChooser.getSelectedItem(); //Убрал +" " в конце после delayFormatChooser...
                            delayChosen = true;
                        } else {
                            delay = delayFieldResult;
                            delayChosen = false;
                        }
                        SystemInfo newAction=new SystemInfo(resultArticle,resultSubsys,resultDevice,mode,delay,(String)queueChooser.getSelectedItem());
                        MainWindow.getSystemInfoVectorForSaving().add(newAction);
                        String queueSelectedItem = (String) queueChooser.getSelectedItem();
                        AlgorithmMaker.getLog().append(resultArticle+" "+resultSubsys+" "+resultDevice+" "+mode+delay);
                        toUsedModes=resultArticle+" "+resultSubsys+" "+resultDevice+ " "+mode+" "+delay;
                        addToUsedModes();

                        if (delayChosen) {
                            if (queueSelectedItem.equals(" ")) {
                                AlgorithmMaker.getLog().append("После стара алгоритма\n");
                            } else {
                                AlgorithmMaker.getLog().append("После запуска " + queueSelectedItem + "\n");
                            }
                        }
                        else
                            AlgorithmMaker.getLog().append("\n");
                        listPanel.remove(resultButtonPanel);
                        listFrame.dispose();
                        AlgorithmMaker.getAlgorithmMakerFrame().setVisible(true);
                        AlgorithmMaker.getAlgorithmMakerFrame().setEnabled(true);
                    }
                }
            });
        }
        buttonTest(currentButtons);
    }
    private JPanel delayPanelInit(){
        JPanel delayPanel=new JPanel();
        delayField.setEditable(true);
        String logContent=AlgorithmMaker.getLog().getText();
        String[] formats={"Часы","Минуты","Секунды","Миллисекунды"};
        delayFormatChooser=new JComboBox<>(formats);
        delayFormatChooser.setSelectedIndex(2);
        String[] logContentSplit = logContent.split("\n");
        Vector<String> deviceToQueueChooser = new Vector<>();
        deviceToQueueChooser.add(" ");
        if(logContent.length()>1) {
            for (String str : logContentSplit) {
                String[] tempStr = str.split(" ");
                deviceToQueueChooser.add(tempStr[2]);
            }
        }
        queueChooser = new JComboBox<>(deviceToQueueChooser);
        queueChooser.setSelectedIndex(0);
        //Set delay activator
        delayActivator=new JCheckBox("Включить задержку");
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
        return delayPanel;
    }
}
