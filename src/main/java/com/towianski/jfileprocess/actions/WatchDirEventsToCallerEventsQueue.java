/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.models.FileTimeEvent;
import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author stan
 */
public class WatchDirEventsToCallerEventsQueue implements Runnable, Player
{
    private static final MyLogger logger = MyLogger.getLogger( WatchDirEventsToCallerEventsQueue.class.getName() );
//    @Autowired
//    WatchFileEventsSw watchFileEventsSw;
    String qName = "WatchDirToCallerEventQueue";
    private WatchService watchService = null;
    ArrayList<WatchKey> watchKeyList = new ArrayList<WatchKey>();
    BlockingQueue<FileTimeEvent> fileEventInputQueue = null;
    BlockingQueue<FileTimeEvent> fileEventOutputQueue = null;
    LinkedHashMap<String, FileTimeEvent> fteLhm = new LinkedHashMap<>();
    ArrayList<Path> pathList = null;
    String eventTypes = "CMD";
    
    boolean runFlag = true;
    boolean queueCreateEvents = false;
    boolean queueDeletesEvents = false;
    boolean queueModifyEvents = false;
    private int millisGap = 1500;

    public WatchDirEventsToCallerEventsQueue( String qName, WatchService watchService, Path watchDir, String eventTypes, 
                int millisGap, BlockingQueue<FileTimeEvent> fileEventInputQueue, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
        {
        this.qName = qName;
        this.watchService = watchService;
        this.millisGap = millisGap;
        this.eventTypes = eventTypes;

        pathList = new ArrayList<Path>();
        pathList.add( watchDir );
        this.pathList = pathList;
        this.fileEventInputQueue = fileEventInputQueue;
        this.fileEventOutputQueue = fileEventOutputQueue;
        //this.lockObj = lockObj;
        addWatchPaths( pathList );
        }

    public WatchDirEventsToCallerEventsQueue( String qName, WatchService watchService, ArrayList<Path> pathList, String eventTypes, 
                int millisGap, BlockingQueue<FileTimeEvent> fileEventInputQueue, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
        {
        this.qName = qName;
        this.watchService = watchService;
        this.pathList = pathList;
        this.eventTypes = eventTypes;
        this.millisGap = millisGap;
        this.fileEventInputQueue = fileEventInputQueue;
        this.fileEventOutputQueue = fileEventOutputQueue;
        //this.lockObj = lockObj;
        addWatchPaths( pathList );
        }
    
    public List<WatchEvent.Kind<?>> createEventTypesList( String eventTypes )
    {
        List<WatchEvent.Kind<?>> events = new ArrayList<>();
        
        if ( eventTypes.toUpperCase().contains( "C" ) )
            {
            events.add( StandardWatchEventKinds.ENTRY_CREATE );
            queueCreateEvents = true;
            }
        else
            {
            queueCreateEvents = false;
            }
            
        
        if ( eventTypes.toUpperCase().contains( "M" ) )
            {
            events.add( StandardWatchEventKinds.ENTRY_MODIFY );
            queueModifyEvents = true;
            }
        else
            {
            queueModifyEvents = false;
            }
        
        if ( eventTypes.toUpperCase().contains( "D" ) )
            {
            events.add( StandardWatchEventKinds.ENTRY_DELETE );
            queueDeletesEvents = true;
            }
        else
            {
            queueDeletesEvents = false;
            }

        return events;
    }

    public void go()
        {
        run();
        }
    
    public void pause()
        {
        }
    
    public void restart()
        {
        }
    
    public void stop()
        {
        logger.info( "WatchDirToCallerEventQueue.stop() set runFlag to false");
        runFlag = false;
        removeWatchPaths();
        }

    public void addWatchPaths()
        {
        logger.info( "entered WatchDirToCallerEventQueue (" + qName + ").addWatchPaths() using previous pathList" );
        }
    
    public void addWatchPaths( ArrayList<Path> pathList )
        {
        logger.info( "entered WatchDirToCallerEventQueue (" + qName + ").addWatchPaths()" );

        try {
            for ( Path watchDir : pathList )
                {
                logger.info( "addWatchPaths() path =" + watchDir + "=" );
                WatchKey watchKey = watchDir.register(
                                            watchService, (createEventTypesList( this.eventTypes )).toArray(new WatchEvent.Kind<?>[0]) );
//                                            StandardWatchEventKinds.ENTRY_CREATE,
//                                            StandardWatchEventKinds.ENTRY_DELETE,
//                                            StandardWatchEventKinds.ENTRY_MODIFY);

                //watchFileEventsSw.getWatchKeyToPathAndQueueMap().put(watchKey, new WatchKeyToPathAndQueue( watchDir, fileEventInputQueue ) );
                logger.info( "addWatchPaths() fileEventCallerQueue =" + System.identityHashCode(fileEventInputQueue ) + "=   watchKey =" + System.identityHashCode( watchKey ) );
                watchKeyList.add( watchKey );
                }
            }
        catch (Exception ex)
            {
            logger.info( "WatchDirToCallerEventQueue (" + qName + ") set cancelFlag caught error !");
            logger.severeExc( ex );
            }
        logger.info( "exit WatchDirToCallerEventQueue (" + qName + ") addWatchPaths()");
        }

    public void removeWatchPaths()
        {
        logger.info( "entered WatchDirToCallerEventQueue.removeWatchPaths() (" + qName + ")" );

        try {
            for ( WatchKey watchKey : watchKeyList )
                {
                logger.info( "removeWatchPaths() watchKey =" + System.identityHashCode( watchKey ) + "=" );
                //watchFileEventsSw.getWatchKeyToPathAndQueueMap().remove( watchKey );
                watchKey.cancel();
                
                for (WatchEvent<?> event : watchKey.pollEvents()) 
                    {
                    logger.info( 
                            "Throw away since canceled: Event kind:" + event.kind()
                                    + ". File affected =" + event.context() + "=");
                    }
                }
            watchKeyList.clear();
            }
        catch (Exception ex)
            {
            logger.info( "WatchDirToCallerEventQueue.removeWatchPaths() caught error !");
            logger.severeExc( ex );
            }
        logger.info( "exit WatchDirToCallerEventQueue removeWatchPaths() (" + qName + ")");
        }

    @Override
    public void run() {
        FileTimeEvent oldFte = null;
        Instant baseTime = Instant.now();

        try {
            logger.info( "Read from caller's queue getFileEventCallerQueue() (" + qName + ") =" + System.identityHashCode(fileEventInputQueue ) + "=" );
            while ( runFlag ) {
                
                FileTimeEvent newFte = fileEventInputQueue.poll( millisGap, TimeUnit.MILLISECONDS );

                if ( newFte == null )
                    {
                    //logger.info( "\n=== millisGap (" + qName + ") (" + millisGap + ") Poll finished so add ALL using iterator() ===");
                    Iterator<Map.Entry<String, FileTimeEvent>> fteLhmIter = fteLhm.entrySet().iterator();
                    while (fteLhmIter.hasNext()) 
                        {
                        Map.Entry<String, FileTimeEvent> entry = fteLhmIter.next();
                        logger.info( entry.getKey() + " move to final Q 1 => " + entry.getValue());
                        fileEventOutputQueue.put( entry.getValue() );
                        }
                    fteLhm = new LinkedHashMap<>();
                    continue;
                    }

                // Hmmm. Used getFilename() as key and realize that if you watch multiple folders they can have a same file name
                // and this would cause a problem. I could probably go to using the fullpath instead, but if you have 2 files
                // with the same name one would overwrite the other anyways and who knows what order they would come so I am
                // not caring for now.  Also, fteLhm is per this class and not global so I don't think will have an issue like that either.
                // if needed we can probably just use the fullfilepath instead in the future.
//                logger.info( "===== process caller queue (" + qName + ") =======  newFte: " + newFte.getFilename() + "   event =" + newFte.getEvent() + "========================" );
                if ( fteLhm.containsKey( newFte.getFilename() ) )
                    {
                    oldFte = fteLhm.get( newFte.getFilename() );
                    if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_MODIFY &&
                         oldFte.getEvent() == StandardWatchEventKinds.ENTRY_CREATE )
                        {
                        oldFte.setInstant( newFte.getInstant() );
                        //logger.info( "UPDATE LtmQueue fte: " + oldFte.getFilename() + "   event =" + oldFte.getEvent() );
                        }
                    else
                        {
                        //logger.info( "IGNORE UPDATE fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    }
                else
                    {
                    if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_CREATE && queueCreateEvents )
                        {
                        fteLhm.put( newFte.getFilename(), newFte );
                        //logger.info( "ADD to LtmQueue fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    else if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_DELETE && queueDeletesEvents )
                        {
                        fteLhm.put( newFte.getFilename(), newFte );
                        //logger.info( "DELETE to LtmQueue fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    else if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_MODIFY && queueModifyEvents )
                        {
                        fteLhm.put( newFte.getFilename(), newFte );
                        //logger.info( "ADD for Preexisting File to LtmQueue fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    else
                        {
                        //logger.info( "IGNORE ADD fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    }

                // Check times on all now and move to final queue if old enough
                if ( Instant.now().minusMillis(millisGap).isAfter(baseTime) )
                    {
                    //logger.info( "\n=== Iterating (" + qName + ") over found and Check old times to final Q ===");
                    Iterator<Map.Entry<String, FileTimeEvent>> fteLhmIter = fteLhm.entrySet().iterator();
                    while (fteLhmIter.hasNext()) 
                        {
                        Map.Entry<String, FileTimeEvent> entry = fteLhmIter.next();
                        //logger.info( entry.getKey() + " look at time for => " + entry.getValue());
                        oldFte = entry.getValue();
                        if ( Instant.now().minusMillis(millisGap).isAfter( oldFte.getInstant() ) )
                            {
                            logger.info( entry.getKey() + " MOVE to final Q 2 => " + entry.getValue());
                            fileEventOutputQueue.put( entry.getValue() );                                
                            fteLhmIter.remove();
                            //logger.info( "removed from key hash" );
                            }
                        }
                    logger.info( "<====");
                    baseTime = Instant.now();
                    }
            } // while
        } catch (InterruptedException e) {
            logger.info( "WatchDirEventsToCallerEventsQueue (" + qName + ") run() Interrupt");
            }
        finally
            {
            try {
                fileEventOutputQueue.put( new FileTimeEvent( null, null, null, null, null ) );   // to tell groovy script that we are stopping !
                } 
            catch (InterruptedException ex) {
                logger.severeExc( ex );
                }
            removeWatchPaths();
            }
    logger.info( "exit WatchDirToCallerEventQueue (" + qName + ") run()");
    }

}    
