/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.WatchDir;
import com.towianski.jfileprocess.actions.WatchDirPost;
import com.towianski.models.Constants;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchDirSw {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    JFileFinderWin jFileFinderWin = null;
    Thread watchThread = null;
    Thread watchDirPostThread = null;
    WatchDir watchDir = null;
    static long count = 0;
    Object LockObj = "";
    ArrayList<String> pathsToNotWatch;
    Path watchFolder;
    
    public WatchDirSw( JFileFinderWin jFileFinderWin, ArrayList<String> pathsToNotWatch, Path watchFolder )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.pathsToNotWatch = pathsToNotWatch;
        this.watchFolder = watchFolder;
        System.out.println( "enter watchDirSw() with watchFolder =" + watchFolder );
        }

    public synchronized void cancelWatch() 
        {
        System.out.println( "enter watchDirSw.cancelWatch()" );
        if ( watchDir != null )
            {
            watchDir.cancelWatch();
            }
        if ( watchDirPostThread != null )
            {
            System.out.println( "watchDirSw.cancelWatch() - before watchDirPostThread.join()" );
            if ( watchDirPostThread.isAlive() )
                {
                try
                    {
                    watchDirPostThread.join();
                    } catch (InterruptedException ex)
                    {
                    Logger.getLogger(WatchDirSw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            System.out.println( "watchDirSw.cancelWatch() - after watchDirPostThread.join()" );
            }
        System.out.println( "exit watchDirSw.cancelWatch()" );
        }

    public void actionPerformed( ArrayList<String> pathsToNotWatch, Path watchFolder ) {                                         
        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
            {
            this.cancelWatch();
            }
        else
            {
            try {
                System.out.println( "WatchDirSw doCmdBtnActionPerformed start" );
                System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                this.pathsToNotWatch = pathsToNotWatch;
                this.watchFolder = watchFolder;
//                watchDirSwingWorker = new WatchDirSwingWorker( jFileFinderWin, this, startingPath );
//                watchDir = watchDirSwingWorker.getWatchDir();
//                watchDirSwingWorker.execute();   //doInBackground();

                WatchDirPost watchDirPost = new WatchDirPost( jFileFinderWin, LockObj );
                watchDirPostThread = ProcessInThread.newThread( "watchDirPost", count++, true, watchDirPost );
                watchDirPostThread.start();

                watchDir = new WatchDir( pathsToNotWatch, LockObj, watchDirPostThread, watchDirPost, watchFolder, this, false );
                watchThread = ProcessInThread.newThread( "watchDir", count++, true, watchDir );
                watchThread.start();
                System.out.println( "WatchDirSw (" + watchFolder + ") after start watch thread, now exit actionPerformed" );
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
