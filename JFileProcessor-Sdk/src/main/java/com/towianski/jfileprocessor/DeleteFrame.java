/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.CloseWinOnTimer;
import static com.towianski.jfileprocessor.DeleteFrame.PROCESS_STATUS_DELETE_CANCEL;
import static com.towianski.jfileprocessor.DeleteFrame.PROCESS_STATUS_DELETE_CANCELED;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.DeleteModel;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.models.ResultsData;
import com.towianski.utils.Rest;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.springframework.web.client.RestTemplate;

/**
 *
* @author Stan Towianski July 2015
  */
public class DeleteFrame extends javax.swing.JFrame {

    JFileFinderWin jFileFinderWin = null;
    Thread jfinderThread = null;
    JFileFinderSwingWorker jFileFinderSwingWorker = null;
    ResultsData resultsData = null;
    JFileDelete jfiledelete = null;
    Color saveColor = null;
    
    public static final String PROCESS_STATUS_DELETE_STARTED = "Delete Started . . .";
    public static final String PROCESS_STATUS_DELETE_CANCELED = "Delete canceled";
    public static final String PROCESS_STATUS_DELETE_COMPLETED = "Delete completed";
    public static final String PROCESS_STATUS_DELETE_CANCEL = "Cancel Delete";
    public static final String PROCESS_STATUS_DELETE_READY = "Delete";

    boolean cancelFlag = false;
    String startingPath = null;
//    ArrayList<Path> deletePaths = new ArrayList<Path>();
    ArrayList<String> deletePaths = new ArrayList<String>();
    Deleter deleter = null;
    Boolean dataSyncLock = false;
    ConnUserInfo connUserInfo = null;
    
    /**
     * Creates new form DeleteFrame
     */
    public DeleteFrame() {
        initComponents();

        this.setLocationRelativeTo( getRootPane() );
        this.addEscapeListener( this );
        this.getRootPane().setDefaultButton( doCmdBtn );
        doCmdBtn.requestFocusInWindow();
    }

    public void setup( JFileFinderWin jFileFinderWin, ConnUserInfo connUserInfo, String startingPath, ArrayList<String> deletePaths )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.connUserInfo = connUserInfo;
        this.startingPath = startingPath;
        this.deletePaths = deletePaths;
        
