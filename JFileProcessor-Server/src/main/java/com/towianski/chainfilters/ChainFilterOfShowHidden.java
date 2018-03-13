/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Stan Towianski - June 2017
 */
public class ChainFilterOfShowHidden implements FilterChainFilter {

    boolean showHidden;
    
    public ChainFilterOfShowHidden()
        {
        //System.out.println( "new ChainFilterOfMaxFileCount()" );
        }
    
    public ChainFilterOfShowHidden( boolean showHidden ) 
        {
        this.showHidden = showHidden;
//        System.out.println( "showHidden =" + this.showHidden );
        }
    
    // These must be the same parms for all filters that get used.
    //  First check is do we show this folder?
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
//        System.out.println( "for path =" + fpath + "   chainFilterArgs.getNumFileMatches() =" + chainFilterArgs.getNumFileMatches() );
        try {
            return ! Files.isHidden( fpath );    // ASSUMING ALWAYS SHOWS HIDDEN BY DEFAULT SO ONLY USING THIS TO not SHOW HIDDEN.
            }
        catch( Exception exc )
            {
            return false;
            }
        }
}
