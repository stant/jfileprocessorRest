/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author stan
 */
public class LogStringHandlerHide 
{
    private static final MyLogger logger = MyLogger.getLogger( LogStringHandlerHide.class.getName() );

  private static List strHolder = new ArrayList();

  public static void main(String[] args) {
    logger.addHandler(new Handler() {
      public void publish(LogRecord logRecord) {
        strHolder.add(logRecord.getLevel() + ":");
        strHolder.add(logRecord.getSourceClassName() + ":");
        strHolder.add(logRecord.getSourceMethodName() + ":");
        strHolder.add("<" + logRecord.getMessage() + ">");
        strHolder.add("\n");
      }

      public void flush() {
      }

      public void close() {
      }
    });

    logger.setUseParentHandlers(false);       
    logger.setLevel( Level.SEVERE );

    logger.warning("Logging Warning");
    logger.info("Logging Info");
    logger.info( "message 1");
    logger.severe( "message 2");
    logger.fine( "message 3");
    //logger.info( strHolder );
  }
}
