/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author stan
 */
public class ListOfFilesPanel extends javax.swing.JFrame {

        LinkedHashMap<String,String> savedPathsHm = new LinkedHashMap<String,String>();
        JFileFinderWin jFileFinderWin = null;
        String name = "";
        DefaultComboBoxModel listOfFilesPanelModel = null;
        String currentDirectory = "";
        String currentFile = "";
    /**
     * Creates new form SavedPathsPanel
     */
    public ListOfFilesPanel( JFileFinderWin jFileFinderWin, String name, DefaultComboBoxModel listOfFilesPanelModel ) {
        this.jFileFinderWin = jFileFinderWin;
        this.name = name;
        this.listOfFilesPanelModel = listOfFilesPanelModel;
        initComponents();
        
        if ( jFileFinderWin.getStartingFolder().equals( "" ) )
            {
            currentDirectory = ".";
            }
        else
            {
            currentDirectory = jFileFinderWin.getStartingFolder();
            }
        PathsList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        this.setLocationRelativeTo( getRootPane() );
        this.validate();
        this.addEscapeListener( this );
    }

    public void setJFileFinderWin( JFileFinderWin jFileFinderWin ) {
        this.jFileFinderWin = jFileFinderWin;
    }

    public LinkedHashMap<String, String> getSavedPathsHm() {
        return savedPathsHm;
    }

    public void setSavedPathsHm(LinkedHashMap<String, String> savedPathsHm) {
        this.savedPathsHm = savedPathsHm;
    }

    public DefaultComboBoxModel getModel() {
        return (DefaultComboBoxModel) PathsList.getModel();
    }

    public void setModel( DefaultComboBoxModel model ) {
        PathsList.setModel( model );
    }

    public void setSavedPathsList(JList<String> savedPathsList) {
        this.PathsList = savedPathsList;
    }

