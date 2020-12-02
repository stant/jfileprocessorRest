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

import com.towianski.utils.MyLogger;
import java.io.IOException;
import java.io.PrintWriter;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
 
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private static final MyLogger logger = MyLogger.getLogger( CustomBasicAuthenticationEntryPoint.class.getName() );

    @Override
    public void commence(final HttpServletRequest request, 
            final HttpServletResponse response, 
            final AuthenticationException authException) throws IOException// , ServletException 
        {
        //Authentication failed, send error response.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
         
        PrintWriter writer = response.getWriter();
        logger.info("HTTP Status 401 : " + authException.getMessage() );
        writer.print( "HTTP Status 401 : " + authException.getMessage() );
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
     
    @Override
    public void afterPropertiesSet() // throws Exception 
        {
        setRealmName("MY_TEST_REALM");
        super.afterPropertiesSet();
        }
}