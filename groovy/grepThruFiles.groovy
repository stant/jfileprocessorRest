package com.towianski.testutils;

// written by: Stan Towianski - August 2017

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import com.towianski.models.ResultsData;

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered grepThruFiles.main()");
//        def test = new Test();

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
        ResultsData resultsData = binding.getVariable( "resultsData" );

        String winList = codeProcessorPanel.listOfLists.getSelectedItem();
        System.out.println( "selected item =" + winList + "=" );

        // we are going to write output to this file !
        def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "NewFile-" + winList + ".txt" );
        outFile.write "";
        
        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String str = "";
        def atFile = null;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            outFile << System.getProperty("line.separator") + "------------  " + "[" + (i + 1) + "]   " + str + "   ----------------------------" + System.getProperty("line.separator");
            //System.out.println( "check for other list index =" + i + "   str =" + str + "=" );

    //            String fileContents = new File( str ).text
    //            outFile << fileContents;
    //            String cmd = "ls -l " + str;
                String cmd = "grep -i ArrayList " + str;
                def list = cmd.execute().text
                list.eachLine{
                    outFile << it;
                    }
            }

        TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, outFile.toString() );
        textEditPanel.setState ( JFrame.ICONIFIED );

        textEditPanel.pack();
        textEditPanel.setVisible(true);
        textEditPanel.setState ( JFrame.NORMAL );
   }  
