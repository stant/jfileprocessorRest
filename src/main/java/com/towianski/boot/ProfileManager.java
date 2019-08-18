/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot;

import com.towianski.utils.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class ProfileManager {
    private static final MyLogger logger = MyLogger.getLogger( ProfileManager.class.getName() );

    @Autowired
    Environment environment;
 
    public void getActiveProfiles() {
        for (final String profileName : environment.getActiveProfiles()) {
            logger.info( "Currently active profile - " + profileName);
        }   
    }
}