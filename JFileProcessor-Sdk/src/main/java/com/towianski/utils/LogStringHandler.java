/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author stan
 */
public class LogStringHandler extends Handler {

    SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd.HHmmss");

    public LogStringHandler()
        {
        System.out.println( "new LogStringHandler()" );
        }
    
    public void publish( LogRecord logRecord )
        {
        StringBuffer outBuf = new StringBuffer();
        outBuf.append( sdf.format( Calendar.getInstance().getTime() ) )
                .append( " ");
        outBuf.append( logRecord.getLevel() + ": ");
        outBuf.append( logRecord.getSourceClassName() + ": ");
        outBuf.append( logRecord.getSourceMethodName() + ": ");
//        outBuf.append( "line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ": ");
//            String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
//    String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
//    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
//    int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
//
//    Log.d(className + "." + methodName + "():" + lineNumber, message);

        outBuf.append( logRecord.getMessage() );
        outBuf.append( "\n");
        System.out.println( "log-" + outBuf.toString() );
        //System.out.println( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
        //System.out.println( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
//        outBuf.setLength( 0 );
//        outBuf = new StringBuffer();
        }

    public void flush() {
        }

    public void close() {
        }
  
//public String getLogString() 
//    {
//    return outBuf.toString();
//    }
//  
//public void clearLog()
//    {
//    System.out.println( "log outBuf before clear =\n" + outBuf.toString() + "\n=" );
//    outBuf.setLength( 0 );
//    outBuf = new StringBuffer();
//    System.out.println( "log outBuf after  clear =\n" + outBuf.toString() + "\n=" );
//    }

}
