package testit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.towianski.boot.server.TomcatApp;
import testit.model.ProgramMemoryEmp;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Rest {

    private static final Logger logger = LoggerFactory.getLogger(Rest.class);

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

    static public ProgramMemoryEmp readProgramMemoryFile() {
        ProgramMemoryEmp programMemory = new ProgramMemoryEmp();
        //read json file data to String
        byte[] jsonData = new byte[0];

        try {
            jsonData = Files.readAllBytes( Paths.get( programMemoryFile.getPath() ) );

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object
            programMemory = objectMapper.readValue(jsonData, ProgramMemoryEmp.class );
            }
        catch (IOException e) {
            logger.info( "assuming file does not exist so create empty programMemory()");
//            e.printStackTrace();
//            return programMemory;
        }

        System.out.println("ProgramMemory Object\n" + jsonData);
        TomcatApp.setProgramMemory( programMemory );
        return programMemory;
    }

    static public void saveProgramMemoryFile( ProgramMemoryEmp programMemory ) {
        //convert Object to json string
//        ProgramMemory programMemory = createEmployee();
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //configure Object mapper for pretty print
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        //writing to console, can write to any output stream such as file
        StringWriter stringEmp = new StringWriter();
        try {
            objectMapper.writeValue( programMemoryFile, programMemory );
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("write programMemory JSON file =\n" + programMemoryFile.getPath() );
    }

}

