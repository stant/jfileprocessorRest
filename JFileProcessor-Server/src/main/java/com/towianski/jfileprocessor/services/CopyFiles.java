/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.services;

import com.towianski.jfileprocessor.JFileCopy;
import com.towianski.models.CopyModel;
import com.towianski.models.ResultsData;

/**
 *
 * @author stan
 */
public class CopyFiles
    {

    public ResultsData run( CopyModel copyModel )
        {
//        copyFrame.stopDirWatcher();
//        copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_STARTED );
        JFileCopy jfilecopy = new JFileCopy( copyModel.getIsDoingCutFlag(), copyModel.getStartingPath(), copyModel.getCopyPaths(), copyModel.getToPath(), copyModel.getFileVisitOptions(), copyModel.getCopyOpts() );

        jfilecopy.run();
//        return jfilecopy.getResultsData();
//    }
//    @Override
//    public void done() {
        ResultsData resultsData = null;
        try {
            //System.out.println( "entered SwingWork.done()" );
            resultsData = jfilecopy.getResultsData();
            //Integer ii = get();
            //System.out.println( "SwingWork.done() at 2  ii = " + ii );
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
//            NumberFormat numFormat = NumberFormat.getIntegerInstance();
//            String partialMsg = "";
////            String msg =  "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() );
//            String msg =  "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.";
////            System.out.println( "CopyFrameSwingWorker. got results msg =" + msg + "=" );
//            if ( resultsData.getSearchWasCanceled() )
//                {
//                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_CANCELED );
//                msg = msg + " PARTIAL files list.";
//                }
//            else
//                {
//                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_COMPLETED );
//                System.out.println( "do new CloseWinOnTimer( copyFrame, 4000 )" );
//                new CloseWinOnTimer( copyFrame, closeWhenDoneFlag ? 4000 : 0 ){{setRepeats(false);}}.start();
//                }
//
//            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
//                {
//                copyFrame.setProcessStatus( resultsData.getProcessStatus() );
//                }
//            if ( ! resultsData.getMessage().trim().equals( "" ) )
//                {
//                msg = resultsData.getMessage();
//                }
//
//            copyFrame.setMessage( msg + partialMsg );
//            copyFrame.setResultsData( resultsData );
//            
//            // clean up
//            resultsData = null;
//            jfilecopy = null;
//            copyPaths = null;            
            
//            copyFrame.callSearchBtnActionPerformed();
            //System.out.println( "exiting SwingWork.done()" );
            }
        catch (Exception ignore) 
            {}
        return resultsData;
        }
    
//    public void setCloseWhenDoneFlag(boolean closeWhenDoneFlag) {
//        this.closeWhenDoneFlag = closeWhenDoneFlag;
//    }

//    public void publish2( Long num ) {
//        if ( showProgressFlag )
//            {
//            publish( num );
//            }
//        }

//    protected void process(List<Long> numList ) {
//        if ( showProgressFlag )
//            {
//            Long lastNum = numList.get( numList.size() - 1 );
//            copyFrame.setMessage( "" + lastNum );
//            }
//        }
     
    }
