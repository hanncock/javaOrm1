package org.trialorm1.Settings;

import org.jooq.tools.json.JSONArray;
import org.trialorm1.SqlConnector;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

public class User {
    private BigInteger id;
    private String firstName;
    private String otherNames;
    private int phone;
    private String idNo;
    private String gender;
    private String postalAdd;
    private String email;
    private BigInteger companyId;


    public static Connection connection(){

        Connection connection = SqlConnector.getConnection();

        return connection;

    }

    public static Serializable login(Object jsonInput){

        Map<String, Object> jsonObject = (Map<String, Object>) jsonInput;

        String name = (String) jsonObject.get("email");
        String pass = (String) jsonObject.get("pass");

        String sql = "SELECT * FROM users WHERE email = " + "'" +name + "'";

        JSONArray result = new JSONArray();
        Map<String, Object> userData = new HashMap<>();


        try {
            Connection connection = connection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql.toString());
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();


            while (resultSet.next()){
                Map<String, String> row = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    userData.put(columnName, columnValue);
                }

            }

            if(!userData.isEmpty()){

                String id = (String) userData.get("id");

                String sqluserCompanies = "SELECT * FROM usercompanys WHERE userId = " + "'" +id + "'";

                ResultSet resultSet2 = statement.executeQuery(sqluserCompanies.toString());
                ResultSetMetaData metaData2 = resultSet2.getMetaData();

                int columnCount2 = metaData2.getColumnCount();

                JSONArray userCompanies = new JSONArray();

                while (resultSet2.next()){
                    Map<String, String> row = new HashMap<>();

                    for (int i = 1; i <= columnCount2; i++) {
                        String columnName = metaData2.getColumnName(i);
                        String columnValue = resultSet2.getString(i);
                        row.put(columnName, columnValue);
                    }

                    userCompanies.add(row);
                }

                if(!userCompanies.isEmpty()){
                    JSONArray companiesfnd = userCompanies;
                    List<String> companiesId = new ArrayList<>();
                    for(Object fndId : companiesfnd){

                        Map<String,String> val1 = (Map<String, String>) fndId;
                        companiesId.add(val1.get("companyId"));

                    }

                    String cmpnySql = "SELECT * FROM companys WHERE id IN (" + String.join(",",companiesId) +")";

                    ResultSet resultSet3 = statement.executeQuery(cmpnySql.toString());
                    ResultSetMetaData metaData3 = resultSet3.getMetaData();

                    int columnCount3 = metaData3.getColumnCount();

                    JSONArray userCompaniesfnd = new JSONArray();

                    while (resultSet3.next()){
                        Map<String, String> row = new HashMap<>();

                        for (int i = 1; i <= columnCount3; i++) {
                            String columnName = metaData3.getColumnName(i);
                            String columnValue = resultSet3.getString(i);
                            row.put(columnName, columnValue);
                        }

                        userCompaniesfnd.add(row);
                    }
                    userData.put("allowedCompanies", userCompaniesfnd);
                    Map<String, Object> newerow = new HashMap<>();
                    newerow.put("success",true);
                    newerow.put("data",userData);

//                    result.add(newerow);
                    return (Serializable) newerow;

                }

            }else {
                return "Login failed ";
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;

    }
}
