/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

import com.towianski.models.ServerUserFileRights;
import com.towianski.utils.MyLogger;
import java.util.ArrayList;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 *
 * @author stan
 */
@Service
//@DependsOn( { "serverUserFileRightsList" } )
@Profile("server")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    private static final MyLogger logger = MyLogger.getLogger( CustomAuthenticationProvider.class.getName() );

    //@Autowired
    //ServerUserFileRightsList serverUserFileRightsList = new ServerUserFileRightsList();
    ArrayList<ServerUserFileRights> serverUserFileRightsList = null;

    public CustomAuthenticationProvider() {
        logger.info( "entered CustomAuthenticationProvider() constructor" );
//        serverUserFileRightsList = ServerUserFileRightsList.readInServerUserFileRightsFromFileList();        
        serverUserFileRightsList = ServerUserFileRightsListHolder.readInServerUserFileRightsListFromFile().getServerUserFileRightsList();
    }
        
    @Override
    public Authentication authenticate(Authentication authentication)  throws AuthenticationException
        {
        logger.info( "entered authenticate()" );
 
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        logger.info( "sent username =" + name + "=    pass =" + password + "=" );
        
        ArrayList<GrantedAuthority> rightsList = new ArrayList<>();
        rightsList.add( new SimpleGrantedAuthority( "ROLE_WRITE" ) );

        logger.info( "serverUserFileRightsList Address = " + System.identityHashCode( serverUserFileRightsList ) );
        if ( serverUserFileRightsList == null ) logger.info( "serverUserFileRightsList is NULL" );
        if ( serverUserFileRightsList != null ) logger.info( "serverUserFileRightsList size = " + serverUserFileRightsList.size() );
        for ( ServerUserFileRights serverUserFileRights : serverUserFileRightsList )
            {
            logger.info( "checking auth for =" + serverUserFileRights.getUser() + "=    pass =" + serverUserFileRights.getPassword() + "=" );
            if ( serverUserFileRights.getUser().equalsIgnoreCase( name) && serverUserFileRights.getPassword().equals( password) )
                {
                logger.info( "Found Valid authenticate username =" + name + "=    pass =" + password + "=" );
                // use the credentials
                // and authenticate against the third-party system
                return new UsernamePasswordAuthenticationToken( name, password, rightsList );
                } 
            }
        throw new BadCredentialsException("1000");  //null;  //new UsernamePasswordAuthenticationToken( "invalid", password, rightsList );
        }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//        return true;
    }
    
//    @Override
//    public boolean supports(Class<?> authentication) {
//              return (UsernamePasswordAuthenticationToken.class
//                    .isAssignableFrom(authentication));
//    }
                      
}