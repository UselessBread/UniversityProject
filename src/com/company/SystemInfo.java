package com.company;

public class SystemInfo {
    private String article;
    private String subsystem;
    private String deviceName;
    private String mode;
    private String delay;
    private String relation;
    String getArticle(){return article;}
    String getSubsystem(){
        return subsystem;
    }
    String getDeviceName(){
        return deviceName;
    }
    String getMode(){
        return mode;
    }
    String getDelay(){
        return delay;
    }
    String getRelation(){
        return relation;
    }
    SystemInfo(){
        this.article="";
        this.subsystem="";
        this.deviceName="";
        this.mode="";
        this.delay="";
        this.relation="";
    }
    SystemInfo(String str){
        String[] splittedStr=str.split(" ");
        article=splittedStr[0];
        subsystem=splittedStr[1];
        deviceName=splittedStr[2];
        mode = splittedStr[3];
        if(splittedStr.length==6) {
            delay = splittedStr[4];
            relation = splittedStr[5];
        }
        else if(splittedStr.length==5){
            delay=splittedStr[4];
            relation="";
        }
        else if(splittedStr.length==4){
            delay="";
            relation="";
        }

    }
    SystemInfo(String article,String subsystem,String deviceName,String mode,String delay,String relation){
        this.article=article;
        this.subsystem=subsystem;
        this.deviceName=deviceName;
        this.mode=mode;
        this.delay=delay;
        this.relation=relation;
    }
    void setAll(String article,String subsystem,String deviceName,String mode,String delay,String relation){
        this.article=article;
        this.subsystem=subsystem;
        this.deviceName=deviceName;
        this.mode=mode;
        this.delay=delay;
        this.relation=relation;
    }
    String getInfoWithoutDelay(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName()+" "+getMode();
    }
    String getInfoWithoutDelayAndMode(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName();
    }

    String getAllInfo(){
        return getArticle()+" "+ getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay()+" "+getRelation();
    }
    String getInfoWithRelation(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay()+" После "+getRelation();
    }
    String getInfoWithoutRelation(){
        return getArticle()+" "+getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay();
    }
}
