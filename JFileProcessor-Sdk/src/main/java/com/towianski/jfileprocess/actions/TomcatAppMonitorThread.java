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
public class TomcatAppMonitorThread implements Runnable
    {
    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    JFileFinderWin jFileFinderWin = null;
    RestServerSw restServerSw = null;
    boolean startedServer = false;
    TomcatAppThread tomcatAppThread = null;
    Thread runThread = null;
    int count = 0;
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public TomcatAppMonitorThread( ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin, RestServerSw restServerSw )
        {
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelRestServer( boolean forceStop )
        {
        System.out.println("TomcatAppMonitorThread set cancelFlag to true");
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

        Thread.currentThread().interrupt();
        System.out.println("TomcatAppMonitorThread exit cancelSearch()");
        }
    
    public void cancelAppThread( boolean forceStop )
        {
        System.out.println("TomcatAppMonitorThread cancelAppThread()");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        if ( tomcatAppThread != null )
            {
            tomcatAppThread.cancelRestServer( forceStop );
            runThread.interrupt();
            try
                {
                runThread.join();
                System.out.println( "TomcatAppMonitorThread.cancelRestServer() - after runThread.join()" );
                } 
            catch (InterruptedException ex)
                {
                System.out.println( "TomcatAppMonitorThread.cancelRestServer() - ERROR on runThread.join()" );
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        System.out.println("exit TomcatAppMonitorThread cancelAppThread()");
        }
    
    @Override
    public void run() 
        {
        System.out.println( "entered TomcatAppMonitorThread.run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

        cancelFlag = false;
        int downTimes = 99;
        String response = null;
        boolean didFirstStart = false;

        tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
        runThread = newThread( tomcatAppThread );
        runThread.setName( "TomcatAppMonitorThread=" + count++ );
        runThread.start();

        waitUntilStarted();
        
        while ( ! cancelFlag )
            {
            try
                {
                System.out.println( "TomcatAppMonitorThread.run() make rest /jfp/sys/ping call" );
                try
                    {
                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING, String.class );
                    }
                catch( Exception exc )
                    {
                    System.out.println( "TomcatAppMonitorThread.run() ping threw Exception !!" );
                    response = null;
//                    SwingUtilities.invokeLater(new Runnable() 
//                        {
//                        public void run() {
//                            jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                            }
//                        });
                    exc.printStackTrace();
                    }
                System.out.println( "TomcatAppMonitorThread.run() ping response =" + response + "=" );
                
                if ( ! cancelFlag && 
                    ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
                    {
                    if ( ++downTimes > 5 )
                        {
                        if ( runThread != null && runThread.isAlive() )
                            {
                            System.out.println( "TomcatAppMonitorThread.run() STOP any existing running server" );
                            cancelAppThread( false );
                            }
                        tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
                        runThread = newThread( tomcatAppThread );
                        runThread.setName( "TomcatAppMonitorThread=" + count++ );
                        runThread.start();
                
                        waitUntilStarted();

//                        SwingUtilities.invokeLater(new Runnable() 
//                            {
//                            public void run() {
//                                jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                                }
//                            });
                        System.out.println( "TomcatAppMonitorThread after start remote jfp server" );
//                        Thread.sleep( 30000 );
                        downTimes = 0;
                        }
                    else
                        {
                        Thread.sleep( 1000 );
                        }
                    }
                else   //if ( didFirstStart ) // is running or first start ?
                    {
                    Thread.sleep( 10000 );
                    }
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
                }
            catch( InterruptedException intexc )
                {
                System.out.println( "TomcatAppMonitorThread sleep interrupted" );
//                SwingUtilities.invokeLater(new Runnable() 
//                    {
//                    public void run() {
//                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                        }
//                    });
                }
            catch( Exception exc )
                {
//                SwingUtilities.invokeLater(new Runnable() 
//                    {
//                    public void run() {
//                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                        }
//                    });
                exc.printStackTrace();
                }
            didFirstStart = true;
            } // while
        
        connUserInfo.setConnectedFlag( false );
//        SwingUtilities.invokeLater(new Runnable() 
//            {
//            public void run() {
//                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                }
//            });
        System.out.println( "Exiting TomcatAppMonitorThread run() - Done" );
        }
                
    public Thread newThread(final Runnable r) 
        {
        Thread thread = new Thread( r );
        thread.setName( "TomcatAppThread" + thread.getName());
        thread.setDaemon(true);
        return thread;
        }

    public void waitUntilStarted()
        {
        System.out.println( "entered TomcatAppMonitorThread waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        int waitCount = 15;
        
        while ( response  <0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            System.out.println( "TomcatAppMonitorThread.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                System.out.println( "RestServerSw.run() SYS_GET_FILESYS response =" + response );
                connUserInfo.setToFilesysType(response);
                System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
                }
            catch( Exception exc )
                {
                System.out.println( "TomcatAppMonitorThread.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            System.out.println( "TomcatAppMonitorThread.waitUntilStarted response =" + response + "=" );
            try
                {
                if ( response <0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
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
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }

            if ( --waitCount < 1 )  break;
            }

        if ( response  <0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
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
