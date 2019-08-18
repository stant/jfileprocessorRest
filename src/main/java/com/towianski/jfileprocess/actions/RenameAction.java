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
public class RenameAction extends AbstractAction
    {
    private static final MyLogger logger = MyLogger.getLogger( RenameAction.class.getName() );
    JFileFinderWin win = null;
    
    public RenameAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "Rename    F2");
        }

    public void actionPerformed(ActionEvent e)
        {
        //logger.info( "RenameActionPerformed( null ) do action");
        try
            {
            win.callRenameActionPerformed( null );
            } 
        catch (Exception ex)
            {
            logger.info( "RenameActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    }
