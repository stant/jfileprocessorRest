package com.towianski.boot;

import com.towianski.jfileprocessor.*;
import com.towianski.utils.MyLogger;

public class StartApp
{
    private final static MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );
/**
     * @param args the command line arguments
     */
    public static void main(String args[]) 
    {


        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );

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
        
        for ( int i = 0; i < args.length; i++ )
            {
//            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            System.out.println( "** args [" + i + "] =" + args[i] + "=" );
            }

        final JFileFinderWin jffw = new JFileFinderWin();
        if ( args.length > 0 )
            {
//   FIXXX          jffw.startingFolder.setText( args[0] );
//            jffw.searchBtnActionPerformed( null );
            }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new JFileFinderWin().setVisible(true);
                jffw.setVisible(true);
            }
        });
        
    }
}


