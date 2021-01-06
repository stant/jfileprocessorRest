/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import com.towianski.utils.MyLogger;

/**
 *
 * @author stan
 */
public class JfpUser {
    
    private static final MyLogger logger = MyLogger.getLogger(JfpUser.class.getName() );
	 
    String user;
    String password;

    public JfpUser() { }
    
    public JfpUser( String user, String password )
        {
        this.user = user;
        this.password = password;
        }
    

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
