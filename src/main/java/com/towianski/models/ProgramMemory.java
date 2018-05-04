
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.towianski.jfileprocessor.JFileFinderWin;

/**
 *
 * @author stowians
 */
public class ProgramMemory {
    
    JFileFinderWin jFileFinderWin = null;
    boolean showHiddenFilesFlag = false;
    boolean showOwnerFlag = false;
    boolean showGroupFlag = false;
    boolean showPermsFlag = false;
	
    public ProgramMemory()
        {
        System.out.println( "ProgramMemory constructor()" );
        }
    
    @JsonIgnore
    public void setJFileFinderWin( JFileFinderWin jFileFinderWin ) {
        this.jFileFinderWin = jFileFinderWin;
    }

//    public ProgramMemory( JFileFinderWin jFileFinderWin )
//        {
//        System.out.println( "ProgramMemory constructor()" );
//        }

    public boolean isShowHiddenFilesFlag() {
        return showHiddenFilesFlag;
    }

    public void setShowHiddenFilesFlag(boolean showHiddenFilesFlag) {
        this.showHiddenFilesFlag = showHiddenFilesFlag;
    }

    public boolean isShowOwnerFlag() {
        return showOwnerFlag;
    }

    public void setShowOwnerFlag(boolean showOwnerFlag) {
        this.showOwnerFlag = showOwnerFlag;
    }

    public boolean isShowGroupFlag() {
        return showGroupFlag;
    }

    public void setShowGroupFlag(boolean showGroupFlag) {
        this.showGroupFlag = showGroupFlag;
    }

    public boolean isShowPermsFlag() {
        return showPermsFlag;
    }

    public void setShowPermsFlag(boolean showPermsFlag) {
        this.showPermsFlag = showPermsFlag;
    }
    
    
    //---------
    @JsonIgnore
    public void infuseSavedValues()
        {
        System.out.println( "ProgramMemory infuseSavedValues()" );
        jFileFinderWin.setShowHiddenFilesFlag(showHiddenFilesFlag);
        jFileFinderWin.setShowOwnerFlag(showOwnerFlag);
        jFileFinderWin.setShowGroupFlag(showGroupFlag);
        jFileFinderWin.setShowPermsFlag(showPermsFlag);
        }
    
    //---------
    @JsonIgnore
    public void extractCurrentValues()
        {
        System.out.println( "ProgramMemory ExtractCurrentValues()" );
        try {
        this.showHiddenFilesFlag = jFileFinderWin.getShowHiddenFilesFlag();
        this.showOwnerFlag = jFileFinderWin.isShowOwnerFlag();
        this.showGroupFlag = jFileFinderWin.getShowGroupFlag();
        this.showPermsFlag = jFileFinderWin.isShowPermsFlag();
        }
        catch( Exception exc )
        {
            exc.printStackTrace();
        }
        System.out.println( "ProgramMemory showHiddenFilesFlag =" + showHiddenFilesFlag );
        System.out.println( "ProgramMemory showOwnerFlag =" + showOwnerFlag );
        }

}

