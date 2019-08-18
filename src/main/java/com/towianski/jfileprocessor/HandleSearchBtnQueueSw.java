/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.boot.GlobalMemory;
import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.HandleSearchBtnQueue;
import com.towianski.models.Constants;
import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author stan
 */
public class HandleSearchBtnQueueSw {
    
    private static final MyLogger logger = MyLogger.getLogger( HandleSearchBtnQueueSw.class.getName() );

    @Autowired
    private JFileFinderWin jFileFinderWin;
    
    @Autowired 
    private GlobalMemory globalMemory;

    HandleSearchBtnQueue handleSearchBtnQueue = null;
    
    Thread handleSearchBtnThread = null;
    //HandleSearchBtnQueue handleSearchBtnQueue = null;
    static long count = 0;
    
    public HandleSearchBtnQueueSw()
        {
        logger.info( "HandleSearchBtnQueueSw() constructor() 1" );
        }

//    public HandleSearchBtnQueueSw( JFileFinderWin jFileFinderWin )
//        {
//        this.jFileFinderWin = jFileFinderWin;
//        }

    public synchronized void cancelWatch() 
        {
        logger.info( "enter HandleSearchBtnQueueSw.cancelWatch()" );
        if ( handleSearchBtnQueue != null )
            {
            handleSearchBtnQueue.cancelWatch();
            }
        logger.info( "exit HandleSearchBtnQueueSw.cancelWatch()" );
        }
    
    public void putSearchBtnQueue()
        {
        logger.info( "enter HandleSearchBtnQueueSw.putSearchBtnQueue()" );
        handleSearchBtnQueue.putSearchBtnQueue();
        }

    public void actionPerformed( ArrayList<String> pathsToNotWatch, Path watchFolder ) {                                         
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.cancelWatch();
            }
        else
            {
            try {
                logger.info( "HandleSearchBtnQueueSw doCmdBtnActionPerformed start" );
//                logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

                handleSearchBtnQueue = new HandleSearchBtnQueue( jFileFinderWin, globalMemory );
                handleSearchBtnThread = ProcessInThread.newThread( "handleSearchBtnQueue", count++, true, handleSearchBtnQueue );
                handleSearchBtnThread.start();
//                logger.info( "handleSearchBtnQueueSw (" + watchFolder + ") after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) {
                Logger.getLogger(HandleSearchBtnQueueSw.class.getName()).log(Level.SEVERE, null, ex);
            } 
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
//                new WatchDirSwFrame().setVisible(true);
//            }
//        });
    }
}
