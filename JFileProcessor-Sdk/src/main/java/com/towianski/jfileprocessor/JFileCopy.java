package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.ResultsData;
import com.towianski.sshutils.Sftp;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    ArrayList<String> copyPaths = new ArrayList<String>();
    String toPath = null;
    Boolean dataSyncLock = false;
    Copier copier = null;
    CopierNonWalker copierNonWalker = null;
    EnumSet<FileVisitOption> fileVisitOptions = null;
//    private CopyOption[] copyOptions = null;
    ArrayList<CopyOption> copyOptions = new ArrayList<CopyOption>();
    ConnUserInfo connUserInfo = null;
    
    public JFileCopy( ConnUserInfo connUserInfo, Boolean isDoingCutFlag, String startingPath, ArrayList<String> copyPaths, String toPath, EnumSet<FileVisitOption> fileVisitOptions, ArrayList<CopyOption> copyOptions )
    {
//        this.copyFrame = copyFrame;\
        this.connUserInfo = connUserInfo;
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
            System.out.println( "JFileCopy.getResultsData() connUserInfo.getCopyProcotol() =" + connUserInfo.getCopyProcotol() + "=" );
//            if ( connUserInfo.isUsingSftp() )
            if ( connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_LOCAL )
                {
                resultsData = new ResultsData( cancelFlag, copier.getProcessStatus(), copier.getMessage(), copier.getNumTested(), copier.getNumFileMatches(), copier.getNumFolderMatches(), copier.getNumFileTests(), copier.getNumFolderTests() );
                }
            else
                {
                System.out.println("jFileCopy use copierNonWalker results" );
                resultsData = new ResultsData( cancelFlag, copierNonWalker.getProcessStatus(), copierNonWalker.getMessage(), copierNonWalker.getNumTested(), copierNonWalker.getNumFileMatches(), copierNonWalker.getNumFolderMatches(), copierNonWalker.getNumFileTests(), copierNonWalker.getNumFolderTests() );
                }
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

    public void run( CopyFrameSwingWorker swingWorker ) 
        {
        System.out.println( "JFileCopy.run() toPath =" + toPath + "=" );
        System.out.println( "connUserInfo() =" + connUserInfo );
        
        if ( connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_LOCAL )
            {
            copier = new Copier( isDoingCutFlag, copyOptions, swingWorker );
            try {
                synchronized( dataSyncLock ) 
                    {
                    cancelFlag = false;
                    cancelFillFlag = false;
                    for ( String pathstr : copyPaths )
                        {
                        System.out.println( "\n-------  new filewalk: copy path =" + pathstr + "=" );
                        copier.setPaths( Paths.get( pathstr ), startingPath, toPath );
                        Files.walkFileTree( Paths.get( pathstr ), fileVisitOptions, Integer.MAX_VALUE, copier );

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
        else
            {
            Sftp sftp = null;
            if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
                 connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
                {
                sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
                }
            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
                {
                sftp = new Sftp( connUserInfo.getFromUser(), connUserInfo.getFromPassword(), connUserInfo.getFromHost() );
                }
            copierNonWalker = new CopierNonWalker( connUserInfo, sftp, isDoingCutFlag, copyOptions, swingWorker );

            try {
                synchronized( dataSyncLock ) 
                    {
                    cancelFlag = false;
                    cancelFillFlag = false;
                    for ( String pathstr : copyPaths )
                        {
                        System.out.println( "\n-------  new CopierNonWalker: copy path =" + pathstr + "=" );
                        copierNonWalker.setPaths( Paths.get( pathstr ), startingPath, toPath, connUserInfo );
                        copierNonWalker.copyRecursive( pathstr );  // toPath gets calced to targetPath from setPaths()

                        //break;  for testing to do just 1st path
                        }
                    copierNonWalker.done();
                    if ( copierNonWalker.getProcessStatus().equals( "" ) )
                        {
                        copierNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_COMPLETED );
                        }
                    }
                } 
            catch (Exception ex) 
                {
                Logger.getLogger(JFileCopy.class.getName()).log(Level.SEVERE, null, ex);
                }
            finally
                {
                sftp.close();
                }
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
