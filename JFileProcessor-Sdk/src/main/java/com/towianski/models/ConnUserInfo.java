/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import static com.towianski.models.Constants.FILESYSTEM_POSIX;

/**
 *
 * @author stan
 */
public class ConnUserInfo
    {
    String fromUri = "";
    String fromProtocol = "file://";
    String fromUser = null;
    String fromPassword = null;
    String fromHost = null;
    String fromSshPort = "22";
    int fromFilesysType = FILESYSTEM_POSIX;
    
    boolean connectedFlag = false;
    String ToUri = "";
    String toProtocol = "file://";
    String toUser = null;
    String toPassword = null;
    String toHost = null;
    String toSshPort = "22";
    int toFilesysType = FILESYSTEM_POSIX;

    public ConnUserInfo()
        {
        }

    public ConnUserInfo( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort, String toProtocol, String toUser, String toPassword, String toHost, String toSshPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = fromHost;
        this.fromSshPort = fromSshPort;
        
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        }

    public ConnUserInfo( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = fromHost;
        this.fromSshPort = fromSshPort;
        }

    public ConnUserInfo( boolean connectedFlag, String toProtocol, String toUser, String toPassword, String toHost, String toSshPort )
        {
        this.connectedFlag = connectedFlag;
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        }

    public void setFrom( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = fromHost;
        this.fromSshPort = fromSshPort;
        }

    public void setTo( String toProtocol, String toUser, String toPassword, String toHost, String toSshPort )
        {
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        }

    public boolean isConnectedFlag()
        {
        return connectedFlag;
        }

    public void setConnectedFlag(boolean connectedFlag)
        {
        this.connectedFlag = connectedFlag;
        }

    public String getFromUri() {
        return "https://" + fromHost + ":8443";
    }

    public void setFromUri(String fromUri) {
        this.fromUri = fromUri;
    }

    public String getToUri() {
        return "https://" + toHost + ":8443";
    }

    public void setToUri(String ToUri) {
        this.ToUri = ToUri;
    }

    public int getFromFilesysType() {
        return fromFilesysType;
    }

    public void setFromFilesysType(int fromFilesysType) {
        this.fromFilesysType = fromFilesysType;
    }

    public int getToFilesysType() {
        return toFilesysType;
    }

    public void setToFilesysType(int toFilesysType) {
        this.toFilesysType = toFilesysType;
    }

    public boolean isUsingSftp()
        {
        return fromProtocol.equalsIgnoreCase( "sftp://" ) || toProtocol.equalsIgnoreCase( "sftp://" );
        }

    public String getFromProtocol()
        {
        return fromProtocol;
        }

    public void setFromProtocol(String fromProtocol)
        {
        this.fromProtocol = fromProtocol;
        }

    public String getFromUser()
        {
        return fromUser;
        }

    public void setFromUser(String fromUser)
        {
        this.fromUser = fromUser;
        }

    public String getFromPassword()
        {
        return fromPassword;
        }

    public void setFromPassword(String fromPassword)
        {
        this.fromPassword = fromPassword;
        }

    public String getFromHost()
        {
        return fromHost;
        }

    public void setFromHost(String fromHost)
        {
        this.fromHost = fromHost;
        }

    public String getFromSshPort()
        {
        return fromSshPort;
        }

    public void setFromSshPort(String fromSshPort)
        {
        this.fromSshPort = fromSshPort;
        }

    public String getToProtocol()
        {
        return toProtocol;
        }

    public void setToProtocol(String toProtocol)
        {
        this.toProtocol = toProtocol;
        }

    public String getToUser()
        {
        return toUser;
        }

    public void setToUser(String toUser)
        {
        this.toUser = toUser;
        }

    public String getToPassword()
        {
        return toPassword;
        }

    public void setToPassword(String toPassword)
        {
        this.toPassword = toPassword;
        }

    public String getToHost()
        {
        return toHost;
        }

    public void setToHost(String toHost)
        {
        this.toHost = toHost;
        }

    public String getToSshPort()
        {
        return toSshPort;
        }

    public void setToSshPort(String toSshPort)
        {
        this.toSshPort = toSshPort;
        }

    public String toString()
        {
        return "  fromUri =" + fromUri + "="
+ "  fromProtocol =" + fromProtocol + "="
+ "  fromUser =" + fromUser + "="
+ "  fromPassword =" + fromPassword + "="
+ "  fromHost =" + fromHost + "="
+ "  fromSshPort =" + fromSshPort + "="
+ "  fromFilesysType =" + fromFilesysType + "="
+ "  connectedFlag =" + connectedFlag + "="
+ "  ToUri =" + ToUri + "="
+ "  toProtocol =" + toProtocol + "="
+ "  toUser =" + toUser + "="
+ "  toPassword =" + toPassword + "="
+ "  toHost =" + toHost + "="
+ "  toSshPort =" + toSshPort + "="
+ "  toFilesysType =" + toFilesysType + "=";
        }
        
    }
