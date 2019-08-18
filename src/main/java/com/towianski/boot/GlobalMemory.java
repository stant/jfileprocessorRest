/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot;

import com.towianski.utils.MyLogger;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author stan
 */
public class GlobalMemory {
    
    private static final MyLogger logger = MyLogger.getLogger( GlobalMemory.class.getName() );

    ReentrantLock searchLock = new ReentrantLock();

    public GlobalMemory()  {       logger.info( "GlobalMemory() constructor 1" ); }
        
    public boolean trySearchLock()
        {
        if ( searchLock.tryLock() )
            {
            // Got the lock
            logger.info( "GlobalMemoryImpl trySearchLock - got lock" );
            return true;
//            try
//                {
//                logger.info( "got searchLock" );
//                }
//            finally
//                {
//                // Make sure to unlock so that we don't cause a deadlock
//                searchLock.unlock();
//                }
            }
        else
            {
            // Someone else had the lock, abort
            logger.info( "GlobalMemoryImpl.trySearchLock() could NOT get searchLock" );
            }
        return false;
        }
    
    public void getSearchLock()
        {
        logger.info( "GlobalMemoryImpl.getSearchLock() - wait for lock" );
        searchLock.lock();
        logger.info( "GlobalMemoryImpl.getSearchLock() - got lock" );
        }
    
    public void releaseSearchLock()
        {
        logger.info( "GlobalMemoryImpl.releaseSearchLock()" );
        try {
            if  ( searchLock.isLocked() ) 
                {
                searchLock.unlock();        
                }
            }   
        catch( IllegalMonitorStateException ex )
            {
            logger.info( "GlobalMemoryImpl.releaseSearchLock() skip illegal monitor exception" );
            }
        }
    
    public void print()
        {
        logger.info( "GlobalMemoryImpl.print() this = " + this );
        }    
    
}
