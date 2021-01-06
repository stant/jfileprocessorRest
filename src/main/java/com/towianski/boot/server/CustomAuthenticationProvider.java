/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

import com.towianski.models.JfpUser;
import com.towianski.models.ServerUserFileRights;
import com.towianski.utils.MyLogger;
import java.util.ArrayList;
import java.util.HashMap;
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

    private static HashMap<String, JfpUser> jfpUserHm = null;  // new HashMap<String, JfpUser>();

    public CustomAuthenticationProvider() {
        logger.info( "entered CustomAuthenticationProvider() constructor" );
        jfpUserHm = ServerUserFileRightsListHolder.readInJfpUserHmFromFile().getJfpUserHm();
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

        logger.info( "jfpUserHm Address = " + System.identityHashCode( jfpUserHm ) );
        if ( jfpUserHm == null ) logger.info( "jfpUserHm is NULL" );
        if ( jfpUserHm != null ) logger.info( "jfpUserHm size = " + jfpUserHm.size() );
        logger.info( "checking auth for =" + name + "=    pass =" + password + "=" );
        JfpUser jfpUser = jfpUserHm.get( name );
        if ( jfpUser != null && jfpUser.getPassword().equals( password ) )
            {
            logger.info( "Found Valid authenticate username =" + name + "=    pass =" + password + "=" );
            // use the credentials
            // and authenticate against the third-party system
            return new UsernamePasswordAuthenticationToken( name, password, rightsList );
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