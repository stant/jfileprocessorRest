package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.towianski.models.ResultsData;
import windows.*;
//import org.codehaus.groovy.tools.RootLoader;
//import java.net.URL;

// written by: Stan Towianski - March 2019

class Test {
}

 static void main(String[] args) {
    System.out.println( "entered renameFiles.groovy.main()");

    // def jardir = new File( "/net2/programs/jfp/JfpLib.jar")
    // def urlList = [];
    // def jars  = jardir.listFiles().findAll { it.name.endsWith(".jar") }
    // println "groovy 2"
    // jars.each {
    // 	URL url = it.toURI().toURL()
    // 	println "Loading lib: $url ..."
    // 	urlList.add(url)
    // }

    // This works if you want to do it just from groovy instead of Jfp java.
//     def myjar = new File( "/net2/programs/jfp/JfpLib.jar")
//     URL url = myjar.toURI().toURL()
//     println "Loading lib: $url ..."
//     def urlList = [];
//     urlList.add(url)
// 
//     def urls = (URL[]) urlList.toArray()
//     def loader = new RootLoader( urls, this.class.classLoader)

    com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
    def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
    System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
    ResultsData resultsData = binding.getVariable( "resultsData" );

    String winList = codeProcessorPanel.getSelectedList();
    System.out.println( "selected item =" + winList + "=" );

    // we are going to write output to this file !
    def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "RenameFiles-" + winList + ".txt" );
    outFile.write "";

    int numItems = defaultComboBoxModel.getSize();
    System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );

    // This works if you want to do it just from groovy instead of Jfp java.
//     Class<?> clz = loader.loadClass("windows.RenameFiles");
//     Object renameFiles = clz.newInstance();
    RenameFiles renameFiles = new RenameFiles();
    
    renameFiles.setVisible(true);
    System.out.println( "renameFiles.getFindText() =" + renameFiles.getFindText() + "=" );
    System.out.println( "renameFiles.getReplaceText() =" + renameFiles.getReplaceText() + "=" );
    if ( ! renameFiles.getContinueFlag() )
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
        Path folder = path.getParent();

        strName = strName.replaceAll( renameFiles.getFindText(), renameFiles.getReplaceText() );
        Path newPath = Paths.get( folder.toString() + System.getProperty("file.separator") + strName );
        System.out.println( "new file: =" + newPath + "=" );
        
        if ( ! path.toString().equals( newPath.toString() ) )
            {
            outFile << System.getProperty("line.separator") + "------ " + (i + 1) + " - " + str + "  -------------------------------" + System.getProperty("line.separator");

            outFile << "new file: =" + newPath + "=" + System.getProperty("line.separator");
            path.toFile().renameTo( newPath.toFile() );
        
            //def list = cmd.execute().text
            //list.eachLine{
            //    outFile << it + System.getProperty("line.separator");
            //    }
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
