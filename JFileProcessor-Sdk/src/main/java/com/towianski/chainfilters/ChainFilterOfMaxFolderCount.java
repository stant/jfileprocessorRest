/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2017
 */
public class ChainFilterOfMaxFolderCount implements FilterChainFilter {

    long maxFolderCount;
    
    public ChainFilterOfMaxFolderCount()
        {
        //System.out.println( "new ChainFilterOfMaxFolderCount()" );
        }
    
    public ChainFilterOfMaxFolderCount( String startingFolder, String maxFolderCount ) 
        {
        this.maxFolderCount = Long.parseLong( maxFolderCount ) - 1;
        System.out.println( "Long.parseLong( maxFolderCount ) =" + this.maxFolderCount );
        }
    
    // These must be the same parms for all filters that get used.
    //  First check is do we show this folder?
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
//        System.out.println( "for path =" + fpath + "   chainFilterArgs.getNumFolderMatches() =" + chainFilterArgs.getNumFolderMatches() );
        if ( chainFilterArgs.getNumFolderMatches() >= maxFolderCount )
            {
//            System.out.println( "STOP for path =" + fpath + "   chainFilterArgs.getNumFolderMatches() =" + chainFilterArgs.getNumFolderMatches() );
            jFileFinder.cancelSearch();
            }
        return true;
        }
}
