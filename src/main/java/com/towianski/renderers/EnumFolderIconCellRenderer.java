/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import com.towianski.models.FilesTblModel;
import com.towianski.utils.MyLogger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author stan
 */
public class EnumFolderIconCellRenderer extends DefaultTableCellRenderer {
 
private static final MyLogger logger = MyLogger.getLogger( EnumFolderIconCellRenderer.class.getName() );
private final Map<Integer, ImageIcon> icons = new HashMap<Integer, ImageIcon>();
 
public EnumFolderIconCellRenderer() {
    try {
        // Initialize data used to render cells.
        ImageIcon fileicon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/document-icon-16.png")));
        ImageIcon filenotfoundicon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/document-not-found-icon-16.png")));
        ImageIcon foldericon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/Folder-Blank-icon-16.png")));
        ImageIcon noaccessFoldericon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/Folder-No-Access-icon-16.png")));
        ImageIcon noaccessFileicon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/File-No-Access-icon-16.png")));

        this.icons.put( FilesTblModel.FOLDERTYPE_FILE, fileicon );
        this.icons.put( FilesTblModel.FOLDERTYPE_FILE_NOT_FOUND, filenotfoundicon );
        this.icons.put( FilesTblModel.FOLDERTYPE_FOLDER, foldericon );
        this.icons.put( FilesTblModel.FOLDERTYPE_FOLDER_NOACCESS, noaccessFoldericon );
        this.icons.put( FilesTblModel.FOLDERTYPE_FILE_NOACCESS, noaccessFileicon );
                
        // Set properties that never change.
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        this.setSize( 16, 16 );
        this.setText(null);
        } 
    catch (IOException ex) 
        {
        logger.severeExc( ex );
        }
  }
 
  @Override
  protected void setValue(Object value) {
    // Set properties that change on individual cells.
//    if ( value instanceof Boolean )
//        logger.info( "EnumFolderIconCellRenderer is boolean =" + (Boolean) value );
    if ( value instanceof Integer )
        {
        //logger.info( "EnumFolderIconCellRenderer is Integer =" + (Integer) value );
        this.setIcon( icons.get( (Integer) value ) );
        }
    }
}
