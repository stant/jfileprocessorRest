/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.models.FileTimeEvent;
import com.towianski.utils.MyLogger;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author stan
 */
public class WatchDirEventToTimeQueue implements Runnable
{
    private static final MyLogger logger = MyLogger.getLogger( WatchDirEventToTimeQueue.class.getName() );
    private WatchService watchService = null;
    BlockingQueue<FileTimeEvent> fileEventTimeQueue = null;
    private boolean runFlag = true;
    Semaphore waitSemaphore = null;

//    static {
//        try {
//            throw new NullPointerException("---print stacktrace here--- static "); 
//              }
//        catch (Exception ex) 
//            {
//                ex.printStackTrace();
//            }
//        }

    public WatchDirEventToTimeQueue( WatchService watchService, BlockingQueue<FileTimeEvent> fileEventTimeQueue, Semaphore waitSemaphore ) //boolean isReady, Object readyLock ) 
    {
        logger.info( "WatchDirEventToTimeQueue() Constructor()" );
        this.watchService = watchService;
        this.fileEventTimeQueue = fileEventTimeQueue;
        this.waitSemaphore = waitSemaphore;
    }
    
    public void cancelWatch()
        {
        logger.info( "WatchDirEventToTimeQueue() set cancelFlag to true" );

        try {
            runFlag = false;
            }
        catch (Exception ex)
            {
            logger.info( "WatchDirEventToTimeQueue() set cancelFlag caught error !");
            logger.severeExc( ex );
            }
        logger.info( "WatchDirEventToTimeQueue() exit cancelSearch()");
        }


    @Override
    public void run() 
        {
        logger.info( "entered WatchDirEventToTimeQueue() run() - wait until ready" );
        try {
            waitSemaphore.acquire();
        } catch (InterruptedException ex) {
            logger.severeExc( ex );
        }
        
        logger.info( "WatchDirEventToTimeQueue() run() - now start" );
        //logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        try {
            WatchKey watchKey;
            while ( runFlag && (watchKey = watchService.take()) != null) 
                {
                for (WatchEvent<?> event : watchKey.pollEvents()) 
                    {
                    //logger.info( "Event kind:" + event.kind() + ". File affected =" + event.context() + "=");
                    Instant instant = Instant.now();
                    
                    WatchEvent.Kind kind = event.kind();

                    if (kind == OVERFLOW) 
                        {
                        System.err.println( "*** Error - Watch Service Event OVERFLOW !!   watchKey =" + System.identityHashCode( watchKey ) );
                        try {
                            FileTimeEvent fte = new FileTimeEvent( watchKey, null, instant );
                            fileEventTimeQueue.put( fte );
                            watchKey.reset();
                            }
                        catch( Exception exc )
                            {
                            logger.info( "watchDir.processEvents() key.reset() error 2" );
                            }
                        continue;
                        }

                    FileTimeEvent fte = new FileTimeEvent( watchKey, event, instant );
                    fileEventTimeQueue.put( fte );
                    } // poll loop
                
                watchKey.reset();
                }
            } 
        catch (InterruptedException ex) {
            logger.info( "WatchDirEventToTimeQueue() run() Interrupt" );
            cancelWatch();
            }
        logger.info( "exit WatchDirEventToTimeQueue() run()" );
        }
}    
