/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.Constants;
import com.towianski.utils.DesktopUtils;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Stan Towianski
 */
public class Deleter extends SimpleFileVisitor<Path> 
{
    private Path fromPath;
    private long numFilesDeleted = 0;
    private long numFoldersDeleted = 0;
    private long numTested = 0;
    private ArrayList<String> errorList = new ArrayList<String>();

    boolean cancelFlag = false;
//    ArrayList<Path> deletePaths = new ArrayList<Path>();
    ArrayList<String> deletePaths = new ArrayList<String>();
    Boolean dataSyncLock = false;
    Boolean deleteFilesOnlyFlag = false;
    Boolean deleteToTrashFlag = true;
    Boolean deleteReadonlyFlag = false;
    Path trashFolder = DesktopUtils.getTrashFolder().toPath();
    String processStatus = "";
    String message = "";
    DeleteFrameSwingWorker swingWorker = null;
    int fsType = -1;
    
    public Deleter( String startingPath, ArrayList<String> deletePaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag, int fsType, DeleteFrameSwingWorker swingWorker )
    {
        this.fromPath = Paths.get( startingPath );
        this.deletePaths = deletePaths;
        this.deleteFilesOnlyFlag = deleteFilesOnlyFlag;
        this.deleteToTrashFlag = deleteToTrashFlag;
        this.deleteReadonlyFlag = deleteReadonlyFlag;
        this.fsType = fsType;
        this.swingWorker = swingWorker;
        System.out.println( "Deleter this.fromPath =" + this.fromPath + "=" );
        cancelFlag = false;        
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }
        
//    @Override
//    public FileVisitResult visitFileFailed( Path file, IOException exc )
//            throws IOException 
//        {
//        if ( exc instanceof AccessDeniedException ) 
//            {
////            return FileVisitResult.SKIP_SUBTREE;
//            try {
//                System.out.println( "Delete visitFileFailed 0" );
////                setMods( file );
//                commonVisitFile( file );
//                return FileVisitResult.CONTINUE;
//                }
//            catch ( Exception ex ) 
//                {
//                System.out.println( "Delete visitFileFailed Error: " + ex );
//                processStatus = "Error";
//                message = file + ": " + ex;
//                return FileVisitResult.TERMINATE;
//                }
//            }
//
//        System.out.println( "Delete visitFileFailed OTHER Error: " + exc );
//        return super.visitFileFailed(file, exc);
//    }
    
