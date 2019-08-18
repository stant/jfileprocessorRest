/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.utils.MyLogger;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Stan Towianski
 */
public class UpFolderAction extends AbstractAction
    {
    private static final MyLogger logger = MyLogger.getLogger( UpFolderAction.class.getName() );
    JFileFinderWin win = null;
    
    public UpFolderAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "UpFolder    Backspace");
        }

    public void actionPerformed(ActionEvent e)
        {
        //logger.info( "RenameActionPerformed( null ) do action");
        try
            {
            win.callUpFolderActionPerformed( null );
            } 
        catch (Exception ex)
            {
            logger.info( "callUpFolderActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    }
