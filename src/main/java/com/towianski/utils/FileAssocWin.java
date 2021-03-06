/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.models.FileAssoc;
import com.towianski.models.FileAssocList;
import com.towianski.models.JfpConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */
public class FileAssocWin extends javax.swing.JDialog {

    private static final MyLogger logger = MyLogger.getLogger( FileAssocWin.class.getName() );
    static FileAssocWin fileAssocWin = null;
    static boolean okFlag = false;
    static boolean deleteFlag = false;
    static int winAction = 0;
    FileAssocList fileAssocList = new FileAssocList();
    ArrayList<FileAssoc> matchingFileAssocList = new ArrayList<FileAssoc>();
    int faIdx = 0;

    static String filename = "";
    static Path filepath = null;
    static FileAssoc fa = null;
    
    /**
     * Creates new form RenameFiles
     */
//    public FileAssocWin( String title, String filename, FileAssoc fa ) {
//        initComponents();
//        this.setTitle(title);
//        this.filename = filename;
//        this.filepath = Paths.get(filename);
//        this.fa = fa;
//        if ( fa != null )
//            {
//            setAssocType( fa.getAssocType() );
//            setMatchType( fa.getMatchType());
//            setMatchPattern( fa.getMatchPattern());
//            setExec( fa.getExec() );
//            setStop( fa.getStop() );
//            }
//        else
//            {
//            int at = filename.lastIndexOf( '.' );
//            if ( regexMatchType.isSelected() )
//                setMatchPattern( ".*" + filename.substring( at ) );
//            else 
//                setMatchPattern( "**" + filename.substring( at ) );
//            }
//        this.setLocationRelativeTo( null );
//        this.setVisible(true);
//    }

