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
import com.towianski.models.ConnUserInfo;
import com.towianski.utils.MyLogger;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JschSftpUtils
    {    
    private static final MyLogger logger = MyLogger.getLogger( JschSftpUtils.class.getName() );
    JSch jsch =new JSch();
    Session session = null;
    ConnUserInfo connUserInfo = null;

    // FIXXX - these methods are not passing SshKeyFilename and they need to
    // either retrofit or move to using Sftp instead possibly.
    // this empty constr is called when need to use connUserInfo constr instead.
    public JschSftpUtils()
        {    
        }
    
    public JschSftpUtils( ConnUserInfo connUserInfo )
        {    
        this.connUserInfo = connUserInfo;
        }
    
    public Session getSession()
        {
        return session;
        }
        
    public void close()
        {
        session.disconnect();
        }
        
// return "" if ok, or an error message
public String sftpIfDiff( String locFile, String user, String password, String rhost, int toSshPort, String toSshKeyFilename, String rmtFile )
    {
//    Sftp sftp = new Sftp( user, password, rhost, toSshPort );
    Sftp sftp = new Sftp( "TO", connUserInfo );
    if ( ! sftp.isConnected() )
        {
        return sftp.getMessage();
        }
    
    String errMsg = "";
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
    boolean doCopy = false;
    
    try {
        logger.info( "sftpIfDiff() SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        SftpATTRS sftpAttrs = null;

        try {
            logger.info( "remote getHome() =" + chanSftp.getHome() + "=" );
            logger.info( "remote pwd =" + chanSftp.pwd() + "=" );
            BasicFileAttributes attr = Files.readAttributes( Paths.get( locFile ), BasicFileAttributes.class );
            sftpAttrs = chanSftp.stat( rmtFile );
            if ( sftpAttrs.getSize() != attr.size() )
                {
                logger.info( "  -- file sizes diff so recopy over jar file." );
                doCopy = true;
                }
            else
                {
                logger.info( "  -- remote file size same so no recopy." );
                }
            } 
        catch (SftpException ex)
            {
            doCopy = true;
            logger.severeExc( ex );
            }
        catch (Exception exc)
            {
            logger.info( "Exception" );
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
        logger.severeExc( ex );
        errMsg = ex.toString();
        }
    sftp.close();
    return errMsg;
    }

public boolean isRemoteDos( String user, String password, String rhost, int toSshPort )
    {
    Sftp sftp = new Sftp( user, password, rhost, toSshPort );
    if ( ! sftp.isConnected() )
        {
        return false;
        }
    
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
    boolean isDos = false;
    
    try {
        String testStr = chanSftp.pwd();
        // keep string before 2nd / as drive: so /c/Users -> /c    /home/stan -> /home
        int at = testStr.substring( 1 ).indexOf( "/" );
        if ( at < 0 )  at = testStr.length();
        testStr = testStr.substring( 0, at + 1 ) + "/Windows/System";
        //logger.info( "remote getHome() =" + chanSftp.getHome() + "=" );
        logger.info( "checking for remote windows path =" + testStr + "=" );
        SftpATTRS sftpAttrs = chanSftp.stat( testStr );
        if ( sftpAttrs != null )
            {
            isDos = true;
            }
        } 
    catch (SftpException exc)
        {
        exc.printStackTrace();
        }
    sftp.close();
    return isDos;
    }

public boolean isRemotePosix( String user, String password, String rhost, int toSshPort )
    {
    return ! isRemoteDos( user, password, rhost, toSshPort );
    }

//    public void copyIfMissing( String lfile, String user, String password, String rhost, String rfile )
//        {
//        //scpTo.copyTo( file.toString(), " ", "", "localhost", "/tmp/" + file.getName().toString() );
//        FileInputStream fis=null;
//
//        try
//            {
//            BasicFileAttributes attr = Files.readAttributes( Paths.get( lfile ), BasicFileAttributes.class );
//
//            logger.info( "try scpTo   user =" + user + "=   to password =" + password + "=" );
//            logger.info( "try scpTo   rhost =" + rhost + "=  to remoteFile =" + rfile + "=" );
//            logger.info( "try scpTo   lfilesize =" + attr.size() + "=   remote fsize =" + getRemoteFileSize( lfile, user, password, rhost, rfile ) + "=" );
//            if ( getRemoteFileSize( lfile, user, password, rhost, rfile ) == attr.size() )
//                {
//                logger.info( "files are same size to return" );
//                return;
//                }
//
//        //      String lfile=arg[0];
//        //      String user=remoteFile.substring(0, remoteFile.indexOf('@')).trim();
//        //      remoteFile=remoteFile.substring(remoteFile.indexOf('@')+1);
//        //      String host=remoteFile.substring(0, remoteFile.indexOf(':'));
//        //      String rfile=remoteFile.substring(remoteFile.indexOf(':')+1);
//
//            session=jsch.getSession(user, rhost, 22);
//            logger.info( "at 2" );
//
//            session.setPassword( password );
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking","no");
//            logger.info( "at 3" );
//            session.setConfig(config);
//
//              // username and password will be given via UserInfo interface.
//        //      UserInfo ui=new MyUserInfo();
//        //    logger.info( "at 3" );
//        //      session.setUserInfo(ui);
//
//            logger.info( "at 4" );
//            session.connect();
//            logger.info( "at 5" );
//
//            boolean ptimestamp = true;
//
//            // exec 'scp -t rfile' remotely
//            String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
//            logger.info( "at 6" );
//            Channel channel=session.openChannel("exec");
//            logger.info( "at 7" );
//            ((ChannelExec)channel).setCommand(command);
//            logger.info( "at 8" );
//
//            // get I/O streams for remote scp
//            OutputStream out=channel.getOutputStream();
//            logger.info( "at 9" );
//            InputStream in=channel.getInputStream();
//            logger.info( "at 10" );
//
//            channel.connect();
//            logger.info( "at 11" );
//
//            if(checkAck(in)!=0){
//                return;
//            }
//
//            File _lfile = new File(lfile);
//            logger.info( "at 12" );
//
//            if(ptimestamp){
//                command="T"+(_lfile.lastModified()/1000)+" 0";
//                // The access time should be sent here,
//                // but it is not accessible with JavaAPI ;-<
//                command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
//                logger.info( "at 13" );
//                out.write(command.getBytes()); out.flush();
//                logger.info( "at 14" );
//                if(checkAck(in)!=0){
//                    return;
//                    }
//                }
//
//              // send "C0644 filesize filename", where filename should not include '/'
//            long filesize=_lfile.length();
//            command="C0644 "+filesize+" ";
//            logger.info( "at 15" );
//            if(lfile.lastIndexOf('/')>0){
//                command+=lfile.substring(lfile.lastIndexOf('/')+1);
//            }
//            else{
//                command+=lfile;
//            }
//            command+="\n";
//            logger.info( "at 16" );
//            out.write(command.getBytes()); out.flush();
//            logger.info( "at 17" );
//            if(checkAck(in)!=0){
//                return;
//            }
//
//            // send a content of lfile
//            fis=new FileInputStream(lfile);
//            byte[] buf=new byte[1024];
//            logger.info( "at 17" );
//            while(true){
//                int len=fis.read(buf, 0, buf.length);
//                if(len<=0) break;
//                out.write(buf, 0, len); //out.flush();
//            }
//            logger.info( "at 18" );
//            fis.close();
//            fis=null;
//            // send '\0'
//            buf[0]=0; out.write(buf, 0, 1); out.flush();
//            if(checkAck(in)!=0){
//                return;
//            }
//            out.close();
//            logger.info( "at 19" );
//
//            channel.disconnect();
//            session.disconnect();
//
//            logger.info( "DONE." );
//            return;
//            }
//        catch(Exception e)
//            {
//            logger.info( e);
//            try{if(fis!=null)fis.close();}catch(Exception ee){}
//            }
//      }

//    public Session createSession( String user, String password, String rhost )
//        {
//        try
//            {
//            session = jsch.getSession( user, rhost, 22 );
//            logger.info( "at 2" );
//
//            session.setPassword( password );
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking","no");
//            logger.info( "at 3" );
//            session.setConfig(config);
//
//            // username and password will be given via UserInfo interface.
//            //      UserInfo ui=new MyUserInfo();
//            //    logger.info( "at 3" );
//            //      session.setUserInfo(ui);
//
//            logger.info( "at 4" );
//            session.connect();
//            } 
//        catch (JSchException ex)
//            {
//            java.util.logging.logger.severeExc( ex );
//            }
//        logger.info( "at 5" );
//        return session;
//        }
    
public void copyTo( Session session, String lfile, String rfile )
        {
        //scpTo.copyTo( file.toString(), " ", "", "localhost", "/tmp/" + file.getName().toString() );
        FileInputStream fis=null;

        try
            {
            boolean ptimestamp = true;

            // exec 'scp -t rfile' remotely
            String command="copyTo scp " + ( ptimestamp ? "-p" : "" ) + " -t " + rfile;
            logger.info( "copyTo at 6" );
            Channel channel=session.openChannel("exec");
            logger.info( "copyTo at 7" );
            ((ChannelExec)channel).setCommand(command);
            logger.info( "copyTo at 8" );

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            logger.info( "copyTo at 9" );
            InputStream in = channel.getInputStream();
            logger.info( "copyTo at 10" );

            channel.connect();
            logger.info( "copyTo at 11" );

            if ( checkAck( in ) !=0 )
                {
                return;
                }

            File _lfile = new File( lfile );
            logger.info( "copyTo at 12" );

            if ( ptimestamp )
                {
                command="T"+(_lfile.lastModified()/1000)+" 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
                logger.info( "copyTo at 13" );
                out.write(command.getBytes()); out.flush();
                logger.info( "copyTo at 14" );
                if ( checkAck( in ) !=0 )
                    {
                    return;
                    }
                }

              // send "C0644 filesize filename", where filename should not include '/'
            long filesize=_lfile.length();
            command="C0644 "+filesize+" ";
            logger.info( "copyTo at 15" );
            if ( lfile.lastIndexOf( '/' ) > 0 )
                {
                command+=lfile.substring( lfile.lastIndexOf( '/' ) + 1 );
                }
            else
                {
                command += lfile;
                }
            command += "\n";
            logger.info( "copyTo at 16" );
            out.write(command.getBytes()); out.flush();
            logger.info( "copyTo at 17" );
            if ( checkAck( in ) !=0 )
                {
                return;
                }

              // send a content of lfile
            fis=new FileInputStream(lfile);
            byte[] buf=new byte[ 4096 ];
            logger.info( "copyTo at 18" );
            while( true )
                {
                int len=fis.read(buf, 0, buf.length);
                if(len<=0) break;
                out.write(buf, 0, len); //out.flush();
                }
            logger.info( "copyTo at 19" );
            fis.close();
            fis=null;
            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();
            if(checkAck(in)!=0){
                return;
              }
            out.close();
            logger.info( "copyTo at 20" );

            channel.disconnect();
//            session.disconnect();

            logger.info( "copyTo DONE." );
            return;
            }
        catch(Exception e)
            {
            logger.severeExc(e);
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

public void SftpPut( String locFile, String user, String password, String rhost, String toSshPort, String rmtFile )
    {
    try {
        SftpPut( locFile, user, password, rhost, Integer.parseInt( toSshPort ), rmtFile );
        }
    catch( Exception ex ) 
        {
        SftpPut( locFile, user, password, rhost, 22, rmtFile );
        }
    }

public void SftpPut( String locFile, String user, String password, String rhost, int toSshPort, String rmtFile )
    {
    Sftp sftp = new Sftp( user, password, rhost, toSshPort );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();

    try {
        //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
        //logger.info( "SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        rmtFile = rmtFile.replace( "\\", "/" );
        logger.info( "SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        chanSftp.put( locFile, rmtFile );
    } catch (SftpException ex) {
        logger.severeExc( ex );
    }
    sftp.close();
    }

public void SftpGet( String rmtFile, String user, String password, String rhost, String toSshPort, String locFile )
    {
    try {
        SftpGet( rmtFile, user, password, rhost, Integer.parseInt( toSshPort ), locFile );
        }
    catch( Exception ex ) 
        {
        SftpGet( rmtFile, user, password, rhost, 22, locFile );
        }
    }

public void SftpGet( String rmtFile, String user, String password, String rhost, int toSshPort, String locFile )
    {
    Sftp sftp = new Sftp( user, password, rhost, toSshPort );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();

    try {
        //logger.info( "SftpGet rmtFile =" + rmtFile + "=   to locFile =" + locFile + "=" );
        rmtFile = rmtFile.replace( "\\", "/" );
//        locFile = locFile.replace( "\\", "/" );
        logger.info( "SftpGet rmtFile =" + rmtFile + "=   to locFile =" + locFile + "=" );
        //    sftpChannel.get("/source/remote/path/file.zip", "C:/target/local/path/file.zip");
        //FileUtils.touch( Paths.get( locFile ) );
        //logger.info( "SftpGet after touch locFile =" + locFile + "=" );
        chanSftp.get( rmtFile, locFile );
    } catch (SftpException ex) {
        logger.info( "SftpGet ERROR =" + ex );
        logger.severeExc( ex );
    } catch (Exception ex) {
           logger.info( "SftpGet Touch ERROR =" + ex );
           logger.severeExc( ex );
        }
    sftp.close();
    }

public boolean SftpExists( String rmtFile, String user, String password, String rhost, int toSshPort )
    {
    Sftp sftp = new Sftp( user, password, rhost, toSshPort );

    try {
        logger.info( "SftpExists rmtFile =" + rmtFile + "=" );
        return sftp.exists( rmtFile );
        } 
    catch ( Exception ex )
        {
        logger.severeExc( ex );
        }
    sftp.close();
    return false;
    }

public long getRemoteFileSize( String lfile, String user, String password, String rhost, int toSshPort, String filename )
    {
    Sftp sftp = new Sftp( user, password, rhost, toSshPort );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
    SftpATTRS attrs = null;
    long fsize = -1;
    
    try {
        logger.info( "remote getHome() =" + chanSftp.getHome() + "=" );
        logger.info( "remote pwd =" + chanSftp.pwd() + "=" );
        logger.info( "look for filename =" + filename );
        attrs = chanSftp.stat( filename );
        logger.info( "got attrs =" );
        logger.info( attrs.toString() );
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
//            logger.info( "-Ensuring file is not being written to (waiting 1 minute)");
//            Thread.sleep(60000); //Wait a minute to make sure the file size isn't changing
//            attrs = chanSftp.lstat(Main.oldestFile);
//            long size2 = attrs.getSize();
//            if (size1 == size2) {
//                logger.info( "-It isn't.");
//                fileFound = true;
//            }
//            else {
//                logger.info( "-It is.");
//                fileFound = false;
//            }
//        }
//    } catch (Exception ex) {ex.printStackTrace();}
//    return Main.oldestFile;
//}

public void exec( String user, String password, String rhost, ConnUserInfo connUserInfo, String runCmd )
    {
    try{
    logger.info( "try exec   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try exec   rhost =" + rhost + "=" );

    Pattern startedOnPortMsg = Pattern.compile( ".*Tomcat .* port\\(s\\): (\\d+) \\(https\\).*" );   // the pattern to search for
    logger.info( "exec at 9b" );
    Pattern portInUseMsg = Pattern.compile( ".*Address already in use.*" );   // the pattern to search for
    logger.info( "exec at 9c" );
    Pattern connectionRefusedUseMsg = Pattern.compile( ".*Connection refused: connect.*" );
    logger.info( "exec at 9d" );
    
    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    if ( ! connUserInfo.getToSshKeyFilename().equals( "" ) )
        {
        jsch.addIdentity( connUserInfo.getToSshKeyFilename() );
        logger.info( "using Sftp sshKeyFilename =" + connUserInfo.getToSshKeyFilename() + "=" );
        }
    session=jsch.getSession( user, rhost, connUserInfo.getToSshPortInt() );
    logger.info( "exec at 1" );

    if ( connUserInfo.getToSshKeyFilename().equals( "" ) )
        {
        session.setPassword( password );
        }

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "exec at 2" );
    session.setConfig(config);
    logger.info( "exec at 3" );

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//    logger.info( "at 3" );
//      session.setUserInfo(ui);

    session.setTimeout( 15000 );
    //session.setServerAliveInterval( 15000 );
   // session.setSocketFactory( new SocketFactoryWithTimeout() );
    logger.info( "exec at 4" );
      session.connect();
    logger.info( "exec at 5" );

        if ( ! session.isConnected() )
        {
            logger.info( "jsch session did not connect !" );
            throw new JSchException();
        }
      
      String xhost="localhost";
      int xport=10;
      //String display=JOptionPane.showInputDialog("Enter display name", 
      //                                           xhost+":"+xport);
      //xhost=display.substring(0, display.indexOf(':'));
      //xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
      //session.setX11Host(xhost);
      //session.setX11Port(xport+6000);
      

//      String command=JOptionPane.showInputDialog("Enter command", 
////                                                 "/opt/jFileProcessor/server.sh");
//                                                 runCmd );

      String command = runCmd;
      //Channel channel=session.openChannel("exec");
      ChannelExec channel = (ChannelExec)session.openChannel("exec");
      command += "\n";
    logger.info( "exec at 6" );
    
    //channel.setPty(true);

      ((ChannelExec)channel).setCommand(command);
    logger.info( "exec at 7" );

      // X Forwarding
       //channel.setXForwarding(true);

//       channel.sendRequests();
       
      channel.setInputStream(System.in);
//      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
      ((ChannelExec)channel).setErrStream(System.err);

    InputStream in=channel.getInputStream();
    logger.info( "exec at 8" );

    channel.connect( 15000 );
    logger.info( "exec at 9" );

    byte[] tmp=new byte[1024];
//    logger.info( "exec at channel = " + channel );
    logger.info( "exec at before while loop" );
    while ( connUserInfo.getState() != ConnUserInfo.STATE_CANCEL && channel != null && ! channel.isEOF() )  // this does not seem to ever find CANCEL value !! ?
        {
        logger.finest("connUserInfo.getToUsingHttpsPort() = " + connUserInfo.getToUsingHttpsPort() );
        logger.finest( "connUserInfo.getState() = " + connUserInfo.getState() );
        while(in.available()>0)
            {
            int i=in.read(tmp, 0, 1024);
            if(i<0)break;
            String rin = new String( tmp, 0, i );
            logger.finest( "rmt stream ->" + rin );
            Matcher m1 = startedOnPortMsg.matcher( rin );
            Matcher m2 = portInUseMsg.matcher( rin );
            Matcher m3 = connectionRefusedUseMsg.matcher( rin );

            // if we find a match, get the group 
            if (m1.find())
                {
                // we're only looking for one group, so get it
                connUserInfo.setToUsingHttpsPort( m1.group(1) );
                // print the group out for verification
                logger.finest( "got portUsed = " + connUserInfo.getToUsingHttpsPort() );
                logger.finest( "got connUserInfo.getToUri() = " + connUserInfo.getToUri() );
                }
            else if (m2.find())
                {
                logger.info( "Port Already In Use !" );
                return;
                }
//            else if (m3.find())
//                {
//                logger.info( "Connection refused: connect !" );
//                return;
//                }
            }
        //System.out.print( "DONE rmt stream." );
        if ( channel.isClosed() )
            {
            if ( in.available() > 0 ) continue; 
            logger.info( "exit-status: "+channel.getExitStatus());
            break;
            }
        try{Thread.sleep(1000);} 
        catch(Exception ee)
            { 
            logger.info( "JschSftpUtils() sleep exception" );
            connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
            break;
            }
      } // while
    
        logger.info( "exec at 10" );
        channel.disconnect();
    }
    catch( JSchException je )
        {
        logger.info( "caught JSchException" );
        }
    catch(Exception e){
        logger.info( "caught Exception" );
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
        }
    finally
        {
        try {
            session.disconnect();
            }
            catch( Exception ex )
            {
            logger.info( "session disconnect error" );
            }
        logger.info( "exec at 11" );
        }
    logger.info( "exec() Done" );
  }

public void execWithPemLogin( String user, String password, String rhost, ConnUserInfo connUserInfo, String runCmd )
    {
    try{
    logger.info( "try exec   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try exec   rhost =" + rhost + "=" );

        Pattern startedOnPortMsg = Pattern.compile( ".*Tomcat started on port\\(s\\): (\\d+) \\(https\\).*" );   // the pattern to search for
    logger.info( "exec at 9b" );
    Pattern portInUseMsg = Pattern.compile( ".*Address already in use.*" );   // the pattern to search for
    logger.info( "exec at 9c" );
    Pattern connectionRefusedUseMsg = Pattern.compile( ".*Connection refused: connect.*" );
    logger.info( "exec at 9d" );
    
    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    jsch.addIdentity("/home/stan/linux1.pem");
    session=jsch.getSession(user, rhost, connUserInfo.getToSshPortInt() );
    logger.info( "exec at 1" );

////    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "exec at 2" );
    session.setConfig(config);
    logger.info( "exec at 3" );

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//    logger.info( "at 3" );
//      session.setUserInfo(ui);

    session.setTimeout( 15000 );
    //session.setServerAliveInterval( 15000 );
   // session.setSocketFactory( new SocketFactoryWithTimeout() );
    logger.info( "exec at 4" );
      session.connect();
    logger.info( "exec at 5" );

        if ( ! session.isConnected() )
        {
            logger.info( "jsch session did not connect !" );
            throw new JSchException();
        }
      
      String xhost="localhost";
      int xport=10;
      //String display=JOptionPane.showInputDialog("Enter display name", 
      //                                           xhost+":"+xport);
      //xhost=display.substring(0, display.indexOf(':'));
      //xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
      //session.setX11Host(xhost);
      //session.setX11Port(xport+6000);
      

//      String command=JOptionPane.showInputDialog("Enter command", 
////                                                 "/opt/jFileProcessor/server.sh");
//                                                 runCmd );

      String command = runCmd;
      //Channel channel=session.openChannel("exec");
      ChannelExec channel = (ChannelExec)session.openChannel("exec");
      command += "\n";
    logger.info( "exec at 6" );
    
    //channel.setPty(true);

      ((ChannelExec)channel).setCommand(command);
    logger.info( "exec at 7" );

      // X Forwarding
       //channel.setXForwarding(true);

//       channel.sendRequests();
       
      channel.setInputStream(System.in);
//      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
      ((ChannelExec)channel).setErrStream(System.err);

    InputStream in=channel.getInputStream();
    logger.info( "exec at 8" );

    channel.connect( 15000 );
    logger.info( "exec at 9" );

    byte[] tmp=new byte[1024];
//    logger.info( "exec at channel = " + channel );
    logger.info( "exec at before while loop" );
    while ( connUserInfo.getState() != ConnUserInfo.STATE_CANCEL && channel != null && ! channel.isEOF() )  // this does not seem to ever find CANCEL value !! ?
        {
        logger.finest("connUserInfo.getToUsingHttpsPort() = " + connUserInfo.getToUsingHttpsPort() );
        logger.finest( "connUserInfo.getState() = " + connUserInfo.getState() );
        while(in.available()>0)
            {
            int i=in.read(tmp, 0, 1024);
            if(i<0)break;
            String rin = new String( tmp, 0, i );
            logger.finest( "rmt stream ->" + rin );
            Matcher m1 = startedOnPortMsg.matcher( rin );
            Matcher m2 = portInUseMsg.matcher( rin );
            Matcher m3 = connectionRefusedUseMsg.matcher( rin );

            // if we find a match, get the group 
            if (m1.find())
                {
                // we're only looking for one group, so get it
                connUserInfo.setToUsingHttpsPort( m1.group(1) );
                // print the group out for verification
                logger.finest( "got portUsed = " + connUserInfo.getToUsingHttpsPort() );
                logger.finest( "got connUserInfo.getToUri() = " + connUserInfo.getToUri() );
                }
            else if (m2.find())
                {
                logger.info( "Port Already In Use !" );
                return;
                }
//            else if (m3.find())
//                {
//                logger.info( "Connection refused: connect !" );
//                return;
//                }
            }
        //System.out.print( "DONE rmt stream." );
        if ( channel.isClosed() )
            {
            if ( in.available() > 0 ) continue; 
            logger.info( "exit-status: "+channel.getExitStatus());
            break;
            }
        try{Thread.sleep(1000);} 
        catch(Exception ee)
            { 
            logger.info( "JschSftpUtils() sleep exception" );
            connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
            break;
            }
      } // while
    
        logger.info( "exec at 10" );
        channel.disconnect();
    }
    catch( JSchException je )
        {
        logger.info( "caught JSchException" );
        }
    catch(Exception e){
        logger.info( "caught Exception" );
        connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
        }
    finally
        {
        try {
            session.disconnect();
            }
            catch( Exception ex )
            {
            logger.info( "session disconnect error" );
            }
        logger.info( "exec at 11" );
        }
    logger.info( "exec() Done" );
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
    logger.info( "at 3" );
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
      logger.severeExc( e);
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
    logger.info( "try execX11Forwarding   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try execX11Forwarding   rhost =" + rhost + "=" );

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
    logger.info( "at 2" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "at 3" );
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
    logger.info( "at 4" );

//      String command=JOptionPane.showInputDialog("Enter command", 
//                                                 "echo =$DISPLAY=; cd /net2/programs/jFileProcessor; /usr/bin/java -jar /net2/programs/jFileProcessor/JFileProcessor.jar ");
      String command=JOptionPane.showInputDialog( "Enter command", 
                                                  defCommand );
      
      command += "\n";
      ((ChannelExec)channel).setCommand(command);
    logger.info( "at 5" );
      
      channel.setXForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
    logger.info( "at 6" );

      channel.connect();
    logger.info( "at 7" );
    }
    catch(Exception e){
      logger.severeExc( e);
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
	    	logger.info( "Connected");
	    	
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
	            logger.info( "exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        channel.disconnect();
	        session.disconnect();
	        logger.info( "DONE");
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
    logger.info( "try execX11   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try execX11   rhost =" + rhost + "=" );

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
    logger.info( "at 1" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "at 2" );
    session.setConfig(config);
    logger.info( "at 2.5" );

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
    logger.info( "at 2.6" );
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

    logger.info( "execX11   xhost =" + xhost + "=   to (xport+6000) =" + (xport+6000) + "=" );
      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//      session.setUserInfo(ui);

    session.connect();

      Channel channel=session.openChannel("shell");
    logger.info( "at 3" );

//      String command=JOptionPane.showInputDialog("Enter command", 
//                                                 "echo =$DISPLAY=; cd /net2/programs/jFileProcessor; /usr/bin/java -jar /net2/programs/jFileProcessor/JFileProcessor.jar ");
      String command=JOptionPane.showInputDialog( "Enter command", 
                                                  defCommand + " \n");

//      ((ChannelExec)channel).setCommand(command);
//    logger.info( "at 4" );
      
      channel.setXForwarding(true);

    StringBufferInputStream reader = new StringBufferInputStream(command + " \n");
        channel.setInputStream(reader);
//    channel.connect();

//    channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
    logger.info( "at 5" );

//    channel.setPty(true);
    channel.connect();
    logger.info( "at 6" );
    }
    catch(Exception e){
      logger.severeExc( e);
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
