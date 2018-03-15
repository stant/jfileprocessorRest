package com.towianski.testutils;

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered Test.main()");
//        def test = new Test();

        // we are going to write output to this file !
        def outFile = new File( "/tmp/NewFile.txt" );
        outFile.write "";

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

        System.out.println( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String str = "";
        def atFile = null;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            System.out.println( "check for other list index =" + i + "   str =" + str + "=" );

            if ( 0 == 1 )  // example for list as files
                {
    //            String fileContents = new File( str ).text
    //            outFile << fileContents;
    //            String cmd = "ls -l " + str;
                String cmd = "grep root " + str;
                def list = cmd.execute().text
                list.eachLine{
                    outFile << it;
                    }
                outFile << System.getProperty("line.separator") + "-------------------------------------" + System.getProperty("line.separator");
                }
            else    // example for list of string
                {
                // text after last "root"
                if ( ( matcher = str =~ /.*(root)(.*)/) )
                outFile << matcher[0][1] + " - " + matcher[0][2];
                }
            }
   }  

//}

//Test test = new Test();

//println test.sayHello() 
//println test.doIt() 
