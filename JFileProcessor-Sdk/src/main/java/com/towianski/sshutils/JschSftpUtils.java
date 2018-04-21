package com.towianski.sshutils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the file transfer from local to remote.
 *   $ CLASSPATH=.:../build javac ScpTo.java
 *   $ CLASSPATH=.:../build java ScpTo file1 user@remotehost:file2
 * You will be asked passwd. 
 * If everything works fine, a local file 'file1' will copied to
 * 'file2' on 'remotehost'.
 *
 */
import com.jcraft.jsch.*;
import com.towianski.jfileprocessor.CopierNonWalker;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.logging.Level;

public class JschSftpUtils
    {    
    JSch jsch =new JSch();
    Session session = null;

    public Session getSession()
        {
        return session;
        }
        
    public void close()
        {
        session.disconnect();
        }
        
public void sftpIfDiff( String locFile, String user, String password, String rhost, String rmtFile )
    {
    Sftp sftp = new Sftp( user, password, rhost );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
    boolean doCopy = false;
    
    try {
        System.out.println( "SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        SftpATTRS sftpAttrs = null;

        try {
            System.out.println( "remote getHome() =" + chanSftp.getHome() + "=" );
            System.out.println( "remote pwd =" + chanSftp.pwd() + "=" );
            BasicFileAttributes attr = Files.readAttributes( Paths.get( locFile ), BasicFileAttributes.class );
            sftpAttrs = chanSftp.stat( rmtFile );
            if ( sftpAttrs.getSize() != attr.size() )
                {
                System.out.println( "file sizes diff so recopy over jar file." );
                doCopy = true;
                }
            } 
        catch (SftpException ex)
            {
            doCopy = true;
            java.util.logging.Logger.getLogger(CopierNonWalker.class.getName()).log(Level.SEVERE, null, ex);
            }
        catch (Exception exc)
            {
            System.out.println( "Exception" );
            doCopy = true;
            exc.printStackTrace();
            }
        if ( doCopy )
            {
            chanSftp.put( locFile, rmtFile );
            }
        } 
    catch (SftpException ex) 
        {
        java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    sftp.close();
    }

    public void copyIfMissing( String lfile, String user, String password, String rhost, String rfile )
        {
        //scpTo.copyTo( file.toString(), " ", "", "localhost", "/tmp/" + file.getName().toString() );
        FileInputStream fis=null;

        try
            {
        BasicFileAttributes attr = Files.readAttributes( Paths.get( lfile ), BasicFileAttributes.class );

        System.out.println( "try scpTo   user =" + user + "=   to password =" + password + "=" );
        System.out.println( "try scpTo   rhost =" + rhost + "=  to remoteFile =" + rfile + "=" );
        System.out.println( "try scpTo   lfilesize =" + attr.size() + "=   remote fsize =" + getRemoteFileSize( lfile, user, password, rhost, rfile ) + "=" );
        if ( getRemoteFileSize( lfile, user, password, rhost, rfile ) == attr.size() )
            {
            System.out.println( "files are same size to return" );
            return;
            }

    //      String lfile=arg[0];
    //      String user=remoteFile.substring(0, remoteFile.indexOf('@')).trim();
    //      remoteFile=remoteFile.substring(remoteFile.indexOf('@')+1);
    //      String host=remoteFile.substring(0, remoteFile.indexOf(':'));
    //      String rfile=remoteFile.substring(remoteFile.indexOf(':')+1);

          session=jsch.getSession(user, rhost, 22);
        System.out.println( "at 2" );

          session.setPassword( password );

          Properties config = new Properties();
          config.put("StrictHostKeyChecking","no");
        System.out.println( "at 3" );
          session.setConfig(config);

          // username and password will be given via UserInfo interface.
    //      UserInfo ui=new MyUserInfo();
    //    System.out.println( "at 3" );
    //      session.setUserInfo(ui);

        System.out.println( "at 4" );
          session.connect();
        System.out.println( "at 5" );

          boolean ptimestamp = true;

          // exec 'scp -t rfile' remotely
          String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
        System.out.println( "at 6" );
          Channel channel=session.openChannel("exec");
        System.out.println( "at 7" );
          ((ChannelExec)channel).setCommand(command);
        System.out.println( "at 8" );

          // get I/O streams for remote scp
          OutputStream out=channel.getOutputStream();
        System.out.println( "at 9" );
          InputStream in=channel.getInputStream();
        System.out.println( "at 10" );

          channel.connect();
        System.out.println( "at 11" );

          if(checkAck(in)!=0){
            return;
          }

          File _lfile = new File(lfile);
        System.out.println( "at 12" );

          if(ptimestamp){
            command="T"+(_lfile.lastModified()/1000)+" 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
        System.out.println( "at 13" );
            out.write(command.getBytes()); out.flush();
        System.out.println( "at 14" );
            if(checkAck(in)!=0){
                    return;
            }
          }

          // send "C0644 filesize filename", where filename should not include '/'
          long filesize=_lfile.length();
          command="C0644 "+filesize+" ";
        System.out.println( "at 15" );
          if(lfile.lastIndexOf('/')>0){
            command+=lfile.substring(lfile.lastIndexOf('/')+1);
          }
          else{
            command+=lfile;
          }
          command+="\n";
        System.out.println( "at 16" );
          out.write(command.getBytes()); out.flush();
        System.out.println( "at 17" );
          if(checkAck(in)!=0){
            return;
          }

          // send a content of lfile
          fis=new FileInputStream(lfile);
          byte[] buf=new byte[1024];
        System.out.println( "at 17" );
          while(true){
            int len=fis.read(buf, 0, buf.length);
            if(len<=0) break;
            out.write(buf, 0, len); //out.flush();
          }
        System.out.println( "at 18" );
          fis.close();
          fis=null;
          // send '\0'
          buf[0]=0; out.write(buf, 0, 1); out.flush();
          if(checkAck(in)!=0){
            return;
          }
          out.close();
        System.out.println( "at 19" );

          channel.disconnect();
          session.disconnect();

        System.out.println( "DONE." );
            return;
        }
        catch(Exception e){
          System.out.println(e);
          try{if(fis!=null)fis.close();}catch(Exception ee){}
        }
      }

    public Session createSession( String user, String password, String rhost )
        {
        try
            {
            session = jsch.getSession( user, rhost, 22 );
            System.out.println( "at 2" );

            session.setPassword( password );

            Properties config = new Properties();
            config.put("StrictHostKeyChecking","no");
            System.out.println( "at 3" );
            session.setConfig(config);

            // username and password will be given via UserInfo interface.
            //      UserInfo ui=new MyUserInfo();
            //    System.out.println( "at 3" );
            //      session.setUserInfo(ui);

            System.out.println( "at 4" );
            session.connect();
            } 
        catch (JSchException ex)
            {
            java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println( "at 5" );
        return session;
        }
    
public void copyTo( Session session, String lfile, String rfile )
        {
        //scpTo.copyTo( file.toString(), " ", "", "localhost", "/tmp/" + file.getName().toString() );
        FileInputStream fis=null;

        try
            {
            boolean ptimestamp = true;

            // exec 'scp -t rfile' remotely
            String command="copyTo scp " + ( ptimestamp ? "-p" : "" ) + " -t " + rfile;
            System.out.println( "copyTo at 6" );
            Channel channel=session.openChannel("exec");
            System.out.println( "copyTo at 7" );
            ((ChannelExec)channel).setCommand(command);
            System.out.println( "copyTo at 8" );

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            System.out.println( "copyTo at 9" );
            InputStream in = channel.getInputStream();
            System.out.println( "copyTo at 10" );

            channel.connect();
            System.out.println( "copyTo at 11" );

            if ( checkAck( in ) !=0 )
                {
                return;
                }

            File _lfile = new File( lfile );
            System.out.println( "copyTo at 12" );

            if ( ptimestamp )
                {
                command="T"+(_lfile.lastModified()/1000)+" 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
                System.out.println( "copyTo at 13" );
                out.write(command.getBytes()); out.flush();
                System.out.println( "copyTo at 14" );
                if ( checkAck( in ) !=0 )
                    {
                    return;
                    }
                }

              // send "C0644 filesize filename", where filename should not include '/'
            long filesize=_lfile.length();
            command="C0644 "+filesize+" ";
            System.out.println( "copyTo at 15" );
            if ( lfile.lastIndexOf( '/' ) > 0 )
                {
                command+=lfile.substring( lfile.lastIndexOf( '/' ) + 1 );
                }
            else
                {
                command += lfile;
                }
            command += "\n";
            System.out.println( "copyTo at 16" );
            out.write(command.getBytes()); out.flush();
            System.out.println( "copyTo at 17" );
            if ( checkAck( in ) !=0 )
                {
                return;
                }

              // send a content of lfile
            fis=new FileInputStream(lfile);
            byte[] buf=new byte[ 4096 ];
            System.out.println( "copyTo at 18" );
            while( true )
                {
                int len=fis.read(buf, 0, buf.length);
                if(len<=0) break;
                out.write(buf, 0, len); //out.flush();
                }
            System.out.println( "copyTo at 19" );
            fis.close();
            fis=null;
            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();
            if(checkAck(in)!=0){
                return;
              }
            out.close();
            System.out.println( "copyTo at 20" );

            channel.disconnect();
//            session.disconnect();

            System.out.println( "copyTo DONE." );
            return;
            }
        catch(Exception e)
            {
            System.out.println(e);
            try
                {
                if (fis != null)
                    {
                    fis.close();
                    }
                } catch (Exception ee)
                    {
                    }
            }
      }

public void SftpPut( String locFile, String user, String password, String rhost, String rmtFile )
    {
    Sftp sftp = new Sftp( user, password, rhost );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();

    try {
        //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
        System.out.println( "SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        chanSftp.put( locFile, rmtFile );
    } catch (SftpException ex) {
        java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
    sftp.close();
    }

public void SftpGet( String rmtFile, String user, String password, String rhost, String locFile )
    {
    Sftp sftp = new Sftp( user, password, rhost );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();

    try {
        System.out.println( "SftpGet rmtFile =" + rmtFile + "=   to locFile =" + locFile + "=" );
        //    sftpChannel.get("/source/remote/path/file.zip", "C:/target/local/path/file.zip");
        chanSftp.get( rmtFile, locFile );
    } catch (SftpException ex) {
        java.util.logging.Logger.getLogger(JschSftpUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
    sftp.close();
    }

public long getRemoteFileSize( String lfile, String user, String password, String rhost, String filename )
    {
    Sftp sftp = new Sftp( user, password, rhost );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
    SftpATTRS attrs = null;
    long fsize = -1;
    
    try {
        System.out.println( "remote getHome() =" + chanSftp.getHome() + "=" );
        System.out.println( "remote pwd =" + chanSftp.pwd() + "=" );
        System.out.println( "look for filename =" + filename );
        attrs = chanSftp.stat( filename );
        System.out.println( "got attrs =" );
        System.out.println( attrs );
        fsize = attrs.getSize();
        }
    catch (Exception ex) 
        {
        ex.printStackTrace();
        }
    finally
        {
        sftp.close();
        }
    return fsize;
}

//public static String oldestFile() {
//    Vector list = null;
//    int currentOldestTime;
//    int nextTime = 2140000000; //Made very big for future-proofing
//    ChannelSftp.LsEntry lsEntry = null;
//    SftpATTRS attrs = null;
//    String nextName = null;
//    try {
//        list = Main.chanSftp.ls("*.xml");
//        if (list.isEmpty()) {
//            fileFound = false;
//        }
//        else {
//            lsEntry = (ChannelSftp.LsEntry) list.firstElement();
//            oldestFile = lsEntry.getFilename();
//            attrs = lsEntry.getAttrs();
//            currentOldestTime = attrs.getMTime();
//            for (Object sftpFile : list) {
//                lsEntry = (ChannelSftp.LsEntry) sftpFile;
//                nextName = lsEntry.getFilename();
//                attrs = lsEntry.getAttrs();
//                nextTime = attrs.getMTime();
//                if (nextTime < currentOldestTime) {
//                    oldestFile = nextName;
//                    currentOldestTime = nextTime;
//                }
//            }
//            attrs = chanSftp.lstat(Main.oldestFile);
//            long size1 = attrs.getSize();
//            System.out.println("-Ensuring file is not being written to (waiting 1 minute)");
//            Thread.sleep(60000); //Wait a minute to make sure the file size isn't changing
//            attrs = chanSftp.lstat(Main.oldestFile);
//            long size2 = attrs.getSize();
//            if (size1 == size2) {
//                System.out.println("-It isn't.");
//                fileFound = true;
//            }
//            else {
//                System.out.println("-It is.");
//                fileFound = false;
//            }
//        }
//    } catch (Exception ex) {ex.printStackTrace();}
//    return Main.oldestFile;
//}

public void exec( String user, String password, String rhost, String runCmd )
    {
    try{
    System.out.println( "try exec   user =" + user + "=   to password =" + password + "=" );
    System.out.println( "try exec   rhost =" + rhost + "=" );

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
    System.out.println( "exec at 2" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    System.out.println( "exec at 3" );
    session.setConfig(config);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//    System.out.println( "at 3" );
//      session.setUserInfo(ui);

    System.out.println( "exec at 4" );
      session.connect();
    System.out.println( "exec at 5" );

      
      String xhost="localhost";
      int xport=10;
      //String display=JOptionPane.showInputDialog("Enter display name", 
      //                                           xhost+":"+xport);
      //xhost=display.substring(0, display.indexOf(':'));
      //xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
      session.setX11Host(xhost);
      session.setX11Port(xport+6000);
      

//      String command=JOptionPane.showInputDialog("Enter command", 
////                                                 "/opt/jFileProcessor/server.sh");
//                                                 runCmd );

      String command = runCmd;
      Channel channel=session.openChannel("exec");
      command += "\n";
    System.out.println( "exec at 6" );
      ((ChannelExec)channel).setCommand(command);
    System.out.println( "exec at 7" );

      // X Forwarding
       channel.setXForwarding(true);

      channel.setInputStream(System.in);
//      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
      ((ChannelExec)channel).setErrStream(System.err);

      InputStream in=channel.getInputStream();
    System.out.println( "exec at 8" );

      channel.connect();
    System.out.println( "exec at 9" );

      byte[] tmp=new byte[1024];
      while(true){
        while(in.available()>0){
          int i=in.read(tmp, 0, 1024);
          if(i<0)break;
          System.out.print(new String(tmp, 0, i));
        }
        if(channel.isClosed()){
          if(in.available()>0) continue; 
          System.out.println("exit-status: "+channel.getExitStatus());
          break;
        }
        try{Thread.sleep(1000);}catch(Exception ee){}
      }
    System.out.println( "exec at 10" );
      channel.disconnect();
      session.disconnect();
    System.out.println( "exec at 11" );
    }
    catch(Exception e){
      System.out.println(e);
    }
    System.out.println( "exec done" );
  }

public void execX11ForwardingOrig( String user, String password, String host )
    {
    String xhost="127.0.0.1";
//    String xhost="192.168.56.30";
    int xport=0;

    try{
//      String host=null;
//      if(arg.length>0){
//        host=arg[0];
//      }
//      else{
//        host=JOptionPane.showInputDialog("Enter username@hostname",
//                                         System.getProperty("user.name")+
//                                         "@localhost"); 
//      }
//      String user=host.substring(0, host.indexOf('@'));
      host=host.substring(host.indexOf('@')+1);

      Session session=jsch.getSession(user, host, 22);

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    config.put("ForwardX11Trusted","yes" );
    System.out.println( "at 3" );
    session.setConfig(config);


    session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
      UserInfo ui=new MyUserInfo();
      session.setUserInfo(ui);
      session.connect();

      Channel channel=session.openChannel("shell");

      channel.setXForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);

      channel.connect();
    }
    catch(Exception e){
      System.out.println(e);
    }
}
  
public void execX11Forwarding( String user, String password, String rhost )
    {
//    String xhost="127.0.0.1";
    String xhost="localhost";
//    String xhost="192.168.56.1";
//    String defCommand = "tty;who;/net2/programs/jFileProcessor/run.sh";
    String defCommand = "tty;who;/opt/jFileProcessor/server.sh";
    int xport=0;

    try{
    System.out.println( "try execX11Forwarding   user =" + user + "=   to password =" + password + "=" );
    System.out.println( "try execX11Forwarding   rhost =" + rhost + "=" );

    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    Session session=jsch.getSession(user, rhost, 22);
    System.out.println( "at 2" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    System.out.println( "at 3" );
    session.setConfig(config);

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//      session.setUserInfo(ui);

      session.connect();

      Channel channel=session.openChannel("exec");
    System.out.println( "at 4" );

//      String command=JOptionPane.showInputDialog("Enter command", 
//                                                 "echo =$DISPLAY=; cd /net2/programs/jFileProcessor; /usr/bin/java -jar /net2/programs/jFileProcessor/JFileProcessor.jar ");
      String command=JOptionPane.showInputDialog( "Enter command", 
                                                  defCommand );
      
      command += "\n";
      ((ChannelExec)channel).setCommand(command);
    System.out.println( "at 5" );
      
      channel.setXForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
    System.out.println( "at 6" );

      channel.connect();
    System.out.println( "at 7" );
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

public void execX11Two( String user, String password, String rhost )
    {
//    String xhost="127.0.0.1";
    String xhost="localhost";
//    String xhost="192.168.56.1";
//    String defCommand = "tty;who;/net2/programs/jFileProcessor/run.sh";
    String defCommand = "tty;who;/opt/jFileProcessor/run.sh";
    int xport=0;

    try{
	    	
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	Session session=jsch.getSession(user, rhost, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

                
                session.connect();
	    	System.out.println("Connected");
	    	
	    	Channel channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(defCommand);

      
      channel.setXForwarding(true);
      
                channel.setInputStream(null);
	        ((ChannelExec)channel).setErrStream(System.err);
	        
	        InputStream in=channel.getInputStream();
	        channel.connect();
	        byte[] tmp=new byte[1024];
	        while(true){
	          while(in.available()>0){
	            int i=in.read(tmp, 0, 1024);
	            if(i<0)break;
	            System.out.print(new String(tmp, 0, i));
	          }
	          if(channel.isClosed()){
	            System.out.println("exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        channel.disconnect();
	        session.disconnect();
	        System.out.println("DONE");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }

  }

public void execX11( String user, String password, String rhost )
    {
//    String xhost="127.0.0.1";
    String xhost = "localhost";
//     String xhost = "192.168.27.50";
    int xport = 0;
//    int xport = 1;
//    String defCommand = "tty;who;/opt/jFileProcessor/run.sh";
//    String defCommand = "tty;who;/usr/bin/kwrite";
    String defCommand = "tty;who;/opt/jFileProcessor/run.sh";

    try{
    System.out.println( "try execX11   user =" + user + "=   to password =" + password + "=" );
    System.out.println( "try execX11   rhost =" + rhost + "=" );

    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    Session session=jsch.getSession(user, rhost, 22);
    System.out.println( "at 1" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    System.out.println( "at 2" );
    session.setConfig(config);
    System.out.println( "at 2.5" );

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
    System.out.println( "at 2.6" );
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

    System.out.println( "execX11   xhost =" + xhost + "=   to (xport+6000) =" + (xport+6000) + "=" );
      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//      session.setUserInfo(ui);

    session.connect();

      Channel channel=session.openChannel("shell");
    System.out.println( "at 3" );

//      String command=JOptionPane.showInputDialog("Enter command", 
//                                                 "echo =$DISPLAY=; cd /net2/programs/jFileProcessor; /usr/bin/java -jar /net2/programs/jFileProcessor/JFileProcessor.jar ");
      String command=JOptionPane.showInputDialog( "Enter command", 
                                                  defCommand + " \n");

//      ((ChannelExec)channel).setCommand(command);
//    System.out.println( "at 4" );
      
      channel.setXForwarding(true);

    StringBufferInputStream reader = new StringBufferInputStream(command + " \n");
        channel.setInputStream(reader);
//    channel.connect();

//    channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
    System.out.println( "at 5" );

//    channel.setPty(true);
    channel.connect();
    System.out.println( "at 6" );
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

//private void connect (String command) {    
//    Channel channel = session.getChannel("shell");
//    channel.setXForwarding(true);
//    StringBufferInputStream reader = new StringBufferInputStream(command + " \n");
//    // or use
//    // ByteArrayInputStream reader = new ByteArrayInputStream((command + " \n").getBytes());
//    channel.setInputStream(reader);
//    channel.setOutputStream(System.out);
//    channel.connect();
//    try {
//        Thread.sleep(1000); // give GUI time to come up
//    } catch (InterruptedException ex) {
//        // print message
//    }
//    //channel.disconnect();
//}

/**
	 * Execute a command and return the result as a String
	 * 
	 * @param command
	 *            the command to execute
	 * @return the result as a String
	 * @throws IOException
	 */
//	public String executeCommand(String command) throws IOException {
//		ps.println(command);
//
//		int size = 1024;
//		final byte[] tmp = new byte[size];
//		final StringBuilder sb = new StringBuilder();
//
//		while (true) {
//			while (input.available() > 0) {
//				int i = input.read(tmp, 0, 1024);
//				if (i < 0) {
//					break;
//				}
//				sb.append(new String(tmp, 0, i));
//			}
//
//			final String output = sb.toString();
//			if (output.contains("object")) {
//				break;
//			}
//
//			if (channel.isClosed()) {
//				if (input.available() > 0) {
//					int i = input.read(tmp, 0, 1024);
//					sb.append(new String(tmp, 0, i));
//				}
//				break;
//			}
//
//			try {
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				LOG.error(e);
//			}
//		}
//
//		return sb.toString();
//	}


  static int checkAck(InputStream in) throws IOException{
    int b=in.read();
    // b may be 0 for success,
    //          1 for error,
    //          2 for fatal error,
    //          -1
    if(b==0) return b;
    if(b==-1) return b;

    if(b==1 || b==2){
      StringBuffer sb=new StringBuffer();
      int c;
      do {
	c=in.read();
	sb.append((char)c);
      }
      while(c!='\n');
      if(b==1){ // error
	System.out.print(sb.toString());
      }
      if(b==2){ // fatal error
	System.out.print(sb.toString());
      }
    }
    return b;
  }

  public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
    public String getPassword(){ return passwd; }
    public boolean promptYesNo(String str){
      Object[] options={ "yes", "no" };
      int foo=JOptionPane.showOptionDialog(null, 
             str,
             "Warning", 
             JOptionPane.DEFAULT_OPTION, 
             JOptionPane.WARNING_MESSAGE,
             null, options, options[0]);
       return foo==0;
    }
  
    String passwd;
    JTextField passwordField=(JTextField)new JPasswordField(20);

    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){ return true; }
    public boolean promptPassword(String message){
      Object[] ob={passwordField}; 
      int result=
	  JOptionPane.showConfirmDialog(null, ob, message,
					JOptionPane.OK_CANCEL_OPTION);
      if(result==JOptionPane.OK_OPTION){
	passwd=passwordField.getText();
	return true;
      }
      else{ return false; }
    }
    public void showMessage(String message){
      JOptionPane.showMessageDialog(null, message);
    }
    final GridBagConstraints gbc = 
      new GridBagConstraints(0,0,1,1,1,1,
                             GridBagConstraints.NORTHWEST,
                             GridBagConstraints.NONE,
                             new Insets(0,0,0,0),0,0);
    private Container panel;
    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo){
      panel = new JPanel();
      panel.setLayout(new GridBagLayout());

      gbc.weightx = 1.0;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.gridx = 0;
      panel.add(new JLabel(instruction), gbc);
      gbc.gridy++;

      gbc.gridwidth = GridBagConstraints.RELATIVE;

      JTextField[] texts=new JTextField[prompt.length];
      for(int i=0; i<prompt.length; i++){
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.weightx = 1;
        panel.add(new JLabel(prompt[i]),gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        if(echo[i]){
          texts[i]=new JTextField(20);
        }
        else{
          texts[i]=new JPasswordField(20);
        }
        panel.add(texts[i], gbc);
        gbc.gridy++;
      }

      if(JOptionPane.showConfirmDialog(null, panel, 
                                       destination+": "+name,
                                       JOptionPane.OK_CANCEL_OPTION,
                                       JOptionPane.QUESTION_MESSAGE)
         ==JOptionPane.OK_OPTION){
        String[] response=new String[prompt.length];
        for(int i=0; i<prompt.length; i++){
          response[i]=texts[i].getText();
        }
	return response;
      }
      else{
        return null;  // cancel
      }
    }
  }
}
