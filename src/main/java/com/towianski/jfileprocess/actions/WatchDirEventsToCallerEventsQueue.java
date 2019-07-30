/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.WatchFileEventsSw;
import com.towianski.models.FileTimeEvent;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchDirEventsToCallerEventsQueue implements Runnable, Player
{
//    @Autowired
//    WatchFileEventsSw watchFileEventsSw;
    String qName = "WatchDirToCallerEventQueue";
    private WatchService watchService = null;
    ArrayList<WatchKey> watchKeyList = new ArrayList<WatchKey>();
    BlockingQueue<FileTimeEvent> fileEventInputQueue = null;
    BlockingQueue<FileTimeEvent> fileEventOutputQueue = null;
    LinkedHashMap<String, FileTimeEvent> fteLhm = new LinkedHashMap<>();
    ArrayList<Path> pathList = null;
    
    boolean runFlag = true;
    boolean queueDeletesFlag = false;
    private int millisGap = 1500;

    public WatchDirEventsToCallerEventsQueue( String qName, WatchService watchService, 
                Path watchDir, int millisGap, BlockingQueue<FileTimeEvent> fileEventInputQueue, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
        {
        this.qName = qName;
        this.watchService = watchService;
        this.millisGap = millisGap;

        pathList = new ArrayList<Path>();
        pathList.add( watchDir );
        this.pathList = pathList;
        this.fileEventInputQueue = fileEventInputQueue;
        this.fileEventOutputQueue = fileEventOutputQueue;
        //this.lockObj = lockObj;
        addWatchPaths( pathList );
        }

    public WatchDirEventsToCallerEventsQueue( String qName, WatchService watchService, 
                ArrayList<Path> pathList, int millisGap, BlockingQueue<FileTimeEvent> fileEventInputQueue, BlockingQueue<FileTimeEvent> fileEventOutputQueue )
        {
        this.qName = qName;
        this.watchService = watchService;
        this.pathList = pathList;
        this.millisGap = millisGap;
        this.fileEventInputQueue = fileEventInputQueue;
        this.fileEventOutputQueue = fileEventOutputQueue;
        //this.lockObj = lockObj;
        addWatchPaths( pathList );
        }

    public void setQueueDeletesFlag( boolean x )
        {
        queueDeletesFlag = x;
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
        System.out.println("WatchDirToCallerEventQueue.stop() set runFlag to false");
        runFlag = false;
        removeWatchPaths();
        }

    public void addWatchPaths()
        {
        System.out.println( "entered WatchDirToCallerEventQueue (" + qName + ").addWatchPaths() using previous pathList" );
        }
    
    public void addWatchPaths( ArrayList<Path> pathList )
        {
        System.out.println( "entered WatchDirToCallerEventQueue (" + qName + ").addWatchPaths()" );

        try {
            for ( Path watchDir : pathList )
                {
                System.out.println( "addWatchPaths() path =" + watchDir + "=" );
                WatchKey watchKey = watchDir.register(
                                            watchService,
                                            StandardWatchEventKinds.ENTRY_CREATE,
                                            StandardWatchEventKinds.ENTRY_DELETE,
                                            StandardWatchEventKinds.ENTRY_MODIFY);

                //watchFileEventsSw.getWatchKeyToPathAndQueueMap().put(watchKey, new WatchKeyToPathAndQueue( watchDir, fileEventInputQueue ) );
                System.out.println("addWatchPaths() fileEventCallerQueue =" + System.identityHashCode(fileEventInputQueue ) + "=   watchKey =" + System.identityHashCode( watchKey ) );
                watchKeyList.add( watchKey );
                }
            }
        catch (Exception ex)
            {
            System.out.println("WatchDirToCallerEventQueue (" + qName + ") set cancelFlag caught error !");
            Logger.getLogger(WatchDirEventToTimeQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("exit WatchDirToCallerEventQueue (" + qName + ") addWatchPaths()");
        }

    public void removeWatchPaths()
        {
        System.out.println( "entered WatchDirToCallerEventQueue.removeWatchPaths() (" + qName + ")" );

        try {
            for ( WatchKey watchKey : watchKeyList )
                {
                System.out.println( "removeWatchPaths() watchKey =" + System.identityHashCode( watchKey ) + "=" );
                //watchFileEventsSw.getWatchKeyToPathAndQueueMap().remove( watchKey );
                watchKey.cancel();
                
                for (WatchEvent<?> event : watchKey.pollEvents()) 
                    {
                    System.out.println(
                            "Throw away since canceled: Event kind:" + event.kind()
                                    + ". File affected =" + event.context() + "=");
                    }
                }
            watchKeyList.clear();
            }
        catch (Exception ex)
            {
            System.out.println( "WatchDirToCallerEventQueue.removeWatchPaths() caught error !");
            Logger.getLogger(WatchDirEventToTimeQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("exit WatchDirToCallerEventQueue removeWatchPaths() (" + qName + ")");
        }

    @Override
    public void run() {
        FileTimeEvent oldFte = null;
        Instant baseTime = Instant.now();

        try {
            System.out.println("Read from caller's queue getFileEventCallerQueue() (" + qName + ") =" + System.identityHashCode(fileEventInputQueue ) + "=" );
            while ( runFlag ) {
                
                FileTimeEvent newFte = fileEventInputQueue.poll( millisGap, TimeUnit.MILLISECONDS );

                if ( newFte == null )
                    {
                    System.out.println("\n=== millisGap (" + qName + ") (" + millisGap + ") Poll finished so add ALL using iterator() ===");
                    Iterator<Map.Entry<String, FileTimeEvent>> fteLhmIter = fteLhm.entrySet().iterator();
                    while (fteLhmIter.hasNext()) 
                        {
                        Map.Entry<String, FileTimeEvent> entry = fteLhmIter.next();
                        System.out.println(entry.getKey() + " move to final Q 1 => " + entry.getValue());
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
                System.out.println( "===== process caller queue (" + qName + ") =======  newFte: " + newFte.getFilename() + "   event =" + newFte.getEvent() + "========================" );
                if ( fteLhm.containsKey( newFte.getFilename() ) )
                    {
                    oldFte = fteLhm.get( newFte.getFilename() );
                    if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_MODIFY &&
                         oldFte.getEvent() == StandardWatchEventKinds.ENTRY_CREATE )
                        {
                        oldFte.setInstant( newFte.getInstant() );
                        System.out.println( "UPDATE LtmQueue fte: " + oldFte.getFilename() + "   event =" + oldFte.getEvent() );
                        }
                    else
                        {
                        System.out.println( "IGNORE UPDATE fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    }
                else
                    {
                    if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_CREATE )
                        {
                        fteLhm.put( newFte.getFilename(), newFte );
                        System.out.println( "ADD to LtmQueue fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    else if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_DELETE && queueDeletesFlag )
                        {
                        fteLhm.put( newFte.getFilename(), newFte );
                        System.out.println( "DELETE to LtmQueue fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    else if ( newFte.getEvent() == StandardWatchEventKinds.ENTRY_MODIFY )
                        {
                        fteLhm.put( newFte.getFilename(), newFte );
                        System.out.println( "ADD for Preexisting File to LtmQueue fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    else
                        {
                        System.out.println( "IGNORE ADD fte: " + newFte.getFilename() + "   event =" + newFte.getEvent() );
                        }
                    }

                // Check times on all now and move to final queue if old enough
                if ( Instant.now().minusMillis(millisGap).isAfter(baseTime) )
                    {
                    System.out.println("\n=== Iterating (" + qName + ") over found and Check old times to final Q ===");
                    Iterator<Map.Entry<String, FileTimeEvent>> fteLhmIter = fteLhm.entrySet().iterator();
                    while (fteLhmIter.hasNext()) 
                        {
                        Map.Entry<String, FileTimeEvent> entry = fteLhmIter.next();
                        System.out.println(entry.getKey() + " look at time for => " + entry.getValue());
                        oldFte = entry.getValue();
                        if ( Instant.now().minusMillis(millisGap).isAfter( oldFte.getInstant() ) )
                            {
                            System.out.println(entry.getKey() + " MOVE to final Q 2 => " + entry.getValue());
                            fileEventOutputQueue.put( entry.getValue() );                                
                            fteLhmIter.remove();
                            System.out.println( "removed from key hash" );
                            }
                        }
                    System.out.println("<====");
                    baseTime = Instant.now();
                    }
            } // while
        } catch (InterruptedException e) {
            System.out.println("WatchDirEventsToCallerEventsQueue (" + qName + ") run() Interrupt");
            }
        finally
            {
            try {
                fileEventOutputQueue.put( new FileTimeEvent( null, null, null, null, null ) );   // to tell groovy script that we are stopping !
                } 
            catch (InterruptedException ex) {
                Logger.getLogger(WatchDirEventsToCallerEventsQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
            removeWatchPaths();
            }
    System.out.println("exit WatchDirToCallerEventQueue (" + qName + ") run()");
    }

}    
