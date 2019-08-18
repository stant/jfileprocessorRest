/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class ChainFilterOfDates implements FilterChainFilter {

    String op1 = "";
    long setDate1 = 0;
    String logicOp = "";
    String op2 = "";
    long setDate2 = 0;
    char doLogic = '1';
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" );
    
    public ChainFilterOfDates()
        {
        //logger.info( "new ChainFilterOfSizes()" );
        }
    
    public ChainFilterOfDates( String op1, Date setDate1, String logicOp, String op2, Date setDate2 ) 
        {
        this.op1 = op1;
        this.setDate1 = Long.parseLong( sdf.format( setDate1 ) );
        this.logicOp = logicOp;
        
        if ( logicOp.trim().equalsIgnoreCase( "AND" ) )
            {
            this.doLogic = 'A';
            this.op2 = op2;
            this.setDate2 = Long.parseLong( sdf.format( setDate2 ) );
            }
        else if ( logicOp.trim().equalsIgnoreCase( "OR" ) )
            {
            this.doLogic = 'O';
            this.op2 = op2;
            this.setDate2 = Long.parseLong( sdf.format( setDate2 ) );
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
        //logger.info( "entered test for FilterOfSizes() - test size: " + size + " compared to setsize:" + setDate1 );
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
        Date tdate = new Date( attr.lastModifiedTime().toMillis() );
        long testDate = Long.parseLong( sdf.format( tdate ) );
        //logger.info( "entered test for FilterOfDates() - op1 =" + op1 + "   test date 1: " + testDate + " compared to setdate:" + setDate1 );
        
        if ( op1.equals( "<" ) && testDate < setDate1 )
            {
            //logger.info( "true <" );
            return true;
            }
        else if ( op1.equals( "<=" ) && testDate <= setDate1 )
            {
            //logger.info( "true >" );
            return true;
            }
        else if ( op1.equals( "=" ) && testDate == setDate1 )
            {
            return true;
            }
        else if ( op1.equals( "!=" ) && testDate != setDate1 )
            {
            return true;
            }
        else if ( op1.equals( ">" ) && testDate > setDate1 )
            {
            return true;
            }
        else if ( op1.equals( ">=" ) && testDate >= setDate1 )
            {
            return true;
            }
        //logger.info( "false 1" );
        return false; 
        }
    
    // These must be the same parms for all filters that get used.
    public Boolean acceptOp2( Path fpath, BasicFileAttributes attr )
        {
        Date tdate = new Date( attr.lastModifiedTime().toMillis() );
        long testDate = Long.parseLong( sdf.format( tdate ) );
        //logger.info( "entered test for FilterOfDates() - test date 2: " + testDate + " compared to setdate:" + setDate2 );
        
        if ( op2.equals( "<" ) && testDate < setDate2 )
            {
            //logger.info( "true <" );
            return true;
            }
        else if ( op2.equals( "<=" ) && testDate <= setDate2 )
            {
            //logger.info( "true >" );
            return true;
            }
        else if ( op2.equals( "=" ) && testDate == setDate2 )
            {
            return true;
            }
        else if ( op2.equals( "!=" ) && testDate != setDate2 )
            {
            return true;
            }
        else if ( op2.equals( ">" ) && testDate > setDate2 )
            {
            return true;
            }
        else if ( op2.equals( ">=" ) && testDate >= setDate2 )
            {
            return true;
            }
        //logger.info( "false 2" );
        return false; 
        }
}
