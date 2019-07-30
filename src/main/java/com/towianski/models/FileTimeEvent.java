/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.time.Instant;

/**
 *
 * @author stan
 */
public class FileTimeEvent {
    
    WatchKey watchKey = null;
    Path fullFilePath = null;
    String filename = null;
    Instant instant = null;
    WatchEvent.Kind event = null;

    public FileTimeEvent( WatchKey watchKey, Path fullFilePath, String filename, Instant instant, WatchEvent.Kind event )
    {
        this.watchKey = watchKey;
        this.fullFilePath = fullFilePath;
        this.filename = filename;
        this.instant = instant;
        this.event = event;
    }

    public WatchKey getWatchKey() {
        return watchKey;
    }

    public void setWatchKey(WatchKey watchKey) {
        this.watchKey = watchKey;
    }

    public String getKey() {
        return filename + event;
    }

    public Path getFullFilePath() {
        return fullFilePath;
    }

    public void setFullFilePath(Path fullFilePath) {
        this.fullFilePath = fullFilePath;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public WatchEvent.Kind getEvent() {
        return event;
    }

    public void setEvent(WatchEvent.Kind Event) {
        this.event = Event;
    }
    
    
}
