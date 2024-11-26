package org.trialorm1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Random;

public class SqlConnector {



    public static Connection getConnection(){

        Connection connection = null;

//        String url = "jdbc:mysql://localhost:3306/trialOrmJava?allowMultiQueries=true";
        String url = "jdbc:mysql://localhost:3306/ekso?allowMultiQueries=true";
        String username="soke";
        String password = "";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, username, password);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return connection;

    }

    public static String giveId(){
        long timestamp = Instant.now().toEpochMilli();

        // Generate a random number between 1 and 9000
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1; // 1 to 9000

        // Concatenate timestamp and random number
        return timestamp + String.valueOf(randomNum);

    }
    
}

/*
public class SqlConnector {

    String url = "jdbc:mysql://localhost:3306/ekso";
    String username="soke";
    String password = "";

    public String getConnection(){
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url,username,password);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from users");

            while (resultSet.next()){
                return resultSet.getString(1);
            }
        } catch (Exception e) {

            return  e.toString();
//            throw new RuntimeException(e);
        }
        return "Failed";

    }
}
*/

