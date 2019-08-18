/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
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
import javax.swing.JOptionPane;

/**
 *
 * @author Stan Towianski
 */
public class Copier extends SimpleFileVisitor<Path> 
{
    private static final MyLogger logger = MyLogger.getLogger( Copier.class.getName() );  // because this is just used by the copyFrame

    private Boolean isDoingCutFlag = false;
    private Path startingPath;
    private Path toPath;
    private Path fromPath;
//    private CopyOption[] copyOptions = null;
    ArrayList<CopyOption> copyOptions = new ArrayList<CopyOption>();
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

//      static {
////       logger.setLevel(Level.INFO);
//        logger.info( "\nCopier: list of log handlers" );
//       for (Handler handler : logger.getHandlers()) {
////           handler.setLevel(Level.INFO);
//            logger.info( "handler =" + handler );
//       }           
//   }
      
    public Copier( Boolean isDoingCutFlag, ArrayList<CopyOption> copyOptions, CopyFrameSwingWorker swingWorker )
    {
        this.isDoingCutFlag = isDoingCutFlag;
        this.copyOptions = copyOptions;
        this.swingWorker = swingWorker;
        logger.info( "Copier this.startingPath (startingPath) =" + this.startingPath + "   this.toPath =" + this.toPath + "=" );
        logger.info( "isDoingCutFlag =" + isDoingCutFlag );
        cancelFlag = false;
        
//        logger.info( "Copier jFileFinderWin.getLogLevel() =" + jFileFinderWin.getLogLevel() + "=" );
//        logger.setLevel( jFileFinderWin.getLogLevel() );
        logger.info( "Copier logger.getLevel() =" + logger.getLevel() + "=" );
//        logger.info( "Copier logger.getLogString() =" + logger.getLogString() + "=" );        
//        logger.clearLog();
    }

