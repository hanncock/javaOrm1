package org.trialorm1.controllers;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.trialorm1.CRUD;
import org.trialorm1.Settings.User;
import org.trialorm1.setup.Alltables;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @GetMapping("/**")
    public Serializable handleRequest(HttpServletRequest request) {

        try{

            String requestUri = request.getRequestURI();
            String[] splitUrl = requestUri.split("/api");
            String[] clsName = splitUrl[1].split("/");

            String query_param =  request.getQueryString() == null ? null:request.getQueryString();//.split("\\?") ;

            CRUD crud = new CRUD();

            String packageName = clsName[1].substring(0, 1).toUpperCase() + clsName[1].substring(1);

            String className = clsName[2].substring(0, 1).toUpperCase() + clsName[2].substring(1);

            String fullPath = packageName +"/" +className;

            Map<String, Object> newerow = new HashMap<>();
//            newerow.put("success",true);
//            newerow.put("data",userData);

            if(clsName[3].equals("list")){

                Object gottenVals = crud.getValues(fullPath, query_param);

                newerow.put("data",gottenVals);


                System.out.println("this is the output" + newerow);
//                return  crud.getValues(fullPath, query_param);
                return (Serializable) newerow;

            }else {
                return "[error]";
            }



        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

    }


    @GetMapping("/setup/all/tables")
    public Serializable setupTables(){
        Alltables getTables = new Alltables();
//        return getTables.autogenTables();
        return getTables.checkTables();
    }


    @PostMapping("/api/settings/user/login")
    public Serializable login(@RequestBody Object jsonInput){

//        return "will login";

        User userdata = new User();
        return userdata.login(jsonInput);

    }

    @PostMapping("/**")
    public Serializable insert(HttpServletRequest request, @RequestBody Object jsonInput){

        System.out.println("Received JSON: " + jsonInput);

        try{

            String requestUri = request.getRequestURI();
            String[] splitUrl = requestUri.split("/api");
            String[] clsName = splitUrl[1].split("/");

//            String query_param =  request.getQueryString() == null ? null:request.getQueryString();//.split("\\?") ;

            CRUD crud = new CRUD();

            String packageName = clsName[1].substring(0, 1).toUpperCase() + clsName[1].substring(1);

            String className = clsName[2].substring(0, 1).toUpperCase() + clsName[2].substring(1);

            String fullPath = packageName +"/" +className;

            switch (clsName[3]){
                case ("add"):
                    return crud.insertValues(fullPath,jsonInput);
                case ("del"):
                    return "will dell";
//                    return crud.delValues(fullPath,jsonInput);
                default:
                    return "error";
            }

//            if(clsName[3].equals("add")){
//
//            }
//
////            return (Serializable) jsonInput;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }



}
