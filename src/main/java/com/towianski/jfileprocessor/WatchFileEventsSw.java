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
import com.towianski.utils.MyLogger;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author stan
 */
@Component
public class WatchFileEventsSw implements Player {
    private static final MyLogger logger = MyLogger.getLogger( WatchFileEventsSw.class.getName() );
    JFileFinderWin jFileFinderWin = null;
    private WatchService watchService = null;
    BlockingQueue<FileTimeEvent> fileEventTimeQueue = new LinkedBlockingQueue<>();
    BlockingQueue<FileTimeEvent> fileEventOutputQueue = null;
    Thread firstWatchThread = null;
    Thread eventQueueThread = null;
    WatchDirEventToTimeQueue watchDirEventToTimeQueue = null;
    WatchDirEventsToCallerEventsQueue watchDirEventsToCallerEventsQueue = null;
    static long count = 0;
    String qName = "WatchFileEventsSw";
    ArrayList<Path> pathList = null;
    String eventTypes = null;
    private int millisGap = 1500;
//    Object readyLock = new Object();
//    boolean isReady = false;
    Semaphore waitSemaphore = new Semaphore(0);
    
    public WatchFileEventsSw()
        {
        }

    public WatchFileEventsSw( String qName, ArrayList<Path> pathList, String eventTypes, int millisGap, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
    {
        this.qName = qName;
        this.pathList = pathList;
        this.eventTypes = eventTypes;
        this.millisGap = millisGap;
        this.fileEventOutputQueue = fileEventOutputQueue;  
        startWatchService();
    }

    public WatchFileEventsSw( String qName, Path watchDir, String eventTypes, int millisGap, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
    {
        this.qName = qName;
        this.pathList = new ArrayList<Path>();
        this.pathList.add( watchDir );
        this.eventTypes = eventTypes;
        this.millisGap = millisGap;
        this.fileEventOutputQueue = fileEventOutputQueue;        
        startWatchService();
    }

    public WatchService getWatchService() {
        return watchService;
    }

//    public synchronized void cancelWatch() 
//        {
//        logger.info( "enter WatchFileEventsSw.cancelWatch()" );
//        ProcessInThread.stopThread( eventQueueThread );
//        ProcessInThread.stopThread( firstWatchThread );
//        logger.info( "exit WatchFileEventsSw.cancelWatch()" );
//        }
    
    public void stop()
        {
        logger.info( "WatchFileEventsSw.stop()");
        ProcessInThread.stopThread( eventQueueThread );
        ProcessInThread.stopThread( firstWatchThread );
        try {
            watchService.close();
        } catch (IOException ex) {
            logger.severeExc( ex );
        }
        logger.info( "exit WatchFileEventsSw.stop()" );
        }

    public void pause()
        {
        logger.info( "WatchFileEventsSw.pause()");
        //watchDirEventsToCallerEventsQueue.stop();
        ProcessInThread.stopThread( eventQueueThread );
        // it shuts it down and makes it return a null event which .take() below will catch and stop on
        }
    
    public void restart( ArrayList<Path> pathList, String eventTypes )
        {
        this.pathList = pathList;
        this.eventTypes = eventTypes;
        startEventQueueService();
        }

    public void restart( Path watchDir, String eventTypes )
        {
        this.pathList = new ArrayList<>();
        this.pathList.add( watchDir );
        this.eventTypes = eventTypes;
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
                pathList, eventTypes, millisGap, fileEventTimeQueue, fileEventOutputQueue, waitSemaphore ); //isReady, readyLock );

        eventQueueThread = ProcessInThread.newThread( "watchDirEventsToCallerEventsQueue-" + qName, count++, true, watchDirEventsToCallerEventsQueue );
        eventQueueThread.start();
        }

    public void run() {
        startEventQueueService();
        startEventTimeQueue();
        }

    public void startWatchService() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(WatchFileEventsSw.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startEventTimeQueue() {
        try {
            logger.info( "WatchFileEventsSw() startWatchService()" );
            watchDirEventToTimeQueue = new WatchDirEventToTimeQueue( watchService, fileEventTimeQueue, waitSemaphore ); //, isReady, readyLock );
            logger.info( "WatchFileEventsSw() startWatchService() at 2" );
            firstWatchThread = ProcessInThread.newThread( "watchDirEventToTimeQueue-" + qName, count++, true, watchDirEventToTimeQueue );
            logger.info( "WatchFileEventsSw() startWatchService() at 3" );
            firstWatchThread.start();
            logger.info( "WatchFileEventsSw() startWatchService() at 4" );
            }
        catch (Exception ex) {
            ex.printStackTrace();
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
