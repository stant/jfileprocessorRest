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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;


/**
 *
 * @author Stan Towianski
 */
public class CopyModel
    {
    int filesysType = FILESYSTEM_DOS;
    String searchBtnText = null;
    Boolean isDoingCutFlag = false;
    String startingPath = null;
    ArrayList<Path> copyPaths = null;
    String toPath = null;
//    fileVisitOptions = null;
//    copyOpts.toArray( new CopyOption[ copyOpts.size() ] ) );
    ArrayList<CopyOption> copyOpts = new ArrayList<CopyOption>();
    EnumSet<FileVisitOption> fileVisitOptions = EnumSet.noneOf( FileVisitOption.class );

    public int getFilesysType()
        {
        return filesysType;
        }

    public void setFilesysType(int filesysType)
        {
        this.filesysType = filesysType;
        }

    public String getSearchBtnText()
        {
        return searchBtnText;
        }

    public void setSearchBtnText(String searchBtnText)
        {
        this.searchBtnText = searchBtnText;
        }

    public Boolean getIsDoingCutFlag()
        {
        return isDoingCutFlag;
        }

    public void setIsDoingCutFlag(Boolean isDoingCutFlag)
        {
        this.isDoingCutFlag = isDoingCutFlag;
        }

    public String getStartingPath()
        {
        return startingPath;
        }

    public void setStartingPath(String startingPath)
        {
        this.startingPath = startingPath;
        }

    public ArrayList<Path> getCopyPaths()
        {
        return copyPaths;
        }

    public void setCopyPaths(ArrayList<Path> copyPaths)
        {
        this.copyPaths = copyPaths;
        }

    public String getToPath()
        {
        return toPath;
        }

    public void setToPath(String toPath)
        {
        this.toPath = toPath;
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

    }
