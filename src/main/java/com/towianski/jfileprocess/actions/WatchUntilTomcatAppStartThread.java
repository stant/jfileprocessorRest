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
public class WatchUntilTomcatAppStartThread implements Runnable
    {
    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    JFileFinderWin jFileFinderWin = null;
    RestServerSw restServerSw = null;
    boolean startedServer = false;
    Object WaitOnMe = null;
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchUntilTomcatAppStartThread( ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
        {
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelRestServer( boolean forceStop ) 
        {
        System.out.println("WatchUntilTomcatAppStartThread set cancelFlag to true - forceStop =" + forceStop );
        cancelFlag = true;
//        if ( isStartedServer() || forceStop )
//            {
//            System.out.println( "WatchUntilTomcatAppStartThread.cancelRestServer() thread not null to make rest /jfp/sys/stop call" );
//            try
//                {
//                RestTemplate restTemplate = Rest.createNoHostVerifyRestTemplate();
//                restTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_STOP, String.class );
//                } 
//            catch (Exception ex)
//                {
//                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        else
//            {
//            System.out.println("WatchUntilTomcatAppStartThread.cancelRestServer() - stop prev running thread");
////            Thread.currentThread().interrupt();
//            notify();
//            }
        System.out.println("WatchUntilTomcatAppStartThread exit cancelSearch()");
        }

    @Override
    public void run() {
        System.out.println( "entered WatchUntilTomcatAppStartThread waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        int waitCount = 15;
        
        while ( (response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX))  && ! cancelFlag )
            {
            System.out.println( "WatchUntilTomcatAppStartThread.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                System.out.println( "RestServerSw.run() SYS_GET_FILESYS response =" + response );
//                connUserInfo.setToFilesysType(response);
//                System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
                jFileFinderWin.setFilesysType(response);
                System.out.println( "jFileFinderWin.getFilesysType() =" + jFileFinderWin.getFilesysType() );
                }
            catch( Exception exc )
                {
                System.out.println( "WatchUntilTomcatAppStartThread.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            System.out.println( "WatchUntilTomcatAppStartThread.waitUntilStarted response =" + response + "=" );
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
                Logger.getLogger(WatchUntilTomcatAppStartThread.class.getName()).log(Level.SEVERE, null, ex);
                }

//            if ( --waitCount < 1 )  break;
            }
        System.out.println( "WatchUntilTomcatAppStartThread waitUntilStarted() after while loop" );

        if ( response < 0 || cancelFlag || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            System.out.println( "WatchUntilTomcatAppStartThread waitUntilStarted() reset button color" );
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
            System.out.println( "WatchUntilTomcatAppStartThread waitUntilStarted() set button color to green for connected" );
            connUserInfo.setConnectedFlag( true );
            SwingUtilities.invokeLater(new Runnable() 
                {
                public void run() {
                    jFileFinderWin.setRmtConnectBtnBackground( Color.green );
                    }
                });
            }
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
