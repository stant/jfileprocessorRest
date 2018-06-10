package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.towianski.models.ResultsData;

// written by: Stan Towianski - August 2017

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered duSelected.main()");
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
        
        //numItems = 0;
        String str = "";
        def atFile = null;
        //numItems = 10;
        ArrayList<String> cmdAl = new ArrayList<String>();
        cmdAl.add( "du" );
        cmdAl.add( "-sm" );
        for( int i = 0; i < numItems; i++ )
            {
            cmdAl.clear();
            cmdAl.add( "du" );
            cmdAl.add( "-sm" );
            str = defaultComboBoxModel.getElementAt( i ).toString();

            System.out.println( "check for other list index =" + i + "   str =" + str + "=" );

    //            String fileContents = new File( str ).text
    //            outFile << fileContents;
                //String cmd = "grep -i " + grepText + " " + "${str}";
                cmdAl.add( str );
                def lines = cmdAl.execute().text
                def list = lines.readLines()
                //outFile << "run cmdAl =" + cmdAl + "   list.size() =" + list.size() +  System.getProperty("line.separator");
                //if ( 1 == 1 )
                if ( list.size() > 0 )  // != null && ! list.equals( "" ) )
                {
            outFile << System.getProperty("line.separator") + "------ " + (i + 1) + " - " + str + "  -------------------------------" +  System.getProperty("line.separator");
               lines.eachLine{
                    outFile << it + System.getProperty("line.separator");
                    }
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
