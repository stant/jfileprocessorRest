package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author stan
 */
public class WatchDirPost implements Runnable
    {
    JFileFinderWin jFileFinderWin = null;
    Object lockObj = null;

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDirPost( JFileFinderWin jFileFinderWin, Object lockObj )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.lockObj = lockObj;
        }
    
    public void setTriggerSearchFlag(boolean triggerSearchFlag)
        {
        System.out.println( "WatchDirPost.setTriggerSearchFlag() set triggerSearchFlag = " + triggerSearchFlag );
        if ( triggerSearchFlag )
            {
//            jFileFinderWin.callSearchBtnActionPerformed( null );

            SwingUtilities.invokeLater(new Runnable() 
            {
                public void run() {
                    System.out.println( "WatchDirPost   invokeLater() searchButton" );
                    System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                    //                watchDirSw.setIsDone(true);
                    jFileFinderWin.callSearchBtnActionPerformed( null );
                }
            });

            System.out.println( "WatchDirPost.setTriggerSearchFlag() - after call jFileFinderWin.callSearchBtnActionPerformed( null )" );
            }
        }
    
    public void run()
        {
        System.out.println("WatchDirPost() wait until notified");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

        try {
            synchronized ( lockObj ) {
                lockObj.wait();
                }
            }
        catch (InterruptedException ex)
            {
            System.out.println("WatchDirPost Interrupt !" );
            Logger.getLogger(WatchDirPost.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println( "exit WatchDirPost()");
        }

}
