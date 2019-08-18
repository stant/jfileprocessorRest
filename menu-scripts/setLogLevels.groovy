package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import com.towianski.models.ResultsData;

// written by: Stan Towianski - August 2017

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered runCommandOnSelectedFiles.groovy.main()");
//        def test = new Test();

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
        ResultsData resultsData = binding.getVariable( "resultsData" );

        String winList = codeProcessorPanel.getSelectedList();
        System.out.println( "selected item =" + winList + "=" );

        // we are going to write output to this file !
        def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "NewFile-" + winList + ".txt" );
        outFile.write "";

        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
            
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocessor.Copier", Level.ALL );
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocessor.CopyFrame", Level.ALL );
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocessor.JFileCopy", Level.ALL );
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocess.actions.HandleSearchBtnQueue", Level.FINE );
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocess.actions.HandleSearchBtnQueueSw", Level.FINE );
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocess.actions.WatchDirEventsToCallerEventsQueue", Level.FINE );
        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "com.towianski.jfileprocessor.JFileFinderWin", Level.FINE );
//        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "towianski", Level.ALL );
//        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "towianski", Level.ALL );
//        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "towianski", Level.ALL );
//        codeProcessorPanel.jFileFinderWin.logger.setLoggerLevels( "towianski", Level.ALL );
    
   }  
