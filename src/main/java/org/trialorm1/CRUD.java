package org.trialorm1;
import org.jooq.tools.json.JSONArray;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRUD {

    public static Connection connection(){

        Connection connection = SqlConnector.getConnection();

        return connection;

    }

    public static String getId(){
        String newId = SqlConnector.giveId();
        return newId;
    }

    public Serializable insertValues(String clsName, Object jsonInput){

        List<String> queries = new ArrayList<>();

        try{

            String[] cmbndClsnPckg = clsName.split("/");

            String fullClassName = "org.trialorm1." + cmbndClsnPckg[0] + "." + cmbndClsnPckg[1];

            Class<?> cls = Class.forName(fullClassName);

            Field[] fields = cls.getDeclaredFields();

            List<String> clsProperties = new ArrayList<>();

            for (Field field : fields) {
                String fieldName = field.getName();
                clsProperties.add(fieldName);
            }

            if (jsonInput instanceof List) {

                List<Map<String, Object>> jsonArray = (List<Map<String, Object>>) jsonInput;

                for (Map<String, Object> jsonObjec : jsonArray) {

                    List<String> columnNames = new ArrayList<>();
                    List<String> columnValues = new ArrayList<>();

                    Map<String, Object> jsonObject = (Map<String, Object>) jsonObjec;

                    for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        if(clsProperties.contains(key)){
                            columnNames.add(key);
                            columnValues.add("'"+value.toString()+"'");
                        }

                    }

                    if((columnNames.contains("id")) && (columnValues.get(columnNames.indexOf("id")) != null)){

                        int index = Integer.parseInt(String.valueOf(columnNames.indexOf("id")));
                        String idchanger = "id = " +columnValues.get(index) ;

                        columnNames.remove(index);
                        columnValues.remove(index);

                        List<String> newVals = new ArrayList<>();

                        for(int i = 0; i<columnNames.size(); i++){

                            String newstinrg = columnNames.get(i) + "=" + columnValues.get(i) ;

                            newVals.add(newstinrg);
                        }

                        String newQuery = "UPDATE " + cmbndClsnPckg[1].toLowerCase()+"s SET "+ String.join(",",newVals)+ "WHERE " + idchanger ;
                        queries.add(newQuery);

                    }else {
                        String newQuery = "INSERT INTO " + cmbndClsnPckg[1].toLowerCase()+"s" + "(" + "id,"+ String.join(",",columnNames) + ")" + "VALUES (" + getId()+"," + String.join(",", columnValues) + ")";
                        queries.add(newQuery);

                    }

                }

            } else if (jsonInput instanceof Map) {

                List<String> columnNames = new ArrayList<>();
                List<String> columnValues = new ArrayList<>();

                Map<String, Object> jsonObject = (Map<String, Object>) jsonInput;

                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if(clsProperties.contains(key)){
                        columnNames.add(key);
                        columnValues.add("'" + (value == null ? null : value.toString()) +"'");
                    }

                }




                if((columnNames.contains("id")) && (columnValues.get(columnNames.indexOf("id")) != null)){

                    int index = Integer.parseInt(String.valueOf(columnNames.indexOf("id")));
                    String idchanger = "id = " +columnValues.get(index) ;

                    columnNames.remove(index);
                    columnValues.remove(index);

                    List<String> newVals = new ArrayList<>();

                    for(int i = 0; i<columnNames.size(); i++){

                        String newstinrg = columnNames.get(i) + "=" + columnValues.get(i) ;

                        newVals.add(newstinrg);
                    }

                    String newQuery = "UPDATE " + cmbndClsnPckg[1].toLowerCase()+"s SET "+ String.join(",",newVals)+ "WHERE " + idchanger + ";"; //+ "(" + "id,"+ String.join(",",columnNames) + ")" + "VALUES (" + getId() + String.join(",", columnValues) + ")";
                    queries.add(newQuery);//


                }else {
                    String newQuery = "INSERT INTO " + cmbndClsnPckg[1].toLowerCase()+"s" + "(" + "id,"+ String.join(",",columnNames) + ")" + "VALUES (" + getId()+"," + String.join(",", columnValues) + ")";
                    queries.add(newQuery);
                    System.out.println(newQuery);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return execQeris("insert",queries);
    }

    public Serializable getValues(String clsName, String values){

        List<String> queries = new ArrayList<>();

        String[] cmbndClsnPckg = clsName.split("/");

        String tblName =  cmbndClsnPckg[1].toLowerCase() + "s";

        ArrayList<String> sql = new ArrayList<>();

        if(values == null){

            sql .add("SELECT * FROM " + tblName);

            return (Serializable) execQeris("fetch",sql);

        }else {

            List<String> jsonpropandVals = List.of(values.split("&"));

            try{

                String fullClassName = "org.trialorm1." + cmbndClsnPckg[0] + "." + cmbndClsnPckg[1];

                Class<?> cls = Class.forName(fullClassName);

                Field[] fields = cls.getDeclaredFields();

                List<String> clsProperties = new ArrayList<>();

                for (Field field : fields) {
                    String fieldName = field.getName();
                    clsProperties.add(fieldName);
                }

                List<String> params = new ArrayList<>();

                for(String jsonProp: jsonpropandVals) {

                    List<String> propVal = List.of(jsonProp.split("="));

                    int ind = 0;

                    if (clsProperties.contains(propVal.get(ind))) {

                        String srchParam = propVal.get(ind) + "=" + '"' + propVal.get(ind + 1) + '"';

                        params.add(srchParam);
                    }
                    ind = +2;

                }

                sql .add("SELECT * FROM " + tblName + " WHERE " + String.join(" AND ", params));

//                System.out.println(sql);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }

            return (Serializable) execQeris("fetch",sql);
        }
    }

    public Serializable delValues(){
        return "will dell";
    }

    private static Serializable execQeris(String operation,List<String> queries){

        JSONArray result = new JSONArray();

        Connection connection = connection();

        if(queries.isEmpty()){

            return "nothing to exec";

        }else {

            StringBuilder sql = new StringBuilder();

            for (String query : queries) {
                sql.append(query+";");
            }

            try {
                Statement statement = connection.createStatement();

                if(operation.equals( "insert")){

                    if(statement.executeUpdate(String.valueOf(sql)) == 0){
                        return "error";
                    }else {
                        return "Success";
                    }
                }else{
                    System.out.println(sql);
                    ResultSet resultSet = statement.executeQuery(sql.toString());

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (resultSet.next()){
                        Map<String, String> row = new HashMap<>();

                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            String columnValue = resultSet.getString(i);
                            row.put(columnName, columnValue);
                        }

                        result.add(row);

                    }

                    System.out.println(result);

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return (Serializable) result;
    }
}
