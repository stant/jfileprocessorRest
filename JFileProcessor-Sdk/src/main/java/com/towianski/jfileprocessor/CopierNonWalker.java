/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.sshutils.Sftp;
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Stan Towianski
 */
public class CopierNonWalker extends SimpleFileVisitor<Path> 
{
    //private static final Logger logger = Logger.getLogger( MyLogger.class.getName() );
    private static final MyLogger logger = MyLogger.getLogger(CopierNonWalker.class.getName() );  // because this is just used by the copyFrame

    private Boolean isDoingCutFlag = false;
    private Path startingPath;
    private Path toPath;
    private Path fromPath;
//    private CopyOption[] copyOptions = null;
    private ArrayList<CopyOption> copyOptions = new ArrayList<CopyOption>();
    private long numFileMatches = 0;
    private long numFolderMatches = 0;
    private long numFileTests = 0;
    private long numFolderTests = 0;
    private long numTested = 0;

    boolean cancelFlag = false;
    boolean cancelFillFlag = false;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    String processStatus = "";
    String message = "";
    String toPathFileSeparator = "";
    HashMap<Path,Path> renameDirHm = new HashMap<Path,Path>();
    CopyFrameSwingWorker swingWorker = null;
    ConnUserInfo connUserInfo = null;
    Sftp sftp = null;
    com.jcraft.jsch.ChannelSftp chanSftp = null;
    com.jcraft.jsch.ChannelSftp chanSftpSrc = null;
    JschSftpUtils jschSftpUtils = new JschSftpUtils();
    Session jschSession = null;
    Path targetPath = null;
    String targetPathStr = null;

//      static {
////       logger.setLevel(Level.INFO);
//        System.out.println( "\nCopier: list of log handlers" );
//       for (Handler handler : logger.getHandlers()) {
////           handler.setLevel(Level.INFO);
//            System.out.println( "handler =" + handler );
//       }           
//   }
      
    public CopierNonWalker( ConnUserInfo connUserInfo, Sftp sftp, Sftp sftpSrc, Boolean isDoingCutFlag, ArrayList<CopyOption> copyOptions, CopyFrameSwingWorker swingWorker )
    {
        this.connUserInfo = connUserInfo;
        this.sftp = sftp;
        this.sftp = sftpSrc;
        this.isDoingCutFlag = isDoingCutFlag;
        this.copyOptions = copyOptions;
        this.swingWorker = swingWorker;
        System.out.println("Copier this.startingPath (startingPath) =" + this.startingPath + "   this.toPath =" + this.toPath + "=" );
        System.out.println( "isDoingCutFlag =" + isDoingCutFlag );
        chanSftp = sftp.getChanSftp();
        chanSftpSrc = sftpSrc.getChanSftp();
        cancelFlag = false;
        
//        jschSession = jschSftpUtils.createSession( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
//            if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
//                 connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
//                {
//                }
//            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
//                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
//                {
//                }

//        System.out.println( "Copier jFileFinderWin.getLogLevel() =" + jFileFinderWin.getLogLevel() + "=" );
//        logger.setLevel( jFileFinderWin.getLogLevel() );
        System.out.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
//        System.out.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );        
//        logger.clearLog();
    }

