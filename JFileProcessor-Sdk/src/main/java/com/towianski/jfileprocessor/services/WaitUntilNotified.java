/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.services;

/**
 *
 * @author stan
 */
public class WaitUntilNotified implements Runnable {
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
        System.out.println( "call waitUntilDone() empty =" + empty );
        synchronized ( myLock ) {
//        while (empty) {
            try {
                System.out.println( "waitUntilDone() go to wait()" );
                myLock.wait();
                System.out.println( "waitUntilDone() AFTER wait()" );
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
        System.out.println( "call waitUntilDone() call notifyAll()" );
        myLock.notifyAll();
        }
    }

    public void run() {
        System.out.println( "RUN waitUntilDone() empty =" + empty );
        synchronized ( myLock ) {
            try {
                System.out.println( "waitUntilDone() go to wait()" );
                while (empty) {
                    myLock.wait();
                }
                System.out.println( "waitUntilDone() AFTER wait()" );
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
