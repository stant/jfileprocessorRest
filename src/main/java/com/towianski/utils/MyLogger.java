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
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Stan Towianski - July 2015
 */
public class MyLogger extends java.util.logging.Logger
{
//private final static Logger logger = logger.severeExc( ex );
//    private static MyLogger logger2 = null;

    private static Level currentLogLevel = Level.OFF;
    
static MyLogger mylogger = null;

//    final java.util.logging.Logger logger = null;
    static SimpleDateFormat logfileSdf = new SimpleDateFormat( "yyyyMMdd.HHmmss");
    static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd.HH:mm:ss");
    protected static LogStringHandler loghand = new LogStringHandler();
  
    protected MyLogger(String name) {
        super(name,null);
    }
    
 
    public static MyLogger getLogger(String name)
        {
        //StringBuffer outBuf = new StringBuffer();
        
        LogManager m = LogManager.getLogManager();
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
//        //logger.info( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
//        //logger.info( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
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
        logger.setLevel( currentLogLevel );
    
        return (MyLogger)logger;
        }

    public static void setAllLoggerLevels( Level newLevel )
        {    
        synchronized( currentLogLevel )
            {
            currentLogLevel = newLevel;
            }
        LogManager m = LogManager.getLogManager();
        Enumeration<String> enumList = m.getLoggerNames();
        while ( enumList.hasMoreElements() )
            {
            String logname = enumList.nextElement();
            System.out.println( "set loglevel (" + newLevel + ") for logger: " + logname );
            Logger logger = m.getLogger( logname );
            if (logger == null) 
                {
                continue;
                }

            logger.setLevel( currentLogLevel );
            }
        }

    public static void setLoggerLevels( String logname, Level newLevel )
        {    
        LogManager m = LogManager.getLogManager();
        Logger logger = m.getLogger( logname );
        if (logger != null) 
            {
            logger.setLevel( newLevel );
            System.out.println( "set loglevel (" + newLevel + ") for logger: " + logname );
            }
        }
    
    
// public  void init()
//    {
//    //Logger logger = logger.severeExc( ex );    
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
//        //logger.info( "logRecord.getLevel() =" + logRecord.getLevel() + "=" );
//        //logger.info( "logRecord.getMessage() =" + logRecord.getMessage() + "=" );
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
////    logger.info( "log outBuf before clear =\n" + outBuf.toString() + "\n=" );
////    outBuf.setLength( 0 );
////    logger.info( "log outBuf after  clear =\n" + outBuf.toString() + "\n=" );
//    loghand.clearLog();
//    }

public static String getFilenameLogDate() 
    {
    return logfileSdf.format( Calendar.getInstance().getTime() );
    }

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

public void severeExc( Exception exc ) 
    {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exc.printStackTrace(pw);
    System.err.println( sw.toString() );
    }

 public static void main(String[] args) 
    {
//    MyLogger logger2 = MyLogger.getLogger("test");
    MyLogger logger2 = MyLogger.getLogger( MyLogger.class.getName() );

     logger2.setLevel( Level.FINE );

    logger2.log(Level.INFO, "message 1");
    logger2.log(Level.SEVERE, "message 2");
    logger2.log(Level.FINE, "message 3");
    
    logger2.fine( "this is my error 4" );
    logger2.fine( "this is my error 5" );
    logger2.fine( "this is my error 6" );
            
//    logger.info( "log string =\n" + logger2.getLogString() + "\n=" );
    }    

}
