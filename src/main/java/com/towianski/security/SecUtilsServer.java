/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.security;

import com.towianski.boot.GlobalMemoryServer;
import com.towianski.boot.server.CustomPermissionEvaluator;
import com.towianski.boot.server.ServerUserFileRightsList;
import com.towianski.models.ServerUserFileRights;
import com.towianski.utils.MyLogger;
import java.nio.file.Path;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author stan
 */
@Profile( "server" )
public class SecUtilsServer extends SecUtils implements ApplicationContextAware
    {
    private static final MyLogger logger = MyLogger.getLogger( SecUtilsServer.class.getName() );

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    } 

    public ApplicationContext getContext() {
           return applicationContext;
       }

    public boolean hasPermission( Path fpath, String rightNeeded )
        {
        return hasPermission( fpath.toString(), rightNeeded );
        }

    public boolean hasPermission( String fpath, String rightNeeded )
        {
        SecurityContext sc = SecurityContextHolder.getContext(); 
        Authentication authentication = sc.getAuthentication();
        logger.info( "user principal =" + authentication.getName() + "=    pass =" + authentication.getCredentials() + "=    details =" + authentication.getDetails() );

        if ( applicationContext == null )  logger.info( "applicationContext is NULL" ); else logger.info( "applicationContext =" + applicationContext.toString() + "=" );
        ServerUserFileRightsList serverUserFileRightsList = GlobalMemoryServer.getServerUserFileRightsList();
        CustomPermissionEvaluator customPermissionEvaluatorNormBean = GlobalMemoryServer.getCustomPermissionEvaluatorNormBean();
        //(context.getBean( ServerUserFileRightsList.class )).readInServerUserFileRightsFromFileList();

        if ( serverUserFileRightsList == null )  logger.info( "serverUserFileRightsList is NULL" ); else logger.info( "serverUserFileRightsList =" + serverUserFileRightsList.getServerUserFileRightsList().toString() + "=" );
        if ( customPermissionEvaluatorNormBean == null )  logger.info( "customPermissionEvaluatorNormBean is NULL" ); else logger.info( "customPermissionEvaluatorNormBean =" + customPermissionEvaluatorNormBean + "=" );
        boolean hasPermiss = customPermissionEvaluatorNormBean.hasPermission( authentication, fpath, ServerUserFileRights.class.getName(), rightNeeded );
        logger.info( "hasPermiss =" + hasPermiss + "=" );
        
        return hasPermiss;
        }
}
