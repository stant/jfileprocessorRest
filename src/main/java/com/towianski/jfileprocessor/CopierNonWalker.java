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
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
public class CopierNonWalker //extends SimpleFileVisitor<Path> 
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
    private ArrayList<String> errorList = new ArrayList<String>();

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
    Sftp sftpSrc = null;
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
        this.sftpSrc = sftpSrc;
        this.isDoingCutFlag = isDoingCutFlag;
        this.copyOptions = copyOptions;
        this.swingWorker = swingWorker;
        System.out.println("Copier this.startingPath (startingPath) =" + this.startingPath + "   this.toPath =" + this.toPath + "=" );
        System.out.println( "isDoingCutFlag =" + isDoingCutFlag );
        chanSftp = sftp.getChanSftp();
        if ( sftpSrc != null ) 
            {
            chanSftpSrc = sftpSrc.getChanSftp();
            }
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
        String ans = "";
        Path fromParent = null;
        
        try {
            this.fromPath = fromPath;
            System.out.println( "called set fromPath =" + this.fromPath + "=" );

            this.startingPath = Paths.get( startingPath );
            this.toPath = Paths.get( toPath );
    //        java.net.URI toPathUri = new File( toPath ).toURI();
    //        java.net.URI fromPathUri = fromPath.toFile().toURI();

            String startingPathStr = this.startingPath.toString().replace( "\\", "/" );
            String toPathStr = toPath.replace( "\\", "/" );
            String fromPathStr = fromPath.toString().replace( "\\", "/" );

//            startingPathStr = startingPathStr.substring(0, 1).equals( "/" ) ? startingPathStr : "/" + startingPathStr;
//            toPathStr = toPathStr.substring(0, 1).equals( "/" ) ? toPathStr : "/" + toPathStr;
//            fromPathStr = fromPathStr.substring(0, 1).equals( "/" ) ? fromPathStr : "/" + fromPathStr;

    //        System.out.println( "Copier new File( toPath ).toURI() =" + new File( toPath ).toURI() + "=" );
            System.out.println( "startingPathStr =" + startingPathStr + "=" );
            System.out.println( "toPathStr =" + toPathStr + "=" );
            System.out.println( "fromPathStr =" + fromPathStr + "=" );
            toPathFileSeparator = this.toPath.getFileSystem().getSeparator();

            System.out.println( "this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
            System.out.println( "this.toPath =" + this.toPath + "=" );
            Path fromPathOrig = this.fromPath;
            fromParent = fromPath.getParent();

            System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() + "=" );
    //        targetPath = this.toPath.resolve( this.startingPath.relativize( fromPath ) );
    //        System.out.println( "relativize =" + this.startingPath.relativize( fromPath ) + "=" );

            targetPath = Paths.get( toPathStr ).resolve( Paths.get( startingPathStr ).relativize( Paths.get( fromPathStr ) ) );
            System.out.println( "relativize =" + Paths.get( startingPathStr ).relativize( Paths.get( fromPathStr ) ) + "=" );

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
                else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
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
                else
                    {
                    System.out.println( "Error: not sftp to local or the reverse !" );
                    return;
                    }
                }
            }
        catch (Exception ex) 
            {
            ex.printStackTrace();
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
            copyRecursiveSftpToLocal( sourceFolder, targetPathStr );
//            copyRecursiveSftpToSftp( sourceFolder, targetPathStr );
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
        
//        if ( connUserInfo.getFromFilesysType() != connUserInfo.getToFilesysType() )
//            {
//            if ( connUserInfo.getToFilesysType() == Constants.FILESYSTEM_DOS )
//                {
//                destinationFolderStr = destinationFolderStr.replace( "/", "\\" );
//                }
//            else
//                {
//                destinationFolderStr = destinationFolderStr.replace( "\\", "/" );
//                }
//            }
        destinationFolderStr = destinationFolderStr.replace( "\\", "/" );
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
                System.out.println( "remote dir exists" );
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
                    System.out.println( "mkdir =" + destinationFolderStr + "=" );
                    //                destinationFolder.mkdir();
                    chanSftp.mkdir( destinationFolderStr );
                    System.out.println( "Directory created :: " + destinationFolderStr );
                    } 
                catch (SftpException ex)
                    {
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    processStatus = "Error";
                    message = ex + ": " + destinationFolderStr;
                    errorList.add( destinationFolderStr + " -> create remote Directory - ERROR " + ex );
                    return;
                    }
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
            Path dir = Paths.get( sourceFolderStr );  // delete folder if doing cut
            try {
                if ( isDoingCutFlag )
                    {
                    Files.delete( dir );
                    System.out.println( "would delete local folder =" + dir );
                    }
                }
            catch (Exception ex2) 
                {
                logger.log(Level.SEVERE, null, ex2 );
                errorList.add( dir + " -> " + "ERROR " + ex2 );
                //System.out.println( "CAUGHT ERROR  " + "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
                //throw new IOException( ex2 + ": " + dir );
                }
            numFolderMatches++;
            }
        else
            {
            System.out.println( "Is File" );
            numFileTests ++;
            boolean copyErrorFlag = false;
            //Copy the file content from one place to another
//            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            try {
                //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
                System.out.println( "SftpPut locFile =" + sourceFolder + "=   to rmtFile =" + destinationFolderStr + "=" );
                
//                System.out.println( "copyOptions contains? StandardCopyOption.REPLACE_EXISTING =" + copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) + "=" );
                    
                if ( sftp.exists( destinationFolderStr ) )
                    {
                    if ( copyOptions.contains( StandardCopyOption.REPLACE_EXISTING ) )
                        {
//                        chanSftp.chmod( 511, destinationFolderStr );   // 511 is decimal for 777 in Octal
//					overwrite option could be looked at:  channelSftp.put(new FileInputStream(f1), f1.getName(), ChannelSftp.OVERWRITE);
                        System.out.println( "rm " + destinationFolderStr );
                        chanSftp.rm( destinationFolderStr );
                        System.out.println( "put " + sourceFolderStr );
                        chanSftp.put( sourceFolderStr, destinationFolderStr );
                        System.out.println( "File replaced :: " + destinationFolderStr );
                        numFileMatches++;
                        }
                    else
                        {
                        System.out.println( "File Not replaced/copied :: " + destinationFolderStr );
                        copyErrorFlag = true;
                        message = "File Not replaced/copied: " + destinationFolderStr;
                        errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  Will not replace existing file." );
                        }
                    }
                else
                    {
                    System.out.println( "put new " + sourceFolderStr );
                    chanSftp.put( sourceFolderStr, destinationFolderStr );
                    System.out.println( "File copied :: " + destinationFolderStr );
                    numFileMatches++;
                    }
//                jschSftpUtils.copyTo()  does not work with spaces in names off-hand.
//                jschSftpUtils.copyTo( jschSession, sourceFolderStr, destinationFolderStr );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag && ! copyErrorFlag )
                    {
                    Files.delete( Paths.get( sourceFolderStr ) );
//                    System.out.println( "would delete local file =" + sourceFolderStr );
                    }
                }
            catch (SftpException sex) 
                {
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, sex);
                processStatus = "Error";
                message = sex + ": " + destinationFolderStr;
                errorList.add( sourceFolderStr + " -> copy to remote File =" + destinationFolderStr + "=  ERROR " + sex );
                }
            catch (Exception ex) 
                {
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
                processStatus = "Error";
                message = ex + ": " + destinationFolderStr;
                errorList.add( sourceFolderStr + " -> copy to remote File =" + destinationFolderStr + "=  ERROR " + ex );
                }
            }
        if ( ! connUserInfo.isRunCopyOnRemote() )
            {
            swingWorker.publish2( numTested );
            }
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
        
