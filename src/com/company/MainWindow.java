package com.company;

import com.company.NodeClasses.TopNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;
import com.company.NodeClasses.*;

import static java.nio.file.StandardOpenOption.APPEND;

public class MainWindow extends JPanel implements ActionListener,MouseListener {
    private final String NEW_ALGORITHM = "Create new algorithm";
    private final String OPEN = "Open algorithm";
    private final String ADD="Add to db";
    private final String CLEAR="Clear";
    private final String SAVE="Save algorithm";
    private final String RUN="Run";
    static Path used_namesPath= Paths.get("C:\\Users\\igord\\IdeaProjects\\Prototype v0.3\\src\\com\\company\\used_names.txt");
    static Charset charset=Charset.forName("UTF-8");
    private static Vector<SystemInfo> systemInfoVector=new Vector<>();
    private static Vector<SystemInfo> systemInfoVectorForSaving=new Vector<>();
    private DB DBC=new DB();
    private static JPanel mainPanel = new JPanel();
    static JFrame mainFrame;
    private JTextArea openedAlgorithms=new JTextArea(5,30);
    static JTextArea resourceMonitor=new JTextArea(1,30);
    JTextArea algorithmInfo=new JTextArea(20,20);
    static ArrayList<Double> allResources;
    private JTree tree;
    private int clicks;
    private boolean dc;


    public MainWindow() {
        ArrayList<Double> resources=DBC.getAllResources();
        allResources=resources;
        resourceMonitor.append("0/"+resources.get(0)+" 0/"+resources.get(1)+" 0/"+resources.get(2));
        resourceMonitor.setEditable(false);
        openedAlgorithms.setEditable(false);

        JButton runButton=new JButton("Запустить");
        runButton.setActionCommand(RUN);
        runButton.addActionListener(this);

        JButton addToDBButton=new JButton("Добавить");
        addToDBButton.setActionCommand(ADD);
        addToDBButton.addActionListener(this);
        JButton clearButton=new JButton("Очистить");
        clearButton.setActionCommand(CLEAR);
        clearButton.addActionListener(this);
        JButton saveButton=new JButton("Сохранить алгоритм");
        saveButton.setActionCommand(SAVE);
        saveButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        buttonPanel.add(addToDBButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);

        JSplitPane treeSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        treeSplitPane.setDividerLocation(200);
        treeSplitPane.setPreferredSize(new Dimension(950,450));
        DefaultMutableTreeNode top=new DefaultMutableTreeNode("Изделия");
        tree=new JTree(top);
        createTree(top);
        JScrollPane treeScrollPane=new JScrollPane(tree);
        treeSplitPane.setLeftComponent(treeScrollPane);

        JSplitPane treeWithInfo=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        treeWithInfo.setTopComponent(treeSplitPane);
        JScrollPane algorithmInfoScrollPane=new JScrollPane(algorithmInfo);
        algorithmInfoScrollPane.setPreferredSize(new Dimension(950,60));
        treeWithInfo.setBottomComponent(algorithmInfoScrollPane);

        JSplitPane splitPane=new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(buttonPanel);
        JScrollPane openedScrollPane=new JScrollPane(openedAlgorithms);
        splitPane.setBottomComponent(openedScrollPane);
        JSplitPane withMonitor=new JSplitPane(JSplitPane.VERTICAL_SPLIT,splitPane,resourceMonitor);
        withMonitor.setDividerLocation(420);
        treeSplitPane.setRightComponent(withMonitor);

        mainPanel.add(treeWithInfo);

        add(mainPanel);
    }

    static void createAndShowGUI() {
        mainFrame = new JFrame("Главное окно");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainWindow contentPane = new MainWindow();
        contentPane.setOpaque(true);
        mainFrame.setContentPane(contentPane);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLocation(500,120);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(RUN)){
            (new ThreadRunWindow()).start();
        }
        if(e.getActionCommand().equals(SAVE)){
            boolean ok=true;
            String logText=openedAlgorithms.getText();
            String[] splittedLogText=logText.split("\n");
            String article=splittedLogText[0].split(" ")[1];
            for(String str:splittedLogText){
                if(!str.contains(article)){
                    System.out.print("Different Articles");
                    JOptionPane.showMessageDialog(mainFrame,"Different Articles");
                    ok=false;
                }
            }
            if(ok) {
                saveToDB(article);
            }
        }
        if(e.getActionCommand().equals(CLEAR)){
            openedAlgorithms.setText("");
            systemInfoVector.clear();
        }

