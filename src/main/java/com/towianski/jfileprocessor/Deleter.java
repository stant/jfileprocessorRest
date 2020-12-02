/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.boot.GlobalMemory;
import com.towianski.models.Constants;
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.MyLogger;
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
import javax.swing.JOptionPane;

/**
 *
 * @author Stan Towianski
 */
public class Deleter extends SimpleFileVisitor<Path>
{
    private static final MyLogger logger = MyLogger.getLogger( Deleter.class.getName() );
    
//    @Autowired
//    ServerUserFileRightsList serverUserFileRightsList;
//
//    @Autowired
//    private CustomPermissionEvaluator customPermissionEvaluatorNormBean;
        
    private Path fromPath;
    private long numFilesDeleted = 0;
    private long numFoldersDeleted = 0;
    private long numFileTests = 0;
    private long numFolderTests = 0;  // added but not set up to use yet like in copier which has a previsit
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

    public Deleter()
        {
        }
        
    public Deleter( String startingPath, ArrayList<String> deletePaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag, int fsType, DeleteFrameSwingWorker swingWorker )
    {
        this.fromPath = Paths.get( startingPath );
        this.deletePaths = deletePaths;
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
        
//    @Override
//    public FileVisitResult visitFileFailed( Path file, IOException exc )
//            throws IOException 
//        {
//        if ( exc instanceof AccessDeniedException ) 
//            {
////            return FileVisitResult.SKIP_SUBTREE;
//            try {
//                logger.info( "Delete visitFileFailed 0" );
////                setMods( file );
//                commonVisitFile( file );
//                return FileVisitResult.CONTINUE;
//                }
//            catch ( Exception ex ) 
//                {
//                logger.info( "Delete visitFileFailed Error: " + ex );
//                processStatus = "Error";
//                message = file + ": " + ex;
//                return FileVisitResult.TERMINATE;
//                }
//            }
//
//        logger.info( "Delete visitFileFailed OTHER Error: " + exc );
//        return super.visitFileFailed(file, exc);
//    }

    // just put in late just to check rights of folder when doing https
    @Override
    public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
            throws IOException 
        {
        logger.info( "visitFile dir =" + dir );
        try {
            if ( dir.toFile().isDirectory() )
                {
                logger.finest( "check dir rights for =" + dir );
                if ( ! GlobalMemory.getSecUtils().hasPermission( dir, "w" ) )
                    {
                    logger.info( "Do not have Write permission on folder so skip the whole folder =" + dir );
                    errorList.add( dir + " -> " + "ERROR " + "Do not have Write permission on folder" );
                    return FileVisitResult.SKIP_SUBTREE;
                    //throw new Exception( "https: \"" + "\" does not have folder permissions" );
                    }

                return FileVisitResult.CONTINUE;
                }
            }
//        catch ( java.nio.file.NoSuchFileException noSuchFileExc ) 
//            {
//            logger.info( "ERROR  " + noSuchFileExc + ": " + dir );
//            logger.info( logger.getExceptionAsString( noSuchFileExc ) );
//            errorList.add( dir + " -> " + "ERROR " + noSuchFileExc );
//            //return FileVisitResult.TERMINATE;
//            }
//        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
//            {
//            logger.info( "WARNING  " + exAccessDenied + ": " + dir );
//            logger.info( logger.getExceptionAsString( exAccessDenied ) );
//            errorList.add( dir + " -> " + "WARNING " + exAccessDenied );
//            if ( swingWorker != null )  swingWorker.setCloseWhenDoneFlag( false );
//            //return FileVisitResult.TERMINATE;
//            }
//        catch ( java.nio.file.FileAlreadyExistsException faeExc )
//            {
//            logger.info( "ERROR  " + faeExc + ": " + dir );
//            logger.info( logger.getExceptionAsString( faeExc ) );
//            errorList.add( dir + " -> " + "ERROR " + faeExc );
//            message = "ERROR: " + faeExc + ": " + dir;
//            //return FileVisitResult.CONTINUE;
//            }
//        catch ( java.nio.file.FileSystemException fsFileExc ) 
//            {
//            logger.info( "ERROR  " + fsFileExc + ": " + dir );
//            logger.info( logger.getExceptionAsString( fsFileExc ) );
//            errorList.add( dir + " -> " + "ERROR " + fsFileExc );
//            return FileVisitResult.CONTINUE;
//            }
        catch ( Exception exc )
            {
            logger.severe( "ERROR  " + exc + ": " + dir );
            logger.info( "ERROR  " + exc + ": " + dir );
            errorList.add( dir + " -> " + "ERROR " + exc );
            processStatus = "Error";
            message = exc + ": " + dir;
            //return FileVisitResult.TERMINATE;
            }
    
        return FileVisitResult.CONTINUE;
        }
    
    @Override
    public FileVisitResult visitFile( Path fpath, BasicFileAttributes attrs ) 
            throws IOException 
        {
        logger.info( "visitFile fpath =" + fpath );
        if ( cancelFlag )
            {
            logger.info( "Delete cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        numFileTests ++;
        try {
            if ( fpath.toFile().isDirectory() )
                {
                logger.info( "commonVisitFile fpath just return for folder =" + fpath );
                return FileVisitResult.CONTINUE;
                }

//            if ( deleteReadonlyFlag )   handled by calling chmod() before all deletes as timing does not work here !
//                {
//                setMods( fpath );
//                }
            if ( ! GlobalMemory.getSecUtils().hasPermission( fpath, "w" ) )
                {
                throw new Exception( "https: \"" + "\" does not have folder permissions" );
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
                logger.info( "visitFile() Files.copy trashFpath =" + trashFpath + "=" );
                Files.copy( fpath, trashFpath, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS );
                }
            logger.info( "del fpath =" + fpath );
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
            logger.severeExc( exAccessDenied );
            logger.info( "visitFile() AccessDeniedException: " + "  " + exAccessDenied.getClass().getSimpleName() + ": " + fpath );
            exAccessDenied.printStackTrace();
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
            
//            logger.severeExc( ex );
//            logger.info( "visitFile() CAUGHT ERROR  " + "  " + ex.getClass().getSimpleName() + ": " + fpath );
//            ex.printStackTrace();
//            return FileVisitResult.TERMINATE;
            
            logger.info( "WARNING  " + ex + ": " + fpath );
            logger.info( logger.getExceptionAsString( ex ) );
            errorList.add( fpath + " -> " + "WARNING " + ex );
//            if ( swingWorker != null )  swingWorker.setCloseWhenDoneFlag( false );
            return FileVisitResult.CONTINUE;
            }
        //logger.info( "would delete file =" + file );
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
            message = ex2 + ": " + fpath;
            logger.severeExc( ex );
            logger.info( "postVisitDirectory() delete folder ERROR  " + "  " + ex2.getClass().getSimpleName() + ": " + fpath );
            ex2.printStackTrace();
            return FileVisitResult.TERMINATE;
            }
        //return FileVisitResult.CONTINUE;
        }
    
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        logger.info( "Tested:  " + numTested );
        logger.info( "Deleted Files count: " + numFilesDeleted );
        logger.info( "Deleted Folders count: " + numFoldersDeleted );
        logger.info( "Deleted numFileTests: " + numFileTests );
        logger.info( "Deleted numFolderTests: " + numFolderTests );

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

    public long getNumFileTests() {
        return numFileTests;
    }

    public long getNumFolderTests() {
        return numFolderTests;
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

    public ArrayList<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(ArrayList<String> errorList) {
        this.errorList = errorList;
    }
        
}
