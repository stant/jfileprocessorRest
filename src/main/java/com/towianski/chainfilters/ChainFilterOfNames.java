/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import com.towianski.utils.MyLogger;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class ChainFilterOfNames implements FilterChainFilter {

    private static final MyLogger logger = MyLogger.getLogger( ChainFilterOfNames.class.getName() );
    private PathMatcher matcher;
    
    public ChainFilterOfNames()
        {
        //logger.info( "new ChainFilterOfSizes()" );
        }
    
    public ChainFilterOfNames( String patternType, String pattern ) 
        {
        logger.info( "patternType =" + patternType + "=" );
        if ( patternType.equalsIgnoreCase( "-regex" ) )
            {
            matcher = FileSystems.getDefault().getPathMatcher("regex:" + pattern);
            logger.info( "matching by regex" );
            }
        else
            {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            logger.info( "matching by glob" );
            }
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        //logger.info( "\ntest chainfilterofNames =" + fpath + "=" );
        if ( fpath.getFileName() != null && matcher.matches( fpath  ) )
            {
            return true;
            }
        return false; 
        }
}
