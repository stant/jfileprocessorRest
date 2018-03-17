/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.TomcatAppThread;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.JschSftpUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class RestServerSw {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    public String SERVER_URI = "http://localhost:8080";

    JFileFinderWin jFileFinderWin = null;
    Thread runThread = null;
    Thread tomcatAppPostThread = null;
    TomcatAppThread tomcatAppThread = null;
    static long count = 0;
    Object LockObj = "";
    
    public RestServerSw( JFileFinderWin jFileFinderWin )
        {
        this.jFileFinderWin = jFileFinderWin;
        SERVER_URI = "http://" + jFileFinderWin.getRmtHost() + ":8080";
        }

    public RestTemplate timeoutRestTemplate( RestTemplateBuilder restTemplateBuilder )
        {
        return restTemplateBuilder
                .setConnectTimeout(100)
                .setReadTimeout(100)
                .build();
        }
    
    public void cancelRestServer() 
        {
        System.out.println( "enter RestServerSw.cancelRestServer()" );
        if ( tomcatAppThread != null )
            {
            System.out.println( "RestServerSw.cancelRestServer() thread not null to make rest /jfp/sys/stop call" );
            tomcatAppThread.cancelRestServer();
            try
                {
                RestTemplate restTemplate = timeoutRestTemplate( new RestTemplateBuilder() );
                restTemplate.getForObject( SERVER_URI + JfpRestURIConstants.SYS_STOP, String.class );
                } 
            catch (Exception ex)
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println( "RestServerSw.cancelRestServer() - after rest stop call" );
            try
                {
                runThread.join();
                System.out.println( "RestServerSw.cancelRestServer() - after runThread.join()" );
                } 
            catch (InterruptedException ex)
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        if ( tomcatAppPostThread != null )
            {
            System.out.println( "RestServerSw.cancelRestServer() - before tomcatAppPostThread.join()" );
            if ( tomcatAppPostThread.isAlive() )
                {
                try
                    {
                    tomcatAppPostThread.join();
                    } 
                catch (InterruptedException ex)
                    {
                    Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            System.out.println( "RestServerSw.cancelRestServer() - after tomcatAppPostThread.join()" );
            }
        System.out.println( "exit RestServerSw.cancelRestServer()" );
        }

    public void actionPerformed(java.awt.event.ActionEvent evt) 
        {
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.cancelRestServer();
            }
        else
            {
            try {
                System.out.println( "TomcatAppSw doCmdBtnActionPerformed start" );
                System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//                RestServerSwingWorker = new TomcatAppSwingWorker( jFileFinderWin, this, startingPath );
//                tomcatApp = RestServerSwingWorker.getTomcatApp();
//                RestServerSwingWorker.execute();   //doInBackground();

//                TomcatAppPost tomcatAppPost = new TomcatAppPost( jFileFinderWin, LockObj );
//                tomcatAppPostThread = newThread( tomcatAppPost );
//                tomcatAppPostThread.setName( "watchdirPostThread=" + count );
//                tomcatAppPostThread.start();

                tomcatAppThread = new TomcatAppThread( jFileFinderWin.getRmtUser(), jFileFinderWin.getRmtPasswd(), jFileFinderWin.getRmtHost() );
                runThread = newThread( tomcatAppThread );
                runThread.setName( "tomcatAppThread=" + count++ );
                runThread.start();
                waitUntilStarted();
                System.out.println( "RestServerSw after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) 
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }

    public void waitUntilStarted()
        {
        System.out.println( "entered TomcatAppThread waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate restTemplate = new RestTemplate();
        boolean isRunning = false;
        String response = null;
        
        while ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) )
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
            System.out.println( "waitUntilStarted response =" + response + "=" );
            try
                {
                if ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) )
                    {
                    Thread.sleep( 4000 );
                    }
                } 
            catch (InterruptedException ex)
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
                
    public Thread newThread(final Runnable r) 
        {
        Thread thread = new Thread( r );
        thread.setName( "tomcatApp" + thread.getName());
        thread.setDaemon(true);
        return thread;
        }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TomcatAppSwFrame().setVisible(true);
//            }
//        });
    }
}
