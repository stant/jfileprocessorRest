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
public class ChainFilterOfMaxFileCount implements FilterChainFilter {

    private static final MyLogger logger = MyLogger.getLogger( ChainFilterOfMaxFileCount.class.getName() );
    long maxFileCount;
    
    public ChainFilterOfMaxFileCount()
        {
        //logger.info( "new ChainFilterOfMaxFileCount()" );
        }
    
    public ChainFilterOfMaxFileCount( String startingFolder, String maxFileCount ) 
        {
        this.maxFileCount = Long.parseLong( maxFileCount ) - 1;
        logger.info( "Long.parseLong( maxFileCount ) =" + this.maxFileCount );
        }
    
    // These must be the same parms for all filters that get used.
    //  First check is do we show this folder?
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
//        logger.info( "for path =" + fpath + "   chainFilterArgs.getNumFileMatches() =" + chainFilterArgs.getNumFileMatches() );
        if ( chainFilterArgs.getNumFileMatches() >= maxFileCount )
            {
//            logger.info( "STOP for path =" + fpath + "   chainFilterArgs.getNumFileMatches() =" + chainFilterArgs.getNumFileMatches() );
            jFileFinder.cancelSearch();
            }
        return true;
        }
}
