/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author Stan Towianski
 */
public class DeleterNonWalker extends SimpleFileVisitor<Path> 
{
    private static final MyLogger logger = MyLogger.getLogger( DeleterNonWalker.class.getName() );
    private Path fromPath;
    private long numFilesDeleted = 0;
    private long numFoldersDeleted = 0;
    private long numTested = 0;
    private long numFileTests = 0;
    private long numFolderTests = 0;

    boolean cancelFlag = false;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    Boolean deleteFilesOnlyFlag = false;
    Boolean deleteToTrashFlag = true;
    Boolean deleteReadonlyFlag = false;
    Path trashFolder = DesktopUtils.getTrashFolder().toPath();
    String processStatus = "";
    String message = "";
    DeleteFrameSwingWorker swingWorker = null;
    int fsType = -1;
    ConnUserInfo connUserInfo = null;
    com.jcraft.jsch.ChannelSftp chanSftp = null;
    JschSftpUtils jschSftpUtils = new JschSftpUtils();
    Session jschSession = null;
    Path targetPath = null;
    
    public DeleterNonWalker( ConnUserInfo connUserInfo, com.jcraft.jsch.ChannelSftp chanSftp, String startingPath, ArrayList<Path> copyPaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag, int fsType, DeleteFrameSwingWorker swingWorker )
        {
        this.connUserInfo = connUserInfo;
        this.chanSftp = chanSftp;
        this.fromPath = Paths.get( startingPath );
        this.copyPaths = copyPaths;
        this.deleteFilesOnlyFlag = deleteFilesOnlyFlag;
        this.deleteToTrashFlag = deleteToTrashFlag;
        this.deleteReadonlyFlag = deleteReadonlyFlag;
        this.fsType = fsType;
        this.swingWorker = swingWorker;
        logger.info( "Deleter this.fromPath =" + this.fromPath + "=" );
        cancelFlag = false;        
        }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    @Override
    public FileVisitResult visitFile( Path fpath, BasicFileAttributes attrs ) 
            throws IOException 
        {
        if ( cancelFlag )
            {
            logger.info( "Delete cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        try {
            if ( deleteReadonlyFlag )
                {
                fpath.toFile().setWritable( true );
                if ( fsType == Constants.FILESYSTEM_DOS )
                    {
                    Files.setAttribute( fpath, "dos:readonly", false );
                    }        
                }
            if ( deleteToTrashFlag )
                {
                Path trashFpath = trashFolder.resolve( fromPath.relativize( fpath ) );
                Path trashparent = trashFolder.resolve( fromPath.relativize( fpath ) ).getParent();
                //logger.info( "trashparent =" + trashparent );
                trashparent.toFile().mkdirs();
                if ( trashFpath.toFile().exists() )
                    {
                    trashFpath.toFile().setWritable( true );
                    if ( fsType == Constants.FILESYSTEM_DOS )
                        {
                        Files.setAttribute( trashFpath, "dos:readonly", false );
                        }        
                    }
                //logger.info( "visitFile() Files.copy trashFpath =" + trashFpath + "=" );
                Files.copy( fpath, trashFpath, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS );
                }
            //logger.info( "del fpath =" + fpath );
            Files.delete( fpath );
            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            processStatus = "Error";
            if ( fsType == Constants.FILESYSTEM_DOS && Files.isHidden( fpath ) )
                {
                JOptionPane.showMessageDialog( null, exAccessDenied.getClass().getSimpleName()
                                + "\nThis is because of a hidden file: "
                                + fpath
                                + "\nEither delete hidden files first, or check \"Delete Read-Only\" which will also delete hidden files."
                                , "Error", JOptionPane.ERROR_MESSAGE );
                message = "Handle hidden file first: " + fpath;
                }
            else if ( fsType == Constants.FILESYSTEM_POSIX )
                {
                JOptionPane.showMessageDialog( null, exAccessDenied.getClass().getSimpleName()
                                + "\nThis is because of a file: "
                                + fpath
                                + "\nPossibly you have no rwx rights to the folder, or no read rights on the file."
                                , "Error", JOptionPane.ERROR_MESSAGE );
                message = "Handle rights on file: " + fpath;
                }
            else
                {
                message = exAccessDenied.getClass().getSimpleName() + ": " + fpath;
                }
            logger.severeExc( exAccessDenied );
            logger.info( "visitFile() AccessDeniedException: " + "  " + exAccessDenied.getClass().getSimpleName() + ": " + fpath );
            if ( deleteToTrashFlag )
                {
                logger.info( "visitFile() copy toPath =" + trashFolder.resolve( fromPath.relativize( fpath ) ) + "=" );
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
            logger.severeExc( ex );
            logger.info( "visitFile() CAUGHT ERROR  " + "  " + ex.getClass().getSimpleName() + ": " + fpath );
            ex.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
        //logger.info( "would delete file =" + file );
        numFilesDeleted++;
        return FileVisitResult.CONTINUE;
        }

    /**
     * This function recursively deletes all the sub folder and files from sourceFolder to destinationFolder
     * */

    public void deleteRecursiveRemote( String filename ) throws IOException
        {
        logger.info( "entered deleteRecursive()" );
        logger.info( "ck to delete =" + filename + "=" );
        if ( filename.endsWith( "." ) || filename.endsWith( ".." ) )
            {
            return;
            }

        try {
            //        if ( ! sourceFolder.exists() )
            if ( chanSftp.stat( filename ).getSize() >= 0 )
            { } // continue if not error meaning exists
           } 
        catch (SftpException ex) {
            logger.info( "delete Done" );
            logger.severeExc( ex );
            ex.printStackTrace();
            return;   // done
        }
        
        boolean isDirFlag = false;
        //Check if sourceFolder is a directory or file
        numTested++;
        try {
//            if ( sourceFolder.isDirectory() )
            if ( chanSftp.stat( filename ).isDir() )
                {
                logger.info( "Is Directory" );
                isDirFlag = true;
             
                //Get all files from source directory
//                String files[] = sourceFolder.list();
                Vector vecfiles = chanSftp.ls( filename );
                ArrayList<ChannelSftp.LsEntry> files = new ArrayList<ChannelSftp.LsEntry>();
                for ( Object obj : vecfiles )
                    {
                    if (obj instanceof ChannelSftp.LsEntry) {
                        files.add( (ChannelSftp.LsEntry) obj );
                        }
                    }
                
                logger.info( "at 1" );

                //Iterate over all files and copy them to destinationFolder one by one
                for ( ChannelSftp.LsEntry lsEntry : files )
                    {
                    File srcFile = new File( filename, lsEntry.getFilename() );
                    logger.info( "SftpRm filename =" + filename + "=" );
                    logger.info( "SftpRm lsEntry.getFilename() =" + lsEntry.getFilename() + "=" );

                    //Recursive function call
                    deleteRecursiveRemote( srcFile.toString() );
                    }
                logger.info( "at 2" );
                }
            }
        catch (Exception exc)
            {
            logger.info( "Exception" );
            exc.printStackTrace();
            }

        
        logger.info( "SftpRm rmtFile =" + filename + "=" );
        try
            {
            logger.info( "at 3" );
            if ( isDirFlag )
                {
                logger.info( "Is Directory" );
                numFolderTests ++;
                if ( 1 == 1 )   chanSftp.rmdir( filename );
                logger.info( "Folder deleted :: " + filename );
                numFoldersDeleted ++;
                }
            else
                {
                logger.info( "Is File" );
                numFileTests ++;
                if ( 1 == 1 )   chanSftp.rm( filename );
                logger.info( "File deleted :: " + filename );
                numFilesDeleted ++;
                }
            } 
        catch (SftpException ex)
            {
            logger.info( "File delete Failed :: " + filename );
            logger.severeExc( ex );
            }
//            jschSftpUtils.copyTo( jschSession, filename, destinationFolder.toString() );
        
        swingWorker.publish2( numTested );
    }

    @Override
    public FileVisitResult postVisitDirectory(Path fpath, IOException ex) 
            throws IOException
        {
        try {
            numTested++;
            swingWorker.publish2( numTested );
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
//                    //logger.info( "trashparent =" + trashparent );
//                    trashparent.toFile().mkdirs();
//                    Files.copy( fpath, trashFolder.resolve( fromPath.relativize( fpath ) ), StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS );
//                    }
                logger.info( "del fpath folder =" + fpath );
                Files.delete( fpath );
                numFoldersDeleted++;
                }
            //logger.info( "would delete folder =" + dir );
            return FileVisitResult.CONTINUE;
            }
        catch (Exception ex2) 
            {
            processStatus = "Error";
            message = ex2.getClass().getSimpleName() + ": " + fpath;
            logger.severeExc( ex );
            logger.info( "postVisitDirectory() delete folder ERROR  " + "  " + ex2.getClass().getSimpleName() + ": " + fpath );
            ex2.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
        //return FileVisitResult.CONTINUE;
        //return FileVisitResult.CONTINUE;
        }
    
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        logger.info( "Tested:  " + numTested );
        logger.info( "Deleted Files count: " + numFilesDeleted );
        logger.info( "Deleted Folders count: " + numFoldersDeleted );

//            for ( Path mpath : matchedPathsList )
//                {
//                logger.info( mpath );
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
        
}
