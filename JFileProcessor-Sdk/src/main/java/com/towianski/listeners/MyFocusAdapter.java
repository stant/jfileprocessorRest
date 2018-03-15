/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.listeners;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.renderers.TableCellListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JScrollPane;

/**
 *
 * @author stan
 */
public class MyFocusAdapter extends FocusAdapter 
{ 
    TableCellListener tableCellListener = null;
    JFileFinderWin jFileFinderWin = null;
    JScrollPane filesTblScrollPane = null;
    
    public MyFocusAdapter( TableCellListener filesTblCellListener, JFileFinderWin jFileFinderWin )
        {
        this.tableCellListener = filesTblCellListener;
        this.jFileFinderWin = jFileFinderWin;
        }

        @Override
        public void focusGained( FocusEvent e) {
            System.out.println( "TableCellListener().focusGained() for " + e.getSource() );
        }
         
        @Override
        public void focusLost( FocusEvent e) {
            System.out.println( "TableCellListener().focusLost() for " + e.getSource() );
            if ( e.getSource().toString().startsWith( "com.towianski.renderers.TableCellListener" ) )
                    {
                    if ( this.tableCellListener.getOnOffFlag() )
                            {
                            System.out.println( "TableCellListener().focusLost() call processEditingCanceled()" );
                            this.tableCellListener.processEditingCanceled();
                            }
                    }
        }
}
