/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.chainfilters.ChainFilterArgs;
import com.towianski.chainfilters.FilterChain;
import com.towianski.jfileprocessor.JFileFinder;
import com.towianski.jfileprocessor.JFileFinderSwingWorker;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author stan
 */
public class FinderFileVisitor extends SimpleFileVisitor<Path>
        {
        private static final MyLogger logger = MyLogger.getLogger( FinderFileVisitor.class.getName() );
    
        private long numFileMatches = 0;
        private long numFolderMatches = 0;
        private long numFileTests = 0;
        private long numFolderTests = 0;
        private long numTested = 0;
        private ChainFilterArgs chainFilterArgs = new ChainFilterArgs();
        private JFileFinder jFileFinder = null;
        private FilterChain chainFilterList = null;
        private FilterChain chainFilterFolderList = null;
        private FilterChain chainFilterPreVisitFolderList = null;
        private ArrayList<Path> matchedPathsList = new ArrayList<Path>();
        private HashMap<Path, String> noAccessFolder = new HashMap<Path, String>();
        private boolean cancelFlag = false;
        private JFileFinderSwingWorker jFileFinderSwingWorker;
        
        public FinderFileVisitor( String pattern, JFileFinder jFileFinder, ArrayList<Path> matchedPathsList, FilterChain chainFilterList, FilterChain chainFilterFolderList, FilterChain chainFilterPreVisitFolderList
                                  , HashMap<Path, String> noAccessFolder, JFileFinderSwingWorker jFileFinderSwingWorker ) 
            {
            chainFilterArgs.setNumFileMatches(numFileMatches);
            chainFilterArgs.setNumFolderMatches(numFolderMatches);
            chainFilterArgs.setNumFileTests(numFileTests);
            chainFilterArgs.setNumFolderTests(numFolderTests);
            chainFilterArgs.setNumTested(numTested);
            this.jFileFinder = jFileFinder;
//            matchedPathsList = new ArrayList<Path>();
            this.matchedPathsList = matchedPathsList;
            this.chainFilterList = chainFilterList;
            this.chainFilterFolderList = chainFilterFolderList;
            this.chainFilterPreVisitFolderList = chainFilterPreVisitFolderList;
            this.noAccessFolder = noAccessFolder;
            this.jFileFinderSwingWorker = jFileFinderSwingWorker;
        }

        public void cancelSearch()
            {
            cancelFlag = true;
            }

        
        Boolean processFile( Path file, BasicFileAttributes attrs )
            {
            numFileTests++;
            numTested++;
            chainFilterArgs.setNumFileMatches(numFileMatches);
            if ( jFileFinderSwingWorker != null )  jFileFinderSwingWorker.publish2( numTested );

            if ( chainFilterList != null )
                {
                //BasicFileAttributes attrs;
                try {
                    //attrs = Files.readAttributes( file, BasicFileAttributes.class );
                    if ( chainFilterList.testFilters( file, attrs, chainFilterArgs, jFileFinder ) )
                        {
//                      rowList.add( fpath.toString() );
//                      rowList.add( new Date( attr.lastModifiedTime().toMillis() ) );
//                      rowList.add( attr.size() );
//                      rowList.add( attr.isDirectory() );
//                      rowList.add( attr.isSymbolicLink() );
                        numFileMatches++;
//                      logger.info( "Match =" + file );
                        matchedPathsList.add( file );
//                      int at = pathStr.indexOf( System.getProperty( "file.separator" ), startingPathLength );
//                      logger.info( "search from string =" + pathStr.substring(startingPathLength) + "=" );
//                      logger.info( "startingPathLength =" + startingPathLength );
//                      logger.info( "path separator >" + System.getProperty( "file.separator" ) + "<" );
//                      logger.info( "at =" + at );
//                      String pkgpath = pathStr.substring( startingPathLength, at );
                        }
                    } 
                catch (Exception ex) 
                    {
                    logger.severeExc( ex);
                    return false;
                    }
                }
            else
                {
                numFileMatches++;
                matchedPathsList.add( file );
                }
            return true;
            }
        

        Boolean processFolder( Path fpath, BasicFileAttributes attrs )
            {
            numFolderTests++;
            numTested++;
            chainFilterArgs.setNumFolderMatches(numFolderMatches);
            if ( chainFilterFolderList != null )
                {
                try {
//                    logger.info( "folder match ? =" + fpath.toString() );
//                    logger.info( "folder match result = " + chainFilterFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) );
                    if ( chainFilterFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) )
                        {
                        numFolderMatches++;
//                        logger.info( "Match =" + fpath + "   numFolderMatches =" + numFolderMatches );
                        matchedPathsList.add( fpath );
                        }
                    }
                catch (Exception ex) 
                    {
                    logger.severeExc( ex);
                    return false;
                    }
                }
            else
                {
                numFolderMatches++;
//                logger.info( "no filter Match =" + fpath + "   numFolderMatches =" + numFolderMatches );
                matchedPathsList.add( fpath );
                }
            return true;
            }
        
        // Prints the total number of
        // matches to standard out.
        public void done() {
            logger.info( "Tested:  " + numTested );
            logger.info( "Matched Files count: " + numFileMatches );
            logger.info( "Matched Folders count: " + numFolderMatches );

//            for ( Path mpath : matchedPathsList )
//                {
//                logger.info( mpath );
//                }
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile( Path fpath, BasicFileAttributes attrs )
            {
            try
                {
                if ( cancelFlag )
                    {
                    logger.info( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                //logger.info( "test file ? =" + fpath.toString() );
                processFile( fpath, attrs );
                }
            catch( Exception ex )
                {
                logger.info( "Error parsing file =" + fpath + "=" );
                return FileVisitResult.TERMINATE;
                }
            return FileVisitResult.CONTINUE;
            }


        @Override
        public FileVisitResult preVisitDirectory( Path fpath, BasicFileAttributes attrs )
            {
//            logger.info( "preVisitDirectory() chainFilterFolderlist.size() =" + chainFilterFolderList.size() + "=" );
//            logger.info( "preVisitDirectory() chainFilterPreVisitFolderList.size() =" + chainFilterPreVisitFolderList.size() + "=" );

            //  First check is do we show this folder?
            try {
                if ( cancelFlag )
                    {
                    logger.info( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                processFolder( fpath, attrs );
                }
            catch (Exception ex) 
                {
                logger.severeExc( ex);
                logger.info( "preVisitDirectory Exception: " + ex.toString() );
                }

            // Second check is do we go into this folder or skip it?
            if ( chainFilterPreVisitFolderList != null )
                {
                try {
                    //logger.info( "previsit folder ? =" + fpath.toString() );
                    if ( chainFilterPreVisitFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) )
                        {
                        return CONTINUE;
                        }
                    else
                        {
                        logger.info( "SKIP folder =" + fpath.toString() );
                        return FileVisitResult.SKIP_SUBTREE;
                        }
                    }
                catch (Exception ex) 
                    {
                    logger.severeExc( ex);
                    }
                }
            return FileVisitResult.SKIP_SUBTREE;
            }

        // Print each directory visited.
//        @Override
//        public FileVisitResult postVisitDirectory( Path dir, IOException exc )
//            {
            //System.out.format("Directory: %s%n", dir);
//            BasicFileAttributes attrs;
//            try {
//                attrs = Files.readAttributes( dir, BasicFileAttributes.class );
//                if ( cancelFlag )
//                    {
//                    logger.info( "Search cancelled by user." );
//                    return FileVisitResult.TERMINATE;
//                    }
//                processFolder( dir, attrs );
//                }
//            catch (Exception ex) 
//                {
//                logger.severeExc( ex );
//                }
//            return CONTINUE;
//            }

        @Override
        public FileVisitResult visitFileFailed( Path file, IOException exc ) 
            {
            //logger.info( exc + "  for file =" + file.toString() );
            if ( new File( file.toString() ).isDirectory() )
                {
                logger.info( "skipping inaccessible folder: " + file.toString() );
                if ( exc instanceof java.nio.file.AccessDeniedException )
                    {
                    BasicFileAttributes attrs;
                    try {
                        attrs = Files.readAttributes( file, BasicFileAttributes.class );
                        processFolder( file, attrs );
                        noAccessFolder.put( file, null );
            }
                    catch (Exception ex) 
                        {
                        logger.info( "Error calling processFolder in visitFileFailed()" );
                        ex.printStackTrace();
                        }
                    }
                else
                    {
                    exc.printStackTrace();
                    }
                return FileVisitResult.SKIP_SUBTREE;
                }
            return CONTINUE;
            }
            
        public long getNumTested()
            {
            return numTested;
            }
            
        public long getNumFileMatches()
            {
            return numFileMatches;
            }
            
        public long getNumFolderMatches()
            {
            return numFolderMatches;
            }

        public long getNumFileTests() {
            return numFileTests;
        }

        public long getNumFolderTests() {
            return numFolderTests;
        }
        
        } // class Finder

