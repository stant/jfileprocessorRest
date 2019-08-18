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
    WatchEvent<?> watchEvent = null;
    Instant instant = null;
    
    String filename = null;
    Path fullFilePath = null;
    WatchEvent.Kind eventKind = null;

    public FileTimeEvent( WatchKey watchKey, WatchEvent<?> watchEvent, Instant instant )
    {
        this.watchKey = watchKey;
        this.watchEvent = watchEvent;
        this.instant = instant;
    }

    public void calcOtherValues() {
        if ( watchEvent == null )
            {
            this.fullFilePath = null;    
            }
        else
            {
            filename = watchEvent.context().toString();
            eventKind = watchEvent.kind();
            Path dir = (Path) watchKey.watchable();
            this.fullFilePath = dir.resolve( (Path) watchEvent.context() );
            }
    }

    public WatchKey getWatchKey() {
        return watchKey;
    }

    public void setWatchKey(WatchKey watchKey) {
        this.watchKey = watchKey;
    }

    public String getKey() {
        return filename + watchEvent;
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

    public WatchEvent.Kind getEventKind() {
        return eventKind;
    }

    public void setEventKind(WatchEvent.Kind eventKind) {
        this.eventKind = eventKind;
    }
    
    
}
