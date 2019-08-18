package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.RestServerSw;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.awt.Color;
import java.io.*;
import javax.swing.SwingUtilities;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppMonitor implements Runnable
    {
    private static final MyLogger logger = MyLogger.getLogger( TomcatAppMonitor.class.getName() );

    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    RestServerSw restServerSw = null;
    JFileFinderWin jFileFinderWin = null;
    boolean startedServer = false;
    TomcatAppThread tomcatAppThread = null;
    Thread runThread = null;
    WatchTomcatAppStartThread watchTomcatAppStartThread = null;
    Thread watchStartThread = null;
    int count = 0;
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public TomcatAppMonitor( RestServerSw restServerSw, ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
        {
        this.restServerSw = restServerSw;
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelTomcatAppMonitor( boolean forceStop )
        {
        logger.info( "cancelTomcatAppMonitor() set cancelFlag to true" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = true;
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );

        cancelAppThread( forceStop );

        connUserInfo.setConnectedFlag( false );
        SwingUtilities.invokeLater(new Runnable() 
            {
            public void run() {
                jFileFinderWin.setRmtConnectBtnBackgroundReset();
                }
            });

        //Thread.currentThread().interrupt();
        logger.info( "exit cancelTomcatAppMonitor()");
        }
    
    public void cancelAppThread( boolean forceStop )
        {
        logger.info( "TomcatAppMonitor cancelAppThread()");
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        if ( runThread != null && runThread.isAlive() )
//            {
//            logger.info( "cancel runThread/tomcatAppThread" );
//            tomcatAppThread.cancelTomcatAppThread( forceStop );
//            }

        if ( runThread != null && runThread.isAlive() )
            {
            logger.info( "call to stop remote server first!" );
            tomcatAppThread.cancelTomcatAppThread(forceStop);
            logger.info( "interrupt runThread/tomcatAppThread" );
            runThread.interrupt();
            }
        
        if ( watchStartThread != null && watchStartThread.isAlive() )
            {
            logger.info( "interrupt watchStartThread" );
            watchStartThread.interrupt();
            }
        
        logger.info( "exit TomcatAppMonitor cancelAppThread()");
        }
    
    // Starts 2 threads.
    // watch tomcatapp thread which just tests connection and turns button green or reset. if goes from running to stopped, it calls disconnect.
    // tomcat app thread
    // then waits to be stopped.
    @Override
    public void run() 
        {
        logger.info( "entered TomcatAppMonitor.run()" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

        cancelFlag = false;
        int downTimes = 0;

        tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
        runThread = ProcessInThread.newThread( "tomcatAppThread", count++, false, tomcatAppThread );
        runThread.start();

        watchTomcatAppStartThread = new WatchTomcatAppStartThread( restServerSw, runThread, connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
        watchStartThread = ProcessInThread.newThread( "watchTomcatAppStartThread", 0, false, watchTomcatAppStartThread );
        watchStartThread.start();
//        tomcatAppThread.run();
        //logger.info( "after first tomcatAppThread.run()" );
        
        //waitUntilStarted();
        try
            {
            synchronized (this) 
                {
                //this.wait();
                runThread.join();

                if ( watchStartThread != null && watchStartThread.isAlive() )
                    {
                    logger.info( "join watchTomcatAppStartThread" );
                    try
                        {
                        watchStartThread.join();
                        } 
                    catch (InterruptedException ex)
                        {
                        logger.info( "TomcatAppMonitor.cancelAppThread() - InterruptedException on watchStartThread.join()" );
                        logger.severeExc( ex );
                        }
                    logger.info( "Done/joined watchTomcatAppStartThread" );
                    }
                }
            } 
        catch (InterruptedException ex)
            {
            logger.info( "TomcatAppMonitor.wait() - InterruptedException" );
            logger.severeExc( ex );
            }
        connUserInfo.setConnectedFlag( false );
        SwingUtilities.invokeLater(new Runnable() 
            {
            public void run() {
                jFileFinderWin.setRmtConnectBtnBackgroundReset();
                }
            });
        
//        while ( ! cancelFlag &&  ++downTimes < 5 )
//            {
//            try
//                {
//                tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
////                        runThread = ProcessInThread.newThread( "TomcatAppMonitor", count++, false, tomcatAppThread );
////                        runThread.start();
//                tomcatAppThread.run();
//                logger.info( "after tomcatAppThread.run()  downTimes =" + downTimes );
//                Thread.sleep( 1000 );
//                }
//            catch( InterruptedException intexc )
//                {
//                logger.info( "TomcatAppMonitor sleep interrupted" );
////                SwingUtilities.invokeLater(new Runnable() 
////                    {
////                    public void run() {
////                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
////                        }
////                    });
//                }
//            catch( Exception exc )
//                {
////                SwingUtilities.invokeLater(new Runnable() 
////                    {
////                    public void run() {
////                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
////                        }
////                    });
//                exc.printStackTrace();
//                }
//            } // while
        //logger.info( "TomcatAppMonitor.run() STOP any existing running server" );
        //cancelAppThread( false );
        
        //connUserInfo.setConnectedFlag( false );
//        SwingUtilities.invokeLater(new Runnable() 
//            {
//            public void run() {
//                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                }
//            });
        logger.info( "Exiting TomcatAppMonitor run() - Done" );
        }
                
    public void waitUntilStarted()
        {
        logger.info( "entered TomcatAppMonitor waitUntilStarted()" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        int waitCount = 15;
        
        while ( (response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX))  && ! cancelFlag )
            {
            logger.info( "TomcatAppMonitor.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                logger.info( "TomcatAppMonitor.run() SYS_GET_FILESYS response =" + response );
//                connUserInfo.setToFilesysType(response);
//                logger.info( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
                jFileFinderWin.setFilesysType(response);
                logger.info( "jFileFinderWin.getFilesysType() =" + jFileFinderWin.getFilesysType() );
                }
            catch( Exception exc )
                {
                logger.info( "TomcatAppMonitor.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            logger.info( "TomcatAppMonitor.waitUntilStarted response =" + response + "=" );
            try
                {
                if ( cancelFlag )
                    {
                    logger.info( "cancel so no pause." );
                    }
                else if ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
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
        logger.info( "TomcatAppMonitor waitUntilStarted() after while loop" );

        if ( response < 0 || cancelFlag || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            logger.info( "TomcatAppMonitor waitUntilStarted() reset button color" );
            connUserInfo.setConnectedFlag( false );
            SwingUtilities.invokeLater(new Runnable() 
                {
                public void run() {
                    jFileFinderWin.setRmtConnectBtnBackgroundReset();
                    }
                });
            }
        else
            {
            logger.info( "TomcatAppMonitor waitUntilStarted() set button color to green for connected" );
            connUserInfo.setConnectedFlag( true );
            SwingUtilities.invokeLater(new Runnable() 
                {
                public void run() {
                    jFileFinderWin.setRmtConnectBtnBackground( Color.green );
                    }
                });
            }
        }

    public boolean isStartedServer() {
        return startedServer;
    }

    public void setStartedServer(boolean startedServer) {
        this.startedServer = startedServer;
    }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
