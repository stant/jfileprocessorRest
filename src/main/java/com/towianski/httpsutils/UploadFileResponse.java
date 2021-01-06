/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.httpsutils;

/**
 *
 * @author stan
 */
public class UploadFileResponse {
    private String filename;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponse(String filename, String fileDownloadUri, String fileType, long size) {
        this.filename = filename;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return filename;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}