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
import com.towianski.jfileprocess.actions.ProcessInThread;
import java.io.IOException;
import com.towianski.jfileprocess.actions.Player;

import windows.*;

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
    System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

    codeProcessorPanel.jFileFinderWin.startDirWatcher();  // I think I can leave this on.

    String winList = codeProcessorPanel.getSelectedList();
    System.out.println( "selected item =" + winList + "=" );

    int numItems = defaultComboBoxModel.getSize();
    System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
    ArrayList<Path> pathList = new ArrayList<Path>();

    if ( numItems < 1 )
        {
        if ( ! codeProcessorPanel.jFileFinderWin.getStartingFolder().equals( "" ) )
            {
            pathList.add( Paths.get( codeProcessorPanel.jFileFinderWin.getStartingFolder() ) );
            System.out.println( "no files selected so Use starting Folder to watch =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );
            }            
        }
    else
        {
        for( int i = 0; i < numItems; i++ )
            {
            String watchDirStr = defaultComboBoxModel.getElementAt( i ).toString() + System.getProperty( "file.separator");
            Path path = Paths.get( watchDirStr );
            if ( Files.isDirectory( path ) )
                pathList.add( path );
            else
            pathList.add( path.getParent() );
            }
        }

    // we are going to write output to this file !
    def outFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty( "file.separator") + "watchFolders3-" + pathList.get(0).toString().replace( System.getProperty( "file.separator"), "-") + ".log" );
    outFile.write "";
    System.out.println( "outfile =" + outFile + "=" );

    // Create a Queue to receive your file create events
    BlockingQueue<FileTimeEvent> fileEventQueue = new LinkedBlockingQueue<>(100100);

    // args: a name, arrayList of paths to watch, string with C/D/M create, delete. modify. events you want to watch
    // millisecond gap if file does not change in that time consider it done, your queue to receive events
    WatchFileEventsSw watchFileEventsSw = new WatchFileEventsSw( "watchFolders3", pathList, "CM", 1000, fileEventQueue );
  
    // pass in your watcher that I can call the .stop() method on when you hit the "cancel" button
    codeProcessorPanel.setPlayer( watchFileEventsSw );
    
    watchFileEventsSw.startEventTimeQueue();
    putAllowToRunQueue();
    
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
    System.out.println( "putAllowToRunQueue() allowToRunQueue.size() = " + allowToRunQueue.size() );
            System.out.println("watchFolders3 waiting to get runallow token" );
            allowToRunQueue.take();   // blocks until get a token denoting allowed to run.
            System.out.println("watchFolders3 after .take()" );

            watchFileEventsSw.restart();
            System.out.println("watchFolders3 after watchFileEventsSw.restart();" );

            while( true )
                {
                System.out.println("watchFolders3 Waiting at fileEventQueue.take()" );
                FileTimeEvent fte = fileEventQueue.take();
                System.out.println( "watchFolders3 > " + fte.getFilename() + "   event =" + fte.getEventKind() + System.getProperty("line.separator") );
                if ( fte.getEventKind() == null )
                    {
                    outFile << "--Canceled--" + System.getProperty("line.separator");
                    resultsData.setProcessStatus( codeProcessorPanel.PROCESS_STATUS_COPY_CANCELED );
                    resultsData.setMessage( "by user" );
                    break;
                    }
                outFile << System.getProperty("line.separator") + "new file: =" + "> " + fte.getFilename() + "   event =" + fte.getEventKind() + System.getProperty("line.separator");
                if ( fte.getFullFilePath() == null )
                    {
                    outFile << "Error getFullFilePath: =" + "> " + fte.getFullFilePath() + "   event =" + fte.getEventKind() + "   watchKey =" + fte.getwatchKey() + System.getProperty("line.separator");
                    continue;
                    }

                // HERE IS WHERE YOU DO WHAT YOU WANT WITH THE FILES CREATED !
                try {
    //                Files.move( fte.getFullFilePath(), 
    //                    Paths.get("/net2/watch-final-2").resolve( Paths.get( fte.getFilename() ) ), 
    //                    StandardCopyOption.REPLACE_EXISTING);
                    outFile << "copy file: =" + "> " + fte.getFullFilePath() + "=";
                    outFile << "  to file: =" + "> " + Paths.get("/net2/watch-3").resolve( Paths.get( fte.getFilename() ) ) + "=" + System.getProperty("line.separator");;
                    Files.copy( fte.getFullFilePath(), 
                        Paths.get("/net2/watch-final-1").resolve( Paths.get( fte.getFilename() ) ),   // c:\\watch-1
                        StandardCopyOption.REPLACE_EXISTING);
                    outFile << "delete file: =" + "> " + fte.getFullFilePath() + System.getProperty("line.separator");
                    Files.deleteIfExists( fte.getFullFilePath() );
                    }
                catch (Exception e) {
                    putAllowToRunQueue();
                    e.printStackTrace();
                    }
                }// while
            }
        catch (InterruptedException ex)
            {
            System.out.println("watchFolders3 waiting InterruptedException" );
            }
        catch (Exception e2) {
            System.out.println("watchFolders3 waiting Exception" );
            e2.printStackTrace();
            }
        } // while
    watchFileEventsSw.stop();
    System.out.println( "<== final Q" );
    outFile << "<== final Q" + System.getProperty("line.separator");
    
//       resultsData.setMessage( "junk msg" );

    TextEditPanel textEditPanel = new TextEditPanel( codeProcessorPanel.jFileFinderWin, outFile.toString() );
    textEditPanel.setState ( JFrame.ICONIFIED );

    textEditPanel.pack();
    textEditPanel.setVisible(true);
    textEditPanel.setState ( JFrame.NORMAL );
    }

public void putAllowToRunQueue()
    {
    try {
        allowToRunQueue.put( 1 );
        }
    catch (InterruptedException ex) {
        System.out.println( "putAllowToRunQueue() InterruptedException" );
        ex.printStackTrace();
        }
    System.out.println( "putAllowToRunQueue() allowToRunQueue.size() = " + allowToRunQueue.size() );
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
