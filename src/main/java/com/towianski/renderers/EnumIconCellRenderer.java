/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import com.towianski.models.FilesTblModel;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author stan
 */
public class EnumIconCellRenderer extends DefaultTableCellRenderer {
 
private final Map<Integer, ImageIcon> icons = new HashMap<Integer, ImageIcon>();
 
public EnumIconCellRenderer() {
    try {
        // Initialize data used to render cells.
        ImageIcon fileLinkIcon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/file-link-icon-16.png")));
        ImageIcon normalIcon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/blank-icon-16.png")));
        ImageIcon questionIcon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/question-icon-16.png")));

        this.icons.put( FilesTblModel.FILETYPE_NORMAL, normalIcon );
        this.icons.put( FilesTblModel.FILETYPE_LINK, fileLinkIcon );
        this.icons.put( FilesTblModel.FILETYPE_OTHER, questionIcon );
                
        // Set properties that never change.
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        this.setSize( 16, 16 );
        this.setText(null);
        } 
    catch (IOException ex) 
        {
        Logger.getLogger(EnumIconCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
 
  @Override
  protected void setValue(Object value) {
    // Set properties that change on individual cells.
//    if ( value instanceof Boolean )
//        System.out.println( "EnumIconCellRenderer is boolean =" + (Boolean) value );
    if ( value instanceof Integer )
        {
//        System.out.println( "EnumIconCellRenderer is Integer =" + (Integer) value );
        this.setIcon( icons.get( (Integer) value ) );
        }
    }
}
