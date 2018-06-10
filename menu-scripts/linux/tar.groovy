package com.towianski.testutils;

import javax.swing.JFrame;
import javax.swing.JFileChooser
import javax.swing.JOptionPane;
import com.towianski.jfileprocessor.TextEditPanel;
import com.towianski.jfileprocessor.CodeProcessorPanel;
import com.towianski.jfileprocessor.JFileFinderWin;

// written by: Stan Towianski - August 2017

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered tar()");
//        def test = new Test();

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "Tar file to Save To" );
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
    
    if ( chooser.showDialog( codeProcessorPanel.jFileFinderWin, "Select" ) == JFileChooser.APPROVE_OPTION )
        {        
        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String str = "";
        StringBuffer zipFileList = new StringBuffer();
        String[] zipFileAr = new String[ numItems + 3];
        def atFile = null;
        zipFileAr[0] = "tar";
        zipFileAr[1] = "cvfz";
        zipFileAr[2] = chooser.getSelectedFile();
        int offset = 3;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            //System.out.println( "add to tar file =" + i + "   str =" + str + "=" );

            zipFileList.append( " '" ).append ( str ).append( "'" );
            zipFileAr[i+offset] = str;
            //System.out.println( "zipFileList =" + zipFileList + "=" );
            }
        System.out.println( "tar " + chooser.getSelectedFile() + " " + zipFileList );
//        String cmd = "zip "  + chooser.getSelectedFile() + " " + zipFileList;
//        def output = cmd.execute().text;
        def output = zipFileAr.execute().text;

        TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, null );
        textEditPanel.setState ( JFrame.ICONIFIED );

        textEditPanel.setText( output );
        textEditPanel.pack();
        textEditPanel.setVisible(true);
        textEditPanel.setState ( JFrame.NORMAL );
        }
}  
