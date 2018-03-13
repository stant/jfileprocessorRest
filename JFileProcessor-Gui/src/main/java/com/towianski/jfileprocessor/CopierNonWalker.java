/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.towianski.models.ConnUserInfo;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
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
    private CopyOption[] copyOptions = null;
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
    com.jcraft.jsch.ChannelSftp chanSftp = null;
    JschSftpUtils jschSftpUtils = new JschSftpUtils();
    Session jschSession = null;
    Path targetPath = null;

//      static {
////       logger.setLevel(Level.INFO);
//        System.out.println( "\nCopier: list of log handlers" );
//       for (Handler handler : logger.getHandlers()) {
////           handler.setLevel(Level.INFO);
//            System.out.println( "handler =" + handler );
//       }           
//   }
      
    public CopierNonWalker( ConnUserInfo connUserInfo, com.jcraft.jsch.ChannelSftp chanSftp, Boolean isDoingCutFlag, CopyOption[] copyOptions, CopyFrameSwingWorker swingWorker )
    {
        this.connUserInfo = connUserInfo;
        this.chanSftp = chanSftp;
        this.isDoingCutFlag = isDoingCutFlag;
        this.copyOptions = copyOptions;
        this.swingWorker = swingWorker;
        System.out.println("Copier this.startingPath (startingPath) =" + this.startingPath + "   this.toPath =" + this.toPath + "=" );
        System.out.println( "isDoingCutFlag =" + isDoingCutFlag );
        cancelFlag = false;
        
        jschSession = jschSftpUtils.createSession( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );

//        System.out.println( "Copier jFileFinderWin.getLogLevel() =" + jFileFinderWin.getLogLevel() + "=" );
//        logger.setLevel( jFileFinderWin.getLogLevel() );
        System.out.println( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
//        System.out.println( "Copier logger.getLogString() =" + logger.getLogString() + "=" );        
//        logger.clearLog();
    }

    public void setPaths( Path fromPath, String startingPath, String toPath ) {
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

        targetPath = this.toPath.resolve( this.startingPath.relativize( fromPath ) );
        System.out.println( "relativize =" + this.startingPath.relativize( fromPath ) + "=" );
        System.out.println( "toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

        /** FIXXX when doing remote !
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
            * **/
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
            if ( copyOptions == null || copyOptions.length < 1 )
                {
//                System.out.println("copy with default options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                Files.copy( file, toPathFile );
                }
            else
                {
//                System.out.println("copy with sent options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                Files.copy( file, toPathFile, copyOptions );
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

    public void copyRecursive( File sourceFolder ) throws IOException
        {
        copyRecursive( sourceFolder, new File( targetPath.toString() ) );
        }
    
    public void copyRecursive( File sourceFolder, File destinationFolder ) throws IOException
    {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        numTested++;
        if (sourceFolder.isDirectory())
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
                System.out.println( "at 2" );
                chanSftp.lstat( destinationFolder.toString() );
                } 
            catch (SftpException ex)
                {
                System.out.println( "at 2b" );
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
                System.out.println( "at 3" );
                    //                destinationFolder.mkdir();
                    chanSftp.mkdir( destinationFolder.toString() );
                System.out.println( "at 4" );
                    } 
                catch (SftpException ex)
                    {
                System.out.println( "at 5" );
                    Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                System.out.println("Directory created :: " + destinationFolder);
                }
             
            //Get all files from source directory
            String files[] = sourceFolder.list();
                System.out.println( "at 6" );
             
            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files)
                {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);
                 
                //Recursive function call
                copyRecursive(srcFile, destFile);
                }
                System.out.println( "at 7" );
            postVisitDirectory( Paths.get( destinationFolder.toString() ) );
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
                System.out.println( "SftpPut locFile =" + sourceFolder + "=   to rmtFile =" + destinationFolder + "=" );
//                chanSftp.put( sourceFolder.toString(), destinationFolder.toString() );
                jschSftpUtils.copyTo( jschSession, sourceFolder.toString(), destinationFolder.toString() );
                System.out.println( "at 8" );
    
                if ( isDoingCutFlag )
                    {
                    Files.delete( Paths.get( sourceFolder.toString() ) );
                    System.out.println( "would delete file =" + sourceFolder.toString() );
                    }
                } 
            catch (Exception ex) 
                {
                java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            numFileMatches++;
            System.out.println("File copied :: " + destinationFolder);
            }
        swingWorker.publish2( numTested );
    }

    public void postVisitDirectory(Path dir)
            throws IOException
        {
        try {
            //numTested++;
            if ( isDoingCutFlag )
                {
                Files.delete( dir );
                //System.out.println( "would delete folder =" + dir );
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
