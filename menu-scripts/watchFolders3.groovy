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
import com.towianski.jfileprocessor.WatchFileEventsSw2;
import com.towianski.jfileprocess.actions.ProcessInThread;
import java.io.IOException;
import com.towianski.jfileprocess.actions.Player;

import windows.*;
//import org.codehaus.groovy.tools.RootLoader;
//import java.net.URL;

// written by: Stan Towianski - March 2019

class WatchFolders3Impl {
    
    BlockingQueue<Integer> allowToRunQueue = new LinkedBlockingQueue<>(100);
    boolean runFlag = true;
    com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = null;
    def defaultComboBoxModel = null;
    ResultsData resultsData = null;


    public WatchFolders3Impl() {}

    public void run()
    {
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

    System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

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
    if ( numItems < 1 )
        {
        if ( ! codeProcessorPanel.jFileFinderWin.getStartingFolder().equals( "" ) )
            {
            pathList.add( Paths.get( codeProcessorPanel.jFileFinderWin.getStartingFolder() ) );
            }            
        }

    // we are going to write output to this file !
    def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "watchFolders3-" + pathList.get(0).toString().replace( System.getProperty( "file.separator"), "-") + ".log" );
    outFile.write "";
    System.out.println( "outfile =" + outFile + "=" );

    // Create a Queue to receive your file create events
    BlockingQueue<FileTimeEvent> fileEventQueue = new LinkedBlockingQueue<>(1000);

    // args: needed, arrayList of paths to watch, millisecond gap if file does not change in that time consider it done, your queue to receive events
    WatchFileEventsSw2 watchFileEventsSw2 = new WatchFileEventsSw2( "watchFolders3", pathList, 1000, fileEventQueue );
    watchFileEventsSw2.startWatchService();
  
    // pass in your watcher that I can call the .stop() method on when you hit the "cancel" button
    codeProcessorPanel.setPlayer( watchFileEventsSw2 );
    
    restart();
    
    System.out.println( "\nfinal Q => " );
    outFile << "final Q => " + System.getProperty("line.separator");
    while ( runFlag ) 
        {
        try {
            if ( codeProcessorPanel.getStopSearch() )
                {
                outFile << "--Canceled--" + System.getProperty("line.separator");
                resultsData.setProcessStatus( codeProcessorPanel.PROCESS_STATUS_COPY_CANCELED );
                resultsData.setMessage( "by user" );
                break;
                }
            System.out.println("watchFolders3 waiting to get runallow token" );
            allowToRunQueue.take();   // blocks until get a token denoting allowed to run.

            watchFileEventsSw2.restart();

            while( true )
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
    //                Files.move( fte.getFullFilePath(), 
    //                    Paths.get("/net2/watch-final-2").resolve( Paths.get( fte.getFilename() ) ), 
    //                    StandardCopyOption.REPLACE_EXISTING);
                    Files.copy( fte.getFullFilePath(), 
                        Paths.get("/net2/watch-1").resolve( Paths.get( fte.getFilename() ) ),   // c:\\watch-1
                        StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists( fte.getFullFilePath() );
                    }
                catch (Exception e) {
                    restart();
                    e.printStackTrace();
                    }
                }// while
            }
        catch (InterruptedException ex)
            {
            System.out.println("watchFolders3 waiting to get runallow token" );
            }
        catch (Exception e2) {
            e2.printStackTrace();
            }
        } // while
    watchFileEventsSw2.stop();
    System.out.println( "<== final Q" );
    outFile << "<== final Q" + System.getProperty("line.separator");
    
//       resultsData.setMessage( "junk msg" );

    TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, outFile.toString() );
    textEditPanel.setState ( JFrame.ICONIFIED );

    textEditPanel.pack();
    textEditPanel.setVisible(true);
    textEditPanel.setState ( JFrame.NORMAL );
    }

public void restart()
    {
    try {
        allowToRunQueue.put( 1 );
        }
    catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
   }
 

 static void main(String[] args) {
    System.out.println( "entered watchFolders3.groovy.main()");
//    com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
//    def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
//    ResultsData resultsData = binding.getVariable( "resultsData" );

    WatchFolders3Impl watchFolders3Impl = new WatchFolders3Impl();
    watchFolders3Impl.codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
    System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
    watchFolders3Impl.defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
    watchFolders3Impl.resultsData = binding.getVariable( "resultsData" );

    watchFolders3Impl.run();
 }