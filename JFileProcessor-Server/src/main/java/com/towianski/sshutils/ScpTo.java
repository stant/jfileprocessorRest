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
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class ScpTo{
    
public void copyTo( String lfile, String user, String password, String rhost, String rfile )
    {
    //scpTo.copyTo( file.toString(), " kids", "hostess", "localhost", "/tmp/" + file.getName().toString() );

    FileInputStream fis=null;
    try{

//      String lfile=arg[0];
//      String user=remoteFile.substring(0, remoteFile.indexOf('@')).trim();
//      remoteFile=remoteFile.substring(remoteFile.indexOf('@')+1);
//      String host=remoteFile.substring(0, remoteFile.indexOf(':'));
//      String rfile=remoteFile.substring(remoteFile.indexOf(':')+1);

      JSch jsch=new JSch();
    System.out.println( "try scpTo   user =" + user + "=   to password =" + password + "=" );
    System.out.println( "try scpTo   rhost =" + rhost + "=  to remoteFile =" + rfile + "=" );

      Session session=jsch.getSession(user, rhost, 22);
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

public void exec( String user, String password, String rhost )
    {
    try{
      JSch jsch=new JSch();  

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
      
    Session session=jsch.getSession(user, rhost, 22);
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

      
      String xhost="localhost";
      int xport=10;
      //String display=JOptionPane.showInputDialog("Enter display name", 
      //                                           xhost+":"+xport);
      //xhost=display.substring(0, display.indexOf(':'));
      //xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
      session.setX11Host(xhost);
      session.setX11Port(xport+6000);
      

      String command=JOptionPane.showInputDialog("Enter command", 
                                                 "/opt/jFileProcessor/server.sh");

      Channel channel=session.openChannel("exec");
      command += "\n";
      ((ChannelExec)channel).setCommand(command);

      // X Forwarding
       channel.setXForwarding(true);

      channel.setInputStream(System.in);
//      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
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
          if(in.available()>0) continue; 
          System.out.println("exit-status: "+channel.getExitStatus());
          break;
        }
        try{Thread.sleep(1000);}catch(Exception ee){}
      }
      channel.disconnect();
      session.disconnect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

public void execX11ForwardingOrig( String user, String password, String host )
    {
    String xhost="127.0.0.1";
//    String xhost="192.168.56.30";
    int xport=0;

    try{
      JSch jsch=new JSch();  

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
      JSch jsch=new JSch();  

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
	    	JSch jsch = new JSch();
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
      JSch jsch=new JSch();  

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
