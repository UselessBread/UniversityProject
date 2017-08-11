package com.company;

/**
 * Created by Игорь on 08.08.2017.
 */
import java.sql.*;
import java.util.Vector;

public class DB {
    public static  final int CLASS_NOT_FOUND=-1;
    public static  final int SQL_EXCEPTION=-2;
    public static  final int CLASS_CAST_EXCEPTION=-3;
    public static  final int OK=2;
    private int subsystemNumber,deviceNumber,sensorNumber,deviceOrSensorNumber;
    private Connection connection = null;
    private Statement statement = null;
    ResultSet result;
    public int state;

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
        } catch (java.lang.ClassNotFoundException ex) {
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

            if(resultInt==DB.CLASS_NOT_FOUND){
                return  CLASS_NOT_FOUND;
            }
            else if(resultInt==DB.SQL_EXCEPTION){
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
    public Vector<String> queryToDeviceOrSensor(String result){
        boolean device=false;
        boolean sensor=false;
        Vector<String> stringVector=new Vector<>();
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
            try {
                if(device) {
                    while (resultSet.next()) {
                        //stringVector.add(resultSet.getString("idприбор"));
                        stringVector.add(resultSet.getString("idрежима"));
                        stringVector.add(resultSet.getString("потребление_ресурса1"));
                        stringVector.add(resultSet.getString("потребление_ресурса2"));
                        stringVector.add(resultSet.getString("потребление_ресурса3"));
                    }
                }
                if(sensor){
                    while (resultSet.next()) {
                        //stringVector.add(resultSet.getString("idдатчик"));
                        stringVector.add(resultSet.getString("idрежима"));
                        stringVector.add(resultSet.getString("потребление_ресурса1"));
                        stringVector.add(resultSet.getString("потребление_ресурса2"));
                        stringVector.add(resultSet.getString("потребление_ресурса3"));
                    }
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

}