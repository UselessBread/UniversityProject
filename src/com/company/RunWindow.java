package com.company;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;

import static com.company.AlgorithmMaker.*;
import static com.company.MainWindow.*;


public class RunWindow implements WindowListener{
    private JTextArea executingArea;
    JPanel main;
    JFrame runWindowFrame;
    private Vector<HashMap<HashMap<String, String>, String>> t=new Vector<>();


    public RunWindow() {
        executingArea = new JTextArea(30, 80);
        executingArea.setEditable(false);
        executingArea.setText("");
        JScrollPane executingAreaScrollPane = new JScrollPane(executingArea);
        main = new JPanel();
        main.setOpaque(true);
        main.add(executingAreaScrollPane);
        runWindowFrame = new JFrame("Запуск");
        runWindowFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        runWindowFrame.addWindowListener(this);
        runWindowFrame.setContentPane(main);
        runWindowFrame.pack();
        runWindowFrame.setVisible(true);

       makeWindow();
    }
    private void makeWindow() {
        for(SystemInfo systemInfo:systemInfoVector){
            if(systemInfo.getDelay().length()==0){
                executingArea.append(systemInfo.getInfoWithoutDelay()+"\n");
            }
            if(systemInfo.getDelay().length()>0&&systemInfo.getRelation().length()>0){
                String[] delay=systemInfo.getDelay().split("/");
                int time=convertToMilliseconds(delay);
                executeDelay(Calendar.MILLISECOND,time,systemInfo.getInfoWithRelation());
            }
            if(systemInfo.getDelay().length()>0&&systemInfo.getRelation().length()==0){
                String delay[]=systemInfo.getDelay().split("/");
                int time=convertToMilliseconds(delay);
                executeDelay(Calendar.MILLISECOND,time,systemInfo.getInfoWithoutRelation()+" ");
            }
        }

    }
    //Сделать варианты для большого количества часов\минут\секунд(как с миллисекундами)
    private void executeDelay(int calendarConstant,int timeValue,String deviceInfo) {
        boolean done = false;
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(calendarConstant) + timeValue;
        if (calendarConstant == Calendar.MILLISECOND) {
            time = calendar.get(Calendar.HOUR) * 3600000 + calendar.get(Calendar.MINUTE) * 60000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
            int goalTime = time + timeValue;
            while (calendar.get(Calendar.HOUR) * 3600000 + calendar.get(Calendar.MINUTE) * 60000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND) < goalTime) {
                calendar = Calendar.getInstance();
            }
            executingArea.append(deviceInfo+"\n");
        }
        else{
                while (!done) {
                    calendar = Calendar.getInstance();
                    if (calendar.get(calendarConstant) >= time) {
                        executingArea.append(deviceInfo+"\n");
                        done = true;
                    }
                }
            }
        }

    private int convertToMilliseconds(String[] delay){
        int delayVal;
        String delayMetrics=delay[1];
        delayVal=Integer.parseInt(delay[0]);
        if(delayMetrics.equals("Часы")){
            return delayVal=delayVal*3600000;
        }
        if (delayMetrics.equals("Минуты")) {
            return delayVal=delayVal*60000;
        }
        if (delayMetrics.equals("Секунды")) {
            return delayVal=delayVal*1000;
        }
            return delayVal;
    }
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        runWindowFrame.dispose();
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
