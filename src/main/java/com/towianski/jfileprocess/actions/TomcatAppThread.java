package com.towianski.jfileprocess.actions;

import com.towianski.boot.JFileProcessorVersion;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.utils.Rest;
import java.awt.Color;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppThread implements Runnable
    {
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
    public TomcatAppThread( ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
        {
        this.connUserInfo = connUserInfo;
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.jFileFinderWin = jFileFinderWin;
        }
    
    public void cancelTomcatAppThread( boolean forceStop ) 
        {
        System.out.println("TomcatAppThread set cancelFlag to true - forceStop =" + forceStop + "   StartedServer =" + startedServer );
        cancelFlag = true;
//        if ( isStartedServer() || forceStop )
        if ( iStartedServer || forceStop )
            {
            System.out.println( "TomcatAppThread.cancelTomcatAppThread() thread not null to make rest /jfp/sys/stop call" );
            try
                {
                RestTemplate restTemplate = Rest.createNoHostVerifyRestTemplate();
                restTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_STOP, String.class );
                } 
            catch (Exception ex)
                {
                Logger.getLogger(TomcatAppThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        else
            {
            System.out.println( "TomcatAppThread.cancelTomcatAppThread() - I did Not start server so just stop myself" );
            //notify();
            }
//        else
//            {
//            System.out.println("TomcatAppThread.cancelRestServer() - stop prev running thread");
////            Thread.currentThread().interrupt();
//            notify();
//            }
//        Thread.currentThread().interrupt();   maybe did not work?
        System.out.println("TomcatAppThread exit cancelTomcatAppThread()");
        }

    // Now this runs once and exits. It does not loop
    @Override
    public void run() {
        System.out.println( "entered TomcatAppThread run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

        cancelFlag = false;
        JschSftpUtils jschSftpUtils = new JschSftpUtils();
        String response = null;
        
            try
                {
                System.out.println( "TomcatAppThread.run() make rest /jfp/sys/ping call" );
                try
                    {
                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING, String.class );
                    }
                catch( Exception exc )
                    {
                    System.out.println( "TomcatAppThread.run() ping threw Exception !!" );
                    response = null;
                    SwingUtilities.invokeLater(new Runnable() 
                        {
                        public void run() {
                            jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
                            }
                        });
                    exc.printStackTrace();
                    }
                System.out.println( "TomcatAppThread.run() ping response =" + response + "=" );
                
                if ( ! cancelFlag && 
                    ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
                    {
//                    String[] mainCommand = System.getProperty("sun.java.command").split(" ");
//                    String jfpFilename = mainCommand[0];
//                    System.out.println( "jfpFilename =" + jfpFilename + "=" );
//                        String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + "build" + System.getProperty( "file.separator" ) + "libs" + System.getProperty( "file.separator" );
                    String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" );
//                    fpath = fpath.replace( "-Gui", "-Server" );
//                    System.out.println( "jfpFilename =" + jfpFilename + "=" );
                    String jfpFilename = JFileProcessorVersion.getName() + "-" + JFileProcessorVersion.getVersion() + ".jar";  //"JFileProcessor-1.6.0.jar";
                    System.out.println( "set jfpFilename =" + jfpFilename + "=" );
                    System.out.println( "try jschSftpUtils   file =" + fpath + jfpFilename + "=   to remote =" + user + "@" + rmtHost + ":" + jfpFilename + "=" );

                    jFileFinderWin.setMessage( "copy server to remote" );
                    //jschSftpUtils.copyIfMissing( fpath + jfpFilename, user, passwd, rmtHost, jfpFilename );
                    String errMsg = jschSftpUtils.sftpIfDiff( fpath + jfpFilename, user, passwd, rmtHost, jfpFilename );
                    if ( ! errMsg.equals( "" ) )
                        {
                        JOptionPane.showMessageDialog( null, "Could not connect. Is sftp subsystem configured in ssh?", "Error", JOptionPane.ERROR_MESSAGE );
                        cancelFlag = true;
                        }
                    if ( ! cancelFlag )
                        {
                        jFileFinderWin.setMessage( "start remote server" );
                        String runCmd = "java -Dserver.port=" + System.getProperty( "server.port", "8443" ) + " -jar " + jfpFilename + " --server --logging.file=/tmp/jfp-springboot.logging";

                        if ( jschSftpUtils.isRemoteDos( user, passwd, rmtHost ) )
                            {
                            runCmd = "powershell.exe Start-Process -FilePath java -ArgumentList '-Dserver.port=" + System.getProperty( "server.port", "8443" ) + " -jar " + jfpFilename + " --server --logging.file=/tmp/jfp-springboot.logging" + "' -Wait";
                            }
                        
                        System.out.println( "start remote server with runCmd =" + runCmd + "=" );
                        iStartedServer = true;
                        setStartedServer( true );
                        jschSftpUtils.exec( user, passwd, rmtHost, runCmd );
                        //java -jar your-spring.jar --security.require-ssl=true --server.port=8443 --server.ssl.key-store=keystore --server.ssl.key-store-password=changeit --server.ssl.key-password=changeit
                        }
                    System.out.println( "after exec remote jfp server" );
//                    if ( ! cancelFlag )    // If I can get windows -Wait to work I do not want this wait here.
//                        {
//                        waitUntilNotified();
//                        }
                    }
                else
                    {
                    System.out.println( "using prev running remote jfp server" );
//                    waitUntilNotified();
                    synchronized (this) 
                        {
                        this.wait();
                        }
                    System.out.println( "after wait using remote jfp server" );
                    }
                } 
            catch (InterruptedException ex) 
                {
                System.out.println( "TomcatAppThread.run() Interrupted" );
                Logger.getLogger(TomcatAppThread.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println( "Exiting TomcatAppThread run() - Done" );
        }

    public synchronized void waitUntilNotified()
        {
        System.out.println( "entered waitUntilNotified()" );
        try {
            wait();
            } 
        catch (InterruptedException ex) 
            {
            System.out.println( "Interrupted in waitUntilNotified()" );
            Logger.getLogger(TomcatAppThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        cancelFlag = true;
        setStartedServer( false );
        SwingUtilities.invokeLater(new Runnable() 
            {
            public void run() {
                jFileFinderWin.setRmtConnectBtnBackgroundReset();
                }
            });
        System.out.println( "exit waitUntilNotified()" );
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
