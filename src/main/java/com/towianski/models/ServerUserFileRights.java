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
public class ServerUserFileRights {
    
    private static final MyLogger logger = MyLogger.getLogger( ServerUserFileRights.class.getName() );
	 
    String user;
    String path;
    String rights;

    public ServerUserFileRights() { }
    
    public ServerUserFileRights( String user, String path, String rights )
        {
        this.user = user;
        this.path = path;
        this.rights = rights;
        }
    

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }
    
}
