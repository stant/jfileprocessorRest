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
public class ChainFilterOfSizes implements FilterChainFilter {

    String op1 = "";
    long setSize1 = 0;
    String logicOp = "";
    String op2 = "";
    long setSize2 = 0;
    char doLogic = '1';
    
    public ChainFilterOfSizes()
        {
        //System.out.println( "new ChainFilterOfSizes()" );
        }
    
    public ChainFilterOfSizes( String op1, String setSize1, String logicOp, String op2, String setSize2 ) 
        {
        this.op1 = op1;
        this.setSize1 = Long.parseLong( setSize1 );
        this.logicOp = logicOp;
        
        if ( logicOp.trim().equalsIgnoreCase( "AND" ) )
            {
            this.doLogic = 'A';
            this.op2 = op2;
            this.setSize2 = Long.parseLong( setSize2 );
            }
        else if ( logicOp.trim().equalsIgnoreCase( "OR" ) )
            {
            this.doLogic = 'O';
            this.op2 = op2;
            this.setSize2 = Long.parseLong( setSize2 );
            }
        else
            {
            this.doLogic = '1';
            }
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        long size = attr.size();
        //System.out.println( "entered test for FilterOfSizes() - test size: " + size + " compared to setsize:" + setSize1 );
        if ( doLogic == '1' )
            {
            return acceptOp1( fpath, attr );
            }
        else if ( doLogic == 'A' )
            {
            return acceptOp1( fpath, attr ) && acceptOp2( fpath, attr );
            }
        else if ( doLogic == 'O' )
            {
            return acceptOp1( fpath, attr ) || acceptOp2( fpath, attr );
            }
        return false; 
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean acceptOp1( Path fpath, BasicFileAttributes attr )
        {
        long size = attr.size();
        //System.out.println( "entered test for FilterOfSizes() - test size: " + size + " compared to setsize:" + setSize1 );
        if ( op1.equals( "<" ) && size < setSize1 )
            {
            //System.out.println( "true <" );
            return true;
            }
        else if ( op1.equals( "<=" ) && size <= setSize1 )
            {
            //System.out.println( "true >" );
            return true;
            }
        else if ( op1.equals( "=" ) && size == setSize1 )
            {
            return true;
            }
        else if ( op1.equals( "!=" ) && size != setSize1 )
            {
            return true;
            }
        else if ( op1.equals( ">" ) && size > setSize1 )
            {
            return true;
            }
        else if ( op1.equals( ">=" ) && size >= setSize1 )
            {
            return true;
            }
        //System.out.println( "false" );
        return false; 
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean acceptOp2( Path fpath, BasicFileAttributes attr )
        {
        long size = attr.size();
        //System.out.println( "entered test for FilterOfSizes() - test size: " + size + " compared to setsize:" + setSize1 );
        if ( op2.equals( "<" ) && size < setSize2 )
            {
            //System.out.println( "true <" );
            return true;
            }
        else if ( op2.equals( "<=" ) && size <= setSize2 )
            {
            //System.out.println( "true >" );
            return true;
            }
        else if ( op2.equals( "=" ) && size == setSize2 )
            {
            return true;
            }
        else if ( op2.equals( "!=" ) && size != setSize2 )
            {
            return true;
            }
        else if ( op2.equals( ">" ) && size > setSize2 )
            {
            return true;
            }
        else if ( op2.equals( ">=" ) && size >= setSize2 )
            {
            return true;
            }
        //System.out.println( "false" );
        return false; 
        }
}
