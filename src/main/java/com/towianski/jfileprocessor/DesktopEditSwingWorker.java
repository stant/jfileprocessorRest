/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.httpsutils.HttpsUtils;
import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.utils.MyLogger;
import java.awt.Desktop;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author Stan Towianski - June 2015
 */
public class DesktopEditSwingWorker extends SwingWorker<Void, String> {

    private static final MyLogger logger = MyLogger.getLogger(DesktopEditSwingWorker.class.getName() );
    JFileFinderWin jFileFinderWin = null;
    String filestr = null;
    int rowIndex = -1;

    public DesktopEditSwingWorker( JFileFinderWin jFileFinderWin, String filestr, int rowIndex )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.filestr = filestr;
        this.rowIndex = rowIndex;
        }

    @Override
    public Void doInBackground() {
        jFileFinderWin.stopDirWatcher();
        
        String rmtFile = filestr;
        File file = new File( filestr.replace( "\\", "/" ) );
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            logger.info( "Desktop is not supported");
            return null;
            }
         
        logger.info( "do Desktop Edit" );
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( jFileFinderWin.getConnUserInfo().isConnectedFlag()  &&   //rmtConnectBtn.getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) &&
                 ! jFileFinderWin.getStdOutFile().getText().equals( filestr ) && ! jFileFinderWin.getStdErrFile().getText().equals( filestr ) )
                {
                jFileFinderWin.setActionMessage( "Get remote file" );
                if ( jFileFinderWin.getConnUserInfo().isUsingSftp() )
                    {
                    JschSftpUtils jschSftpUtils = new JschSftpUtils();
                    jschSftpUtils.SftpGet( filestr, jFileFinderWin.getRmtUser(), jFileFinderWin.getRmtPasswd(), jFileFinderWin.getRmtHost(), jFileFinderWin.getRmtSshPort(), jFileFinderWin.JfpHomeTempDir + file.getName() );
                    file = new File( jFileFinderWin.JfpHomeTempDir + file.getName() );
                    }
                else if ( jFileFinderWin.getConnUserInfo().isUsingHttps() )
                    {
                    HttpsUtils httpsUtils = new HttpsUtils( "TO", jFileFinderWin.getConnUserInfo() );
                    httpsUtils.getFileViaRestTemplate( filestr, jFileFinderWin.JfpHomeTempDir + file.getName() );
                    file = new File( jFileFinderWin.JfpHomeTempDir + file.getName() );
                    }
                }

            if ( file.exists() )
                {
                //desktop.edit( file );
                logger.info( "start Cmd =" + jFileFinderWin.getMyEditorCmd() + " " + file.toString() + "=" );
                jFileFinderWin.setActionMessage( "Run file" );
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.exec( jFileFinderWin.getConnUserInfo().isConnectedFlag() ? jFileFinderWin.JfpHomeTempDir : jFileFinderWin.getStartingFolder(), true, false, jFileFinderWin.getMyEditorCmd(), file.toString() );
                logger.info( "javaprocess.exec start new window rc = " + rc + "=" );
                }
            if ( jFileFinderWin.getConnUserInfo().isConnectedFlag()  &&   //rmtConnectBtn.getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) &&
                 ! jFileFinderWin.getStdOutFile().getText().equals( filestr ) && ! jFileFinderWin.getStdErrFile().getText().equals( filestr ) )
                {
                if ( jFileFinderWin.getConnUserInfo().isUsingSftp() )
                    {
                    SftpReturnFileFrame sftpReturnFileFrame = new SftpReturnFileFrame( jFileFinderWin.getConnUserInfo(), jFileFinderWin.JfpHomeTempDir + file.getName(), jFileFinderWin.getRmtUser(), jFileFinderWin.getRmtPasswd(), jFileFinderWin.getRmtHost(), rmtFile.toString() );
                    sftpReturnFileFrame.validate();
                    sftpReturnFileFrame.pack();
                    sftpReturnFileFrame.setVisible(true);
                    }
                else if ( jFileFinderWin.getConnUserInfo().isUsingHttps() )
                    {
                    HttpsReturnFileFrame httpsReturnFileFrame = new HttpsReturnFileFrame( jFileFinderWin.getConnUserInfo(), jFileFinderWin.JfpHomeTempDir + file.getName(), jFileFinderWin.getRmtUser(), jFileFinderWin.getRmtPasswd(), jFileFinderWin.getRmtHost(), rmtFile.toString() );
                    httpsReturnFileFrame.validate();
                    httpsReturnFileFrame.pack();
                    httpsReturnFileFrame.setVisible(true);
                    }
//                
//                Process p = Runtime.getRuntime().exec("start /wait cmd /K " + file);
//                int exitVal = p.waitFor();
                }
            }
        catch (Exception ex) 
            {
            logger.severeExc(ex);
//            JOptionPane.showMessageDialog( this, "Edit not supported in this desktop.\nWill try Open.", "Error", JOptionPane.ERROR_MESSAGE );
 // FIXXX           desktopOpen( filestr, JfpConstants.ASSOC_CMD_TYPE_EXEC, rowIndex );
            }
        finally
            {
            jFileFinderWin.setActionMessage( "done" );
            }
        return null;
    }

    @Override
    protected void process(List<String> numList ) {
        String lastMsg = numList.get( numList.size() - 1 );
        jFileFinderWin.setActionMessage( lastMsg );
        }

    @Override
    public void done() {
        try {
            //logger.info( "entered SwingWork.done()" );
            get();
            jFileFinderWin.setActionMessage( "done" );
//            SwingUtilities.invokeLater(new Runnable() 
//                {
//                public void run() {
                    try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException ex) {
                        Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    jFileFinderWin.setActionMessage( "" );
//                    }
//                });
            }
        catch (InterruptedException ignore) 
            {}
        catch (java.util.concurrent.ExecutionException e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            logger.info( "Error in DesktopFrameSwingWorker(): " + why);
            }
    }    
}
