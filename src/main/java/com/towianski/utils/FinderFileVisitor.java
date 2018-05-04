/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.chainfilters.ChainFilterArgs;
import com.towianski.chainfilters.FilterChain;
import com.towianski.jfileprocessor.JFileFinder;
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
import java.util.logging.Level;

/**
 *
 * @author stan
 */
public class FinderFileVisitor extends SimpleFileVisitor<Path>
        {
        private final static MyLogger logger = MyLogger.getLogger( JFileFinder.class.getName() );
    
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

        
        public FinderFileVisitor( String pattern, JFileFinder jFileFinder, ArrayList<Path> matchedPathsList, FilterChain chainFilterList, FilterChain chainFilterFolderList, FilterChain chainFilterPreVisitFolderList
                                  , HashMap<Path, String> noAccessFolder ) 
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
//                      System.out.println( "Match =" + file );
                        matchedPathsList.add( file );
//                      int at = pathStr.indexOf( System.getProperty( "file.separator" ), startingPathLength );
//                      System.out.println( "search from string =" + pathStr.substring(startingPathLength) + "=" );
//                      System.out.println( "startingPathLength =" + startingPathLength );
//                      System.out.println( "path separator >" + System.getProperty( "file.separator" ) + "<" );
//                      System.out.println( "at =" + at );
//                      String pkgpath = pathStr.substring( startingPathLength, at );
                        }
                    } 
                catch (Exception ex) 
                    {
                    logger.log(Level.SEVERE, null, ex);
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
//                    System.out.println( "folder match ? =" + fpath.toString() );
//                    System.out.println( "folder match result = " + chainFilterFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) );
                    if ( chainFilterFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) )
                        {
                        numFolderMatches++;
//                        System.out.println( "Match =" + fpath + "   numFolderMatches =" + numFolderMatches );
                        matchedPathsList.add( fpath );
                        }
                    }
                catch (Exception ex) 
                    {
                    logger.log(Level.SEVERE, null, ex);
                    return false;
                    }
                }
            else
                {
                numFolderMatches++;
//                System.out.println( "no filter Match =" + fpath + "   numFolderMatches =" + numFolderMatches );
                matchedPathsList.add( fpath );
                }
            return true;
            }
        
        // Prints the total number of
        // matches to standard out.
        public void done() {
            System.out.println( "Tested:  " + numTested );
            System.out.println( "Matched Files count: " + numFileMatches );
            System.out.println( "Matched Folders count: " + numFolderMatches );

//            for ( Path mpath : matchedPathsList )
//                {
//                System.out.println( mpath );
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
                    System.out.println( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                //System.out.println( "test file ? =" + fpath.toString() );
                processFile( fpath, attrs );
                }
            catch( Exception ex )
                {
                System.out.println( "Error parsing file =" + fpath + "=" );
                return FileVisitResult.TERMINATE;
                }
            return FileVisitResult.CONTINUE;
            }


        @Override
        public FileVisitResult preVisitDirectory( Path fpath, BasicFileAttributes attrs )
            {
//            System.out.println( "preVisitDirectory() chainFilterFolderlist.size() =" + chainFilterFolderList.size() + "=" );
//            System.out.println( "preVisitDirectory() chainFilterPreVisitFolderList.size() =" + chainFilterPreVisitFolderList.size() + "=" );

            //  First check is do we show this folder?
            try {
                if ( cancelFlag )
                    {
                    System.out.println( "Search cancelled by user." );
                    return FileVisitResult.TERMINATE;
                    }
                processFolder( fpath, attrs );
                }
            catch (Exception ex) 
                {
                logger.log(Level.SEVERE, null, ex);
                }

            // Second check is do we go into this folder or skip it?
            if ( chainFilterPreVisitFolderList != null )
                {
                try {
                    //System.out.println( "previsit folder ? =" + fpath.toString() );
                    if ( chainFilterPreVisitFolderList.testFilters( fpath, attrs, chainFilterArgs, jFileFinder ) )
                        {
                        return CONTINUE;
                        }
                    else
                        {
                        System.out.println( "SKIP folder =" + fpath.toString() );
                        return FileVisitResult.SKIP_SUBTREE;
                        }
                    }
                catch (Exception ex) 
                    {
                    logger.log(Level.SEVERE, null, ex);
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
//                    System.out.println( "Search cancelled by user." );
//                    return FileVisitResult.TERMINATE;
//                    }
//                processFolder( dir, attrs );
//                }
//            catch (Exception ex) 
//                {
//                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            return CONTINUE;
//            }

        @Override
        public FileVisitResult visitFileFailed( Path file, IOException exc ) 
            {
            //System.out.println( exc + "  for file =" + file.toString() );
            if ( new File( file.toString() ).isDirectory() )
                {
                System.out.println( "skipping inaccessible folder: " + file.toString() );
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
                        System.out.println( "Error calling processFolder in visitFileFailed()" );
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

