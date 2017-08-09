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
        int podsysNumber = Integer.parseInt(selectedItem);
        String query = "SELECT подсистема_1.idприбора,подсистема_1.idдатчика\n" +
                "FROM подсистемы\n" +
                "INNER JOIN подсистема_1 ON подсистемы.idподсистемы=подсистема_1.idподсистема_1\n" +
                "WHERE idподсистема_1="+ podsysNumber + ";"; //+ podsysNumber + ";";
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
}