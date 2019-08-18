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
public class WatchTomcatAppStartThread implements Runnable
    {
    private static final MyLogger logger = MyLogger.getLogger( WatchTomcatAppStartThread.class.getName() );
    RestServerSw restServerSw = null;
    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    JFileFinderWin jFileFinderWin = null;
    Thread runThread = null;
    boolean isRunning = false;
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchTomcatAppStartThread( RestServerSw restServerSw, Thread runThread, ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
        {
        this.restServerSw = restServerSw;
        this.runThread = runThread;
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelWatchTomcatAppStartThread( boolean forceStop ) 
        {
        logger.info( "WatchTomcatAppStartThread set cancelFlag to true - forceStop =" + forceStop );
        cancelFlag = true;
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
        }

    @Override
    public void run() {
        try
            {
        logger.info( "entered WatchTomcatAppStartThread waitUntilStarted()" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        //int waitCount = 15;
        
            //while ( (response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX))  && ! cancelFlag )
            while ( ! cancelFlag && (connUserInfo.getState() != ConnUserInfo.STATE_CANCEL) && runThread != null && runThread.isAlive() )
                {
                logger.info( "WatchTomcatAppStartThread.run() make rest " + connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
                try
                    {
                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                    //jFileFinderWin.setFilesysType(response);
                    //logger.info( "jFileFinderWin.getFilesysType() =" + jFileFinderWin.getFilesysType() );
                    }
                catch( Exception exc )
                    {
                    logger.info( "WatchTomcatAppStartThread.run() get filesys rest threw Exception !!" );
                    exc.printStackTrace();
                    response = -9;
                    }
                logger.info( "WatchTomcatAppStartThread SYS_GET_FILESYS response =" + response + "=" );

                if ( response < 0 || cancelFlag || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
                    {
                    if ( isRunning )  // diff so flip status
                        {
                        logger.info( "WatchTomcatAppStartThread was running but now is not. reset button color" );
                        connUserInfo.setConnectedFlag( false );
                        //jFileFinderWin.setFilesysType(response);
                        SwingUtilities.invokeLater(new Runnable() 
                            {
                            public void run() {
                                jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
                                jFileFinderWin.setRmtToUsingHttpsPortToolTip( "" );
                                }
                            });
                        restServerSw.cancelRestServer( false );   // no force stop
                        }
                    isRunning = false;
                    }
                else
                    {
                    logger.info( "WatchTomcatAppStartThread isRunning = " + isRunning );
                    if ( ! isRunning )  // diff so flip status
                        {
                        logger.info( "WatchTomcatAppStartThread waitUntilStarted() set button color to green for connected" );
                        connUserInfo.setConnectedFlag( true );
                        connUserInfo.setToFilesysType( response );
                        jFileFinderWin.setFilesysType( response );
                        SwingUtilities.invokeLater(new Runnable() 
                            {
                            public void run() {
                                jFileFinderWin.setRmtConnectBtnBackground( Color.green );
                                jFileFinderWin.setRmtToUsingHttpsPortToolTip( connUserInfo.getToUsingHttpsPort() );
                                jFileFinderWin.setMessage( "remote https server at port " + connUserInfo.getToUsingHttpsPort() );
                                }
                            });
                        }
                    isRunning = true;
                    }

                if ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
                    {
                    if ( firstWaitFlag )
                        {
                        firstWaitFlag = false;
                        logger.info( "pause 12 seconds first time. give longer pause to let server start." );
                        Thread.sleep( 12000 );
                        }
                    }
                //logger.info( "pause 4 secs" );
                if ( ! cancelFlag && (connUserInfo.getState() != ConnUserInfo.STATE_CANCEL) )
                    Thread.sleep( 4000 );
                } // while
            }
        catch (InterruptedException ex)
            {
            logger.severeExc( ex );
            logger.info( "WatchTomcatAppStartThread() interrupted" );
            }
        logger.info( "WatchTomcatAppStartThread() - Done" );
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
