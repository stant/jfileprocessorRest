package com.towianski.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.towianski.models.ResultsData;

import java.io.File;
import java.io.PrintWriter;

public class Rest {

    private final static MyLogger logger = MyLogger.getLogger( Rest.class.getName() );

    static File programMemoryFile = null;

    static {
        programMemoryFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "programMemory.json" );
        }

//    @Override
//    public String toString(Path filepath, Object obj ) {
//        ObjectMapper objectMapper = new ObjectMapper();
////        Car car = new Car("yellow", "renault");
////        objectMapper.writeValue(new File("target/car.json"), car);
//        objectMapper.writeValue(new File( filepath.get), obj );
//    }

//    static public ProgramMemory readProgramMemoryFile() {
//        ProgramMemory programMemory = new ProgramMemory();
//        //read json file data to String
//        byte[] jsonData = new byte[0];
//
//        try {
//            jsonData = Files.readAllBytes( Paths.get( programMemoryFile.getPath() ) );
//
//            //create ObjectMapper instance
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            //convert json string to object
//            programMemory = objectMapper.readValue( jsonData, ProgramMemory.class );
//            }
//        catch (IOException e) {
//            logger.info( "assuming file does not exist so create empty programMemory()");
////            e.printStackTrace();
////            return programMemory;
//        }
//
//        System.out.println("ProgramMemory Object\n" + jsonData);
//        TomcatApp.setProgramMemory( programMemory );
//        return programMemory;
//    }

    static public ResultsData jsonToObject( String json, Object obj ) 
        {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ResultsData resultsData = objectMapper.readValue( json, ResultsData.class );
            return resultsData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void saveObjectToFile( String fileName, Object obj ) 
        {
        //convert Object to json string
//        ProgramMemory programMemory = createEmployee();
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //configure Object mapper for pretty print
        objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true);

        //writing to console, can write to any output stream such as file
        File toFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName );

//        StringWriter stringEmp = new StringWriter();
        try {
            objectMapper.writeValue( toFile, obj );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("write Object JSON file =" + toFile );
    }

    static public void saveStringToFile( String fileName, String outs ) 
        {
        //writing to console, can write to any output stream such as file
        try {
            File toFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName );
            PrintWriter out = new PrintWriter( toFile );

            out.println( outs );
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("write JSON file = " + fileName );
    }

}

