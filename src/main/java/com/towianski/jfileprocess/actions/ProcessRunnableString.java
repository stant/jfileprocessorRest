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
public final class ProcessRunnableString implements Runnable {

    String startDir = null;
    String cmd = null;
    int exitValue = 0;
    
    public ProcessRunnableString( String startDir, String cmd )
        {
        this.startDir = startDir;
        this.cmd = cmd;
        }

    @Override
    public void run() 
        {
        try {
            System.out.println( "ProcessRunnable start()" );
            System.out.println( "(" + cmd + ") " );
            ProcessBuilder builder = new ProcessBuilder( cmd );
            if ( startDir != null )
                {
                builder.directory( new File( startDir ) );
                }
            builder.redirectErrorStream(true);
            
            Process process = builder.start();
            process.waitFor();
            exitValue = process.exitValue();
            System.out.println( "process.waitFor() rc = " + exitValue );
        } catch (IOException ex) {
            Logger.getLogger(ProcessRunnableString.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessRunnableString.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

    public int getExitValue() {
        return exitValue;
    }

}