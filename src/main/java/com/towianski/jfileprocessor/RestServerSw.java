/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.TomcatAppMonitor;
import com.towianski.jfileprocess.actions.TomcatAppSftpThread;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class RestServerSw {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    private static final MyLogger logger = MyLogger.getLogger( RestServerSw.class.getName() );
    ConnUserInfo connUserInfo = null;
    JFileFinderWin jFileFinderWin = null;
    boolean cancelFlag = false;
    TomcatAppMonitor tomcatAppMonitor = null;
    Thread runThread = null;
    int count = 0;
    
    public RestServerSw( ConnUserInfo connUserInfo, JFileFinderWin jFileFinderWin )
        {
        this.connUserInfo = connUserInfo;
        this.jFileFinderWin = jFileFinderWin;
        }

    public RestTemplate timeoutRestTemplate( RestTemplateBuilder restTemplateBuilder )
        {
        return restTemplateBuilder
                .setConnectTimeout( Duration.ofMillis( 100 ) )
                .setReadTimeout( Duration.ofMillis( 100 ) )
                .build();
        }

    public void cancelRestServer( boolean forceStop ) 
        {
        logger.info( "enter RestServerSw.cancelRestServer()" );
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
        if ( tomcatAppMonitor != null )
            {
            if ( runThread != null && runThread.isAlive() )
                {
                tomcatAppMonitor.cancelTomcatAppMonitor( forceStop );
                try
                    {
                    //runThread.interrupt();
                    runThread.join();
                    logger.info( "RestServerSw.cancelRestServer() - after runThread.join()" );
                    } 
                catch (InterruptedException ex)
                    {
                    logger.info( "RestServerSw.cancelRestServer() - InterruptedException on runThread.join()" );
                    ex.printStackTrace();
                    logger.severeExc( ex );
                    }
                }
            }
        else   // no thread already running
            {
            TomcatAppSftpThread tomcatAppThread = new TomcatAppSftpThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
            tomcatAppThread.cancelTomcatAppThread(forceStop);
            }
        logger.info( "exit RestServerSw.cancelRestServer()" );
        }

    public void actionPerformed(java.awt.event.ActionEvent evt) {                                         
//        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
        if ( 1 ==2  && ! connUserInfo.isConnectedFlag() )
            {
            this.cancelRestServer( false );
            }
        else
            {
            try {
                logger.info( "RestServerSw doCmdBtnActionPerformed start" );
                logger.info( "   on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                logger.info( "   connUserInfo = " + connUserInfo );
//                RestServerSwingWorker = new TomcatAppSwingWorker( jFileFinderWin, this, startingPath );
//                tomcatApp = RestServerSwingWorker.getTomcatApp();
//                RestServerSwingWorker.execute();   //doInBackground();

//                TomcatAppPost tomcatAppPost = new TomcatAppPost( jFileFinderWin, LockObj );
//                tomcatAppPostThread = newThread( tomcatAppPost );
//                tomcatAppPostThread.setName( "watchdirPostThread=" + count );
//                tomcatAppPostThread.start();

                if ( runThread != null && runThread.isAlive() )
                    {
                    cancelRestServer( false );
                    }
                tomcatAppMonitor = new TomcatAppMonitor( this, connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
                runThread = ProcessInThread.newThread( "TomcatAppMonitor", count++, true, tomcatAppMonitor );
                runThread.start();

      // TEST          waitUntilStarted();
                logger.info( "RestServerSw after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) 
                {
                logger.severeExc( ex );
                } 
            }
        }

//    public void keepRunning() 
//        {
//        logger.info( "entered RestServerSw keepRunning()" );
//        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
//
//        cancelFlag = false;
//        int downTimes = 99;
//        String response = null;
//        boolean didFirstStart = false;
//        
//        while ( ! cancelFlag )
//            {
//            try
//                {
//                logger.info( "RestServerSw.run() make rest /jfp/sys/ping call" );
//                try
//                    {
//                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING, String.class );
//                    }
//                catch( Exception exc )
//                    {
//                    logger.info( "RestServerSw.run() ping threw Exception !!" );
//                    response = null;
//                    SwingUtilities.invokeLater(new Runnable() 
//                        {
//                        public void run() {
//                            jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                            }
//                        });
//                    exc.printStackTrace();
//                    }
//                logger.info( "RestServerSw.run() ping response =" + response + "=" );
//                if ( ! cancelFlag && 
//                    ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
//                    {
//                    if ( ++downTimes > 5 )
//                        {
//                tomcatAppMonitor = new TomcatAppMonitor( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin, this );
//                runThread = newThread( tomcatAppMonitor );
//                runThread.setName( "tomcatAppMonitor=" + count++ );
//                runThread.start();
//                
//                if ( tomcatAppMonitor.isStartedServer() )
//                    {
//                    Thread.sleep( 15000 );
//                    }
//                        SwingUtilities.invokeLater(new Runnable() 
//                            {
//                            public void run() {
//                                jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                                }
//                            });
//                        logger.info( "after start remote jfp server" );
////                        Thread.sleep( 30000 );
//                        downTimes = 0;
//                        }
//                    else
//                        {
//                        Thread.sleep( 1000 );
//                        }
//                    }
//                else if ( didFirstStart ) // is running or first start ?
//                    {
//                    Thread.sleep( 4000 );
//                    }
//                else if ( ! didFirstStart ) // is running or first start ?
//                    {
//                    // already running
//                    downTimes = 0;
//                    connUserInfo.setConnectedFlag( true );
//                    SwingUtilities.invokeLater(new Runnable() 
//                        {
//                        public void run() {
//                            jFileFinderWin.setRmtConnectBtnBackground( Color.green );
//                            }
//                        });
//                    }
//                }
//            catch( InterruptedException intexc )
//                {
//                logger.info( "RestServerSw sleep interrupted" );
//                SwingUtilities.invokeLater(new Runnable() 
//                    {
//                    public void run() {
//                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                        }
//                    });
//                }
//            catch( Exception exc )
//                {
//                SwingUtilities.invokeLater(new Runnable() 
//                    {
//                    public void run() {
//                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                        }
//                    });
//                exc.printStackTrace();
//                }
//            didFirstStart = true;
//            } // while
//        
//        connUserInfo.setConnectedFlag( false );
//        SwingUtilities.invokeLater(new Runnable() 
//            {
//            public void run() {
//                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                }
//            });
//        logger.info( "Exiting RestServerSw run() - Done" );
//        }

    public void waitUntilStarted()    // not called at this point
        {
        logger.info( "entered RestServerSw waitUntilStarted()" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        int waitCount = 15;
        
        while ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            logger.info( "RestServerSw.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                logger.info( "RestServerSw.run() SYS_GET_FILESYS response =" + response );
//                connUserInfo.setToFilesysType(response);
//                logger.info( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
                jFileFinderWin.setFilesysType(response);
                logger.info( "jFileFinderWin.getFilesysType() =" + jFileFinderWin.getFilesysType() );
                }
            catch( Exception exc )
                {
                logger.info( "RestServerSw.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            logger.info( "waitUntilStarted response =" + response + "=" );
            try
                {
                if ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
                    {
                    if ( firstWaitFlag )
                        {
                        firstWaitFlag = false;
                        logger.info( "pause 12 seconds first time. give longer pause to let server start." );
                        Thread.sleep( 12000 );
                        }
                    else
                        {
                        Thread.sleep( 4000 );
                        }
                    }
                } 
            catch (InterruptedException ex)
                {
                logger.severeExc( ex );
                }

            if ( --waitCount < 1 )  break;
            }
        }
                
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TomcatAppSwFrame().setVisible(true);
//            }
//        });
    }
}