        if(e.getActionCommand().equals(ADD)){
            (new ThreadDBUpdater()).start();
        }

    }
    static Vector<SystemInfo> getSystemInfoVector(){
        return systemInfoVector;
    }
    static Vector<SystemInfo> getSystemInfoVectorForSaving(){return systemInfoVectorForSaving;}
    private void createTree(DefaultMutableTreeNode top){
        Vector<TopNode> topNodes=createTopNodes(top);
        tree.addMouseListener(this);
        //Root hiding
        tree.setRootVisible(false);
        TreePath p=new TreePath(top.getPath());
        tree.expandPath(p);
        Vector<SubsystemNode> subsystemNodes=createSubsystemLeaves(topNodes);
        Vector<DeviceNode> deviceNodes=createDeviceLeaves(subsystemNodes);
        createDeviceModeLeaves(deviceNodes);
    }
    private Vector<TopNode> createTopNodes(DefaultMutableTreeNode top){
        Vector<String> result=DBC.queryToArticles();
        verifyResult(result,mainFrame);
        Vector<TopNode> topNodes=new Vector<>();
        for(String str:result){
            DefaultMutableTreeNode newTop=new DefaultMutableTreeNode(str);
            TopNode topNode=new TopNode(str,newTop);
            topNodes.add(topNode);
            top.add(newTop);
        }
        return topNodes;
    }
    private Vector<SubsystemNode> createSubsystemLeaves(Vector<TopNode> topNodes){
        Vector<SubsystemNode> subsystemNodes=new Vector<>();
        for(TopNode topNode:topNodes){
            DefaultMutableTreeNode systemNode=new DefaultMutableTreeNode("Системы");
            topNode.getTopNode().add(systemNode);
            String topNodeName=topNode.getName();
            Vector<String> result=DBC.queryToArticle(topNodeName);
            for(String str:result) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(str);
                systemNode.add(newNode);
                SubsystemNode subsystemNode = new SubsystemNode(topNodeName,str,newNode);
                subsystemNodes.add(subsystemNode);
            }
        }
        createAlgorithmLeaves(topNodes);
        return subsystemNodes;
    }
    private void createAlgorithmLeaves(Vector<TopNode> topNodes){
        for(TopNode topNode:topNodes){
            DefaultMutableTreeNode systemNode=new DefaultMutableTreeNode("Алгоритмы");
            DefaultMutableTreeNode variantNode=new DefaultMutableTreeNode("Варианты");
            systemNode.add(variantNode);
            topNode.getTopNode().add(systemNode);
            String topNodeName=topNode.getName();
            Vector<String> result=DBC.queryToAlgorithms(topNodeName);
            for(String str:result) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(str);
                variantNode.add(newNode);
            }
        }
    }
    private Vector<DeviceNode> createDeviceLeaves(Vector<SubsystemNode> subsystemNodes){
        Vector<DeviceNode> deviceNodeVector=new Vector<>();
        for(SubsystemNode subsystemNode:subsystemNodes) {
            String articleName=subsystemNode.getTopNodeName();
            String subsystemName=subsystemNode.getName();
            Vector<String> sensorName = DBC.getSensorNames(articleName,subsystemName);
            Vector<String> deviceName=DBC.getDeviceNames(articleName,subsystemName);
            if(sensorName.size()>0){
                for(String str:sensorName){
                    DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(str);
                    subsystemNode.getSubsystemNode().add(newNode);
                    DeviceNode deviceNode=new DeviceNode(articleName,str,subsystemName,newNode);
                    deviceNodeVector.add(deviceNode);
                }
            }
            if(deviceName.size()>0){
                for(String str:deviceName){
                    DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(str);
                    subsystemNode.getSubsystemNode().add(newNode);
                    DeviceNode deviceNode=new DeviceNode(articleName,str,subsystemName,newNode);
                    deviceNodeVector.add(deviceNode);
                }
            }
        }
        return deviceNodeVector;
    }
    private void createDeviceModeLeaves(Vector<DeviceNode> deviceNodes){
        for(DeviceNode deviceNode:deviceNodes){
            String articleName=deviceNode.getArticleName();
            String subsystemName=deviceNode.getSubsystemNodeName();
            String deviceName=deviceNode.getDeviceName();
            Vector<String> sensorModes=DBC.queryToSensor(articleName,subsystemName,deviceName);
            Vector<String> deviceModes=DBC.queryToDevice(articleName,subsystemName,deviceName);
            if(sensorModes.size()>0&&!sensorModes.get(0).equals("-12")){
                for(String str:sensorModes){
                    deviceNode.getDeviceNode().add(new DefaultMutableTreeNode(str));
                }
            }
            if(deviceModes.size()>0&&!deviceModes.get(0).equals("-12")){
                for(String str:deviceModes){
                    deviceNode.getDeviceNode().add(new DefaultMutableTreeNode(str));
                }
            }
            if(deviceModes.size()==0||sensorModes.size()==0){
                deviceNode.getDeviceNode().add(new DefaultMutableTreeNode());
            }
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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        clicks=0;
        dc=false;
        if(e.getButton()==MouseEvent.BUTTON1){
            Integer timeInterval=(Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
            if(e.getClickCount()==1){
                int x=e.getX();
                int y=e.getY();
                TreePath path=tree.getPathForLocation(x,y);
                String[] splittedPath=path.toString().split(",");
                if(splittedPath.length==5&&path.toString().contains("Варианты")){
                    Vector<String> result=getAlgorithmInfo(splittedPath);
                    algorithmInfo.setText("");
                    for(String str:result){
                        //handle with time for algorithm info
                        SystemInfo systemInfo=new SystemInfo(str.trim());
                        handleWithTimeForAlgorithmInfo(systemInfo);

                    }
                }
            }
            if(e.getClickCount()==2)
                dc=true;
            Timer timer=new Timer(timeInterval, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    if(dc){
                        clicks++;
                        int x=e.getX();
                        int y=e.getY();
                        TreePath path=tree.getPathForLocation(x,y);
                        String string=path.toString();
                        String[] splittedPath=string.split(",");
                        if(clicks==2&&splittedPath.length==6&&path.toString().contains("Системы")) {
                            askForDelay(splittedPath);
                        }
                        if(clicks==2&&splittedPath.length==5&&path.toString().contains("Варианты")){
                            //Добавление алгоритма в окно
                            Vector<String> result=getAlgorithmInfo(splittedPath);
                            for(String str:result){
                                SystemInfo systemInfo=new SystemInfo(str.trim());
                                systemInfoVector.add(systemInfo);
                                handleWithTime(systemInfo);
                            }
                        }
                    }
                }
            });
            timer.start();
            timer.setRepeats(false);
            if(e.getID()==MouseEvent.MOUSE_RELEASED)
                timer.stop();

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    private void askForDelay(String[] splittedPath){
        JPanel delayPanel=new JPanel();
        JTextField delayField=new JTextField(10);
        delayField.setEditable(true);
        String logContent=openedAlgorithms.getText();
        String[] formats={"Часы","Минуты","Секунды","Миллисекунды"};
        JComboBox<String>delayFormatChooser=new JComboBox<>(formats);
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
        JComboBox<String>queueChooser = new JComboBox<>(deviceToQueueChooser);
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
        JButton confirmButton=new JButton("Confirm");
        delayPanel.add(delayActivator);
        delayPanel.add(delayField);
        delayPanel.add(delayFormatChooser);
        delayPanel.add(queueChooser);
        delayPanel.add(confirmButton);
        JFrame delayFrame=new JFrame("Delay");
        delayFrame.setContentPane(delayPanel);
        delayFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        delayFrame.setLocationRelativeTo(mainFrame);
        delayFrame.pack();
        delayFrame.setVisible(true);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String articleName=splittedPath[1].trim();
                String subsystemName=splittedPath[3].trim();
                String deviceName=splittedPath[4].trim();
                String mode=splittedPath[5].split("]")[0].trim();
                if(!delayActivator.isSelected()){
                    SystemInfo systemInfo=new SystemInfo(articleName,subsystemName,deviceName,mode,"","");
                    systemInfoVector.add(systemInfo);
                    handleWithTime(systemInfo);
                    delayFrame.dispose();

                }
                if(delayActivator.isSelected()){
                    SystemInfo systemInfo=new SystemInfo(articleName,subsystemName,deviceName,mode,
                            delayField.getText()+delayFormatChooser.getSelectedItem(),(String)queueChooser.getSelectedItem());
                    systemInfoVector.add(systemInfo);
                    handleWithTime(systemInfo);
                    delayFrame.dispose();
                }
            }
        });

    }
    private void saveToDB(String article) {

        JPanel mainPanel = new JPanel();
        JTextField nameField = new JTextField(10);
        JButton confirmSaveButton = new JButton("Сохранить");
        mainPanel.add(nameField);
        mainPanel.add(confirmSaveButton);
        mainPanel.setOpaque(true);
        JFrame saveFrame = new JFrame("Сохранение");
        saveFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        saveFrame.setContentPane(mainPanel);
        saveFrame.pack();
        saveFrame.setVisible(true);
        confirmSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (name.length() > 0) {
                    if (compareName(name)) {
                        int res = DBC.saveToDB(article, name, systemInfoVector);
                        if (res == DB.CLASS_NOT_FOUND) {
                            JOptionPane.showMessageDialog(mainFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (res == DB.SQL_EXCEPTION) {
                            JOptionPane.showMessageDialog(mainFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (res == DB.CLASS_CAST_EXCEPTION) {
                            JOptionPane.showMessageDialog(mainFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        writeName(name);
                        saveFrame.dispose();
                        openedAlgorithms.setText("");
                        MainWindow.getSystemInfoVectorForSaving().clear();
                       DefaultMutableTreeNode temp=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                       DefaultTreeModel treeModel=(DefaultTreeModel) tree.getModel();
                       DefaultMutableTreeNode articleNode=(DefaultMutableTreeNode) treeModel.getPathToRoot(temp)[1];
                       DefaultMutableTreeNode algorithmsNode=(DefaultMutableTreeNode)treeModel.getChild(articleNode,1);
                       DefaultMutableTreeNode variantNode=(DefaultMutableTreeNode)treeModel.getChild(algorithmsNode,0);
                       treeModel.insertNodeInto(new DefaultMutableTreeNode(name),variantNode,variantNode.getChildCount());
                        /*DefaultMutableTreeNode newTop = new DefaultMutableTreeNode("Изделия");
                        tree=new JTree(newTop);
                        createTree(newTop);*/

                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Выберите другое имя", "Выберите другое имя", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });


    }
    private void writeName(String name){
        try(BufferedWriter bufferedWriter= Files.newBufferedWriter(used_namesPath,charset,APPEND)){
            bufferedWriter.write(name+"/");
        }catch(IOException ex){}
    }
    private boolean compareName(String name){
        String line;
        try(BufferedReader bufferedReader= Files.newBufferedReader(used_namesPath,charset)){
            while((line=bufferedReader.readLine())!=null){
                String[] usedNames=line.split("/");
                for(String str:usedNames){
                    if(str.equals(name))
                        return false;
                }
            }
        }catch(IOException ex){}
        return true;
    }
    private Vector<String> getAlgorithmInfo(String[] splittedPath){
        String article=splittedPath[1].toLowerCase().trim();
        String algorithm=splittedPath[splittedPath.length-1].split("]")[0].toLowerCase().trim();
        Vector<String> result=DBC.getAlgorithmInfo(article,algorithm);
        verifyResult(result,mainFrame);
        return result;
    }
    private void handleWithTime(SystemInfo systemInfo){
        try {
            String text = openedAlgorithms.getText();
            if(!text.equals("")) {
                int lines=openedAlgorithms.getLineCount();
                String lastLine = text.split("\n")[openedAlgorithms.getLineCount()-2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c","").trim();
                int lastTime = Integer.parseInt(lastTimeStr);
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");
                int time = Integer.parseInt(delay[0]);
                //if delay has been set
                int newTime = time + lastTime;
                openedAlgorithms.append("=" + newTime + "c|" + systemInfo.getArticle() +" "+ systemInfo.getSubsystem()+" " +
                        systemInfo.getDeviceName()+" " + systemInfo.getMode()+ "\n");
            }
            else{
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");
                int time = Integer.parseInt(delay[0]);
                openedAlgorithms.append("="+time+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
            }

        }catch (NumberFormatException e){
            //if there is no delay
            String text=openedAlgorithms.getText();
            String lastLine;
            int lastTime;
            if(!text.isEmpty()) {
                lastLine = text.split("\n")[openedAlgorithms.getLineCount() - 2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c", "").trim();
                lastTime = Integer.parseInt(lastTimeStr);
            }
            else
                lastTime=0;
            openedAlgorithms.append("="+lastTime+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+ systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
        }
    }
    private void handleWithTimeForAlgorithmInfo(SystemInfo systemInfo){
        try {
            String text = algorithmInfo.getText();
            if(!text.equals("")) {
                int lines=algorithmInfo.getLineCount();
                String lastLine = text.split("\n")[algorithmInfo.getLineCount()-2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c","").trim();
                int lastTime = Integer.parseInt(lastTimeStr);
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");
                int time = Integer.parseInt(delay[0]);
                //if delay has been set
                int newTime = time + lastTime;
                algorithmInfo.append("=" + newTime + "c|" + systemInfo.getArticle() +" "+ systemInfo.getSubsystem()+" "+systemInfo.getDeviceName()+
                        " "+systemInfo.getMode()+"\n");
            }
            else{
                String[] delay=systemInfo.getDelay().split("[а-яА-Я]");
                int time = Integer.parseInt(delay[0]);
                algorithmInfo.append("="+time+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+
                        systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
            }

        }catch (NumberFormatException e){
            //if there is no delay
            String text=algorithmInfo.getText();
            String lastLine;
            int lastTime;
            if(!text.isEmpty()) {
                lastLine = text.split("\n")[algorithmInfo.getLineCount() - 2];
                String lastTimeStr = lastLine.split("\\u007c")[0].replace("=", "").replace("c", "").trim();
                lastTime = Integer.parseInt(lastTimeStr);
            }
            else
                lastTime=0;
            algorithmInfo.append("="+lastTime+"c|"+systemInfo.getArticle()+" "+systemInfo.getSubsystem()+" "+
                    systemInfo.getDeviceName()+" "+ systemInfo.getMode()+"\n");
        }
    }

}
