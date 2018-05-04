/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.TomcatAppMonitor;
import com.towianski.jfileprocess.actions.TomcatAppThread;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.utils.Rest;
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
    ConnUserInfo connUserInfo = null;
    JFileFinderWin jFileFinderWin = null;
    boolean cancelFlag = false;
    TomcatAppMonitor tomcatAppMonitor = null;
    Thread runThread = null;
    int count = 0;
    
    public RestServerSw( ConnUserInfo connUserInfo, JFileFinderWin jFileFinderWin )
        {
        this.connUserInfo = connUserInfo;
        this.jFileFinderWin = jFileFinderWin;
        }

    public RestTemplate timeoutRestTemplate( RestTemplateBuilder restTemplateBuilder )
        {
        return restTemplateBuilder
                .setConnectTimeout(100)
                .setReadTimeout(100)
                .build();
        }

    public void cancelRestServer( boolean forceStop ) 
        {
        System.out.println( "enter RestServerSw.cancelRestServer()" );
        if ( tomcatAppMonitor != null )
            {
            tomcatAppMonitor.cancelRestServer( forceStop );
            try
                {
                runThread.join();
                System.out.println( "RestServerSw.cancelRestServer() - after runThread.join()" );
                } 
            catch (InterruptedException ex)
                {
                System.out.println( "RestServerSw.cancelRestServer() - ERROR on runThread.join()" );
                ex.printStackTrace();
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        else   // no thread already running
            {
            TomcatAppThread tomcatAppThread = new TomcatAppThread( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin );
            tomcatAppThread.cancelRestServer(forceStop);
            }
        System.out.println( "exit RestServerSw.cancelRestServer()" );
        }

    public void actionPerformed(java.awt.event.ActionEvent evt) {                                         
//        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
        if ( 1 ==2  && ! connUserInfo.isConnectedFlag() )
            {
            this.cancelRestServer( false );
            }
        else
            {
            try {
                System.out.println( "RestServerSw doCmdBtnActionPerformed start" );
                System.out.println( "   on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                System.out.println( "   connUserInfo = " + connUserInfo );
//                RestServerSwingWorker = new TomcatAppSwingWorker( jFileFinderWin, this, startingPath );
//                tomcatApp = RestServerSwingWorker.getTomcatApp();
//                RestServerSwingWorker.execute();   //doInBackground();

//                TomcatAppPost tomcatAppPost = new TomcatAppPost( jFileFinderWin, LockObj );
//                tomcatAppPostThread = newThread( tomcatAppPost );
//                tomcatAppPostThread.setName( "watchdirPostThread=" + count );
//                tomcatAppPostThread.start();

                if ( runThread != null && runThread.isAlive() )
                    {
                    cancelRestServer( false );
                    }
                tomcatAppMonitor = new TomcatAppMonitor( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin, this );
                runThread = ProcessInThread.newThread( "TomcatAppMonitor", count++, true, tomcatAppMonitor );
                runThread.start();

      // TEST          waitUntilStarted();
                System.out.println( "RestServerSw after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) 
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }

//    public void keepRunning() 
//        {
//        System.out.println( "entered RestServerSw keepRunning()" );
//        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
//
//        cancelFlag = false;
//        int downTimes = 99;
//        String response = null;
//        boolean didFirstStart = false;
//        
//        while ( ! cancelFlag )
//            {
//            try
//                {
//                System.out.println( "RestServerSw.run() make rest /jfp/sys/ping call" );
//                try
//                    {
//                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_PING, String.class );
//                    }
//                catch( Exception exc )
//                    {
//                    System.out.println( "RestServerSw.run() ping threw Exception !!" );
//                    response = null;
//                    SwingUtilities.invokeLater(new Runnable() 
//                        {
//                        public void run() {
//                            jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                            }
//                        });
//                    exc.printStackTrace();
//                    }
//                System.out.println( "RestServerSw.run() ping response =" + response + "=" );
//                if ( ! cancelFlag && 
//                    ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
//                    {
//                    if ( ++downTimes > 5 )
//                        {
//                tomcatAppMonitor = new TomcatAppMonitor( connUserInfo, connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), jFileFinderWin, this );
//                runThread = newThread( tomcatAppMonitor );
//                runThread.setName( "tomcatAppMonitor=" + count++ );
//                runThread.start();
//                
//                if ( tomcatAppMonitor.isStartedServer() )
//                    {
//                    Thread.sleep( 15000 );
//                    }
//                        SwingUtilities.invokeLater(new Runnable() 
//                            {
//                            public void run() {
//                                jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                                }
//                            });
//                        System.out.println( "after start remote jfp server" );
////                        Thread.sleep( 30000 );
//                        downTimes = 0;
//                        }
//                    else
//                        {
//                        Thread.sleep( 1000 );
//                        }
//                    }
//                else if ( didFirstStart ) // is running or first start ?
//                    {
//                    Thread.sleep( 4000 );
//                    }
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
//                }
//            catch( InterruptedException intexc )
//                {
//                System.out.println( "RestServerSw sleep interrupted" );
//                SwingUtilities.invokeLater(new Runnable() 
//                    {
//                    public void run() {
//                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                        }
//                    });
//                }
//            catch( Exception exc )
//                {
//                SwingUtilities.invokeLater(new Runnable() 
//                    {
//                    public void run() {
//                        jFileFinderWin.setRmtConnectBtnBackground( Color.yellow );
//                        }
//                    });
//                exc.printStackTrace();
//                }
//            didFirstStart = true;
//            } // while
//        
//        connUserInfo.setConnectedFlag( false );
//        SwingUtilities.invokeLater(new Runnable() 
//            {
//            public void run() {
//                jFileFinderWin.setRmtConnectBtnBackgroundReset();
//                }
//            });
//        System.out.println( "Exiting RestServerSw run() - Done" );
//        }

    public void waitUntilStarted()
        {
        System.out.println( "entered RestServerSw waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        
        boolean isRunning = false;
        boolean firstWaitFlag = true;
        int response = -9;
        int waitCount = 15;
        
        while ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            System.out.println( "RestServerSw.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
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
                System.out.println( "RestServerSw.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            System.out.println( "waitUntilStarted response =" + response + "=" );
            try
                {
                if ( response < 0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
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
