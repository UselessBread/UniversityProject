package com.company;

/**
 * Created by Игорь on 08.08.2017.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;


public class DB {
    static final int CLASS_NOT_FOUND = -11;
    static final int SQL_EXCEPTION = -12;
    static final int CLASS_CAST_EXCEPTION = -13;
    static final int OK = 12;
    private final String url = "jdbc:mysql://127.0.0.1:3306/ка";
    private final String user = "root";
    private final String password = "5986";
    private Connection connection = null;
    private Statement statement = null;
    private Savepoint savepoint;
    int state;

    DB() {
        state = connect(url, user, password);
    }

    private int connect(String url, String user, String password) {
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

    public int connect() {
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

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Object execQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException exc) {
            return SQL_EXCEPTION;
        }
    }

    public Vector<String> firstQuery() {
        Vector<String> sVec = new Vector<>();
        String query = "SELECT * FROM подсистемы";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    sVec.add("Подсистема" + resultSet.getString("idподсистемы"));
                }
            } catch (SQLException SQLexc) {
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        return sVec;
    }

    private int verifyResult(Object resultObject) {
        try {
            int resultInt = (int) resultObject;

            if (resultInt == DB.CLASS_NOT_FOUND) {
                return CLASS_NOT_FOUND;
            } else if (resultInt == DB.SQL_EXCEPTION) {
                return SQL_EXCEPTION;
            }
            return OK;
        } catch (ClassCastException classCastExc) {
            return OK;
        }
    }

    //article=изделие_№, selectedItem-выбранная подсистема
    Vector<String> queryToSubsys(String article, String selectedItem) {
        Vector<String> stringVector = new Vector<>();
        String query = "SELECT название,idприбор_for\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + selectedItem + "_" + article + " on " + article + ".подсистемы=" + selectedItem + "_" + article + ".idподсистема\n" +
                "left join приборы_" + article + "_" + selectedItem + " on " + selectedItem + "_" + article + ".idприбора=приборы_" + article + "_" + selectedItem + ".idприборы\n" +
                "left join датчики_" + article + "_" + selectedItem + " on " + selectedItem + "_" + article + ".idдатчика=датчики_" + article + "_" + selectedItem + ".idдатчики";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("название"));
                    stringVector.add(resultSet.getString("idприбор_for"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> queryToSubsys(String article, String selectedItem, ArrayList<Integer> lastIndex) {
        Vector<String> stringVector = new Vector<>();
        String query = "SELECT название,idприбор_for\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + selectedItem + "_" + article + " on " + article + ".подсистемы=" + selectedItem + "_" + article + ".idподсистема\n" +
                "inner join приборы_" + article + "_" + selectedItem + " on " + selectedItem + "_" + article + ".idприбора=приборы_" + article + "_" + selectedItem + ".idприборы\n" +
                "inner join датчики_" + article + "_" + selectedItem + " on " + selectedItem + "_" + article + ".idдатчика=датчики_" + article + "_" + selectedItem + ".idдатчики";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("название"));
                    stringVector.add(resultSet.getString("idприбор_for"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        String lasIndexQuery = "SELECT idприбора,idдатчика FROM `ка`.`" + selectedItem + "_" + article + "`;";
        resultObject = execQuery(lasIndexQuery);
        int device = 0, sensor = 0;
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    if (resultSet.getInt("idприбора") != 0) {
                        device = resultSet.getInt("idприбора");
                    }
                    if (resultSet.getInt("idдатчика") != 0) {
                        sensor = resultSet.getInt("idдатчика");
                    }
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            lastIndex.add(device);
            lastIndex.add(sensor);
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> queryToArticles() {
        String query = "SELECT * FROM изделия;";
        Vector<String> sVec = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    sVec.add(resultSet.getString("имя_изделия"));
                }
            } catch (SQLException SQLexc) {
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        return sVec;
    }

    Vector<String> queryToArticles(ArrayList<Integer> lastIndex) {
        String query = "SELECT * FROM изделия;";
        Vector<String> sVec = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    sVec.add(resultSet.getString("имя_изделия"));
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                sVec.add(Integer.toString(SQL_EXCEPTION));
                return sVec;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            sVec.add(Integer.toString(CLASS_NOT_FOUND));
            return sVec;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            sVec.add(Integer.toString(SQL_EXCEPTION));
            return sVec;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            sVec.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return sVec;
        }
        return sVec;
    }

    Vector<String> queryToArticle(String prevQueryResult) {
        String query = "SELECT подсистемы\n" +
                "FROM ка.изделия\n" +
                "inner join " + prevQueryResult + " on изделия.имя_изделия=" + prevQueryResult + ".имя;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("подсистемы"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;

    }

    Vector<String> queryToArticle(String prevQueryResult, ArrayList<Integer> lastIndex) {
        String query = "SELECT " + prevQueryResult + ".id,подсистемы\n" +
                "FROM ка.изделия\n" +
                "inner join " + prevQueryResult + " on изделия.имя_изделия=" + prevQueryResult + ".имя;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("подсистемы"));
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            if (lastIndex.size() == 0) {
                lastIndex.add(0);
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;

    }

    //selectedItem- имя выбранного прибора
    Vector<String> queryToDevice(String article, String subsystem, String selectedItem) {
        String query = "SELECT idрежима,потребление_ресурса1,потребление_ресурса2,потребление_ресурса3\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + subsystem + "_" + article + " on " + article + ".подсистемы=" + subsystem + "_" + article + ".idподсистема\n" +
                "left join приборы_" + article + "_" + subsystem + " on " + subsystem + "_" + article + ".idприбора=приборы_" + article + "_" + subsystem + ".idприборы\n" +
                "inner join " + selectedItem + "_" + article + "_" + subsystem + " on приборы_" + article + "_" + subsystem + ".idприбор_for=" + selectedItem + "_" + article + "_" + subsystem + ".idприбор\n" +
                "inner join режимы_" + selectedItem + "_" + article + "_" + subsystem + " on " + selectedItem + "_" + article + "_" + subsystem + ".режимы=режимы_" + selectedItem + "_" + article + "_" + subsystem + ".idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("idрежима") + "\t" + resultSet.getString("потребление_ресурса1") + "\t" +
                            resultSet.getString("потребление_ресурса2") + "\t" + resultSet.getString("потребление_ресурса3"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> queryToDevice(String article, String subsystem, String selectedItem, ArrayList<Integer> lastIndex) {
        String query = "SELECT `" + selectedItem + "_" + article + "_" + subsystem + "`.`id`,idрежима,потребление_ресурса1,потребление_ресурса2,потребление_ресурса3\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + subsystem + "_" + article + " on " + article + ".подсистемы=" + subsystem + "_" + article + ".idподсистема\n" +
                "left join приборы_" + article + "_" + subsystem + " on " + subsystem + "_" + article + ".idприбора=приборы_" + article + "_" + subsystem + ".idприборы\n" +
                "inner join " + selectedItem + "_" + article + "_" + subsystem + " on приборы_" + article + "_" + subsystem + ".idприбор_for=" + selectedItem + "_" + article + "_" + subsystem + ".idприбор\n" +
                "inner join режимы_" + selectedItem + "_" + article + "_" + subsystem + " on " + selectedItem + "_" + article + "_" + subsystem + ".режимы=режимы_" + selectedItem + "_" + article + "_" + subsystem + ".idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("idрежима") + "\t" + resultSet.getString("потребление_ресурса1") + "\t" +
                            resultSet.getString("потребление_ресурса2") + "\t" + resultSet.getString("потребление_ресурса3"));
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            if (lastIndex.size() == 0) {
                lastIndex.add(0);
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    //selectedItem- имя выбранного датчика
    Vector<String> queryToSensor(String article, String subsystem, String selectedItem) {
        String query = "SELECT idрежима,потребление_ресурса1,потребление_ресурса2,потребление_ресурса3\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + subsystem + "_" + article + " on " + article + ".подсистемы=" + subsystem + "_" + article + ".idподсистема\n" +
                "left join датчики_" + article + "_" + subsystem + " on " + subsystem + "_" + article + ".idдатчика=датчики_" + article + "_" + subsystem + ".idдатчики\n" +
                "inner join " + selectedItem + "_" + article + "_" + subsystem + " on датчики_" + article + "_" + subsystem + ".название=" + selectedItem + "_" + article + "_" + subsystem + ".idдатчик\n" +
                "inner join режимы_" + selectedItem + "_" + article + "_" + subsystem + " on " + selectedItem + "_" + article + "_" + subsystem + ".режимы=режимы_" + selectedItem + "_" + article + "_" + subsystem + ".idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("idрежима") + "\t" + resultSet.getString("потребление_ресурса1") + "\t" +
                            resultSet.getString("потребление_ресурса2") + "\t" + resultSet.getString("потребление_ресурса3"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> queryToSensor(String article, String subsystem, String selectedItem, ArrayList<Integer> lastIndex) {
        String query = "SELECT `" + selectedItem + "_" + article + "_" + subsystem + "`.`id`,idрежима,потребление_ресурса1,потребление_ресурса2,потребление_ресурса3\n" +
                "FROM ка.изделия\n" +
                "inner join " + article + " on изделия.имя_изделия=" + article + ".имя\n" +
                "inner join " + subsystem + "_" + article + " on " + article + ".подсистемы=" + subsystem + "_" + article + ".idподсистема\n" +
                "left join датчики_" + article + "_" + subsystem + " on " + subsystem + "_" + article + ".idдатчика=датчики_" + article + "_" + subsystem + ".idдатчики\n" +
                "inner join " + selectedItem + "_" + article + "_" + subsystem + " on датчики_" + article + "_" + subsystem + ".название=" + selectedItem + "_" + article + "_" + subsystem + ".idдатчик\n" +
                "inner join режимы_" + selectedItem + "_" + article + "_" + subsystem + " on " + selectedItem + "_" + article + "_" + subsystem + ".режимы=режимы_" + selectedItem + "_" + article + "_" + subsystem + ".idрежима;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("idрежима") + "\t" + resultSet.getString("потребление_ресурса1") + "\t" +
                            resultSet.getString("потребление_ресурса2") + "\t" + resultSet.getString("потребление_ресурса3"));
                    lastIndex.clear();
                    lastIndex.add(resultSet.getInt("id"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
            if (lastIndex.size() == 0) {
                lastIndex.add(0);
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> getDeviceNames(String articleName, String subsystemName) {
        String query = "SELECT idприбор_for\n" +
                "FROM " + subsystemName + "_" + articleName + "\n" +
                "INNER JOIN приборы_" + articleName + "_" + subsystemName + " ON " + subsystemName + "_" + articleName + ".idприбора=приборы_" + articleName + "_" + subsystemName + ".idприборы";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("idприбор_for"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        }
        return stringVector;
    }

    Vector<String> getSensorNames(String articleName, String subsystemName) {
        String query = "SELECT  название\n" +
                "FROM " + subsystemName + "_" + articleName + "\n" +
                "INNER JOIN датчики_" + articleName + "_" + subsystemName + " ON " + subsystemName + "_" + articleName + ".idдатчика=датчики_" + articleName + "_" + subsystemName + ".idдатчики";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("название"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        }
        return stringVector;
    }

    private int execUpdate(String query) {
        try {
            savepoint = connection.setSavepoint();
            connection.setAutoCommit(false);
            int result = statement.executeUpdate(query);
            connection.commit();
            return result;
        } catch (SQLException exc) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException rollbackExc) {
                return SQL_EXCEPTION;
            }
            return SQL_EXCEPTION;
        }
    }

    int saveToDB(String article, String name, Vector<SystemInfo> systemInfoVector) {
        String query = "CREATE TABLE `ка`.`" + name + "_" + article + "` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `изделие` VARCHAR(45) NULL,\n" +
                "  `подсистема` VARCHAR(45) NULL,\n" +
                "  `устройство` VARCHAR(45) NULL,\n" +
                "  `режим` VARCHAR(45) NULL,\n" +
                "  `задержка` VARCHAR(45) NULL,\n" +
                "  `отношение` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE INDEX `idname_UNIQUE` (`id` ASC))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8;\n";
        String addQuery = "INSERT INTO " + article + "_алгоритмы(`имя_алгоритма`) VALUES('" + name + "');";
        int result1 = execUpdate(addQuery);
        int result = execUpdate(query);

        int verRes = verifyResult(result);
        int verRes1 = verifyResult(result1);
        if (verRes == CLASS_NOT_FOUND || verRes1 == CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if (verRes == SQL_EXCEPTION || verRes1 == SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if (verRes == CLASS_CAST_EXCEPTION || verRes1 == CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        for (SystemInfo systemInfo : systemInfoVector) {

            String insertQuery = "INSERT INTO `ка`.`" + name + "_" + article + "` (`изделие`,`подсистема`,`устройство`,`режим`,`задержка`,`отношение`)\n" +
                    "VALUES('" + systemInfo.getArticle() + "','" + systemInfo.getSubsystem() + "','" + systemInfo.getDeviceName() + "','" + systemInfo.getMode() + "','" + systemInfo.getDelay() + "','" + systemInfo.getRelation() + "');";
            result = execUpdate(insertQuery);
            verRes = verifyResult(result);
            if (verRes == CLASS_NOT_FOUND) {
                return CLASS_NOT_FOUND;
            }
            if (verRes == SQL_EXCEPTION) {
                return SQL_EXCEPTION;
            }
            if (verRes == CLASS_CAST_EXCEPTION) {
                return CLASS_CAST_EXCEPTION;
            }

        }
        return OK;
    }

    Object openQuery(String name) {
        String query = "SELECT * FROM `ка`.`" + name + "`;";
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            ResultSet resultSet = (ResultSet) resultObject;
            try {
                while (resultSet.next()) {
                    String article = resultSet.getString("изделие") + " ";
                    String subsystem = resultSet.getString("подсистема") + " ";
                    String deviceName = resultSet.getString("устройство") + " ";
                    String mode = resultSet.getString("режим");
                    String delay = resultSet.getString("задержка");
                    String relation = resultSet.getString("отношение");
                    if (relation.length() == 0)
                        relation = "";
                    SystemInfo systemInfo = new SystemInfo(article, subsystem, deviceName, mode, delay, relation);
                    MainWindow.getSystemInfoVector().add(systemInfo);
                }
            } catch (SQLException e) {
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return MainWindow.getSystemInfoVector();
    }

    ArrayList<Double> getAllResources() {
        ArrayList<Double> resultList = new ArrayList<>();
        String query = "SELECT * FROM `ка`.`ресурсы`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add((double) CLASS_NOT_FOUND);
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add((double) SQL_EXCEPTION);
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add((double) CLASS_CAST_EXCEPTION);
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                resultList.add(Double.parseDouble(resultSet.getString("ресурс_1")));
                resultList.add(Double.parseDouble(resultSet.getString("ресурс_2")));
                resultList.add(Double.parseDouble(resultSet.getString("ресурс_3")));
            }
        } catch (SQLException sqlE) {
            resultList.add((double) SQL_EXCEPTION);
            return resultList;
        }
        return resultList;
    }

    Vector<Vector<String>> getResourcesCountAndNamesAndMaxValue(String articleName) {
        Vector<String> resourceVector = new Vector<>();
        Vector<String> vectorOfNames = new Vector<>();
        Vector<Vector<String>> resultVector = new Vector<>();

        String query = "SELECT * FROM `ка`.`ресурсы`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            vectorOfNames.add(Integer.toString(CLASS_NOT_FOUND));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            vectorOfNames.add(Integer.toString(CLASS_CAST_EXCEPTION));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        if (test == SQL_EXCEPTION) {
            vectorOfNames.add(Integer.toString(SQL_EXCEPTION));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                resourceVector.add(resultSet.getString("ресурс_1"));
                resourceVector.add(resultSet.getString("ресурс_2"));
                resourceVector.add(resultSet.getString("ресурс_3"));
            }
        } catch (SQLException SQLexc) {
            vectorOfNames.clear();
            vectorOfNames.add(Integer.toString(SQL_EXCEPTION));
            resultVector.add(vectorOfNames);
            resultVector.add(resourceVector);
            return resultVector;
        }
        vectorOfNames.add("ресурс_1");
        vectorOfNames.add("ресурс_2");
        vectorOfNames.add("ресурс_3");
        resultVector.add(vectorOfNames);
        resultVector.add(resourceVector);
        return resultVector;
    }

    Vector<String> getModeNames() {
        Vector<String> modeNames = new Vector<>();
        String query = "SELECT * FROM `ка`.`ресурсы`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_CAST_EXCEPTION) {
        }
        if (test == CLASS_NOT_FOUND) {
        }
        if (test == SQL_EXCEPTION) {
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                modeNames.add(resultSet.getString("idрежима"));
            }
        } catch (SQLException ex) {
        }
        return modeNames;
    }

    Vector<String> queryToAlgorithms(String article) {
        String query = "SELECT * FROM " + article + "_алгоритмы";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("имя_алгоритма"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> getAlgorithmInfo(String article, String algorithmName) {
        String query = "SELECT * FROM `ка`.`" + algorithmName + "_" + article + "`;";
        Vector<String> stringVector = new Vector<>();
        Object resultObject = execQuery(query);
        if (verifyResult(resultObject) == OK) {
            try {
                ResultSet resultSet = (ResultSet) resultObject;
                while (resultSet.next()) {
                    stringVector.add(resultSet.getString("изделие") +
                            resultSet.getString("подсистема") +
                            resultSet.getString("устройство") +
                            resultSet.getString("режим") + " " +
                            resultSet.getString("задержка") + " " +
                            resultSet.getString("отношение"));
                }
            } catch (SQLException SQLexc) {
                stringVector.add(Integer.toString(SQL_EXCEPTION));
                return stringVector;
            }
        } else if (verifyResult(resultObject) == CLASS_NOT_FOUND) {
            stringVector.add(Integer.toString(CLASS_NOT_FOUND));
            return stringVector;
        } else if (verifyResult(resultObject) == SQL_EXCEPTION) {
            stringVector.add(Integer.toString(SQL_EXCEPTION));
            return stringVector;
        } else if (verifyResult(resultObject) == CLASS_CAST_EXCEPTION) {
            stringVector.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return stringVector;
        }
        return stringVector;
    }

    Vector<String> getArticleResources(String articleName) {
        String query = "SELECT * FROM `ка`.`" + articleName + "_ресурсы`";
        Vector<String> resultList = new Vector<>();
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add(Integer.toString(CLASS_NOT_FOUND));
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                int i = 1;
                String result = resultSet.getString(i);
                while (result != null) {
                    i++;
                    result = resultSet.getString(i);
                    resultList.add(result);
                }
            }
        } catch (SQLException sqlE) {
            //resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        return resultList;

    }

    Vector<String> getArticleResourceNames(String articleName) {
        Vector<String> resultList = new Vector<>();
        String query = "SELECT * FROM `ка`.`" + articleName + "_ресурсы_наименования`";
        Object resultObject = execQuery(query);
        int test = verifyResult(resultObject);
        if (test == CLASS_NOT_FOUND) {
            resultList.add(Integer.toString(CLASS_NOT_FOUND));
            return resultList;
        }
        if (test == SQL_EXCEPTION) {
            resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        if (test == CLASS_CAST_EXCEPTION) {
            resultList.add(Integer.toString(CLASS_CAST_EXCEPTION));
            return resultList;
        }
        ResultSet resultSet = (ResultSet) resultObject;
        try {
            while (resultSet.next()) {
                int i = 1;
                String result = resultSet.getString(i);
                while (result != null) {
                    i++;
                    result = resultSet.getString(i);
                    resultList.add(result);
                }
            }
        } catch (SQLException sqlE) {
            //resultList.add(Integer.toString(SQL_EXCEPTION));
            return resultList;
        }
        return resultList;
    }

    Vector<String> getUsedArticleResourcesIfEmpty(String articleName) {
        Vector<String> resultVector = new Vector<>();
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\Users\\igord\\IdeaProjects\\Prototype v0.3\\src\\com\\company\\used_resources.txt"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.substring(line.indexOf("<"), line.indexOf(">")).equals(articleName)) {
                    line = line.replace("<" + articleName + ">", "");
                    line = line.trim();
                    String[] splittedLine = line.split("\t");
                    resultVector.addAll(Arrays.asList(splittedLine));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultVector;
    }

    String getUsedArticleResources(String articleName) {
        String line = "";
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\Users\\igord\\IdeaProjects\\Prototype v0.3\\src\\com\\company\\used_resources.txt"));
            while ((line = reader.readLine()) != null) {
                if (line.substring(line.indexOf("<"), line.indexOf(">") + 1).equals("<" + articleName + ">")) {
                    line = line.replace("<" + articleName + ">", "");
                    line = line.trim();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    //For ThreadDBUpdater
    int addArticle(String articleName, Integer lastIndex, ArrayList<String> columns, ArrayList<Double> columnsValues, ArrayList<String> valuesMeasurement) {
        lastIndex++;
        String addResourcesQueryString = "";
        String insertResourcesQueryString = "";
        String insertResourcesNamesQueryString = "";
        for (int i = 0; i < columns.size(); i++) {
            addResourcesQueryString += "`ресурс_" + (i+1) + "` varchar(45) DEFAULT NULL,\n";
            if(i!=(columns.size()-1)) {
                insertResourcesQueryString += "'" + columnsValues.get(i) + "',";
                insertResourcesNamesQueryString += "'" + valuesMeasurement.get(i)+"',";
            }
            else{
                insertResourcesQueryString += "'" + columnsValues.get(i) + "'";
                insertResourcesNamesQueryString += "'" + valuesMeasurement.get(i)+"'";
            }
        }
        String createResourcesQuery = " CREATE TABLE `" + articleName + "_ресурсы` (\n" +
                "  `id` int(11) NOT NULL,\n" + addResourcesQueryString +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String insertIntoResourcesQuery = "INSERT INTO `" + articleName + "_ресурсы` VALUES(1," + insertResourcesQueryString + ")";
        String createResourcesNamesQuery = " CREATE TABLE `" + articleName + "_ресурсы_наименования` (\n" +
                "  `id` int(11) NOT NULL,\n" + addResourcesQueryString +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String insertResourcesNamesQuery = "INSERT INTO `" + articleName + "_ресурсы_наименования` VALUES(1," + insertResourcesNamesQueryString + " )";
        String addQuery = "INSERT INTO `ка`.`изделия`(имя_изделия) VALUES('" + articleName + "');";
        String createQuery = " CREATE TABLE `" + articleName + "` (\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  `имя` varchar(45) DEFAULT NULL,\n" +
                "  `подсистемы` varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `id_UNIQUE` (`id`),\n" +
                "  UNIQUE KEY `подсистемы_UNIQUE` (`подсистемы`),\n" +
                "  KEY `FK_имя_изделия` (`имя`),\n" +
                "  CONSTRAINT `FK_имя_изделия_" + articleName + "` FOREIGN KEY (`имя`) REFERENCES `изделия` (`имя_изделия`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";
        String createAlgQuery = " CREATE TABLE `" + articleName + "_алгоритмы` (\n" +
                "  `id_алгоритмы` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `имя_алгоритма` varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id_алгоритмы`),\n" +
                "  UNIQUE KEY `имя алгоритма_UNIQUE` (`имя_алгоритма`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        try {
            savepoint = connection.setSavepoint();
            connection.setAutoCommit(false);
            statement.executeUpdate(createResourcesQuery);
            statement.executeUpdate(createResourcesNamesQuery);
            statement.executeUpdate(insertIntoResourcesQuery);
            statement.executeUpdate(insertResourcesNamesQuery);
            statement.executeUpdate(createQuery);
            statement.executeUpdate(addQuery);
            statement.executeUpdate(createAlgQuery);

            connection.commit();

        } catch (SQLException ex) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException rollbackExc) {
                return SQL_EXCEPTION;
            }
        }
        return OK;
    }

    int addSubsystem(String articleName,String subsystemName,Integer lastIndex){
        //Поставить константы,Запрос на добавление в подсистемы
        //Вычислять индекс самостоятельно
        String addSensorsTable="CREATE TABLE `ка`.`датчики_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idдатчики` int(11) NOT NULL,\n" +
                "  `название` varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (`название`,`idдатчики`),\n" +
                "  UNIQUE KEY `название_UNIQUE` (`название`),\n" +
                "  UNIQUE KEY `idдатчики_UNIQUE` (`idдатчики`),\n" +
                "  CONSTRAINT `FK_idдатчики_"+subsystemName+"_"+articleName+"` FOREIGN KEY (`idдатчики`) REFERENCES `"+subsystemName+"_"+articleName+"` (`idдатчика`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String addDevicesTable="CREATE TABLE `ка`.`приборы_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idприборы` int(11) DEFAULT NULL,\n" +
                "  `idприбор_for` varchar(45) DEFAULT NULL,\n" +
                "  `id` varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idприбор_1_idx` (`idприбор_for`),\n" +
                "  KEY `FK_idприборы_idx` (`idприборы`),\n" +
                "  CONSTRAINT `FK_idприборы_"+subsystemName+"_"+articleName+"` FOREIGN KEY (`idприборы`) REFERENCES `"+subsystemName+"_"+articleName+"` (`idприбора`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";
        String addQuery="INSERT INTO "+articleName+" VALUES ("+lastIndex+1+",'"+articleName+"','"+subsystemName+"');";
        String createQuery=" CREATE TABLE `ка`.`"+subsystemName+"_"+articleName+"` (\n" +
                "  `idподсистема` varchar(45) NOT NULL,\n" +
                "  `idприбора` int(11) DEFAULT NULL,\n" +
                "  `idдатчика` int(11) DEFAULT NULL,\n" +
                "  UNIQUE KEY `idприбора_UNIQUE` (`idприбора`),\n" +
                "  UNIQUE KEY `idдатчика_UNIQUE` (`idдатчика`),\n" +
                "  KEY `FK_idподсистема_idx` (`idподсистема`),\n" +
                "  CONSTRAINT `FK_idподсистема"+articleName+"_"+subsystemName+"` FOREIGN KEY (`idподсистема`) REFERENCES `"+articleName+"` (`подсистемы`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8                           ";
        int resultState=execUpdate(createQuery);
        int resultState1=execUpdate(addQuery);
        int resultState2=execUpdate(addSensorsTable);
        int resultState3=execUpdate(addDevicesTable);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND||resultState2==CLASS_NOT_FOUND||resultState3==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION||resultState2==SQL_EXCEPTION||resultState3==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION||resultState2==CLASS_CAST_EXCEPTION||resultState3==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }
    //lastIndex здесь-индекс устройства
    int addDevice(String articleName,String subsystemName, String deviceName,Integer lastIndex){
        lastIndex++;
        String subsystemAddQuery="INSERT INTO `ка`."+subsystemName+"_"+articleName+" (idподсистема,idприбора) VALUES('"+subsystemName+"',"+lastIndex+");\n";
        String addQuery="INSERT INTO `ка`.приборы_"+articleName+"_"+subsystemName+" VALUES("+lastIndex+",'"+deviceName+"','"+lastIndex+"');";
        String createQuery=" CREATE TABLE `ка`.`"+deviceName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idприбор` varchar(45) DEFAULT NULL,\n" +
                "  `режимы` varchar(45) NOT NULL,\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  PRIMARY KEY (`id`,`режимы`),\n" +
                "  KEY `idрежима_idx` (`режимы`),\n" +
                "  KEY `FK_прибор` (`idприбор`),\n" +
                "  CONSTRAINT `FK_прибор"+articleName+"_"+subsystemName+"_"+deviceName+"` FOREIGN KEY (`idприбор`) REFERENCES `приборы_"+articleName+"_"+subsystemName+"` (`idприбор_for`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String createModeTable=" CREATE TABLE `ка`.`режимы_"+deviceName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idрежима` varchar(45) NOT NULL,\n" +
                "  `потребление_ресурса1` double DEFAULT NULL,\n" +
                "  `потребление_ресурса2` double DEFAULT NULL,\n" +
                "  `потребление_ресурса3` double DEFAULT NULL,\n" +
                "  PRIMARY KEY (`idрежима`),\n" +
                "  UNIQUE KEY `idрежимы_датчик1_UNIQUE` (`idрежима`),\n" +
                "  KEY `FK_idрежима_д1_idx` (`idрежима`),\n" +
                "  CONSTRAINT `FK_idрежимы_датчик1_"+articleName+"_"+subsystemName+"_"+deviceName+"` FOREIGN KEY (`idрежима`) REFERENCES `"+deviceName+"_"+articleName+"_"+subsystemName+"` (`режимы`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        int resultState2=execUpdate(subsystemAddQuery);
        int resultState1=execUpdate(addQuery);
        int resultState=execUpdate(createQuery);
        int resultState3=execUpdate(createModeTable);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND||resultState2==CLASS_NOT_FOUND||resultState3==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION||resultState2==SQL_EXCEPTION||resultState3==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION||resultState2==CLASS_CAST_EXCEPTION||resultState3==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }
    //lastIndex здесь-индекс датчика
    int addSensor(String articleName,String subsystemName, String sensorName,Integer lastIndex){
        lastIndex++;
        String subsystemAddQuery="INSERT INTO `ка`."+subsystemName+"_"+articleName+" (idподсистема,idдатчика) VALUES('"+subsystemName+"',"+lastIndex+");\n";
        String addQuery="INSERT INTO `ка`.датчики_"+articleName+"_"+subsystemName+" VALUES("+lastIndex+",'"+sensorName+"');";
        String createQuery=" CREATE TABLE `ка`.`"+sensorName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "`idдатчик` varchar(45) DEFAULT NULL,\n" +
                "  `режимы` varchar(45) NOT NULL,\n" +
                "  `id` int(11) NOT NULL,\n" +
                "  PRIMARY KEY (`id`,`режимы`),\n" +
                "  UNIQUE KEY `id_UNIQUE` (`id`),\n" +
                "  UNIQUE KEY `режимы_д1_UNIQUE` (`режимы`),\n" +
                "  KEY `FK_название_датчика_idx_"+articleName+"_"+subsystemName+"_"+sensorName+"+` (`idдатчик`),\n"+
                "  CONSTRAINT `FK_прибор"+articleName+"_"+subsystemName+"_"+sensorName+"` FOREIGN KEY (`idдатчик`) REFERENCES `датчики_"+articleName+"_"+subsystemName+"` (`название`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
        String createModeTable=" CREATE TABLE `ка`.`режимы_"+sensorName+"_"+articleName+"_"+subsystemName+"` (\n" +
                "  `idрежима` varchar(45) NOT NULL,\n" +
                "  `потребление_ресурса1` double DEFAULT NULL,\n" +
                "  `потребление_ресурса2` double DEFAULT NULL,\n" +
                "  `потребление_ресурса3` double DEFAULT NULL,\n" +
                "  PRIMARY KEY (`idрежима`),\n" +
                "  UNIQUE KEY `idрежимы_датчик1_UNIQUE` (`idрежима`),\n" +
                "  KEY `FK_idрежима_д1_idx` (`idрежима`),\n" +
                "  CONSTRAINT `FK_idрежимы_датчик1_"+articleName+"_"+subsystemName+"_"+sensorName+"` FOREIGN KEY (`idрежима`) REFERENCES `"+sensorName+"_"+articleName+"_"+subsystemName+"` (`режимы`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

        int resultState2=execUpdate(subsystemAddQuery);
        int resultState1=execUpdate(addQuery);
        int resultState=execUpdate(createQuery);
        int resultState3=execUpdate(createModeTable);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND||resultState2==CLASS_NOT_FOUND||resultState3==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION||resultState2==SQL_EXCEPTION||resultState3==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION||resultState2==CLASS_CAST_EXCEPTION||resultState3==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }

    int addMode(String articleName,String subsystemName,String deviceName,String mode,double modeConsumption1,double modeConsumption2,double modeConsumption3,Integer lastIndex) {
        //create table with modes,add mode to device and add mode to mode table
        lastIndex++;
        String addModeToDeviceQuery="INSERT INTO `ка`."+deviceName+"_"+articleName+"_"+subsystemName+" VALUES ('"+deviceName+"','"+mode+"',"+lastIndex+")";

        String addQuery="INSERT INTO `ка`.`режимы_"+deviceName+"_"+articleName+"_"+subsystemName+"` " +
                "VALUES('"+mode+"',"+modeConsumption1+","+modeConsumption2+","+modeConsumption3+");";
        int resultState1=execUpdate(addModeToDeviceQuery);
        int resultState=execUpdate(addQuery);
        if(resultState==CLASS_NOT_FOUND||resultState1==CLASS_NOT_FOUND) {
            return CLASS_NOT_FOUND;
        }
        if(resultState==SQL_EXCEPTION||resultState1==SQL_EXCEPTION) {
            return SQL_EXCEPTION;
        }
        if(resultState==CLASS_CAST_EXCEPTION||resultState1==CLASS_CAST_EXCEPTION) {
            return CLASS_CAST_EXCEPTION;
        }
        return OK;
    }
}