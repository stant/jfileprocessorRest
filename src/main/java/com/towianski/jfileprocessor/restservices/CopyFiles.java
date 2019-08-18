/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.restservices;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import com.towianski.jfileprocessor.CopyFrame;
import com.towianski.jfileprocessor.JFileCopy;
import com.towianski.models.CopyModel;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class CopyFiles   //extends SwingWorker<ResultsData, Long> 
    {
    private static final MyLogger logger = MyLogger.getLogger( CopyFiles.class.getName() );

//    JFileFinderWin jFileFinderWin = null;
    CopyFrame copyFrame = null;
    ArrayList<String> copyPaths = new ArrayList<String>();
    String toPath = null;
    JFileCopy jfilecopy = null;
    boolean showProgressFlag = true;
    boolean closeWhenDoneFlag = true;

    public CopyFiles( CopyModel copyModel )
        {
        logger.info( "entered CopyFiles() constructor" );
////        this.jFileFinderWin = jFileFinderWin;
//        this.copyFrame = copyFrame;
//        this.jfilecopy = jfilecopy;
//        this.copyPaths = copyPaths;
//        this.toPath = toPath;
//        this.showProgressFlag = showProgressFlag;
//        this.closeWhenDoneFlag = closeWhenDoneFlag;

//        Rest.saveObjectToFile( "CopyModel.json", copyModel );
        jfilecopy = new JFileCopy( copyModel.getConnUserInfo(), copyModel.isDoingCutFlag(), copyModel.getStartingPath(), copyModel.getCopyPaths(), copyModel.getToPath(), copyModel.getFileVisitOptions(), copyModel.getCopyOpts() );
//        logger.info( "entered CopyFiles() new jfilecopy =" + jfilecopy );
        }

//    @Override
    public ResultsData doInBackground() {
//        copyFrame.stopDirWatcher();
//        copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_STARTED );
//        logger.info( "entered CopyFiles() doInBackground() before jfilecopy" );
        jfilecopy.run( null );
//        logger.info( "entered CopyFiles() doInBackground() after jfilecopy" );
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

    protected void process(List<Long> numList ) {
        if ( showProgressFlag )
            {
            Long lastNum = numList.get( numList.size() - 1 );
            copyFrame.setMessage( "" + lastNum );
            }
        }
     
//    @Override
    public ResultsData done()   // NOT ACTUALLY USED !
        {
        ResultsData resultsData = new ResultsData();
        try {
            logger.info( "entered CopyFiles.done()" );
//            ResultsData resultsData = get();
            resultsData = doInBackground();
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
                resultsData.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_CANCELED );
                msg = msg + " PARTIAL files list.";
                }
            else
                {
                resultsData.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_COMPLETED );
                logger.info( "do new CloseWinOnTimer( copyFrame, 4000 )" );
                new CloseWinOnTimer( copyFrame, closeWhenDoneFlag ? 4000 : 0 ){{setRepeats(false);}}.start();
                }

//            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
//                {
//                copyFrame.setProcessStatus( resultsData.getProcessStatus() );
//                }
//            if ( ! resultsData.getMessage().trim().equals( "" ) )
//                {
//                msg = resultsData.getMessage();
//                }

            resultsData.setMessage( msg + partialMsg );
//            copyFrame.setResultsData( resultsData );
            
            // clean up
//            resultsData = null;
            jfilecopy = null;
            copyPaths = null;            
            
//            copyFrame.callSearchBtnActionPerformed();
            //logger.info( "exiting SwingWork.done()" );
            }
        catch ( Exception e ) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            logger.info( "Error in CopyFiles(): " + why);
            e.printStackTrace();
            }
        return resultsData;
    }    
}
