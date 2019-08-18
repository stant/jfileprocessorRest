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
public class ForwardFolderAction extends AbstractAction
    {
    private static final MyLogger logger = MyLogger.getLogger( ForwardFolderAction.class.getName() );
    JFileFinderWin win = null;
    
    public ForwardFolderAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "ForwardFolder    Alt-Right Arrow");
//        logger.info( "constructor ForwardFolderAction()" );
        }

    public void actionPerformed(ActionEvent e)
        {
        //logger.info( "RenameActionPerformed( null ) do action");
        try
            {
//            logger.info( "callForwardFolderActionPerformed( null ) " );
            win.callForwardFolderActionPerformed( null );
            } 
        catch (Exception ex)
            {
            logger.info( "callForwardFolderActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    }
