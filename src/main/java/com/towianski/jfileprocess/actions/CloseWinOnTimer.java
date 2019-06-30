/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.utils.DesktopUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author stowians
 */
public class CloseWinOnTimer extends Timer 
{
    
    public CloseWinOnTimer( final JFrame jframe, final int delay )
    {
         this( jframe, "moveToBottom", delay );
    }
    
    public CloseWinOnTimer( final JFrame jframe, final String doMove, final int delay )
    {
    super( delay, new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                  System.out.println( "entered CloseWinOnTimer " + delay + " actionPerformed()" );
      //            Window win = SwingUtilities.getWindowAncestor(deleteFrame);
      //            win.dispose();
                    if ( delay > 0 )
                    {
                    jframe.dispatchEvent( new WindowEvent( jframe, WindowEvent.WINDOW_CLOSING )); 
                    jframe.dispose();
                    }
               }
            } );
//        jframe.setState ( JFrame.ICONIFIED );

        if ( doMove.equalsIgnoreCase( "moveToBottom" ) )
            DesktopUtils.moveJframeToBottomScreen(jframe);
//    {{setRepeats(false);}}.start();    
    }
}
