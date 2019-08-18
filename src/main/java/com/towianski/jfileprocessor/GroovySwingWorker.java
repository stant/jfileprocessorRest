/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2017
 */
public class GroovySwingWorker extends SwingWorker<ResultsData, Object> {

    private static final MyLogger logger = MyLogger.getLogger( GroovySwingWorker.class.getName() );
    JFileFinderWin jFileFinderWin = null;
    CodeProcessorPanel codeProcessorPanel = null;
    String currentDirectory = null;
    String currentFile = null;
    DefaultComboBoxModel defaultComboBoxModel = null;
    JRunGroovy jRunGroovy = null;

    public GroovySwingWorker( JFileFinderWin jFileFinderWin, CodeProcessorPanel codeProcessorPanel, JRunGroovy jRunGroovy, String currentDirectory, String currentFile, DefaultComboBoxModel defaultComboBoxModel )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.codeProcessorPanel = codeProcessorPanel;
        this.jRunGroovy = jRunGroovy;
        this.currentDirectory = currentDirectory;
        this.currentFile = currentFile;
        this.defaultComboBoxModel = defaultComboBoxModel;
        }

    @Override
    public ResultsData doInBackground() {
        jFileFinderWin.stopDirWatcher();
        codeProcessorPanel.setProcessStatus(codeProcessorPanel.PROCESS_STATUS_STARTED );
        jRunGroovy.run();
        return jRunGroovy.getResultsData();
    }

    @Override
    public void done() {
        try {
            logger.info( "entered GroovySwingWork.done()" );
            ResultsData resultsData = get();
            String partialMsg = "";
            String msg =  "Done";
            if ( resultsData.getSearchWasCanceled() )
                {
                codeProcessorPanel.setProcessStatus(codeProcessorPanel.PROCESS_STATUS_CANCELED );
                msg = msg + " PARTIAL run.";
                }
            else
                {
                codeProcessorPanel.setProcessStatus(codeProcessorPanel.PROCESS_STATUS_COMPLETED );
                if ( codeProcessorPanel instanceof ScriptSwingWorker )
                    {
                    logger.info( "do new CloseWinOnTimer( ScriptSwingWorker, 4000 )" );
                    new CloseWinOnTimer( codeProcessorPanel, 4000 ){{setRepeats(false);}}.start();
                    }
                }

            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
                {
                codeProcessorPanel.setProcessStatus( resultsData.getProcessStatus() );
                }
            if ( ! resultsData.getMessage().trim().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }

            codeProcessorPanel.setMessage( msg + partialMsg );
            codeProcessorPanel.setResultsData( resultsData );
            
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
            logger.info( "Error in CodeProcessorPanelSwingWorker(): " + why);
            e.printStackTrace();
            }
    }    
}