    public FileAssocWin( String filename, FileAssocList fileAssocList, int winAction ) {
        initComponents();
        this.filename = filename;
        this.filepath = Paths.get(filename);
        this.fileAssocList = fileAssocList;
        this.winAction = winAction;
        
        matchingFileAssocList = fileAssocList.getFileAssocList( filename );
        showFileAssoc( 0 );
        
        logger.info( "winAction =" + winAction );
        if ( winAction == JfpConstants.ASSOC_WINDOW_ACTION_SELECT )
            {
//            logger.info( "doing select" );
            okBtn.setText( "Select" );
            deleteBtn.setVisible( false );
            this.setTitle( "Choose one to Use" );
            }
        this.setLocationRelativeTo( null );
        this.addEscapeListener( this );
        this.setVisible(true);
    }

    
    public void showFileAssoc( int idx ) {
        faIdx = idx;
        if ( faIdx < 0 )  
            faIdx = 0;
        else if ( faIdx >= matchingFileAssocList.size() )
            {
            if ( winAction == JfpConstants.ASSOC_WINDOW_ACTION_SELECT )
                {
                faIdx = matchingFileAssocList.size() - 1;
                }
            else
                {
                faIdx = matchingFileAssocList.size();
                }
            }
        logger.info( "\nSHOW faIdx =" + faIdx );
        if ( matchingFileAssocList != null && faIdx < matchingFileAssocList.size() )
            {
            this.setTitle( "Edit" );
            fa = matchingFileAssocList.get( faIdx );
            logger.info( "\nEDIT faIdx =" + faIdx + "\nmatched and got fa.getAssocType =" + fa.getAssocType() + "=" );
            logger.info( "matched and got fa.getMatchType =" + fa.getMatchType() + "=" );
            logger.info( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
            logger.info( "matched and got fa.exec =" + fa.getExec() + "=" );
            setAssocType( fa.getAssocType() );
            setMatchType( fa.getMatchType());
            setMatchPattern( fa.getMatchPattern());
            setDesc( fa.getDesc() );
            setStartDir( fa.getStartDir() );
            setExec( fa.getExec() );
            setStop( fa.getStop() );
            }
        else
            {
            this.setTitle( "New" );
            faIdx = matchingFileAssocList.size();
            int at = filename.lastIndexOf( '.' );
            if ( at >= 0 )
                {
                if ( regexMatchType.isSelected() )
                    setMatchPattern( ".*[.]" + filename.substring( at + 1 ) );
                else 
                    setMatchPattern( "**" + filename.substring( at ) );
                }
            else
                {
                filenameRB.setSelected( true );
                if ( regexMatchType.isSelected() )
                    // this is a pain! if \ I SHould make it \\   not for now
                    setMatchPattern( ".*" + System.getProperty( "file.separator" ) + filepath.getFileName() );
                else 
                    setMatchPattern( "**" + System.getProperty( "file.separator" ) + filepath.getFileName() );
                }
            setExec( "" );
            setStop( "" );
            }
        if ( winAction == JfpConstants.ASSOC_WINDOW_ACTION_SELECT )
            {
            this.setTitle( "Choose one to Use" );
            }
        }

    public static boolean isOkFlag() {
        return okFlag;
    }

    public static boolean isDeleteFlag() {
        return deleteFlag;
    }

    public String getMatchType()
    {
        if ( globMatchType.isSelected() )
            return JfpConstants.MATCH_TYPE_GLOB;
        else if ( regexMatchType.isSelected() )
            return JfpConstants.MATCH_TYPE_REGEX;
        return "";
    }
    
    public void setMatchType( String ss )
    {
        if ( ss.toUpperCase().equals( JfpConstants.MATCH_TYPE_GLOB ) )
                globMatchType.setSelected( true );
        else if ( ss.toUpperCase().equals( JfpConstants.MATCH_TYPE_REGEX ) )
            regexMatchType.setSelected( true );
    }

    public String getMatchPattern()
    {
        return matchPattern.getText();
    }
    
    public void setMatchPattern( String ss )
    {
        matchPattern.setText(ss);
    }

    public String getStartDir() {
        return startDir.getText();
    }

    public void setStartDir(String ss) {
        startDir.setText( ss );
    }
    
    public String getExec()
    {
        return exec.getText();
    }

    public void setExec( String ss )
    {
        exec.setText( ss );
    }
    
    public String getDesc()
    {
        return desc.getText();
    }

    public void setDesc( String ss )
    {
        desc.setText( ss );
    }

    public String getStop() {
        return stop.getText();
    }

    public void setStop(String ss) {
        stop.setText( ss );
    }

    public String getAssocType()
    {
        if ( suffixRB.isSelected() )
            return JfpConstants.ASSOC_TYPE_SUFFIX;
        else if ( filenameRB.isSelected() )
            return JfpConstants.ASSOC_TYPE_FILENAME;
        else if ( oneFileRB.isSelected() )
            return JfpConstants.ASSOC_TYPE_EXACT_FILE;
        return "";
    }

    public void setAssocType( String ss )
    {
        if ( ss.toUpperCase().equals(JfpConstants.ASSOC_TYPE_SUFFIX ) )
                suffixRB.setSelected( true );
        else if ( ss.toUpperCase().equals( JfpConstants.ASSOC_TYPE_FILENAME ) )
            filenameRB.setSelected( true );
        else if ( ss.toUpperCase().equals(JfpConstants.ASSOC_TYPE_EXACT_FILE ) )
                oneFileRB.setSelected( true );
    }


    public void addEscapeListener(final JDialog win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //logger.info( "previewImportWin formWindow dispose()" );
                okFlag = false;
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
        globMatchType = new javax.swing.JRadioButton();
        regexMatchType = new javax.swing.JRadioButton();
        matchPattern = new javax.swing.JTextField();
        exec = new javax.swing.JTextField();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        suffixRB = new javax.swing.JRadioButton();
        filenameRB = new javax.swing.JRadioButton();
        oneFileRB = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        stop = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        deleteBtn = new javax.swing.JButton();
        leftFa = new javax.swing.JButton();
        rightFa = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        startDir = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        desc = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(900, 300));
        setModal(true);
        setSize(new java.awt.Dimension(200, 60));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(globMatchType);
        globMatchType.setSelected(true);
        globMatchType.setText("glob");
        globMatchType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globMatchTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        getContentPane().add(globMatchType, gridBagConstraints);

        buttonGroup1.add(regexMatchType);
        regexMatchType.setText("regex");
        regexMatchType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regexMatchTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 26, 0, 0);
        getContentPane().add(regexMatchType, gridBagConstraints);

        matchPattern.setMinimumSize(new java.awt.Dimension(150, 25));
        matchPattern.setPreferredSize(new java.awt.Dimension(150, 25));
        matchPattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchPatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 223;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(matchPattern, gridBagConstraints);

