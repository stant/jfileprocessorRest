/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author stan
 */
public class WatchKeyToPathAndQueue {
    
    private Path watchDir = null;
    private BlockingQueue<FileTimeEvent> fileEventCallerQueue = null;

    public WatchKeyToPathAndQueue( Path watchDir, BlockingQueue<FileTimeEvent> fileEventCallerQueue )
    {
        this.watchDir = watchDir;
        this.fileEventCallerQueue = fileEventCallerQueue;
    }
    
    public Path getWatchDir() {
        return watchDir;
    }

    public void setWatchDir(Path watchDir) {
        this.watchDir = watchDir;
    }

    public BlockingQueue<FileTimeEvent> getFileEventCallerQueue() {
        return fileEventCallerQueue;
    }

    public void setFileEventCallerQueue(BlockingQueue<FileTimeEvent> fileEventCallerQueue) {
        this.fileEventCallerQueue = fileEventCallerQueue;
    }
    
}
