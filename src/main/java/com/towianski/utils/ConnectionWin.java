/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */
public class ConnectionWin extends javax.swing.JDialog {

    private static final MyLogger logger = MyLogger.getLogger(ConnectionWin.class.getName() );

    JTextField parentConnectionType = null;
    JTextField parentHost = null;
    JTextField parentUsername = null;
    JTextField parentPassword = null;
    JTextField parentHttpsPort = null;
    JTextField parentSshPort = null;
    
    public ConnectionWin( JTextField parentConnectionType, JTextField parentHost, JTextField parentUsername, JTextField parentPassword, JTextField parentHttpsPort, JTextField parentSshPort ) {
        initComponents();
        this.parentConnectionType = parentConnectionType;
        this.parentHost = parentHost;
        this.parentUsername = parentUsername;
        this.parentPassword = parentPassword;
        this.parentHttpsPort = parentHttpsPort;
        this.parentSshPort = parentSshPort;

        connectionType.setSelectedItem( parentConnectionType.getText() );
        host.setText( parentHost.getText() );
        username.setText( parentUsername.getText() );
        password.setText( parentPassword.getText() );
        httpsPort.setText( parentHttpsPort.getText() );
        sshPort.setText( parentSshPort.getText() );

        this.setLocationRelativeTo( parentConnectionType );
        this.addEscapeListener( this );
        this.pack();
        this.setVisible(true);
    }

    public void addEscapeListener(final JDialog win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //logger.info( "previewImportWin formWindow dispose()" );
                win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        host = new javax.swing.JTextField();
        httpsPort = new javax.swing.JTextField();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        sshPort = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        password = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        connectionType = new javax.swing.JComboBox<>();

        setMinimumSize(new java.awt.Dimension(600, 300));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(600, 300));
        setSize(new java.awt.Dimension(600, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        host.setMinimumSize(new java.awt.Dimension(300, 25));
        host.setPreferredSize(new java.awt.Dimension(300, 25));
        host.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(host, gridBagConstraints);

        httpsPort.setMinimumSize(new java.awt.Dimension(70, 25));
        httpsPort.setPreferredSize(new java.awt.Dimension(70, 25));
        httpsPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                httpsPortActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(httpsPort, gridBagConstraints);

        okBtn.setText("Use");
        okBtn.setMinimumSize(new java.awt.Dimension(81, 25));
        okBtn.setPreferredSize(new java.awt.Dimension(81, 25));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 7, 10);
        getContentPane().add(okBtn, gridBagConstraints);

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 7, 10);
        getContentPane().add(cancelBtn, gridBagConstraints);

        jLabel1.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        username.setMinimumSize(new java.awt.Dimension(120, 25));
        username.setPreferredSize(new java.awt.Dimension(120, 25));
        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(username, gridBagConstraints);

        jLabel3.setText("Host");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel2.setText("Https Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        sshPort.setMinimumSize(new java.awt.Dimension(70, 25));
        sshPort.setPreferredSize(new java.awt.Dimension(70, 25));
        sshPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sshPortActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(sshPort, gridBagConstraints);

        jLabel4.setText("Sftp Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel4, gridBagConstraints);

        password.setMinimumSize(new java.awt.Dimension(120, 25));
        password.setPreferredSize(new java.awt.Dimension(120, 25));
        password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(password, gridBagConstraints);

        jLabel5.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel5, gridBagConstraints);

        jLabel6.setText("Connection Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel6, gridBagConstraints);

        connectionType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "https", "sftp + https" }));
        connectionType.setMinimumSize(new java.awt.Dimension(100, 25));
        connectionType.setPreferredSize(new java.awt.Dimension(120, 25));
        connectionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(connectionType, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        parentConnectionType.setText( connectionType.getSelectedItem().toString() );
        parentHost.setText( host.getText() );
        parentUsername.setText( username.getText() );
        parentPassword.setText( password.getText() );
        parentHttpsPort.setText( httpsPort.getText() );
        parentSshPort.setText( sshPort.getText() );
        this.dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING )); 
        this.dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void hostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hostActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hostActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        logger.info( "connectionWin CANCEL");
        this.setVisible( false );
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void httpsPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_httpsPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_httpsPortActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameActionPerformed

    private void sshPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sshPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sshPortActionPerformed

    private void passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordActionPerformed

    private void connectionTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionTypeActionPerformed
        if ( connectionType.getSelectedItem().toString().equalsIgnoreCase( "" ) )
            {
            host.setText( "" );
            host.setVisible( false );
            username.setText( "" );
            username.setVisible( false );
            password.setText( "" );
            password.setVisible( false );
            httpsPort.setText( "" );
            httpsPort.setVisible( false );
            sshPort.setText( "" );
            sshPort.setVisible( false );
            }
        else if ( connectionType.getSelectedItem().toString().equalsIgnoreCase( "https" ) )
            {
            host.setVisible( true );
            username.setVisible( true );
            password.setVisible( true );
            httpsPort.setVisible( true );
            sshPort.setText( "" );
            sshPort.setVisible( false );
            }
        else if ( connectionType.getSelectedItem().toString().equalsIgnoreCase( "sftp + https" ) )
            {
            host.setVisible( true );
            username.setVisible( true );
            password.setVisible( true );
            httpsPort.setVisible( true );
            sshPort.setVisible( true );
            }
        this.pack();
    }//GEN-LAST:event_connectionTypeActionPerformed

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
            logger.severeExc( ex );
        } catch (InstantiationException ex) {
            logger.severeExc( ex );
        } catch (IllegalAccessException ex) {
            logger.severeExc( ex );
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.severeExc( ex );
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                fileAssocWin = new FileAssocWin( "c:/whatever/you.com" );
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JComboBox<String> connectionType;
    private javax.swing.JTextField host;
    private javax.swing.JTextField httpsPort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton okBtn;
    private javax.swing.JTextField password;
    private javax.swing.JTextField sshPort;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
