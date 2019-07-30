/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.models.FileTimeEvent;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchDirEventToTimeQueue implements Runnable
{
    private WatchService watchService = null;
    BlockingQueue<FileTimeEvent> fileEventTimeQueue = null;
    private ConcurrentHashMap watchKeyToPathAndQueueMap = null;
    private boolean runFlag = true;

//    static {
//        try {
//            throw new NullPointerException("---print stacktrace here--- static "); 
//              }
//        catch (Exception ex) 
//            {
//                ex.printStackTrace();
//            }
//        }

    public WatchDirEventToTimeQueue( WatchService watchService, BlockingQueue<FileTimeEvent> fileEventTimeQueue, ConcurrentHashMap watchKeyToPathAndQueueMap ) {
        System.out.println( "WatchDirEventToTimeQueue() Constructor()" );
        this.watchService = watchService;
        this.fileEventTimeQueue = fileEventTimeQueue;
        this.watchKeyToPathAndQueueMap = watchKeyToPathAndQueueMap;
    }
    
    public WatchDirEventToTimeQueue( WatchService watchService, BlockingQueue<FileTimeEvent> fileEventTimeQueue ) {
        System.out.println( "WatchDirEventToTimeQueue() Constructor()" );
        this.watchService = watchService;
        this.fileEventTimeQueue = fileEventTimeQueue;
    }
    
    public void cancelWatch()
        {
        System.out.println( "WatchDirEventToTimeQueue() set cancelFlag to true" );

        try {
            runFlag = false;
            watchService.close();
            }
        catch (Exception ex)
            {
            System.out.println("WatchDirEventToTimeQueue() set cancelFlag caught error !");
            Logger.getLogger(WatchDirEventToTimeQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("WatchDirEventToTimeQueue() exit cancelSearch()");
        }
    
    @Override
    public void run() 
        {
        System.out.println( "entered WatchDirEventToTimeQueue() run()" );
        //System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        try {
            WatchKey watchKey;
            while ( runFlag && (watchKey = watchService.take()) != null) 
                {
                for (WatchEvent<?> event : watchKey.pollEvents()) 
                    {
                    //System.out.println( "Event kind:" + event.kind() + ". File affected =" + event.context() + "=");
                    Instant instant = Instant.now();
                    
                    Path dir = (Path) watchKey.watchable();
                    Path fullPath = dir.resolve( (Path) event.context() );

                    FileTimeEvent fte = new FileTimeEvent( watchKey, fullPath, event.context().toString(), instant, event.kind() );
                    fileEventTimeQueue.put( fte );
                    } // poll loop
                
                watchKey.reset();
                }
            } 
        catch (InterruptedException ex) {
            System.out.println( "WatchDirEventToTimeQueue() run() Interrupt" );
            cancelWatch();
            }
        System.out.println( "exit WatchDirEventToTimeQueue() run()" );
        }
}    
