/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.listeners;

import com.towianski.models.FilesTblModel;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.JfpConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author stan
 */
public class MyMouseAdapter extends MouseAdapter 
{ 
    JPopupMenu jPopupMenu = null;
    JFileFinderWin jFileFinderWin = null;
    JScrollPane filesTblScrollPane = null;
    
    public MyMouseAdapter( JPopupMenu jPopupMenu, JFileFinderWin jFileFinderWin, JScrollPane filesTblScrollPane )
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
                //logger.info( "mouseclicked on filesTblScrollPane" );
                return;
                }
            
            if (e.getClickCount() == 2) {
                JTable filesTbl = (JTable)e.getSource();
                //int rowIndex = filesTbl.getSelectedRow();
                //logger.info( "rowIndex =" + rowIndex );
                int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
                //logger.info( "converted rowIndex =" + rowIndex );
                String selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
//                Boolean isDir = (Boolean) filesTbl.getModel().getValueAt(rowIndex, FilesTblModel.FILESTBLMODEL_FOLDERTYPE );
                int folderType = (Integer) filesTbl.getModel().getValueAt(rowIndex, FilesTblModel.FILESTBLMODEL_FOLDERTYPE );
                //logger.info( "selected row file =" + selectedPath );
//                logger.info( "myMouseAdapter mouseClicked selected folderType =" + folderType );
                if ( folderType == FilesTblModel.FOLDERTYPE_FOLDER ) // skipping no access folder for now !
                    {
                    jFileFinderWin.setStartingFolder( selectedPath );
                    jFileFinderWin.callSearchBtnActionPerformed( null );
                    }
                else if ( folderType == FilesTblModel.FOLDERTYPE_FILE )
                    {
                    jFileFinderWin.desktopOpen( selectedPath, JfpConstants.ASSOC_CMD_TYPE_EXEC, rowIndex );
                    }
               }            
        }
}
