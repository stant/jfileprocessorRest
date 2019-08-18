package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.WatchFileEventsSw;
import com.towianski.models.FileTimeEvent;
import com.towianski.utils.MyLogger;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingUtilities;

/**
 *
 * @author stan
 */
public class WatchStartingFolder implements Runnable
    {
    private static final MyLogger logger = MyLogger.getLogger( WatchStartingFolder.class.getName() );
    WatchFileEventsSw watchFileEventsSw = null;
    JFileFinderWin jFileFinderWin = null;
    Object lockObj = new Object();
    BlockingQueue<Integer> allowToRunQueue = new LinkedBlockingQueue<>(100);
    boolean runFlag = true;
    
    public WatchStartingFolder( JFileFinderWin jFileFinderWin ) //, Object lockObj )
        {
        this.jFileFinderWin = jFileFinderWin;
//        this.fileEventQueue = fileEventQueue;
//        this.lockObj = lockObj;
        }
    
    public void setTriggerSearchFlag(boolean triggerSearchFlag)
        {
        logger.info( "WatchStartingFolder.setTriggerSearchFlag() set triggerSearchFlag = " + triggerSearchFlag );
        if ( triggerSearchFlag )
            {
//            jFileFinderWin.callSearchBtnActionPerformed( null );

            SwingUtilities.invokeLater(new Runnable() 
            {
                public void run() {
                    logger.info( "WatchStartingFolder   invokeLater() searchButton" );
                    logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                    //                watchDirSw.setIsDone(true);
                    jFileFinderWin.callSearchBtnActionPerformed( null );
                }
            });

            logger.info( "WatchStartingFolder.setTriggerSearchFlag() - after call jFileFinderWin.callSearchBtnActionPerformed( null )" );
            }
        }
    
    public void restart()
        {
        try {
            allowToRunQueue.put( 1 );
            }
        catch (InterruptedException ex) {
            logger.severeExc( ex );
            }
        }
    
    public void stop()
        {
        logger.info( "WatchStartingFolder.stop()");
        watchFileEventsSw.stop();
        // it shuts it down and makes it return a null event which .take() below will catch and stop on
        }

    public void pause()
        {
        logger.info( "WatchStartingFolder.pause()");
        watchFileEventsSw.pause();
        // it shuts it down and makes it return a null event which .take() below will catch and stop on
        }
    
    public void run()
        {
        logger.info( "WatchStartingFolder() wait until notified");
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        // Create a Queue to receive your file create events
        BlockingQueue<FileTimeEvent> fileEventQueue = new LinkedBlockingQueue<>(1000);

        // args: needed, arrayList of paths to watch, millisecond gap if file does not change in that time consider it done, your queue
        watchFileEventsSw = new WatchFileEventsSw( "watchStartFolder", Paths.get( jFileFinderWin.getStartingFolder() ), "CDM", 750, fileEventQueue );
        watchFileEventsSw.startWatchService();
        restart();
        
        while ( runFlag )
            {
            try {
                logger.info( "WatchStartingFolder waiting to get runallow token" );
                allowToRunQueue.take();   // blocks until get a token denoting allowed to run.
                
                watchFileEventsSw.restart( Paths.get( jFileFinderWin.getStartingFolder() ), "CDM" );

                logger.info( "\nstarting folder final Q => " );
                boolean triggerSearchFlag = false;
                try {
                    //while (true) 
                        //{
                        FileTimeEvent fte = fileEventQueue.take();
                        logger.info( "> " + fte.getFilename() + "   event =" + fte.getEvent() + System.getProperty("line.separator") );
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
                    watchFileEventsSw.pause();
                    }
                // HERE IS WHERE YOU DO WHAT YOU WANT WITH THE FILES CREATED !
                try {
                    if ( triggerSearchFlag )
                        setTriggerSearchFlag( true );
                    }
                catch (Exception e) {
                    e.printStackTrace();
                    }
                logger.info( "<== final Q" );
                }
            catch (Exception ex)
                {
                logger.info( "WatchStartingFolder Interrupt !" );
                logger.severeExc( ex );
                }
            } // not stop flag
        logger.info( "exit WatchStartingFolder()");
        }

}
