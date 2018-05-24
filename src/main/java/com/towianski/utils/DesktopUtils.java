/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author stan
 */
public class DesktopUtils 
{
    
   public static File getTrashFolder()
   {
      return getJfpHome( "TrashFolder", "folder" );
   }
    
   public static File getBookmarks()
   {
      return getJfpHome( "Bookmarks.txt", "file" );
   }

   public static File getUserTmp()
   {
//      return getJfpHome( "tmp", "folder" );
        return new File( "/tmp/JFP" );
   }
    
   public static boolean isHeadlessProperty()
        {
        return System.getProperty( "java.awt.headless", "false" ).equalsIgnoreCase( "TRUE" ) ? true : false ;
        }
   
   public static File getJfpHome( String specificFolder, String fType )
   {
      System.out.println( "os.name =" + System.getProperty( "os.name" ) + "=" );
      File jfpHome = null;
      File jfpHome1 = null;
      File jfpHome2 = null;
      File jfpHome3 = null;
      File jfpHome4 = null;
      String missingHomeErrMsg = "";
              
      if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
        {
        jfpHome1 = new File( System.getProperty( "user.home" ) + "/Library/Application Support", "JFileProcessor" );
        System.out.println( "try jfpHome folder =" + jfpHome1 + "=" );
        if ( jfpHome1.exists() )
            {
            jfpHome = jfpHome1;
            }
        else
            {
            jfpHome2 = new File( System.getProperty( "user.home" ) + "/Library/Preferences", "JFileProcessor" );
            System.out.println( "try jfpHome folder =" + jfpHome2 + "=" );
            if ( jfpHome2.exists() )
                {
                jfpHome = jfpHome2;
                }
            else
                {
                jfpHome3 = new File( "/Library/Preferences", "JFileProcessor" );
                System.out.println( "try jfpHome folder =" + jfpHome3 + "=" );
                if ( jfpHome3.exists() )
                    {
                    jfpHome = jfpHome3;
                    }
                else
                    {
                    jfpHome4 = new File( System.getProperty( "user.home" ) + "/Library", "JFileProcessor" );
                    System.out.println( "try jfpHome folder =" + jfpHome4 + "=" );
                    if ( jfpHome4.exists() )
                        jfpHome = jfpHome4;
                    } // 3
                } // 2
            } // 1
        
        // I am assuming at this point that these Mac folders do exist.
        
        if ( jfpHome == null )
            {
            System.out.println( "Could not find so assuming jfpHome folder =" + jfpHome1 + "=" );
            jfpHome = jfpHome1;
            missingHomeErrMsg = "\n\nI looked in these 4 places in this order: \n\n"
                        + jfpHome1 + "\n"
                        + jfpHome2 + "\n"
                        + jfpHome3 + "\n"
                        + jfpHome4 + "\n";
            }
        }
      else  // windows + Linux : test for moneydance folder
        {
        jfpHome1 = new File( System.getProperty( "user.home" ), ".JFileProcessor" );
        System.out.println( "try jfpHome folder =" + jfpHome1 + "=" );
        if ( jfpHome1.exists() )
            jfpHome = jfpHome1;

        if ( jfpHome == null )
            {
            System.out.println( "Could not find so assuming jfpHome folder =" + jfpHome1 + "=" );
            jfpHome = jfpHome1;
            missingHomeErrMsg = "";   //\n\nI looked in this place: \n\n"
                                      //+ jfpHome + "\n";
            }
        }

      // for all os's
    if ( ! jfpHome.exists() )
        {
        boolean ok = jfpHome.mkdir();
        if ( ! isHeadlessProperty() )  JOptionPane.showMessageDialog( null, "Could not find a JFileProcessor folder so I created one here: \n\n" + jfpHome
                                        + missingHomeErrMsg
                                        );
        if ( ! ok )
            {
            if ( ! isHeadlessProperty() )  JOptionPane.showMessageDialog( null, "*** Error creating JFileProcessor folder: \n\n" + jfpHome );
            }
        }
    jfpHome = new File( jfpHome, specificFolder );
      
      // all systems - jfpHome now includes properties file path
      try {
        if ( ! jfpHome.exists() )
            {
            boolean ok = false;
            ok = fType.equals( "folder" ) ? jfpHome.mkdirs() : jfpHome.createNewFile();
            if ( ! isHeadlessProperty() )  JOptionPane.showMessageDialog( null, "Could not find its JFileProcessor " + specificFolder + " file so I created one here: \n\n" + jfpHome
                        );
            }
        }
      catch (Exception ex) 
        {
        Logger.getLogger( DesktopUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

      return jfpHome;
   }
    
    //public static Rectangle getScreenViewableBounds(Window window) {
    //    return getScreenViewableBounds((Component) window);
    //}
    //
    //public static Rectangle getScreenViewableBounds(Component comp) {
    //    return getScreenViewableBounds(getGraphicsDevice(comp));
    //}
    //
    //public static Rectangle getScreenViewableBounds(GraphicsDevice gd) {
    //    Rectangle bounds = new Rectangle(0, 0, 0, 0);
    //    if (gd == null) {
    //        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    //        gd = ge.getDefaultScreenDevice();
    //    }
    //
    //    if (gd != null) {
    //        GraphicsConfiguration gc = gd.getDefaultConfiguration();
    //        bounds = gc.getBounds();
    //
    //        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
    //        bounds.x += insets.left;
    //        bounds.y += insets.top;
    //        bounds.width -= (insets.left + insets.right);
    //        bounds.height -= (insets.top + insets.bottom);
    //    }
    //
    //    return bounds;
    //}   

    public static void moveJframeToBottomScreen( JFrame f )
        {
        if ( isHeadlessProperty() )  return;
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle bounds = defaultScreen.getDefaultConfiguration().getBounds();
        GraphicsConfiguration gc = defaultScreen.getDefaultConfiguration();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= (insets.left + insets.right);
        bounds.height -= (insets.top + insets.bottom);
        int x = (int) (bounds.getMaxX() / 2) - (f.getWidth() / 2);
        int y = (int) bounds.getMaxY() - f.getHeight();
        f.setLocation(x, y);
        f.validate();
        f.setVisible(true);
        }
}
