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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author stan
 */
public class DesktopUtils 
{
    private static final MyLogger logger = MyLogger.getLogger( DesktopUtils.class.getName() );

   public static File getBookmarks()
   {
      return getJfpConfigHome( "Bookmarks.txt", "file", true );
   }
    
   public static File getTrashFolder()
   {
      return getJfpConfigHome( "TrashFolder", "folder", true );
   }

   public static String getJfpHomeTmpDir( boolean addEndingSlash )
   {
    
    String jfpTmp = getJfpHome( "temp", "folder" );;
    return addEndingSlash ? jfpTmp + System.getProperty("file.separator") : jfpTmp;
   }

   public static String getJfpHomeTmpFile( String filename )
   {
      return getJfpHomeTmpDir( true ) + filename;
   }
        
   public static String getJfpHome( String specificFolder, String fType )
   {
       try {
            File jfpHome = new File( getJfpHome( false ), specificFolder );
            logger.info( "jfpHome from specificFolder(" + specificFolder + ") =" + jfpHome.toString() + "=" );
      
        if ( ! jfpHome.exists() )
            {
            boolean ok = false;
            ok = fType.equals( "folder" ) ? jfpHome.mkdirs() : jfpHome.createNewFile();
            if ( ! isHeadlessProperty() )  JOptionPane.showMessageDialog( null, "Could not find its JFileProcessor " + specificFolder + " file so I created one here: \n\n" + jfpHome
                        );
            }
        return jfpHome.toString();
       } catch (IOException ex) {
           logger.severeExc( ex );
       }
    return null;
   }

        
   public static File getJfpHome( boolean addEndingSlash )
    {
       try {
//            logger.info( "toURI() =" + JFileFinderWin.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "=" );
//           File jfpHome = new File( JFileFinderWin.class.getProtectionDomain().getCodeSource().getLocation().toURI() ).getParentFile();
//            URL url = getLocation( JFileFinderWin.class );
//            logger.info( "URL =" + url.toString() + "=" );
//            String urlStr = (new File( url.toString() )).getParent();
//            File jfpHome = urlToFile( new URL( urlStr ) );
            String JfpHomeDir = addEndingSlash ? System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) : System.getProperty( "user.dir" );
            logger.info( "jfpHome =" + JfpHomeDir + "=" );
            return new File( JfpHomeDir );
       } catch (Exception ex) {
           logger.severeExc( ex );
       }
    return null;
   }

   public static boolean isHeadlessProperty()
        {
        return System.getProperty( "java.awt.headless", "false" ).equalsIgnoreCase( "TRUE" ) ? true : false ;
        }
   
   public static File getJfpConfigHome( String specificFolder, String fType, boolean doNotifyIfCreate )
   {
      logger.info( "os.name =" + System.getProperty( "os.name" ) + "=" );
      File jfpHome = null;
      File jfpHome1 = null;
      File jfpHome2 = null;
      File jfpHome3 = null;
      File jfpHome4 = null;
      String missingHomeErrMsg = "";
              
      if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
        {
        jfpHome1 = new File( System.getProperty( "user.home" ) + "/Library/Application Support", "JFileProcessor" );
        logger.info( "try jfpHome folder =" + jfpHome1 + "=" );
        if ( jfpHome1.exists() )
            {
            jfpHome = jfpHome1;
            }
        else
            {
            jfpHome2 = new File( System.getProperty( "user.home" ) + "/Library/Preferences", "JFileProcessor" );
            logger.info( "try jfpHome folder =" + jfpHome2 + "=" );
            if ( jfpHome2.exists() )
                {
                jfpHome = jfpHome2;
                }
            else
                {
                jfpHome3 = new File( "/Library/Preferences", "JFileProcessor" );
                logger.info( "try jfpHome folder =" + jfpHome3 + "=" );
                if ( jfpHome3.exists() )
                    {
                    jfpHome = jfpHome3;
                    }
                else
                    {
                    jfpHome4 = new File( System.getProperty( "user.home" ) + "/Library", "JFileProcessor" );
                    logger.info( "try jfpHome folder =" + jfpHome4 + "=" );
                    if ( jfpHome4.exists() )
                        jfpHome = jfpHome4;
                    } // 3
                } // 2
            } // 1
        
        // I am assuming at this point that these Mac folders do exist.
        
        if ( jfpHome == null )
            {
            logger.info( "Could not find so assuming jfpHome folder =" + jfpHome1 + "=" );
            jfpHome = jfpHome1;
            missingHomeErrMsg = "\n\nI looked in these 4 places in this order: \n\n"
                        + jfpHome1 + "\n"
                        + jfpHome2 + "\n"
                        + jfpHome3 + "\n"
                        + jfpHome4 + "\n";
            }
        }
      else  // windows + Linux : test for .JFileProcessor folder
        {
        jfpHome1 = new File( System.getProperty( "user.home" ), ".JFileProcessor" );
        logger.info( "try jfpHome folder =" + jfpHome1 + "=" );
        if ( jfpHome1.exists() )
            jfpHome = jfpHome1;

        if ( jfpHome == null )
            {
            logger.info( "Could not find so assuming jfpHome folder =" + jfpHome1 + "=" );
            jfpHome = jfpHome1;
            missingHomeErrMsg = "";   //\n\nI looked in this place: \n\n"
                                      //+ jfpHome + "\n";
            }
        }

      // for all os's
    if ( ! jfpHome.exists() )
        {
        boolean ok = jfpHome.mkdir();
        if ( ! isHeadlessProperty() && doNotifyIfCreate )  
            JOptionPane.showMessageDialog( null, "Could not find a JFileProcessor folder so I created one here: \n\n" + jfpHome
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
        if ( ! jfpHome.exists() && doNotifyIfCreate )
            {
            boolean ok = false;
            ok = fType.equals( "folder" ) ? jfpHome.mkdirs() : jfpHome.createNewFile();
            if ( ! isHeadlessProperty() )  JOptionPane.showMessageDialog( null, "Could not find its JFileProcessor " + specificFolder + " file so I created one here: \n\n" + jfpHome
                        );
            }
        }
      catch (Exception ex) 
        {
        logger.severeExc( ex );
        }

      return jfpHome;
   }
    
    /**
     * Gets the base location of the given class.
     * <p>
     * If the class is directly on the file system (e.g.,
     * "/path/to/my/package/MyClass.class") then it will return the base directory
     * (e.g., "file:/path/to").
     * </p>
     * <p>
     * If the class is within a JAR file (e.g.,
     * "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
     * path to the JAR (e.g., "file:/path/to/my-jar.jar").
     * </p>
     *
     * @param c The class whose location is desired.
     * @see FileUtils#urlToFile(URL) to convert the result to a {@link File}.
     */
    public static URL getLocation(final Class<?> c) {
        if (c == null) return null; // could not load the class

        // try the easy way first
        try {
            final URL codeSourceLocation =
                c.getProtectionDomain().getCodeSource().getLocation();
            if (codeSourceLocation != null) return codeSourceLocation;
        }
        catch (final SecurityException e) {
            // NB: Cannot access protection domain.
        }
        catch (final NullPointerException e) {
            // NB: Protection domain or code source is null.
        }

        // NB: The easy way failed, so we try the hard way. We ask for the class
        // itself as a resource, then strip the class's path from the URL string,
        // leaving the base path.

        // get the class's raw resource path
        final URL classResource = c.getResource(c.getSimpleName() + ".class");
        if (classResource == null) return null; // cannot find class resource

        final String url = classResource.toString();
        final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
        if (!url.endsWith(suffix)) return null; // weird URL

        // strip the class's path from the URL string
        final String base = url.substring(0, url.length() - suffix.length());

        String path = base;

        // remove the "jar:" prefix and "!/" suffix, if present
        if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

        try {
            return new URL(path);
        }
        catch (final MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    } 

    /**
     * Converts the given {@link URL} to its corresponding {@link File}.
     * <p>
     * This method is similar to calling {@code new File(url.toURI())} except that
     * it also handles "jar:file:" URLs, returning the path to the JAR file.
     * </p>
     * 
     * @param url The URL to convert.
     * @return A file path suitable for use with e.g. {@link FileInputStream}
     * @throws IllegalArgumentException if the URL does not correspond to a file.
     */
    public static File urlToFile(final URL url) {
        return url == null ? null : urlToFile(url.toString());
    }

    /**
     * Converts the given URL string to its corresponding {@link File}.
     * 
     * @param url The URL to convert.
     * @return A file path suitable for use with e.g. {@link FileInputStream}
     * @throws IllegalArgumentException if the URL does not correspond to a file.
     */
    public static File urlToFile(final String url) {
        String path = url;
        if (path.startsWith("jar:")) {
            // remove "jar:" prefix and "!/" suffix
            final int index = path.indexOf("!/");
            path = path.substring(4, index);
        }
        try {
            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) 
                 && path.matches("file:[A-Za-z]:.*")) {
                path = "file:/" + path.substring(5);
            }
            return new File(new URL(path).toURI());
        }
        catch (final MalformedURLException e) {
            // NB: URL is not completely well-formed.
        }
        catch (final URISyntaxException e) {
            // NB: URL is not completely well-formed.
        }
        if (path.startsWith("file:")) {
            // pass through the URL as-is, minus "file:" prefix
            path = path.substring(5);
            return new File(path);
        }
        throw new IllegalArgumentException("Invalid URL: " + url);
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
