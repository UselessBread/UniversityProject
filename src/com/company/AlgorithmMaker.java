package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//import static com.company.MainWindow.deviceAndDelayAndRelativeDeviceVector;
import static com.company.MainWindow.mainFrame;
import static com.company.MainWindow.systemInfoVector;
import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Created by Игорь on 03.08.2017.
 */
//Перенести запросы в DB
public class AlgorithmMaker extends JPanel implements ActionListener{
    static final String NEW_ALGORITHM="Create new algorithm";
    static final String SAVE_BUTTON ="Save button pressed";
    static ArrayList<String> usedModes=new ArrayList<>();

    private JButton runButton;
    static JTextArea log=new JTextArea(30,80);
    static JFrame algorithmMakerFrame;
    private JPanel main,buttonPanel;
    static DB DBConnection;
    static Path used_namesPath= Paths.get("C:\\Users\\igord\\IdeaProjects\\Prototype v0.2\\src\\com\\company\\used_names.txt");
    static Charset charset=Charset.forName("UTF-8");
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

        JButton listShowButton=new JButton("Добавить действие");
        listShowButton.setActionCommand(NEW_ALGORITHM);
        listShowButton.addActionListener(this);
        JButton saveButton=new JButton("Сохранить алгоритм");
        saveButton.setActionCommand(SAVE_BUTTON);
        saveButton.addActionListener(this);


        buttonPanel=new JPanel();
        buttonPanel.add(listShowButton);
        buttonPanel.add(saveButton);

        JScrollPane logPane=new JScrollPane(log);
        log.setEditable(false);


        main=new JPanel();
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
        main.add(labelPanel);
        main.add(buttonPanel);
        main.add(logPane);
        add(main);

    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(NEW_ALGORITHM)) {
            NewAlgorithmWindow algorithmWindow = new NewAlgorithmWindow(e, DBConnection);
        }
        if(e.getActionCommand().equals(SAVE_BUTTON)){
            JPanel mainPanel=new JPanel();
            JTextField nameField=new JTextField(10);
            JButton confirmSaveButton=new JButton("Сохранить");
            mainPanel.add(nameField);
            mainPanel.add(confirmSaveButton);
            mainPanel.setOpaque(true);
            JFrame saveFrame=new JFrame("Сохранение");
            saveFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            saveFrame.setContentPane(mainPanel);
            saveFrame.setLocationRelativeTo(algorithmMakerFrame);
            saveFrame.pack();
            saveFrame.setVisible(true);
            confirmSaveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name=nameField.getText();
                    if(name.length()>0){
                        if(compareName(name)) {
                            int res = DBConnection.saveToDB(name,systemInfoVector );
                            if (res == DB.CLASS_NOT_FOUND) {
                                JOptionPane.showMessageDialog(algorithmMakerFrame, "ClassNotFound exception", "ClassNotFound", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (res == DB.SQL_EXCEPTION) {
                                JOptionPane.showMessageDialog(algorithmMakerFrame, "SQLException", "SQLException", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (res == DB.CLASS_CAST_EXCEPTION) {
                                JOptionPane.showMessageDialog(algorithmMakerFrame, "ClassCastException", "ClassCastException", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            writeName(name);
                            saveFrame.dispose();
                            log.setText("");
                            systemInfoVector.clear();
                        }
                        else{
                            JOptionPane.showMessageDialog(algorithmMakerFrame,"Выберите другое имя","Выберите другое имя",JOptionPane.ERROR_MESSAGE);
                        }

                    }
                }
            });
        }
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

    static void createWindow(){
        mainFrame.setVisible(false);
        mainFrame.setEnabled(false);
        algorithmMakerFrame =new JFrame("УА РВ");
        algorithmMakerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        AlgorithmMaker contentPane=new AlgorithmMaker();
        contentPane.setOpaque(true);
        algorithmMakerFrame.setContentPane(contentPane);
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
