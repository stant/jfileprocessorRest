/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

/**
 *
 * @author stan
 */

import java.io.Serializable;
import org.springframework.context.annotation.Profile;

import com.towianski.models.ServerUserFileRights;
import com.towianski.utils.MyLogger;
import java.util.ArrayList;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

//@RequiredArgsConstructor
//@Slf4j
//@Component
@Profile("server")
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final MyLogger logger = MyLogger.getLogger(CustomPermissionEvaluator.class.getName() );

    //@Autowired
    ArrayList<ServerUserFileRights> serverUserFileRightsList = null;
//    @Autowired
//    ServerUserFileRightsList serverUserFileRightsListObj;

    //ArrayList<ServerUserFileRights> serverUserFileRightsList = serverUserFileRightsListObj.getServerUserFileRightsList();

    public CustomPermissionEvaluator() {
        logger.info( "entered CustomPermissionEvaluator() constructor" );
        //serverUserFileRightsList = ServerUserFileRightsList.readInServerUserFileRightsFromFileList();        
        //this.serverUserFileRightsList = ServerUserFileRightsListHolder.readInServerUserFileRightsFromFileList();
    }
        
    public CustomPermissionEvaluator( ServerUserFileRightsList serverUserFileRightsList ) {
        logger.info( "entered CustomPermissionEvaluator() constructor" );
        this.serverUserFileRightsList = serverUserFileRightsList.getServerUserFileRightsList();
    }
        
    //private final SpreadsheetPermissionStore store;
    //private Set<SpreadsheetPermission> permissions = new HashSet<>();

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
//		User principal = (User) authentication.getPrincipal();
        String principal = (String) authentication.getPrincipal();
        logger.info("target domain principal " + principal + "    permission " + permission );

        if (targetDomainObject instanceof ServerUserFileRights) {
                return hasPermissionByObject(principal, (ServerUserFileRights) targetDomainObject, permission);
        }

        return false;
        }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                    Object permission) {
//		User principal = (User) authentication.getPrincipal();
        String principal = (String) authentication.getPrincipal();
        logger.info("serial target principal " + principal + "    permission " + permission );

        logger.info( "serverUserFileRightsList Address = " + System.identityHashCode( serverUserFileRightsList ) );
        if ( serverUserFileRightsList == null ) logger.info( "serverUserFileRightsList is NULL" );
        if ( serverUserFileRightsList != null ) logger.info( "serverUserFileRightsList size = " + serverUserFileRightsList.size() );
        for ( ServerUserFileRights serverUserFileRights : serverUserFileRightsList )
            {
            logger.info( "checking auth for =" + serverUserFileRights.getUser() + "=    pass =" + serverUserFileRights.getPassword() + "=    pass =" + serverUserFileRights.getPassword() );
            //if ( serverUserFileRights.getUser().equalsIgnoreCase( name) && serverUserFileRights.getPassword().equals( password) )
                {
                logger.info("rights user " + serverUserFileRights.getUser() + "    password " + serverUserFileRights.getPassword() + "    targetId =" + targetId + "=    targetType =" + targetType + "=" );
                } 
            }

                if (targetType.equals(ServerUserFileRights.class.getName())) {
                        return hasPermissionById(principal, targetId, permission);
                }

                return false;
        }

    private boolean hasPermissionByObject(String principal, ServerUserFileRights serverUserFileRights, Object permission) {
            boolean hasPermission = true;  //store.getPermissions().stream().anyMatch(p -> p.getUser().equals(principal)
//				&& p.getSpreadsheet().equals(spreadsheet)
//				&& p.getLevel().equals(permission));
        if (!hasPermission) {
            logger.info("Denying principal " + principal + "    permission " + permission );
            }
        return hasPermission;
        }

    private boolean hasPermissionById(String principal, Serializable targetId, Object permission) {
            boolean hasPermission = false;  //store.getPermissions().stream().anyMatch(p -> p.getUser().equals(principal)
//				&& p.getSpreadsheet().getId().equals(targetId)
//				&& p.getLevel().equals(permission));
        logger.info( "\nserverUserFileRightsList Address = " + System.identityHashCode( serverUserFileRightsList ) );
        if ( serverUserFileRightsList == null ) logger.info( "serverUserFileRightsList is NULL" );
        if ( serverUserFileRightsList != null ) logger.info( "serverUserFileRightsList size = " + serverUserFileRightsList.size() );
        for ( ServerUserFileRights serverUserFileRights : serverUserFileRightsList )
            {
            logger.info( "checking auth for =" + serverUserFileRights.getUser() + "=    pass =" + serverUserFileRights.getPassword() + "=" );
            logger.info( "              for =" + principal + "=    rights =" + serverUserFileRights.getRights() + "=    permission.toString() =" + permission.toString() + "=" );
            logger.info( "              for =" + serverUserFileRights.getPath() + "=    targetId.toString() =" + (targetId == null ? "null" : targetId.toString()) + "=" );
            if ( targetId != null
                    && serverUserFileRights.getUser().equalsIgnoreCase( principal ) 
                    && ( ( targetId.toString() ).startsWith( serverUserFileRights.getPath() ) )
                    && ( ( serverUserFileRights.getRights().toLowerCase() ).indexOf( permission.toString().toLowerCase() ) >= 0 ) )
                {
                logger.info("FOUND PERMISSION: rights user " + serverUserFileRights.getUser() + "    password " + serverUserFileRights.getPassword() + "    path " + serverUserFileRights.getPath() );
                return true;
                } 
            }

        if (!hasPermission) {
            logger.info("Denying principal " + principal + "    permission " + permission );
            }
        return hasPermission;
    }
}
