/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author Stan Towianski July 2015
 */
public class CopyFrame extends javax.swing.JFrame {

//    private final static MyLogger logger = new MyLogger( Logger.getLogger( CopyFrame.class.getName() ) );
    private final static MyLogger logger = MyLogger.getLogger( CopyFrame.class.getName() );

    JFileFinderWin jFileFinderWin = null;
    Thread jfinderThread = null;
    ResultsData resultsData = null;
    JFileCopy jfilecopy = null;
    Color saveColor = null;
    
    public static final String PROCESS_STATUS_COPY_STARTED = "Copy Started . . .";
    public static final String PROCESS_STATUS_COPY_CANCELED = "Copy canceled";
    public static final String PROCESS_STATUS_COPY_COMPLETED = "Copy completed";
    public static final String PROCESS_STATUS_COPY_INCOMPLETED = "Copy incomplete";
    public static final String PROCESS_STATUS_CANCEL_COPY = "Cancel Copy";
    public static final String PROCESS_STATUS_COPY_READY = "Copy";

    boolean cancelFlag = false;
    boolean cancelFillFlag = false;
    boolean isDoingCutFlag = false;
    String startingPath = null;
    ArrayList<Path> copyPaths = new ArrayList<Path>();
    String toPath = null;
    Copier copier = null;
    Boolean dataSyncLock = false;
    
    /**
     * Creates new form CopyFrame
     */
    public CopyFrame() {
        initComponents();
        System.out.println( "CopyFrame constructor()" );
//        MyLogger.init();
        
        this.setLocationRelativeTo( getRootPane() );
        this.addEscapeListener( this );
        this.getRootPane().setDefaultButton( doCmdBtn );
        doCmdBtn.requestFocusInWindow();
    }

    public void setup( JFileFinderWin jFileFinderWin, Boolean isDoingCutFlag, String startingPath, ArrayList<Path> copyPaths, String toPath )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.isDoingCutFlag = isDoingCutFlag;
        this.startingPath = startingPath;
        this.copyPaths = copyPaths;
        this.toPath = toPath;
        
        this.doCmdBtn.setText( isDoingCutFlag ? "Cut/Paste" : "Copy/Paste" );

        fromPath.setText( copyPaths.get( 0 ).toString() );
        if ( copyPaths.size() > 1 )
            {
            fromPath.setText( copyPaths.get( 0 ) + " + others" );
            }
        pastePath.setText( toPath );
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

    public void callDoCmdBtnActionPerformed(java.awt.event.ActionEvent evt)
    {
        doCmdBtnActionPerformed( evt );
    }
            
    public void stopSearch() {
        jfilecopy.cancelSearch();
    }

