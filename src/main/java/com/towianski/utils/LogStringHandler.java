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
//import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class LogStringHandler extends Handler {
    //private static final MyLogger logger = MyLogger.getLogger( LogStringHandler.class.getName() );
    //private static Logger logger = Logger.getLogger("LogStringHandler");

//    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss");
    SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss");

    public LogStringHandler()
        {
        System.out.println( "new LogStringHandler()" );
        }
    
    public void publish( LogRecord logRecord )
        {
        StringBuffer outBuf = new StringBuffer();
        //outBuf.append( "publish->");
        outBuf.append( sdf.format( Calendar.getInstance().getTime() ) ).append( " " );
//        outBuf.append( "Thread: " + Thread.currentThread().getId() ).append( " " );
        outBuf.append( "Thread: " + Thread.currentThread().getName() ).append( " " );
        outBuf.append( logRecord.getLevel() + ": " );
        outBuf.append( logRecord.getSourceClassName() + ": " );
        //outBuf.append( logRecord.getSourceMethodName() + "(): " );
        outBuf.append( logRecord.getSourceMethodName() + "():" + Thread.currentThread().getStackTrace()[6].getLineNumber() ).append( " " );
//        outBuf.append( "##################");
//        int i = 0;
//        for ( StackTraceElement ste : Thread.currentThread().getStackTrace() )
//            {
//            outBuf.append( "[" + (i++) + "] =" + ste.toString() + "\n" );
//            }
//        outBuf.append( "###----------#####");
//            String fullClassName = Thread.currentThread().getStackTrace()[5].getClassName();
//    String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
//    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
//    int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
//
//    Log.d(className + "." + methodName + "():" + lineNumber, message);

        outBuf.append( logRecord.getMessage() );
        //outBuf.append( "\n");
        //outBuf.append( "<-publish");
//        logger.info( outBuf.toString() );
        System.out.println( outBuf.toString() );
        
        //logger.info( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
        //logger.info( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
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
//    logger.info( "log outBuf before clear =\n" + outBuf.toString() + "\n=" );
//    outBuf.setLength( 0 );
//    outBuf = new StringBuffer();
//    logger.info( "log outBuf after  clear =\n" + outBuf.toString() + "\n=" );
//    }

}
