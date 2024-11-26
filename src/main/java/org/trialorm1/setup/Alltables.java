package org.trialorm1.setup;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import org.trialorm1.SqlConnector;


import java.util.ArrayList;
import java.util.List;

public class Alltables {

    public static Connection connection(){

        Connection connection = SqlConnector.getConnection();

        return connection;

    }

    private static String[] allTables(){

        String[] tblNames = {

                "School/Student","School/Exam","School/Academicsession",
                "School/Classe","School/Examresult","School/Subject",
                "School/Studentguard","School/Teacher","School/Grading",
                "School/Gradingvalue","School/Stream","School/Examsubject",

                "Accounting/Accheader","Accounting/Chargepackage","Accounting/Transactionentry",

                "crm/Client","crm/Projcolvals","crm/Project","crm/Projectcolumn",

                "settings/Company","settings/Companymodule","settings/Module","settings/User","settings/Usercompany"

        };

        return tblNames;
    }

   /* public Serializable autogenTables(){

        String[] tables = allTables();

        List<String> tableNames = new ArrayList<>();

        for (String tblName : tables){

            String newtblName  = tblName.toLowerCase() + "s";

            tableNames.add(newtblName);
        }

        List<String> tblCreateQueries = new ArrayList<>();

        for (String clsName : tables){

            String sqlCreateQueries = createTableFromClass(clsName);

            tblCreateQueries.add(sqlCreateQueries);

        }

        return (Serializable) checkTables();

    }*/

    public Serializable checkTables(){

        List<String> tableNames = new ArrayList<>();

        Connection connection = connection();

        try {
            String query = "SHOW TABLES"; // Example query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return checkColumns(tableNames);
    }

    private static Serializable checkColumns(List<String> tablesInDb){

       Map<String,ArrayList<String>> tblCol = new HashMap<>();

        List<String> tblColumns = new ArrayList<>();

        Connection connection = connection();

        for(String TblToChk : tablesInDb){
            try {
                String query = "DESCRIBE "+ TblToChk; // Example query
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    tblColumns.add(resultSet.getString(1));
                }
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            tblCol.put(TblToChk, (ArrayList<String>) tblColumns);
        }

        return compareNewandOld(tblCol);
    }

    private static Serializable compareNewandOld(Map<String, ArrayList<String>> tblCol){

        String[] tables = allTables();

        List<String> queries = new ArrayList<>() ;

        for (String tbl : tables){

            try{

                String[] cmbndClsnPckg = tbl.split("/");

                String fullClassName = "org.trialorm1." + cmbndClsnPckg[0] + "." + cmbndClsnPckg[1];

                Class<?> cls = Class.forName(fullClassName);

                String newtblName  = cmbndClsnPckg[1].toLowerCase() ;

                if(tblCol.containsKey(newtblName+"s")){

                   ArrayList<String> existingCols = tblCol.get(newtblName+"s");

                    List<String> clsProperty = new ArrayList<>();

                    Map<String,String> clsPropertyType = new HashMap<>();

                    Field[] fields = cls.getDeclaredFields();

                    for (Field field : fields) {
                        String fieldName = field.getName();
                        Class<?> fieldType = field.getType();
                        String fieldTypeName = fieldType.getSimpleName();

                        clsProperty.add(fieldName);

                        String propTypeName = mapDataType(fieldTypeName, fieldName);

                        clsPropertyType.put(fieldName, propTypeName);

                    }

                    ArrayList<String> list1 = new ArrayList<>(existingCols);
                    ArrayList<String> list2 = new ArrayList<>(clsProperty);

                    // Find elements unique to each list
                    ArrayList<String> diff1 = new ArrayList<>(list1);
                    ArrayList<String> diff2 = new ArrayList<>(list2);

                    diff1.removeAll(list2);  // Elements in list1 but not in list2
                    diff2.removeAll(list1);  // Elements in list2 but not in list1

                    if(diff1.isEmpty() || diff2.isEmpty()){
//                        System.out.println("Table ("+newtblName+"s)" + "columns and data types are similar to class" + tbl);

                    }else{

                        if(!diff1.isEmpty()){

                            for(String extr: diff1){

                                String sql = "ALTER TABLE "+newtblName+"s DROP COLUMN "+ extr;

                                queries.add(sql);
                            }

                        }

                        if(!diff2.isEmpty()){
                            for (String diff : diff2){

                                String sql = "ALTER TABLE "+newtblName+"s ADD COLUMN "+diff +" "+ clsPropertyType.get(diff);

                                queries.add(sql);

                            }
                        }

                    }

                }else{

                    String qrs = createTableFromClass(tbl);

                    queries.add(qrs);

                }
            }catch (Exception e){
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

//        return (Serializable) queries;
        return execQueries(queries);

    }

    private static String mapDataType(String fieldType, String fieldName){

        switch (fieldType){
            case "int":
                return  "INT";
            case "BigInteger":
                return  "BIGINT";
            case "string":
            default:
                return "VARCHAR(255)";
        }

    }

    private static String createTableFromClass(String clsName){


        String[] tables = allTables();

        List<String> tableNames = new ArrayList<>();

        for (String tblName : tables){

            String[] tblNames = clsName.split("/");

            String newtblName  = tblNames[1].toLowerCase() + "s";

            tableNames.add(newtblName);
        }


        List<String> columns = new ArrayList<>();

        try{

            String[] cmbndClsnPckg = clsName.split("/");

            String fullClassName = "org.trialorm1." + cmbndClsnPckg[0] + "." + cmbndClsnPckg[1];

            Class<?> cls = Class.forName(fullClassName);

            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                String fieldTypeName = fieldType.getSimpleName();

                String propTypeName = mapDataType(fieldTypeName, fieldName);

                String columnDef = null;

                if((fieldName == "ID") || (fieldName == "id" ) || (fieldName == "Id")){

                    columnDef = fieldName + " " + propTypeName +" PRIMARY KEY";

                }else {

                     columnDef = " "+ fieldName +" "+propTypeName ;
                }

                columns.add(columnDef);
            }

            String columnString = String.join("," , columns);

            String createQuery = "CREATE TABLE "+cmbndClsnPckg[1].toLowerCase()+"s" + "(" + columnString + ")";

            return createQuery;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

    }

    private static Serializable execQueries(List<String> queries2exec){

        Connection connection = connection();

        StringBuilder sql = new StringBuilder();

        for (String query : queries2exec) {
            sql.append(query+";");
        }

        String result = null;
        System.out.println(sql);
        PreparedStatement stmt = null;

        if(queries2exec.isEmpty()){
            return "nothing to exec";
        }else {
            try {
                stmt = connection.prepareStatement(sql.toString());
                boolean resultS = stmt.execute();

                if (resultS) {
                    result =  "Success";
                }else {
                    result = "Success";
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return result;
    }

}
