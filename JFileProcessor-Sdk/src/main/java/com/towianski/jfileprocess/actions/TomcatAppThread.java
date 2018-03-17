package com.towianski.jfileprocess.actions;

import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.JschSftpUtils;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppThread implements Runnable
    {
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    Object lockObj = null;
    String SERVER_URI = "http://" + rmtHost + ":8080";

    /**
     * Creates a WatchService and registers the given directory
     */
    public TomcatAppThread( String user, String passwd, String rmtHost )
        {
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.lockObj = lockObj;
        SERVER_URI = "http://" + rmtHost + ":8080";
        }
    
    public void cancelRestServer()
        {
        System.out.println("TomcatAppThread set cancelFlag to true");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = true;
        System.out.println("TomcatAppThread exit cancelSearch()");
        }

    @Override
    public void run() {
        System.out.println( "entered TomcatAppThread run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate restTemplate = new RestTemplate();
        cancelFlag = false;
        int downTimes = 99;
        JschSftpUtils scpTo = new JschSftpUtils();
        String response = null;
        boolean didFirstStart = false;
        
        while ( ! cancelFlag )
            {
            try
                {
                System.out.println( "TomcatAppThread.run() make rest /jfp/sys/ping call" );
                try
                    {
                    response = restTemplate.getForObject( SERVER_URI + JfpRestURIConstants.SYS_PING, String.class );
                    }
                catch( Exception exc )
                    {
                    response = null;
                    exc.printStackTrace();
                    }
                System.out.println( "ping response =" + response + "=" );
                if ( ! cancelFlag && 
                    ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
                    {
                    if ( ++downTimes > 5 )
                        {
//                        System.out.println( "RestServerSw.cancelRestServer() thread not null to make rest /jfp/sys/stop call" );
//                        restTemplate.getForObject(SERVER_URI + JfpRestURIConstants.SYS_STOP, String.class );

                        String[] mainCommand = System.getProperty("sun.java.command").split(" ");
//                        String jfpFilename = menuS mainCommand[0];
//                        System.out.println( "jfpFilename =" + jfpFilename + "=" );
//                        String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + "build" + System.getProperty( "file.separator" ) + "libs" + System.getProperty( "file.separator" );
                        String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" );
                        fpath = fpath.replace( "-Gui", "-Server" );
//                        System.out.println( "jfpFilename =" + jfpFilename + "=" );
                        String jfpFilename = "JFileProcessor-Server-1.5.11.jar";
                        System.out.println( "jfpFilename =" + jfpFilename + "=" );
                        scpTo.copyIfMissing( fpath + jfpFilename, user, passwd, rmtHost, jfpFilename );
                        System.out.println( "try scpTo   file =" + fpath + jfpFilename + "=   to remote =" + user + "@" + rmtHost + ":" + jfpFilename + "=" );
                        if ( ! cancelFlag )
                            {
                            scpTo.exec( user, passwd, rmtHost, "java -jar " + jfpFilename + " --logging.file=/tmp/jfp-springboot.logging" );
                            }
                        System.out.println( "after start remote jfp server" );
                        Thread.sleep( 30000 );
                        downTimes = 0;
                        }
                    else
                        {
                        Thread.sleep( 1000 );
                        }
                    }
                else if ( didFirstStart ) // is running or first start
                    {
                    Thread.sleep( 4000 );
                    downTimes = 0;
                    }
                }
            catch( Exception exc )
                {
                exc.printStackTrace();
                }
            didFirstStart = true;
            } // while
        System.out.println( "Exiting TomcatAppThread run() - Done" );
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
