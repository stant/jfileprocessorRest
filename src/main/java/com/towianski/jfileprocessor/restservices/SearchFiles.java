/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.restservices;

import com.towianski.boot.GlobalMemory;
import com.towianski.chainfilters.ChainFilterOfBoolean;
import com.towianski.chainfilters.ChainFilterOfDates;
import com.towianski.chainfilters.ChainFilterOfMaxDepth;
import com.towianski.chainfilters.ChainFilterOfMaxFileCount;
import com.towianski.chainfilters.ChainFilterOfMaxFolderCount;
import com.towianski.chainfilters.ChainFilterOfMinDepth;
import com.towianski.chainfilters.ChainFilterOfNames;
import com.towianski.chainfilters.ChainFilterOfPreVisitMaxDepth;
import com.towianski.chainfilters.ChainFilterOfPreVisitMinDepth;
import com.towianski.chainfilters.ChainFilterOfShowHidden;
import com.towianski.chainfilters.ChainFilterOfSizes;
import com.towianski.chainfilters.FilterChain;
import com.towianski.jfileprocessor.JFileFinder;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_BOTH;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_FILES_ONLY;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_FOLDERS_ONLY;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_NEITHER;
import com.towianski.models.FilesTblModel;
import com.towianski.models.ResultsData;
import com.towianski.models.SearchModel;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.util.ArrayList;

/**
 *
 * @author stan
 */
