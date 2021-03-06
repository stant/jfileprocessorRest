/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.utils.MyLogger;

/**
 *
 * @author stan
 */
public class MsgBoxFrame extends javax.swing.JFrame {

    private static final MyLogger logger = MyLogger.getLogger( MsgBoxFrame.class.getName() );
    /**
     * Creates new form NewJFrame1
     */
    public MsgBoxFrame() {
        initComponents();
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setLocationRelativeTo( null );
    }

    public MsgBoxFrame( String ss ) {
        this();
        setMessage(ss);
    }

    public void setMessage( String text ) {
        message.setText( text );
    }

    public static void MsgBoxFrameEDT( String text )
    {
        {
            final String tmp = text;
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MsgBoxFrame( tmp ).setVisible( true );
                }
            });
        }        
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

        jScrollPane1 = new javax.swing.JScrollPane();
        message = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 70));
        setPreferredSize(new java.awt.Dimension(640, 140));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(350, 60));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 100));

        message.setEditable(false);
        message.setText("what the world");
        message.setMinimumSize(new java.awt.Dimension(250, 50));
        message.setPreferredSize(new java.awt.Dimension(575, 75));
        jScrollPane1.setViewportView(message);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
//            Thread.sleep( 4000 );
//            this.dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ));
//            this.dispose();

            new CloseWinOnTimer( this, "noMove", 3000 ){{setRepeats(false);}}.start();
        } catch (Exception ex) {
            logger.severeExc( ex );
        }

    }//GEN-LAST:event_formWindowOpened

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


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MsgBoxFrame( "Test Message" ).setVisible( true );
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JEditorPane message;
    // End of variables declaration//GEN-END:variables
}