        fromPath.setText( deletePaths.get( 0 ).toString() );
        if ( deletePaths.size() > 1 )
            {
            fromPath.setText( deletePaths.get( 0 ) + " + others" );
            }
        }
    
    public void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println( "previewImportWin formWindow dispose()" );
                win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }    

    public boolean getDeleteToTrashFlag() {
        return this.deleteToTrashFlag.isSelected();
    }

    public void setDeleteToTrashFlag(boolean deleteToTrashFlag) {
        this.deleteToTrashFlag.setSelected( deleteToTrashFlag );
    }

    public void callDoCmdBtnActionPerformed(java.awt.event.ActionEvent evt)
    {
        doCmdBtnActionPerformed( evt );
    }
            
    public void stopSearch() {
        jfiledelete.cancelSearch();
    }

    public void setDoCmdBtn( String text, Color setColor )
        {
        doCmdBtn.setText( text );
        doCmdBtn.setBackground( setColor );
        doCmdBtn.setOpaque(true);
        }

    public void setResultsData( ResultsData resultsData )
        {
        this.resultsData = resultsData;
        }

    public void setProcessStatus( String text )
        {
        processStatus.setText(text);
        switch( text )
            {
            case PROCESS_STATUS_DELETE_STARTED:  
                processStatus.setBackground( Color.GREEN );
                setDoCmdBtn( this.PROCESS_STATUS_DELETE_CANCEL, Color.RED );
                setMessage( "" );
                break;
            case PROCESS_STATUS_DELETE_CANCELED:
                processStatus.setBackground( Color.YELLOW );
                setDoCmdBtn( this.PROCESS_STATUS_DELETE_READY, saveColor );
                break;
            case PROCESS_STATUS_DELETE_COMPLETED:
                processStatus.setBackground( saveColor );
                setDoCmdBtn( this.PROCESS_STATUS_DELETE_READY, saveColor );
                doCmdBtn.setEnabled(false);
                break;
            default:
                processStatus.setBackground( saveColor );
                setDoCmdBtn( this.PROCESS_STATUS_DELETE_READY, saveColor );
                break;
            }
        }

    public String getProcessStatus()
        {
        return processStatus.getText();
        }

    public String getMessage()
        {
        return message.getText();
        }

    public void setMessage( String text )
        {
        message.setText(text);
        }

    public void stopDirWatcher()
        {
        jFileFinderWin.stopDirWatcher();
        }

    public void callSearchBtnActionPerformed()
        {
        jFileFinderWin.callSearchBtnActionPerformed( null );
        }

    ActionListener menuActionListener = new ActionListener()
        {
        @Override
        public void actionPerformed(ActionEvent e) 
            {
            message.setText(e.getActionCommand());
            }          
        };
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        doCmdBtn = new javax.swing.JButton();
        processStatus = new javax.swing.JLabel();
        message = new javax.swing.JLabel();
        fromPath = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        deleteFilesOnlyFlag = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        deleteToTrashFlag = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        deleteReadonlyFlag = new javax.swing.JCheckBox();
        showProgressTb = new javax.swing.JToggleButton();
        closeWhenDoneTb = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Delete");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        doCmdBtn.setText("Delete");
        doCmdBtn.setMaximumSize(new java.awt.Dimension(150, 23));
        doCmdBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                doCmdBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(doCmdBtn, gridBagConstraints);

        processStatus.setMaximumSize(new java.awt.Dimension(999999, 999999));
        processStatus.setMinimumSize(new java.awt.Dimension(150, 25));
        processStatus.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 0, 0);
        getContentPane().add(processStatus, gridBagConstraints);

        message.setMaximumSize(new java.awt.Dimension(999999, 999999));
        message.setMinimumSize(new java.awt.Dimension(200, 25));
        message.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 0, 5);
        getContentPane().add(message, gridBagConstraints);

        fromPath.setText("   ");
        fromPath.setMaximumSize(new java.awt.Dimension(99999, 99999));
        fromPath.setMinimumSize(new java.awt.Dimension(300, 25));
        fromPath.setPreferredSize(new java.awt.Dimension(300, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
        getContentPane().add(fromPath, gridBagConstraints);

        jLabel1.setText("From Path(s):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        deleteFilesOnlyFlag.setText("Delete Files Only (leave folders)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(deleteFilesOnlyFlag, gridBagConstraints);

        jLabel2.setMaximumSize(new java.awt.Dimension(999, 999));
        jLabel2.setMinimumSize(new java.awt.Dimension(20, 15));
        jLabel2.setPreferredSize(new java.awt.Dimension(20, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(jLabel2, gridBagConstraints);

        deleteToTrashFlag.setSelected(true);
        deleteToTrashFlag.setText("Delete to Trash");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(deleteToTrashFlag, gridBagConstraints);

        jLabel3.setMinimumSize(new java.awt.Dimension(20, 15));
        jLabel3.setPreferredSize(new java.awt.Dimension(20, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        getContentPane().add(jLabel3, gridBagConstraints);

        deleteReadonlyFlag.setText("Delete Read-Only");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(deleteReadonlyFlag, gridBagConstraints);

        showProgressTb.setSelected(true);
        showProgressTb.setText("Show Progress");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        getContentPane().add(showProgressTb, gridBagConstraints);

        closeWhenDoneTb.setSelected(true);
        closeWhenDoneTb.setText("Close When Done");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(closeWhenDoneTb, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doCmdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCmdBtnActionPerformed
        //JOptionPane.showConfirmDialog( null, "doCmdBtnActionPerformed =" + evt.getSource() );

        if ( connUserInfo.isConnectedFlag() )   //.getRmtConnectBtn().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) )
            {
//            deleteBtnActionSwing( evt );
            deleteBtnActionRest( evt );
            }
        else
            {
            deleteBtnActionSwing( evt );
            }
    }//GEN-LAST:event_doCmdBtnActionPerformed

    public void deleteBtnActionSwing( java.awt.event.ActionEvent evt )
        {
        if ( doCmdBtn.getText().equalsIgnoreCase( PROCESS_STATUS_DELETE_CANCEL ) )
            {
            System.out.println( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_DELETE_CANCELED );
            this.stopSearch();
            //JOptionPane.showConfirmDialog( null, "at call stop search" );
            }
        else
            {
            try {
                //JOptionPane.showConfirmDialog( null, "hit key" );  if ( 1 == 1 ) return;
                jfiledelete = new JFileDelete( connUserInfo, startingPath, deletePaths, deleteFilesOnlyFlag.isSelected(), deleteToTrashFlag.isSelected(), deleteReadonlyFlag.isSelected(), jFileFinderWin.getFilesysType() );
                DeleteFrameSwingWorker deleteFrameSwingWorker = new DeleteFrameSwingWorker( this, jfiledelete, deletePaths, showProgressTb.isSelected(), closeWhenDoneTb.isSelected() );
                deleteFrameSwingWorker.execute();   //doInBackground();
               } 
            catch (Exception ex)
                {
                Logger.getLogger(JFileDelete.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    
    public void deleteBtnActionRest( java.awt.event.ActionEvent evt )
        {
        System.out.println("jfilewin deleteBtnActionRest()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        stopDirWatcher();

        DeleteModel deleteModel = extractDeleteModel();
        Rest.saveObjectToFile( "DeleteModel.json", deleteModel );
//        RestTemplate restTemplate = new RestTemplate();
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        //we can't get List<Employee> because JSON convertor doesn't know the type of
        //object in the list and hence convert it to default JSON object type LinkedHashMap
//        FilesTblModel filesTblModel = restTemplate.getForObject( SERVER_URI+JfpRestURIConstants.GET_FILES, FilesTblModel.class, SearchModel.class );

        System.out.println( "rest send deleteModel =" + deleteModel + "=" );

        String response = noHostVerifyRestTemplate.postForEntity( connUserInfo.getToUri() + JfpRestURIConstants.DELETE, deleteModel, String.class).getBody();
        System.out.println( "response =" + response + "=" );
        resultsData = Rest.jsonToObject( response, ResultsData.class );
        System.out.println( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );

        // from deleteframeswingworker done()
        try {
            //System.out.println( "entered SwingWork.done()" );
            NumberFormat numFormat = NumberFormat.getIntegerInstance();
            String partialMsg = "";
            String msg = "Deleted " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) + " folders out of " + numFormat.format( resultsData.getFilesVisited() );
            if ( ! resultsData.getMessage().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }
            
            if ( resultsData.getSearchWasCanceled() )
                {
                this.setProcessStatus( this.PROCESS_STATUS_DELETE_CANCELED );
                msg = msg + " PARTIAL files list.";
                }
            else
                {
                if ( resultsData.getProcessStatus().equals( this.PROCESS_STATUS_DELETE_COMPLETED ) )
                    {
                    new CloseWinOnTimer( this, closeWhenDoneTb.isSelected()? 4000 : 0 ){{setRepeats(false);}}.start();
                    }
                }
            if ( ! resultsData.getProcessStatus().trim().equals( "" ) )
                {
                setProcessStatus( resultsData.getProcessStatus() );
                }
            if ( ! resultsData.getMessage().trim().equals( "" ) )
                {
                msg = resultsData.getMessage();
                }

            this.setMessage( msg + partialMsg );
            this.setResultsData( resultsData );
            
            // clean up
            resultsData = null;
            jfiledelete = null;
            deletePaths = null;            
            
            this.callSearchBtnActionPerformed();
            //System.out.println( "exiting SwingWork.done()" );
            }
        catch ( Exception e ) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.out.println( "Error in DeleteFrameSwingWorker(): " + why);
            }
        }

    public DeleteModel extractDeleteModel()
        {
        System.out.println("jfilewin extractDeleteModel() " );  //searchBtn.getText() =" + searchBtn.getText() + "=" );
        DeleteModel deleteModel = new DeleteModel();
//      filedelete = new JFileDelete( connUserInfo, startingPath, deletePaths, deleteFilesOnlyFlag.isSelected(), 
//            deleteToTrashFlag.isSelected(), deleteReadonlyFlag.isSelected(), jFileFinderWin.getFilesysType() );

                ArrayList<CopyOption> copyOpts = new ArrayList<CopyOption>();
                EnumSet<FileVisitOption> fileVisitOptions = EnumSet.noneOf( FileVisitOption.class );
//                if ( noFollowLinks.isSelected() )
//                    {
//                    copyOpts.add( LinkOption.NOFOLLOW_LINKS );
//                    }
//                else
//                    {
//                    fileVisitOptions = EnumSet.of( FOLLOW_LINKS );
//                    }

        deleteModel.setConnUserInfo( connUserInfo );
        deleteModel.setStartingPath( startingPath );        
        deleteModel.setDeletePaths( deletePaths );
        deleteModel.setDeleteFilesOnlyFlag( deleteFilesOnlyFlag.isSelected() );
        deleteModel.setDeleteToTrashFlag( deleteToTrashFlag.isSelected() );
        deleteModel.setDeleteReadonlyFlag( deleteReadonlyFlag.isSelected() );
        deleteModel.setFileVisitOptions( fileVisitOptions );
        deleteModel.setCopyOpts( copyOpts );
        deleteModel.setFilesysType( connUserInfo.getToFilesysType() );
        deleteModel.setShowProgressFlag( showProgressTb.isEnabled() );
        
//    int filesysType = FILESYSTEM_DOS;
        return deleteModel;
        }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DeleteFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeleteFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeleteFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeleteFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DeleteFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton closeWhenDoneTb;
    private javax.swing.JCheckBox deleteFilesOnlyFlag;
    private javax.swing.JCheckBox deleteReadonlyFlag;
    private javax.swing.JCheckBox deleteToTrashFlag;
    private javax.swing.JButton doCmdBtn;
    private javax.swing.JLabel fromPath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel message;
    private javax.swing.JLabel processStatus;
    private javax.swing.JToggleButton showProgressTb;
    // End of variables declaration//GEN-END:variables
}