    @Override
    public FileVisitResult visitFile( Path fpath, BasicFileAttributes attrs ) 
            throws IOException 
        {
        System.out.println( "visitFile fpath =" + fpath );
        if ( cancelFlag )
            {
            System.out.println( "Delete cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        if ( fpath.toFile().isDirectory() )
            {
            System.out.println( "commonVisitFile fpath just return for folder =" + fpath );
            return FileVisitResult.CONTINUE;
            }
        try {
//            if ( deleteReadonlyFlag )   handled by calling chmod() before all deletes as timing does not work here !
//                {
//                setMods( fpath );
//                }
            if ( deleteToTrashFlag )
                {
                Path trashFpath = trashFolder.resolve( fromPath.relativize( fpath ) );
                Path trashparent = trashFolder.resolve( fromPath.relativize( fpath ) ).getParent();
                //System.out.println( "trashparent =" + trashparent );
                trashparent.toFile().mkdirs();
                if ( trashFpath.toFile().exists() )
                    {
                    trashFpath.toFile().setWritable( true );
                    if ( fsType == Constants.FILESYSTEM_DOS )
                        {
                        Files.setAttribute( trashFpath, "dos:readonly", false );
                        }        
                    }
                System.out.println( "visitFile() Files.copy trashFpath =" + trashFpath + "=" );
                Files.copy( fpath, trashFpath, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS );
                }
            System.out.println( "del fpath =" + fpath );
            Files.delete( fpath );
            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            processStatus = "Error";
            if ( fsType == Constants.FILESYSTEM_DOS && Files.isHidden( fpath ) )
                {
                if ( swingWorker != null )
                    {
                    JOptionPane.showMessageDialog( null, exAccessDenied.getClass().getSimpleName()
                                    + "\nThis is because of a hidden file: "
                                    + fpath
                                    + "\nEither delete hidden files first, or check \"Delete Read-Only\" which will also delete hidden files."
                                    , "Error", JOptionPane.ERROR_MESSAGE );
                    }
                message = "Handle hidden file first: " + fpath;
                }
            else if ( fsType == Constants.FILESYSTEM_POSIX )
                {
                if ( swingWorker != null )
                    {
                    JOptionPane.showMessageDialog( null, exAccessDenied.getClass().getSimpleName()
                                    + "\nThis is because of a file: "
                                    + fpath
                                    + "\nPossibly you have no rwx rights to the folder, or no read rights on the file."
                                    , "Error", JOptionPane.ERROR_MESSAGE );
                    }
                message = "Handle rights on file: " + fpath;
                }
            else
                {
                message = exAccessDenied.getClass().getSimpleName() + ": " + fpath;
                }
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, exAccessDenied );
            System.out.println( "visitFile() AccessDeniedException: " + "  " + exAccessDenied.getClass().getSimpleName() + ": " + fpath );
            exAccessDenied.printStackTrace();
            if ( deleteToTrashFlag )
                {
                System.out.println( "visitFile() copy toPath =" + trashFolder.resolve( fromPath.relativize( fpath ) ) + "=" );
                }
            // I tried to catch accessDenied from an error trying to delete a readOnly file
            // and then do file.setwritable(true) and delete it again, but it did not work and 
            // just cascaded errors down the road. Stan
            return FileVisitResult.TERMINATE;
            }
        catch ( Exception ex ) 
            {
            processStatus = "Error";
            message = ex.getClass().getSimpleName() + ": " + fpath;
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex );
            System.out.println( "visitFile() CAUGHT ERROR  " + "  " + ex.getClass().getSimpleName() + ": " + fpath );
            ex.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
        //System.out.println( "would delete file =" + file );
        numFilesDeleted++;
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult postVisitDirectory(Path fpath, IOException ex) 
            throws IOException
        {
        try {
            numTested++;
            if ( swingWorker != null )  swingWorker.publish2( numTested );
            if ( ! deleteFilesOnlyFlag )
                {
                if ( deleteReadonlyFlag )
                    {
                    fpath.toFile().setWritable( true );
                    if ( fsType == Constants.FILESYSTEM_DOS )
                        {
                        Files.setAttribute( fpath, "dos:readonly", false );
                        }        
                    }
//                if ( deleteToTrashFlag )
//                    {
//                    Path trashparent = trashFolder.resolve( fromPath.relativize( fpath ) ).getParent();
//                    //System.out.println( "trashparent =" + trashparent );
//                    trashparent.toFile().mkdirs();
//                    Files.copy( fpath, trashFolder.resolve( fromPath.relativize( fpath ) ), StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS );
//                    }
                System.out.println( "del fpath folder =" + fpath );
                Files.delete( fpath );
                numFoldersDeleted++;
                }
            //System.out.println( "would delete folder =" + dir );
            return FileVisitResult.CONTINUE;
            }
        catch (Exception ex2) 
            {
            processStatus = "Error";
            message = ex2 + ": " + fpath;
            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex2 );
            System.out.println( "postVisitDirectory() delete folder ERROR  " + "  " + ex2.getClass().getSimpleName() + ": " + fpath );
            ex2.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
        //return FileVisitResult.CONTINUE;
        }
    
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        System.out.println( "Tested:  " + numTested );
        System.out.println( "Deleted Files count: " + numFilesDeleted );
        System.out.println( "Deleted Folders count: " + numFoldersDeleted );

//            for ( Path mpath : matchedPathsList )
//                {
//                System.out.println( mpath );
//                }
        }
    
    public long getNumTested()
        {
        return numTested;
        }

    public long getNumFilesDeleted()
        {
        return numFilesDeleted;
        }

    public long getNumFoldersDeleted() {
        return numFoldersDeleted;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }
        
}