    public void setPaths( Path fromPath, String startingPath, String toPath ) {
        this.fromPath = fromPath;
        logger.info( "called set fromPath =" + this.fromPath + "=" );
        
        this.startingPath = Paths.get( startingPath );
        this.toPath = Paths.get( toPath );
        logger.info( "Copier new File( toPath ).toURI() =" + new File( toPath ).toURI() + "=" );
        toPathFileSeparator = this.toPath.getFileSystem().getSeparator();

        logger.info( "this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
        logger.info( "this.toPath =" + this.toPath + "=" );
        Path fromPathOrig = this.fromPath;
        Path fromParent = fromPath.getParent();
        String ans = "";

        Path targetPath = this.toPath.resolve( this.startingPath.relativize( fromPath ) );
        logger.info( "relativize =" + this.startingPath.relativize( fromPath ) + "=" );
        logger.info( "toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

        try {
            while ( fromPath.toFile().isDirectory() &&
                    this.toPath.toRealPath().equals( this.startingPath.toRealPath() ) )
                {
                if ( swingWorker == null )
                    {
                    message = "ERROR: Folder exists";
                    cancelFlag = true;
                    break;
                    }

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
                logger.info( "new this.startingPath =" + this.startingPath + "   this.fromPath =" + this.fromPath + "=" );
                logger.info( "new this.toPath =" + this.toPath + "=" );
                }
            
            if ( this.toPath.toRealPath().startsWith( this.fromPath.toRealPath() ) )
                {
                JOptionPane.showMessageDialog( null, "You cannot copy a parent into a child folder." );
                cancelFlag = true;
                }
            } 
        catch (IOException ex) 
            {
            logger.severeExc( ex );
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
        try {
            Path targetPath = this.toPath.resolve( this.startingPath.relativize( dir ) );
    //        logger.info( );
    //        logger.info( "preVisitDir relativize =" + this.startingPath.relativize( dir ) + "=" );
    //        logger.info( "preVisitDir toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );

            numTested++;
            numFolderTests ++;
            numFolderMatches++;
            if ( cancelFlag )
                {
                logger.info( "Search cancelled by user." );
                return FileVisitResult.TERMINATE;
                }

            Path toPathFile = toPath.resolve( startingPath.relativize( dir ) );

    //        logger.info( "dir =" + dir + "= .getFileSystem() =" + dir.getFileSystem() + "=   toPathFile =" + toPathFile + "= .getFileSystem() =" + toPathFile.getFileSystem() + "=" );
            logger.info( "dir =" + dir + "= .getFileStore() =" + Files.getFileStore( dir ) + "=   toPathFile.getParent() =" + toPathFile.getParent() + "= .getFileStore() =" + Files.getFileStore( toPathFile.getParent() ) + "=" );
            if ( isDoingCutFlag &&
                ( Files.getFileStore( dir ).equals( Files.getFileStore( toPathFile.getParent() ) ) ) )
                {  // doing Move
                logger.info( "do MOVE dir =" + dir + "=   to =" + toPathFile + "=" );
                //logger.info( "copyOptions contains? StandardCopyOption.REPLACE_EXISTING =" + copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) + "=" );
                if ( copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) )
                    Files.move( dir, toPathFile, StandardCopyOption.REPLACE_EXISTING );
                else
                    Files.move( dir, toPathFile );
                //Files.delete( dir );
                //logger.info( "would delete folder =" + dir );
                //numFoldersDeleted++;
                return FileVisitResult.SKIP_SUBTREE;
                }
            else   // doing Copy
                {
                if ( ! Files.exists( targetPath ) )
                    {
                    //logger.info( "preVisitDir would do Files.createDirectory( " + targetPath + ")" );
                    Files.createDirectory( targetPath );
                    }
                }
            }
        catch ( java.nio.file.NoSuchFileException noSuchFileExc ) 
            {
            logger.info( "ERROR  " + noSuchFileExc + ": " + dir );
            logger.info( logger.getExceptionAsString( noSuchFileExc ) );
            errorList.add( dir + " -> " + "ERROR " + noSuchFileExc );
            //return FileVisitResult.TERMINATE;
            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            logger.info( "WARNING  " + exAccessDenied + ": " + dir );
            logger.info( logger.getExceptionAsString( exAccessDenied ) );
            errorList.add( dir + " -> " + "WARNING " + exAccessDenied );
            if ( swingWorker != null )  swingWorker.setCloseWhenDoneFlag( false );
            //return FileVisitResult.TERMINATE;
            }
        catch ( java.nio.file.FileAlreadyExistsException faeExc )
            {
            logger.info( "ERROR  " + faeExc + ": " + dir );
            logger.info( "ERROR  " + faeExc + ": " + dir );
            errorList.add( dir + " -> " + "ERROR " + faeExc );
            message = "ERROR: " + faeExc + ": " + dir;
            //return FileVisitResult.CONTINUE;
            }
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
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) 
            throws IOException 
        {
        Path toPathFile = null;

        try {
        Path targetPath = toPath.resolve( startingPath.relativize( file ) );
//        logger.info( );
//        logger.info( "preVisit startingPath =" + startingPath + "   file =" + file + "=" );
//        logger.info( "preVisit relativize =" + startingPath.relativize( file ) + "=" );
//        logger.info( "preVisit toPath =" + toPath + "   resolve targetPath =" + targetPath + "=" );
//        logger.info( "VisitFile copy file =" + file + "=    toPath.resolve( startingPath.relativize( file ) ) =" + toPath.resolve( startingPath.relativize( file ) ) + "=" );
        logger.finest( "VisitFile copy file =" + file + "=    toPath.resolve( startingPath.relativize( file ) ) =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
        if ( cancelFlag )
            {
            logger.info( "Search cancelled by user." );
            return FileVisitResult.TERMINATE;
            }
        numTested++;
        numFileTests ++;
        if ( swingWorker != null )  swingWorker.publish2( numTested );
        
//                    this.toPath.toRealPath().toString().startsWith( this.startingPath.toRealPath().toString() ) )
//                    if ( this.toPath.toRealPath().toString().startsWith( this.startingPath.toRealPath().toString() ) )
//                        {
//                        ans = JOptionPane.showInputDialog( "Cannot copy a parent folder onto a child. New name: ", fromPath.getFileName() );
//                        }
//                    else
//                        {
//                        }
        
        toPathFile = toPath.resolve( startingPath.relativize( file ) );
        while ( file.compareTo( toPathFile ) == 0 )
            {
            logger.info( "would Copy to Itself." );
            Path beforeFile = file;
            String ans = JOptionPane.showInputDialog( "Copy file onto itself. New name: ", file.getFileName() );
            if ( ans == null )
                {
                return FileVisitResult.CONTINUE;
                }
            toPathFile = Paths.get( toPathFile.getParent() + toPathFileSeparator + ans );
            this.startingPath = fromPath;
            logger.info( "beforeFile =" + beforeFile + "=" );
            logger.info( "new file =" + toPathFile + "=" );
            }

//            CopyOption[] copyOpts = new CopyOption[3];
//            //copyOpts[0] = StandardCopyOption.REPLACE_EXISTING;
//            copyOpts[1] = StandardCopyOption.COPY_ATTRIBUTES;
//            //copyOpts[2] = LinkOption.NOFOLLOW_LINKS;
//            logger.info( "copyOptions size =" + copyOptions.size() + "=" );
//            logger.info( "copyOptions contains? StandardCopyOption.REPLACE_EXISTING =" + copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) + "=" );
//            logger.info( "copyOptions.toArray( new CopyOption[ copyOptions.size() ] ) =" + copyOptions.toArray( new CopyOption[ copyOptions.size() ] ) + "=" );

//            logger.info( "file =" + file + "= .getFileSystem() =" + file.getFileSystem() + "=   toPathFile =" + toPathFile + "= .getFileSystem() =" + toPathFile.getFileSystem() + "=" );
            logger.info( "file =" + file + "= .getFileStore() =" + Files.getFileStore( file ) + "=   toPathFile.getParent() =" + toPathFile.getParent() + "= .getFileStore() =" + Files.getFileStore( toPathFile.getParent() ) + "=" );
            if ( isDoingCutFlag &&
                ( Files.getFileStore( file ).equals( Files.getFileStore( toPathFile.getParent() ) ) ) )
                {  // doing Move
                logger.info( "do MOVE file =" + file + "=   to =" + toPathFile + "=" );
                logger.info( "copyOptions contains? StandardCopyOption.REPLACE_EXISTING =" + copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) + "=" );
                if ( copyOptions.contains(StandardCopyOption.REPLACE_EXISTING) )
                    Files.move( file, toPathFile, StandardCopyOption.REPLACE_EXISTING );
                else
                    Files.move( file, toPathFile );
                //Files.delete( dir );
                //logger.info( "would delete folder =" + dir );
                //numFoldersDeleted++;
                }
            else   // doing Copy
                {
                if ( copyOptions == null || copyOptions.size() < 1 )
                    {
    //                logger.info( "copy with default options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                    //logger.info( "copy with default options. file =" + toPathFile + "=" );
                    Files.copy( file, toPathFile );
                    }
                else
                    {
    //                logger.info( "copy with sent options. file =" + file + "=   to =" + toPath.resolve(startingPath.relativize( file ) ) + "=" );
                    //logger.info( "copy with sent options. file =" + toPathFile + "=" );
                    Files.copy( file, toPathFile, copyOptions.toArray( new CopyOption[ copyOptions.size() ] ) );
                    }
                }
            }
        catch ( java.nio.file.NoSuchFileException noSuchFileExc ) 
            {
            logger.info( "ERROR  " + noSuchFileExc + ": " + file );
            logger.info( logger.getExceptionAsString( noSuchFileExc ) );
            errorList.add( file + " -> " + "ERROR " + noSuchFileExc );
            return FileVisitResult.CONTINUE;
            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            logger.info( "WARNING  " + exAccessDenied + ": " + file );
            logger.info( logger.getExceptionAsString( exAccessDenied ) );
            errorList.add( file + " -> " + "WARNING " + exAccessDenied );
            if ( swingWorker != null )  swingWorker.setCloseWhenDoneFlag( false );
            return FileVisitResult.CONTINUE;
            }
        catch ( java.nio.file.FileAlreadyExistsException faeExc )
            {
            logger.info( "ERROR  " + faeExc + ": " + file );
            logger.info( "ERROR  " + faeExc + ": " + file );
            errorList.add( file + " -> " + "ERROR " + faeExc );
            message = "ERROR: " + faeExc + ": " + file;
            return FileVisitResult.CONTINUE;
            }
        catch ( Exception exc )
            {
            logger.severe( "ERROR  " + exc + ": " + file );
            logger.info( "ERROR  " + exc + ": " + file );
            errorList.add( file + " -> " + "ERROR " + exc );
            processStatus = "Error";
            message = exc + ": " + file;
            return FileVisitResult.TERMINATE;
            }
    
        if ( isDoingCutFlag && Files.exists( file ) &&
              ! ( Files.getFileStore( file ).equals( Files.getFileStore( toPathFile.getParent() ) ) ) )
            {
            Files.delete( file );
            //logger.info( "would delete file =" + file );
            }
        
        numFileMatches++;
        logger.info( "visitFile() numFileMatches = " + numFileMatches );
        return FileVisitResult.CONTINUE;
        }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ex) 
            throws IOException
        {
        try {
            //numTested++;
            if ( isDoingCutFlag )
                {
                Files.delete( dir );
                logger.info( "would delete folder =" + dir );
                //numFoldersDeleted++;
                }
            return FileVisitResult.CONTINUE;
            }
        //throw ex;
//        catch (RuntimeException ex3) 
//            {
//            logger.severeExc( ex );
//        logger.info( "CAUGHT RUNTIME ERROR  " + "my error msg" + ex3 + ": " + dir );
//            throw new IOException( "my runtime msg" + ex3.getClass().getSimpleName() + ": " + dir );
//            }
        catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
            {
            logger.info( "WARNING  " + exAccessDenied + ": " + dir );
            logger.info( logger.getExceptionAsString( exAccessDenied ) );
            errorList.add( dir + " -> " + "WARNING " + exAccessDenied );
            if ( swingWorker != null )  swingWorker.setCloseWhenDoneFlag( false );
            return FileVisitResult.CONTINUE;
            }
        catch (Exception ex2) 
            {
            logger.severeExc( ex2 );
            errorList.add( dir + " -> " + "ERROR " + ex2 );
            ex2.printStackTrace();
            //logger.info( "ERROR  " + "my error msg" + ex2 + ": " + dir );
            throw new IOException( ex2 + ": " + dir );
            }
        //return FileVisitResult.TERMINATE;
        }
    
        @Override
        public FileVisitResult visitFileFailed( Path file, IOException exc ) 
            {
            logger.info( "Copier.visitFileFailed() for file =" + file.toString() );
            errorList.add( file + " -> " + "ERROR " + exc );
            return FileVisitResult.SKIP_SUBTREE;
//            if ( new File( file.toString() ).isDirectory() )
//                {
//                logger.info( "skipping inaccessible folder: " + file.toString() );
//                if ( exc instanceof java.nio.file.AccessDeniedException )
//                    {
//                    BasicFileAttributes attrs;
//                    try {
//                        attrs = Files.readAttributes( file, BasicFileAttributes.class );
//                        processFolder( file, attrs );
//                        noAccessFolder.put( file, null );
//            }
//                    catch (Exception ex) 
//                        {
//                        logger.info( "Error calling processFolder in visitFileFailed()" );
//                        ex.printStackTrace();
//                        }
//                    }
//                else
//                    {
//                    exc.printStackTrace();
//                    }
//                return FileVisitResult.SKIP_SUBTREE;
//                }
//            return CONTINUE;
            }
            
        
    // Prints the total number of
    // matches to standard out.
    void done() 
        {
        logger.info( "Tested:  " + numTested );
        logger.info( "Copied numFileMatches: " + numFileMatches );
        logger.info( "Copied numFolderMatches: " + numFolderMatches );
        logger.info( "Copied numFileTests: " + numFileTests );
        logger.info( "Copied numFolderTests: " + numFolderTests );

        if ( numFileMatches != numFileTests  ||
             numFolderMatches != numFolderTests )
            {
            processStatus = CopyFrame.PROCESS_STATUS_COPY_INCOMPLETED;
            logger.info( "processStatus =" + processStatus + "=  should be incomplete" );
            }

//            for ( Path mpath : matchedPathsList )
//                {
//                logger.info( mpath );
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
        
}
