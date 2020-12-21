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
public class CopyCounts {
    
    long files = 0;
    long oneFileBytes = 0;

    public CopyCounts( long files, long oneFileBytes )
        {
        this.files = files;
        this.oneFileBytes = oneFileBytes;
        }

        
    public long getFiles() {
        return files;
    }

    public void setFiles(long files) {
        this.files = files;
    }

    public long getOneFileBytes() {
        return oneFileBytes;
    }

    public void setOneFileBytes(long oneFileBytes) {
        this.oneFileBytes = oneFileBytes;
    }
    
}
