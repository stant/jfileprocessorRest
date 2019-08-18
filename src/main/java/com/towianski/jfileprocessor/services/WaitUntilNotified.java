/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.services;

import com.towianski.utils.MyLogger;

/**
 *
 * @author stan
 */
public class WaitUntilNotified implements Runnable {
    private static final MyLogger logger = MyLogger.getLogger( WaitUntilNotified.class.getName() );
    // Message sent from producer
    // to consumer.
    private String message;
    // True if consumer should wait
    // for producer to send message,
    // false if producer should wait for
    // consumer to retrieve message.
    private boolean empty = true;
    private Boolean myLock = true;

    public  String waitUntilDone() {
        // Wait until message is
        // available.
        logger.info( "call waitUntilDone() empty =" + empty );
        synchronized ( myLock ) {
//        while (empty) {
            try {
                logger.info( "waitUntilDone() go to wait()" );
                myLock.wait();
                logger.info( "waitUntilDone() AFTER wait()" );
            } catch (Exception e) {}
//        }
        }
        // Toggle status.
        empty = true;
        return message;
    }

    public synchronized void iAmDone(String message) {
        // Wait until message has
        // been retrieved.
        synchronized ( myLock ) {
        empty = false;
        // Store message.
        this.message = message;
        // Notify consumer that status
        // has changed.
        logger.info( "call waitUntilDone() call notifyAll()" );
        myLock.notifyAll();
        }
    }

    public void run() {
        logger.info( "RUN waitUntilDone() empty =" + empty );
        synchronized ( myLock ) {
            try {
                logger.info( "waitUntilDone() go to wait()" );
                while (empty) {
                    myLock.wait();
                }
                logger.info( "waitUntilDone() AFTER wait()" );
            } catch (Exception e) {}
        }
    }
    
    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
}
