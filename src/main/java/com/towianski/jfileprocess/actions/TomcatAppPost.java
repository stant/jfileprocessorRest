package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.utils.MyLogger;
import javax.swing.SwingUtilities;

/**
 *
 * @author stan
 */
public class TomcatAppPost implements Runnable
    {
    private static final MyLogger logger = MyLogger.getLogger( TomcatAppPost.class.getName() );

    JFileFinderWin jFileFinderWin = null;
    Object lockObj = null;

    /**
     * Creates a WatchService and registers the given directory
     */
    public TomcatAppPost( JFileFinderWin jFileFinderWin, Object lockObj )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.lockObj = lockObj;
        }
    
    public void setTriggerSearchFlag(boolean triggerSearchFlag)
        {
        logger.info( "WatchDirPost.setTriggerSearchFlag() set triggerSearchFlag = " + triggerSearchFlag );
        if ( triggerSearchFlag )
            {
//            jFileFinderWin.callSearchBtnActionPerformed( null );

            SwingUtilities.invokeLater(new Runnable() 
            {
                public void run() {
                    logger.info( "WatchDirPost   invokeLater() searchButton" );
                    logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                    //                watchDirSw.setIsDone(true);
                    jFileFinderWin.callSearchBtnActionPerformed( null );
                }
            });

            logger.info( "WatchDirPost.setTriggerSearchFlag() - after call jFileFinderWin.callSearchBtnActionPerformed( null )" );
            }
        }
    
    public void run()
        {
        logger.info( "WatchDirPost() wait until notified");
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

        try {
            synchronized ( lockObj ) {
                lockObj.wait();
                }
            }
        catch (InterruptedException ex)
            {
            logger.info( "WatchDirPost Interrupt !" );
            logger.severeExc( ex );
            }
        logger.info( "exit WatchDirPost()");
        }

}
