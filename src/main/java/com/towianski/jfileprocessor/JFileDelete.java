package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.models.ConnUserInfo;
import com.towianski.models.ResultsData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
//    ArrayList<Path> deletePaths = new ArrayList<Path>();
    ArrayList<String> deletePaths = new ArrayList<String>();
    Boolean dataSyncLock = false;
    Deleter deleter = null;
    Chmod chmoder = null;
    DeleterNonWalker deleterNonWalker = null;
    ConnUserInfo connUserInfo = null;
    int fsType = -1;
    
    public JFileDelete( ConnUserInfo connUserInfo, String startingPath, ArrayList<String> deletePaths, Boolean deleteFilesOnlyFlag, Boolean deleteToTrashFlag, Boolean deleteReadonlyFlag, int fsType )
        {
        this.connUserInfo = connUserInfo;
        this.startingPath = startingPath;
        this.deletePaths = deletePaths;
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
//            if ( connUserInfo.isUsingSftp() )
//                {
//                System.out.println("jFileCopy use deleterNonWalker results" );
//                resultsData = new ResultsData( cancelFlag, deleterNonWalker.getProcessStatus(), deleterNonWalker.getMessage(), deleterNonWalker.getNumTested(), deleterNonWalker.getNumFilesDeleted(), deleterNonWalker.getNumFoldersDeleted() );
//                }
//            else
//                {
                resultsData = new ResultsData( cancelFlag, deleter.getProcessStatus(), deleter.getMessage(), deleter.getNumTested(), deleter.getNumFilesDeleted(), deleter.getNumFoldersDeleted() );
//                }
            }
        catch( Exception ex )
            {
            System.out.println( "ERROR in jFileDelete.getResultsData()" );
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
        System.out.println( "JFileDelete.run() connUserInfo.isUsingSftp() =" + connUserInfo.isUsingSftp() + "=" );
//        if ( connUserInfo.isUsingSftp() )
//            {
//            Sftp sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
//            com.jcraft.jsch.ChannelSftp chanSftp = sftp.getSftp();
//            deleterNonWalker = new DeleterNonWalker( connUserInfo, chanSftp, startingPath, deletePaths, deleteFilesOnlyFlag, deleteToTrashFlag, deleteReadonlyFlag, fsType, swingWorker );
//
//            try {
//                synchronized( dataSyncLock ) 
//                    {
//                    cancelFlag = false;
//                    for ( Path fpath : deletePaths )
//                        {
//                        System.out.println( "ck delete path =" + fpath + "=" );
//                        String filename = fpath.toString().replace( "\\", "/" );
//                        System.out.println( "ck delete path =" + filename + "=" );
//                        if ( fpath.toFile().exists() || Files.isSymbolicLink( fpath ) )
////                        if ( chanSftp.lstat( filename ).getSize() >= 0 || chanSftp.lstat( filename ).isLink() )
//                            {
//                            System.out.println( "\n-------  new DeleterNonWalker: copy path =" + filename + "=" );
//                            deleterNonWalker.deleteRecursiveRemote( filename );  // toPath gets calced to targetPath from setPaths()
//
//                            //break;  for testing to do just 1st path
//                            }
//                        }
//                    deleterNonWalker.done();
////                    if ( deleterNonWalker.getProcessStatus().equals( "" ) )
////                        {
////                        deleterNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_COMPLETED );
////                        }
//                    }
//                } 
//            catch (Exception ex) 
//                {
//                Logger.getLogger(JFileCopy.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            finally
//                {
//                sftp.close();
//                }
//            }
        // Skip using filewalker at all for deletes
//        else
//            {
            chmoder = new Chmod( startingPath, deletePaths, deleteFilesOnlyFlag, deleteToTrashFlag, deleteReadonlyFlag, fsType, swingWorker );
            deleter = new Deleter( startingPath, deletePaths, deleteFilesOnlyFlag, deleteToTrashFlag, deleteReadonlyFlag, fsType, swingWorker );
//            synchronized( dataSyncLock ) 
//                {
                cancelFlag = false;
                try {
                    for ( String strpath : deletePaths )
                        {
                        System.out.println( "delete strpath =" + strpath + "=   deleteReadonlyFlag =" + deleteReadonlyFlag );
                        if ( deleteReadonlyFlag )
                            {
                            chmoder.chmodRecursive( strpath, "777" );
                            }
                        Path fpath = Paths.get( strpath );
                        //EnumSet<FileVisitOption> opts = EnumSet.of( FOLLOW_LINKS );
                        if ( fpath.toFile().exists() || Files.isSymbolicLink( fpath ) )
                            {
                            Files.walkFileTree( fpath, deleter );
                            }

                        //break;  for testing to do just 1st path
                        }
                    } 
//                catch ( java.nio.file.AccessDeniedException exAccessDenied ) 
                catch (IOException ioex) 
                    {
                    //System.out.println( "up ERROR  " + "my error getSimpleName" + ioex.getClass().getSimpleName() );
                    System.out.println( "delete io ERROR:  " + ioex );
                    deleter.setProcessStatus( DeleteFrame.PROCESS_STATUS_DELETE_INCOMPLETE );
                    deleter.setMessage( ioex.toString() );
                    if ( ! connUserInfo.isConnectedFlag() )
                        {
                        JOptionPane.showMessageDialog( null, ioex.getClass().getSimpleName() + ": " + ioex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    Logger.getLogger(JFileDelete.class.getName()).log(Level.SEVERE, null, ioex);
                    ioex.printStackTrace();
                    }
                catch (Exception ex) 
                    {
                    deleter.setProcessStatus( DeleteFrame.PROCESS_STATUS_DELETE_INCOMPLETE );
                    deleter.setMessage( ex.toString() );
                    if ( ! connUserInfo.isConnectedFlag() )
                        {
                        JOptionPane.showMessageDialog( null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    Logger.getLogger(JFileDelete.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println( "delete ERROR:  " + ex );
                    }
//                }
            deleter.done();
            if ( deleter.getProcessStatus().equals( "" ) )
                {
                deleter.setProcessStatus( DeleteFrame.PROCESS_STATUS_DELETE_COMPLETED );
                }
//            }
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
