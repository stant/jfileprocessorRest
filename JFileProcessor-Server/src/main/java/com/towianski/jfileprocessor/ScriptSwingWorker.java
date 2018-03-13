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
import java.io.File;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */
public class ScriptSwingWorker extends CodeProcessorPanel {

    LinkedHashMap<String,String> savedPathsHm = new LinkedHashMap<String,String>();
    JFileFinderWin jFileFinderWin = null;
    DefaultComboBoxModel listOfFilesPanelModel = null;
    String currentDirectory = "";
    String currentFile = "";
        
/// extra vars

    private final static MyLogger logger = MyLogger.getLogger( ScriptSwingWorker.class.getName() );
        
    Thread jfinderThread = null;
    ResultsData resultsData = null;
    JRunGroovy jRunGroovy = null;
    Color saveColor = null;

    public static final String PROCESS_STATUS_COPY_STARTED = "Started . . .";
    public static final String PROCESS_STATUS_COPY_CANCELED = "canceled";
    public static final String PROCESS_STATUS_COPY_COMPLETED = "completed";
    public static final String PROCESS_STATUS_CANCEL_COPY = "Cancel";
    public static final String PROCESS_STATUS_COPY_READY = "Run";

    boolean cancelFlag = false;
    boolean cancelFillFlag = false;
    String startingPath = null;
    Boolean dataSyncLock = false;
        

    /**
     * Creates new form SavedPathsPanel
     */
    public ScriptSwingWorker( JFileFinderWin jFileFinderWin, String selectedPath ) {
        this.jFileFinderWin = jFileFinderWin;
        this.listOfFilesPanelModel = jFileFinderWin.getListOfFilesPanelsModel();
        initComponents();
        
        File selectedFile = new File( selectedPath );
        if ( selectedPath != null )
            {
            currentDirectory = selectedFile.getParent();
            currentFile = selectedFile.getAbsolutePath();
            }
        else if ( jFileFinderWin.getStartingFolder().equals( "" ) )
            {
            currentDirectory = ".";
            }
        else
            {
            currentDirectory = jFileFinderWin.getStartingFolder();
            }
        this.setLocation( Integer.valueOf( (int) jFileFinderWin.getLocation().getX() ), (Integer.valueOf( (int) jFileFinderWin.getLocation().getY() ) - 80 ) );
        this.validate();
        this.addEscapeListener( this );
        this.setTitle( selectedFile.getName() );
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
        System.out.println( "coming thru scriptspringwork setProcStatus()" );
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
//                doCmdBtn.setEnabled(false);
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

    public void run(java.awt.event.ActionEvent evt) 
        {                                         
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
                DefaultComboBoxModel defaultComboBoxModel = jFileFinderWin.getSelectedItemsAsComboBoxModel();
                jRunGroovy = new JRunGroovy( jFileFinderWin, this, startingPath, currentDirectory, currentFile, defaultComboBoxModel );
                GroovySwingWorker groovySwingWorker = new GroovySwingWorker( jFileFinderWin, this, jRunGroovy, currentDirectory, currentFile, defaultComboBoxModel );
                groovySwingWorker.execute();   //doInBackground();
                } 
            catch (Exception ex) 
                {
                logger.log(Level.SEVERE, null, ex);
                } 
            }
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
        doCmdBtn = new javax.swing.JButton();
        processStatus = new javax.swing.JLabel();
        message = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(150, 40));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 50));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        doCmdBtn.setText("Run");
        doCmdBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCmdBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(doCmdBtn, gridBagConstraints);

        processStatus.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(processStatus, gridBagConstraints);

        message.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(message, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        getContentPane().add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void doCmdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCmdBtnActionPerformed
        run( evt );
    }//GEN-LAST:event_doCmdBtnActionPerformed

        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                           String[] data = {"Fe", "Fi", "Fo", "Fum"};

           final DefaultComboBoxModel listOfFilesPanelModel = new DefaultComboBoxModel(data);
                ScriptSwingWorker dialog = new ScriptSwingWorker( null, null );
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
    private javax.swing.JButton doCmdBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel message;
    private javax.swing.JLabel processStatus;
    // End of variables declaration//GEN-END:variables
}
