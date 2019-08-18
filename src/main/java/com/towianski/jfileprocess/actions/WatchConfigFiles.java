/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.utils.DesktopUtils;
import static com.towianski.utils.DesktopUtils.getJfpConfigHome;
import com.towianski.utils.MyLogger;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 *
 * @author stan
 */
public class WatchConfigFiles implements Runnable
{
    private static final MyLogger logger = MyLogger.getLogger( WatchConfigFiles.class.getName() );
    private WatchService watchService = null;
    private JFileFinderWin jFileFinderWin = null;
    
    public WatchConfigFiles( JFileFinderWin jFileFinderWin )
    {
    this.jFileFinderWin = jFileFinderWin;   
    }
    
    public void cancelWatch()
        {
        logger.info( "WatchConfigFiles set cancelFlag to true");

        try {
            watchService.close();
            }
        catch (Exception ex)
            {
            logger.info( "WatchConfigFiles set cancelFlag caught error !");
            logger.severeExc( ex );
            }
        logger.info( "WatchConfigFiles exit cancelSearch()");
        }
    
    @Override
    public void run() {
        //logger.info( "entered WatchConfigFiles run()" );
        //logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            
            String bookmarksFile = Paths.get( DesktopUtils.getBookmarks().toString() ).getFileName().toString();
            logger.info( "Bookmarks File =" + bookmarksFile + "=" );
            String fileAssocListFile = Paths.get( getJfpConfigHome( "FileAssocList.json", "file", false ).toString() ).getFileName().toString();
            logger.info( "fileAssocListFile =" + fileAssocListFile + "=" );
            Path watchDir = Paths.get( getJfpConfigHome( "", "folder", false ).toString() );
            boolean bookmarksChanged = false;
            boolean fileAssocListChanged = false;
            
            watchDir.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    logger.info( 
                            "Event kind:" + event.kind()
                                    + ". File affected =" + event.context() + "=");
                    if ( event.kind() == StandardWatchEventKinds.ENTRY_MODIFY )
                        {
                        //logger.info( "GOT ENTRY_MODIFY" );
                        if ( event.context().toString().equals( bookmarksFile ) )
                            {
                            logger.info( "Bookmarks File Changed =" + bookmarksFile + "=" );
                            bookmarksChanged = true;
                            Thread.sleep( 1000 );
                            }
                        else if ( event.context().toString().equals( fileAssocListFile ) )
                            {
                            logger.info( "fileAssocListFile Changed =" + fileAssocListFile + "=" );
                            fileAssocListChanged = true;
                            Thread.sleep( 1000 );
                            }
                        }
                } // poll loop
                
                if ( bookmarksChanged )
                    {
                    logger.info( "readin Bookmarks" );
                    bookmarksChanged = false;
                    jFileFinderWin.readInBookmarks();
                    }
                if ( fileAssocListChanged )
                    {
                    logger.info( "readin fileAssocListFile" );
                    fileAssocListChanged = false;
                    jFileFinderWin.readInFileAssocList();
                    }
                key.reset();
            }
        } catch (IOException ex) {
            logger.severeExc( ex );
        } catch (InterruptedException ex) {
            logger.severeExc( ex );
        }
    }
}    
