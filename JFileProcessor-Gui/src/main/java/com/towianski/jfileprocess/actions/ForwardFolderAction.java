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
public class ForwardFolderAction extends AbstractAction
    {
    JFileFinderWin win = null;
    
    public ForwardFolderAction( JFileFinderWin win )
        {
        this.win = win;
        setEnabled( true );
        putValue(Action.NAME, "ForwardFolder    Alt-Right Arrow");
//        System.out.println("constructor ForwardFolderAction()" );
        }

    public void actionPerformed(ActionEvent e)
        {
        //System.out.println("RenameActionPerformed( null ) do action");
        try
            {
//            System.out.println("callForwardFolderActionPerformed( null ) " );
            win.callForwardFolderActionPerformed( null );
            } 
        catch (Exception ex)
            {
            System.out.println("callForwardFolderActionPerformed( null ) " + ex);
            ex.printStackTrace();
            }
        }
    }
