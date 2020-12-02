package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.ConnUserInfo;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppThread implements Runnable
    {
    ConnUserInfo connUserInfo = null;
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    JFileFinderWin jFileFinderWin = null;
    boolean iStartedServer = false;
    boolean startedServer = false;
    Object WaitOnMe = null;

    /**
     * Creates a WatchService and registers the given directory
     */
//    public TomcatAppThread( ConnUserInfo connUserInfo, String user, String passwd, String rmtHost, JFileFinderWin jFileFinderWin )
//        {
//        this.connUserInfo = connUserInfo;
//        this.user = user;
//        this.passwd = passwd;
//        this.rmtHost = rmtHost;
//        this.jFileFinderWin = jFileFinderWin;
//        }

    public void cancelTomcatAppThread( boolean forceStop ) { };

//    @Override
    public void run() { };

    public void waitUntilNotified() { };
    
    public boolean isStartedServer() { return false; };

    public void setStartedServer(boolean startedServer) { };

//    public static void main(String[] args) throws IOException {
//        // parse arguments
//    }
}
