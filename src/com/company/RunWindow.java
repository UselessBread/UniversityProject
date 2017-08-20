package com.company;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;

import static com.company.AlgorithmMaker.*;
import static com.company.MainWindow.*;


public class RunWindow implements WindowListener{
    private HashMap<HashMap<String,String>,String> deviceAndDelayAndRelativeDevice=new HashMap<>();
    private HashMap<String,String[]> finalDelay=new HashMap<>();
    private JTextArea executingArea;
    JPanel main;


    public RunWindow() {
        //algorithmMakerFrame.setEnabled(false);
        executingArea = new JTextArea(30, 80);
        executingArea.setEditable(false);
        JScrollPane executingAreaScrollPane = new JScrollPane(executingArea);
        main = new JPanel();
        main.setOpaque(true);
        main.add(executingAreaScrollPane);
        JFrame runWindowFrame = new JFrame("Запуск");
        runWindowFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        runWindowFrame.addWindowListener(this);
        runWindowFrame.setContentPane(main);
        runWindowFrame.pack();
        runWindowFrame.setVisible(true);
        if (deviceAndDelayAndRelativeDeviceVector.size() != 0) {
            for (int i = 0; i < MainWindow.deviceAndDelayAndRelativeDeviceVector.size(); ++i) {
                deviceAndDelayAndRelativeDevice = deviceAndDelayAndRelativeDeviceVector.get(i);
                makeWindow();
            }
        }
        else{
            makeWindow();
        }
    }
    private void makeWindow() {
        Set<?> firstKeySet = deviceAndDelayAndRelativeDevice.keySet();
        String setResult = firstKeySet.toString();
        setResult = setResult.replaceAll("[^а-яА-Я_0-9&&[^a-zA-Z_0-9]&&[^=]&&[^,]&&[^\t]&&[^.]&&[^ \\t\\n\\x0B\\f\\r]&&[^/]]", "");
        String[] mapsTemp = setResult.split(",");
        int length = mapsTemp.length;
        int mapsLength = 0;
        String[] maps = new String[length];
        for (String str : mapsTemp) {
            maps[mapsLength] = mapsTemp[length - 1];
            mapsLength++;
            length--;
        }
        ArrayList<String> firstValues = new ArrayList<>();
        ArrayList<Integer> noDelayIndexes = new ArrayList<>();
        for (String str : maps) {
            HashMap<String, String> tempMap = new HashMap<>();
            if (str.endsWith("=")) {
                firstValues.add("NoDelay");
            } else {
                String[] forTempMap = str.split("=");
                forTempMap[0]=forTempMap[0].trim();
                tempMap.put(forTempMap[0], forTempMap[1]);
                String res = deviceAndDelayAndRelativeDevice.get(tempMap);
                firstValues.add(res);
            }
        }
        //reverse(firstValues);
        for (int i = 0; i < firstValues.size(); ++i) {
            String[] temp = maps[i].split("=");
            String[] deviceNameSplit=temp[0].split(" ");
            String deviceName=deviceNameSplit[1];
            if(maps[i].endsWith("=")){
                String[] delay={"0"};
                finalDelay.put(deviceName,delay);
                executingArea.append(temp[0]+"\n");
            }
            else {
                String[] delay = temp[1].split("/");
                String firstValuesResult = firstValues.get(i);
                finalDelay.put(deviceName, delay);
                if (firstValuesResult.equals(" ")) {
                    int timeValue = Integer.parseInt(delay[0]);
                    if (delay[1].equals("Часы")) {
                        executeDelay(Calendar.HOUR, timeValue, temp);
                    }
                    if (delay[1].equals("Минуты")) {
                        executeDelay(Calendar.MINUTE, timeValue, temp);
                    }
                    if (delay[1].equals("Секунды")) {
                        executeDelay(Calendar.SECOND, timeValue, temp);
                    }
                    if (delay[1].equals("Миллисекунды")) {
                        executeDelay(Calendar.MILLISECOND, timeValue, temp);
                    }
                } /*else if (firstValuesResult.equals("NoDelay")) {
                    executingArea.append(temp[0]);
                }*/ else if(!firstValuesResult.equals(" ")&&!firstValuesResult.equals("NoDelay")) {
                    String device = firstValuesResult;
                    String[] relationDevicesDelay = finalDelay.get(device);
                    if(relationDevicesDelay[0].equals("0")){
                        int intDelay = convertToMilliseconds(delay);
                        executeDelay(Calendar.MILLISECOND, intDelay, temp);
                    }
                    else {
                        int intDelay = convertToMilliseconds(delay);
                        //int intRelationDelay = convertToMilliseconds(relationDevicesDelay);
                        //int thisDeviceDelay = intDelay + intRelationDelay;
                        String[] newValue = new String[2];
                        newValue[0] = Integer.toString(intDelay);//this device delay if you want to place summary
                        newValue[1] = "Миллисекунды";
                        finalDelay.replace(deviceName, delay, newValue);
                        executeDelay(Calendar.MILLISECOND, intDelay, temp);
                    }
                }
            }
        }





    }
    //Сделать варианты для большого количества часов\минут\секунд(как с миллисекундами)
    private void executeDelay(int calendarConstant,int timeValue,String[] temp) {
        boolean done = false;
        Calendar calendar = Calendar.getInstance();
        int time = calendar.get(calendarConstant) + timeValue;
        if (calendarConstant == Calendar.MILLISECOND) {
            time = calendar.get(Calendar.HOUR) * 3600000 + calendar.get(Calendar.MINUTE) * 60000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
            int goalTime = time + timeValue;
            while (calendar.get(Calendar.HOUR) * 3600000 + calendar.get(Calendar.MINUTE) * 60000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND) < goalTime) {
                calendar = Calendar.getInstance();
            }
            executingArea.append(temp[0]+"\n");
        }
        else{
                while (!done) {
                    calendar = Calendar.getInstance();
                    if (calendar.get(calendarConstant) >= time) {
                        executingArea.append(temp[0]+"\n");
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
    private void reverse(ArrayList<String> reverseList){
        ArrayList<String> temp=new ArrayList<>(reverseList);
        int size=reverseList.size();
        reverseList.clear();
        for(int i=0;i<temp.size();++i){
            reverseList.add(temp.get(size-1));
            size--;
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
