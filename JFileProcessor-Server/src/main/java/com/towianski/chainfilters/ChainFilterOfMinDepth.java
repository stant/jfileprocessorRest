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
public class ChainFilterOfMinDepth implements FilterChainFilter {

    int minDepth;
    
    public ChainFilterOfMinDepth()
        {
        //System.out.println( "new ChainFilterOfMinDepth()" );
        }
    
    public ChainFilterOfMinDepth( String startingFolder, String minDepth ) 
        {
        this.minDepth = Paths.get( startingFolder ).getNameCount() + Integer.parseInt( minDepth );
        //System.out.println( "Paths.get( startingFolder ).getNameCount() =" + Paths.get( startingFolder ).getNameCount() + "   Integer.parseInt( minDepth ) =" + Integer.parseInt( minDepth ) );
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        //System.out.println( "mindepth accept2 it for path =" + fpath + "   depthcount =" + fpath.getNameCount() );
//        if ( attr.isDirectory() )
//            {
            //System.out.println( " folder  >=  min =" + minDepth + "  true/false =" + (fpath.getNameCount() >= minDepth) );
            return fpath.getNameCount() >= minDepth;
//            }
//        else
//            {
//            System.out.println( " file   >  min =" + minDepth + "  true/false =" + (fpath.getNameCount() > minDepth) );
//            return fpath.getNameCount() > minDepth;
//            }
        }
}
