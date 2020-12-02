/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot;

import com.towianski.boot.server.CustomPermissionEvaluator;
import com.towianski.boot.server.ServerUserFileRightsList;
import com.towianski.utils.MyLogger;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author stan
 */
public class GlobalMemoryServer {
    
    private static final MyLogger logger = MyLogger.getLogger(GlobalMemoryServer.class.getName() );

    ReentrantLock searchLock = new ReentrantLock();

    static ServerUserFileRightsList serverUserFileRightsList = null;
    static CustomPermissionEvaluator customPermissionEvaluatorNormBean = null;
            
    public GlobalMemoryServer()  {       logger.info( "GlobalMemory() constructor 1" ); }
        
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

    public static ServerUserFileRightsList getServerUserFileRightsList() {
        return serverUserFileRightsList;
    }

    public static void setServerUserFileRightsList(ServerUserFileRightsList serverUserFileRightsListArg ) {
        serverUserFileRightsList = serverUserFileRightsListArg;
    }

    public static CustomPermissionEvaluator getCustomPermissionEvaluatorNormBean() {
        return customPermissionEvaluatorNormBean;
    }

    public static void setCustomPermissionEvaluatorNormBean(CustomPermissionEvaluator customPermissionEvaluatorNormBeanArg) {
        customPermissionEvaluatorNormBean = customPermissionEvaluatorNormBeanArg;
    }
    
}
