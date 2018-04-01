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
public class Constants
    {
    public static final String PROCESS_STATUS_SEARCH_STARTED = "Search Started . . .";
    public static final String PROCESS_STATUS_FILL_STARTED = "Fill Started . . .";
    public static final String PROCESS_STATUS_SEARCH_CANCELED = "Search canceled";
    public static final String PROCESS_STATUS_SEARCH_COMPLETED = "Search completed";
    public static final String PROCESS_STATUS_FILL_CANCELED = "Fill canceled";
    public static final String PROCESS_STATUS_FILL_COMPLETED = "Fill completed";
    public static final String PROCESS_STATUS_CANCEL_SEARCH = "Cancel Search";
    public static final String PROCESS_STATUS_CANCEL_FILL = "Cancel Fill";
    public static final String PROCESS_STATUS_SEARCH_READY = "Search";
    public static final String PROCESS_STATUS_ERROR = "Error";

    public static final String SHOWFILESFOLDERSCB_BOTH = "Files & Folders";
    public static final String SHOWFILESFOLDERSCB_FILES_ONLY = "Files Only";
    public static final String SHOWFILESFOLDERSCB_FOLDERS_ONLY = "Folders Only";
    public static final String SHOWFILESFOLDERSCB_NEITHER = "Neither";

    public static final int FILESYSTEM_POSIX = 0;
    public static final int FILESYSTEM_DOS = 1;
    public int filesysType = FILESYSTEM_POSIX;

    public static final String PATH_PROTOCOL_FILE = "file://";
    public static final String PATH_PROTOCOL_SFTP = "sftp://";

    public static final int COPY_PROTOCOL_LOCAL = 0;
    public static final int COPY_PROTOCOL_SFTP_GET = 1;
    public static final int COPY_PROTOCOL_SFTP_PUT = 2;

    public static final String RMT_CONNECT_BTN_CONNECT = "Connect";
    public static final String RMT_CONNECT_BTN_CONNECTED = "Connected";
    public static final String RMT_CONNECT_BTN_DISCONNECT = "Disconnect";
    }
