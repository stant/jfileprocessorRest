/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.newunused;

import com.towianski.models.FilesTblModel;
import com.towianski.jfileprocessor.JFileFinderWin;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author stan
 */
public class filesTblScollPaneMouseAdapter extends MouseAdapter 
{ 
    JPopupMenu jPopupMenu = null;
    JFileFinderWin jFileFinderWin = null;
    JScrollPane filesTblScrollPane = null;
    
    public filesTblScollPaneMouseAdapter( JPopupMenu jPopupMenu, JFileFinderWin jFileFinderWin, JScrollPane filesTblScrollPane )
        {
        this.jPopupMenu = jPopupMenu;
        this.jFileFinderWin = jFileFinderWin;
        this.filesTblScrollPane = filesTblScrollPane;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
         
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
         
        @Override
        public void mouseClicked(MouseEvent e) {
//            if (e.isPopupTrigger()) {
//                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
//            }
            if ( e.getSource() == filesTblScrollPane )
                {
                //System.out.println( "mouseclicked on filesTblScrollPane" );
                return;
                }
            
            if (e.getClickCount() == 2) {
                JTable filesTbl = (JTable)e.getSource();
                //int rowIndex = filesTbl.getSelectedRow();
                //System.out.println( "rowIndex =" + rowIndex );
                int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
                //System.out.println( "converted rowIndex =" + rowIndex );
                String selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
//                Boolean isDir = (Boolean) filesTbl.getModel().getValueAt(rowIndex, FilesTblModel.FILESTBLMODEL_FOLDERTYPE );
                int folderType = (Integer) filesTbl.getModel().getValueAt(rowIndex, FilesTblModel.FILESTBLMODEL_FOLDERTYPE );
                //System.out.println( "selected row file =" + selectedPath );
//                System.out.println( "mouseClicked selected folderType =" + folderType );
                if ( folderType == FilesTblModel.FOLDERTYPE_FOLDER ) // skipping no access folder for now !
                    {
                    jFileFinderWin.setStartingFolder( selectedPath );
                    jFileFinderWin.callSearchBtnActionPerformed( null );
                    }
                else
                    {
                    jFileFinderWin.desktopEdit( new File( selectedPath ) );
                    }
               }            
        }
}
