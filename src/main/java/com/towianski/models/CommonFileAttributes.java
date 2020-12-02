/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import java.nio.file.attribute.FileTime;

/**
 *
 * @author stan
 */
public class CommonFileAttributes {

FileTime	 creationTime;
//Returns the creation time.
Object	fileKey;
//Returns an object that uniquely identifies the given file, or null if a file key is not available.
boolean	directory;
//Tells whether the file is a directory.
boolean	other;
//Tells whether the file is something other than a regular file, directory, or symbolic link.
boolean	regularFile;
//Tells whether the file is a regular file with opaque content.
boolean	symbolicLink;
//Tells whether the file is a symbolic link.
FileTime lastAccessTime;
//Returns the time of last access.
FileTime    lastModifiedTime;
//Returns the time of last modification.
long size;
//Returns the size of the file (in bytes). 

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public boolean isRegularFile() {
        return regularFile;
    }

    public void setRegularFile(boolean regularFile) {
        this.regularFile = regularFile;
    }

    public boolean isSymbolicLink() {
        return symbolicLink;
    }

    public void setSymbolicLink(boolean symbolicLink) {
        this.symbolicLink = symbolicLink;
    }

    public FileTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(FileTime creationTime) {
        this.creationTime = creationTime;
    }

    public Object getFileKey() {
        return fileKey;
    }

    public void setFileKey(Object fileKey) {
        this.fileKey = fileKey;
    }

    public FileTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(FileTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


}
