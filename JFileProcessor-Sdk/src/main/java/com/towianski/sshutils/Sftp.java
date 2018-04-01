/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.sshutils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.towianski.jfileprocessor.CopierNonWalker;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author stowians
 */
public class Sftp {
    JSch jsch=new JSch();  
    com.jcraft.jsch.ChannelSftp chanSftp = null;
    Channel channel = null;
    Session session = null;
            
public Sftp( String user, String password, String rhost )
    {
    try
        {
        System.out.println( "Sftp user =" + user + "=   to rhost =" + rhost + "=" );
//        System.out.println( "Sftp   password =" + password + "=" );

        if ( user == null )
            {
            rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
            }
      
        session=jsch.getSession(user, rhost, 22);
        System.out.println( "Sftp at 2" );

        session.setPassword( password );

        Properties config = new Properties();
        config.put("StrictHostKeyChecking","no");
        System.out.println( "Sftp at 3" );
        session.setConfig(config);

          // username and password will be given via UserInfo interface.
    //      UserInfo ui=new MyUserInfo();
    //    System.out.println( "at 3" );
    //      session.setUserInfo(ui);

        System.out.println( "Sftp at 4" );
        session.connect();
        System.out.println( "Sftp at 5" );

        channel=session.openChannel( "sftp" );
        channel.connect();
        chanSftp = (com.jcraft.jsch.ChannelSftp)channel;
        System.out.println( "Sftp done" );
        }
    catch(Exception e)
        {
        System.out.println(e);
        }
    }
    
public com.jcraft.jsch.ChannelSftp getChanSftp()
    {
    return chanSftp;
    }
    
public boolean exists( String path )
    {
    try {
        chanSftp.stat( path );
        } 
    catch (SftpException ex)
        {
        Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
        return false;
        }
    return true;
    }
    
public boolean isDir( String path )
    {
    SftpATTRS sourceSftpAttrs = null;
    try {
        sourceSftpAttrs = chanSftp.stat( path );
        } 
    catch (SftpException ex)
        {
        Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
        return false;
        }
    return sourceSftpAttrs.isDir();
    }
    
public void close()
    {
    try
        {
        channel.disconnect();
        session.disconnect();
        }
    catch(Exception e){
      System.out.println(e);
    }
  }
        
}
