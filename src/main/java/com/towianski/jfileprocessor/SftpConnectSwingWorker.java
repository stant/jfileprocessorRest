/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.ResultsData;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2017
 */
public class SftpConnectSwingWorker extends SwingWorker<ResultsData, Object> {

    JFileFinderWin jFileFinderWin = null;
    RestServerSw restServerSw;
    
    public SftpConnectSwingWorker( JFileFinderWin jFileFinderWin, RestServerSw restServerSw )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.restServerSw = restServerSw;
        }

    @Override
    public ResultsData doInBackground() {
        jFileFinderWin.stopDirWatcher();
//        codeProcessorPanel.setProcessStatus(codeProcessorPanel.PROCESS_STATUS_STARTED );
        restServerSw.actionPerformed(null);        
        System.out.println( "exit SftpConnectSwingWorker() " );  //with connUserInfo =" + connUserInfo + "=" );
        return null;
    }

    @Override
    public void done() {
        try {
            System.out.println( "entered SftpConnectSwingWorker.done()" );
            ResultsData resultsData = get();
//            String partialMsg = "";
//            String msg =  "Done";
//            if ( resultsData.getSearchWasCanceled() )
//                {
//                codeProcessorPanel.setProcessStatus(codeProcessorPanel.PROCESS_STATUS_CANCELED );
//                msg = msg + " PARTIAL run.";
//                }
//            else
//                {
//                codeProcessorPanel.setProcessStatus(codeProcessorPanel.PROCESS_STATUS_COMPLETED );
//                if ( codeProcessorPanel instanceof ScriptSwingWorker )
//                    {
//                    System.out.println( "do new CloseWinOnTimer( ScriptSwingWorker, 4000 )" );
//                    new CloseWinOnTimer( codeProcessorPanel, 4000 ){{setRepeats(false);}}.start();
//                    }
//                }
//
//            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
//                {
//                codeProcessorPanel.setProcessStatus( resultsData.getProcessStatus() );
//                }
//            if ( ! resultsData.getMessage().trim().equals( "" ) )
//                {
//                msg = resultsData.getMessage();
//                }
//
//            codeProcessorPanel.setMessage( msg + partialMsg );
//            codeProcessorPanel.setResultsData( resultsData );
            
            jFileFinderWin.startDirWatcher();
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
            System.out.println( "Error in CodeProcessorPanelSwingWorker(): " + why);
            e.printStackTrace();
            }
    }    
}
