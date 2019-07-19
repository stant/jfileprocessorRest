/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.WatchConfigFiles;
import com.towianski.jfileprocess.actions.WatchDir;
import com.towianski.models.Constants;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchConfigFilesSw {
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
        System.out.println( "enter WatchConfigFilesSw.cancelWatch()" );
        if ( watchConfigFiles != null )
            {
            watchConfigFiles.cancelWatch();
            }
        System.out.println( "exit WatchConfigFilesSw.cancelWatch()" );
        }

    public void actionPerformed( ArrayList<String> pathsToNotWatch, Path watchFolder ) {                                         
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.cancelWatch();
            }
        else
            {
            try {
//                System.out.println( "WatchConfigFilesSw doCmdBtnActionPerformed start" );
//                System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

                watchConfigFiles = new WatchConfigFiles( jFileFinderWin );
                watchThread = ProcessInThread.newThread( "watchConfigFiles", count++, true, watchConfigFiles );
                watchThread.start();
//                System.out.println( "watchConfigFilesSw (" + watchFolder + ") after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) {
                Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
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
