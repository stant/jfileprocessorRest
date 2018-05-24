/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import java.util.ArrayList;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class ResultsData {
    private Boolean searchWasCanceled = false;
    private Boolean fillWasCanceled = false;
    private long filesVisited = 0;
    private long filesMatched = 0;
    private long foldersMatched = 0;
    private long filesTested = 0;
    private long foldersTested = 0;
    private String processStatus = "";
    private String message = "";
    private FilesTblModel filesTblModel = null;
    private int filesysType = -1;
    private ArrayList<String> errorList = new ArrayList<String>();
    
    public ResultsData()
        {
        }
    
    public ResultsData( Boolean searchWasCanceled, Boolean fillWasCanceled, long filesVisited
            , long filesMatched, long foldersMatched, long filesTested, long foldersTested )
        {
        this.searchWasCanceled = searchWasCanceled;
        this.fillWasCanceled = fillWasCanceled;
        this.filesVisited = filesVisited;
        this.filesMatched = filesMatched;
        this.foldersMatched = foldersMatched;
        this.filesTested = filesTested;
        this.foldersTested = foldersTested;
        }
    
    public ResultsData( Boolean searchWasCanceledArg, String processStatus, String message, long filesVisitedArg, long filesMatchedArg, long foldersMatched )
        {
        this.searchWasCanceled = searchWasCanceledArg;
        this.processStatus = processStatus;
        this.message = message;
        this.filesVisited = filesVisitedArg;
        this.filesMatched = filesMatchedArg;
        this.foldersMatched = foldersMatched;
        }
    
    public ResultsData( Boolean searchWasCanceledArg, String processStatus, String message, long filesVisitedArg, long filesMatchedArg, long foldersMatched, long filesTested, long foldersTested, ArrayList<String> errorList )
        {
        this.searchWasCanceled = searchWasCanceledArg;
        this.processStatus = processStatus;
        this.message = message;
        this.filesVisited = filesVisitedArg;
        this.filesMatched = filesMatchedArg;
        this.foldersMatched = foldersMatched;
        this.filesTested = filesTested;
        this.foldersTested = foldersTested;
        this.errorList = errorList;
        }
    
    public ResultsData( Boolean searchWasCanceledArg, String processStatus, String message )
        {
        this.searchWasCanceled = searchWasCanceledArg;
        this.processStatus = processStatus;
        this.message = message;
        }
    
    public Boolean getSearchWasCanceled() {
        return searchWasCanceled;
    }

    public void setSearchWasCanceled(Boolean searchWasCanceled) {
        this.searchWasCanceled = searchWasCanceled;
    }

    public Boolean getFillWasCanceled() {
        return fillWasCanceled;
    }

    public void setFillWasCanceled(Boolean fillWasCanceled) {
        this.fillWasCanceled = fillWasCanceled;
    }

    public long getFilesVisited() {
        return filesVisited;
    }

    public void setFilesVisited(long filesVisited) {
        this.filesVisited = filesVisited;
    }

    public long getFilesMatched() {
        return filesMatched;
    }

    public long getFoldersMatched() {
        return foldersMatched;
    }

    public void setFoldersMatched(long foldersMatched) {
        this.foldersMatched = foldersMatched;
    }

    public void setFilesMatched(long filesMatched) {
        this.filesMatched = filesMatched;
    }

    public long getFilesTested() {
        return filesTested;
    }

    public void setFilesTested(long filesTested) {
        this.filesTested = filesTested;
    }

    public long getFoldersTested() {
        return foldersTested;
    }

    public void setFoldersTested(long foldersTested) {
        this.foldersTested = foldersTested;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FilesTblModel getFilesTblModel()
        {
        return filesTblModel;
        }

    public void setFilesTblModel(FilesTblModel filesTblModel)
        {
        this.filesTblModel = filesTblModel;
        }

    public int getFilesysType() {
        return filesysType;
    }

    public void setFilesysType(int filesysType) {
        this.filesysType = filesysType;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(ArrayList<String> errorList) {
        this.errorList = errorList;
    }

}
