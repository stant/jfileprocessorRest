package com.towianski.jfileprocess.actions;

import com.towianski.interfaces.Callback;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class SystemTrayIcon {
    
    private Callback callback = null;
    private String execDesc = "running";
    
    public static void main(String[] args) {
            System.out.println("entered main()");
            SystemTrayIcon systemTrayIcon = new SystemTrayIcon( null, "running something" );
    }
    
    public SystemTrayIcon( Callback callback, String execDesc ) 
        {
        //System.out.println("entered SystemTrayIcon()");

        this.callback = callback;
        this.execDesc = execDesc;
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }

                /* Turn off metal's use of bold fonts */
            UIManager.put("swing.boldMetal", Boolean.FALSE);

            //adding TrayIcon.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //System.out.println("call to createAndShowGUI()");
                    createAndShowGUI();
                }
            });
                }
        }   catch (ClassNotFoundException ex) {
                Logger.getLogger(SystemTrayIcon.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(SystemTrayIcon.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(SystemTrayIcon.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(SystemTrayIcon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    private void createAndShowGUI() 
        {
        //System.out.println("entered createAndShowGUI()");

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
            }
        final PopupMenu popup = new PopupMenu();
        ImageIcon fileicon = null;
        try {
            fileicon = new ImageIcon(ImageIO.read(SystemTrayIcon.class.getResource("/icons/jfp-child.png")));
        } catch (IOException ex) {
            Logger.getLogger(SystemTrayIcon.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TrayIcon trayIcon = new TrayIcon( fileicon.getImage() );
        final SystemTray tray = SystemTray.getSystemTray();
         
        MenuItem runningItem = new MenuItem("running: " + execDesc );
        MenuItem exitItem = new MenuItem("Stop");
         
        popup.add(runningItem);
        popup.add(exitItem);
         
        trayIcon.setPopupMenu(popup);
         
        try {
            //System.out.println("try to add TrayIcon");
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
         
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                //System.out.println("doing exit/callback" );
                if ( callback != null )
                    callback.call();
                else
                    return;
            }
        });
    }
}
