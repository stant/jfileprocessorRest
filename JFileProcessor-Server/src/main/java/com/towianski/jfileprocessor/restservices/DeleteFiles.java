/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.restservices;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import com.towianski.jfileprocessor.DeleteFrame;
import com.towianski.jfileprocessor.JFileDelete;
import com.towianski.models.DeleteModel;
import com.towianski.models.ResultsData;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class DeleteFiles
//public class DeleteFiles extends SwingWorker<ResultsData, Long> {
    {

    DeleteFrame deleteFrame = null;
    ArrayList<Path> deletePaths = new ArrayList<Path>();
    JFileDelete jfiledelete = null;
    boolean showProgressFlag = true;
    boolean closeWhenDoneFlag = true;

    public DeleteFiles( DeleteModel deleteModel )
        {
        this.deletePaths = deletePaths;
        this.showProgressFlag = showProgressFlag;
        this.closeWhenDoneFlag = closeWhenDoneFlag;
        
        jfiledelete = new JFileDelete( deleteModel.getConnUserInfo(), deleteModel.getStartingPath(), deleteModel.getDeletePaths(), deleteModel.getDeleteFilesOnlyFlag(), 
                                        deleteModel.isDeleteToTrashFlag(), deleteModel.getDeleteReadonlyFlag(), deleteModel.getFilesysType() );

        }

    public ResultsData doInBackground() {
   // FIXXX     deleteFrame.stopDirWatcher();
//        deleteFrame.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_STARTED );
        jfiledelete.run( null );
        return jfiledelete.getResultsData();
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
            deleteFrame.setMessage( "" + lastNum );
            }
        }

//    @Override
    public ResultsData done()    // NOT ACTUALLY USED !
        {
        ResultsData resultsData = new ResultsData();
        try {
            //System.out.println( "entered SwingWork.done()" );
            resultsData = doInBackground();
            //Integer ii = get();
            //System.out.println( "SwingWork.done() at 2  ii = " + ii );
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            String partialMsg = "";
            String msg = "Deleted " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() );
            if ( ! resultsData.getMessage().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }
            
            if ( ! resultsData.getProcessStatus().equals( "" ) )
                {
                resultsData.setProcessStatus( resultsData.getProcessStatus() );
                }
            else if ( resultsData.getSearchWasCanceled() )
                {
                resultsData.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_CANCELED );
                msg = msg + " PARTIAL files list.";
                }
            else
                {
                resultsData.setProcessStatus( deleteFrame.PROCESS_STATUS_DELETE_COMPLETED );
                new CloseWinOnTimer( deleteFrame, closeWhenDoneFlag ? 4000 : 0 ){{setRepeats(false);}}.start();
                }
            resultsData.setMessage( msg + partialMsg );
//            deleteFrame.setResultsData( resultsData );
            
            // clean up
//            resultsData = null;
            jfiledelete = null;
            deletePaths = null;            
            
     //       deleteFrame.callSearchBtnActionPerformed();
            //System.out.println( "exiting SwingWork.done()" );
            }
        catch ( Exception e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.out.println( "Error in DeleteFiles(): " + why);
            }
        return resultsData;
        }
    }
