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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */
public class CodeProcessorPanel extends javax.swing.JFrame {

    LinkedHashMap<String,String> savedPathsHm = new LinkedHashMap<String,String>();
    JFileFinderWin jFileFinderWin = null;
    DefaultComboBoxModel listOfFilesPanelModel = null;
    String currentDirectory = "";
    String currentFile = "";
        
/// extra vars

    private final static MyLogger logger = MyLogger.getLogger( CodeProcessorPanel.class.getName() );

    Thread jfinderThread = null;
    JFileFinderSwingWorker jFileFinderSwingWorker = null;
    ResultsData resultsData = null;
    JRunGroovy jRunGroovy = null;
    Color saveColor = null;
    
    public static final String PROCESS_STATUS_STARTED = "Started . . .";
    public static final String PROCESS_STATUS_CANCELED = "canceled";
    public static final String PROCESS_STATUS_COMPLETED = "completed";
    public static final String PROCESS_STATUS_DO_CANCEL = "Cancel";
    public static final String PROCESS_STATUS_RUN = "Run";

    boolean cancelFlag = false;
    boolean cancelFillFlag = false;
    String startingPath = null;
    Boolean dataSyncLock = false;
        

    /**
     * 
     */
    public CodeProcessorPanel() {
    }
    
    public CodeProcessorPanel( JFileFinderWin jFileFinderWin, String selectedPath, String listOfFilesPanelName ) {
        this.jFileFinderWin = jFileFinderWin;
        this.listOfFilesPanelModel = jFileFinderWin.getListOfFilesPanelsModel();
        initComponents();
        
        if ( selectedPath != null )
            {
            readFile( new File( selectedPath ) );
            }
        else if ( jFileFinderWin.getStartingFolder().equals( "" ) )
            {
            currentDirectory = ".";
            }
        else
            {
            currentDirectory = jFileFinderWin.getStartingFolder();
            }
        if ( listOfFilesPanelName != null )
            {
            listOfLists.setSelectedItem( listOfFilesPanelName );
            }
        else
            {
            listOfLists.setSelectedItem( jFileFinderWin.LIST_OF_FILES_SELECTED );
            }
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

    public javax.swing.JEditorPane getCodePane() {
        return codePane;
    }

    public String getText() {
        return codePane.getText();
    }

    public void setText(String str) {
        this.codePane.setText(str);
    }
    
    public void setListPanelModel(String str, DefaultComboBoxModel defaultComboBoxModel ) {
        jFileFinderWin.setListPanelModel( (String) listOfLists.getSelectedItem(), defaultComboBoxModel );
    }

    public DefaultComboBoxModel getListPanelModel( String listName ) 
        {
        DefaultComboBoxModel defaultComboBoxModel = null;
        try {
            defaultComboBoxModel = (DefaultComboBoxModel) jFileFinderWin.getListPanelModel( listName );
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            }
        return defaultComboBoxModel;
        }

    public void stopSearch() {
        cancelFlag = true;
        jRunGroovy.cancelSearch();
    }

    public boolean getStopSearch() {
        return cancelFlag;
    }

    public void stopFill() {
        cancelFillFlag = true;
        jRunGroovy.cancelFill();
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

//    public void setNumFilesInTable()
//        {
//        NumberFormat numFormat = NumberFormat.getIntegerInstance();
//        numFilesInTable.setText( numFormat.format( filesTbl.getModel().getRowCount() ) );
//        }
    
    public void setProcessStatus( String text )
        {
        processStatus.setText(text);
        switch( text )
            {
            case PROCESS_STATUS_STARTED:  
                processStatus.setBackground( Color.GREEN );
                setDoCmdBtn(this.PROCESS_STATUS_DO_CANCEL, Color.RED );
                setMessage( "" );
                break;
            case PROCESS_STATUS_CANCELED:
                processStatus.setBackground( Color.YELLOW );
                setDoCmdBtn(this.PROCESS_STATUS_RUN, saveColor );
                break;
            case PROCESS_STATUS_COMPLETED:
                processStatus.setBackground( saveColor );
                setDoCmdBtn(this.PROCESS_STATUS_RUN, saveColor );
//                doCmdBtn.setEnabled(false);
                break;
            default:
                processStatus.setBackground( saveColor );
                setDoCmdBtn(this.PROCESS_STATUS_RUN, saveColor );
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

    public void readFile( File selectedFile )
        {
        System.out.println( "File to read =" + selectedFile + "=" );
        currentDirectory = selectedFile.getParent();
        currentFile = selectedFile.getAbsolutePath();
        
        try
            {
            if ( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileReader fr = new FileReader( selectedFile.getAbsoluteFile() );
            BufferedReader br = new BufferedReader(fr);

//            codePane.read( br, evt );
            codePane.read( br, null );
            //close BufferedWriter
            br.close();
            //close FileWriter 
            fr.close();
            this.setTitle( "code - " + currentFile );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        }
    
    public void saveToFile( File selectedFile )
        {
        System.out.println( "File to save to =" + selectedFile + "=" );
        currentDirectory = selectedFile.getParent();
        currentFile = selectedFile.getAbsolutePath();
        
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            codePane.write(bw);
//            int numItems = thisListModel.getSize();
//            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
//            
//            //loop for jtable rows
//            for( int i = 0; i < numItems; i++ )
//                {
//                bw.write( (String) thisListModel.getElementAt( i ) );
//                bw.write( "\n" );
//            }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
//            JOptionPane.showMessageDialog(null, "Saved to File");        
            this.setTitle( "code - " + currentFile );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        }
    
//    public void executeScript()
//    {
//        File currentDir = new File( currentDirectory );
////        this.pathRoots = new String[] { currentDir.getAbsolutePath() };
//        CallGroovy callGroovy = new CallGroovy( new String[] { currentDir.getAbsolutePath() } );
//        System.out.println( "before call: callGroovy.testGroovyScriptEngineVsGroovyShell();" );
//        Binding binding = new Binding();
//        System.out.println( "start codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + this.jFileFinderWin.getStartingFolder() + "=" );
//
//        DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) jFileFinderWin.getListPanelModel( (String) listOfLists.getSelectedItem() );
//        
//        binding.setProperty( "codeProcessorPanel", this );
//        binding.setProperty( "defaultComboBoxModel", defaultComboBoxModel );
//        File tmpFile = new File( currentFile );
//        callGroovy.testGroovyScriptEngineVsGroovyShell( tmpFile.getName(), binding );        
//    }
//    
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
        doCmdBtn = new javax.swing.JButton();
        saveToFile = new javax.swing.JButton();
        openFile = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        codePane = new javax.swing.JEditorPane();
        saveBtn = new javax.swing.JButton();
        processStatus = new javax.swing.JLabel();
        message = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(500, 500));
        setPreferredSize(new java.awt.Dimension(800, 700));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cmdCb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Run on List" }));
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

        doCmdBtn.setText("Run");
        doCmdBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCmdBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(doCmdBtn, gridBagConstraints);

        saveToFile.setText("Save As...");
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

        openFile.setText("Open File");
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(openFile, gridBagConstraints);

        jScrollPane2.setViewportView(codePane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane2, gridBagConstraints);

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
        jPanel1.add(saveBtn, gridBagConstraints);

        processStatus.setText("          ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(processStatus, gridBagConstraints);

        message.setText("          ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(message, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        getContentPane().add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void doCmdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCmdBtnActionPerformed
        if ( doCmdBtn.getText().equalsIgnoreCase(PROCESS_STATUS_DO_CANCEL ) )
            {
            System.out.println( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus(PROCESS_STATUS_CANCELED );
            this.stopSearch();
            //JOptionPane.showConfirmDialog( null, "at call stop search" );
            }
        else
            {
            try {
                DefaultComboBoxModel defaultComboBoxModel = null;
                if ( ((String) listOfLists.getSelectedItem()).equals( jFileFinderWin.LIST_OF_FILES_SELECTED ) )
                    {
                    defaultComboBoxModel = jFileFinderWin.getSelectedItemsAsComboBoxModel();
                    }
                else
                    {
                    defaultComboBoxModel = (DefaultComboBoxModel) jFileFinderWin.getListPanelModel( (String) listOfLists.getSelectedItem() );
                    }
                cancelFlag = false;
                jRunGroovy = new JRunGroovy( jFileFinderWin, this, startingPath, currentDirectory, currentFile, defaultComboBoxModel );
                GroovySwingWorker groovySwingWorker = new GroovySwingWorker( jFileFinderWin, this, jRunGroovy, currentDirectory, currentFile, defaultComboBoxModel );
                groovySwingWorker.execute();   //doInBackground();
                } 
            catch (Exception ex) 
                {
                logger.log(Level.SEVERE, null, ex);
                } 
            }

    }//GEN-LAST:event_doCmdBtnActionPerformed

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
        saveToFile( selectedFile );
        }

    }//GEN-LAST:event_saveToFileActionPerformed

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "Open File" );
        chooser.setCurrentDirectory( new File( jFileFinderWin.JfpHomeDir ) );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        readFile( chooser.getSelectedFile() );
        }
    }//GEN-LAST:event_openFileActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        saveToFile( new File( currentFile )  );
    }//GEN-LAST:event_saveBtnActionPerformed

        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                           String[] data = {"Fe", "Fi", "Fo", "Fum"};

           final DefaultComboBoxModel listOfFilesPanelModel = new DefaultComboBoxModel(data);
                CodeProcessorPanel dialog = new CodeProcessorPanel( null, null, null );
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
    private javax.swing.JComboBox<String> cmdCb;
    private javax.swing.JEditorPane codePane;
    private javax.swing.JButton doCmdBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<String> listOfLists;
    private javax.swing.JLabel message;
    private javax.swing.JButton openFile;
    private javax.swing.JLabel processStatus;
    private javax.swing.JButton saveBtn;
    private javax.swing.JButton saveToFile;
    // End of variables declaration//GEN-END:variables
}