public class SearchFiles
    {
    private static final MyLogger logger = MyLogger.getLogger( SearchFiles.class.getName() );
    
    public SearchFiles()
        {
        }
    
    public ResultsData find( SearchModel searchModel )
        {
        logger.info( "jfilewin searchBtn() searchModel.getSearchBtnText() =" + searchModel.getSearchBtnText() + "=" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        stopDirWatcher();

//        if ( searchModel.getSearchBtnText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_SEARCH ) )
//            {
//            logger.info( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
////            setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
////            this.stopSearch();
//            }
//        else if ( searchModel.getSearchBtnText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_FILL ) )
//            {
//            logger.info( "hit stop fill button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
////            setProcessStatus( PROCESS_STATUS_FILL_CANCELED );
////            this.stopFill();
//            }
//        else
            {
            try {
                //int startingPathLength = (args[0].endsWith( System.getProperty( "file.separator" ) ) || args[0].endsWith( "/" ) ) ? args[0].length() - 1 : args[0].length();
                //args[0] = args[0].substring( 0, startingPathLength );
                if ( ! ( searchModel.getStartingFolder().endsWith(System.getProperty( "file.separator" ) )
                         || searchModel.getStartingFolder().endsWith( "/" ) ) ) 
                    {
                    searchModel.setStartingFolder( searchModel.getStartingFolder() + System.getProperty( "file.separator" ) );
                    }
                
                String tmp = searchModel.getStartingFolder();
  /* FIXXX 1 == 2 &&  */         if ( ! GlobalMemory.getSecUtils().hasPermission( tmp.substring( 0, (tmp.length() - 1) ), "r" ) )
                    {
                    logger.info( "No Permission to Search startingFolder =" + tmp + "=" );
                    ResultsData resultsData = new ResultsData(Boolean.FALSE, "Error", "You do not have at least 'read' rights to this folder.");
                    
                    ArrayList<String> HeaderList = new ArrayList<String>();
                    ArrayList<ArrayList> PathsInfoList = new ArrayList<ArrayList>();

                    HeaderList.add( " " );

                    ArrayList<Object> rowList = new ArrayList<Object>();
                    rowList.add( "inaccessable" );
                    PathsInfoList.add( rowList );

                    resultsData.setFilesTblModel( new FilesTblModel( HeaderList, PathsInfoList ) );
                    return resultsData;
                    }

//        if ( useGlobPattern.isSelected()
//                && ! ( searchModel.getStartingFolder().endsWith(System.getProperty( "file.separator" ) )
//                 || searchModel.getStartingFolder().endsWith( "/" ) )
//                && ! searchModel.getFilePattern().startsWith( "**" )
//                && ! (searchModel.getFilePattern().startsWith( System.getProperty( "file.separator" ) ) || searchModel.getFilePattern().startsWith( "/" )) )
//            {
//            int result = JOptionPane.showConfirmDialog( null, 
//               "There is no file separator (/ or \\ or **) between starting folder and pattern. Do you want to insert one?"
//                    ,null, JOptionPane.YES_NO_OPTION );
//            if ( result == JOptionPane.YES_OPTION )
//                {
//                filePattern.setText( System.getProperty( "file.separator" ) + searchModel.getFilePattern() );
//                searchModel.setFilePattern( filePattern.getText() );
//                }
//            }
                
                logger.info( "tabsLogic button.getText() =" + (searchModel.getTabsLogicType()) + "=" );
                FilterChain chainFilterList = new FilterChain( searchModel.getTabsLogicType() );
                FilterChain chainFilterFolderList = new FilterChain( searchModel.getTabsLogicType() );
                FilterChain chainFilterPreVisitFolderList = new FilterChain( searchModel.getTabsLogicType() );

                try {
                    if ( ! searchModel.getFilePattern().equals( "" ) )
                        {
                        logger.info( "add filter of names!" );
                        ChainFilterOfNames chainFilterOfNames = new ChainFilterOfNames( searchModel.getPatternType(), (searchModel.getStartingFolder() + searchModel.getFilePattern()).replace( "\\", "\\\\" ) );
                        if ( searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY )  ||
                             searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_BOTH ) )
                            {
                            chainFilterFolderList.addFilter( chainFilterOfNames );
                            }
                        if ( searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_FILES_ONLY )  ||
                             searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_BOTH ) )
                            {
                            chainFilterList.addFilter( chainFilterOfNames );
                            }
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    logger.info( "searchModel.getShowFilesFoldersType() =" + searchModel.getShowFilesFoldersType() + "=" );
                    if ( searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_FILES_ONLY ) 
                         || searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        logger.info( "add filter Boolean False for folders" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterFolderList.addFilter( chainFilterOfBoolean );
                        }
                    if ( searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY ) 
                         || searchModel.getShowFilesFoldersType().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        logger.info( "add filter Boolean False for files" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterList.addFilter( chainFilterOfBoolean );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Boolean filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    if ( ! searchModel.getSize1().equals( "" ) )
                        {
                        logger.info( "add filter of sizes!" );
                        ChainFilterOfSizes chainFilterOfSizes = new ChainFilterOfSizes( searchModel.getSize1Op(), searchModel.getSize1(), searchModel.getSizeLogicOp(), searchModel.getSize2Op(), searchModel.getSize2() );
                        chainFilterList.addFilter( chainFilterOfSizes );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Size filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    if ( searchModel.getDate1() != null )
                        {
                        logger.info( "add filter of dates!" );
                        logger.info( "selected date =" + searchModel.getDate1() + "=" );
                        ChainFilterOfDates chainFilterOfDates = new ChainFilterOfDates( searchModel.getDate1Op(), searchModel.getDate1(), searchModel.getDateLogicOp(), searchModel.getDate2Op(), searchModel.getDate2() );
                        chainFilterList.addFilter( chainFilterOfDates );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Date filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    if ( ! searchModel.getMaxDepth().equals( "" ) )
                        {
                        logger.info( "add filter of maxdepth!" );
                        logger.info( "selected maxdepth =" + searchModel.getMaxDepth() + "=" );
                        ChainFilterOfMaxDepth chainFilterOfMaxDepth = new ChainFilterOfMaxDepth( searchModel.getStartingFolder(), searchModel.getMaxDepth() );
                        chainFilterFolderList.addFilter( chainFilterOfMaxDepth );
                        chainFilterList.addFilter( chainFilterOfMaxDepth );
                        ChainFilterOfPreVisitMaxDepth chainFilterOfPreVisitMaxDepth = new ChainFilterOfPreVisitMaxDepth( searchModel.getStartingFolder(), searchModel.getMaxDepth() );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMaxDepth );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    if ( ! searchModel.getStopFileCount().equals( "" ) )
                        {
                        logger.info( "add filter of stopFileCount!" );
                        logger.info( "selected stopFileCount =" + searchModel.getStopFileCount() + "=" );
                        ChainFilterOfMaxFileCount chainFilterOfMaxFileCount = new ChainFilterOfMaxFileCount( searchModel.getStartingFolder(), searchModel.getStopFileCount() );
                        chainFilterList.addFilter( chainFilterOfMaxFileCount );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in stopFileCount filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    if ( ! searchModel.getStopFolderCount().equals( "" ) )
                        {
                        logger.info( "add filter of stopFolderCount!" );
                        logger.info( "selected stopFolderCount =" + searchModel.getStopFolderCount() + "=" );
                        ChainFilterOfMaxFolderCount chainFilterOfMaxFolderCount = new ChainFilterOfMaxFolderCount( searchModel.getStartingFolder(), searchModel.getStopFolderCount() );
                        chainFilterFolderList.addFilter( chainFilterOfMaxFolderCount );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in stopFolderCount filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                try {
                    if ( ! searchModel.getMinDepth().equals( "" ) )
                        {
                        logger.info( "add filter of minDepth!" );
                        logger.info( "selected minDepth =" + searchModel.getMinDepth() + "=" );
                        ChainFilterOfMinDepth chainFilterOfMinDepth = new ChainFilterOfMinDepth( searchModel.getStartingFolder(), searchModel.getMinDepth() );
                        chainFilterFolderList.addFilter( chainFilterOfMinDepth );
                        chainFilterList.addFilter( chainFilterOfMinDepth );
                        ChainFilterOfPreVisitMinDepth chainFilterOfPreVisitMinDepth = new ChainFilterOfPreVisitMinDepth( searchModel.getStartingFolder(), searchModel.getMinDepth() );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMinDepth );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }
                
                try {
                    if ( ! searchModel.isShowHiddenFilesFlag() )
                        {
                        logger.info( "add filter of do not show hidden files!" );
                        ChainFilterOfShowHidden filter = new ChainFilterOfShowHidden( false );
                        chainFilterList.addFilter( filter );
                        chainFilterFolderList.addFilter( filter );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    new ResultsData();
                    }

                // if it matters for speed I could pass countOnlyFlag to jFileFinder too and not create the arrayList of paths !
                JFileFinder jfilefinder = null;
                jfilefinder = new JFileFinder( searchModel.getStartingFolder(), searchModel.getPatternType(), searchModel.getFilePattern(), chainFilterList, chainFilterFolderList, chainFilterPreVisitFolderList );
//                jFileFinderSwingWorker = new JFileFinderSwingWorker( this, jfilefinder, searchModel.getStartingFolder(), searchModel.getPatternType(), searchModel.getFilePattern(), countOnlyFlag );
    
                logger.info( "*************  jFileFinderSwingWorker.execute()  ****************" );
            
//                jFileFinderSwingWorker.execute();   //doInBackground();
                return getRecords( jfilefinder );
            } 
            catch (Exception ex) {
//                logger.severe( null, ex);
            }
        }
        return new ResultsData();
    }
    
    public ResultsData getRecords( JFileFinder jfilefinder ) 
        {
//        public ResultsData doInBackground() {
        logger.info( "JFileFinderSwingWorker.doInBackground() before jfilefinder.run()" );
//        stopDirWatcher();
//        jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_STARTED );
//        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        jfilefinder.run( null );
        ResultsData resultsData = jfilefinder.getResultsData();   //get();
        jfilefinder.getFilesTableModel();
//        Rest.saveObjectToFile( "resultsData.getFilesTblModel.json", resultsData.getFilesTblModel() );
//        Rest.saveObjectToFile( "resultsData.json", resultsData );

        //publish("Listing all text files under the directory: ");
//        return jfilefinder.getResultsData();
//    }
//
        try {
//            logger.info( "entered JFileFinderSwingWorker.done()" );
//            logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//            ResultsData resultsData = jfilefinder.getResultsData();   //get();
            //logger.info( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();
//            NumberFormat numFormat = NumberFormat.getIntegerInstance();
//            String partialMsg = "";
//            if ( resultsData.getSearchWasCanceled() )
//                {
//                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_CANCELED );
//                partialMsg = " PARTIAL files list.";
//                }
//            else
//                {
//                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_COMPLETED );
//                }
//            jFileFinderWin.setMessage( "Matched " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) 
//                    + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.  Total "
//                    + numFormat.format( resultsData.getFilesVisited() ) + partialMsg );

            logger.info( "exiting FillTableModelSwingWorker.done()" );
            
//            jFileFinderWin.releaseSearchLock();
            logger.info( "exiting JFileFinderSwingWorker.done()" );
        }
        catch (Exception e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            logger.info( "Error retrieving file: " + why);
            e.printStackTrace();
            }
        
        logger.info( "will return resultsData =" + Rest.saveObjectToString( resultsData ) );
        return resultsData;
        }

    }

