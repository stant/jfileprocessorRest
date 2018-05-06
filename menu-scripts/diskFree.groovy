package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import com.towianski.models.ResultsData;
import java.awt.Font;
import java.nio.file.*;
//import java.nio.file.FileSystems;

// written by: Stan Towianski - August 2017

class Test {
}

    static void printFileStore( FileStore store, StringBuffer strbuf ) throws IOException 
       {
        final long Mb = 1024000;
        long total = store.getTotalSpace() / Mb;
        long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / Mb;
        long avail = store.getUsableSpace() / Mb;
        long percentUsed = (long)((float)used/total*100);

        strbuf.append( String.format("%12d %12d %12d %6d%% %-40s\n", total, used, avail, percentUsed, store ) );
    }

 static void main(String[] args) {
        System.out.println( "entered df.main()");
        StringBuffer strbuf = new StringBuffer();
//        def test = new Test();

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
        ResultsData resultsData = binding.getVariable( "resultsData" );

        strbuf.append( String.format("%12s %12s %12s %6s %-40s\n", "mbytes", "used", "avail", " % used", "Mounted on (from Filesystem)" ) );
        FileSystem fs = FileSystems.getDefault();
        for (FileStore store: fs.getFileStores()) {
            printFileStore( store, strbuf );
        }

        System.out.println( "strbuf =" + strbuf + "=" );
        TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin );
        textEditPanel.setText( strbuf.toString() );
        textEditPanel.setState( JFrame.ICONIFIED );

        textEditPanel.pack();
        textEditPanel.setVisible(true);
        textEditPanel.setFont( new Font("monospaced", Font.PLAIN, 12) );
        textEditPanel.setState ( JFrame.NORMAL );

   }  
