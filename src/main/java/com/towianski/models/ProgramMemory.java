
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
    String startConsoleCmd = "";
    String myEditorCmd = "";
    String checkForUpdateDate = "";   // value is only here, which is better
    
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

    public String getCheckForUpdateDate() {
        return checkForUpdateDate;
    }

    public void setCheckForUpdateDate(String checkForUpdateDate) {
        this.checkForUpdateDate = checkForUpdateDate;
    }

    public String getStartConsoleCmd()
        {
        return startConsoleCmd;
        }

    public void setStartConsoleCmd(String startConsoleCmd)
        {
        this.startConsoleCmd = startConsoleCmd;
        }

    public String getMyEditorCmd()
        {
        return myEditorCmd;
        }

    public void setMyEditorCmd(String myEditorCmd)
        {
        this.myEditorCmd = myEditorCmd;
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
        jFileFinderWin.setStartConsoleCmd( startConsoleCmd );
        jFileFinderWin.setMyEditorCmd( myEditorCmd );
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
            this.startConsoleCmd = jFileFinderWin.getStartConsoleCmd();
            this.myEditorCmd = jFileFinderWin.getMyEditorCmd();
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        System.out.println( "ProgramMemory showHiddenFilesFlag =" + showHiddenFilesFlag );
        System.out.println( "ProgramMemory showOwnerFlag =" + showOwnerFlag );
        }

}