    public void setPaths( Path fromPath, String startingPath, String toPath, ConnUserInfo connUserInfo ) {
        this.fromPath = fromPath;
        System.out.println( "called set fromPath =" + this.fromPath + "=" );
        
        this.startingPath = Paths.get( startingPath );
        this.toPath = Paths.get( toPath );
        System.out.println( "Copier new File( toPath ).toURI() =" + new File( toPath ).toURI() + "=" );
        toPathFileSeparator = this.toPath.getFileSystem().getSeparator();

        System.out.println( "this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
        System.out.println( "this.toPath =" + this.toPath + "=" );
        Path fromPathOrig = this.fromPath;
        Path fromParent = fromPath.getParent();
        String ans = "";

        System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() + "=" );
        targetPath = this.toPath.resolve( this.startingPath.relativize( fromPath ) );
        System.out.println( "relativize =" + this.startingPath.relativize( fromPath ) + "=" );
        System.out.println( "toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

        targetPathStr = targetPath.toString();
        if ( connUserInfo.getFromFilesysType() != connUserInfo.getToFilesysType() )
            {
            if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
                 connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
                {
                if ( connUserInfo.getToFilesysType() == Constants.FILESYSTEM_POSIX )
                    {
                    targetPathStr = targetPath.toString().replace( "\\", "/" );
                    System.out.println( "posix targetPathStr =" + targetPathStr + "=" );
                    }
                else if ( connUserInfo.getToFilesysType() == Constants.FILESYSTEM_DOS )
                    {
                    targetPathStr = targetPath.toString().replace( "/", "\\" );
                    System.out.println( "dos targetPathStr =" + targetPathStr + "=" );
                    }
                }
            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
                {
                    // I would want to change sourceStr and since I am looking at target here it does not need to be changed.
                System.out.println( "targetPathStr from local path =" + targetPathStr + "=" );
//                if ( connUserInfo.getFromFilesysType() == Constants.FILESYSTEM_POSIX )
//                    {
//                    targetPathStr = targetPath.toString().replace( "\\", "/" );
//                    System.out.println( "posix targetPathStr =" + targetPathStr + "=" );
//                    }
//                else if ( connUserInfo.getFromFilesysType() == Constants.FILESYSTEM_DOS )
//                    {
//                    targetPathStr = targetPath.toString().replace( "/", "\\" );
//                    System.out.println( "dos targetPathStr =" + targetPathStr + "=" );
//                    }
                }
            else
                {
                System.out.println( "Error: not sftp to local or the reverse !" );
                return;
                }
            }
        
        // FIXXX when doing remote !
        if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
             connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
            {
            try {
                while ( fromPath.toFile().isDirectory() &&
                        this.toPath.toRealPath().equals( this.startingPath.toRealPath() ) )
                    {
                    if ( ! this.startingPath.equals( this.toPath ) )
                        {
                        ans = JOptionPane.showInputDialog( "Folder exists (probably through a symbolic link). New name: ", fromPath.getFileName() );
                        }
                    else
                        {
                        ans = JOptionPane.showInputDialog( "Folder exists. New name: ", fromPath.getFileName() );
                        }
                    if ( ans == null )
                        {
                        cancelFlag = true;
                        break;
                        }
                    this.toPath = Paths.get( fromParent + toPathFileSeparator + ans );
                    this.startingPath = fromPath;
                    System.out.println( "new this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
                    System.out.println( "new this.toPath =" + this.toPath + "=" );
                    }

                if ( this.toPath.toRealPath().startsWith( this.fromPath.toRealPath() ) )
                    {
                    JOptionPane.showMessageDialog( null, "You cannot copy a parent into a child folder." );
                    cancelFlag = true;
                    }
                } 
            catch (IOException ex) 
                {
                Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        }

    @Override
    public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
            throws IOException 
        {
        Path targetPath = this.toPath.resolve( this.startingPath.relativize( dir ) );
//        System.out.println( );
//        System.out.println( "preVisitDir relativize =" + this.startingPath.relativize( dir ) + "=" );
//        System.out.println( "preVisitDir toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

        numTested++;
        numFolderTests ++;
        numFolderMatches++;
        if ( cancelFlag )
            {
            System.out.println( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        if ( ! Files.exists( targetPath ) )
            {
//            System.out.println( "preVisitDir would do Files.createDirectory( " + targetPath + ")" );
            Files.createDirectory( targetPath );
            }
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) 
            throws IOException 
        {
        try {
        Path targetPath = toPath.resolve( startingPath.relativize( file ) );
//        System.out.println( );
//        System.out.println("preVisit startingPath =" + startingPath + "   file =" + file + "=" );
//        System.out.println("preVisit relativize =" + startingPath.relativize( file ) + "=" );
//        System.out.println( "preVisit toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );
//        System.out.println( "VisitFile copy file =" + file + "=    toPath.resolve( startingPath.relativize( file ) ) =" + toPath.resolve( startingPath.relativize( file ) ) + "=" );
        logger.log(Level.FINEST, "VisitFile copy file =" + file + "=    toPath.resolve( startingPath.relativize( file ) ) =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
        if ( cancelFlag )
            {
            System.out.println( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        numFileTests ++;
        swingWorker.publish2( numTested );
        
//                    this.toPath.toRealPath().toString().startsWith( this.startingPath.toRealPath().toString() ) )
//                    if ( this.toPath.toRealPath().toString().startsWith( this.startingPath.toRealPath().toString() ) )
//                        {
//                        ans = JOptionPane.showInputDialog( "Cannot copy a parent folder onto a child. New name: ", fromPath.getFileName() );
//                        }
//                    else
//                        {
//                        }
        
        Path toPathFile = toPath.resolve( startingPath.relativize( file ) );
        while ( file.compareTo( toPathFile ) == 0 )
            {
            System.out.println( "would Copy to Itself." );
            Path beforeFile = file;
            String ans = JOptionPane.showInputDialog( "Copy file onto itself. New name: ", file.getFileName() );
            if ( ans == null )
                {
                return FileVisitResult.CONTINUE;
                }
            toPathFile = Paths.get( toPathFile.getParent() + toPathFileSeparator + ans );
            this.startingPath = fromPath;
            System.out.println( "beforeFile =" + beforeFile + "=" );
            System.out.println( "new file =" + toPathFile + "=" );
            }

//            CopyOption[] copyOpts = new CopyOption[3];
//            //copyOpts[0] = StandardCopyOption.REPLACE_EXISTING;
//            copyOpts[1] = StandardCopyOption.COPY_ATTRIBUTES;
//            //copyOpts[2] = LinkOption.NOFOLLOW_LINKS;
            if ( copyOptions == null || copyOptions.size() < 1 )
                {
//                System.out.println("copy with default options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                Files.copy( file, toPathFile );
                }
            else
                {
//                System.out.println("copy with sent options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                Files.copy( file, toPathFile, copyOptions.toArray( new CopyOption[ copyOptions.size() ] ) );
                }
            }
        catch ( java.nio.file.NoSuchFileException noSuchFileExc ) 
            {
            logger.log( Level.INFO, "CAUGHT ERROR  " + noSuchFileExc.getClass().getSimpleName() + ": " + file );
            logger.log( Level.INFO, logger.getExceptionAsString( noSuchFileExc ) );
            return FileVisitResult.CONTINUE;
            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            logger.log( Level.INFO, "CAUGHT WARNING  " + exAccessDenied.getClass().getSimpleName() + ": " + file );
            logger.log( Level.INFO, logger.getExceptionAsString( exAccessDenied ) );
            swingWorker.setCloseWhenDoneFlag( false );
            return FileVisitResult.CONTINUE;
            }
        catch ( java.nio.file.FileAlreadyExistsException faeExc )
            {
            logger.log( Level.INFO, "CAUGHT ERROR  " + faeExc.getClass().getSimpleName() + ": " + file );
            System.out.println( "CAUGHT ERROR  " + faeExc.getClass().getSimpleName() + ": " + file );
            message = "ERROR: " + faeExc.getClass().getSimpleName() + ": " + file;
            return FileVisitResult.TERMINATE;
            }
        catch ( Exception exc )
            {
            logger.log(Level.SEVERE, "CAUGHT ERROR  " + exc.getClass().getSimpleName() + ": " + file );
            System.out.println( "CAUGHT ERROR  " + exc.getClass().getSimpleName() + ": " + file );
            processStatus = "Error";
            message = exc.getClass().getSimpleName() + ": " + file;
            return FileVisitResult.TERMINATE;
            }
    
        if ( isDoingCutFlag )
            {
            Files.delete( file );
            //System.out.println( "would delete file =" + file );
            }

        
        numFileMatches++;
        return FileVisitResult.CONTINUE;        
        }
    /**
     * This function recursively copy all the sub folder and files from sourceFolder to destinationFolder
     * */

    public void copyRecursive( String sourceFolder ) throws IOException
        {
        System.out.println( "copyRecursive connUserInfo =" + connUserInfo + "=" );
        System.out.println( "copyRecursive sourceFolder =" + sourceFolder + "=   to targetPathStr =" + targetPathStr + "=" );
        if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
             connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
            {
            copyRecursiveLocalToSftp( sourceFolder, targetPathStr );
            }
        else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
                  connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
            {
            copyRecursiveSftpToLocal( sourceFolder, targetPathStr );
            }
        else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
                  connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
            {
            copyRecursiveSftpToSftp( sourceFolder, targetPathStr );
            }
        else
            {
            System.out.println( "Error: not sftp to local or the reverse !" );
            return;
            }
        }
    
    public void copyRecursiveLocalToSftp( String sourceFolderStr, String destinationFolderStr ) throws IOException
    {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        System.out.println( "copyRecursiveLocalToSftp sourceFolderStr =" + sourceFolderStr + "=   to destinationFolderStr =" + destinationFolderStr + "=" );
        numTested++;
//        String sourceFolderStr = sourceFolder.toString();
//        String destinationFolderStr = destinationFolder.toString();
        File sourceFolder = new File( sourceFolderStr );
        File destinationFolder = new File( destinationFolderStr );
        
        if ( connUserInfo.getFromFilesysType() != connUserInfo.getToFilesysType() )
            {
            if ( connUserInfo.getToFilesysType() == Constants.FILESYSTEM_DOS )
                {
                destinationFolderStr = destinationFolderStr.replace( "/", "\\" );
                }
            else
                {
                destinationFolderStr = destinationFolderStr.replace( "\\", "/" );
                }
            }
        System.out.println( "copyRecursiveLocalToSftp /or\\ sourceFolderStr =" + sourceFolderStr + "=   to destinationFolderStr =" + destinationFolderStr + "=" );
        
        if ( sourceFolder.isDirectory() )
            {
            System.out.println( "Is Directory" );
            numFolderTests ++;
            //Verify if destinationFolder is already present; If not then create it
//            if (!destinationFolder.exists())
//                {
//                //                destinationFolder.mkdir();
////                chanSftp.mkdir( destinationFolder.toString() );
//                System.out.println("Directory created :: " + destinationFolder);
//                }
            boolean doMkdir = false;
            try {
                chanSftp.stat( destinationFolderStr );
                } 
            catch (SftpException ex)
                {
                doMkdir = true;
                Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                }
            catch (Exception exc)
                {
                System.out.println( "Exception" );
                doMkdir = true;
                exc.printStackTrace();
                }
            if ( doMkdir )
                {
                try
                    {
                    //                destinationFolder.mkdir();
                    chanSftp.mkdir( destinationFolderStr );
                    System.out.println( "mkdir =" + destinationFolderStr + "=" );
                    } 
                catch (SftpException ex)
                    {
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    processStatus = "Error";
                    message = ex.getClass().getSimpleName() + ": " + destinationFolderStr;
                    }
                System.out.println("Directory created :: " + destinationFolderStr );
                }
             
            //Get all files from source directory
            String files[] = sourceFolder.list();
             
            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files)
                {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);
                 
                //Recursive function call
                copyRecursiveLocalToSftp( srcFile.toString(), destFile.toString() );
                }
            postVisitDirectory( Paths.get( sourceFolderStr ) );  // delete folder if doing cut
            numFolderMatches++;
            }
        else
            {
            System.out.println( "Is File" );
            numFileTests ++;
            //Copy the file content from one place to another
//            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            try {
                //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
                System.out.println( "SftpPut locFile =" + sourceFolder + "=   to rmtFile =" + destinationFolderStr + "=" );
                
//                System.out.println( "copyOptions contains? StandardCopyOption.REPLACE_EXISTING =" + copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) + "=" );
                    
                if ( sftp.exists( destinationFolderStr ) )
                    {
                    if ( copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) )
                        {
                        chanSftp.put( sourceFolderStr, destinationFolderStr );
                        System.out.println( "File replaced :: " + destinationFolderStr );
                        numFileMatches++;
                        }
                    else
                        {
                        System.out.println( "File Not replaced/copied :: " + destinationFolderStr );
                        message = "File Not replaced/copied: " + destinationFolderStr;
                        }
                    }
                else
                    {
                    chanSftp.put( sourceFolderStr, destinationFolderStr );
                    System.out.println( "File copied :: " + destinationFolderStr );
                    numFileMatches++;
                    }
//                jschSftpUtils.copyTo()  does not work with spaces in names off-hand.
//                jschSftpUtils.copyTo( jschSession, sourceFolderStr, destinationFolderStr );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag )
                    {
                    Files.delete( Paths.get( sourceFolderStr ) );
                    System.out.println( "would delete local file =" + sourceFolderStr );
                    }
                } 
            catch (Exception ex) 
                {
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
                processStatus = "Error";
                message = ex.getClass().getSimpleName() + ": " + destinationFolderStr;
                }
            }
        swingWorker.publish2( numTested );
    }
    
    public void copyRecursiveSftpToLocal( String sourceFolderStr, String destinationFolderStr ) throws IOException
        {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        numTested++;
        
        System.out.println( "copyRecursiveSftpToLocal() srcFile =" + sourceFolderStr + "=   destFile =" + destinationFolderStr + "=" );

//        String sourceFolderStr = sourceFolder.toString();
//        String destinationFolderStr = destinationFolder.toString();
        File sourceFolder = new File( sourceFolderStr );
        File destinationFolder = new File( destinationFolderStr );
        
        if ( sourceFolderStr.endsWith( "." ) || sourceFolderStr.endsWith( ".." ) )
            {
            return;
            }
        
        if ( connUserInfo.getFromFilesysType() != connUserInfo.getToFilesysType() )
            {
            if ( connUserInfo.getFromFilesysType() == Constants.FILESYSTEM_DOS )
                {
                sourceFolderStr = sourceFolderStr.replace( "/", "\\" );
                }
            else
                {
                sourceFolderStr = sourceFolderStr.replace( "\\", "/" );
                }
            }
        System.out.println( "copyRecursiveSftpToLocal() /or\\ srcFile =" + sourceFolderStr + "=   destFile =" + destinationFolderStr + "=" );
        
        SftpATTRS sourceSftpAttrs = null;
        try {
            sourceSftpAttrs = chanSftp.stat( sourceFolderStr );
            } 
        catch (SftpException ex)
            {
            Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
            }
        if ( sourceSftpAttrs != null && sourceSftpAttrs.isDir() )
            {
            System.out.println( "copyRecursiveSftpToLocal() - Is Directory" );
            numFolderTests ++;
            //Verify if destinationFolder is already present; If not then create it
            if ( ! destinationFolder.exists() )
                {
                destinationFolder.mkdir();
                System.out.println( "Directory created :: " + destinationFolder );
                }
             
            //Get all files from source directory
            Vector vecfiles;
            ArrayList<ChannelSftp.LsEntry> files = new ArrayList<ChannelSftp.LsEntry>();
            try {
                vecfiles = chanSftp.ls( sourceFolderStr );
                for ( Object obj : vecfiles )
                    {
                    if (obj instanceof ChannelSftp.LsEntry) {
                        files.add( (ChannelSftp.LsEntry) obj );
                        }
                    }
                } 
            catch (SftpException ex) 
                {
                Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                }
             
            //Iterate over all files and copy them to destinationFolder one by one
            for ( ChannelSftp.LsEntry lsEntry : files )
                {
                File srcFile = new File( sourceFolder, lsEntry.getFilename() );
                File destFile = new File( destinationFolder, lsEntry.getFilename() );
                System.out.println( "copyRecursiveSftpToLocal srcFile =" + srcFile + "=   destFile =" + destFile + "=" );

                //Recursive function call
                copyRecursiveSftpToLocal( srcFile.toString(), destFile.toString() );
                }
            
            if ( isDoingCutFlag )
                {
                try
                    {
                    chanSftp.rmdir( sourceFolderStr );
                    System.out.println( "would delete sftp folder =" + sourceFolderStr );
                    //numFoldersDeleted++;
                    } 
                catch (SftpException ex)
                    {
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            numFolderMatches++;
            }
        else
            {
            System.out.println( "copyRecursiveSftpToLocal() - Is File" );
            numFileTests ++;
            //Copy the file content from one place to another
//            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            try {
                //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
                System.out.println( "SftpGet locFile =" + sourceFolder + "=   to rmtFile =" + destinationFolderStr + "=" );
                if ( Files.exists( Paths.get( destinationFolderStr ) ) )
                    {
                    if ( copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) )
                        {
                        chanSftp.get( sourceFolderStr, destinationFolderStr );
                        numFileMatches++;
                        System.out.println( "File replaced :: " + destinationFolderStr );
                        }
                    else
                        {
                        System.out.println( "File Not replaced/copied :: " + destinationFolderStr );
                        message = "File Not replaced/copied: " + destinationFolderStr;
                        }
                    }
                else
                    {
                    chanSftp.get( sourceFolderStr, destinationFolderStr );
                    numFileMatches++;
                    System.out.println( "File copied :: " + destinationFolderStr );
                    }
//                jschSftpUtils.copyTo()  does not work with spaces in names off-hand.
//                jschSftpUtils.copyTo( jschSession, sourceFolderStr, destinationFolderStr );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag )
                    {
//                    Files.delete( Paths.get( sourceFolderStr ) );
                    chanSftp.rm( sourceFolderStr );
                    System.out.println( "would delete file =" + sourceFolderStr );
                    }
                } 
            catch (Exception ex) 
                {
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println( "Error for sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" );
                message = "sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=";
                }
            }
        swingWorker.publish2( numTested );
    }
    
    public void copyRecursiveSftpToSftp( String sourceFolderStr, String destinationFolderStr ) throws IOException
        {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        numTested++;
        
        System.out.println( "copyRecursiveSftpToSftp() srcFile =" + sourceFolderStr + "=   destFile =" + destinationFolderStr + "=" );

//        String sourceFolderStr = sourceFolder.toString();
//        String destinationFolderStr = destinationFolder.toString();
        File sourceFolder = new File( sourceFolderStr );
        File destinationFolder = new File( destinationFolderStr );
        
        if ( sourceFolderStr.endsWith( "." ) || sourceFolderStr.endsWith( ".." ) )
            {
            return;
            }
        
        if ( connUserInfo.getFromFilesysType() != connUserInfo.getToFilesysType() )
            {
            if ( connUserInfo.getFromFilesysType() == Constants.FILESYSTEM_DOS )
                {
                sourceFolderStr = sourceFolderStr.replace( "/", "\\" );
                }
            else
                {
                sourceFolderStr = sourceFolderStr.replace( "\\", "/" );
                }

            if ( connUserInfo.getToFilesysType() == Constants.FILESYSTEM_DOS )
                {
                destinationFolderStr = destinationFolderStr.replace( "/", "\\" );
                }
            else
                {
                destinationFolderStr = destinationFolderStr.replace( "\\", "/" );
                }
            }
        System.out.println( "copyRecursiveSftpToSftp() /or\\ srcFile =" + sourceFolderStr + "=   destFile =" + destinationFolderStr + "=" );
        
        SftpATTRS sourceSftpAttrs = null;
        try {
            sourceSftpAttrs = chanSftpSrc.stat( sourceFolderStr );
            } 
        catch (SftpException ex)
            {
            Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
            }
        if ( sourceSftpAttrs != null && sourceSftpAttrs.isDir() )
            {
            System.out.println( "copyRecursiveSftpToSftp() - Is Directory" );
            numFolderTests ++;
            //Verify if destinationFolder is already present; If not then create it
            boolean doMkdir = false;
            try {
                chanSftp.stat( destinationFolderStr );
                } 
            catch (SftpException ex)
                {
                doMkdir = true;
                Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                }
            catch (Exception exc)
                {
                System.out.println( "Exception" );
                doMkdir = true;
                exc.printStackTrace();
                }
            if ( doMkdir )
                {
                try
                    {
                    //                destinationFolder.mkdir();
                    chanSftp.mkdir( destinationFolderStr );
                    System.out.println( "mkdir =" + destinationFolderStr + "=" );
                    } 
                catch (SftpException ex)
                    {
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    processStatus = "Error";
                    message = ex.getClass().getSimpleName() + ": " + destinationFolderStr;
                    }
                System.out.println("Directory created :: " + destinationFolderStr );
                }
             
            //Get all files from source directory
            Vector vecfiles;
            ArrayList<ChannelSftp.LsEntry> files = new ArrayList<ChannelSftp.LsEntry>();
            try {
                vecfiles = chanSftpSrc.ls( sourceFolderStr );
                for ( Object obj : vecfiles )
                    {
                    if (obj instanceof ChannelSftp.LsEntry) {
                        files.add( (ChannelSftp.LsEntry) obj );
                        }
                    }
                } 
            catch (SftpException ex) 
                {
                Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                }
             
            //Iterate over all files and copy them to destinationFolder one by one
            for ( ChannelSftp.LsEntry lsEntry : files )
                {
                File srcFile = new File( sourceFolder, lsEntry.getFilename() );
                File destFile = new File( destinationFolder, lsEntry.getFilename() );
                System.out.println( "copyRecursiveSftpToSftp srcFile =" + srcFile + "=   destFile =" + destFile + "=" );

                //Recursive function call
                copyRecursiveSftpToSftp( srcFile.toString(), destFile.toString() );
                }
            
            if ( isDoingCutFlag )
                {
                try
                    {
                    chanSftpSrc.rmdir( sourceFolderStr );
                    System.out.println( "would delete sftp folder =" + sourceFolderStr );
                    //numFoldersDeleted++;
                    } 
                catch (SftpException ex)
                    {
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            numFolderMatches++;
            }
        else
            {
            System.out.println( "copyRecursiveSftpToSftp() - Is File" );
            numFileTests ++;
            //Copy the file content from one place to another
//            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            try {
                //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
                System.out.println( "SftpGet locFile =" + sourceFolder + "=   to rmtFile =" + destinationFolderStr + "=" );
                if ( Files.exists( Paths.get( destinationFolderStr ) ) )
                    {
                    if ( copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) )
                        {
//                        chanSftp.get( sourceFolderStr, destinationFolderStr );
                        InputStream inputStream = chanSftpSrc.get( sourceFolderStr );
                        chanSftp.put( inputStream, destinationFolderStr );
                        numFileMatches++;
                        System.out.println( "File replaced :: " + destinationFolderStr );
                        }
                    else
                        {
                        System.out.println( "File Not replaced/copied :: " + destinationFolderStr );
                        message = "File Not replaced/copied: " + destinationFolderStr;
                        }
                    }
                else
                    {
//                    chanSftp.get( sourceFolderStr, destinationFolderStr );
                    InputStream inputStream = chanSftpSrc.get( sourceFolderStr );
//                       File f = new File(file.getName());
                    chanSftp.put( inputStream, destinationFolderStr );
                    numFileMatches++;
                    System.out.println( "File copied :: " + destinationFolderStr );
                    }
//                jschSftpUtils.copyTo()  does not work with spaces in names off-hand.
//                jschSftpUtils.copyTo( jschSession, sourceFolderStr, destinationFolderStr );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag )
                    {
//                    Files.delete( Paths.get( sourceFolderStr ) );
                    chanSftpSrc.rm( sourceFolderStr );
                    System.out.println( "would delete file =" + sourceFolderStr );
                    }
                } 
            catch (Exception ex) 
                {
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println( "Error for sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" );
                message = "sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=";
                }
            }
        //swingWorker.publish2( numTested );
    }

    public void postVisitDirectory(Path dir)
            throws IOException
        {
        try {
            //numTested++;
            if ( isDoingCutFlag )
                {
                Files.delete( dir );
                System.out.println( "would delete local folder =" + dir );
                //numFoldersDeleted++;
                }
            }
        //throw ex;
//        catch (RuntimeException ex3) 
//            {
//            Logger.getLogger(Deleter.class.getName()).log(Level.SEVERE, null, ex3 );
//        System.out.println( "CAUGHT RUNTIME ERROR  " + "my error msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            throw new IOException( "my runtime msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            }
        catch (Exception ex2) 
            {
            logger.log(Level.SEVERE, null, ex2 );
            //System.out.println( "CAUGHT ERROR  " + "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
            throw new IOException( ex2.getClass().getSimpleName() + ": " + dir );
            }
        //return FileVisitResult.TERMINATE;
        }
    
        
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        System.out.println( "Tested:  " + numTested );
        System.out.println( "Copied numFileMatches: " + numFileMatches );
        System.out.println( "Copied numFolderMatches: " + numFolderMatches );
        System.out.println( "Copied numFileTests: " + numFileTests );
        System.out.println( "Copied numFolderTests: " + numFolderTests );

        if ( numFileMatches != numFileTests  ||
             numFolderMatches != numFolderTests )
            {
            processStatus = CopyFrame.PROCESS_STATUS_COPY_INCOMPLETED;
            System.out.println( "processStatus =" + processStatus + "=  should be incomplete" );
            }

//            for ( Path mpath : matchedPathsList )
//                {
//                System.out.println( mpath );
//                }
        }
    
    public long getNumTested()
        {
        return numTested;
        }

    public long getNumFileMatches()
        {
        return numFileMatches;
        }

    public long getNumFolderMatches() {
        return numFolderMatches;
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

//    public static void main(String[] args) throws IOException
//    {
//        //Source directory which you want to copy to new location
//        File sourceFolder = new File("c:\\temp");
//         
//        //Target directory where files should be copied
//        File destinationFolder = new File("c:\\tempNew");
// 
//        //Call Copy function
//        copyFolder(sourceFolder, destinationFolder);
//    }
        
}
