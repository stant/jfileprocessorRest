/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2017
 */
public class ChainFilterOfMaxFolderCount implements FilterChainFilter {

    private static final MyLogger logger = MyLogger.getLogger( ChainFilterOfMaxFolderCount.class.getName() );

    long maxFolderCount;
    
    public ChainFilterOfMaxFolderCount()
        {
        //logger.info( "new ChainFilterOfMaxFolderCount()" );
        }
    
    public ChainFilterOfMaxFolderCount( String startingFolder, String maxFolderCount ) 
        {
        this.maxFolderCount = Long.parseLong( maxFolderCount ) - 1;
        logger.info( "Long.parseLong( maxFolderCount ) =" + this.maxFolderCount );
        }
    
    // These must be the same parms for all filters that get used.
    //  First check is do we show this folder?
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
//        logger.info( "for path =" + fpath + "   chainFilterArgs.getNumFolderMatches() =" + chainFilterArgs.getNumFolderMatches() );
        if ( chainFilterArgs.getNumFolderMatches() >= maxFolderCount )
            {
//            logger.info( "STOP for path =" + fpath + "   chainFilterArgs.getNumFolderMatches() =" + chainFilterArgs.getNumFolderMatches() );
            jFileFinder.cancelSearch();
            }
        return true;
        }
}
