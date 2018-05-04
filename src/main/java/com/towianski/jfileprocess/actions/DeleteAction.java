/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Stan Towianski
 */
public class DeleteAction extends AbstractAction
    {
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
        //System.out.println("DeleteActionPerformed( null ) do action");
        try {
            win.callDeleteActionPerformed( e );
            } 
        catch (Exception ex)
            {
            System.out.println("DeleteAction( null ) " + ex);
            ex.printStackTrace();
            }
        }

   }
