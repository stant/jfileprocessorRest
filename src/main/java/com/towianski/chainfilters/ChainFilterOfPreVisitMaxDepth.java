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
public class ChainFilterOfPreVisitMaxDepth implements FilterChainFilter {

    int maxDepth;
    
    public ChainFilterOfPreVisitMaxDepth()
        {
        //System.out.println( "new ChainFilterOfMaxDepth()" );
        }
    
    public ChainFilterOfPreVisitMaxDepth( String startingFolder, String maxDepth ) 
        {
        this.maxDepth = Paths.get( startingFolder ).getNameCount() + Integer.parseInt( maxDepth );
        //System.out.println( "preVisit Paths.get( startingFolder ).getNameCount() =" + Paths.get( startingFolder ).getNameCount() + "   Integer.parseInt( maxDepth ) =" + Integer.parseInt( maxDepth ) );
        }
    
    // These must be the same parms for all filters that get used.
    // Second check is do we go into this folder or skip it?
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        //System.out.println( "maxdepth for path =" + fpath + "   depthcount =" + fpath.getNameCount() );
        //System.out.println( " <  max =" + maxDepth + "  true/false =" + (fpath.getNameCount() < maxDepth) );
        return fpath.getNameCount() < maxDepth;
        }
}
