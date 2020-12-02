/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.security;

import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author stan
 */
public class SecUtils implements ApplicationContextAware
    {
    private static final MyLogger logger = MyLogger.getLogger(SecUtils.class.getName() );

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    } 

    public ApplicationContext getContext() {
           return applicationContext;
       }

    public boolean hasPermission( String fpath, String rightNeeded )
        {
        logger.info( "hasPermiss is always false" );   // default so must be overridden to work.
        return false;
        }

    public boolean hasPermission( Path fpath, String needed )
        {
        logger.info( "hasPermiss is always false" );   // default so must be overridden to work.
        return false;
        }
}
