package com.towianski.testutils;

// written by: Stan Towianski - August 2017

class Test {
}

 static void main(String[] args) {
        logger.info( "entered parse.main()");
//        def test = new Test();

        // we are going to write output to this file !
        def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "NewFile.txt" );
        outFile.write "";

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        logger.info( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

        logger.info( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
        int numItems = defaultComboBoxModel.getSize();
        logger.info( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String str = "";
        def atFile = null;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            logger.info( "check for other list index =" + i + "   str =" + str + "=" );

            defaultComboBoxModel.removeElementAt( i );
            defaultComboBoxModel.insertElementAt( str.replaceFirst( /f:\\temp\\/, "f:\\temp2\\" ), i );
            }
   }  
