package com.company.DataTypes;

import java.util.Vector;

public class ArticleBackup {

    private String openedAlgorithmsContent;
    private Vector<SystemInfo> systemInfoVector;
    public ArticleBackup(String openedAlgorithmsContent,Vector<SystemInfo> systemInfoVector){
        this.openedAlgorithmsContent=openedAlgorithmsContent;
        this.systemInfoVector=systemInfoVector;
    }
    public ArticleBackup(){
        openedAlgorithmsContent="";
        systemInfoVector=new Vector<>();
    }

    public String getOpenedAlgorithmsContent() {
        return openedAlgorithmsContent;
    }

    public Vector<SystemInfo> getSystemInfoVector() {
        return systemInfoVector;
    }

    public void setOpenedAlgorithmsContent(String openedAlgorithmsContent) {
        this.openedAlgorithmsContent = openedAlgorithmsContent;
    }

    public void setSystemInfoVector(Vector<SystemInfo> systemInfoVector) {
        this.systemInfoVector = systemInfoVector;
    }
}
