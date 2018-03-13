/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.TomcatAppThread;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.client.RestTemplate;
import testit.TomcatApp;

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
    public static final String SERVER_URI = "http://localhost:8080";

    JFileFinderWin jFileFinderWin = null;
    Thread watchThread = null;
    Thread tomcatAppPostThread = null;
    TomcatAppThread tomcatAppThread = null;
    static long count = 0;
    Object LockObj = "";
    
    public RestServerSw( JFileFinderWin jFileFinderWin )
        {
        this.jFileFinderWin = jFileFinderWin;
        }

    public synchronized void cancelWatch() 
        {
        System.out.println( "enter tomcatAppSw.cancelWatch()" );
        if ( tomcatAppThread != null )
            {
//            tomcatApp.cancelWatch();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(SERVER_URI + JfpRestURIConstants.SYS_STOP, String.class );
            }
        if ( tomcatAppPostThread != null )
            {
            System.out.println( "tomcatAppSw.cancelWatch() - before tomcatAppPostThread.join()" );
            if ( tomcatAppPostThread.isAlive() )
                {
                try
                    {
                    tomcatAppPostThread.join();
                    } catch (InterruptedException ex)
                    {
                    Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            System.out.println( "tomcatAppSw.cancelWatch() - after tomcatAppPostThread.join()" );
            }
        System.out.println( "exit tomcatAppSw.cancelWatch()" );
        }

    public void actionPerformed(java.awt.event.ActionEvent evt) {                                         
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.cancelWatch();
            }
        else
            {
            try {
                System.out.println( "TomcatAppSw doCmdBtnActionPerformed start" );
                System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//                tomcatAppSwingWorker = new TomcatAppSwingWorker( jFileFinderWin, this, startingPath );
//                tomcatApp = tomcatAppSwingWorker.getTomcatApp();
//                tomcatAppSwingWorker.execute();   //doInBackground();

//                TomcatAppPost tomcatAppPost = new TomcatAppPost( jFileFinderWin, LockObj );
//                tomcatAppPostThread = newThread( tomcatAppPost );
//                tomcatAppPostThread.setName( "watchdirPostThread=" + count );
//                tomcatAppPostThread.start();

                tomcatAppThread = new TomcatAppThread();
                watchThread = newThread( tomcatAppThread );
                watchThread.setName( "tomcatAppThread=" + count++ );
                watchThread.start();
                System.out.println( "TomcatAppSw after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) {
                Logger.getLogger(TomcatApp.class.getName()).log(Level.SEVERE, null, ex);
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
