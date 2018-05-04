/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class ProfileManager {
    @Autowired
    Environment environment;
 
    public void getActiveProfiles() {
        for (final String profileName : environment.getActiveProfiles()) {
            System.out.println("Currently active profile - " + profileName);
        }   
    }
}