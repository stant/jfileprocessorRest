package com.towianski.testutils;

// written by: Stan Towianski - August 2017

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered test.main()");
//        def test = new Test();

        // we are going to write output to this file !

        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Save To" );
        if ( codeProcessorPanel.jFileFinderWin.getStartingFolder().trim().equals( "" ) )
            {
            chooser.setCurrentDirectory( new java.io.File(".") );
            }
        else
            {
            chooser.setCurrentDirectory( new java.io.File( codeProcessorPanel.jFileFinderWin.getStartingFolder().trim() ) );
            }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
        def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "NewFile.txt" );
        if ( chooser.showDialog( codeProcessorPanel.jFileFinderWin, "Select" ) == JFileChooser.APPROVE_OPTION )
            {        
            outFile = chooser.getSelectedFile();
            outFile.write "";
            }

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

        System.out.println( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
//        String ans = JOptionPane.showInputDialog( "numItems selected:: ", numItems );

        String str = "";
        def atFile = null;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            System.out.println( "check for other list index =" + i + "   str =" + str + "=" );

    //            String fileContents = new File( str ).text
    //            outFile << fileContents;
                String cmd = "ls -l  " + str;
                def list = cmd.execute().text
                list.eachLine{
                    outFile << it + System.getProperty("line.separator");
                    }
                cmd = "/bin/sleep 7";
      
list = cmd.execute().text
                outFile << System.getProperty("line.separator") + "-------------------------------------" + System.getProperty("line.separator");
            }
   }  
