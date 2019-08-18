/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author stan
 */
public final class ProcessRunnable implements Runnable {

    private static final MyLogger logger = MyLogger.getLogger( ProcessRunnable.class.getName() );
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
            logger.info( "ProcessRunnable start()" );
            for ( String tmp : allArgs )
                {
                logger.info( "(" + tmp + ") " );
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
            logger.info( "process.waitFor() rc = " + exitValue );
        } catch (IOException ex) {
            logger.severeExc( ex );
        } catch (InterruptedException ex) {
            logger.severeExc( ex );
        }
	}

    public int getExitValue() {
        return exitValue;
    }

}