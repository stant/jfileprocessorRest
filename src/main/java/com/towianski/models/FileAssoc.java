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
    String matchType = JfpConstants.MATCH_TYPE_GLOB;
    String matchPattern = "**";
    String desc = "";
    String startDir = "";
    String exec = "";
    String stop = "";

    public FileAssoc()
    {
    }
    
    public FileAssoc( String assocType, String matchType, String matchPattern, String desc, String startDir, String exec, String stop ) {
        this.assocType = assocType;
        this.matchType = matchType;
        this.matchPattern = matchPattern;
        this.desc = desc;
        this.startDir = startDir;
        this.exec = exec;
        this.stop = stop;
    }

    public String getAssocType() {
        return assocType;
    }

    public void setAssocType(String assocType) {
        this.assocType = assocType;
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

    public String getStartDir() {
        return startDir;
    }

    public void setStartDir(String startDir) {
        this.startDir = startDir;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
}