        exec.setMinimumSize(new java.awt.Dimension(150, 25));
        exec.setPreferredSize(new java.awt.Dimension(150, 25));
        exec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                execActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(exec, gridBagConstraints);

        okBtn.setText("Save");
        okBtn.setMinimumSize(new java.awt.Dimension(81, 25));
        okBtn.setPreferredSize(new java.awt.Dimension(81, 25));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 7, 10);
        getContentPane().add(okBtn, gridBagConstraints);

        cancelBtn.setText("Exit");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 7, 10);
        getContentPane().add(cancelBtn, gridBagConstraints);

        jLabel1.setText("Description");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        buttonGroup2.add(suffixRB);
        suffixRB.setSelected(true);
        suffixRB.setText("suffix");
        suffixRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suffixRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(suffixRB, gridBagConstraints);

        buttonGroup2.add(filenameRB);
        filenameRB.setText("filename");
        filenameRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filenameRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(filenameRB, gridBagConstraints);

        buttonGroup2.add(oneFileRB);
        oneFileRB.setText("1 file");
        oneFileRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneFileRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 0);
        getContentPane().add(oneFileRB, gridBagConstraints);

        jTextPane1.setEditable(false);
        jTextPane1.setText("get replaced in commands:\n%f - full file path     %F - filename     %p - parent path\n$JAVA   $JAVAW   $CLASSPATH   $JFP   $JFPHOMETMP");
        jTextPane1.setMinimumSize(new java.awt.Dimension(200, 75));
        jTextPane1.setPreferredSize(new java.awt.Dimension(200, 75));
        jScrollPane1.setViewportView(jTextPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 7, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        stop.setMinimumSize(new java.awt.Dimension(150, 25));
        stop.setPreferredSize(new java.awt.Dimension(150, 25));
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(stop, gridBagConstraints);

        jLabel3.setText("Stop Command");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        deleteBtn.setText("Delete");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 7, 10);
        getContentPane().add(deleteBtn, gridBagConstraints);

        leftFa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Actions-go-previous-icon-16.png"))); // NOI18N
        leftFa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftFaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(leftFa, gridBagConstraints);

        rightFa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Actions-go-next-icon-16.png"))); // NOI18N
        rightFa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightFaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        getContentPane().add(rightFa, gridBagConstraints);

        jButton1.setText("Browse Execute");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 10);
        getContentPane().add(jButton1, gridBagConstraints);

        jLabel2.setText("Execute Command");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        startDir.setMinimumSize(new java.awt.Dimension(150, 25));
        startDir.setPreferredSize(new java.awt.Dimension(150, 25));
        startDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(startDir, gridBagConstraints);

        jLabel4.setText("Start in Directory");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 22, 0, 0);
        getContentPane().add(jLabel4, gridBagConstraints);

        desc.setMinimumSize(new java.awt.Dimension(150, 25));
        desc.setPreferredSize(new java.awt.Dimension(150, 25));
        desc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 10);
        getContentPane().add(desc, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        okFlag = true;
        deleteFlag = false;
        logger.info( "file assoc win OK");
        fa = new FileAssoc( getAssocType(), getMatchType(), getMatchPattern(), getDesc(), getStartDir(), getExec(), getStop() );
        if ( isOkFlag() && winAction == JfpConstants.ASSOC_WINDOW_ACTION_EDIT )
            {
            logger.info( "matched and got fa.getAssocType =" + fa.getAssocType() + "=" );
            logger.info( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
            logger.info( "matched and got fa.getStartDir =" + fa.getStartDir()+ "=   fa.exec =" + fa.getExec() + "=" );
            fileAssocList.addFileAssoc( fa );
            if ( faIdx < matchingFileAssocList.size() )
                {
                matchingFileAssocList.set( faIdx, fa );  //update
                logger.info( "file assoc UPDATE");
                }
            else
                {
                matchingFileAssocList.add( fa );  //add
                logger.info( "file assoc ADD");
                this.setVisible( false );
                }
            Rest.saveObjectToFile( "FileAssocList.json", fileAssocList );
            showFileAssoc( faIdx );
            }
        else
            {
            this.setVisible( false );
            }
    }//GEN-LAST:event_okBtnActionPerformed

    private void matchPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchPatternActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_matchPatternActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        okFlag = false;
        deleteFlag = false;
        logger.info( "file assoc win CANCEL");
        this.setVisible( false );
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void execActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_execActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_execActionPerformed

    private void suffixRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suffixRBActionPerformed
        int at = filename.lastIndexOf( '.' );
        if ( at >= 0 )
            {
            if ( regexMatchType.isSelected() )
                setMatchPattern( ".*[.]" + filename.substring( at + 1 ) );
            else 
                setMatchPattern( "**" + filename.substring( at ) );
            }
        else
            {
            filenameRB.setSelected( true );
            if ( regexMatchType.isSelected() )
                // this is a pain! if \ I SHould make it \\   not for now
                setMatchPattern( ".*" + System.getProperty( "file.separator" ) + filepath.getFileName() );
            else 
                setMatchPattern( "**" + System.getProperty( "file.separator" ) + filepath.getFileName() );
            }        
    }//GEN-LAST:event_suffixRBActionPerformed

    private void filenameRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filenameRBActionPerformed
        if ( regexMatchType.isSelected() )
            // this is a pain! if \ I SHould make it \\   not for now
            setMatchPattern( ".*" + System.getProperty( "file.separator" ) + filepath.getFileName() );
        else 
            setMatchPattern( "**" + System.getProperty( "file.separator" ) + filepath.getFileName() );
    }//GEN-LAST:event_filenameRBActionPerformed

    private void oneFileRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneFileRBActionPerformed
        setMatchPattern( filename );
    }//GEN-LAST:event_oneFileRBActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stopActionPerformed

    private void regexMatchTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regexMatchTypeActionPerformed
        if ( suffixRB.isSelected() )
            suffixRBActionPerformed( null );
        else if ( filenameRB.isSelected() )
            filenameRBActionPerformed( null );
        else if ( oneFileRB.isSelected() )
            oneFileRBActionPerformed( null );
    }//GEN-LAST:event_regexMatchTypeActionPerformed

    private void globMatchTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globMatchTypeActionPerformed
        if ( suffixRB.isSelected() )
            suffixRBActionPerformed( null );
        else if ( filenameRB.isSelected() )
            filenameRBActionPerformed( null );
        else if ( oneFileRB.isSelected() )
            oneFileRBActionPerformed( null );
    }//GEN-LAST:event_globMatchTypeActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        int reply = JOptionPane.showConfirmDialog( null, "Are you sure to Delete ? ", "File Association", JOptionPane.YES_NO_OPTION);
        logger.info( "reply =" + reply + "=" );
        if ( reply == JOptionPane.YES_OPTION )
            {
            okFlag = false;
            deleteFlag = true;
            logger.info( "file assoc win Delete");
            fa = new FileAssoc( getAssocType(), getMatchType(), getMatchPattern(), getDesc(), getStartDir(), getExec(), getStop() );
            if ( faIdx < matchingFileAssocList.size() )
                {
                fileAssocList.deleteFileAssoc( fa );
                matchingFileAssocList.remove( faIdx );  // delete
                Rest.saveObjectToFile( "FileAssocList.json", fileAssocList );
                showFileAssoc( faIdx );
                }
            }
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void rightFaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightFaActionPerformed
        faIdx ++;
        showFileAssoc(faIdx);
    }//GEN-LAST:event_rightFaActionPerformed

    private void leftFaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftFaActionPerformed
        faIdx --;
        showFileAssoc(faIdx);
    }//GEN-LAST:event_leftFaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "Find an executable file" );
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // enable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(true);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        File selectedFile = chooser.getSelectedFile();
        setExec( selectedFile.getPath() + " %f" );
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void startDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startDirActionPerformed

    private void descActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descActionPerformed

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
    private javax.swing.JButton deleteBtn;
    private javax.swing.JTextField desc;
    private javax.swing.JTextField exec;
    private javax.swing.JRadioButton filenameRB;
    private javax.swing.JRadioButton globMatchType;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton leftFa;
    private javax.swing.JTextField matchPattern;
    private javax.swing.JButton okBtn;
    private javax.swing.JRadioButton oneFileRB;
    private javax.swing.JRadioButton regexMatchType;
    private javax.swing.JButton rightFa;
    private javax.swing.JTextField startDir;
    private javax.swing.JTextField stop;
    private javax.swing.JRadioButton suffixRB;
    // End of variables declaration//GEN-END:variables
}
