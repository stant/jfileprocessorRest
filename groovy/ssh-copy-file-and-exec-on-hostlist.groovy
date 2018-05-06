package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import com.towianski.models.ResultsData;
import com.towianski.models.CircularArrayList;

// written by: Stan Towianski - August 2017

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered ssh-hostlist.main()");
//        def test = new Test();
        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
        ResultsData resultsData = binding.getVariable( "resultsData" );

        // You can get a specific list if you want - 
//          defaultComboBoxModel  = codeProcessorPanel.getListPanelModel( "hosts1" );
   //     System.out.println( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
         String strlist = codeProcessorPanel.listOfLists.getSelectedItem();

        // we are going to write output to this file !
        def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + strlist + "-output.txt" );
        outFile.write "";

        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String str = "";
        String auser = "auser";

//        String[] cmd = new String[5];
//        cmd[0] = "ssh";
//        cmd[1] = "-i";
//        cmd[2] = "/home/" + auser + "/.ssh/id_rsa";

        /*  Copy file to remote host /tmp and execute it
        **  assume already created and copied ssh authorized key to remote system.
        **  This assumes using same hardcoded user for ssh. can put users in another file and passwords if needed
        **  and read those in the for loop also.
        */
        
        List<String> cmdAl = new ArrayList<String>();
        String[] cmd = null;
        def outlines = "";

        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            outFile << System.getProperty("line.separator") + "------ " + i + " - " + str + "  -------------------------------" + System.getProperty("line.separator");

            //-------  copy file to remote host  -------
            cmdAl.clear();;
            cmdAl.add( "scp" );
            cmdAl.add( "-i" );
            cmdAl.add( "/home/" + auser + "/.ssh/id_rsa" );
//            cmd[3] = auser + "@" + str;
//            cmd[4] = "hostname; ls -l /etc; cat /etc/resolv.conf;";
            cmdAl.add( "/somewhere/programs/jFileProcessor/groovy/test.sh" );
            cmdAl.add( auser + "@" + str + ":/tmp" );
            System.out.println( "cmdAl =" + cmdAl + "=" );

            cmd = cmdAl.toArray(new String[0]);
            outlines = cmd.execute().text
            outlines.eachLine{
                outFile << it + System.getProperty("line.separator");
                }

            //-------  Execute the file I copied over to /tmp  -------
            cmdAl.clear();;
            cmdAl.add( "ssh" );
            cmdAl.add( "-i" );
            cmdAl.add( "/home/" + auser + "/.ssh/id_rsa" );
            cmdAl.add( auser + "@" + str );
//            cmdAl.add( "ls -al /tmp/JF*.jar" );
            cmdAl.add( "/tmp/test.sh" );
            cmd = cmdAl.toArray(new String[0]);
            outlines = cmd.execute().text
            outlines.eachLine{
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

        TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, outFile.toString() );
        textEditPanel.setState ( JFrame.ICONIFIED );

        textEditPanel.pack();
        textEditPanel.setVisible(true);
        textEditPanel.setState ( JFrame.NORMAL );

   }  
