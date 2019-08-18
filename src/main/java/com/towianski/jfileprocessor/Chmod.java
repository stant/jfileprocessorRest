/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.Constants;
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Stan Towianski
 */
public class Chmod
{
    private static final MyLogger logger = MyLogger.getLogger( Chmod.class.getName() );
    private Path fromPath;
    private long numFilesChmodd = 0;
    private long numFoldersChmodd = 0;
    private long numFilesTested = 0;
    private long numFoldersTested = 0;
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
    
    public Chmod( String startingPath, ArrayList<String> deletePaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag, int fsType, DeleteFrameSwingWorker swingWorker )
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
        
    public void setMods( Path fpath, String octPerms )
        {
        try {
            int owner = octPerms.charAt(0) - '0';
            int group = octPerms.charAt(1) - '0';
            int all = octPerms.charAt(2) - '0';
            boolean ownerRead = false;
            boolean ownerWrite = false;
            boolean ownerExec = false;
            boolean groupRead = false;
            boolean groupWrite = false;
            boolean groupExec = false;
            boolean allRead = false;
            boolean allWrite = false;
            boolean allExec = false;
            
                //add owners permission
                switch( owner )
                    {
                    case 4:  
                    case 5:  
                    case 6:  
                    case 7:  
                        ownerRead = true;
                        break;
                    }
                switch( owner )
                    {
                    case 2:  
                    case 3:  
                    case 6:  
                    case 7:  
                        ownerWrite = true;
                        break;
                    }
                switch( owner )
                    {
                    case 1:  
                    case 3:  
                    case 5:  
                    case 7:  
                        ownerExec = true;
                        break;
                    }
                
                //add group permissions
                switch( group )
                    {
                    case 4:  
                    case 5:  
                    case 6:  
                    case 7:  
                        groupRead = true;
                        break;
                    }
                switch( group )
                    {
                    case 2:  
                    case 3:  
                    case 6:  
                    case 7:  
                        groupWrite = true;
                        break;
                    }
                switch( group )
                    {
                    case 1:  
                    case 3:  
                    case 5:  
                    case 7:  
                        groupExec = true;
                        break;
                    }

                //add others permissions
                switch( all )
                    {
                    case 4:  
                    case 5:  
                    case 6:  
                    case 7:  
                        allRead = true;
                        break;
                    }
                switch( all )
                    {
                    case 2:  
                    case 3:  
                    case 6:  
                    case 7:  
                        allWrite = true;
                        break;
                    }
                switch( all )
                    {
                    case 1:  
                    case 3:  
                    case 5:  
                    case 7:  
                        allExec = true;
                        break;
                    }

                //   THIS DOS Stuff is not Done FIXXX
            if ( fsType == Constants.FILESYSTEM_DOS )
                {
                if ( ownerWrite || groupWrite || allWrite )
                    {
                    fpath.toFile().setWritable( true );
                    Files.setAttribute( fpath, "dos:readonly", false );
                    }
                }
            else
                {
                //using PosixFilePermission to set file permissions 777
                Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();

                if ( ownerRead )
                    perms.add(PosixFilePermission.OWNER_READ);
                if ( ownerWrite )
                        perms.add(PosixFilePermission.OWNER_WRITE);
                if ( ownerExec )
                        perms.add(PosixFilePermission.OWNER_EXECUTE);

                if ( groupRead )
                    perms.add(PosixFilePermission.GROUP_READ);
                if ( groupWrite )
                    perms.add(PosixFilePermission.GROUP_WRITE);
                if ( groupExec )
                    perms.add(PosixFilePermission.GROUP_EXECUTE);

                if ( allRead )
                    perms.add(PosixFilePermission.OTHERS_READ);
                if ( allWrite )
                    perms.add(PosixFilePermission.OTHERS_WRITE);
                if ( allExec )
                    perms.add(PosixFilePermission.OTHERS_EXECUTE);

                Files.setPosixFilePermissions( fpath, perms );
                
//Files.createDirectory(Paths.get("/the/path"), 
//      PosixFilePermissions.asFileAttribute(      
//         PosixFilePermissions.fromString("rwxr-x---")
//      );                
                }
            }
        catch (IOException ex) {
            logger.severeExc( ex );
        }
    }

    public void chmodRecursive( String filename, String octPerms ) throws IOException
        {
        chmodRecursive( new File( filename ), octPerms );
        }
    
    public void chmodRecursive( File filename, String octPerms ) throws IOException
        {
//        logger.info( "entered chmodRecursive()" );
        logger.info( "ck to chmod =" + filename + "=" );
        if ( filename.toString().endsWith( "." ) || filename.toString().endsWith( ".." ) )
            {
            return;
            }
        if ( swingWorker != null )  swingWorker.publish2( numTested );

        setMods( Paths.get( filename.toString() ), octPerms );
        numFilesChmodd++;
        
        try {
            File[] fileArr = filename.listFiles();
            if ( fileArr != null )
                {
                numFoldersChmodd++;
                for (File file : fileArr) 
                    {
                    chmodRecursive( file, octPerms );
                    }
                }
//            else
//                {
//                logger.info( "found no files in =" + filename + "=" );
//                }
            } 
        catch (Exception ex) 
            {
            logger.info( "chmod Done" );
            logger.severeExc( ex );
            ex.printStackTrace();
            return;   // done
            }
        }
    
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        logger.info( "Tested:  " + numTested );
        logger.info( "Chmodd Files count: " + numFilesChmodd );
        logger.info( "Chmodd Folders count: " + numFoldersChmodd );

//            for ( Path mpath : matchedPathsList )
//                {
//                logger.info( mpath );
//                }
        }
    
    public long getNumTested()
        {
        return numTested;
        }

    public long getNumFilesChmodd()
        {
        return numFilesChmodd;
        }

    public long getNumFoldersChmodd() {
        return numFoldersChmodd;
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
