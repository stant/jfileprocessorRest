/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

import com.towianski.models.ServerUserFileRights;
import com.towianski.utils.MyLogger;
import java.util.ArrayList;

/**
 *
 * @author stan
 */
//@Component
//@Profile("server")
public class ServerUserFileRightsList {
    
    private static final MyLogger logger = MyLogger.getLogger(ServerUserFileRightsList.class.getName() );

    private String version = "1.0";
    private static ArrayList<ServerUserFileRights> serverUserFileRightsList = new ArrayList<ServerUserFileRights>();
    
    public ServerUserFileRightsList()
        {
        logger.info( "ServerUserFileRightsList() constuct" );
        }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<ServerUserFileRights> getServerUserFileRightsList() {
        return serverUserFileRightsList;
    }

    public void setServerUserFileRightsList(ArrayList<ServerUserFileRights> serverUserFileRightsList) {
        this.serverUserFileRightsList = serverUserFileRightsList;
    }
    
}
