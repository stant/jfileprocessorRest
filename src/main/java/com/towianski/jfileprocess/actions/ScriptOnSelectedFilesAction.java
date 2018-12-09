/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.ScriptSwingWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 *
 * @author stan
 */
public class ScriptOnSelectedFilesAction extends AbstractAction {

    JFileFinderWin jFileFinderWin = null;
    String filePath = null;
    
    public ScriptOnSelectedFilesAction( JFileFinderWin jFileFinderWin, String filePath )
        {
        this.jFileFinderWin = jFileFinderWin;
        this.filePath = filePath;
        }

    @Override
    public void actionPerformed(ActionEvent e)
        {
        //System.out.println("RenameActionPerformed( null ) do action");
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
            System.out.println("ScriptMenuItemListener.ActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    
}
