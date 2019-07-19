/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.utils.DesktopUtils;
import static com.towianski.utils.DesktopUtils.getJfpConfigHome;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class WatchConfigFiles implements Runnable
{
    private WatchService watchService = null;
    private JFileFinderWin jFileFinderWin = null;
    
    public WatchConfigFiles( JFileFinderWin jFileFinderWin )
    {
    this.jFileFinderWin = jFileFinderWin;   
    }
    
    public void cancelWatch()
        {
        System.out.println("WatchConfigFiles set cancelFlag to true");

        try {
            watchService.close();
            }
        catch (Exception ex)
            {
            System.out.println("WatchConfigFiles set cancelFlag caught error !");
            Logger.getLogger(WatchConfigFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("WatchConfigFiles exit cancelSearch()");
        }
    
    @Override
    public void run() {
        //System.out.println( "entered WatchConfigFiles run()" );
        //System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            
            String bookmarksFile = Paths.get( DesktopUtils.getBookmarks().toString() ).getFileName().toString();
            System.out.println( "Bookmarks File =" + bookmarksFile + "=" );
            String fileAssocListFile = Paths.get( getJfpConfigHome( "FileAssocList.json", "file", false ).toString() ).getFileName().toString();
            System.out.println( "fileAssocListFile =" + fileAssocListFile + "=" );
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
                    System.out.println(
                            "Event kind:" + event.kind()
                                    + ". File affected =" + event.context() + "=");
                    if ( event.kind() == StandardWatchEventKinds.ENTRY_MODIFY )
                        {
                        //System.out.println( "GOT ENTRY_MODIFY" );
                        if ( event.context().toString().equals( bookmarksFile ) )
                            {
                            System.out.println( "Bookmarks File Changed =" + bookmarksFile + "=" );
                            bookmarksChanged = true;
                            Thread.sleep( 1000 );
                            }
                        else if ( event.context().toString().equals( fileAssocListFile ) )
                            {
                            System.out.println( "fileAssocListFile Changed =" + fileAssocListFile + "=" );
                            fileAssocListChanged = true;
                            Thread.sleep( 1000 );
                            }
                        }
                } // poll loop
                
                if ( bookmarksChanged )
                    {
                    System.out.println( "readin Bookmarks" );
                    bookmarksChanged = false;
                    jFileFinderWin.readInBookmarks();
                    }
                if ( fileAssocListChanged )
                    {
                    System.out.println( "readin fileAssocListFile" );
                    fileAssocListChanged = false;
                    jFileFinderWin.readInFileAssocList();
                    }
                key.reset();
            }
        } catch (IOException ex) {
            Logger.getLogger(WatchConfigFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(WatchConfigFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}    
