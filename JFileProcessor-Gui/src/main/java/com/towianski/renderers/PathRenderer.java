/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Stan Towianski
 */
public class PathRenderer extends DefaultTableCellRenderer
{
  
  @Override public void setValue(Object aValue) {
    Object result = aValue;
    if ((aValue != null) && (aValue instanceof String)) {
      String filename = (String) aValue;
      Path fpath = Paths.get( filename );
      result = fpath.getFileName();
      setTooltip( filename );
    } 
    super.setValue(result);
  }

  private void setTooltip(String filename) {
    setToolTipText( filename );
  }   
}    

