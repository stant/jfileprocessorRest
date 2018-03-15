/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Stan Towianski
 */
public class ClipboardUtils {
 
    public static ArrayList<Path> getClipboardFilesList()
        {
        ArrayList<Path> copyPaths = new ArrayList<Path>();
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();

            int max = dataFlavors.length;
            System.out.println( "clipboard Size :" + max );

            for (int count = 0; count < max; count++) 
                {
                //System.out.println(" : " + dataFlavors[count]);

                if ( DataFlavor.stringFlavor != dataFlavors[count] ) 
                    {
                    Object object = transferable.getTransferData( dataFlavors[count] );
                    if (object instanceof List) 
                        {
                        System.out.println("found list on clipboard" );
                        List selectedFileList = (List) object;
                        int size = selectedFileList.size();

                        for (int index = 0; index < size; index++) 
                            {
                            File file = (File) selectedFileList.get(index);
                            copyPaths.add( file.toPath() );
                            //System.out.println( "clipboard got file =" + file.toPath().toString() + "=" );
                            }
                        }
                    }
                } // for
            }
        catch (Exception exception) 
            {
            exception.printStackTrace();
            }
        return copyPaths;
        }    
 
  /**
  * Place a String on the clipboard, and make this class the
  * owner of the Clipboard's contents.
  */
  public static void setClipboardContents(String aString)
  {
    StringSelection stringSelection = new StringSelection(aString);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents( stringSelection, null );
  }

  /**
  * Get the String residing on the clipboard.
  *
  * @return any text found on the Clipboard; if none found, return an
  * empty String.
  */
    public static ArrayList<String> getClipboardStringsList( String delimiter )
        {
        ArrayList<String> StringsList = new ArrayList<String>();
        
        try {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            String result = "";
            boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if ( hasTransferableText ) 
                {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
                StringsList = new ArrayList<String>( Arrays.asList( result.split( delimiter ) ) );
                }
            }
        catch ( Exception ex )
            {
            System.out.println(ex);
            ex.printStackTrace();
            }
        return StringsList;
        }

//
//            /* Map to XML and reverse */
//        String mapToString = objectToString(hashMap);
//        Map parsedMap = (Map) stringToObject(mapToString);
//        System.out.println("Map to XML: \n" + mapToString + "\nXML to map:\n" + parsedMap);
//
//        /* List to XML and reverse */
//        String listToString = objectToString(list);
//        List parsedList = (List) stringToObject(listToString);
//        System.out.println("List to XML: \n" + listToString + "\nXML to list:\n" + parsedList);
//    }
//
//    public static String objectToString(Object hashMap) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        XMLEncoder xmlEncoder = new XMLEncoder(bos);
//        xmlEncoder.writeObject(hashMap);
//        xmlEncoder.close();
//        return bos.toString();
//    }
//
//    public static Object stringToObject(String string) {
//        XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(string.getBytes()));
//        return xmlDecoder.readObject();
//    }

}
