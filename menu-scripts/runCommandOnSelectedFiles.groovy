package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.towianski.models.ResultsData;

// written by: Stan Towianski - August 2017

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered listThruFiles.main()");
//        def test = new Test();

        // we are going to write output to this file !
        def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "NewFile.txt" );
        outFile.write "";

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
        ResultsData resultsData = binding.getVariable( "resultsData" );

   //     System.out.println( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String baseCmd = null;
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            baseCmd = "ls -l %f";
            }
        else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            baseCmd = "cmd.exe /C dir %f";
            }
        else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "linux" ) )
            {
            baseCmd = "ls -l %f";
            }
        
        baseCmd = JOptionPane.showInputDialog( "command to run (%f=full file, %F=file name): ", baseCmd );
        if ( baseCmd == null )
            {
            numItems = 0;
            }
            
        String str = "";
        String strName = "";
        def atFile = null;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            Path path = Paths.get( str );
            strName = path.getFileName().toString();

            outFile << System.getProperty("line.separator") + "------ " + (i + 1) + " - " + str + "  -------------------------------" + System.getProperty("line.separator");

            String cmd = baseCmd;
            cmd = cmd.replace( "%f", str );
            cmd = cmd.replace( "%F", strName );
            System.out.println( "do: =" + cmd + "=" );
            def list = cmd.execute().text
            list.eachLine{
                outFile << it + System.getProperty("line.separator");
                }
            if ( codeProcessorPanel.getStopSearch() )
                {
                outFile << "--Canceled--" + System.getProperty("line.separator");
                resultsData.setProcessStatus( codeProcessorPanel.PROCESS_STATUS_COPY_CANCELED );
                resultsData.setMessage( "by user" );
                break;
                }
            }

 //       resultsData.setMessage( "junk msg" );

        TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, outFile.toString() );
        textEditPanel.setState ( JFrame.ICONIFIED );

        textEditPanel.pack();
        textEditPanel.setVisible(true);
        textEditPanel.setState ( JFrame.NORMAL );

   }  
