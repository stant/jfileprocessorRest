/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

import com.towianski.jfileprocessor.JFileFinder;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class FilterChain {
    
    private ArrayList<FilterChainFilter> filterList = new ArrayList<FilterChainFilter>();
    private String andOrTests = CHAINFILTERA_AND_TEST;
    private FilterChainFilter currentChainFilter = null;
    private FilterChainFilter nextChainFilter = null;
    private int atIdx = -1;
    final public static String CHAINFILTERA_AND_TEST = "AND";
    final public static String CHAINFILTERA_OR_TEST = "OR";
    
    public FilterChain()
        {
        }
    
    public FilterChain( String andOrTests )
        {
        this.andOrTests = andOrTests;
        }
    
    public void addFilter( FilterChainFilter nextChainFilter )
        {
        this.filterList.add( nextChainFilter );
        }
    
    public int size()
        {
        return this.filterList.size();
        }
    
    public Boolean testFilters( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        int max = filterList.size();
        //logger.info( "entered " + this.toString() + ".incAndTest(" + andOrTests + ")   path =" + fpath );
        if ( max < 1 )
            {
            return true;
            }
        
        for ( int filter = 0; filter < max; filter++ )
            {
//            logger.info( "\ntest filter =" + filterList.get( filter ) );
            if ( filterList.get( filter ).accept( fpath, attr, chainFilterArgs, jFileFinder ) )
                {
//                logger.info( "accepted" );
                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_OR_TEST ) )
                    {
//                    logger.info( "return true because OR test" );
                    return true;
                    }
                }
            else   // test failed
                {
//                logger.info( "failed" );
                if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
                    {
//                    logger.info( "return false because AND test" );
                    return false;
                    }
                }
            }

        if ( andOrTests.equalsIgnoreCase( CHAINFILTERA_AND_TEST ) )
            {
//            logger.info( "return true because no more AND tests" );
            return true;
            }
        else
            {
//            logger.info( "return false because no more OR tests" );
            return false;
            }

        }
    
    public Boolean accept( Path fpath, BasicFileAttributes attr, ChainFilterArgs chainFilterArgs, JFileFinder jFileFinder )
        {
        return false;  // expect this method to be overriden
        }
}