//        if ( connUserInfo.getFromFilesysType() != connUserInfo.getToFilesysType() )
//            {
//            if ( connUserInfo.getFromFilesysType() == Constants.FILESYSTEM_DOS )
//                {
//                sourceFolderStr = sourceFolderStr.replace( "/", "\\" );
//                }
//            else
//                {
//                sourceFolderStr = sourceFolderStr.replace( "\\", "/" );
//                }
//            }
        sourceFolderStr = sourceFolderStr.replace( "\\", "/" );
        System.out.println( "copyRecursiveSftpToLocal() /or\\ srcFile =" + sourceFolderStr + "=   destFile =" + destinationFolderStr + "=" );
        
        SftpATTRS sourceSftpAttrs = null;
        try {
            sourceSftpAttrs = chanSftp.stat( sourceFolderStr );
            } 
        catch (SftpException ex)
            {
            ex.printStackTrace();
            Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
            }

        if ( sourceSftpAttrs != null && sourceSftpAttrs.isDir() )
            {
            System.out.println( "copyRecursiveSftpToLocal() - Is Directory" );
            numFolderTests ++;
            try {
                //Verify if destinationFolder is already present; If not then create it
                if ( ! destinationFolder.exists() )
                    {
                    destinationFolder.mkdir();
                    System.out.println( "Directory created :: " + destinationFolder );
                    }

                //Get all files from source directory
                Vector vecfiles;
                ArrayList<ChannelSftp.LsEntry> files = new ArrayList<ChannelSftp.LsEntry>();
                vecfiles = chanSftp.ls( sourceFolderStr );
                for ( Object obj : vecfiles )
                    {
                    if (obj instanceof ChannelSftp.LsEntry) {
                        files.add( (ChannelSftp.LsEntry) obj );
                        }
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
                }
            catch (SftpException ex) 
                {
                errorList.add( sourceFolderStr + " -> list Directory entries to copy - ERROR " + ex );
                ex.printStackTrace();
                Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
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
                    errorList.add( sourceFolderStr + " -> remove Directory - ERROR " + ex );
                    }
                }
            numFolderMatches++;
            }
        else
            {
            System.out.println( "copyRecursiveSftpToLocal() - Is File" );
            numFileTests ++;
            boolean copyErrorFlag = false;
            //Copy the file content from one place to another
//            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            try {
                //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
                System.out.println( "SftpGet locFile =" + sourceFolder + "=   to rmtFile =" + destinationFolderStr + "=" );
                if ( Files.exists( Paths.get( destinationFolderStr ) ) )
                    {
                    if ( copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) )
                        {
                        File dfile = new File( destinationFolderStr );
                        dfile.setWritable( true );
                        if ( connUserInfo.getToFilesysType() == Constants.FILESYSTEM_DOS )
                            {
                            Files.setAttribute( Paths.get( destinationFolderStr ), "dos:readonly", false );
                            }        
                        chanSftp.get( sourceFolderStr, destinationFolderStr );
                        numFileMatches++;
                        System.out.println( "File replaced :: " + destinationFolderStr );
                        }
                    else
                        {
                        System.out.println( "File Not replaced/copied :: " + destinationFolderStr );
                        copyErrorFlag = true;
                        message = "File Not replaced/copied: " + destinationFolderStr;
                        errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  Will not replace existing file." );
                        }
                    }
                else
                    {
//                    sourceFolderStr = sourceFolderStr.substring(0, 1).equals( "/" ) ? sourceFolderStr : "/" + sourceFolderStr;
                    System.out.println( "copyRecursiveSftpToLocal() chanSftp.get() srcFile =" + sourceFolderStr + "=   destFile =" + destinationFolderStr + "=" );
                    chanSftp.get( sourceFolderStr, destinationFolderStr );
                    numFileMatches++;
                    System.out.println( "File copied :: " + destinationFolderStr );
                    }
