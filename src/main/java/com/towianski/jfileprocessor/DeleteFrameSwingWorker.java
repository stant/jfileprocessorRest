/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class DeleteFrameSwingWorker extends SwingWorker<ResultsData, Long> {

    private static final MyLogger logger = MyLogger.getLogger( DeleteFrameSwingWorker.class.getName() );
    DeleteFrame deleteFrame = null;
    ArrayList<String> deletePaths = new ArrayList<String>();
    JFileDelete jfiledelete = null;
    boolean showProgressFlag = true;
    boolean closeWhenDoneFlag = true;

    public DeleteFrameSwingWorker( DeleteFrame deleteFrame, JFileDelete jfiledelete, ArrayList<String> deletePaths, boolean showProgressFlag, boolean closeWhenDoneFlag )
        {
        this.deleteFrame = deleteFrame;
        this.jfiledelete = jfiledelete;
        this.deletePaths = deletePaths;
        this.showProgressFlag = showProgressFlag;
        this.closeWhenDoneFlag = closeWhenDoneFlag;
        }

    @Override
    public ResultsData doInBackground() {
        deleteFrame.stopDirWatcher();
        deleteFrame.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_STARTED );
        jfiledelete.run( this );
        return jfiledelete.getResultsData();
    }

    public void publish2( Long num ) {
        if ( showProgressFlag )
            {
            publish( num );
            }
        }

    protected void process(List<Long> numList ) {
        if ( showProgressFlag )
            {
            Long lastNum = numList.get( numList.size() - 1 );
            deleteFrame.setMessage( "" + lastNum );
            }
        }

    @Override
    public void done() {
        try {
            //logger.info( "entered SwingWork.done()" );
            ResultsData resultsData = get();
            //Integer ii = get();
            //logger.info( "SwingWork.done() at 2  ii = " + ii );
            //logger.info( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            String partialMsg = "";
            String msg = "Deleted " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() );
            if ( ! resultsData.getMessage().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }
            
            if ( resultsData.getSearchWasCanceled() )
                {
                deleteFrame.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_CANCELED );
                msg = msg + " PARTIAL files list.";
                }
            else
                {
                if ( resultsData.getProcessStatus().equals( deleteFrame.PROCESS_STATUS_DELETE_COMPLETED ) )
                    {
                    new CloseWinOnTimer( deleteFrame, closeWhenDoneFlag ? 4000 : 0 ){{setRepeats(false);}}.start();
                    }
                }

            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
                {
                deleteFrame.setProcessStatus( resultsData.getProcessStatus() );
                }
            if ( ! resultsData.getMessage().trim().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }

            deleteFrame.setMessage( msg + partialMsg );
            deleteFrame.setResultsData( resultsData );
            
            // clean up
            resultsData = null;
            jfiledelete = null;
            deletePaths = null;            
            
            deleteFrame.callSearchBtnActionPerformed();
            //logger.info( "exiting SwingWork.done()" );
            }
        catch (InterruptedException ignore) 
            {}
        catch (java.util.concurrent.ExecutionException e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            logger.info( "Error in DeleteFrameSwingWorker(): " + why);
            }
    }    
}
