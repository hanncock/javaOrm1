package org.trialorm1;
/*
import org.trialorm1.School.Student;

import java.lang.reflect.Field;
*/
/*


public class Main {
    public static void main(String[] args) {
        try {
            // Create an instance of the Person class
            Student person = new Student();

            // Get the Class object
            Class<?> clazz = person.getClass();

            // Retrieve all fields
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // Get the field name and type
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();
                String fieldTypeName = fieldType.getName();

                System.out.println("Field Name: " + fieldName + ", Type: " + fieldTypeName);

//                if(fieldName == "id"){
//                    System.out.println("Creating primary key");
//                }

                switch (fieldName){
                    case ("int"):
                        System.out.println("Creating id");
                        break;

                    default:
                        System.out.println("will provide default");
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {
    public static void main(String[] args){
        System.setProperty("spring.devtools.restart.enabled", "true");
        SpringApplication.run(Main.class,args);
    }

}
