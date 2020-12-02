/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot;

import com.towianski.boot.server.CustomPermissionEvaluator;
import com.towianski.boot.server.ServerUserFileRightsList;
import com.towianski.boot.server.ServerUserFileRightsListHolder;
import com.towianski.jfileprocessor.HandleSearchBtnQueueSw;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.security.SecUtils;
import com.towianski.security.SecUtilsClient;
import com.towianski.utils.MyLogger;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author stan
 */
@ComponentScan("com.towianski")
@SpringBootApplication   // same as @Configuration @EnableAutoConfiguration @ComponentScan
@Profile("client")
public class StartJfileFinderWin {
    
    private static final MyLogger logger = MyLogger.getLogger( StartJfileFinderWin.class.getName() );
    
    public static ConfigurableApplicationContext context = null;

    @Bean
    public GlobalMemory globalMemory() {
        logger.info( "GlobalMemory() constructor" );
        return new GlobalMemory();
    }
    
    @Bean
    @DependsOn({"globalMemory"})
    public JFileFinderWin jFileFinderWin() {
        logger.info( "jFileFinderWin() constructor" );
        return new JFileFinderWin();
    }
    
    @Bean
    @DependsOn({"jFileFinderWin"})
    public HandleSearchBtnQueueSw handleSearchBtnQueueSw() {
        logger.info( "handleSearchBtnQueueSw() constructor" );
        return new HandleSearchBtnQueueSw(  );
    }
    
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    public JFileFinder jFileFinder() {
//        logger.info( "JFileFinder() constructor" );
//        return new JFileFinder(  );
//    }
    
//    @Bean
//    @DependsOn({"globalMemory","jFileFinderWin"})
//    public HandleSearchBtnQueueSw handleSearchBtnQueueSw() {
//        logger.info( "handleSearchBtnQueueSw() constructor" );
//        return new HandleSearchBtnQueueSw( jFileFinderWin() );
//    }
    
//    handleSearchBtnQueueSw = new HandleSearchBtnQueueSw( this );

    
    @Bean
    public CustomPermissionEvaluator customPermissionEvaluatorNormBean() {
        logger.info( "CustomPermissionEvaluator() constructor" );
        return new CustomPermissionEvaluator( serverUserFileRightsList() );
//        return customPermissionEvaluator;
    }

    @Bean
    public ServerUserFileRightsList serverUserFileRightsList() {
        logger.info( "ServerUserFileRights() constructor" );
        //return new ServerUserFileRightsListHolder();
        return ServerUserFileRightsListHolder.useAllRightsForProfileClient();

//        return serverUserFileRightsList;
//        return ServerUserFileRightsList.readInServerUserFileRightsFromFileList();
//        ServerUserFileRightsList serverUserFileRightsList = new ServerUserFileRightsList();
//        return serverUserFileRightsList();
    }

    @Bean
    public SecUtilsClient secUtils() {
        logger.info( "SecUtilsClient() constructor" );
        return new SecUtilsClient();
    }

    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        MyLogger logger = Mylogger.severeExc( ex );        

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.severeExc( ex);
        } catch (InstantiationException ex) {
            logger.severeExc( ex);
        } catch (IllegalAccessException ex) {
            logger.severeExc( ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.severeExc( ex);
        }
        
        for ( int i = 0; i < args.length; i++ )
            {
//            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            }
        
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(JFileFinderWin.class).headless(false).run(args);
        context = new SpringApplicationBuilder( StartJfileFinderWin.class ).profiles("client").web( WebApplicationType.NONE ).bannerMode(Banner.Mode.OFF).headless(false).run(args);
        
        GlobalMemory.setSecUtils( (context.getBean( SecUtils.class )) );

        String[] beanNames = context.getBeanDefinitionNames();

        logger.info( "\n---- List Of App Spring Beans ----");
        for (String beanName : beanNames) {
            logger.info( beanName + " : " + context.getBean(beanName).getClass().toString());
        }
        logger.info( "---- List Of App Spring Beans ----\n");

        logger.info( "jFileFinderWin jFileFinderWin = " + context.getBean(JFileFinderWin.class) );
        
//        EventQueue.invokeLater(() -> {
//            JFileFinderWin jFileFinderWin = context.getBean(JFileFinderWin.class);
//            logger.info( "HandleSearchBtnQueue jFileFinderWin = " + jFileFinderWin );
//            jFileFinderWin.setVisible(true);
//            if ( args.length > 0 )
//                {
//                jFileFinderWin.setStartingFolder( args[0] );
//                jFileFinderWin.callSearchBtnActionPerformed( null );
//                }
//            });   

            context.getBean(HandleSearchBtnQueueSw.class).actionPerformed( null, null );

            GlobalMemory ggg = context.getBean(GlobalMemory.class);
            logger.info( "initial  GlobalMemory = " + ggg );
            ggg.print();
     //   context.close();
    }
    
}
