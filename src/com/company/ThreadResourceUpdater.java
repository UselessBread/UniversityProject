package com.company;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

public class ThreadResourceUpdater implements Runnable,WindowListener {
    private String mode;
    private JPanel mainPanel;
    private JPanel resourcePanel=new JPanel();
    private JFrame mainFrame;
    private DB DBConnection=new DB();
    private Vector<JTextField> textFieldVector=new Vector<>();
    ThreadResourceUpdater(String mode){
        this.mode=mode;
    }

    @Override
    public void run() {
        //TODO:Изменение таюлиц потребления режимов
        resourcePanel.setLayout(new GridLayout(0,6));
        if(mode.equals(MainWindow.getADD_RESOURCE())){

            //Set up interface with three columns: resource name,resource val,resource measure
            JTree tree=MainWindow.getTree();
            int x= MainWindow.getPopupX();
            int y= MainWindow.getPopupY();
            TreePath path=tree.getPathForLocation(x,y);
            String pathString=path.toString().replace("[","");
            pathString=pathString.replace("]","");
            String[] splittedPathString=pathString.split(",");
            String article=splittedPathString[1].trim();
            setUpResourceAddingUI(article);
            Vector<String> resourceNames=DBConnection.getResourcesNames(article);
            Vector<String> articleResources=DBConnection.getArticleResources(article);
            Vector<String> resourcesMeasurements=DBConnection.getArticleMeasurements(article);
            for(int i=0;i<articleResources.size();i++){
                JTextField resourceNameField=new JTextField(resourceNames.get(i));
                JLabel resourceNameFieldLabel=new JLabel("Имя ресурса"+(i+1));
                resourceNameFieldLabel.setLabelFor(resourceNameField);
                JTextField resourceValueField=new JTextField(articleResources.get(i));
                JTextField resourceMeasurement=new JTextField(resourcesMeasurements.get(i));
                JLabel resourceValueFieldLabel=new JLabel("Значение ресурса"+(i+1));
                resourceValueFieldLabel.setLabelFor(resourceValueField);
                JLabel resourceMeasurementLabel=new JLabel("Еденицы измерения");
                resourceMeasurementLabel.setLabelFor(resourceMeasurement);
                resourcePanel.add(resourceNameFieldLabel);
                resourcePanel.add(resourceNameField);
                resourcePanel.add(resourceValueFieldLabel);
                resourcePanel.add(resourceValueField);
                resourcePanel.add(resourceMeasurementLabel);
                resourcePanel.add(resourceMeasurement);
                textFieldVector.add(resourceNameField);
                textFieldVector.add(resourceValueField);
                textFieldVector.add(resourceMeasurement);
            }
            JButton addButton=new JButton ("Добавить");
            resourcePanel.add(addButton);
            addButton.addActionListener(e -> {
                JTextField resourceNameField=new JTextField();
                JLabel resourceNameFieldLabel=new JLabel("Имя ресурса");
                resourceNameFieldLabel.setLabelFor(resourceNameField);
                JTextField resourceValueField=new JTextField();
                JTextField resourceMeasurement=new JTextField();
                JLabel resourceValueFieldLabel=new JLabel("Значение ресурса");
                resourceValueFieldLabel.setLabelFor(resourceValueField);
                JLabel resourceMeasurementLabel=new JLabel("Еденицы измерения");
                resourceMeasurementLabel.setLabelFor(resourceMeasurement);
                resourcePanel.add(resourceNameFieldLabel);
                resourcePanel.add(resourceNameField);
                resourcePanel.add(resourceValueFieldLabel);
                resourcePanel.add(resourceValueField);
                resourcePanel.add(resourceMeasurementLabel);
                resourcePanel.add(resourceMeasurement);
                resourcePanel.remove(addButton);
                resourcePanel.add(addButton);
                textFieldVector.add(resourceNameField);
                textFieldVector.add(resourceValueField);
                textFieldVector.add(resourceMeasurement);
                updateWindow(mainFrame.getHeight(),mainFrame.getWidth());
            });
            JButton confirmButton=new JButton("Изменить");
            JPanel confirmButtonPanel=new JPanel();
            confirmButtonPanel.add(confirmButton);
            confirmButton.addActionListener(e -> {
                DBConnection.changeArticleResources(article,textFieldVector);
                mainFrame.dispose();
            });

            mainPanel.add(resourcePanel);
            mainPanel.add(confirmButtonPanel);
        }
    }
    private void setUpResourceAddingUI(String article){
        mainPanel=new JPanel();
        mainPanel.setOpaque(true);
        mainFrame=new JFrame("Изменение ресурсов в "+article+"");
        mainFrame.setContentPane(mainPanel);
        mainFrame.addWindowListener(this);
        mainFrame.setLocationRelativeTo(MainWindow.getMainFrame());
        mainFrame.setPreferredSize(new Dimension(600,500));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }
    @Override
    public void windowClosing(WindowEvent e) {
        Thread.currentThread().interrupt();
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

    private void updateWindow(int height,int width){
        Dimension dimension;
        if(mainFrame.getSize().height<height){
            dimension=new Dimension(width,mainFrame.getSize().height+1);
        }
        else {
            dimension = new Dimension(width, mainFrame.getSize().height - 1);
        }
        mainFrame.setSize(dimension);
    }
}
