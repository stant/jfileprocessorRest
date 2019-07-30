package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.WatchDirSw;
import com.towianski.models.FileTimeEvent;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author stan
 */
public class WatchStartingFolder implements Runnable
    {
    WatchDirToCallerEventQueue watchDirToCallerEventQueue = null;
    JFileFinderWin jFileFinderWin = null;
    //BlockingQueue<FileTimeEvent> fileEventQueue = null;
    Object lockObj = new Object();
    BlockingQueue<Integer> allowToRunQueue = new LinkedBlockingQueue<>(100);
    boolean runFlag = true;
    
    /**
     * Creates a WatchService and registers the given directory
     */
//    public WatchStartingFolder( JFileFinderWin jFileFinderWin, BlockingQueue<FileTimeEvent> fileEventQueue, Object lockObj )
    public WatchStartingFolder( JFileFinderWin jFileFinderWin ) //, Object lockObj )
        {
        this.jFileFinderWin = jFileFinderWin;
//        this.fileEventQueue = fileEventQueue;
//        this.lockObj = lockObj;
        }
    
    public void setTriggerSearchFlag(boolean triggerSearchFlag)
        {
        System.out.println( "WatchStartingFolder.setTriggerSearchFlag() set triggerSearchFlag = " + triggerSearchFlag );
        if ( triggerSearchFlag )
            {
//            jFileFinderWin.callSearchBtnActionPerformed( null );

            SwingUtilities.invokeLater(new Runnable() 
            {
                public void run() {
                    System.out.println( "WatchStartingFolder   invokeLater() searchButton" );
                    System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                    //                watchDirSw.setIsDone(true);
                    jFileFinderWin.callSearchBtnActionPerformed( null );
                }
            });

            System.out.println( "WatchStartingFolder.setTriggerSearchFlag() - after call jFileFinderWin.callSearchBtnActionPerformed( null )" );
            }
        }
    
    public void restart()
        {
//        synchronized( lockObj )
//            {
//            lockObj.notify();
//            }
        try {
            allowToRunQueue.put( 1 );
            }
        catch (InterruptedException ex) {
            Logger.getLogger(WatchStartingFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    public void stop()
        {
        runFlag = false;
        watchDirToCallerEventQueue.stop();
        // it shuts it down and makes it return a null event which .take() below will catch and stop on
        }

    public void pause()
        {
        System.out.println("WatchStartingFolder.pause()");
        watchDirToCallerEventQueue.stop();
        // it shuts it down and makes it return a null event which .take() below will catch and stop on
        }
    
    public void run()
        {
        System.out.println("WatchStartingFolder() wait until notified");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        restart();
        
        while ( runFlag )
            {
            try {
                System.out.println("WatchStartingFolder waiting to get runallow token" );
                allowToRunQueue.take();   // blocks until get a token denoting allowed to run.
                
    //            synchronized ( lockObj ) {
    //                lockObj.wait();
    //                }
                // Create a Queue to receive your file create events
                BlockingQueue<FileTimeEvent> fileEventQueue = new LinkedBlockingQueue<>(1000);

                // args: needed, arrayList of paths to watch, millisecond gap if file does not change in that time consider it done, your queue
                //watchDirToCallerEventQueue = new WatchDirToCallerEventQueue( codeProcessorPanel.jFileFinderWin.getWatchFileEventsSw() , watchDirPath, 3000, fileEventQueue );   // to pass a single dir if you want
                watchDirToCallerEventQueue = new WatchDirToCallerEventQueue( "watchStartingFolder", null /*jFileFinderWin.getWatchFileEventsSw()*/ , Paths.get( jFileFinderWin.getStartingFolder() ), 750, fileEventQueue );
                watchDirToCallerEventQueue.setQueueDeletesFlag(true);

                // pass in your watcher that I can call the .stop() method on when you hit the "cancel" button
                //codeProcessorPanel.setStartStop( watchDirToCallerEventQueue );

                Thread qThread = ProcessInThread.newThread( "watchDirToCallerEventQueue-qThread", 0, true, watchDirToCallerEventQueue );
                qThread.start();

                System.out.println( "\nstarting folder final Q => " );
                boolean triggerSearchFlag = false;
                try {
                    //while (true) 
                        //{
                        FileTimeEvent fte = fileEventQueue.take();
                        System.out.println( "> " + fte.getFilename() + "   event =" + fte.getEvent() + System.getProperty("line.separator") );
                        if ( fte.getEvent() != null )
                            {
                            triggerSearchFlag = true;
                            }
                        //}
                    pause();
                    fileEventQueue.clear();
                    } 
                catch (Exception e2) {
                    e2.printStackTrace();
                    //Thread.currentThread().interrupt();
                    }
                finally
                    {
                    if ( qThread != null )
                        {
                        System.out.println( "watchFiles - before qThread.join()" );
                        if ( qThread.isAlive() )
                            {
                            try
                                {
                                qThread.join();
                                } 
                            catch (InterruptedException ex)
                                {
                                Logger.getLogger(WatchDirSw.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        System.out.println( "watchFiles - after qThread.join()" );
                        }
                    }
                // HERE IS WHERE YOU DO WHAT YOU WANT WITH THE FILES CREATED !
                try {
                    if ( triggerSearchFlag )
                        setTriggerSearchFlag( true );
                    }
                catch (Exception e) {
                    e.printStackTrace();
                    }
                System.out.println( "<== final Q" );
                }
            catch (Exception ex)
                {
                System.out.println("WatchStartingFolder Interrupt !" );
                Logger.getLogger(WatchStartingFolder.class.getName()).log(Level.SEVERE, null, ex);
                }
            } // not stop flag
        System.out.println( "exit WatchStartingFolder()");
        }

}
