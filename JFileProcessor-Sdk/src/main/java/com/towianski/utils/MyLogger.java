/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Stan Towianski - July 2015
 */
public class MyLogger extends java.util.logging.Logger
{
//private final static Logger logger = Logger.getLogger( MyLogger.class.getName() );
//    private static MyLogger logger2 = null;

static MyLogger mylogger = null;

//    final java.util.logging.Logger logger = null;
    static SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd.HHmmss");
    protected static LogStringHandler loghand = new LogStringHandler();
  
    protected MyLogger(String name) {
        super(name,null);
    }
    
 
    public static MyLogger getLogger(String name)
        {
        //StringBuffer outBuf = new StringBuffer();
        
        LogManager m = LogManager.getLogManager();
        //Logger logger = m.getLogger(name);
        Logger logger = m.getLogger(name);
        if (logger == null) 
            {
            m.addLogger(new MyLogger(name));
            logger = m.getLogger(name);
            }
          
        mylogger = (MyLogger)logger;

//        logger.addHandler( new Handler() {
//    public void publish( LogRecord logRecord )
//        {
//        outBuf.append( logRecord.getLevel() );
//        outBuf.append( ": " );
//        outBuf.append( logRecord.getSourceClassName() );
//        outBuf.append( ": " );
//        outBuf.append( logRecord.getSourceMethodName() );
//        outBuf.append( ": " );
//        outBuf.append( logRecord.getMessage() );
//        outBuf.append( "\n");
//        //System.out.println( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
//        //System.out.println( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
//        }
//
//    public void flush() {
//        }
//
//    public void close() {
//        }
//    });

        logger.addHandler( loghand );
        
        logger.setUseParentHandlers(false);
        logger.setLevel( Level.WARNING );
    
        return (MyLogger)logger;
        }

    
// public  void init()
//    {
//    //Logger logger = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );    
//    //logger2 = logger;
//    
////    // suppress the logging output to the console
////    Logger rootLogger = Logger.*getLogger*("");
////    Handler[] handlers = rootLogger.getHandlers();
////    if (handlers[0] instanceof ConsoleHandler) {
////      rootLogger.removeHandler(handlers[0]);
////    }
//    
////    outBuf.setLength( 0 );
//    logger.addHandler( new Handler() {
//    public void publish( LogRecord logRecord )
//        {
//        outBuf.append( logRecord.getLevel() + ": ");
//        outBuf.append( logRecord.getSourceClassName() + ": ");
//        outBuf.append( logRecord.getSourceMethodName() + ": ");
//        outBuf.append( logRecord.getMessage() );
//        outBuf.append( "\n");
//        //System.out.println( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
//        //System.out.println( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
//        }
//
//    public void flush() {
//        }
//
//    public void close() {
//        }
//    });
//
//    logger.setUseParentHandlers(false);
//    logger.setLevel( Level.WARNING );
//    }

//public  void setLevel( Level level ) 
//    {
//    this.setLevel( level );
//    }
  
//public String getLogString() 
//    {
//    //return outBuf.toString();
//    return loghand.getLogString();
//    }
//  
//public void clearLog()
//    {
////    System.out.println( "log outBuf before clear =\n" + outBuf.toString() + "\n=" );
////    outBuf.setLength( 0 );
////    System.out.println( "log outBuf after  clear =\n" + outBuf.toString() + "\n=" );
//    loghand.clearLog();
//    }

public static String getNewLogDate() 
    {
    return sdf.format( Calendar.getInstance().getTime() );
    }

public String getExceptionAsString( Exception exc ) 
    {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exc.printStackTrace(pw);
    return sw.toString();
    }

 public static void main(String[] args) 
    {
    MyLogger logger2 = MyLogger.getLogger("test");

     logger2.setLevel( Level.ALL );

    logger2.log(Level.INFO, "message 1");
    logger2.log(Level.SEVERE, "message 2");
    logger2.log(Level.FINE, "message 3");
            
//    System.out.println( "log string =\n" + logger2.getLogString() + "\n=" );
    }    

}
