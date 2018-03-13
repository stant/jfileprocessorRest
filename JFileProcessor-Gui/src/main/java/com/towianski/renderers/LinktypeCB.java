/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

/**
 *
 * @author Stan Towianski
 */
public class LinktypeCB extends JCheckBox {

    private ImageIcon fileicon;
    private ImageIcon foldericon;

    public LinktypeCB() {
        try {
            foldericon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/file-link-icon-16.png")));
            fileicon = new ImageIcon(ImageIO.read(getClass().getResource("/icons/yellow/blank-icon-16.png")));
            this.setSize( 16, 16 );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setIcon(foldericon);
        } else {
            setIcon(fileicon);
        }
    }
 
}
