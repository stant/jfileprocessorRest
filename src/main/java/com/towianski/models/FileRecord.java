/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import java.util.Date;

/**
 *
 * @author stan
 */
public class FileRecord
    {
    public static int FILESTBLMODEL_FILETYPE = 0;
    public static int FILETYPE_NORMAL = 0;
    public static int FILETYPE_LINK = 1;
    public static int FILETYPE_OTHER = 2;
//    public static int FILESTBLMODEL_ISDIR = 1;
    public static int FILESTBLMODEL_FOLDERTYPE = 1;
    public static int FOLDERTYPE_FILE = 0;
    public static int FOLDERTYPE_FILE_NOT_FOUND = 1;
    public static int FOLDERTYPE_FOLDER = 2;
    public static int FOLDERTYPE_FOLDER_NOACCESS = 3;
    public static int FILESTBLMODEL_PATH = 2;
    public static int FILESTBLMODEL_MODIFIEDDATE = 3;
    public static int FILESTBLMODEL_SIZE = 4;
    public static int FILESTBLMODEL_OWNER = 5;
    public static int FILESTBLMODEL_GROUP = 6;
    public static int FILESTBLMODEL_PERMS = 7;

    private int fileType = 0;
    private int folderType = 0;
    private String path = "";
    private Date modifiedData = null;
    private long size = -1;
    private String Owner = "";
    private String Group = "";
    private String Perms = "";

    public int getFileType()
        {
        return fileType;
        }

    public void setFileType(int fileType)
        {
        this.fileType = fileType;
        }

    public int getFolderType()
        {
        return folderType;
        }

    public void setFolderType(int folderType)
        {
        this.folderType = folderType;
        }

    public String getPath()
        {
        return path;
        }

    public void setPath(String path)
        {
        this.path = path;
        }

    public Date getModifiedData()
        {
        return modifiedData;
        }

    public void setModifiedData(Date modifiedData)
        {
        this.modifiedData = modifiedData;
        }

    public long getSize()
        {
        return size;
        }

    public void setSize(long size)
        {
        this.size = size;
        }

    public String getOwner()
        {
        return Owner;
        }

    public void setOwner(String Owner)
        {
        this.Owner = Owner;
        }

    public String getGroup()
        {
        return Group;
        }

    public void setGroup(String Group)
        {
        this.Group = Group;
        }

    public String getPerms()
        {
        return Perms;
        }

    public void setPerms(String Perms)
        {
        this.Perms = Perms;
        }

            
    }
