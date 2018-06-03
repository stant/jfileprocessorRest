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
public class WatchTomcatAppStartThread implements Runnable
    {
    RestServerSw restServerSw = null;
    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    JFileFinderWin jFileFinderWin = null;
    boolean isRunning = false;
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchTomcatAppStartThread( RestServerSw restServerSw, ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
        {
        this.restServerSw = restServerSw;
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelWatchTomcatAppStartThread( boolean forceStop ) 
        {
        System.out.println("WatchTomcatAppStartThread set cancelFlag to true - forceStop =" + forceStop );
        cancelFlag = true;
        }

    @Override
    public void run() {
        System.out.println( "entered WatchTomcatAppStartThread waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean firstWaitFlag = true;
        int response = -9;
        //int waitCount = 15;
        
        try
            {
            //while ( (response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX))  && ! cancelFlag )
            while ( ! cancelFlag )
                {
                System.out.println( "WatchTomcatAppStartThread.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
                try
                    {
                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                    //jFileFinderWin.setFilesysType(response);
                    //System.out.println( "jFileFinderWin.getFilesysType() =" + jFileFinderWin.getFilesysType() );
                    }
                catch( Exception exc )
                    {
                    System.out.println( "WatchTomcatAppStartThread.run() get filesys rest threw Exception !!" );
                    exc.printStackTrace();
                    response = -9;
                    }
                System.out.println( "WatchTomcatAppStartThread SYS_GET_FILESYS response =" + response + "=" );

                if ( response < 0 || cancelFlag || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
                    {
                    if ( isRunning )  // diff so flip status
                        {
                        System.out.println( "WatchTomcatAppStartThread was running now is not. reset button color" );
//                        connUserInfo.setConnectedFlag( false );
//                        jFileFinderWin.setFilesysType(response);
//                        SwingUtilities.invokeLater(new Runnable() 
//                            {
//                            public void run() {
//                                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                                }
//                            });
                        restServerSw.cancelRestServer( false );   // no force stop
                        }
                    isRunning = false;
                    }
                else
                    {
                    if ( ! isRunning )  // diff so flip status
                        {
                        System.out.println( "WatchTomcatAppStartThread waitUntilStarted() set button color to green for connected" );
                        connUserInfo.setConnectedFlag( true );
                        jFileFinderWin.setFilesysType(response);
                        SwingUtilities.invokeLater(new Runnable() 
                            {
                            public void run() {
                                jFileFinderWin.setRmtConnectBtnBackground( Color.green );
                                }
                            });
                        }
                    isRunning = true;
                    }

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
                    }
                //System.out.println( "pause 4 secs" );
                Thread.sleep( 4000 );
                } // while
            }
        catch (InterruptedException ex)
            {
            Logger.getLogger(WatchTomcatAppStartThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println( "WatchTomcatAppStartThread() - Done" );
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
