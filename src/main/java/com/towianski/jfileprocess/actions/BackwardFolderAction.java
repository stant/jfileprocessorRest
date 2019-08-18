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
public class BackwardFolderAction extends AbstractAction
    {
    private static final MyLogger logger = MyLogger.getLogger( BackwardFolderAction.class.getName() );
    JFileFinderWin win = null;
    
    public BackwardFolderAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "BackwardFolder    Alt-Left Arrow");
//        logger.info( "constructor BackwardFolderAction()" );
        }

    public void actionPerformed(ActionEvent e)
        {
        //logger.info( "RenameActionPerformed( null ) do action");
        try
            {
//            logger.info( "callBackwardFolderActionPerformed( null ) " );
            win.callBackwardFolderActionPerformed( null );
            } 
        catch (Exception ex)
            {
            logger.info( "callBackwardFolderActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    }
