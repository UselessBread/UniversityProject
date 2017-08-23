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
    private ArrayList<String> usingDevices=new ArrayList<>();


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

    void makeWindow() {
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
        for(SystemInfo systemInfo:systemInfoVector){
            if(systemInfo.getDelay().length()==0){
                executingArea.append(systemInfo.getInfoWithoutDelay()+"\n");
            }
            if(systemInfo.getDelay().length()>0&&systemInfo.getRelation().length()>0){
                String[] delay=systemInfo.getDelay().split("/");
                int time=convertToMilliseconds(delay);
                executeDelay(Calendar.MILLISECOND,time,systemInfo.getInfoWithRelation(),systemInfo);
            }
            if(systemInfo.getDelay().length()>0&&systemInfo.getRelation().length()==0){
                String delay[]=systemInfo.getDelay().split("/");
                int time=convertToMilliseconds(delay);
                executeDelay(Calendar.MILLISECOND,time,systemInfo.getInfoWithoutRelation()+" ",systemInfo);
            }
        }

    }
    //Сделать варианты для большого количества часов\минут\секунд(как с миллисекундами)
    private void executeDelay(int calendarConstant,int timeValue,String deviceInfo,SystemInfo systemInfo) {
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
            if(usingDevices.contains(systemInfo.getInfoWithoutDelayAndMode())){
                int index=usingDevices.indexOf(systemInfo.getInfoWithoutDelayAndMode());
                SystemInfo usedDevice=systemInfoVector.get(index);
                String[] usingMode=usedDevice.getMode().split("\t");
                String[] currentMode=systemInfo.getMode().split("\t");
                if(currentMode[0].equals("ВЫКЛ")){
                    //replace with container
                    double prevResourceUsage1=Double.parseDouble(usingMode[1]);
                    double prevResourceUsage2=Double.parseDouble(usingMode[2]);
                    double prevResourceUsage3=Double.parseDouble(usingMode[3]);
                    String resources=resourceMonitor.getText();
                    String[] tempRes=resources.split("\t");
                    double currentResourceUsage1=Double.parseDouble(tempRes[0].split("/")[0]);
                    double currentResourceUsage2=Double.parseDouble(tempRes[1].split("/")[0]);
                    double currentResourceUsage3=Double.parseDouble(tempRes[2].split("/")[0]);
                    if(currentResourceUsage1!=0)
                        currentResourceUsage1=currentResourceUsage1-prevResourceUsage1;
                    if(currentResourceUsage2!=0)
                        currentResourceUsage2=currentResourceUsage2-prevResourceUsage2;
                    if(currentResourceUsage3!=0)
                        currentResourceUsage3=currentResourceUsage3-prevResourceUsage3;
                    resourceMonitor.setText(currentResourceUsage1+"/"+allResources.get(0)+"\t"+
                            currentResourceUsage2+"/"+allResources.get(1)+"\t"+
                            currentResourceUsage3+"/"+allResources.get(2));
                }
            }
            String usingDevice=systemInfo.getInfoWithoutDelayAndMode();
            usingDevices.add(usingDevice);
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
            return delayVal*3600000;
        }
        if (delayMetrics.equals("Минуты")) {
            return delayVal*60000;
        }
        if (delayMetrics.equals("Секунды")) {
            return delayVal*1000;
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
