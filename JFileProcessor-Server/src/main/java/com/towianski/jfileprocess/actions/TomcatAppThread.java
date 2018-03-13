package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import java.nio.file.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import testit.TomcatApp;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppThread implements Runnable
    {
    JFileFinderWin jFileFinderWin = null;
    private boolean trace = false;
    private Path dirToWatch = null;
    boolean cancelFlag = false;
    boolean triggerSearchFlag = false;
//    WatchDirSw watchDirSw = null;
//    Thread watchDirPostThread = null;
//    WatchDirPost watchDirPost = null;
    Object lockObj = null;

    /**
     * Creates a WatchService and registers the given directory
     */
//    public TomcatAppThread( JFileFinderWin jFileFinderWin, Object lockObj, Thread watchDirPostThread, WatchDirPost watchDirPost, Path dirToWatch, WatchDirSw watchDirSw, boolean recursiveFlag )
    public TomcatAppThread()
        {
        this.jFileFinderWin = jFileFinderWin;
        this.lockObj = lockObj;
//        this.watchDirPostThread = watchDirPostThread;
//        this.watchDirPost = watchDirPost;
        this.dirToWatch = dirToWatch;
//        this.watchDirSw = watchDirSw;
        this.trace = true;
        }
    
    public void cancelWatch()
        {
        System.out.println("WatchDir set cancelFlag to true");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        System.out.println("cancelRegister() folder for watch " );
        cancelFlag = true;

        try {
//            watcher.close();
            }
        catch (Exception ex)
            {
            System.out.println("WatchDir set cancelFlag caught error !");
            Logger.getLogger(TomcatAppThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("WatchDir exit cancelSearch()");
        }

    @Override
    public void run() {
        System.out.println( "entered watchDir run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = false;

//        TomcatApp tomcatApp = new TomcatApp();
        System.out.println( "*** before SpringApplication.run(TomcatApp.class, args)" );
        String[] args = {};
        SpringApplication.run(TomcatApp.class, args);
        System.out.println( "*** after SpringApplication.run(TomcatApp.class, args)" );
        
//        watchDirPost.setTriggerSearchFlag( triggerSearchFlag );
//        synchronized ( lockObj ) {
//            lockObj.notify();
//            }
        System.out.println( "exiting watchDir processEvents()" );
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
