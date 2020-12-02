package com.towianski.jfileprocess.actions;

import com.towianski.httpsutils.HttpsUtils;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.awt.Color;
import java.io.*;
import javax.swing.SwingUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppHttpsThread extends TomcatAppThread   //implements Runnable
    {
    private static final MyLogger logger = MyLogger.getLogger(TomcatAppHttpsThread.class.getName() );

    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    JFileFinderWin jFileFinderWin = null;
    boolean iStartedServer = false;
    boolean startedServer = false;
    Object WaitOnMe = null;
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public TomcatAppHttpsThread( ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
        {
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelTomcatAppThread( boolean forceStop ) 
        {
        logger.info( "TomcatAppThread set cancelFlag to true - forceStop =" + forceStop + "   StartedServer =" + startedServer );
        cancelFlag = true;
//        if ( isStartedServer() || forceStop )
        if ( iStartedServer || forceStop )
            {
            logger.info( "TomcatAppThread.cancelTomcatAppThread() thread not null to make rest /jfp/sys/stop call" );
            try
                {
                RestTemplate restTemplate = Rest.createNoHostVerifyRestTemplate();
                restTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_STOP, String.class );
                } 
            catch (Exception ex)
                {
                logger.severeExc( ex );
                }
            }
        else
            {
            logger.info( "TomcatAppThread.cancelTomcatAppThread() - I did Not start server so just stop myself" );
            // Start a msgBox
            {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new MsgBoxFrame( "I did Not start server so just disconnect myself." ).setVisible( true );
                    }
                });
            }
            //notify();
            }
//        else
//            {
//            logger.info( "TomcatAppThread.cancelRestServer() - stop prev running thread");
////            Thread.currentThread().interrupt();
//            notify();
//            }
//        Thread.currentThread().interrupt();   maybe did not work?
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
        logger.info( "TomcatAppThread exit cancelTomcatAppThread()");
        }

    // Now this runs once and exits. It does not loop
    @Override
    public void run() {
        try
            {
            logger.info( "entered TomcatAppThread run()" );
            logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

            cancelFlag = false;
            HttpsUtils httpsUtils = new HttpsUtils( "TO", connUserInfo );

            String response = null;

            logger.info( "TomcatAppThread.run() make rest /jfp/sys/ping call" );
            try
                {
//                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING, String.class );
                HttpEntity request = new HttpEntity( Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() ) );

                // make an HTTP GET request with headers
                ResponseEntity<String> responseEntity = noHostVerifyRestTemplate.exchange(
                        connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING,
                        HttpMethod.GET,
                        request,
                        String.class
                );
                response = responseEntity.getBody().toString();
                }
            catch( Exception exc )
                {
                logger.info( "TomcatAppThread.run() ping threw Exception !!" );
                response = null;
                SwingUtilities.invokeLater(new Runnable() 
                    {
                    public void run() {
                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
                        }
                    });
                exc.printStackTrace();
                }
            logger.info( "TomcatAppThread.run() ping response =" + response + "=" );

            logger.info( "TomcatAppThread.run() make rest /jfp/rest/connect call" );
            try
                {
//                HttpHeaders headers = Rest.getHeaders( user, password );
//                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                logger.info( "TomcatAppThread connect() with user =" + connUserInfo.getToUser() + "=   pass =" + connUserInfo.getToPassword() + "=" );

                HttpEntity request = new HttpEntity( Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() ) );

                // make an HTTP GET request with headers
                ResponseEntity<String> responseEntity = noHostVerifyRestTemplate.exchange(
                        connUserInfo.getToUri() + JfpRestURIConstants.HTTPS_CONNECT,
                        HttpMethod.GET,
                        request,
                        String.class
                );

                // check response
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    System.out.println("Request Successful.");
                    System.out.println(responseEntity.getBody());
                } else {
                    System.out.println("Request Failed");
                    System.out.println(responseEntity.getStatusCode());
                    throw new Exception();
                }
                }
            catch( Exception exc )
                {
                logger.info( "TomcatAppThread.run() https_connect threw Exception !!" );
                response = null;
                SwingUtilities.invokeLater(new Runnable() 
                    {
                    public void run() {
                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
                        }
                    });
                exc.printStackTrace();
                }

            if ( ! cancelFlag && 
                ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
                {
                logger.info( "remote jfp server NOT Connected !" );
                //Thread.sleep( 5000 );
                }
            else
                {
                logger.info( "using prev running remote jfp server" );
//                    waitUntilNotified();
                synchronized (this) 
                    {
                    this.wait();
                    }
                logger.info( "after wait using remote jfp server" );
                }
            }
        catch (InterruptedException ex) 
            {
            logger.info( "TomcatAppThread.run() Interrupted" );
            logger.severeExc( ex );
            setStartedServer( false );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }

        SwingUtilities.invokeLater(new Runnable() 
            {
            public void run() {
                jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
                }
            });
//        SwingUtilities.invokeLater(new Runnable() 
//            {
//            public void run() {
//                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                }
//            });
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
        logger.info( "Exiting TomcatAppThread run() - Done" );
        }

    public synchronized void waitUntilNotified()
        {
        logger.info( "entered waitUntilNotified()" );
        try {
            wait();
            } 
        catch (InterruptedException ex) 
            {
            logger.info( "Interrupted in waitUntilNotified()" );
            logger.severeExc( ex );
            }
        cancelFlag = true;
        setStartedServer( false );
        SwingUtilities.invokeLater(new Runnable() 
            {
            public void run() {
                jFileFinderWin.setRmtConnectBtnBackgroundReset();
                }
            });
        logger.info( "exit waitUntilNotified()" );
        }
    
    public boolean isStartedServer() {
        return startedServer;
    }

    public void setStartedServer(boolean startedServer) {
        this.startedServer = startedServer;
//        if ( startedServer )
//            {
//            connUserInfo.setConnectedFlag( true );
//            SwingUtilities.invokeLater(new Runnable() 
//                {
//                public void run() {
//                    jFileFinderWin.setRmtConnectBtnBackground( Color.green );
//                    }
//                });
//            }
    }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
