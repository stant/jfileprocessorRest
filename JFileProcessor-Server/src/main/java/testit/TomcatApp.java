package testit;

/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import testit.model.ProgramMemory;
import testit.utils.Rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Configuration
@ComponentScan       // This class must be a package level above the controllers !
@SpringBootApplication
public class TomcatApp {

    private static Log logger = LogFactory.getLog(TomcatApp.class);

    static public Rest myRest = new Rest();
    static ProgramMemory programMemory = null;

    public static void setProgramMemory(ProgramMemory programMemory) {
        TomcatApp.programMemory = programMemory;
    }

    static public ProgramMemory getProgramMemory() {
        return programMemory;
    }


    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                logger.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                logger.info("ServletContext destroyed");
            }

        };
    }

    public static void main(String[] args) {
        logger.info( "*** before SpringApplication.run(TomcatApp.class, args)" );
        for ( int i = 0; i < args.length; i++ )
            {
            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            }
        logger.info( "** -Dwhatever =" + System.getProperty("whatever") + "=" );
        logger.info( "** -Dmyarg2 =" + System.getProperty("myarg2") + "=" );
        
        SpringApplication.run(TomcatApp.class, args);
        logger.info( "*** after SpringApplication.run(TomcatApp.class, args)" );

//        final JFileFinderWin jffw = new JFileFinderWin();
//        if ( args.length > 0 )
//            {
//            jffw.setStartingFolder( args[0] );
//            jffw.searchBtnAction( null );
//            }
//        jffw.setVisible(true);
        
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
////                new JFileFinderWin().setVisible(true);
//                jffw.setVisible(true);
//            }
//        });
    }

}
