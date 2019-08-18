/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.Constants;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import java.text.NumberFormat;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class FillTableModelSwingWorker extends SwingWorker<ResultsData, Object> {

    private static final MyLogger logger = MyLogger.getLogger( FillTableModelSwingWorker.class.getName() );
    private JFileFinderWin jFileFinderWin = null;
    private String startingPath = null;
    private String patternType = null;
    private String filePattern = null;
    private JFileFinder jfilefinder = null;

    public FillTableModelSwingWorker( JFileFinderWin jFileFinderWinArg, JFileFinder jfilefinderArg )
        {
        jFileFinderWin = jFileFinderWinArg;
        jfilefinder = jfilefinderArg;
        }

    @Override
    public ResultsData doInBackground() {
        try {
        logger.info( "FillTableModelSwingWorker.doInBackground() before fillInFilesTable.run()" );
        logger.info("id of the thread is " + Thread.currentThread().getId() );   
        jFileFinderWin.releaseSearchLock();
        jFileFinderWin.getSearchLock();
        jFileFinderWin.setProcessStatus( Constants.PROCESS_STATUS_FILL_STARTED );
        jFileFinderWin.fillInFilesTable( null );
        
        logger.info( "after FillTableModelSwingWorker.doInBackground()" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                jFileFinderWin.replaceDirWatcher();
//                logger.info( "entered FillTableModelSwingWorker.doInBackground() set my own DoneFlag" );
//            }
//        });
            }
        catch (Exception ignore ) {}
        finally
            {
            logger.info("id of the thread is " + Thread.currentThread().getId() );   
            jFileFinderWin.releaseSearchLock();
            }
        return jfilefinder.getResultsData();
    }

    @Override
    public void done() {
        try {
            logger.info( "entered FillTableModelSwingWorker.done()" );
            logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            ResultsData resultsData = get();
            //logger.info( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            if ( resultsData.getFillWasCanceled() )
                {
                jFileFinderWin.setProcessStatus( Constants.PROCESS_STATUS_FILL_CANCELED );
                }
            else
                {
                jFileFinderWin.setProcessStatus( Constants.PROCESS_STATUS_FILL_COMPLETED );
                }
            jFileFinderWin.setNumFilesInTable();
            //SwingUtilities.invokeLater( jFileFinderWin.fillInFilesTable( resultsData ) );
            //jFileFinderWin.fillInFilesTable( resultsData );
            //jFileFinderWin.setResultsData( resultsData );

            jFileFinderWin.stopDirWatcher();
            jFileFinderWin.startDirWatcher();
            
            logger.info( "FillTableModelSwingWorker() jFileFinderWin.afterFillSwingWorker =" + jFileFinderWin.afterFillSwingWorker+ "=" );
            if ( jFileFinderWin.afterFillSwingWorker != null )
                {
                jFileFinderWin.takeAfterFillSwingWorker().execute();    
                }

            jfilefinder = null;
//            jFileFinderSwingWorker = null;
            resultsData = null;
//            jFileFinderWin.cleanup();
            

            logger.info( "exiting FillTableModelSwingWorker.done()" );
            } 
        catch (InterruptedException ignore ) {}
        catch (java.util.concurrent.ExecutionException e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) 
                {
                why = cause.getMessage();
                } 
            else 
                {
                why = e.getMessage();
                }
            logger.info( "Error FillTableModelSwingWorker() retrieving file. cause: " + why);
            e.printStackTrace();
            }
        }    
}
