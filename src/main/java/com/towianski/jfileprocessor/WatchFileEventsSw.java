/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.WatchDirEventToTimeQueue;
import com.towianski.jfileprocess.actions.WatchDirEventToCallerTimeQueue;
import com.towianski.jfileprocess.actions.WatchDirToCallerEventQueue;
import com.towianski.models.FileTimeEvent;
import com.towianski.models.WatchKeyToPathAndQueue;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author stan
 */
@Component
public class WatchFileEventsSw {
    JFileFinderWin jFileFinderWin = null;
    private WatchService watchService = null;
    BlockingQueue<FileTimeEvent> fileEventTimeQueue = new LinkedBlockingQueue<>();
    private ConcurrentHashMap watchKeyToPathAndQueueMap = new ConcurrentHashMap<WatchKey, WatchKeyToPathAndQueue>();
    Thread firstWatchThread = null;
    Thread splitterWatchThread = null;
    WatchDirEventToTimeQueue watchDirEventToTimeQueue = null;
    WatchDirEventToCallerTimeQueue watchDirEventToCallerTimeQueue = null;
    WatchDirToCallerEventQueue watchDirToQueue = null;
    static long count = 0;
    
    public WatchFileEventsSw()
        {
        }

    public WatchService getWatchService() {
        return watchService;
    }

    public ConcurrentHashMap getWatchKeyToPathAndQueueMap() {
        return watchKeyToPathAndQueueMap;
    }

    public synchronized void cancelWatch() 
        {
        System.out.println( "enter WatchFileEventsSw.cancelWatch()" );
        if ( firstWatchThread != null )
            {
            watchDirEventToTimeQueue.cancelWatch();
            }
        if ( splitterWatchThread != null )
            {
            watchDirEventToCallerTimeQueue.cancelWatch();
            }
        System.out.println( "exit WatchFileEventsSw.cancelWatch()" );
        }

    public void actionPerformed() {
        try {
            System.out.println( "WatchFileEventsSw() Constructor() !" );
            watchService = FileSystems.getDefault().newWatchService();
            
            watchDirEventToTimeQueue = new WatchDirEventToTimeQueue( watchService, fileEventTimeQueue, watchKeyToPathAndQueueMap );
            firstWatchThread = ProcessInThread.newThread( "watchDirEventToTimeQueue", count++, true, watchDirEventToTimeQueue );
            firstWatchThread.start();
            
            watchDirEventToCallerTimeQueue = new WatchDirEventToCallerTimeQueue( fileEventTimeQueue, watchKeyToPathAndQueueMap );
            splitterWatchThread = ProcessInThread.newThread( "watchDirEventToCallerTimeQueue", count++, true, watchDirEventToCallerTimeQueue );
            splitterWatchThread.start();
            }
        catch (IOException ex) {
            Logger.getLogger(WatchFileEventsSw.class.getName()).log(Level.SEVERE, null, ex);
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
