/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.WatchConfigFiles;
import com.towianski.models.Constants;
import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author stan
 */
public class WatchConfigFilesSw {
    private static final MyLogger logger = MyLogger.getLogger( WatchConfigFilesSw.class.getName() );
    JFileFinderWin jFileFinderWin = null;
    Thread watchThread = null;
    WatchConfigFiles watchConfigFiles = null;
    static long count = 0;
    
    public WatchConfigFilesSw( JFileFinderWin jFileFinderWin )
        {
        this.jFileFinderWin = jFileFinderWin;
        }

    public synchronized void cancelWatch() 
        {
        logger.info( "enter WatchConfigFilesSw.cancelWatch()" );
        if ( watchConfigFiles != null )
            {
            watchConfigFiles.cancelWatch();
            }
        logger.info( "exit WatchConfigFilesSw.cancelWatch()" );
        }

    public void actionPerformed( ArrayList<String> pathsToNotWatch, Path watchFolder ) {                                         
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.cancelWatch();
            }
        else
            {
            try {
//                logger.info( "WatchConfigFilesSw doCmdBtnActionPerformed start" );
//                logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

                watchConfigFiles = new WatchConfigFiles( jFileFinderWin );
                watchThread = ProcessInThread.newThread( "watchConfigFiles", count++, true, watchConfigFiles );
                watchThread.start();
//                logger.info( "watchConfigFilesSw (" + watchFolder + ") after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) {
                logger.severeExc( ex );
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
