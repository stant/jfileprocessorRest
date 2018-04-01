/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import com.towianski.models.ResultsData;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class CopyFrameSwingWorker extends SwingWorker<ResultsData, Long> {

//    JFileFinderWin jFileFinderWin = null;
    CopyFrame copyFrame = null;
    ArrayList<String> copyPaths = new ArrayList<String>();
    String toPath = null;
    JFileCopy jfilecopy = null;
    boolean showProgressFlag = true;
    boolean closeWhenDoneFlag = true;

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
            copyFrame.setMessage( "" + lastNum );
            }
        }
     
    @Override
    public void done() {
        try {
            System.out.println( "entered CopyFrameSwingWorker.done()" );
            ResultsData resultsData = get();
            //Integer ii = get();
            //System.out.println( "SwingWork.done() at 2  ii = " + ii );
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            String partialMsg = "";
//            String msg =  "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() );
            String msg =  "Copied " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.";
//            System.out.println( "CopyFrameSwingWorker. got results msg =" + msg + "=" );
            if ( resultsData.getSearchWasCanceled() )
                {
                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_CANCELED );
                msg = msg + " PARTIAL files list.";
                }
            else
                {
                copyFrame.setProcessStatus( copyFrame.PROCESS_STATUS_COPY_COMPLETED );
                System.out.println( "do new CloseWinOnTimer( copyFrame, 4000 )" );
                new CloseWinOnTimer( copyFrame, closeWhenDoneFlag ? 4000 : 0 ){{setRepeats(false);}}.start();
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
            
            // clean up
            resultsData = null;
            jfilecopy = null;
            copyPaths = null;            
            
            copyFrame.callSearchBtnActionPerformed();
            //System.out.println( "exiting SwingWork.done()" );
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
            System.out.println( "Error in CopyFrameSwingWorker(): " + why);
            e.printStackTrace();
            }
    }    
}
