/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.jfileprocessor.JFileFinderWin;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Stan Towianski
 */
public class BackwardFolderAction extends AbstractAction
    {
    JFileFinderWin win = null;
    
    public BackwardFolderAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "BackwardFolder    Alt-Left Arrow");
//        System.out.println("constructor BackwardFolderAction()" );
        }

    public void actionPerformed(ActionEvent e)
        {
        //System.out.println("RenameActionPerformed( null ) do action");
        try
            {
//            System.out.println("callBackwardFolderActionPerformed( null ) " );
            win.callBackwardFolderActionPerformed( null );
            } 
        catch (Exception ex)
            {
            System.out.println("callBackwardFolderActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    }
