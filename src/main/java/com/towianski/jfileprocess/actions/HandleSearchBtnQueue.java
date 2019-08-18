/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.boot.GlobalMemory;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.Constants;
import com.towianski.models.SearchBtnEvent;
import com.towianski.utils.MyLogger;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author stan
 */
public class HandleSearchBtnQueue implements Runnable
{
    private static final MyLogger logger = MyLogger.getLogger( HandleSearchBtnQueue.class.getName() );
    
    BlockingQueue<SearchBtnEvent> searchBtnQueue = new LinkedBlockingQueue<>(100);
    private JFileFinderWin jFileFinderWin = null;
    private GlobalMemory globalMemory = null;
    int millisGap = 1000;
    
    public HandleSearchBtnQueue( JFileFinderWin jFileFinderWin, GlobalMemory globalMemory )
    {
    this.jFileFinderWin = jFileFinderWin;  
    this.globalMemory = globalMemory;
    logger.info("HandleSearchBtnQueue jFileFinderWin 2 = " + jFileFinderWin );
    
    logger.info("HandleSearchBtnQueue globalMemory = " + globalMemory );
    }
    
    public void cancelWatch()
        {
        logger.info("HandleSearchBtnQueue set cancelFlag to true");

        try {
//            watchService.close();
            }
        catch (Exception ex)
            {
            logger.info("HandleSearchBtnQueue set cancelFlag caught error !");
            Logger.getLogger(HandleSearchBtnQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        logger.info("HandleSearchBtnQueue exit cancelSearch()");
        }
    
    public void putSearchBtnQueue()
        {
        try {
            searchBtnQueue.put( new SearchBtnEvent( SearchBtnEvent.SEARCHBTNEVENT_SEARCH, jFileFinderWin.getStartingFolder() ) );
            logger.info("HandleSearchBtnQueue searchBtnQueue.size() = " + searchBtnQueue.size() );
            }
        catch (InterruptedException ex) {
            Logger.getLogger(WatchStartingFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
//    public void addSearchClick()
//    {
//        if ( isSearchBtnIsWaiting() )
//            {
//            logger.info( "Already have a searchBtn() Waiting so ignore a new one !" );
//            return;
//            }
//        if ( searchBtnLock.tryLock() )
//            {
//            // Got the lock
//            try
//            {
//            logger.info( "searchBtn() not running so Start a search !" );
//            countOnlyFlag = false;
//            searchBtnAction( evt );
//            }
//            finally
//                {
//                // Make sure to unlock so that we don't cause a deadlock
//                searchBtnLock.unlock();
//                }
//            }
//        else
//            {
//            setSearchBtnIsWaiting(true);
//            }        
//    }
    
    public void triggerSearchBtn()
        {
        logger.info( "HandleSearchBtnQueue.triggerSearchBtn()" );
            try {
                SwingUtilities.invokeLater(new Runnable()
                    {
                    public void run() 
                        {
                        logger.finest( "HandleSearchBtnQueue   invokeLater() searchButton" );
                        logger.finest( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
                        //                watchDirSw.setIsDone(true);
                        logger.finest( "HandleSearchBtnQueue   invokeLater() stopSearch()" );
                        //jFileFinderWin.stopSearch();
                        if ( jFileFinderWin.getProcessStatus().equals( Constants.PROCESS_STATUS_SEARCH_STARTED ) )
                            {
                            jFileFinderWin.searchBtnAction( null );
                            }
                        if ( jFileFinderWin.getProcessStatus().equals( Constants.PROCESS_STATUS_FILL_STARTED ) )
                            {
                            jFileFinderWin.searchBtnAction( null );
                            }
                        logger.finest( "HandleSearchBtnQueue   invokeLater() stopFill()" );
//                        jFileFinderWin.stopFill();
                        logger.finest( "HandleSearchBtnQueue   invokeLater() callSearchBtnActionPerformed()" );
//                        jFileFinderWin.callSearchBtnActionPerformed( null );
                        jFileFinderWin.searchBtnAction( null );
                        }
                    });
                
                logger.finest( "HandleSearchBtnQueue.triggerSearchBtn() - after call jFileFinderWin.callSearchBtnActionPerformed( null )" );
                }
            catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(HandleSearchBtnQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    @Override
    public void run() {
        logger.info( "entered HandleSearchBtnQueue run()" );
        //logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        SearchBtnEvent searchBtnEvent;

            while ( true ) 
                {
        try {
                try {
                    logger.info( "HandleSearchBtnQueue .take() Wait for a Queue Item and searchBtnQueue.size() = " + searchBtnQueue.size() );
                    logger.finest( "HandleSearchBtnQueue .take() Wait for a Queue Item and searchBtnQueue.size() = " + searchBtnQueue.size() );
                    searchBtnEvent = searchBtnQueue.take();
                } catch (InterruptedException ex) {
                    logger.info( "HandleSearchBtnQueue Wait for a Queue Item got Interrupt ! searchBtnQueue.size() = " + searchBtnQueue.size() );
                    logger.finest( "HandleSearchBtnQueue Wait for a Queue Item got Interrupt ! searchBtnQueue.size() = " + searchBtnQueue.size() );
                }
                logger.info( "HandleSearchBtnQueue Got searchBtnQueue item so start time loop..." );
                logger.finest( "HandleSearchBtnQueue Got searchBtnQueue item so start time loop..." );

                Instant baseTime = Instant.now();
                while ( true ) 
                    {
                    if ( globalMemory.trySearchLock() )
                        {
                        logger.info( "=== .take() And No Search happening so do one now" );
                        logger.finest( "=== .take() And No Search happening so do one now" );
                        searchBtnQueue.clear();
                    globalMemory.releaseSearchLock();
                        triggerSearchBtn();
                        break;
                        }
                    else
                        {
//                        logger.info( "=== .take() Search in progress so Wait until search is done then Run another search." );
//                        globalMemory.getSearchLock();
//                        //searchBtnQueue.clear();
//                    globalMemory.releaseSearchLock();
//                        //triggerSearchBtn();

                        logger.info( "=== .take() Search in progress Sleep for millisGap and reloop" );
                        logger.finest( "=== .take() Search in progress Sleep for millisGap and reloop" );
                        Thread.sleep(millisGap);
                        break;
                        }

                    /*
                    logger.info( "   HandleSearchBtnQueue Loop waiting for a break in SearchBtn times...." );
                    //if ( ! one running now then)
                    searchBtnEvent = searchBtnQueue.poll( millisGap, TimeUnit.MILLISECONDS );

                    if ( searchBtnEvent == null )
                        {
                        logger.info("\n=== millisGap (" + "HandleSearchBtnQueue" + ") (" + millisGap + ") Poll finished so call triggerSearchBtn() ===");
                        searchBtnQueue.clear();
                        triggerSearchBtn();
                        break;
                        }

                    logger.info(
                            "searchBtnEvent type :" + searchBtnEvent.getType()
                                    + ". startingFolder =" + searchBtnEvent.getStartingFolder() + "=" );                    

                    // Check times on all now and move to final queue if old enough
                    if ( Instant.now().minusMillis(millisGap).isAfter(baseTime) )
                        {
                        logger.info("\n=== millisGap (" + "HandleSearchBtnQueue" + ") (" + millisGap + ") GAP slowdown finished so call triggerSearchBtn() ===");
                        searchBtnQueue.clear();
                        triggerSearchBtn();
                        break;
                        }

                    // restart timer then
                    logger.info( "HandleSearchBtnQueue got searchBtn in less then millisGap so reset and time again" );
                    baseTime = Instant.now();
                    */
                    } // while
            } 
        catch (Exception e) 
            {
            logger.info("HandleSearchBtnQueue Exception !! searchBtnQueue.size() = " + searchBtnQueue.size() );
            e.printStackTrace();
            }
//        catch (InterruptedException e) 
//            {
//            logger.info("HandleSearchBtnQueue InterruptedException !! searchBtnQueue.size() = " + searchBtnQueue.size() );
//            e.printStackTrace();
//            }
                }// while take()

        }
}    
