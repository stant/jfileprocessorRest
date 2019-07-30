/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.Player;
import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.WatchDirEventToTimeQueue;
import com.towianski.jfileprocess.actions.WatchDirEventsToCallerEventsQueue;
import com.towianski.models.FileTimeEvent;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author stan
 */
@Component
public class WatchFileEventsSw2 implements Player {
    JFileFinderWin jFileFinderWin = null;
    private WatchService watchService = null;
    BlockingQueue<FileTimeEvent> fileEventTimeQueue = new LinkedBlockingQueue<>();
    BlockingQueue<FileTimeEvent> fileEventOutputQueue = null;
    Thread firstWatchThread = null;
    Thread eventQueueThread = null;
    WatchDirEventToTimeQueue watchDirEventToTimeQueue = null;
    WatchDirEventsToCallerEventsQueue watchDirEventsToCallerEventsQueue = null;
    static long count = 0;
    String qName = "WatchFileEventsSw2";
    ArrayList<Path> pathList = null;
    private int millisGap = 1500;

    
    public WatchFileEventsSw2()
        {
        }

    public WatchFileEventsSw2( String qName, ArrayList<Path> pathList, int millisGap, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
    {
        this.qName = qName;
        this.pathList = pathList;
        this.millisGap = millisGap;
        this.fileEventOutputQueue = fileEventOutputQueue;        
    }

    public WatchFileEventsSw2( String qName, Path watchDir, int millisGap, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
    {
        this.qName = qName;
        pathList = new ArrayList<Path>();
        pathList.add( watchDir );
        this.pathList = pathList;
        this.millisGap = millisGap;
        this.fileEventOutputQueue = fileEventOutputQueue;        
    }

    public WatchService getWatchService() {
        return watchService;
    }

//    public synchronized void cancelWatch() 
//        {
//        System.out.println( "enter WatchFileEventsSw.cancelWatch()" );
//        ProcessInThread.stopThread( eventQueueThread );
//        ProcessInThread.stopThread( firstWatchThread );
//        System.out.println( "exit WatchFileEventsSw.cancelWatch()" );
//        }
    
    public void stop()
        {
        System.out.println("WatchFileEventsSw2.stop()");
        ProcessInThread.stopThread( eventQueueThread );
        ProcessInThread.stopThread( firstWatchThread );
        try {
            watchService.close();
        } catch (IOException ex) {
            Logger.getLogger(WatchFileEventsSw2.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println( "exit WatchFileEventsSw.stop()" );
        }

    public void pause()
        {
        System.out.println("WatchFileEventsSw2.pause()");
        //watchDirEventsToCallerEventsQueue.stop();
        ProcessInThread.stopThread( eventQueueThread );
        // it shuts it down and makes it return a null event which .take() below will catch and stop on
        }
    
    public void restart( ArrayList<Path> pathList )
        {
        this.pathList = pathList;
        startEventQueueService();
        }

    public void restart( Path watchDir )
        {
        pathList = new ArrayList<Path>();
        pathList.add( watchDir );
        this.pathList = pathList;
        startEventQueueService();
        }

    public void restart()
        {
        startEventQueueService();
        }

    public void go()
        {
        startEventQueueService();
        }

    public void startEventQueueService()
        {
        watchDirEventsToCallerEventsQueue = new WatchDirEventsToCallerEventsQueue( qName, watchService, 
                pathList, millisGap, fileEventTimeQueue, fileEventOutputQueue );
        watchDirEventsToCallerEventsQueue.setQueueDeletesFlag(true);

        eventQueueThread = ProcessInThread.newThread( "watchDirEventsToCallerEventsQueue-" + qName, count++, true, watchDirEventsToCallerEventsQueue );
        eventQueueThread.start();
        }

    public void setQueueDeletesFlag( boolean x )
        {
        watchDirEventsToCallerEventsQueue.setQueueDeletesFlag(x);
        }

    public void run() {
        startWatchService();
        startEventQueueService();
        }
        
    public void startWatchService() {
        try {
            System.out.println( "WatchFileEventsSw() startWatchService() !" );
            watchService = FileSystems.getDefault().newWatchService();
            
            watchDirEventToTimeQueue = new WatchDirEventToTimeQueue( watchService, fileEventTimeQueue );
            firstWatchThread = ProcessInThread.newThread( "watchDirEventToTimeQueue-" + qName, count++, true, watchDirEventToTimeQueue );
            firstWatchThread.start();
            }
        catch (IOException ex) {
            Logger.getLogger(WatchFileEventsSw2.class.getName()).log(Level.SEVERE, null, ex);
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
