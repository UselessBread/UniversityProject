package com.company;

/**
 * Created by Игорь on 08.08.2017.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import static com.company.MainWindow.systemInfoVector;

public class DB {
    static  final int CLASS_NOT_FOUND=-11;
    static  final int SQL_EXCEPTION=-12;
    static  final int CLASS_CAST_EXCEPTION=-13;
    static  final int OK=12;
    private int subsystemNumber,deviceOrSensorNumber;
    private Connection connection = null;
    private Statement statement = null;
    Savepoint savepoint;
    ResultSet result;
    int state;

    public DB() {
        String url = "jdbc:mysql://127.0.0.1:3306/ка";
        String user = "root";
        String password = "5986";
        state=connect(url,user,password);
    }

    public int connect(String url,String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (ClassNotFoundException ex) {
           return CLASS_NOT_FOUND;
        } catch (SQLException SQLExc) {
            return SQL_EXCEPTION;
        }
        return OK;
    }
    public Object execQuery(String query){
        try {
           return statement.executeQuery(query);
        }catch (SQLException exc){return SQL_EXCEPTION;}
    }
    public Vector<String> firstQuery(){
        Vector<String> sVec=new Vector<>();
        String query="SELECT * FROM подсистемы";
        Object resultObject=execQuery(query);
        if(verifyResult(resultObject)==OK){
            try{
                ResultSet resultSet=(ResultSet)resultObject;
                while(resultSet.next()){
                    sVec.add("Подсистема"+resultSet.getString("idподсистемы"));
                }
            }catch (SQLException SQLexc){
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        }
        else if(verifyResult(resultObject)==CLASS_NOT_FOUND){
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        }
        else if(verifyResult(resultObject)==SQL_EXCEPTION){
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        }
        else if(verifyResult(resultObject)==CLASS_CAST_EXCEPTION){
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        return sVec;
    }
    private int verifyResult(Object resultObject){
        try {
            int resultInt = (int) resultObject;

            if(resultInt== DB.CLASS_NOT_FOUND){
                return  CLASS_NOT_FOUND;
            }
            else if(resultInt== DB.SQL_EXCEPTION){
                return  SQL_EXCEPTION;
            }
            return OK;
        }catch (ClassCastException classCastExc){
            return OK;
        }
    }
    public Vector<String> queryToPodsys(String selectedItem) {
        Vector<String> stringVector=new Vector<>();
        subsystemNumber = Integer.parseInt(selectedItem);
        String query = "SELECT подсистема_"+ subsystemNumber + ".idприбора,подсистема_"+ subsystemNumber + ".idдатчика\n" +
                "FROM подсистемы\n" +
                "INNER JOIN подсистема_"+ subsystemNumber + " ON подсистемы.idподсистемы=подсистема_"+ subsystemNumber + ".idподсистема\n" ;
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject)==OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    stringVector.add("Прибор"+resultSet.getString("idприбора"));
                    stringVector.add("Датчик"+resultSet.getString("idдатчика"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        }
        else if(verifyResult(resultObject)==CLASS_NOT_FOUND){
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        }
        else if(verifyResult(resultObject)==SQL_EXCEPTION){
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        }
        else if(verifyResult(resultObject)==CLASS_CAST_EXCEPTION){
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }
    public Vector<Vector<String>> queryToDeviceOrSensor(String result){
        boolean device=false;
        boolean sensor=false;
        Vector<String> vectorForList=new Vector<>();
        Vector<String> vectorForButtons=new Vector<>();
        Vector<Vector<String>> resultVec=new Vector<>();
        Object resultObject;
        String end=result.substring(result.length()-1);
       deviceOrSensorNumber=Integer.parseInt(end);
        String queryToDevice="SELECT режимы_прибор_"+deviceOrSensorNumber+".idрежима,режимы_прибор_"+deviceOrSensorNumber+".потребление_ресурса1,режимы_прибор_"+deviceOrSensorNumber+".потребление_ресурса2,режимы_прибор_"+deviceOrSensorNumber+".потребление_ресурса3\n" +
                "FROM подсистемы\n" +
                "INNER JOIN подсистема_"+ subsystemNumber + " ON подсистемы.idподсистемы=подсистема_"+ subsystemNumber + ".idподсистема \n" +
                "INNER JOIN приборы ON подсистема_"+ subsystemNumber + ".idприбора=приборы.idприборы\n" +
                "INNER JOIN датчики ON подсистема_"+ subsystemNumber + ".idдатчика=датчики.idдатчики\n" +
                "INNER JOIN прибор_"+deviceOrSensorNumber+" ON приборы.idприбор_for=прибор_"+deviceOrSensorNumber+".idприбор\n"+
                "INNER JOIN режимы_прибор_"+deviceOrSensorNumber+" ON прибор_"+deviceOrSensorNumber+".режимы=режимы_прибор_"+deviceOrSensorNumber+".idрежима;";

        String queryToSensor="SELECT режимы_датчик_"+deviceOrSensorNumber+".idрежима,режимы_датчик_"+deviceOrSensorNumber+".потребление_ресурса1,режимы_датчик_"+deviceOrSensorNumber+".потребление_ресурса2,режимы_датчик_"+deviceOrSensorNumber+".потребление_ресурса3\n" +
                "FROM подсистемы\n" +
                "INNER JOIN подсистема_"+ subsystemNumber + " ON подсистемы.idподсистемы=подсистема_"+ subsystemNumber + ".idподсистема \n" +
                "INNER JOIN приборы ON подсистема_"+ subsystemNumber + ".idприбора=приборы.idприборы\n" +
                "INNER JOIN датчики ON подсистема_"+ subsystemNumber + ".idдатчика=датчики.idдатчики\n" +
                "INNER JOIN датчик_"+deviceOrSensorNumber+" ON датчики.название=датчик_"+deviceOrSensorNumber+".idдатчик\n"+
                "INNER JOIN режимы_датчик_"+deviceOrSensorNumber+" ON датчик_"+deviceOrSensorNumber+".режимы=режимы_датчик_"+deviceOrSensorNumber+".idрежима;";
        if(result.contains("Прибор")){
            device=true;
            resultObject=execQuery(queryToDevice);
        }
        else if(result.contains("Датчик")){
            sensor=true;
            resultObject=execQuery(queryToSensor);
        }
        else{
            //replace later
            resultObject=SQL_EXCEPTION;
        }
        if (verifyResult(resultObject)==OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            vectorForList.add("Режим");
            vectorForList.add("Потребление ресурса 1");
            vectorForList.add("Потребление ресурса 2");
            vectorForList.add("Потребление ресурса 3");
            resultVec.add(vectorForList);
            try {
                if(device) {
                    while (resultSet.next()) {
                        //stringVector.add(resultSet.getString("idприбор"));
                        vectorForButtons.add(resultSet.getString("idрежима")+"\t"+resultSet.getString("потребление_ресурса1")+"\t"+
                        resultSet.getString("потребление_ресурса2")+"\t"+resultSet.getString("потребление_ресурса3"));
                    }
                }
                if(sensor){
                    while (resultSet.next()) {
                        //stringVector.add(resultSet.getString("idдатчик"));
                        vectorForButtons.add(resultSet.getString("idрежима")+"\t"+resultSet.getString("потребление_ресурса1")+"\t"+
                        resultSet.getString("потребление_ресурса2")+"\t"+resultSet.getString("потребление_ресурса3"));
                    }
                }
            } catch (SQLException SQLexc) {
                vectorForButtons.add(Integer.toString(SQL_EXCEPTION));
                resultVec.add(vectorForButtons);
                return resultVec;
            }
        }
        else if(verifyResult(resultObject)==CLASS_NOT_FOUND){
            vectorForButtons.add(Integer.toString(CLASS_NOT_FOUND));
            resultVec.add(vectorForButtons);
            return resultVec;
        }
        else if(verifyResult(resultObject)==SQL_EXCEPTION){
            vectorForButtons.add(Integer.toString(SQL_EXCEPTION));
            resultVec.add(vectorForButtons);
            return resultVec;
        }
        else if(verifyResult(resultObject)==CLASS_CAST_EXCEPTION){
            vectorForButtons.add(Integer.toString(CLASS_CAST_EXCEPTION));
            resultVec.add(vectorForButtons);
            return resultVec;
        }
        resultVec.add(vectorForButtons);
        return resultVec;
    }
    int execUpdate(String query) {
        try {
            savepoint=connection.setSavepoint();
            connection.setAutoCommit(false);
            int result= statement.executeUpdate(query);
            connection.commit();
            return result;
        } catch (SQLException exc) {
            try {
                connection.rollback(savepoint);
            }catch (SQLException rollbackExc){
                return SQL_EXCEPTION;
            }
            return SQL_EXCEPTION;
        }
    }
    int saveToDB(String name,Vector<SystemInfo> systemInfoVector){
        String query="CREATE TABLE `ка`.`"+name+"` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `подсистема` VARCHAR(45) NULL,\n" +
                "  `устройство` VARCHAR(45) NULL,\n" +
                "  `режим` VARCHAR(45) NULL,\n" +
                "  `задержка` VARCHAR(45) NULL,\n" +
                "  `отношение` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE INDEX `idname_UNIQUE` (`id` ASC))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8;\n";
        int result=execUpdate(query);

        int verRes=verifyResult(result);
        if(verRes==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(verRes==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(verRes==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        for(SystemInfo systemInfo:systemInfoVector){

            String insertQuery="INSERT INTO `ка`.`"+name+"` (`подсистема`,`устройство`,`режим`,`задержка`,`отношение`)\n"+
                    "VALUES('"+systemInfo.getSubsystem()+"','"+systemInfo.getDeviceName()+"','"+systemInfo.getMode()+"','"+systemInfo.getDelay()+"','"+systemInfo.getRelation()+"');";
            result=execUpdate(insertQuery);
            verRes=verifyResult(result);
            if(verRes==CLASS_NOT_FOUND) {
                return CLASS_NOT_FOUND;
            }
            if(verRes==SQL_EXCEPTION) {
                return SQL_EXCEPTION;
            }
            if(verRes==CLASS_CAST_EXCEPTION) {
                return CLASS_CAST_EXCEPTION;
            }

        }
        return OK;
    }
    Object openQuery(String name){
        String query="SELECT * FROM `ка`.`"+name+"`;";
        Object resultObject=execQuery(query);
        if (verifyResult(resultObject)==OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try{
                while (resultSet.next()){
                    String subsystem=resultSet.getString("подсистема")+" ";
                    String deviceName=resultSet.getString("устройство")+" ";
                    String mode=resultSet.getString("режим");
                    String delay=resultSet.getString("задержка");
                    String relation=resultSet.getString("отношение");
                    if (relation.length()==0)
                        relation="";
                    SystemInfo systemInfo=new SystemInfo(subsystem,deviceName,mode,delay,relation);
                    systemInfoVector.add(systemInfo);
                }
            }catch(SQLException e){}
        }
        else if(verifyResult(resultObject)==CLASS_NOT_FOUND){
            return CLASS_NOT_FOUND;
        }
        else if(verifyResult(resultObject)==SQL_EXCEPTION){
            return SQL_EXCEPTION;
        }
        else if(verifyResult(resultObject)==CLASS_CAST_EXCEPTION){
            return CLASS_CAST_EXCEPTION;
        }
        return systemInfoVector;
    }
}