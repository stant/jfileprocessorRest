/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.utils.MyLogger;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class AfterFillSwingWorker extends SwingWorker<String, Object> {

    private static final MyLogger logger = MyLogger.getLogger( AfterFillSwingWorker.class.getName() );
    private JFileFinderWin jFileFinderWin = null;
    private CodeProcessorPanel codeProcessorPanel = null;
    private String startingPath = null;
    private String patternType = null;
    private String filePattern = null;
    private JFileFinder jfilefinder = null;

    public AfterFillSwingWorker( JFileFinderWin jFileFinderWinArg, JFileFinder jfilefinderArg )
        {
        logger.info( "AfterFillSwingWorker constructor() with 2 args" );
        jFileFinderWin = jFileFinderWinArg;
        jfilefinder = jfilefinderArg;
        }

    
    public AfterFillSwingWorker( CodeProcessorPanel codeProcessorPanelArg )
        {
        logger.info( "AfterFillSwingWorker constructor() with args" );
        codeProcessorPanel = codeProcessorPanelArg;
        }

    public AfterFillSwingWorker( JFileFinderWin jFileFinderWinArg )
        {
        logger.info( "AfterFillSwingWorker constructor() with args" );
        jFileFinderWin = jFileFinderWinArg;
        }

    public AfterFillSwingWorker()
        {
        logger.info( "AfterFillSwingWorker constructor()" );
        }

    @Override
    public String doInBackground() {
        logger.info( "entered AfterFillSwingWorker.doInBackground()" );
        return "";  //JFileFinder.getResultsData();
    }

    @Override
    public void done() {
        try {
            logger.info( "entered AfterFillSwingWorker.done()" );
//            ResultsData resultsData = get();
            String ans = get();
            //logger.info( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();

            logger.info( "exiting AfterFillSwingWorker.done()" );
            } 
        catch (InterruptedException ignore) {}
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
            logger.info( "Error AfterFillSwingWorker() retrieving file: " + why);
            e.printStackTrace();
            }
        }    
}
