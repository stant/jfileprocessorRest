/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.utils.DesktopUtils;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author stan
 */
public class SavedPathsPanel extends javax.swing.JPanel {

        HashMap<String,String> savedPathsHm = new HashMap<String,String>();
        JFileFinderWin jFileFinderWin = null;

    /**
     * Creates new form SavedPathsPanel
     */
    public SavedPathsPanel( JFileFinderWin jFileFinderWin ) {
        initComponents();
        this.jFileFinderWin = jFileFinderWin;
    }

    public void setJFileFinderWin( JFileFinderWin jFileFinderWin ) {
        this.jFileFinderWin = jFileFinderWin;
    }

    public HashMap<String, String> getSavedPathsHm() {
        return savedPathsHm;
    }

    public void setSavedPathsHm(HashMap<String, String> savedPathsHm) {
        this.savedPathsHm = savedPathsHm;
    }

    public JList<String> getSavedPathsList() {
        return savedPathsList;
    }

    public void setSavedPathsList(JList<String> savedPathsList) {
        this.savedPathsList = savedPathsList;
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

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        savedPathsList = new javax.swing.JList<>();
        jButton3 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jButton1.setText("+");
        jButton1.setMinimumSize(new java.awt.Dimension(0, 0));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jButton1, gridBagConstraints);

        jButton2.setText("-");
        jButton2.setMinimumSize(new java.awt.Dimension(0, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jButton2, gridBagConstraints);

        savedPathsList.setModel(new DefaultListModel() );
        savedPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                savedPathsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(savedPathsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        add(jScrollPane1, gridBagConstraints);

        jButton3.setText("Edit");
        jButton3.setMinimumSize(new java.awt.Dimension(0, 0));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jButton3, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DefaultListModel listModel = (DefaultListModel) savedPathsList.getModel();
        System.out.println( "add path() startingFolder.getText() =" + jFileFinderWin.getStartingFolder() + "=" );
        String ans = JOptionPane.showInputDialog( "Name: ", Paths.get( jFileFinderWin.getStartingFolder() ).getFileName() );
        if ( ans == null )
            {
            return;
            }
        if ( jFileFinderWin.getConnUserInfo().isConnectedFlag() )
            {
            savedPathsHm.put( ans, jFileFinderWin.getStartingFolder() + "," + jFileFinderWin.getRmtUser() + "," + jFileFinderWin.getRmtPasswd() + "," + jFileFinderWin.getRmtHost() + "," + jFileFinderWin.getRmtSshPort() );
            }
        else
            {
            savedPathsHm.put( ans, jFileFinderWin.getStartingFolder() );
            }
        listModel.addElement( ans );
        jFileFinderWin.saveBookmarks();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        DefaultListModel listModel = (DefaultListModel) savedPathsList.getModel();
        int index = savedPathsList.getSelectedIndex();
        String strPath = listModel.getElementAt(index).toString();
        System.out.println( "delete path() index =" + index + "   element =" + strPath + "=" );
        if ( strPath.equals( "New Window" ) || strPath.equals( "Trash" ) )
            {
            return;
            }
        savedPathsHm.remove( listModel.getElementAt(index).toString() );
        listModel.remove( index );
        jFileFinderWin.saveBookmarks();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void savedPathsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_savedPathsListMouseClicked
          if ( evt.getClickCount() < 2) 
            {
            return;
            }
        DefaultListModel listModel = (DefaultListModel) savedPathsList.getModel();
        int index = savedPathsList.getSelectedIndex();
        String strPath = listModel.getElementAt(index).toString();
        if ( strPath.equals( "New Window" ) )
            {
            try {
//                int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class );
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, true );
                System.out.println( "SavedPathsPanel.javaprocess.exec start new window rc = " + rc + "=" );
            } catch (IOException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
            }
        else if ( strPath.equals( "Trash" ) )
            {
            try {
//                int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class );
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, true, DesktopUtils.getTrashFolder().toString() );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
            } catch (IOException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
            }
        
        System.out.println( "savedPathsHm.get( strPath ) =" + savedPathsHm.get( strPath ) + "=" );
        String[] arr = savedPathsHm.get( strPath ).split( "," );
        for ( String ttt : arr )
            {
            System.out.println( "bookmarks piece =" + ttt + "=" );
            }
        if ( arr.length > 3 )
            {
            jFileFinderWin.setStartingFolder( arr[0] );
            jFileFinderWin.setRmtUser( arr[1] );
            jFileFinderWin.setRmtPasswd( arr[2] );
            jFileFinderWin.setRmtHost( arr[3] );
            jFileFinderWin.setRmtSshPort( arr[4] );
            
//            SwingUtilities.invokeLater(new Runnable() 
//                {
//                public void run() {
//                    jFileFinderWin.callRmtConnectBtnActionPerformed( null );
//                    jFileFinderWin.callSearchBtnActionPerformed( null );
//                    }
//                });
            
//            SwingUtilities.invokeLater(new Runnable() 
//                {
//                public void run() {
//                    jFileFinderWin.callSearchBtnActionPerformed( null );
//                    }
//                });
            jFileFinderWin.clickConnectAndSearch( null );
            }
        else
            {
            jFileFinderWin.setStartingFolder( savedPathsHm.get( strPath ) );
            jFileFinderWin.callSearchBtnActionPerformed( null );
            }

//    jFileFinderWin.callRmtConnectBtnActionPerformed( null );
//    jFileFinderWin.callSearchBtnActionPerformed( null );

    }//GEN-LAST:event_savedPathsListMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
            TextEditPanel textEditPanel = new TextEditPanel( jFileFinderWin, DesktopUtils.getBookmarks().toString() );
            textEditPanel.setState ( JFrame.ICONIFIED );

            textEditPanel.pack();
            textEditPanel.setVisible(true);
            textEditPanel.setState ( JFrame.NORMAL );

    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> savedPathsList;
    // End of variables declaration//GEN-END:variables
}
