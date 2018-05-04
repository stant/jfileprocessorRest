/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import static com.towianski.models.Constants.FILESYSTEM_DOS;
import java.util.Date;


/**
 *
 * @author stan
 */
public class SearchModel
    {
    String searchBtnText = null;
    String action = "";
    String startingFolder = null;
    String patternType = null;
    String filePattern = null;
    String tabsLogicType = null;
    String showFilesFoldersType = null;
    
    String size1Op;
    String size1;
    String sizeLogicOp;
    String size2Op;
    String size2;

    String date1Op;
    Date date1;
    String dateLogicOp;
    String date2Op;
    Date date2;

    String maxDepth;
    String minDepth;
    String stopFileCount;
    String stopFolderCount;
    
    boolean showHiddenFilesFlag;

    public String getSearchBtnText()
        {
        return searchBtnText;
        }

    public void setSearchBtnText(String searchBtnText)
        {
        this.searchBtnText = searchBtnText;
        }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStartingFolder()
        {
        return startingFolder;
        }

    public void setStartingFolder(String startingFolder)
        {
        this.startingFolder = startingFolder;
        }

    public String getPatternType()
        {
        return patternType;
        }

    public void setPatternType(String patternType)
        {
        this.patternType = patternType;
        }

    public String getFilePattern()
        {
        return filePattern;
        }

    public void setFilePattern(String filePattern)
        {
        this.filePattern = filePattern;
        }

    public String getTabsLogicType()
        {
        return tabsLogicType;
        }

    public void setTabsLogicType(String tabsLogicType)
        {
        this.tabsLogicType = tabsLogicType;
        }

    public String getShowFilesFoldersType()
        {
        return showFilesFoldersType;
        }

    public void setShowFilesFoldersType(String showFilesFoldersType)
        {
        this.showFilesFoldersType = showFilesFoldersType;
        }

    public String getSize1Op()
        {
        return size1Op;
        }

    public void setSize1Op(String size1Op)
        {
        this.size1Op = size1Op;
        }

    public String getSize1()
        {
        return size1;
        }

    public void setSize1(String size1)
        {
        this.size1 = size1;
        }

    public String getSizeLogicOp()
        {
        return sizeLogicOp;
        }

    public void setSizeLogicOp(String sizeLogicOp)
        {
        this.sizeLogicOp = sizeLogicOp;
        }

    public String getSize2Op()
        {
        return size2Op;
        }

    public void setSize2Op(String size2Op)
        {
        this.size2Op = size2Op;
        }

    public String getSize2()
        {
        return size2;
        }

    public void setSize2(String size2)
        {
        this.size2 = size2;
        }

    public String getDate1Op()
        {
        return date1Op;
        }

    public void setDate1Op(String date1Op)
        {
        this.date1Op = date1Op;
        }

    public Date getDate1()
        {
        return date1;
        }

    public void setDate1(Date date1)
        {
        this.date1 = date1;
        }

    public String getDateLogicOp()
        {
        return dateLogicOp;
        }

    public void setDateLogicOp(String dateLogicOp)
        {
        this.dateLogicOp = dateLogicOp;
        }

    public String getDate2Op()
        {
        return date2Op;
        }

    public void setDate2Op(String date2Op)
        {
        this.date2Op = date2Op;
        }

    public Date getDate2()
        {
        return date2;
        }

    public void setDate2(Date date2)
        {
        this.date2 = date2;
        }

    public String getMaxDepth()
        {
        return maxDepth;
        }

    public void setMaxDepth(String maxDepth)
        {
        this.maxDepth = maxDepth;
        }

    public String getMinDepth()
        {
        return minDepth;
        }

    public void setMinDepth(String minDepth)
        {
        this.minDepth = minDepth;
        }

    public String getStopFileCount()
        {
        return stopFileCount;
        }

    public void setStopFileCount(String stopFileCount)
        {
        this.stopFileCount = stopFileCount;
        }

    public String getStopFolderCount()
        {
        return stopFolderCount;
        }

    public void setStopFolderCount(String stopFolderCount)
        {
        this.stopFolderCount = stopFolderCount;
        }

    public boolean isShowHiddenFilesFlag()
        {
        return showHiddenFilesFlag;
        }

    public void setShowHiddenFilesFlag(boolean showHiddenFilesFlag)
        {
        this.showHiddenFilesFlag = showHiddenFilesFlag;
        }

    }