//                jschSftpUtils.copyTo()  does not work with spaces in names off-hand.
//                jschSftpUtils.copyTo( jschSession, sourceFolderStr, destinationFolderStr );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag && ! copyErrorFlag )
                    {
//                    Files.delete( Paths.get( sourceFolderStr ) );
                    chanSftp.rm( sourceFolderStr );
                    System.out.println( "would delete file =" + sourceFolderStr );
                    }
                }
            catch (SftpException sex) 
                {
                sex.printStackTrace();            
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, sex);
                message = "sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" + ": "+ sex;
                errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  ERROR " + sex );
                }
            catch (Exception ex) 
                {
                ex.printStackTrace();            
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println( "Error for sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" );
                message = "sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" + ": "+ ex;
                errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  ERROR " + ex );
                }
            }
        if ( ! connUserInfo.isRunCopyOnRemote() )
            {
            swingWorker.publish2( numTested );
            }
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
                    message = ex + ": " + destinationFolderStr;
                    errorList.add( destinationFolderStr + " -> mkdir  ERROR " + ex );
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
                errorList.add( sourceFolderStr + " -> get list of files in this folder.  ERROR " + ex );
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
                    errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  ERROR " + ex );
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            numFolderMatches++;
            }
        else
            {
            System.out.println( "copyRecursiveSftpToSftp() - Is File" );
            numFileTests ++;
            boolean copyErrorFlag = false;
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
                        copyErrorFlag = true;
                        message = "File Not replaced/copied: " + destinationFolderStr;
                        errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  Will not replace existing file." );
                        }
                    }
                else
                    {
                    try {
//                        chanSftp.get( sourceFolderStr, destinationFolderStr );
                        InputStream inputStream = chanSftpSrc.get( sourceFolderStr );
    //                       File f = new File(file.getName());
                        chanSftp.put( inputStream, destinationFolderStr );
                        } 
                    catch (Exception ex) 
                        {
                        System.out.println( "Error 1st attempt for sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" );
                        System.out.println( "      for connUserInfo =" + connUserInfo + "=" );

                        File tmpTransferFile = DesktopUtils.getJfpHome( "xxx", "file" );
                        String tmpTransferFileStr = DesktopUtils.getJfpHome( "xxx", "file" ).toString();
                        if ( tmpTransferFile.exists() )  tmpTransferFile.delete();
                        chanSftpSrc.get( sourceFolderStr, tmpTransferFileStr );
                        chanSftp.put( tmpTransferFileStr, destinationFolderStr );
                        }
                    numFileMatches++;
                    System.out.println( "File copied :: " + destinationFolderStr );
                    }
//                jschSftpUtils.copyTo()  does not work with spaces in names off-hand.
//                jschSftpUtils.copyTo( jschSession, sourceFolderStr, destinationFolderStr );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag && ! copyErrorFlag )
                    {
//                    Files.delete( Paths.get( sourceFolderStr ) );
                    chanSftpSrc.rm( sourceFolderStr );
                    System.out.println( "would delete file =" + sourceFolderStr );
                    }
                } 
            catch (Exception ex) 
                {
                System.out.println( "Error for sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=" );
                System.out.println( "      for connUserInfo =" + connUserInfo + "=" );
                errorList.add( sourceFolderStr + " -> copy to destinationFolderStr =" + destinationFolderStr + "=  ERROR " + ex );
                
                message = "sourceFolderStr =" + sourceFolderStr + "=    destinationFolderStr =" + destinationFolderStr + "=";
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
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
            errorList.add( dir + " -> " + "ERROR " + ex2 );
            //System.out.println( "CAUGHT ERROR  " + "my error msg" + ex2.getClass().getSimpleName() + ": " + dir );
            //throw new IOException( ex2 + ": " + dir );
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

    public ArrayList<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(ArrayList<String> errorList) {
        this.errorList = errorList;
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
