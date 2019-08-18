/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

/**
 *
 * @author stan
 */
public class SearchBtnEvent {
    public static final int SEARCHBTNEVENT_SEARCH    = 1;
    public static final int SEARCHBTNEVENT_FILL      = 2;
    public static final int SEARCHBTNEVENT_AFTERFILL = 3;
    
    int type = 0;
    String startingFolder = null;
    
    public SearchBtnEvent( int type, String startingFolder )
        {
        this.type = type;
        this.startingFolder = startingFolder;
        }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStartingFolder() {
        return startingFolder;
    }

    public void setStartingFolder(String startingFolder) {
        this.startingFolder = startingFolder;
    }
    
}
