package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2017
 */

import com.towianski.jfileprocessor.services.CallGroovy;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import groovy.lang.Binding;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;



public class JRunGroovy //  implements Runnable 
{
    private static final MyLogger logger = MyLogger.getLogger( JRunGroovy.class.getName() );
    Boolean cancelFlag = false;
//    String processStatus = "";
//    String message = "";
    ResultsData resultsData = new ResultsData();
    Boolean cancelFillFlag = false;
    Boolean isDoingCutFlag = false;
    String startingPath = null;
    String currentDirectory = null;
    String currentFile = null;
    Boolean dataSyncLockGroovy = false;
    private DefaultComboBoxModel defaultComboBoxModel = null;
    CodeProcessorPanel codeProcessorPanel = null;
    JFileFinderWin jFileFinderWin = null;
    
    public JRunGroovy( JFileFinderWin jFileFinderWin, CodeProcessorPanel codeProcessorPanel, String startingPath, String currentDirectory, String currentFile, DefaultComboBoxModel defaultComboBoxModel )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.codeProcessorPanel = codeProcessorPanel;
        this.startingPath = startingPath;
        this.currentDirectory = currentDirectory;
        this.currentFile = currentFile;
        this.defaultComboBoxModel = defaultComboBoxModel;
        cancelFlag = false;
        }

    public void cancelSearch()
        {
        logger.info( "entered JRunGroovy.cancelSearch()" );
        cancelFlag = true;
//        copier.cancelSearch();
        }

    public void cancelFill()
        {
        cancelFillFlag = true;
        }
    
    public ResultsData getResultsData() 
        {
        //logger.info( "entered jfilecopy getResultsData()" );
//        ResultsData resultsData = new ResultsData();
//        try {
////            resultsData = new ResultsData( cancelFlag, copier.getProcessStatus(), copier.getMessage() );
//            resultsData = new ResultsData( cancelFlag, processStatus, message );
//            }
//        catch( Exception ex )
//            {
//            ex.printStackTrace();
//            }
        //ResultsData resultsData = new ResultsData();
        return resultsData;
        }
    
    static void usage() 
        {
        logger.info( "JRunGroovy <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
        }

    public void run() 
        {
        logger.info( "currentFile =" + currentFile + "=" );
        
//        copier = new Copier( jFileFinderWin, isDoingCutFlag, defaultComboBoxModel );
        try {
//            synchronized( dataSyncLockGroovy ) 
//                {
                cancelFlag = false;
                cancelFillFlag = false;
                File currentDir = new File( currentDirectory );
        //        this.pathRoots = new String[] { currentDir.getAbsolutePath() };
                logger.info( "before call: callGroovy() with String[] pathRoots (currentDir.getAbsolutePath()) =" + currentDir.getAbsolutePath() + "=" );
                CallGroovy callGroovy = new CallGroovy( new String[] { currentDir.getAbsolutePath() } );
                Binding binding = new Binding();
                logger.info( "start codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + this.jFileFinderWin.getStartingFolder() + "=" );

                binding.setProperty( "codeProcessorPanel", codeProcessorPanel );
                binding.setProperty( "defaultComboBoxModel", defaultComboBoxModel );
                binding.setProperty( "resultsData", resultsData );
                File tmpFile = new File( currentFile );
                logger.info( "before call: new callGroovy();" );
                callGroovy.groovyScriptEngineRun( tmpFile.getName(), binding );  
                logger.info( "after call: callGroovy.groovyScriptEngineRun()  processStatus =" + resultsData.getProcessStatus() );
                logger.info( "after call: callGroovy.groovyScriptEngineRun()  message =" + resultsData.getMessage() );
//                }
            }
        catch (Exception ex) 
            {
            logger.severeExc( ex );
            }
//        copier.done();
        if ( resultsData.getProcessStatus().equals( "" ) )
            {
            resultsData.setProcessStatus(CodeProcessorPanel.PROCESS_STATUS_COMPLETED );
            }
        }
        
    public static void main(String[] args) throws IOException 
        {
//        if (args.length < 3
//            || !args[1].equals("-name"))
//            usage();

//        Path startingDir = Paths.get(args[0]);
//        String pattern = args[2];

//        startingPath = "F:/data";
//        filePattern = "*.xml";
//        startingPath = args[0];
//        filePattern = args[1];
        logger.info( "java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

//        JFileCopy jfilefinder = new JFileCopy( args[0], args[1], args[2], null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            logger.severeExc( ex );
//        }
        }
}    
