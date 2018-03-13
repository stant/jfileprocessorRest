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
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class LogStringHandlerHide 
{
  private static Logger logger = Logger.getLogger("LogStringHandler");

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
    logger.log(Level.INFO, "message 1");
    logger.log(Level.SEVERE, "message 2");
    logger.log(Level.FINE, "message 3");
    System.out.println(strHolder);
  }
}
