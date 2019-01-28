package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ResultsData;
import com.towianski.models.FilesTblModel;
import com.towianski.chainfilters.FilterChain;
import com.towianski.models.Constants;
import com.towianski.utils.FinderFileVisitor;
import com.towianski.utils.MyLogger;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;


public class JFileFinder //  implements Runnable 
{
    private final static MyLogger logger = MyLogger.getLogger( JFileFinder.class.getName() );

    static Boolean dataSyncLock = false;
//    private JFileFinderWin jFileFinderWin = null;
    int filesysType = -1;
    private String startingPath = null;
    private String patternType = null;
    private String filePattern = null;
    private int startingPathLength = 0;
    private int basePathLen = 0;
    private boolean cancelFlag = false;
    private boolean cancelFillFlag = false;
    private ArrayList<Path> matchedPathsList = null;
    private HashMap<Path, String> noAccessFolder = new HashMap<Path, String>();
    private FinderFileVisitor finderFileVisitor = null;
    private FilterChain chainFilterList = null;
    private FilterChain chainFilterFolderList = null;
    private FilterChain chainFilterPreVisitFolderList = null;
    private SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd hh:mm:ss");
    private Date begDate = null;
    private Date endDate = null;
    private ResultsData resultsData = null;

    
    public JFileFinder()
        {
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            filesysType = Constants.FILESYSTEM_DOS;
            }
        else
            {
            filesysType = Constants.FILESYSTEM_POSIX;
            }
        }    

    public JFileFinder( String startingPathArg, String patternTypeArg, String filePatternArg, FilterChain chainFilterList, FilterChain chainFilterFolderList, FilterChain chainFilterPreVisitFolderList )
    {
        this();
        this.startingPath = startingPathArg;   //.replace( "\\\\", "/" ).replace( "\\", "/" );
        this.patternType = patternTypeArg;
        this.filePattern = filePatternArg;  //.replace( "\\\\", "/" ).replace( "\\", "/" );
        this.cancelFlag = false;
        this.matchedPathsList = new ArrayList<Path>();
        this.noAccessFolder = new HashMap<Path, String>();
        this.chainFilterList = chainFilterList;
        this.chainFilterFolderList = chainFilterFolderList;
        this.chainFilterPreVisitFolderList = chainFilterPreVisitFolderList;
        
        System.out.println( "JFIleFinder constructor() with chainFilterList.size()               =" + chainFilterList.size() + "=" );
        System.out.println( "JFIleFinder constructor() with chainFilterFolderList.size()         =" + chainFilterFolderList.size() + "=" );
        System.out.println( "JFIleFinder constructor() with chainFilterPreVisitFolderList.size() =" + chainFilterPreVisitFolderList.size() + "=" );
        
        logger.setLevel( Level.SEVERE );
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        finderFileVisitor.cancelSearch();
        }

    public void cancelFill()
        {
        cancelFillFlag = true;
        }

    public FilesTblModel getFilesTableModel() 
        {
        synchronized( dataSyncLock ) 
            {
            cancelFillFlag = false;
            ArrayList<String> HeaderList = new ArrayList<String>();
            ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

            if ( filesysType == Constants.FILESYSTEM_POSIX )
                {
                getPosixFileInfo( PathsInfoList );
                }
            else if ( filesysType == Constants.FILESYSTEM_DOS )
                {
                getDosFileInfo( PathsInfoList );
                }

            if ( PathsInfoList.size() < 1 )
                {
//                HeaderList.add( " " );
                ArrayList<Object> newRow = new ArrayList<Object>();
                    newRow.add( "" );
                    newRow.add( "" );
//                newRow.add( false );
//                newRow.add( true );
                if ( noAccessFolder.size() > 0 )
                    {
                    newRow.add( "Inaccessible" );
//   FIXXX                 jFileFinderWin.stopDirWatcher();
                    }
                else
                    {
                    newRow.add( "No Files Found" );
                    }
                    newRow.add( "" );
                    newRow.add( "" );
                    newRow.add( "" );
                    newRow.add( "" );
                    newRow.add( "" );
//                newRow.add( Calendar.getInstance().getTime() );
//                newRow.add( (long) 0 );
                PathsInfoList.add( newRow );
                }
  //          else
  //              {
                HeaderList.add( "Type" );
                HeaderList.add( "Dir" );
                HeaderList.add( "File" );
                HeaderList.add( "last Modified Time" );
                HeaderList.add( "Size" );
//                if ( jFileFinderWin.isShowOwnerFlag() )
                    {
                    HeaderList.add( "Owner" );
                    }
//                if ( jFileFinderWin.isShowGroupFlag() )
                    {
                    HeaderList.add( "Group" );
                    }
//                if ( jFileFinderWin.isShowPermsFlag() )
                    {
                    HeaderList.add( "Perms" );
                    }
//                }

            FilesTblModel filesTblModel = new FilesTblModel( HeaderList, PathsInfoList );
            resultsData.setFilesTblModel(filesTblModel);
            return filesTblModel;
            }
    }
    
    public void getPosixFileInfo( ArrayList<ArrayList> PathsInfoList )
        {
        for ( Path fpath : matchedPathsList )
            {
            if ( cancelFillFlag )
                {
                break;
                }
            ArrayList<Object> rowList = new ArrayList<Object>();
            BasicFileAttributes attr;
            try {
                attr = Files.readAttributes( fpath, BasicFileAttributes.class );
                PosixFileAttributes fsattr = Files.readAttributes( fpath, PosixFileAttributes.class );

                int ftype = FilesTblModel.FILETYPE_NORMAL;
                if ( Files.isSymbolicLink( fpath ) )
                    {
                    ftype = FilesTblModel.FILETYPE_LINK;
                    }
                else if ( attr.isOther() )
                    {
                    ftype = FilesTblModel.FILETYPE_OTHER;
                    }
//                    rowList.add( Files.isSymbolicLink( fpath ) );  // needed to make linux work
                rowList.add( ftype );  // needed to make linux work

//                    rowList.add( attr.isDirectory() );
                int folderType = FilesTblModel.FOLDERTYPE_FILE;
                if ( attr.isDirectory() )
                    {
                    if ( noAccessFolder.containsKey( fpath ) || 
                          ! Files.isExecutable( fpath ) ||
                          ! Files.isReadable(fpath ) )
                        {
                        folderType = FilesTblModel.FOLDERTYPE_FOLDER_NOACCESS;
                        }
                    else
                        {
                        folderType = FilesTblModel.FOLDERTYPE_FOLDER;
                        }
                    }
                rowList.add( folderType );

                rowList.add( fpath.toString() );
                rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
                rowList.add( attr.size() );

//                    if ( jFileFinderWin.isShowOwnerFlag() )
                    {
                    rowList.add( fsattr.owner().toString() );
//                    System.out.println( "JFIleFinder set owner =" + fsattr.owner() + "=" );
                    }
//                    if ( jFileFinderWin.isShowGroupFlag() )
                    {
                    rowList.add( fsattr.group().toString() );
                    }
//                    if ( jFileFinderWin.isShowPermsFlag() )
                    {
                    rowList.add( PosixFilePermissions.toString( fsattr.permissions() ) );
                    }

                PathsInfoList.add( rowList );
                }
            catch( NoSuchFileException nsf )
                {
                int ftype = FilesTblModel.FILETYPE_NORMAL;
                if ( Files.isSymbolicLink( fpath ) )
                    {
                    ftype = FilesTblModel.FILETYPE_LINK;
                    }
                rowList.add( ftype );  // needed to make linux work

                rowList.add( FilesTblModel.FOLDERTYPE_FILE_NOT_FOUND );
                rowList.add( fpath.toString() );
                rowList.add( Calendar.getInstance().getTime() );
                rowList.add( (long) 0 );
                rowList.add( "" );
                rowList.add( "" );
                rowList.add( "---" );
                PathsInfoList.add( rowList );
                logger.log(Level.SEVERE, nsf.toString());
                }
            catch (Exception ex) 
                {
                logger.log(Level.SEVERE, ex.toString());
                }
//        System.out.println("creationTime     = " + attr.creationTime());
//        System.out.println("lastAccessTime   = " + attr.lastAccessTime());
//        System.out.println("lastModifiedTime = " + attr.lastModifiedTime());
// 
//        System.out.println("isDirectory      = " + attr.isDirectory());
//        System.out.println("isOther          = " + attr.isOther());
//        System.out.println("isRegularFile    = " + attr.isRegularFile());
//        System.out.println("isSymbolicLink   = " + attr.isSymbolicLink());
//        System.out.println("size             = " + attr.size());
            }
        }

    public void getDosFileInfo( ArrayList<ArrayList> PathsInfoList )
        {
            for ( Path fpath : matchedPathsList )
                {
                if ( cancelFillFlag )
                    {
                    break;
                    }
                ArrayList<Object> rowList = new ArrayList<Object>();
                BasicFileAttributes attr;
                try {
                    attr = Files.readAttributes( fpath, BasicFileAttributes.class );

                    int ftype = FilesTblModel.FILETYPE_NORMAL;
                    if ( Files.isSymbolicLink( fpath ) )
                        {
                        ftype = FilesTblModel.FILETYPE_LINK;
                        }
                    else if ( attr.isOther() )
                        {
                        ftype = FilesTblModel.FILETYPE_OTHER;
                        }
    //                    rowList.add( Files.isSymbolicLink( fpath ) );  // needed to make linux work
                    rowList.add( ftype );  // needed to make linux work

    //                    rowList.add( attr.isDirectory() );
                    int folderType = FilesTblModel.FOLDERTYPE_FILE;
                    if ( attr.isDirectory() )
                        {
                        if ( noAccessFolder.containsKey( fpath ) || 
                              ! Files.isExecutable( fpath ) ||
                              ! Files.isReadable(fpath ) )
                                {
                                folderType = FilesTblModel.FOLDERTYPE_FOLDER_NOACCESS;
                                }
                            else
                                {
                                folderType = FilesTblModel.FOLDERTYPE_FOLDER;
                                }
                        }
                    rowList.add( folderType );
                
                    rowList.add( fpath.toString() );
                    rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
                    rowList.add( attr.size() );

                    DosFileAttributes fsattr = Files.readAttributes( fpath, DosFileAttributes.class );
//                    if ( jFileFinderWin.isShowOwnerFlag() )
                        {
                        try {
                            rowList.add( Files.getOwner( fpath ).toString() );
                            } 
                        catch (Exception ex) 
                            {
                            rowList.add( "unknown" );
                            if ( ! attr.isDirectory() )
                                {
                                rowList.set( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, FilesTblModel.FOLDERTYPE_FILE_NOACCESS );
                                }
                            logger.log(Level.SEVERE, ": dos owner line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ": " + ex.toString());
                            ex.printStackTrace();
                            }
                        }
//                    if ( jFileFinderWin.isShowGroupFlag() )
                        {
                        rowList.add( "" );
                        }
//                    if ( jFileFinderWin.isShowPermsFlag() )
                        {
                        try {
                            rowList.add( (fsattr.isReadOnly() ? "R" : "-") + (fsattr.isArchive() ? "A" : "-") + (fsattr.isSystem() ? "S" : "-") );
                            } 
                        catch (Exception ex) 
                            {
                            rowList.add( "???" );
                            if ( ! attr.isDirectory() )
                                {
                                rowList.set( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, FilesTblModel.FOLDERTYPE_FILE_NOACCESS );
                                }
                            logger.log(Level.SEVERE, ": dos rights line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + ex.toString());
                            }
                        }
                    
                    PathsInfoList.add( rowList );
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ": line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + ex.toString());
                    ex.printStackTrace();
                }
    //        System.out.println("creationTime     = " + attr.creationTime());
    //        System.out.println("lastAccessTime   = " + attr.lastAccessTime());
    //        System.out.println("lastModifiedTime = " + attr.lastModifiedTime());
    // 
    //        System.out.println("isDirectory      = " + attr.isDirectory());
    //        System.out.println("isOther          = " + attr.isOther());
    //        System.out.println("isRegularFile    = " + attr.isRegularFile());
    //        System.out.println("isSymbolicLink   = " + attr.isSymbolicLink());
    //        System.out.println("size             = " + attr.size());
                }
        }

    public FilesTblModel emptyFilesTableModel( Boolean countOnlyFlag ) 
        {
        synchronized( dataSyncLock ) 
            {
            System.out.println( "entered JFileFinder.emptyFilesTableModel()" );
            ArrayList<String> HeaderList = new ArrayList<String>();
            ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

            HeaderList.add( " " );
            
            ArrayList<Object> rowList = new ArrayList<Object>();
            if ( countOnlyFlag )
                {
                rowList.add( "count only." );
                }
            else
                {
                rowList.add( "filling table . . ." );
                }
            PathsInfoList.add( rowList );

            return new FilesTblModel( HeaderList, PathsInfoList );
            }
    }

    public FilesTblModel newfolderOnlyFilesTableModel( String newfolderPath )
        {
        synchronized( dataSyncLock ) 
            {
            System.out.println( "entered JFileFinder.newfolderOnlyFilesTableModel()" );
            ArrayList<String> HeaderList = new ArrayList<String>();
            ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

            HeaderList.add( "Type" );
            HeaderList.add( "Dir" );
            HeaderList.add( "File" );
            HeaderList.add( "last Modified Time" );
            HeaderList.add( "Size" );
//                if ( jFileFinderWin.isShowOwnerFlag() )
                {
                HeaderList.add( "Owner" );
                }
//                if ( jFileFinderWin.isShowGroupFlag() )
                {
                HeaderList.add( "Group" );
                }
//                if ( jFileFinderWin.isShowPermsFlag() )
                {
                HeaderList.add( "Perms" );
                }
            
            PathsInfoList.add( FilesTblModel.getNewRow( "" ) );

            return new FilesTblModel( HeaderList, PathsInfoList );
            }
    }
    
    public ResultsData getResultsData() {
        //System.out.println( "finderFileVisitor =" + finderFileVisitor + "=" );
        return this.resultsData;
    }
    
    static void usage() {
        System.out.println("java Find <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public void run( JFileFinderSwingWorker jFileFinderSwingWorker ) 
    {
        startingPathLength = startingPath.endsWith( System.getProperty( "file.separator" ) ) ? startingPath.length() : startingPath.length() + 1;
        Path startingDir = Paths.get( startingPath );

        //basePathCount = startingDir.getNameCount();
        basePathLen = startingDir.toString().length();
        
        System.out.println( "startingPath =" + startingPath + "=" );
        System.out.println( "startingDir =" + startingDir + "=" );
        System.out.println( "patternType =" + patternType + "=" );
        System.out.println( "filePattern =" + filePattern + "=" );
        System.out.println( "matching filePattern =" + (startingPath + filePattern).replace( "\\", "\\\\" ) + "=" );
        System.out.println( "basePathLen =" + basePathLen + "=" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
    
        finderFileVisitor = new FinderFileVisitor( (startingPath + filePattern).replace( "\\", "\\\\" ), this, matchedPathsList
                                                   , chainFilterList, chainFilterFolderList, chainFilterPreVisitFolderList, noAccessFolder, jFileFinderSwingWorker );
        
        try {
            synchronized( dataSyncLock ) 
                {            
                cancelFlag = false;
                cancelFillFlag = false;
                EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);

                begDate = Calendar.getInstance().getTime();
                Files.walkFileTree( startingDir, opts, Integer.MAX_VALUE, finderFileVisitor );
                endDate = Calendar.getInstance().getTime();

                resultsData = new ResultsData( cancelFlag, cancelFillFlag, finderFileVisitor.getNumTested()
                    , finderFileVisitor.getNumFileMatches(), finderFileVisitor.getNumFolderMatches() , finderFileVisitor.getNumFileTests(), finderFileVisitor.getNumFolderTests() );
                resultsData.setFilesysType(filesysType);
                
                System.out.println( "BEG: " + sdf.format( begDate ) );
                System.out.println( "END: " + sdf.format( endDate ) );
                System.out.println( "matchedPathsList size =" + matchedPathsList.size() + "=" );
                }
            } 
        catch (IOException ex)
            {
            System.out.println( "walkFileTree Error: " );
            ex.printStackTrace();
            }
        finderFileVisitor.done();
        finderFileVisitor = null;
    }
        
    public static void main(String[] args) throws IOException 
        {
//        if (args.length < 3
//            || !args[1].equals("-name"))
//            usage();

//        Path startingDir = Paths.get(args[0]);
//        String pattern = args[2];

//        startingPath = "F:/data";
//        filePattern = "*.xml";
//        startingPath = args[0];
//        filePattern = args[1];
        System.out.println("java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

        JFileFinder jfilefinder = new JFileFinder( args[0], args[1], args[2], null, null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
}    
