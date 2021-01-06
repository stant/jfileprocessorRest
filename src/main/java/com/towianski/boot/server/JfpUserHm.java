/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

import com.towianski.models.JfpUser;
import com.towianski.utils.MyLogger;
import java.util.HashMap;

/**
 *
 * @author stan
 */
//@Component
//@Profile("server")
public class JfpUserHm {
    
    private static final MyLogger logger = MyLogger.getLogger(JfpUserHm.class.getName() );
	 
    private static HashMap<String, JfpUser> jfpUserHm = new HashMap<String, JfpUser>();
    
    public JfpUserHm()
        {
        //logger.info( "JfpUserHm() constuct" );
        }

    public HashMap<String, JfpUser> getJfpUserHm() {
        return jfpUserHm;
    }

    public void setJfpUserHm(HashMap<String, JfpUser> jfpUserHm) {
        this.jfpUserHm = jfpUserHm;
    }
    
}
