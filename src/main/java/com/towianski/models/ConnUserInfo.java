/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import static com.towianski.models.Constants.FILESYSTEM_POSIX;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class ConnUserInfo
    {
    String fromUri = "";
    String fromProtocol = "file://";
    String fromUser = "";
    String fromPassword = "";
    String fromHost = "localhost";
    String fromSshPort = "22";
    int fromFilesysType = FILESYSTEM_POSIX;
    
    boolean connectedFlag = false;
    String ToUri = "";
    String toProtocol = "file://";
    String toUser = "";
    String toPassword = "";
    String toHost = "localhost";
    String toSshPort = "22";
    String toAskHttpsPort = "0";
    String toUsingHttpsPort = "0";
    int toFilesysType = FILESYSTEM_POSIX;

    public ConnUserInfo()
        {
        }

    public ConnUserInfo( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort, String toProtocol, String toUser, String toPassword, String toHost, String toSshPort, String toAskHttpsPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = findHostAddress( fromHost );
        this.fromSshPort = fromSshPort;
        
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        this.toAskHttpsPort = toAskHttpsPort;
        }

    public ConnUserInfo( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = findHostAddress( fromHost );
        this.fromSshPort = fromSshPort;
        }

    public ConnUserInfo( boolean connectedFlag, String toProtocol, String toUser, String toPassword, String toHost, String toSshPort, String toAskHttpsPort )
        {
        this.connectedFlag = connectedFlag;
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        this.toAskHttpsPort = toAskHttpsPort;
        }

    public void setFrom( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = findHostAddress( fromHost );
        this.fromSshPort = fromSshPort;
        }

    public void setTo( String toProtocol, String toUser, String toPassword, String toHost, String toSshPort, String toAskHttpsPort )
        {
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        this.toAskHttpsPort = toAskHttpsPort;
        }

    public String findHostAddress( String host )
        {
        if ( host == null || host.equals( "?" ) || host.equals( "" ) )
            {
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getLocalHost();
                System.out.println("IP Address:- " + inetAddress.getHostAddress());
                System.out.println("Host Name:- " + inetAddress.getHostName());
                host = inetAddress.getHostAddress();
                }
            catch (UnknownHostException ex) {
                Logger.getLogger(ConnUserInfo.class.getName()).log(Level.SEVERE, null, ex);
                host = "?";
                }
            }
        return host;
        }
    
    ///------------------------------------------------------------------------------------
    
    public boolean isRunCopyOnLocal() {
        return ! isRunCopyOnRemote();
    }

    public boolean isRunCopyOnRemote() {
        return ( fromProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) && toProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) ) ? true : false;
    }

    public int getCopyProcotol() {
        if ( fromHost.equals( toHost ) )
            return Constants.COPY_PROTOCOL_LOCAL;
        else if ( fromProtocol.equals( toProtocol ) )
            return Constants.COPY_PROTOCOL_SFTP_GET;
        else if ( fromProtocol.equals( Constants.PATH_PROTOCOL_FILE ) && toProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) )
            return Constants.COPY_PROTOCOL_SFTP_PUT;
        else if ( fromProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) && toProtocol.equals( Constants.PATH_PROTOCOL_FILE ) )
            return Constants.COPY_PROTOCOL_SFTP_GET;

        return Constants.COPY_PROTOCOL_LOCAL;
    }

    //----------------------
    
    public boolean isConnectedFlag()
        {
        return connectedFlag;
        }

    public void setConnectedFlag(boolean connectedFlag)
        {
        this.connectedFlag = connectedFlag;
        if ( ! connectedFlag )
            {
            setFrom( Constants.PATH_PROTOCOL_FILE, "", "", "?", "" );
            setTo( Constants.PATH_PROTOCOL_FILE, "", "", "?", "", "" );
            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
                {
                fromFilesysType = Constants.FILESYSTEM_DOS;
                toFilesysType = Constants.FILESYSTEM_DOS;
                }
            else
                {
                fromFilesysType = Constants.FILESYSTEM_POSIX;
                toFilesysType = Constants.FILESYSTEM_POSIX;
                }
            }
        }

    public String getFromUri() {
        return "https://" + fromHost + ":" + System.getProperty( "server.port", "8443" );
    }

    public void setFromUri(String fromUri) {
        this.fromUri = fromUri;
    }

    public String getToUri() {
        return "https://" + toHost + ":" + toUsingHttpsPort;
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

    public String getToAskHttpsPort()
        {
        return toAskHttpsPort.trim().equals( "" ) ? "0" : toAskHttpsPort;
        }

    public void setToAskHttpsPort(String toAskHttpsPort)
        {
        this.toAskHttpsPort = toAskHttpsPort;
        }

    public String getToUsingHttpsPort()
        {
        return toUsingHttpsPort;
        }

    public void setToUsingHttpsPort(String tmp)
        {
        this.toUsingHttpsPort = tmp;
        System.out.println( "connUserInfo set toUsingHttpsPort = " + this.toUsingHttpsPort );
        }

    public String toString()
        {
        return  "  connectedFlag =" + connectedFlag + "="
+ "  \nfromUri =" + fromUri + "="
+ "  fromProtocol =" + fromProtocol + "="
+ "  fromUser =" + fromUser + "="
+ "  fromPassword =" + fromPassword + "="
+ "  fromHost =" + fromHost + "="
+ "  fromSshPort =" + fromSshPort + "="
+ "  fromFilesysType =" + fromFilesysType + "="
+ "  \nToUri =" + ToUri + "="
+ "  toProtocol =" + toProtocol + "="
+ "  toUser =" + toUser + "="
+ "  toPassword =" + toPassword + "="
+ "  toHost =" + toHost + "="
+ "  toSshPort =" + toSshPort + "="
+ "  toAskHttpsPort =" + toAskHttpsPort + "="
+ "  toUsingHttpsPort =" + toUsingHttpsPort + "="
+ "  toFilesysType =" + toFilesysType + "="
+ "  \nisRunCopyOnRemote() = " + isRunCopyOnRemote() + "=    getCopyProcotol() = " + getCopyProcotol();
        }
        
    }
