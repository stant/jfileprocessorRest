package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ConnUserInfo;
import com.towianski.models.ResultsData;
import com.towianski.sshutils.Sftp;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;



public class JFileDelete //  implements Runnable 
{
    Boolean deleteFilesOnlyFlag = false;
    boolean deleteToTrashFlag = true;
    Boolean deleteReadonlyFlag = false;
    Boolean cancelFlag = false;
    String startingPath = null;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    Boolean dataSyncLock = false;
    Deleter deleter = null;
    DeleterNonWalker deleterNonWalker = null;
    ConnUserInfo connUserInfo = null;
    int fsType = -1;
    
    public JFileDelete( ConnUserInfo connUserInfo, String startingPath, ArrayList<Path> copyPaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag, int fsType )
        {
        this.connUserInfo = connUserInfo;
        this.startingPath = startingPath;
        this.copyPaths = copyPaths;
        this.deleteFilesOnlyFlag = deleteFilesOnlyFlag;
        this.deleteToTrashFlag = deleteToTrashFlag;
        this.deleteReadonlyFlag = deleteReadonlyFlag;
        this.fsType = fsType;
        cancelFlag = false;
        }

    public void cancelSearch()
        {
        cancelFlag = true;
        deleter.cancelSearch();
        }

    public ResultsData getResultsData() {
        //System.out.println( "entered jfilecopy getResultsData()" );
        ResultsData resultsData = new ResultsData();
        try {
            if ( connUserInfo.isUsingSftp() )
                {
                System.out.println("jFileCopy use deleterNonWalker results" );
                resultsData = new ResultsData( cancelFlag, deleterNonWalker.getProcessStatus(), deleterNonWalker.getMessage(), deleterNonWalker.getNumTested(), deleterNonWalker.getNumFilesDeleted(), deleterNonWalker.getNumFoldersDeleted() );
                }
            else
                {
                resultsData = new ResultsData( cancelFlag, deleter.getProcessStatus(), deleter.getMessage(), deleter.getNumTested(), deleter.getNumFilesDeleted(), deleter.getNumFoldersDeleted() );
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
        System.out.println("jFileDelete <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public void run( DeleteFrameSwingWorker swingWorker )
        {
        if ( connUserInfo.isUsingSftp() )
            {
            Sftp sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
            com.jcraft.jsch.ChannelSftp chanSftp = sftp.getSftp();
            deleterNonWalker = new DeleterNonWalker( connUserInfo, chanSftp, startingPath, copyPaths, deleteFilesOnlyFlag, deleteToTrashFlag, deleteReadonlyFlag, fsType, swingWorker );

            try {
                synchronized( dataSyncLock ) 
                    {
                    cancelFlag = false;
                    for ( Path fpath : copyPaths )
                        {
                        if ( fpath.toFile().exists() || Files.isSymbolicLink( fpath ) )
                            {
                            System.out.println( "\n-------  new DeleterNonWalker: copy path =" + fpath + "=" );
                            deleterNonWalker.deleteRecursive( new File( fpath.toString() ) );  // toPath gets calced to targetPath from setPaths()

                            //break;  for testing to do just 1st path
                            }
                        }
                    deleterNonWalker.done();
//                    if ( deleterNonWalker.getProcessStatus().equals( "" ) )
//                        {
//                        deleterNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_COMPLETED );
//                        }
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
        else
            {
            deleter = new Deleter( startingPath, copyPaths, deleteFilesOnlyFlag, deleteToTrashFlag, deleteReadonlyFlag, fsType, swingWorker );
//            synchronized( dataSyncLock ) 
//                {
                cancelFlag = false;
                for ( Path fpath : copyPaths )
                    {
                    System.out.println( "delete path =" + fpath + "=" );
                    //EnumSet<FileVisitOption> opts = EnumSet.of( FOLLOW_LINKS );
                    if ( fpath.toFile().exists() || Files.isSymbolicLink( fpath ) )
                        {
                        try {
                            Files.walkFileTree( fpath, deleter );
                            } 
                        catch (IOException ioex) 
                            {
                            //System.out.println( "up ERROR  " + "my error getSimpleName" + ioex.getClass().getSimpleName() );
                            System.out.println( "delete io ERROR  " + "my error msg" + ioex.getMessage() );
                            JOptionPane.showMessageDialog( null, ioex.getClass().getSimpleName() + ": " + ioex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                            Logger.getLogger(JFileDelete.class.getName()).log(Level.SEVERE, null, ioex);
                            }
                        catch (Exception ex) 
                            {
                            JOptionPane.showMessageDialog( null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                            Logger.getLogger(JFileDelete.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println( "delete ERROR  " + "my error msg" + ex.getMessage() );
                            }
                        }
                    
                    //break;  for testing to do just 1st path
                    }
//                }
            deleter.done();
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
        System.out.println("java Delete args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

//        JFileDelete jfiledeleter = new JFileDelete( args[0], args[1], args[2], null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(JFileFinder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
}    
