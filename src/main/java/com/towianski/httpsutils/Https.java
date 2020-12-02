/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.httpsutils;

import com.towianski.sshutils.*;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.towianski.utils.MyLogger;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author stowians
 */
public class Https {
    
    private static final MyLogger logger = MyLogger.getLogger(Https.class.getName() );

    JSch jsch=new JSch();  
    com.jcraft.jsch.ChannelSftp chanSftp = null;
    Channel channel = null;
    Session session = null;
    private boolean isConnected = false;
    private String message = "";
    
public Https( String user, String password, String rhost, int sshPort )
    {
    try
        {
        logger.info( "Sftp user =" + user + "=   to rhost =" + rhost + "=" );
//        logger.info( "Sftp   password =" + password + "=" );

        if ( user == null )
            {
            rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
            }
      
        session=jsch.getSession(user, rhost, sshPort);
        logger.info( "Sftp at 2" );

        session.setPassword( password );

        Properties config = new Properties();
        config.put("StrictHostKeyChecking","no");
        logger.info( "Sftp at StrictHostKeyChecking" );
//        config.put("cipher.s2c", "arcfour,aes128-cbc,3des-cbc,blowfish-cbc");
//        logger.info( "Sftp at cipher s2c" );
//        config.put("cipher.c2s", "arcfour,aes128-cbc,3des-cbc,blowfish-cbc");
//        logger.info( "Sftp at cipher c2s" );
        session.setConfig(config);
        logger.info( "Sftp at 3" );

          // username and password will be given via UserInfo interface.
    //      UserInfo ui=new MyUserInfo();
    //    logger.info( "at 3" );
    //      session.setUserInfo(ui);

        logger.info( "Sftp at 4" );
        session.connect();
        logger.info( "Sftp at 5" );

//        session.setConfig("cipher.s2c", "arcfour,aes128-cbc,3des-cbc,blowfish-cbc");
//        logger.info( "Sftp at 6a" );
//        session.setConfig("cipher.c2s", "arcfour,aes128-cbc,3des-cbc,blowfish-cbc");
//        logger.info( "Sftp at 6b" );

//        session.rekey();
//        logger.info( "Sftp at 6 - after rekey for no cipher" );

        channel=session.openChannel( "sftp" );
        logger.info( "Sftp at 7" );
        channel.connect();
        logger.info( "Sftp at 8" );
        chanSftp = (com.jcraft.jsch.ChannelSftp)channel;
        logger.info( "Sftp at 9" );
        isConnected = true;
        message = "";
        logger.info( "Sftp done" );
        }
    catch(Exception ex)
        {
        isConnected = false;
        message = "Sftp(): " + ex;
        logger.info( "Sftp(): " + ex );
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getMessage() {
        return message;
    }
    
public com.jcraft.jsch.ChannelSftp getChanSftp()
    {
    return chanSftp;
    }
    
public boolean exists( String path )
    {
    try {
        chanSftp.stat( path );
        return true;
        } 
    catch (Exception ex)
        {
        logger.severeExc( ex );
        }
    return false;
    }
    
public void mkDir( String path )
    {
    try {
        chanSftp.mkdir( path );
        } 
    catch (Exception ex)
        {
        logger.severeExc( ex );
        }
    }
    
public boolean isDir( String path )
    {
    SftpATTRS sourceSftpAttrs = null;
    try {
        sourceSftpAttrs = chanSftp.stat( path );
        } 
    catch (SftpException ex)
        {
        logger.severeExc( ex );
        return false;
        }
    return sourceSftpAttrs.isDir();
    }
    
public void touch( String path )
    {
    try {
        chanSftp.put( path );
        } 
    catch (Exception ex)
        {
        logger.severeExc( ex );
        }
    }
    
public void close()
    {
    try
        {
        channel.disconnect();
        session.disconnect();
        }
    catch(Exception e){
      logger.severeExc(e);
    }
  }
        
}
