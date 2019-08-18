/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class ChainFilterOfMaxDepth implements FilterChainFilter {

    int maxDepth;
    
    public ChainFilterOfMaxDepth()
        {
        //logger.info( "new ChainFilterOfMaxDepth()" );
        }
    
    public ChainFilterOfMaxDepth( String startingFolder, String maxDepth ) 
        {
        this.maxDepth = Paths.get( startingFolder ).getNameCount() + Integer.parseInt( maxDepth );
        //logger.info( "Paths.get( startingFolder ).getNameCount() =" + Paths.get( startingFolder ).getNameCount() + "   Integer.parseInt( maxDepth ) =" + Integer.parseInt( maxDepth ) );
        }
    
    // These must be the same parms for all filters that get used.
    //  First check is do we show this folder?
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        //logger.info( "maxdepth for path =" + fpath + "   depthcount =" + fpath.getNameCount() );
//        if ( attr.isDirectory() )
//            {
            //logger.info( " folder <=  max =" + maxDepth + "  true/false =" + (fpath.getNameCount() <= maxDepth) );
            return fpath.getNameCount() <= maxDepth;
//            }
//        else
//            {
//            logger.info( " file   <  max =" + maxDepth + "  true/false =" + (fpath.getNameCount() < maxDepth) );
//            return fpath.getNameCount() < maxDepth;
//            }
        }
}
