/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.models.FileTimeEvent;
import com.towianski.models.WatchKeyToPathAndQueue;
import java.nio.file.WatchKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchDirEventToCallerTimeQueue implements Runnable
{
    BlockingQueue<FileTimeEvent> fileEventTimeQueue = null;
    private ConcurrentHashMap watchKeyToPathAndQueueMap = null;
    private boolean runFlag = true;

    public WatchDirEventToCallerTimeQueue( BlockingQueue<FileTimeEvent> fileEventTimeQueue, ConcurrentHashMap watchKeyToPathAndQueueMap ) {
        this.fileEventTimeQueue = fileEventTimeQueue;
        this.watchKeyToPathAndQueueMap = watchKeyToPathAndQueueMap;
    }
    
    public void cancelWatch()
        {
        System.out.println("WatchDirEventToCallerTimeQueue set cancelFlag to true");

        try {
            runFlag = false;
            }
        catch (Exception ex)
            {
            System.out.println("WatchDirEventToCallerTimeQueue set cancelFlag caught error !");
            Logger.getLogger(WatchDirEventToCallerTimeQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("exit WatchDirEventToCallerTimeQueue cancelSearch()");
        }
    
        @Override
        public void run() {
            try {
                WatchKey wk = null;
                System.out.println("WatchDirEventToCallerTimeQueue run()");
                WatchKeyToPathAndQueue watchKeyToPathAndQueue = null;
                while ( runFlag ) 
                    {
                    FileTimeEvent newFte = fileEventTimeQueue.take();
                    watchKeyToPathAndQueue = (WatchKeyToPathAndQueue) watchKeyToPathAndQueueMap.get( newFte.getWatchKey() );
        
                    if ( watchKeyToPathAndQueue == null )
                        {
                        System.out.println( "====== no caller queue for event so Ignore ======  newFte: " + newFte.getFilename() + "   event =" + newFte.getEvent() + "========================" );
                        }
                    else
                        {
                        watchKeyToPathAndQueue.getFileEventCallerQueue().add( newFte );
                        //System.out.println( "====== move to caller queue ======  newFte: " + newFte.getFilename() + "   event =" + newFte.getEvent() + "========================" );
                        //System.out.println( "         watchKeyToPathAndQueue.getFileEventCallerQueue() =" + System.identityHashCode( watchKeyToPathAndQueue.getFileEventCallerQueue() ) + "=" );
                        }
                    } // while
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        System.out.println( "Exit WatchDirEventToCallerTimeQueue run()");
        }
}    
