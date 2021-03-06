package com.towianski.jfileprocessor;

/**
 *
 * @author Stan Towianski - June 2015
 */

import com.towianski.httpsutils.HttpsUtils;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.ResultsData;
import com.towianski.sshutils.Sftp;
import com.towianski.utils.FileUtils;
import com.towianski.utils.MyLogger;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;



public class JFileCopy //  implements Runnable 
    {
    private static final MyLogger logger = MyLogger.getLogger( JFileCopy.class.getName() );
    Boolean cancelFlag = false;
    String processStatus = "";
    String message = "";
    Boolean cancelFillFlag = false;
    boolean copySpecificListOfFiles = false;
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
    
//    public JFileCopy( ConnUserInfo connUserInfo, Boolean isDoingCutFlag, String startingPath, boolean copySpecificListOfFiles, ArrayList<String> copyPaths, String toPath, EnumSet<FileVisitOption> fileVisitOptions, ArrayList<CopyOption> copyOptions )
    public JFileCopy( ConnUserInfo connUserInfo, Boolean isDoingCutFlag, String startingPath, ArrayList<String> copyPaths, String toPath, EnumSet<FileVisitOption> fileVisitOptions, ArrayList<CopyOption> copyOptions )
    {
//        this.copyFrame = copyFrame;\
        this.connUserInfo = connUserInfo;
        this.isDoingCutFlag = isDoingCutFlag;
        this.startingPath = startingPath;
        this.copySpecificListOfFiles = copySpecificListOfFiles;
        this.copyPaths = copyPaths;
        this.toPath = toPath;
        this.fileVisitOptions = fileVisitOptions;
        this.copyOptions = copyOptions;
        cancelFlag = false;
    }

    public void cancelSearch()
        {
        cancelFlag = true;
        if ( copier != null ) copier.cancelSearch();
        if ( copierNonWalker != null ) copierNonWalker.cancelSearch();
        }

    public void cancelFill()
        {
        cancelFillFlag = true;
        }
    
    public ResultsData getResultsData() {
        //logger.info( "entered jfilecopy getResultsData()" );
        ResultsData resultsData = new ResultsData();
        try {
            logger.info( "JFileCopy.getResultsData() connUserInfo.getCopyProcotol() =" + connUserInfo.getCopyProcotol() + "=" );
//            if ( connUserInfo.isUsingSftp() )
            if ( connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_LOCAL )
                {
                resultsData = new ResultsData( cancelFlag, copier.getProcessStatus(), copier.getMessage(), copier.getNumTested(), copier.getNumFileMatches(), copier.getNumFolderMatches(), copier.getNumFileTests(), copier.getNumFolderTests(), copier.getErrorList() );
                }
            else
                {
                logger.info( "jFileCopy use copierNonWalker results" );
                resultsData = new ResultsData( cancelFlag, copierNonWalker.getProcessStatus(), copierNonWalker.getMessage(), copierNonWalker.getNumTested(), copierNonWalker.getNumFileMatches(), copierNonWalker.getNumFolderMatches(), copierNonWalker.getNumFileTests(), copierNonWalker.getNumFolderTests(), copierNonWalker.getErrorList() );
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
        logger.info( "jFileCopy <path>" + " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public void run( CopyFrameSwingWorker swingWorker ) 
        {
        logger.info( "JFileCopy.run() toPath =" + toPath + "=" );
        logger.info( "connUserInfo() =" + connUserInfo );
        logger.info( "connUserInfo.getCopyProcotol() =" + connUserInfo.getCopyProcotol() );
        
        if ( connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_LOCAL )
            {
            if ( copySpecificListOfFiles )
                {
                
                }
            else
                {
                copier = new Copier( isDoingCutFlag, copyOptions, swingWorker );
                try {
                    synchronized( dataSyncLock ) 
                        {
                        cancelFlag = false;
                        cancelFillFlag = false;
                        for ( String pathstr : copyPaths )
                            {
                            logger.info( "\n" );
                            logger.info( "-------  new filewalk:           toPath =" + toPath + "=" );
                            logger.info( "      startingPath =" + startingPath + "=    pathstr =" + pathstr + "=" );
                            this.startingPath = FileUtils.getFolderFromPath( pathstr );
                            logger.info( "      startingPath =" + startingPath + "=" );
                            //logger.info( "      startingPath =" + FileUtils.getFolderFromPath( pathstr ) + "=    pathstr =" + FileUtils.getFilenameFromPath( pathstr ) + "=" );
                            copier.setPaths( Paths.get( pathstr ), startingPath,
                                                                   toPath );

                            Files.walkFileTree( Paths.get( pathstr ), fileVisitOptions, Integer.MAX_VALUE, copier );

                            //logger.info( "\n-------  for testing to copy just 1st path !" );
                            //break;  // for testing to copy just 1st path
                            }
                        }
                    }
                catch (Exception ex) 
                    {
                    ex.printStackTrace();
                    copier.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_INCOMPLETED );
                    copier.setMessage( ex.toString() );
                    copier.getErrorList().add( " -> copy path - ERROR " + ex );
                    }

                copier.done();
                if ( copier.getProcessStatus().equals( "" ) )
                    {
                    copier.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_COMPLETED );
                    }
                }
            }
        else if ( connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_HTTPS_GET || 
                  connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_HTTPS_PUT )
            {
            HttpsUtils httpsUtilsSrc = null;
            HttpsUtils httpsUtilsTar = null;
            if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
                 connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_HTTPS )  )
                {
                logger.info( "CopierNonWalker: will do https TO file" );
                httpsUtilsTar = new HttpsUtils( "TO", connUserInfo );
                if ( ! httpsUtilsTar.isConnected() )
                    {
                    cancelFlag = true;
                    copierNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_CANCELED );
                    copierNonWalker.setMessage( httpsUtilsTar.getMessage() );
                    return;
                    }
                }
            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_HTTPS ) &&
                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
                {
                logger.info( "CopierNonWalker: will do https FROM file" );
                httpsUtilsSrc = new HttpsUtils( "FROM", connUserInfo );
                if ( ! httpsUtilsSrc.isConnected() )
                    {
                    cancelFlag = true;
                    copierNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_CANCELED );
                    copierNonWalker.setMessage( httpsUtilsTar.getMessage() );
                    return;
                    }
                }
            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_HTTPS ) &&
                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_HTTPS )  )
                {
                logger.info( "CopierNonWalker: will do https to https -- like https to local" );
//                sftpSrc = new Sftp( connUserInfo.getFromUser(), connUserInfo.getFromPassword(), connUserInfo.getFromHost() );
//                sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
                httpsUtilsTar = new HttpsUtils( "TO", connUserInfo );
                if ( ! httpsUtilsTar.isConnected() )
                    {
                    cancelFlag = true;
                    copierNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_CANCELED );
                    copierNonWalker.setMessage( httpsUtilsTar.getMessage() );
                    return;
                    }
                httpsUtilsSrc = new HttpsUtils( "FROM", connUserInfo );
                if ( ! httpsUtilsSrc.isConnected() )
                    {
                    cancelFlag = true;
                    copierNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_CANCELED );
                    copierNonWalker.setMessage( httpsUtilsSrc.getMessage() );
                    return;
                    }
                }
            copierNonWalker = new CopierNonWalker( connUserInfo, httpsUtilsTar, httpsUtilsSrc, isDoingCutFlag, copyOptions, swingWorker );

            try {
                synchronized( dataSyncLock ) 
                    {
                    cancelFlag = false;
                    cancelFillFlag = false;
                        logger.info( "\n-------  new Https CopierNonWalker: copyPaths.size() =" + copyPaths.size() + "=" );
                    for ( String pathstr : copyPaths )
                        {
                        logger.info( "\n" );
                        logger.info( "-------  new Https CopierNonWalker:           toPath =" + toPath + "=" );
                        logger.info( "      startingPath =" + startingPath + "=    pathstr =" + pathstr + "=" );
                        this.startingPath = FileUtils.getFolderFromPath( pathstr );
                        logger.info( "      startingPath =" + startingPath + "=" );
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
                logger.severeExc( ex );
                ex.printStackTrace();
                }
            finally
                {
                //httpsUtilsTar.close();
                //if ( httpsUtilsSrc != null )
                    //httpsUtilsSrc.close();
                }
            }
        else if ( connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_SFTP_GET || 
                  connUserInfo.getCopyProcotol() == Constants.COPY_PROTOCOL_SFTP_PUT )
            {
            Sftp sftp = null;
            Sftp sftpSrc = null;
            if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
                 connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
                {
                //sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), connUserInfo.getToSshPortInt() );
                sftp = new Sftp( "TO", connUserInfo );
                }
            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE )  )
                {
                //sftp = new Sftp( connUserInfo.getFromUser(), connUserInfo.getFromPassword(), connUserInfo.getFromHost(), connUserInfo.getFromSshPortInt() );
                sftp = new Sftp( "FROM", connUserInfo );
                }
            else if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_SFTP ) &&
                      connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_SFTP )  )
                {
                logger.info( "CopierNonWalker: will do sftp to sftp -- like sftp to local" );
                //sftp = new Sftp( connUserInfo.getFromUser(), connUserInfo.getFromPassword(), connUserInfo.getFromHost(), connUserInfo.getFromSshPortInt() );
                sftp = new Sftp( "FROM", connUserInfo );
//                sftpSrc = new Sftp( connUserInfo.getFromUser(), connUserInfo.getFromPassword(), connUserInfo.getFromHost() );
//                sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
                }
            copierNonWalker = new CopierNonWalker( connUserInfo, sftp, sftpSrc, isDoingCutFlag, copyOptions, swingWorker );
            if ( ! sftp.isConnected() )
                {
                cancelFlag = true;
                copierNonWalker.setProcessStatus( CopyFrame.PROCESS_STATUS_COPY_CANCELED );
                copierNonWalker.setMessage( sftp.getMessage() );
                return;
                }

            try {
                synchronized( dataSyncLock ) 
                    {
                    cancelFlag = false;
                    cancelFillFlag = false;
                    for ( String pathstr : copyPaths )
                        {
                        logger.info( "\n" );
                        logger.info( "-------  new CopierNonWalker:           toPath =" + toPath + "=" );
                        logger.info( "      startingPath =" + startingPath + "=    pathstr =" + pathstr + "=" );
                        this.startingPath = FileUtils.getFolderFromPath( pathstr );
                        logger.info( "      startingPath =" + startingPath + "=" );
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
                logger.severeExc( ex );
                ex.printStackTrace();
                }
            finally
                {
                sftp.close();
                if ( sftpSrc != null )
                    sftpSrc.close();
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
        logger.info( "java Find args[0] =" + args[0] +  "=  args[1] =" + args[1] + "=  args[2] =" + args[2] + "=");

//        JFileCopy jfilefinder = new JFileCopy( args[0], args[1], args[2], null, null );

//        Thread jfinderThread = new Thread( jfilefinder );
//        jfinderThread.start();
//        try {
//            jfinderThread.join();
//        } catch (InterruptedException ex) {
//            logger.severeExc( ex );
//        }
        }
}    
