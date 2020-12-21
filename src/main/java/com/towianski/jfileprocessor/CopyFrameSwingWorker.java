/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import com.towianski.models.CopyCounts;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import com.towianski.utils.NumberUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */

public class CopyFrameSwingWorker extends SwingWorker<ResultsData, CopyCounts> {

    private static final MyLogger logger = MyLogger.getLogger( CopyFrameSwingWorker.class.getName() );
//    JFileFinderWin jFileFinderWin = null;
    CopyFrame copyFrame = null;
    ArrayList<String> copyPaths = new ArrayList<String>();
    String toPath = null;
    JFileCopy jfilecopy = null;
    boolean showProgressFlag = true;
    boolean closeWhenDoneFlag = true;
    NumberFormat numf = NumberFormat.getIntegerInstance();
        
    public CopyFrameSwingWorker( CopyFrame copyFrame, JFileCopy jfilecopy, ArrayList<String> copyPaths, String toPath, boolean showProgressFlag, boolean closeWhenDoneFlag )
        {
//        this.jFileFinderWin = jFileFinderWin;
        this.copyFrame = copyFrame;
        this.jfilecopy = jfilecopy;
        this.copyPaths = copyPaths;
        this.toPath = toPath;
        this.showProgressFlag = showProgressFlag;
        this.closeWhenDoneFlag = closeWhenDoneFlag;
        }

    @Override
    public ResultsData doInBackground() {
        copyFrame.stopDirWatcher();
        copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_STARTED );
        jfilecopy.run( this );
        return jfilecopy.getResultsData();
    }

    public void setCloseWhenDoneFlag(boolean closeWhenDoneFlag) {
        this.closeWhenDoneFlag = closeWhenDoneFlag;
    }

//    public void publish2( Long num ) {
//        if ( showProgressFlag )
//            {
//            publish( num );
//            }
//        }
//
//    protected void process( List<Long> numList ) {
//        if ( showProgressFlag )
//            {
//            Long lastNum = numList.get( numList.size() - 1 );
//            copyFrame.setMessage( "" + lastNum );
//            }
//        }

    public void publish3( CopyCounts cc ) {
        if ( showProgressFlag )
            {
            publish( cc );
            }
        }
     
    protected void process( List<CopyCounts> numList ) {
        if ( showProgressFlag )
            {
            CopyCounts cc = numList.get( numList.size() - 1 );
            if( cc.getOneFileBytes() > 0 )
                //copyFrame.setMessage( "" + numf.format( cc.getFiles() )+ "    bytes: " + numf.format( cc.getOneFileBytes() ) );
                copyFrame.setMessage( "file: " + numf.format( cc.getFiles() )+ "    bytes: " + NumberUtils.humanReadableByteCount( cc.getOneFileBytes() ) );
            else
                //copyFrame.setMessage( "" + numf.format( cc.getFiles() ) );
                copyFrame.setMessage( "file: " + numf.format( cc.getFiles() ) );
            }
        }
     
    @Override
    public void done() {
        try {
            logger.info( "entered CopyFrameSwingWorker.done()" );
            ResultsData resultsData = get();
            //Integer ii = get();
            //logger.info( "SwingWork.done() at 2  ii = " + ii );
            //logger.info( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            String partialMsg = "";
//            String msg =  "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() );
            String msg =  "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.";
//            logger.info( "CopyFrameSwingWorker. got results msg =" + msg + "=" );
            if ( resultsData.getSearchWasCanceled() )
                {
                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_CANCELED );
                msg = msg + " PARTIAL files list.";
                }
            else
                {
                if ( resultsData.getProcessStatus().equals( copyFrame.PROCESS_STATUS_COPY_COMPLETED ) )
                    {
                    logger.info( "do new CloseWinOnTimer( copyFrame, 4000 )" );
                    new CloseWinOnTimer( copyFrame, closeWhenDoneFlag ? 4000 : 0 ){{setRepeats(false);}}.start();
                    }
                }

            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
                {
                copyFrame.setProcessStatus( resultsData.getProcessStatus() );
                }
            if ( ! resultsData.getMessage().trim().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }

            copyFrame.setMessage( msg + partialMsg );
            copyFrame.setResultsData( resultsData );
            copyFrame.showCopyErrors();
                    
            // clean up
            resultsData = null;
            jfilecopy = null;
            copyPaths = null;            
            
            copyFrame.callSearchBtnActionPerformed();
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
            logger.info( "Error in CopyFrameSwingWorker(): " + why);
            e.printStackTrace();
            }
    }    
}