    public void stopFill() {
        jfilecopy.cancelFill();
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

    public void setCloseOnDoneTb( boolean flag )
        {
        closeWhenDoneTb.setSelected( flag );
        }

    public void setShowProgressTb( boolean flag ) {
        showProgressTb.setSelected( flag );
    }

    public void setProcessStatus(String text) {
        processStatus.setText(text);
        switch( text )
        {
            case PROCESS_STATUS_COPY_STARTED:  
                processStatus.setBackground( Color.GREEN );
                setDoCmdBtn( this.PROCESS_STATUS_CANCEL_COPY, Color.RED );
                setMessage( "" );
                break;
            case PROCESS_STATUS_COPY_CANCELED:
                processStatus.setBackground( Color.YELLOW );
                setDoCmdBtn( this.PROCESS_STATUS_COPY_READY, saveColor );
                break;
            case PROCESS_STATUS_COPY_COMPLETED:
                processStatus.setBackground( saveColor );
                setDoCmdBtn( this.PROCESS_STATUS_COPY_READY, saveColor );
                doCmdBtn.setEnabled(false);
                break;
            default:
                processStatus.setBackground( saveColor );
                setDoCmdBtn( this.PROCESS_STATUS_COPY_READY, saveColor );
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
        pastePath = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        replaceExisting = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        copyAttribs = new javax.swing.JCheckBox();
        noFollowLinks = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        showProgressTb = new javax.swing.JToggleButton();
        closeWhenDoneTb = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Paste");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        doCmdBtn.setText("Copy");
        doCmdBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                doCmdBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(doCmdBtn, gridBagConstraints);

        processStatus.setMaximumSize(new java.awt.Dimension(999999, 999999));
        processStatus.setMinimumSize(new java.awt.Dimension(150, 25));
        processStatus.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
        getContentPane().add(processStatus, gridBagConstraints);

        message.setMaximumSize(new java.awt.Dimension(999999, 999999));
        message.setMinimumSize(new java.awt.Dimension(200, 25));
        message.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 5);
        getContentPane().add(message, gridBagConstraints);

        fromPath.setText("   ");
        fromPath.setMaximumSize(new java.awt.Dimension(99999, 99999));
        fromPath.setMinimumSize(new java.awt.Dimension(300, 25));
        fromPath.setPreferredSize(new java.awt.Dimension(300, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(fromPath, gridBagConstraints);

        pastePath.setMaximumSize(new java.awt.Dimension(99999, 99999));
        pastePath.setMinimumSize(new java.awt.Dimension(300, 25));
        pastePath.setPreferredSize(new java.awt.Dimension(300, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(pastePath, gridBagConstraints);

        jLabel1.setText("From Path(s):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("To Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        replaceExisting.setText("Replace Existing");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(replaceExisting, gridBagConstraints);

        jLabel3.setMaximumSize(new java.awt.Dimension(9999, 9999));
        jLabel3.setMinimumSize(new java.awt.Dimension(40, 40));
        jLabel3.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        getContentPane().add(jLabel3, gridBagConstraints);

        copyAttribs.setText("Copy Attributes");
        copyAttribs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                copyAttribsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        getContentPane().add(copyAttribs, gridBagConstraints);

        noFollowLinks.setSelected(true);
        noFollowLinks.setText("No Follow Links (copy links as links)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        getContentPane().add(noFollowLinks, gridBagConstraints);

        jButton1.setText("Log");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(jButton1, gridBagConstraints);

        showProgressTb.setSelected(true);
        showProgressTb.setText("Show Progress");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(showProgressTb, gridBagConstraints);

        closeWhenDoneTb.setSelected(true);
        closeWhenDoneTb.setText("Close When Done");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(closeWhenDoneTb, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doCmdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCmdBtnActionPerformed
        if ( doCmdBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_COPY ) )
            {
            System.out.println( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_COPY_CANCELED );
            this.stopSearch();
            //JOptionPane.showConfirmDialog( null, "at call stop search" );
            }
        else
            {
            try {
                List<CopyOption> copyOpts = new ArrayList<CopyOption>();
                EnumSet<FileVisitOption> fileVisitOptions = EnumSet.noneOf( FileVisitOption.class );
                if ( replaceExisting.isSelected() )
                    copyOpts.add( StandardCopyOption.REPLACE_EXISTING );
                if ( copyAttribs.isSelected() )
                    copyOpts.add( StandardCopyOption.COPY_ATTRIBUTES );
                if ( noFollowLinks.isSelected() )
                    {
                    copyOpts.add( LinkOption.NOFOLLOW_LINKS );
                    }
                else
                    {
                    fileVisitOptions = EnumSet.of( FOLLOW_LINKS );
                    }
//                CopyOption[] copyOptsAR = copyOpts.toArray( new CopyOption[ copyOpts.size() ] );
//
//                System.out.println( "copyOpts length =" + copyOptsAR.length + "=" );
//                for ( CopyOption cc : copyOptsAR )
//                    System.out.println( "cc =" + cc + "=" );
                
//                jfilecopy = new JFileCopy( this, isDoingCutFlag, startingPath, copyPaths, toPath, fileVisitOptions, copyOpts.toArray( new CopyOption[ copyOpts.size() ] ) );
//                CopyFrameSwingWorker copyFrameSwingWorker = new CopyFrameSwingWorker( this, jfilecopy, copyPaths, toPath, showProgressTb.isSelected(), closeWhenDoneTb.isSelected() );
                
//                 copyFrameSwingWorker.addPropertyChangeListener(
//     new PropertyChangeListener() {
//         public  void propertyChange(PropertyChangeEvent evt) {
//            System.out.println( "copy propertyChange() evt =" + evt + "=" );
//             if ("cpCount".equals(evt.getPropertyName())) {
////                 cpCount.setText((Integer)evt.getNewValue());
//             }
//         }
//     });
                 
//                copyFrameSwingWorker.execute();   //doInBackground();
            } 
            catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            } 
        }
        
    }//GEN-LAST:event_doCmdBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        //  i basically gutted this by taking out logger.getLogString(). if needed, put it back.
//        System.out.println( "log string =\n" + logger.getLogString() + "\n=" );

//        JEditorPane ep = new JEditorPane( "text/text", logger.getLogString() );
//        ep.setVisible(true);
//        ep.setSize( 900,800 );

    // show
//    JOptionPane.showMessageDialog( null, ep );
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void copyAttribsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyAttribsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_copyAttribsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        MyLogger logger = MyLogger.getLogger( CopyFrame.class.getName() );

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CopyFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton closeWhenDoneTb;
    private javax.swing.JCheckBox copyAttribs;
    private javax.swing.JButton doCmdBtn;
    private javax.swing.JLabel fromPath;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel message;
    private javax.swing.JCheckBox noFollowLinks;
    private javax.swing.JLabel pastePath;
    private javax.swing.JLabel processStatus;
    private javax.swing.JCheckBox replaceExisting;
    private javax.swing.JToggleButton showProgressTb;
    // End of variables declaration//GEN-END:variables
}
