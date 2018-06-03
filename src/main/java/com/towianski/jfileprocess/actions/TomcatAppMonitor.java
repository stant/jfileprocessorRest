package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.RestServerSw;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.utils.Rest;
import java.awt.Color;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppMonitor implements Runnable
    {
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
        System.out.println( "cancelTomcatAppMonitor() set cancelFlag to true" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = true;

        cancelAppThread( forceStop );

        connUserInfo.setConnectedFlag( false );
        SwingUtilities.invokeLater(new Runnable() 
            {
            public void run() {
                jFileFinderWin.setRmtConnectBtnBackgroundReset();
                }
            });

        //Thread.currentThread().interrupt();
        System.out.println("exit cancelTomcatAppMonitor()");
        }
    
    public void cancelAppThread( boolean forceStop )
        {
        System.out.println("TomcatAppMonitor cancelAppThread()");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        if ( runThread != null && runThread.isAlive() )
            {
            System.out.println( "stop/interrupt runThread/tomcatAppThread" );
            try
                {
                tomcatAppThread.cancelTomcatAppThread( forceStop );
                runThread.interrupt();
                runThread.join();
                } 
            catch (InterruptedException ex)
                {
                System.out.println( "TomcatAppMonitor.cancelAppThread() - InterruptedException on runThread.join()" );
                Logger.getLogger(TomcatAppMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println( "TomcatAppMonitor.cancelAppThread() - after runThread.join()" );
            }

        if ( watchStartThread != null && watchStartThread.isAlive() )
            {
            System.out.println( "stop/interrupt watchTomcatAppStartThread" );
            try
                {
                watchTomcatAppStartThread.cancelWatchTomcatAppStartThread( false );
                watchStartThread.interrupt();
                watchStartThread.join();
                } 
            catch (InterruptedException ex)
                {
                System.out.println( "TomcatAppMonitor.cancelAppThread() - InterruptedException on runThread.join()" );
                Logger.getLogger(TomcatAppMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println( "Done/joined watchTomcatAppStartThread" );
            }
        System.out.println("exit TomcatAppMonitor cancelAppThread()");
        }
    
    // Starts 2 threads.
    // watch tomcatapp thread which just tests connection and turns button green or reset. if goes from running to stopped, it calls disconnect.
    // tomcat app thread
    // then waits to be stopped.
    @Override
    public void run() 
        {
        System.out.println( "entered TomcatAppMonitor.run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

        cancelFlag = false;
        int downTimes = 0;

        watchTomcatAppStartThread = new WatchTomcatAppStartThread( restServerSw, connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
        watchStartThread = ProcessInThread.newThread( "watchTomcatAppStartThread", 0, false, watchTomcatAppStartThread );
        watchStartThread.start();

        tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
        runThread = ProcessInThread.newThread( "tomcatAppThread", count++, false, tomcatAppThread );
        runThread.start();
//        tomcatAppThread.run();
        //System.out.println( "after first tomcatAppThread.run()" );
        
        //waitUntilStarted();
        try
            {
            synchronized (this) 
                {
                this.wait();
                }
            } 
        catch (InterruptedException ex)
            {
            System.out.println( "TomcatAppMonitor.wait() - InterruptedException" );
            Logger.getLogger(TomcatAppMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        
//        while ( ! cancelFlag &&  ++downTimes < 5 )
//            {
//            try
//                {
//                tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
////                        runThread = ProcessInThread.newThread( "TomcatAppMonitor", count++, false, tomcatAppThread );
////                        runThread.start();
//                tomcatAppThread.run();
//                System.out.println( "after tomcatAppThread.run()  downTimes =" + downTimes );
//                Thread.sleep( 1000 );
//                }
//            catch( InterruptedException intexc )
//                {
//                System.out.println( "TomcatAppMonitor sleep interrupted" );
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
        //System.out.println( "TomcatAppMonitor.run() STOP any existing running server" );
        //cancelAppThread( false );
        
        //connUserInfo.setConnectedFlag( false );
//        SwingUtilities.invokeLater(new Runnable() 
//            {
//            public void run() {
//                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                }
//            });
        System.out.println( "Exiting TomcatAppMonitor run() - Done" );
        }
                
    public void waitUntilStarted()
        {
        System.out.println( "entered TomcatAppMonitor waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        int waitCount = 15;
        
        while ( (response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX))  && ! cancelFlag )
            {
            System.out.println( "TomcatAppMonitor.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                System.out.println( "TomcatAppMonitor.run() SYS_GET_FILESYS response =" + response );
//                connUserInfo.setToFilesysType(response);
//                System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
                jFileFinderWin.setFilesysType(response);
                System.out.println( "jFileFinderWin.getFilesysType() =" + jFileFinderWin.getFilesysType() );
                }
            catch( Exception exc )
                {
                System.out.println( "TomcatAppMonitor.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            System.out.println( "TomcatAppMonitor.waitUntilStarted response =" + response + "=" );
            try
                {
                if ( cancelFlag )
                    {
                    System.out.println( "cancel so no pause." );
                    }
                else if ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
                    {
                    if ( firstWaitFlag )
                        {
                        firstWaitFlag = false;
                        System.out.println( "pause 12 seconds first time. give longer pause to let server start." );
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
                Logger.getLogger(TomcatAppMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }

            if ( --waitCount < 1 )  break;
            }
        System.out.println( "TomcatAppMonitor waitUntilStarted() after while loop" );

        if ( response < 0 || cancelFlag || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            System.out.println( "TomcatAppMonitor waitUntilStarted() reset button color" );
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
            System.out.println( "TomcatAppMonitor waitUntilStarted() set button color to green for connected" );
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
