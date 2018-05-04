/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import com.towianski.models.Constants;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Stan Towianski
 */
public class PathRenderer extends DefaultTableCellRenderer
{
    int filesysType = -9;
    String delim = "\\";   // default to DOS
    int at = 0;
    
    public PathRenderer( int filesysType )
    {
    this.filesysType = filesysType;
    if ( filesysType == Constants.FILESYSTEM_POSIX )    
        {
        delim = "/";
        }
    System.out.println( "filesysType =" + filesysType + "   delim =\"" + delim + "\"" );
    }
  
  @Override public void setValue(Object aValue) 
    {
    Object result = aValue;
    if ((aValue != null) && (aValue instanceof String)) 
        {
        String filename = (String) aValue;
//        Path fpath = Paths.get( filename );
//        result = fpath.getFileName();
        at = filename.lastIndexOf(delim);
        result = at < 0 ? filename : filename.substring( at + 1 );
        setTooltip( filename );
        }
    super.setValue(result);
  }

  private void setTooltip(String filename) {
    setToolTipText( filename );
  }   
}    

