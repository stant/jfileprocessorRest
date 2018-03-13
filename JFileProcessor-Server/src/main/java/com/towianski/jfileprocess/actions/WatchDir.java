package com.towianski.jfileprocess.actions;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
import com.sun.nio.file.SensitivityWatchEventModifier;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.WatchDirSw;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class WatchDir implements Runnable
    {
    JFileFinderWin jFileFinderWin = null;
    private WatchService watcher = null;
    private Map<WatchKey,Path> keys = null;
    private boolean recursiveFlag = false;
    private boolean trace = false;
    private Path dirToWatch = null;
    boolean cancelFlag = false;
    boolean registerOk = false;
    boolean triggerSearchFlag = false;
    WatchDirSw watchDirSw = null;
    Thread watchDirPostThread = null;
    WatchDirPost watchDirPost = null;
    Object lockObj = null;

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir( JFileFinderWin jFileFinderWin, Object lockObj, Thread watchDirPostThread, WatchDirPost watchDirPost, Path dirToWatch, WatchDirSw watchDirSw, boolean recursiveFlag )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.lockObj = lockObj;
        this.watchDirPostThread = watchDirPostThread;
        this.watchDirPost = watchDirPost;
        this.dirToWatch = dirToWatch;
        this.watchDirSw = watchDirSw;
        this.recursiveFlag = recursiveFlag;
        this.keys = new HashMap<WatchKey,Path>();
        this.trace = true;
        }
    
    public void cancelWatch()
        {
        System.out.println("WatchDir set cancelFlag to true");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        System.out.println("cancelRegister() folder for watch " );
        cancelFlag = true;
        registerOk = true;

        try {
            watcher.close();
            }
        catch (Exception ex)
            {
            System.out.println("WatchDir set cancelFlag caught error !");
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("WatchDir exit cancelSearch()");
        }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    public void register(Path dir) 
        {
        System.out.println("register()  trace =" + trace + "   dir =" + dir );
        registerOk = false;
        while( ! cancelFlag && ! registerOk )
            {
            try
                {
                WatchKey key = null;
                if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
                    {
                    // Mac code is not good and is slow so saw this might help.
                    key = dir.register( watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH );
                    }
                else
                    {
                    key = dir.register( watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY );
                    }
                if (trace) {
                    Path prev = keys.get(key);
                    if (prev == null) {
                        System.out.format("register: %s\n", dir);
                    } else {
                        if (!dir.equals(prev)) {
                            System.out.format("update: %s -> %s\n", prev, dir);
                            }
                        }
                    }
                keys.put(key, dir);
                registerOk = true;
                }
            catch (Exception ex) 
                {
                System.out.println( "could not start watchDir on folder = " + dir );
                System.out.println( "exception was: " + ex.getLocalizedMessage() );
                try {
                    System.out.println( "so wait 1 second" );
                    Thread.sleep(1000);
                    } 
                catch (InterruptedException ex2) 
                    {
                    System.out.println("Background interrupted");
                    }
                }
            }
        System.out.println("exit register()" );
        }

    /**
     * Register the given directory with the WatchService
     */
    public void unRegisterExisting()
        {
        try {
            for ( Map.Entry<WatchKey,Path> entry : keys.entrySet())
                {
                System.out.format( "UNregister: %s\n", entry.getKey().watchable().toString() );
                entry.getKey().cancel();
                keys.remove( entry.getKey() );
                }
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     * not used so far......
    */
    public void registerRecursive(final Path start) 
        {
        try {
            // register directory and sub-directories
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException
                {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        System.out.println("entered watchDir.processEvents()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        triggerSearchFlag = false;
        while ( ! cancelFlag && ! triggerSearchFlag ) 
            {
            System.out.println("watchDir.processEvents() start loop" );

            // wait for key to be signalled
            WatchKey key = null;
            try {
                key = watcher.take();
                }
            catch( ClosedWatchServiceException cwe )
                {
                System.out.println("watchDir.processEvents() ClosedWatchServiceException but CONTINUE" );
                //return;
                }
            catch ( InterruptedException ix ) 
                {
                System.out.println("watchDir.processEvents() InterruptedException but CONTINUE" );
                //return;
                }
            System.out.println("watchDir.processEvents() after watcher.take()" );

            Path dir = keys.get(key);
            if (dir == null) 
                {
                System.out.println("WatchKey not recognized!!");
                try {
                    key.reset();
                    }
                catch( Exception exc )
                    {
                    System.out.println( "watchDir.processEvents() key.reset() error 1" );
                    }
                continue;
                }

            try {
                for (WatchEvent<?> event: key.pollEvents()) 
                    {
                    WatchEvent.Kind kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) 
                        {
                        try {
                            key.reset();
                            }
                        catch( Exception exc )
                            {
                            System.out.println( "watchDir.processEvents() key.reset() error 2" );
                            }
                        continue;
                        }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    // print out event
                    System.out.format("watchDir  %s: %s\n", event.kind().name(), child);

                    boolean foundIgnore = false;
                    for ( String ignorePath : jFileFinderWin.pathsToNotWatch() )
                        {
                        System.out.println( "watchDir child      =" + child + "=" );
                        System.out.println( "watchDir ignorePath =" + ignorePath + "=" );
                        if ( child.toString().trim().equals( ignorePath ) )
                            {
                            foundIgnore = true;
                            }
                        }
                    System.out.println( "watchDir  foundIgnore flag = " + foundIgnore );
                    if ( ! foundIgnore )
                        {   
                        triggerSearchFlag = true;
                        }
                    else
                        {
                        // pause 1 second to keep from too frequently refreshing as files change
                        try {
                            System.out.println( "skip watch on this file so sleep 1 seconds" );
                            Thread.sleep( 1000 );
                            } 
                        catch (InterruptedException ex2) 
                            {
                            System.out.println( "Background interrupted" );
                            }
                        }

                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (recursiveFlag && (kind == ENTRY_CREATE)) 
                        {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) 
                                {
                                registerRecursive(child);
                                }
                            }
                        catch (Exception x) 
                            {
                            // ignore to keep sample readbale
                            }
                        }
                    } // end for
                }
            catch (Exception exc) 
                {
                exc.printStackTrace();
                }

            // reset key and remove from set if directory no longer accessible
            boolean valid = false;
            try {
                valid = key.reset();
                }
            catch( Exception exc )
                {
                System.out.println( "watchDir.processEvents() key.reset() error last" );
                }
//            if (!valid) 
//                {
//                keys.remove(key);
//
//                // all directories are inaccessible
//                if (keys.isEmpty()) 
//                    {
//                    break;
//                    }
//                }
            } // end while
        
        try {
            this.watcher.close();
            }
        catch (IOException ex)
            {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            }
    System.out.println( "exiting watchDir process loop !" );
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }

    @Override
    public void run() {
        System.out.println( "entered watchDir run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = false;

        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            } 
        catch (IOException ex)
            {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
            }

        System.out.println("WatchDir() dir =" + dirToWatch + "=" );
        if ( recursiveFlag )
            {
            registerRecursive( dirToWatch );
            } 
        else 
            {
            register( dirToWatch );
            }
    
//            WatchDir watchDir = new WatchDir( dirToWatch, false );
        processEvents();
        watchDirPost.setTriggerSearchFlag( triggerSearchFlag );
        synchronized ( lockObj ) {
            lockObj.notify();
            }
        System.out.println( "exiting watchDir processEvents()" );
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean recursive = false;
        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2)
                usage();
            recursive = true;
            dirArg++;
        }

        // register directory and process its events
        Path dir = Paths.get(args[dirArg]);
//        new WatchDir(dir, recursive).processEvents();
    }
}
