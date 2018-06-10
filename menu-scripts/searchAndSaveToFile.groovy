package com.towianski.testutils;

import com.towianski.jfileprocessor.CodeProcessorPanel;
import com.towianski.jfileprocessor.JFileFinderWin;
import java.awt.Color
import java.lang.InterruptedException;
import com.towianski.jfileprocessor.AfterFillSwingWorker;
import java.io.File;

// written by: Stan Towianski - August 2017

public class AfterFillSwingWorkerTmp extends AfterFillSwingWorker
    {
    static JFileFinderWin jFileFinderWin = null;
    static CodeProcessorPanel codeProcessorPanel = null;

    // To make a process run After a "search", create a swingWorker and set a variable to via: setAfterFillSwingWorker()
    public AfterFillSwingWorkerTmp( CodeProcessorPanel codeProcessorPanelArg )
        {
        //super( jFileFinderWin );
        this.jFileFinderWin = codeProcessorPanelArg.jFileFinderWin;
        this.codeProcessorPanel = codeProcessorPanelArg;
        System.out.println( "entered AfterFillSwingWorkerTmp() constructor" );
        }
    
    @Override
    public void done() {
        try {
            System.out.println( "entered AfterFillSwingWorkerTmp.done()" );
            String ans = get();

//            File selectedPath = new File(  System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") +"auto-created.txt" );
//            System.out.println( "savePathsToFile() to selected row file =" + selectedPath );
//            jFileFinderWin.savePathsToFile( selectedPath );  // This one saves search results to a File.
            jFileFinderWin.openPathsToList( null, "files" );  // This one saves search results to a List window called "files"

          // Example of opening another code window, loading a groovy file and selecting the list window "files" to point to.
           CodeProcessorPanel codeProcessorPanel2 = jFileFinderWin.openCodeWinPanel( jFileFinderWin, "/net2/github/JFileProcessor/groovy/listThruFiles.groovy", "files" );
           // now lets execute the groovy script in this code window !
           codeProcessorPanel2.doCmdBtnActionPerformed( null );
           // this actionPerform is what creates the NewFile.txt window

           // go back to regular file manager mode
           jFileFinderWin.setFileMgrMode( true );
           jFileFinderWin.callFileMgrModeActionPerformed( null );

            System.out.println( "exiting AfterFillSwingWorkerTmp.done()" );
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
            System.out.println("Error AfterFillSwingWorkerTmp() retrieving file: " + why);
            e.printStackTrace();
            }
        }
    };

static void main(String[] args) {
    System.out.println( "entered searchAndSaveToFile.main()");
//    def test = new Test();

    // we are going to write output to this file !
    def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "NewFile.txt" );
    outFile.write "";

    // read in binding args...
    com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
    def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
    
    System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" +  codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

    System.out.println( "codeProcessorPanel.jFileFinderWin.takeAfterFillSwingWorker() =" + codeProcessorPanel.jFileFinderWin.takeAfterFillSwingWorker() + "=" );
    // if you create a AfterFillSwingWorker() and set the value, after a fill files table, 
    // it will run this swingworker which I created above
    // if you comment out this line below, it will turn on search mode and do a search and stop, ie not run your AfterFillSwingWorkerTmp defined above.
    codeProcessorPanel.jFileFinderWin.setAfterFillSwingWorker( new AfterFillSwingWorkerTmp( codeProcessorPanel ) );
    
    // set jfp to search fileMgrMode
    codeProcessorPanel.jFileFinderWin.setFileMgrMode( false );
    codeProcessorPanel.jFileFinderWin.callFileMgrModeActionPerformed( null );

    // set your folder
  //  codeProcessorPanel.jFileFinderWin.setStartingFolder( "f:/programs/Boot2DockerforWindows/" );
    codeProcessorPanel.jFileFinderWin.setStartingFolder( "/net2/programs/Boot2DockerforWindows" );

    // hit search button for the folder you set and get a file list !
    codeProcessorPanel.jFileFinderWin.searchBtnAction( null );

    // it does not stop on the search so you have to catch post search in a swingWorker above.
   }
