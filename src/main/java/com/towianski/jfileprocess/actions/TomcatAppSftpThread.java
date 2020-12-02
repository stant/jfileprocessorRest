package com.towianski.jfileprocess.actions;

import com.towianski.boot.JFileProcessorVersion;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.awt.Color;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppSftpThread extends TomcatAppThread  // implements Runnable, 
    {
    private static final MyLogger logger = MyLogger.getLogger(TomcatAppSftpThread.class.getName() );

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
    public TomcatAppSftpThread( ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
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
            JschSftpUtils jschSftpUtils = new JschSftpUtils();
            String response = null;

            logger.info( "TomcatAppThread.run() make rest /jfp/sys/ping call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING, String.class );
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

            if ( ! cancelFlag && 
                ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
                {
//                    String[] mainCommand = System.getProperty("sun.java.command").split(" ");
//                    String jfpFilename = mainCommand[0];
//                    logger.info( "jfpFilename =" + jfpFilename + "=" );
//                        String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + "build" + System.getProperty( "file.separator" ) + "libs" + System.getProperty( "file.separator" );
                String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" );
//                    fpath = fpath.replace( "-Gui", "-Server" );
//                    logger.info( "jfpFilename =" + jfpFilename + "=" );
                String jfpFilename = JFileProcessorVersion.getFileName();
                logger.info( "set jfpFilename =" + jfpFilename + "=" );
                logger.info( "try jschSftpUtils   file =" + fpath + jfpFilename + "=   to remote =" + user + "@" + rmtHost + ":" + jfpFilename + "=" );

                jFileFinderWin.setMessage( "copy server to remote" );
                if ( connUserInfo.isUsingSftp() )
                    {
                    //jschSftpUtils.copyIfMissing( fpath + jfpFilename, user, passwd, rmtHost, jfpFilename );
                    String errMsg = jschSftpUtils.sftpIfDiff( fpath + jfpFilename, user, passwd, rmtHost, connUserInfo.getToSshPortInt(), jfpFilename );
                    if ( ! errMsg.equals( "" ) )
                        {
                        JOptionPane.showMessageDialog( null, "Could not connect. Is sftp subsystem configured in ssh?", "Error", JOptionPane.ERROR_MESSAGE );
                        cancelFlag = true;
                        }
                    }
                if ( ! cancelFlag )
                    {
                    jFileFinderWin.setMessage( "start remote https server" );
                    // today's date
                    Date today = Calendar.getInstance().getTime();
                    // date "formatter" (the date format we want)
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-hh.mm.ss");
                    // (3) create a new String using the date format we want
                    String serverLogFileName = "jfp-springboot-" + formatter.format(today) + ".log";

                    String runCmd = "java -Dserver.port=" + connUserInfo.getToAskHttpsPort() + " -jar " + jfpFilename + " --server --logging.file=.JFileProcessor/" + serverLogFileName;

                    if ( jschSftpUtils.isRemoteDos( user, passwd, rmtHost, connUserInfo.getToSshPortInt() ) )
                        {
                        //runCmd = "powershell.exe Start-Process -FilePath java -ArgumentList '-Dserver.port=" + connUserInfo.getToAskHttpsPort() + " -jar " + jfpFilename + " --server --logging.file=" + DesktopUtils.getTmpDir() + "\\jfp-springboot-" + "-" + connUserInfo.getToUsingHttpsPort() + ".logging" + "' -Wait";
                        //runCmd = "powershell.exe Start-Process -FilePath java -ArgumentList '-Dserver.port=" + connUserInfo.getToAskHttpsPort() + " -jar " + jfpFilename + " --server " + "' -Wait";
                        runCmd = "powershell.exe Start-Process -NoNewWindow -FilePath java -ArgumentList '-Dserver.port=" + connUserInfo.getToAskHttpsPort() + " -jar " + jfpFilename + " --server --logging.file=.JFileProcessor\\" + serverLogFileName + " " + "' -Wait";
                        }

                    logger.info( "start remote server with runCmd =" + runCmd + "=" );
                    iStartedServer = true;
                    setStartedServer( true );
                    jschSftpUtils.exec( user, passwd, rmtHost, connUserInfo, runCmd );
                    //java -jar your-spring.jar --security.require-ssl=true --server.port=8443 --server.ssl.key-store=keystore --server.ssl.key-store-password=changeit --server.ssl.key-password=changeit
                    }
                logger.info( "after exec remote jfp server" );
//                    if ( ! cancelFlag )    // If I can get windows -Wait to work I do not want this wait here.
//                        {
//                        waitUntilNotified();
//                        }
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