    public void setCount() {
        DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) PathsList.getModel();
        count.setText( thisListModel.getSize() + "" );
    }
    
    public void readFile( File selectedFile )
    {
        System.out.println( "File to read =" + selectedFile + "=" );
        currentDirectory = selectedFile.getParent();
        currentFile = selectedFile.getAbsolutePath();
        //Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        //String[] tt = { selectedFile.getPath() };
        //startingFolder.setText( selectedFile.getPath() );
        
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileReader fr = new FileReader( selectedFile.getAbsoluteFile() );
            BufferedReader br = new BufferedReader(fr);

            DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) PathsList.getModel();
            thisListModel.removeAllElements();
            int numItems = thisListModel.getSize();
            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
            
            String line = "";
            while ( ( line = br.readLine() ) != null )
                {
                System.out.println( "read line =" + line + "=" );
                thisListModel.addElement( line );
                }
            //close BufferedWriter
            br.close();
            //close FileWriter 
            fr.close();
            }
        catch( Exception ex )
            {

            }
        this.setTitle( "List (" + name + ") - " + currentFile );
        setCount();
    }
    
    public static void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println( "previewImportWin formWindow dispose()" );
                win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, KeyEvent.SHIFT_DOWN_MASK ),
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

        jPanel1 = new javax.swing.JPanel();
        cmdCb = new javax.swing.JComboBox<>();
        listOfLists = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        PathsList = new javax.swing.JList<>();
        saveToFile = new javax.swing.JButton();
        readFile = new javax.swing.JButton();
        count = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(700, 550));
        setPreferredSize(new java.awt.Dimension(700, 550));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cmdCb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add from List", "Subtract from List" }));
        cmdCb.setMinimumSize(new java.awt.Dimension(150, 25));
        cmdCb.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(cmdCb, gridBagConstraints);

        listOfLists.setModel( listOfFilesPanelModel );
        listOfLists.setMinimumSize(new java.awt.Dimension(150, 25));
        listOfLists.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(listOfLists, gridBagConstraints);

        jButton1.setText("Run");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(jButton1, gridBagConstraints);

        jButton2.setText("Delete");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(jButton2, gridBagConstraints);

        PathsList.setModel(new DefaultComboBoxModel() );
        jScrollPane1.setViewportView(PathsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        saveToFile.setText("Save to File");
        saveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(saveToFile, gridBagConstraints);

        readFile.setText("Open File");
        readFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(readFile, gridBagConstraints);

        count.setText("   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel1.add(count, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        getContentPane().add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) PathsList.getModel();
        System.out.println( "selected item =" + listOfLists.getSelectedItem() + "=" );
        List<String> selected = PathsList.getSelectedValuesList();
        for ( String str : selected )
            {
            thisListModel.removeElement( str );
            }
        setCount();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DefaultComboBoxModel otherListModel = (DefaultComboBoxModel) jFileFinderWin.getListPanelModel( (String) listOfLists.getSelectedItem() );
        DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) PathsList.getModel();
        System.out.println( "selected item =" + listOfLists.getSelectedItem() + "=" );
        if ( listOfLists.getSelectedItem().equals( this.getTitle() ) )
            {
            JOptionPane.showMessageDialog( null, "You cannot work on the same list!", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        int numItems = otherListModel.getSize();
        int numChanged = 0;
        System.out.println( "otherListModel.getSize() num of items =" + numItems + "=" );
        System.out.println( "cmdCb.getSelectedItem() =" + cmdCb.getSelectedItem() + "=" );
        if ( cmdCb.getSelectedItem().equals( "Subtract from List" ) )
            {
            String str = "";
            for( int i = 0; i < numItems; i++ )
                {
                str = otherListModel.getElementAt( i ).toString();
//                System.out.println( "check for other list index =" + i + "   str =" + str + "=" );
                int foundAt = thisListModel.getIndexOf( str );
                if ( foundAt >= 0 )
                    {
//                    System.out.println( "FOUND str =" + str + "=" );
                    thisListModel.removeElementAt( foundAt );
                    numChanged ++;
                    }
                }
            JOptionPane.showMessageDialog( this, "Number of files subtracted: " + numChanged );
            }
        else if ( cmdCb.getSelectedItem().equals( "Add from List" ) )
            {
            String str = "";
            for( int i = 0; i < numItems; i++ )
                {
                str = otherListModel.getElementAt( i ).toString();
//                System.out.println( "check for other list index =" + i + "   str =" + str + "=" );
                int foundAt = thisListModel.getIndexOf( str );
                if ( foundAt < 0 )
                    {
//                    System.out.println( "not FOUND str =" + str + "= so ADD" );
                    thisListModel.addElement( otherListModel.getElementAt( i ) );
                    numChanged ++;
                    }
                }
            JOptionPane.showMessageDialog( this, "Number of files added: " + numChanged );
            }
        setCount();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void saveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Save To" );
        chooser.setCurrentDirectory( new File( currentDirectory ) );
        chooser.setSelectedFile( new File( currentFile ) );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        File selectedFile = chooser.getSelectedFile();
        System.out.println( "File to save to =" + selectedFile + "=" );
        currentDirectory = selectedFile.getParent();
        currentFile = selectedFile.getAbsolutePath();
        System.out.println( "File to save to =" + selectedFile + "=" );
        System.out.println( "File to save to =" + selectedFile + "=" );
        //Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        //String[] tt = { selectedFile.getPath() };
        //startingFolder.setText( selectedFile.getPath() );
        
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) PathsList.getModel();
            int numItems = thisListModel.getSize();
            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
            
            //loop for jtable rows
            for( int i = 0; i < numItems; i++ )
                {
                bw.write( (String) thisListModel.getElementAt( i ) );
                bw.write( "\n" );
            }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
            JOptionPane.showMessageDialog(null, "Saved to File");        
            this.setTitle( "List (" + name + ") - " + currentFile );
            }
        catch( Exception ex )
            {

            }
        }

    }//GEN-LAST:event_saveToFileActionPerformed

    private void readFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Read" );
        chooser.setCurrentDirectory( new File( currentDirectory ) );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        readFile( chooser.getSelectedFile() );
        }
    }//GEN-LAST:event_readFileActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        jFileFinderWin.removeListPanel( this.name );
    }//GEN-LAST:event_formWindowClosing

        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                           String[] data = {"Fe", "Fi", "Fo", "Fum"};

           final DefaultComboBoxModel listOfFilesPanelModel = new DefaultComboBoxModel(data);
                ListOfFilesPanel dialog = new ListOfFilesPanel( null, "aaa", listOfFilesPanelModel );
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });

                dialog.setSize( 800, 600 );
                dialog.setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> PathsList;
    private javax.swing.JComboBox<String> cmdCb;
    private javax.swing.JLabel count;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> listOfLists;
    private javax.swing.JButton readFile;
    private javax.swing.JButton saveToFile;
    // End of variables declaration//GEN-END:variables
}
