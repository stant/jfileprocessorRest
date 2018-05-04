/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.towianski.models.Constants.FILESYSTEM_DOS;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.util.ArrayList;
import java.util.EnumSet;


/**
 *
 * @author Stan Towianski
 */
public class DeleteModel
    {
    int filesysType = FILESYSTEM_DOS;
    ConnUserInfo connUserInfo = null;
    String searchBtnText = null;
    Boolean deleteFilesOnlyFlag = false;
    boolean deleteToTrashFlag = true;
    Boolean deleteReadonlyFlag = false;
    String startingPath = null;
    ArrayList<String> deletePaths = null;
    ArrayList<CopyOption> copyOpts = new ArrayList<CopyOption>();
    EnumSet<FileVisitOption> fileVisitOptions = EnumSet.noneOf( FileVisitOption.class );

    // Async results
    boolean showProgressFlag = true;
            
    public int getFilesysType()
        {
        return filesysType;
        }

    public void setFilesysType(int filesysType)
        {
        this.filesysType = filesysType;
        }

    public ConnUserInfo getConnUserInfo() {
        return connUserInfo;
    }

    public void setConnUserInfo(ConnUserInfo connUserInfo) {
        this.connUserInfo = connUserInfo;
    }

    public String getSearchBtnText()
        {
        return searchBtnText;
        }

    public void setSearchBtnText(String searchBtnText)
        {
        this.searchBtnText = searchBtnText;
        }

    public Boolean getDeleteFilesOnlyFlag() {
        return deleteFilesOnlyFlag;
    }

    public void setDeleteFilesOnlyFlag(Boolean deleteFilesOnlyFlag) {
        this.deleteFilesOnlyFlag = deleteFilesOnlyFlag;
    }

    public boolean isDeleteToTrashFlag() {
        return deleteToTrashFlag;
    }

    public void setDeleteToTrashFlag(boolean deleteToTrashFlag) {
        this.deleteToTrashFlag = deleteToTrashFlag;
    }

    public Boolean getDeleteReadonlyFlag() {
        return deleteReadonlyFlag;
    }

    public void setDeleteReadonlyFlag(Boolean deleteReadonlyFlag) {
        this.deleteReadonlyFlag = deleteReadonlyFlag;
    }

    public String getStartingPath() {
        return startingPath;
    }

    public void setStartingPath(String startingPath) {
        this.startingPath = startingPath;
    }

    public ArrayList<String> getDeletePaths() {
        return deletePaths;
    }

    public void setDeletePaths(ArrayList<String> deletePaths) {
        this.deletePaths = deletePaths;
    }

    public ArrayList<CopyOption> getCopyOpts()
        {
        return copyOpts;
        }
    
    @JsonIgnore
    public void setCopyOpts(ArrayList<CopyOption> copyOpts)
        {
        this.copyOpts = copyOpts;
        }

    public EnumSet<FileVisitOption> getFileVisitOptions()
        {
        return fileVisitOptions;
        }

    public void setFileVisitOptions(EnumSet<FileVisitOption> fileVisitOptions)
        {
        this.fileVisitOptions = fileVisitOptions;
        }

    public boolean isShowProgressFlag() {
        return showProgressFlag;
    }

    public void setShowProgressFlag(boolean showProgressFlag) {
        this.showProgressFlag = showProgressFlag;
    }

    }
