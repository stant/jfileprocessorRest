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
public class JfpConstants {
    
    public final static Boolean searchBtnLock = false;
    
    // I thought about having these sorted by assoc so just in case I used F, N, S to be in alpha order
    public final static String ASSOC_TYPE_SUFFIX = "S";
    public final static String ASSOC_TYPE_FILENAME = "N";
    public final static String ASSOC_TYPE_EXACT_FILE = "F";
    public final static String ASSOC_TYPE_ALL = "A";
    
    public final static int ASSOC_WINDOW_ACTION_EDIT = 1;
    public final static int ASSOC_WINDOW_ACTION_SELECT = 2;
    
    public final static String ASSOC_CMD_TYPE_EXEC = "exec";
    public final static String ASSOC_CMD_TYPE_STOP = "stop";

    public final static String MATCH_TYPE_GLOB = "G";
    public final static String MATCH_TYPE_REGEX = "R";
    
    public final static int HTTPS_BUFFER_SIZE = 102400;
    public final static long HTTPS_DISP_ON_SIZE = 102400;
}
