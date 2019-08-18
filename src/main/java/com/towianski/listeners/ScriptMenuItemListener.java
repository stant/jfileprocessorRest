/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.listeners;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.ScriptSwingWorker;
import com.towianski.utils.MyLogger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

/**
 *
 * @author stan
 */
public class ScriptMenuItemListener implements ActionListener {

    private static final MyLogger logger = MyLogger.getLogger( ScriptMenuItemListener.class.getName() );
    JFileFinderWin jFileFinderWin = null;
    String filePath = null;
    
    public ScriptMenuItemListener( JFileFinderWin jFileFinderWin, String filePath )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.filePath = filePath;
        }

    @Override
    public void actionPerformed(ActionEvent e)
        {
        //logger.info( "RenameActionPerformed( null ) do action");
        try
            {
//            jFileFinderWin.callEnterActionPerformed( null );
            ScriptSwingWorker scriptSwingWorker = new ScriptSwingWorker( jFileFinderWin, filePath );
            scriptSwingWorker.setState ( JFrame.ICONIFIED );

            scriptSwingWorker.pack();
            scriptSwingWorker.setVisible(true);
            scriptSwingWorker.setState ( JFrame.NORMAL );
            
            scriptSwingWorker.run( null );
            } 
        catch (Exception ex)
            {
            logger.info( "ScriptMenuItemListener.ActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    
}
