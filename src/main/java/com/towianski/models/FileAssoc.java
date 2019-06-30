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
public class FileAssoc {

    String assocType = JfpConstants.ASSOC_TYPE_SUFFIX;
    String editClass = "";
    String matchType = JfpConstants.MATCH_TYPE_GLOB;
    String matchPattern = "**";
    String exec = "";
    String stop = "";

    public FileAssoc()
    {
    }
    
    public FileAssoc( String assocType, String editClass, String matchType, String matchPattern, String exec, String stop ) {
        this.assocType = assocType;
        this.editClass = editClass;
        this.matchType = matchType;
        this.matchPattern = matchPattern;
        this.exec = exec;
        this.stop = stop;
    }

    public String getAssocType() {
        return assocType;
    }

    public void setAssocType(String assocType) {
        this.assocType = assocType;
    }

    public String getEditClass() {
        return editClass;
    }

    public void setEditClass(String editClass) {
        this.editClass = editClass;
    }
    
    
    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public String getExec() {
        return exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }
    
}
