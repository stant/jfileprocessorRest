package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ResultsData;
import com.towianski.models.FilesTblModel;
import com.towianski.chainfilters.FilterChain;
import com.towianski.models.Constants;
import static com.towianski.models.FilesTblModel.FILETYPE_NORMAL;
import static com.towianski.models.FilesTblModel.FOLDERTYPE_FOLDER;
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
    private static final MyLogger logger = MyLogger.getLogger( JFileFinder.class.getName() );

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
        
        logger.info( "JFIleFinder constructor() with chainFilterList.size()               =" + chainFilterList.size() + "=" );
        logger.info( "JFIleFinder constructor() with chainFilterFolderList.size()         =" + chainFilterFolderList.size() + "=" );
        logger.info( "JFIleFinder constructor() with chainFilterPreVisitFolderList.size() =" + chainFilterPreVisitFolderList.size() + "=" );
        
        logger.setLevel( Level.SEVERE );
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        if ( finderFileVisitor != null )
            {
            finderFileVisitor.cancelSearch();
            }
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

//            logger.info( "getFilesTableModel PathsInfoList.size() =" + PathsInfoList.size() + "=" );
//            System.err.println( "getFilesTableModel PathsInfoList.size() =" + PathsInfoList.size() + "=" );
            if ( PathsInfoList.size() < 1 )
                {
                ArrayList<Object> newRow = new ArrayList<Object>();
                newRow.add( FILETYPE_NORMAL );
                newRow.add( FOLDERTYPE_FOLDER  );
                if ( noAccessFolder.size() > 0 )
                    {
                    newRow.add( "Inaccessible" );
//   FIXXX                 jFileFinderWin.stopDirWatcher();
                    }
                else
                    {
                    newRow.add( "No Files Found" );
                    }
                newRow.add( Calendar.getInstance().getTime() );
                newRow.add( (long) 0 );
                newRow.add( "" );
                newRow.add( "" );
                newRow.add( "---" );
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
                logger.finer( "lookin at file = " + fpath.toString() );
                attr = Files.readAttributes( fpath, BasicFileAttributes.class );
                PosixFileAttributes fsattr = Files.readAttributes( fpath, PosixFileAttributes.class );
//                logger.info( "isDirectory      = " + attr.isDirectory());

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
//                    logger.info( "JFIleFinder set owner =" + fsattr.owner() + "=" );
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
                logger.severe( nsf.toString());
                nsf.printStackTrace();
                }
            catch (Exception ex) 
                {
                logger.severe( ex.toString());
                }
//        logger.info( "creationTime     = " + attr.creationTime());
//        logger.info( "lastAccessTime   = " + attr.lastAccessTime());
//        logger.info( "lastModifiedTime = " + attr.lastModifiedTime());
// 
//        logger.info( "isDirectory      = " + attr.isDirectory());
//        logger.info( "isOther          = " + attr.isOther());
//        logger.info( "isRegularFile    = " + attr.isRegularFile());
//        logger.info( "isSymbolicLink   = " + attr.isSymbolicLink());
//        logger.info( "size             = " + attr.size());
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
                            logger.severe( ": dos owner line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ": " + ex.toString());
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
                            logger.severe( ": dos rights line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + ex.toString());
                            }
                        }
                    
                    PathsInfoList.add( rowList );
                } catch (Exception ex) {
                    logger.severe( ": line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": " + ex.toString());
                    ex.printStackTrace();
                }
    //        logger.info( "creationTime     = " + attr.creationTime());
    //        logger.info( "lastAccessTime   = " + attr.lastAccessTime());
    //        logger.info( "lastModifiedTime = " + attr.lastModifiedTime());
    // 
    //        logger.info( "isDirectory      = " + attr.isDirectory());
    //        logger.info( "isOther          = " + attr.isOther());
    //        logger.info( "isRegularFile    = " + attr.isRegularFile());
    //        logger.info( "isSymbolicLink   = " + attr.isSymbolicLink());
    //        logger.info( "size             = " + attr.size());
                }
        }

    public FilesTblModel emptyFilesTableModel( Boolean countOnlyFlag ) 
        {
        synchronized( dataSyncLock ) 
            {
            logger.info( "entered JFileFinder.emptyFilesTableModel()" );
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
            logger.info( "entered JFileFinder.newfolderOnlyFilesTableModel()" );
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
        //logger.info( "finderFileVisitor =" + finderFileVisitor + "=" );
        return this.resultsData;
    }
    
    static void usage() {
        logger.info( "java Find <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public ArrayList<Path> getMatchedPathsList() {
        return matchedPathsList;
    }

    public void setMatchedPathsList(ArrayList<Path> matchedPathsList) {
        this.matchedPathsList = matchedPathsList;
    }

    public void run( JFileFinderSwingWorker jFileFinderSwingWorker ) 
    {
        startingPathLength = startingPath.endsWith( System.getProperty( "file.separator" ) ) ? startingPath.length() : startingPath.length() + 1;
        Path startingDir = Paths.get( startingPath );

        //basePathCount = startingDir.getNameCount();
        basePathLen = startingDir.toString().length();
        
        logger.info( "startingPath =" + startingPath + "=" );
        logger.info( "startingDir =" + startingDir + "=" );
        logger.info( "patternType =" + patternType + "=" );
        logger.info( "filePattern =" + filePattern + "=" );
        logger.info( "matching filePattern =" + (startingPath + filePattern).replace( "\\", "\\\\" ) + "=" );
        logger.info( "basePathLen =" + basePathLen + "=" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
    
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
                
                logger.info( "BEG: " + sdf.format( begDate ) );
                logger.info( "END: " + sdf.format( endDate ) );
                logger.info( "matchedPathsList size =" + matchedPathsList.size() + "=" );
                }
            } 
        catch (IOException ex)
            {
            logger.info( "walkFileTree Error: " );
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
        logger.info( "java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

        JFileFinder jfilefinder = new JFileFinder( args[0], args[1], args[2], null, null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            logger.severeExc( ex );
//        }
        }
}    
