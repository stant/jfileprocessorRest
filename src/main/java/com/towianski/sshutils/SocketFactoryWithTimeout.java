/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.sshutils;

import com.jcraft.jsch.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author stan
 */
public class SocketFactoryWithTimeout implements SocketFactory {
  public Socket createSocket(String host, int port) throws IOException,
                                                           UnknownHostException
  {
    Socket socket=new Socket();
    int timeout = 15000;
    socket.connect(new InetSocketAddress(host, port), timeout);
    return socket;
  }

  public InputStream getInputStream(Socket socket) throws IOException
  {
    return socket.getInputStream();
  }

  public OutputStream getOutputStream(Socket socket) throws IOException
  {
    return socket.getOutputStream();
  }
}