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
public class DeleteAction extends AbstractAction
    {
    private static final MyLogger logger = MyLogger.getLogger( DeleteAction.class.getName() );
    JFileFinderWin win = null;
    boolean isShiftMask = false;
    
   public DeleteAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "Delete    Del");
        }

    public void actionPerformed(ActionEvent e)
        {
        //logger.info( "DeleteActionPerformed( null ) do action");
        try {
            win.callDeleteActionPerformed( e );
            } 
        catch (Exception ex)
            {
            logger.info( "DeleteAction( null ) " + ex);
            ex.printStackTrace();
            }
        }

   }
