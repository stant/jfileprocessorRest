/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.towianski.models.Constants.FILESYSTEM_POSIX;
import com.towianski.utils.MyLogger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author stan
 */
public class ConnUserInfo
    {
    private static final MyLogger logger = MyLogger.getLogger( ConnUserInfo.class.getName() );
    String fromUri = "";
    String fromProtocol = "file://";
    String fromUser = "";
    String fromPassword = "";
    String fromHost = "localhost";
    String fromSshPort = "";
    int fromFilesysType = FILESYSTEM_POSIX;
    String fromAskHttpsPort = "0";
    String fromUsingHttpsPort = "0";
    
    String fromUserHomeDir = "";
    
    boolean connectedFlag = false;
    String toUri = "";
    String toProtocol = "file://";
    String toUser = "";
    String toPassword = "";
    String toHost = "localhost";
    String toSshPort = "";
    String toAskHttpsPort = "0";
    String toUsingHttpsPort = "0";
    int toFilesysType = FILESYSTEM_POSIX;
    
    String toUserHomeDir = "";
    String usingToOrFrom = "";
    
    public int state = 0;

    public static int STATE_CANCEL = 2;

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
        this.toUsingHttpsPort = toAskHttpsPort;
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
        this.toUsingHttpsPort = toAskHttpsPort;
        }

    @JsonIgnore
    public void setFrom( String fromProtocol, String fromUser, String fromPassword, String fromHost, String fromSshPort )
        {
        this.fromProtocol = fromProtocol;
        this.fromUser = fromUser;
        this.fromPassword = fromPassword;
        this.fromHost = findHostAddress( fromHost );
        this.fromSshPort = fromSshPort;
        }

    @JsonIgnore
    public void setTo( String toProtocol, String toUser, String toPassword, String toHost, String toSshPort, String toAskHttpsPort )
        {
        this.toProtocol = toProtocol;
        this.toUser = toUser;
        this.toPassword = toPassword;
        this.toHost = toHost;
        this.toSshPort = toSshPort;
        this.toAskHttpsPort = toAskHttpsPort;
        this.toUsingHttpsPort = toAskHttpsPort;
        }

    @JsonIgnore
    public void setWhichUsing( String usingToOrFrom )
        {
        this.usingToOrFrom = usingToOrFrom;
        }

    @JsonIgnore
    public String findHostAddress( String host )
        {
        if ( host == null || host.equals( "?" ) || host.equals( "" ) )
            {
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getLocalHost();
                logger.info( "IP Address:- " + inetAddress.getHostAddress());
                logger.info( "Host Name:- " + inetAddress.getHostName());
                host = inetAddress.getHostAddress();
                }
            catch (UnknownHostException ex) {
                logger.severeExc( ex );
                host = "?";
                }
            }
        return host;
        }
    
    ///------------------------------------------------------------------------------------
    
    @JsonIgnore
    public boolean isRunCopyOnLocal() {
        return ! isRunCopyOnRemote();
    }

    @JsonIgnore
    public boolean isRunCopyOnRemote() {
                //logger.info( "ConnUserInfo:- " + this.toString() );  infinite loop !
        if ( ( fromProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) && toProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) ) ||
             ( fromProtocol.equals( Constants.PATH_PROTOCOL_HTTPS ) && toProtocol.equals( Constants.PATH_PROTOCOL_HTTPS ) )  )
            return true;
        return false;
    }

    @JsonIgnore
    public int getCopyProcotol() {
//        if ( fromHost.equals( toHost ) )
//            return Constants.COPY_PROTOCOL_LOCAL;
//        else 
            if ( fromProtocol.equals( toProtocol ) )
            {
            if ( fromProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) ) 
                return Constants.COPY_PROTOCOL_SFTP_GET;
            if ( ! fromUri.equals( toUri ) )   // if they are the same, do local copy
                return Constants.COPY_PROTOCOL_HTTPS_GET;
            return Constants.COPY_PROTOCOL_LOCAL;
            }
        else if ( fromProtocol.equals( Constants.PATH_PROTOCOL_FILE ) && toProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) )
            return Constants.COPY_PROTOCOL_SFTP_PUT;
        else if ( fromProtocol.equals( Constants.PATH_PROTOCOL_SFTP ) && toProtocol.equals( Constants.PATH_PROTOCOL_FILE ) )
            return Constants.COPY_PROTOCOL_SFTP_GET;
        else if ( fromProtocol.equals( Constants.PATH_PROTOCOL_FILE ) && toProtocol.equals( Constants.PATH_PROTOCOL_HTTPS ) )
            return Constants.COPY_PROTOCOL_HTTPS_PUT;
        else if ( fromProtocol.equals( Constants.PATH_PROTOCOL_HTTPS ) && toProtocol.equals( Constants.PATH_PROTOCOL_FILE ) )
            return Constants.COPY_PROTOCOL_HTTPS_GET;

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
        else
            {
            state = 0;
            }
        }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        state = state;
        logger.info( "connUserInfo set state = " + state );
    }

    public String getFromUri() {
        return "https://" + fromHost + ":" + fromUsingHttpsPort;  //System.getProperty( "server.port", "8443" );
    }

    public void setFromUri(String fromUri) {
        this.fromUri = fromUri;
    }

    public String getToUri() {
        return "https://" + toHost + ":" + toUsingHttpsPort;
    }

    public void setToUri(String ToUri) {
        this.toUri = ToUri;
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

    @JsonIgnore
    public boolean isUsingSftp()
        {
        return fromProtocol.equalsIgnoreCase( "sftp://" ) || toProtocol.equalsIgnoreCase( "sftp://" );
        }

    @JsonIgnore
    public boolean isUsingHttps()
        {
        return fromProtocol.equalsIgnoreCase( "https://" ) || toProtocol.equalsIgnoreCase( "https://" );
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

    @JsonIgnore
    public int getFromSshPortInt()
        {
        try {
            return Integer.parseInt( fromSshPort );
            }
        catch( Exception ex ) { }
                
        return 22;
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

    @JsonIgnore
    public int getToSshPortInt()
        {
        try {
            return Integer.parseInt( toSshPort );
            }
        catch( Exception ex ) { }
                
        return 22;
        }

    public void setToSshPort(String toSshPort)
        {
        this.toSshPort = toSshPort;
        }

    @JsonIgnore
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

    @JsonIgnore
    public int getToUsingHttpsPortInt()
        {
        try {
            return Integer.parseInt( toUsingHttpsPort );
            }
        catch( Exception ex ) { }
                
        return 8443;
        }

    public void setToUsingHttpsPort(String tmp)
        {
        this.toUsingHttpsPort = tmp;
        logger.info( "connUserInfo set toUsingHttpsPort = " + this.toUsingHttpsPort );
        }

    @JsonIgnore
    public String getFromAskHttpsPort() {
        return fromAskHttpsPort.trim().equals( "" ) ? "0" : fromAskHttpsPort;
    }

    public void setFromAskHttpsPort(String fromAskHttpsPort) {
        this.fromAskHttpsPort = fromAskHttpsPort;
    }

    public String getFromUsingHttpsPort() {
        return fromUsingHttpsPort;
    }

    @JsonIgnore
    public int getFromUsingHttpsPortInt()
        {
        try {
            return Integer.parseInt( fromUsingHttpsPort );
            }
        catch( Exception ex ) { }
                
        return 8443;
        }

    public void setFromUsingHttpsPort(String fromUsingHttpsPort) {
        this.fromUsingHttpsPort = fromUsingHttpsPort;
    }

    public String getFromUserHomeDir() {
        return fromUserHomeDir;
    }

    public void setFromUserHomeDir(String fromUserHomeDir) {
        this.fromUserHomeDir = fromUserHomeDir;
    }

    public String getToUserHomeDir() {
        return toUserHomeDir;
    }

    public void setToUserHomeDir(String toUserHomeDir) {
        this.toUserHomeDir = toUserHomeDir;
    }

    @JsonIgnore
    public String getWhichUsingUri() {
        if ( usingToOrFrom.equalsIgnoreCase( "FROM" ) )
            return getFromUri();

        return getToUri();
    }

    @JsonIgnore
    public String getWhichUsingUser() {
        if ( usingToOrFrom.equalsIgnoreCase( "FROM" ) )
            return getFromUser();

        return getToUser();
    }

    @JsonIgnore
    public String getWhichUsingPassword() {
        if ( usingToOrFrom.equalsIgnoreCase( "FROM" ) )
            return getFromPassword();

        return getToPassword();
    }

    @JsonIgnore
    public String toString()
        {
        return  "  connectedFlag =" + connectedFlag + "="
+ "  \nfromUri =" + fromUri + "="
+ "  fromProtocol =" + fromProtocol + "="
+ "  fromUser =" + fromUser + "="
+ "  fromPassword =" + fromPassword + "="
+ "  fromHost =" + fromHost + "="
+ "  fromSshPort =" + fromSshPort + "="
+ "  fromAskHttpsPort =" + fromAskHttpsPort + "="
+ "  fromUsingHttpsPort =" + fromUsingHttpsPort + "="
+ "  fromFilesysType =" + fromFilesysType + "="
+ "  \nToUri =" + toUri + "="
+ "  toProtocol =" + toProtocol + "="
+ "  toUser =" + toUser + "="
+ "  toPassword =" + toPassword + "="
+ "  toHost =" + toHost + "="
+ "  toSshPort =" + toSshPort + "="
+ "  toAskHttpsPort =" + toAskHttpsPort + "="
+ "  toUsingHttpsPort =" + toUsingHttpsPort + "="
+ "  toFilesysType =" + toFilesysType + "="
+ "  \nisRunCopyOnRemote() = " + isRunCopyOnRemote() + "=    getCopyProcotol() = " + getCopyProcotol()
+ "  \nstate = " + state
+ "  \nusingToOrFrom = " + usingToOrFrom
+ "  \ngetWhichUsingUri = " + getWhichUsingUri();
        }
        
    }
