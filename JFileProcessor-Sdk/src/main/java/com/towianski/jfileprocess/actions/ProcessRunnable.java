/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public final class ProcessRunnable implements Runnable {

    String startDir = null;
    String[] allArgs = {};
    int exitValue = 0;
    
    public ProcessRunnable( String startDir, String[] allArgs )
        {
        this.startDir = startDir;
        this.allArgs = allArgs;
        }

    @Override
    public void run() 
        {
        try {
            System.out.println( "ProcessRunnable start()" );
            for ( String tmp : allArgs )
                {
                System.out.println( "(" + tmp + ") " );
                }
            ProcessBuilder builder = new ProcessBuilder( allArgs );
            if ( startDir != null )
                {
                builder.directory( new File( startDir ) );
                }
            builder.redirectErrorStream(true);
            
            Process process = builder.start();
            process.waitFor();
            exitValue = process.exitValue();
        } catch (IOException ex) {
            Logger.getLogger(ProcessRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

    public int getExitValue() {
        return exitValue;
    }

}