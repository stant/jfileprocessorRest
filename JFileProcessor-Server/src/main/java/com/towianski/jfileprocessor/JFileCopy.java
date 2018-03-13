package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ResultsData;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;



public class JFileCopy //  implements Runnable 
{
    Boolean cancelFlag = false;
    String processStatus = "";
    String message = "";
    Boolean cancelFillFlag = false;
    Boolean isDoingCutFlag = false;
    String startingPath = null;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    String toPath = null;
    Boolean dataSyncLock = false;
    Copier copier = null;
    EnumSet<FileVisitOption> fileVisitOptions = null;
    private ArrayList<CopyOption> copyOptions = null;
    
    public JFileCopy( Boolean isDoingCutFlag, String startingPath, ArrayList<Path> copyPaths, String toPath, EnumSet<FileVisitOption> fileVisitOptions, ArrayList<CopyOption> copyOptions )
    {
        this.isDoingCutFlag = isDoingCutFlag;
        this.startingPath = startingPath;
        this.copyPaths = copyPaths;
        this.toPath = toPath;
        this.fileVisitOptions = fileVisitOptions;
        this.copyOptions = copyOptions;
        cancelFlag = false;
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        copier.cancelSearch();
        }

    public void cancelFill()
        {
        cancelFillFlag = true;
        }
    
    public ResultsData getResultsData() {
        //System.out.println( "entered jfilecopy getResultsData()" );
        ResultsData resultsData = new ResultsData();
        try {
            resultsData = new ResultsData( cancelFlag, copier.getProcessStatus(), copier.getMessage(), copier.getNumTested(), copier.getNumFileMatches(), copier.getNumFolderMatches(), copier.getNumFileTests(), copier.getNumFolderTests() );
            }
        catch( Exception ex )
            {
            ex.printStackTrace();
            }
        //ResultsData resultsData = new ResultsData();
        return resultsData;
    }
    
    static void usage() {
        System.out.println("jFileCopy <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public void run()  // CopyFrameSwingWorker swingWorker ) 
        {
        System.out.println( "toPath =" + toPath + "=" );
        
        copier = new Copier( isDoingCutFlag, copyOptions );   //, swingWorker );
        try {
            synchronized( dataSyncLock ) 
                {
                cancelFlag = false;
                cancelFillFlag = false;
                for ( Path fpath : copyPaths )
                    {
                    System.out.println( "\n-------  new filewalk: copy path =" + fpath + "=" );
                    copier.setPaths( fpath, startingPath, toPath );
                    Files.walkFileTree( fpath, fileVisitOptions, Integer.MAX_VALUE, copier );
                    
                    //break;  for testing to do just 1st path
                    }
                }
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JFileCopy.class.getName()).log(Level.SEVERE, null, ex);
            }
        copier.done();
        if ( copier.getProcessStatus().equals( "" ) )
            {
            copier.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_COMPLETED );
            }
        }
        
    public static void main(String[] args) throws IOException 
        {
//        if (args.length < 3
//            || !args[1].equals("-name"))
//            usage();

//        Path startingDir = Paths.get(args[0]);
//        String pattern = args[2];

//        startingPath = "F:/data";
//        filePattern = "*.xml";
//        startingPath = args[0];
//        filePattern = args[1];
        System.out.println("java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

//        JFileCopy jfilefinder = new JFileCopy( args[0], args[1], args[2], null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
}    
