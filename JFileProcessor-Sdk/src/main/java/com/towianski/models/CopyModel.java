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
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
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
    boolean doingCutFlag = false;
    boolean replaceExisting = false;
    boolean copyAttribs = false;
    boolean noFollowLinks = false;
    String startingPath = null;
    ArrayList<String> copyPaths = null;
    String toPath = null;
//    fileVisitOptions = null;
//    copyOpts.toArray( new CopyOption[ copyOpts.size() ] ) );
    ArrayList<CopyOption> copyOpts = new ArrayList<CopyOption>();
    EnumSet<FileVisitOption> fileVisitOptions = EnumSet.noneOf( FileVisitOption.class );
    ConnUserInfo connUserInfo = null;

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

    public boolean isDoingCutFlag()
        {
        return doingCutFlag;
        }

    public boolean isReplaceExisting() {
        return replaceExisting;
    }

    public boolean isCopyAttribs() {
        return copyAttribs;
    }

    public boolean isNoFollowLinks() {
        return noFollowLinks;
    }

    public void setDoingCutFlag(boolean doingCutFlag)
        {
        this.doingCutFlag = doingCutFlag;
        }
    public void setReplaceExisting(boolean replaceExisting)
        {
        this.replaceExisting = replaceExisting;
        }

    public void setCopyAttribs(boolean copyAttribs)
        {
        this.copyAttribs = copyAttribs;
        }

    public void setNoFollowLinks(boolean noFollowLinks)
        {
        this.noFollowLinks = noFollowLinks;
        }

    public String getStartingPath()
        {
        return startingPath;
        }

    public void setStartingPath(String startingPath)
        {
        this.startingPath = startingPath;
        }

    public ArrayList<String> getCopyPaths()
        {
        return copyPaths;
        }

    public void setCopyPaths(ArrayList<String> copyPaths)
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

    public ConnUserInfo getConnUserInfo() {
        return connUserInfo;
    }

    public void setConnUserInfo(ConnUserInfo connUserInfo) {
        this.connUserInfo = connUserInfo;
    }

    //---------- special stuff -------------

    public ArrayList<CopyOption> getCopyOpts()
        {
        if ( replaceExisting )
            {
            copyOpts.add( StandardCopyOption.REPLACE_EXISTING );
            }
        if ( copyAttribs )
            {
            copyOpts.add( StandardCopyOption.COPY_ATTRIBUTES );
            }
        if ( noFollowLinks )
            {
            copyOpts.add( LinkOption.NOFOLLOW_LINKS );
            }
        return copyOpts;
        }

    @JsonIgnore
    public void setCopyOpts(ArrayList<CopyOption> copyOpts)
        {
        this.copyOpts = copyOpts;
        }

    public EnumSet<FileVisitOption> getFileVisitOptions()
        {
        if ( ! noFollowLinks )
            {
            fileVisitOptions = EnumSet.of( FOLLOW_LINKS );
            }
        return fileVisitOptions;
        }

    @JsonIgnore
    public void setFileVisitOptions(EnumSet<FileVisitOption> fileVisitOptions)
        {
        this.fileVisitOptions = fileVisitOptions;
        }

    }
