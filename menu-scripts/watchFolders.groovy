package com.towianski.testutils;

import com.towianski.jfileprocessor.TextEditPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.towianski.models.ResultsData;
import com.towianski.models.FileTimeEvent;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.towianski.jfileprocessor.WatchFileEventsSw;
import com.towianski.jfileprocess.actions.WatchDirToCallerEventQueue;
import com.towianski.jfileprocess.actions.ProcessInThread;
import java.io.IOException;
import com.towianski.jfileprocess.actions.StartStop;

import windows.*;
//import org.codehaus.groovy.tools.RootLoader;
//import java.net.URL;

// written by: Stan Towianski - March 2019

class Test {
}
    

 static void main(String[] args) {
    System.out.println( "entered watchFolders.groovy.main()");

    WatchDirToCallerEventQueue watchDirToCallerEventQueue = null;

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

    int numItems = defaultComboBoxModel.getSize();
    System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
    ArrayList<Path> pathList = new ArrayList<Path>();

    for( int i = 0; i < numItems; i++ )
        {
        String watchDirStr = defaultComboBoxModel.getElementAt( i ).toString() + System.getProperty( "file.separator");
        pathList.add( Paths.get( watchDirStr ) );
        }

    // we are going to write output to this file !
    def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "watchFolders-" + pathList.get(0).toString().replace( System.getProperty( "file.separator"), "-") + ".log" );
    outFile.write "";
    System.out.println( "outfile =" + outFile + "=" );

    // Create a Queue to receive your file create events
    BlockingQueue<FileTimeEvent> fileEventQueue = new LinkedBlockingQueue<>(500);
  
    // args: needed, arrayList of paths to watch, millisecond gap if file does not change in that time consider it done, your queue
    //watchDirToCallerEventQueue = new WatchDirToCallerEventQueue( codeProcessorPanel.jFileFinderWin.getWatchFileEventsSw() , watchDirPath, 3000, fileEventQueue );   // to pass a single dir if you want
    watchDirToCallerEventQueue = new WatchDirToCallerEventQueue( "watchFolders-1", codeProcessorPanel.jFileFinderWin.getWatchFileEventsSw() , pathList, 3000, fileEventQueue );

    // pass in your watcher that I can call the .stop() method on when you hit the "cancel" button
    codeProcessorPanel.setStartStop( watchDirToCallerEventQueue );
    
    Thread qThread = ProcessInThread.newThread( "watchDirToCallerEventQueue-watchFolders-1", 0, true, watchDirToCallerEventQueue );
    qThread.start();
    
    System.out.println( "\nfinal Q => " );
    outFile << "final Q => " + System.getProperty("line.separator");
    try {
        while (true) 
            {
            FileTimeEvent fte = fileEventQueue.take();
            System.out.println( "> " + fte.getFilename() + "   event =" + fte.getEvent() + System.getProperty("line.separator") );
            if ( fte.getEvent() == null )
                {
                outFile << "--Canceled--" + System.getProperty("line.separator");
                resultsData.setProcessStatus( codeProcessorPanel.PROCESS_STATUS_COPY_CANCELED );
                resultsData.setMessage( "by user" );
                break;
                }
            outFile << "new file: =" + "> " + fte.getFilename() + "   event =" + fte.getEvent() + System.getProperty("line.separator");
                
            // HERE IS WHERE YOU DO WHAT YOU WANT WITH THE FILES CREATED !
            try {
                Files.move( fte.getFullFilePath(), 
                    Paths.get("/net2/watch-2").resolve( Paths.get( fte.getFilename() ) ), 
                    StandardCopyOption.REPLACE_EXISTING);
                }
            catch (Exception e) {
                e.printStackTrace();
                }
            }
        } 
    catch (Exception e2) {
        e2.printStackTrace();
        //Thread.currentThread().interrupt();
        }
    finally
        {
        if ( qThread != null )
            {
            System.out.println( "watchFolders - before qThread.join()" );
            if ( qThread.isAlive() )
                {
                try
                    {
                    qThread.join();
                    } 
                catch (InterruptedException ex)
                    {
                    Logger.getLogger(WatchDirSw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            System.out.println( "watchFolders - after qThread.join()" );
            }
        }
    System.out.println( "<== final Q" );
    outFile << "<== final Q" + System.getProperty("line.separator");
    
//       resultsData.setMessage( "junk msg" );

//     TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, outFile.toString() );
//     textEditPanel.setState ( JFrame.ICONIFIED );
// 
//     textEditPanel.pack();
//     textEditPanel.setVisible(true);
//     textEditPanel.setState ( JFrame.NORMAL );

   }  
