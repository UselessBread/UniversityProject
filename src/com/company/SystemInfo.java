package com.company;

public class SystemInfo {
    private String subsystem;
    private String deviceName;
    private String mode;
    private String delay;
    private String relation;
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
        this.subsystem="";
        this.deviceName="";
        this.mode="";
        this.delay="";
        this.relation="";
    }
    SystemInfo(String subsystem,String deviceName,String mode,String delay,String relation){
        this.subsystem=subsystem;
        this.deviceName=deviceName;
        this.mode=mode;
        this.delay=delay;
        this.relation=relation;
    }
    void setAll(String subsystem,String deviceName,String mode,String delay,String relation){
        this.subsystem=subsystem;
        this.deviceName=deviceName;
        this.mode=mode;
        this.delay=delay;
        this.relation=relation;
    }
    String getInfoWithoutDelay(){
        return getSubsystem()+" "+getDeviceName()+" "+getMode();
    }
    String getAllInfo(){
        return getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay()+" "+getRelation();
    }
    String getInfoWithRelation(){
        return getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay()+" После "+getRelation();
    }
    String getInfoWithoutRelation(){
        return getSubsystem()+" "+getDeviceName()+" "+getMode()+" "+getDelay();
    }
}
