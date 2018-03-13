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
 * @author Stan Towianski - June 2015
 */
public class ChainFilterOfBoolean implements FilterChainFilter {

    private Boolean flag = true;
    
    public ChainFilterOfBoolean()
        {
        //System.out.println( "new ChainFilterOfSizes()" );
        }
    
    public ChainFilterOfBoolean( Boolean flag ) 
        {
        this.flag = flag;
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        //System.out.println( "\ntest chainfilterofNames =" + fpath + "=" );
        return flag; 
        }
}
