package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Stan Towianski - June 2015
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilesTblModel extends AbstractTableModel 
{
    private ArrayList<String>colNames;
    private ArrayList<ArrayList> data;
    public static int FILESTBLMODEL_FILETYPE = 0;
    public static int FILETYPE_NORMAL = 0;
    public static int FILETYPE_LINK = 1;
    public static int FILETYPE_OTHER = 2;
    
//    public static int FILESTBLMODEL_ISDIR = 1;
    public static int FILESTBLMODEL_FOLDERTYPE = 1;
    public static int FOLDERTYPE_FILE_NOACCESS = -1;
    public static int FOLDERTYPE_FILE = 0;
    public static int FOLDERTYPE_FILE_NOT_FOUND = 1;
    public static int FOLDERTYPE_FOLDER = 2;
    public static int FOLDERTYPE_FOLDER_NOACCESS = 3;
    
    public static int FILESTBLMODEL_PATH = 2;
    public static int FILESTBLMODEL_MODIFIEDDATE = 3;
    public static int FILESTBLMODEL_SIZE = 4;
    public static int FILESTBLMODEL_OWNER = 5;
    public static int FILESTBLMODEL_GROUP = 6;
    public static int FILESTBLMODEL_PERMS = 7;
    private HashMap<String,Boolean> editableCells = new HashMap<String,Boolean>(); // 2d array to represent rows and columns

//    public PreviewImportTblModel( ArrayList<String> colNamesArg, String[][] dataArg )
//        {
//        colNames = colNamesArg;
//        data = dataArg;
//        
//        System.out.println( "row count =" + data.length );
//        System.out.println( "col count =" + data[0].length );
//        }

    public FilesTblModel()
        {
        colNames = new ArrayList<String>();
        data = new ArrayList<ArrayList>();
            ArrayList newRow = new ArrayList();
            newRow.add( FILETYPE_NORMAL );
            newRow.add( FOLDERTYPE_FOLDER  );
            newRow.add( "" );
            newRow.add( Calendar.getInstance().getTime() );
            newRow.add( (long) 0 );
            newRow.add( "" );
            newRow.add( "" );
            newRow.add( "---" );
            
            data.add( 0, newRow );
        
        System.out.println( "GUI DEFAULT CONSTRUCTOR() row count =" + data.size() );
        System.out.println( "col count =" + data.get(0).size() );
        }

    public FilesTblModel( ArrayList<String> colNamesArg, ArrayList<ArrayList> dataArg )
        {
        colNames = colNamesArg;
        data = dataArg;
        
        System.out.println( "row count =" + data.size() );
        System.out.println( "col count =" + data.get(0).size() );
        }

    public ArrayList<String> getColNames()
        {
        return colNames;
        }

    public void setColNames(ArrayList<String> colNames)
        {
        this.colNames = colNames;
        }

    public ArrayList<ArrayList> getData()
        {
        return data;
        }

    public void setData(ArrayList<ArrayList> data)
        {
        this.data = data;
        }

    
    @JsonProperty
    @Override
    public int getColumnCount() 
    {
        if ( data.size() < 1 )
            return 0;
        return data.get(0).size(); 
    }
    
    @JsonProperty
    @Override
    public int getRowCount() { return data.size();}
    
    @JsonProperty
    @Override
    public Object getValueAt(int row, int col) 
        {
        //System.out.println( "getValueAt row =" + row + "  col =" + col );
        try {
            if ( data.get(row).get(col) == "" ) 
                {
                //System.out.println( "NOT EXISTS getValueAt row =" + row + "  col =" + col );
                }
            } 
        catch( Exception ex )
            {
            return "";
            }
        return data.get(row).get(col); 
        }
    
    @JsonIgnore
    @Override
    public void setValueAt(Object aValue, int row, int col) 
        {
        //System.out.println( "setValueAt  value =" + aValue + "=  row =" + row + "  col =" + col );
        //if ( 1 == 1 ) return;
        try {
            if ( row < data.size() && col < data.get( row ).size() )
                {
                if ( col == FILESTBLMODEL_MODIFIEDDATE )
                    if ( aValue == null )
                        data.get( row ).set( col, new Date() );
                    else
                        data.get( row ).set( col, (Date) aValue );
                else if ( col == FILESTBLMODEL_SIZE )
                    data.get( row ).set( col, (Long) aValue );
                else if ( col == FILESTBLMODEL_FOLDERTYPE )
                    data.get( row ).set( col, (Integer) aValue );
                else if ( col == FILESTBLMODEL_FILETYPE )
                    data.get( row ).set( col, (Integer) aValue );
                else
                    data.get( row ).set( col, (String) aValue );
                }
            } 
        catch( Exception ex )
            {
            System.out.println( "setValueAt  Exception for value =" + aValue + "=  row =" + row + "  col =" + col );
            ex.printStackTrace();
            }
        //refresh();   NOTE:  DOING THIS CAUSED IT TO DROP A WHOLE COLUMN ! !
        }
    
    @JsonProperty
    public String getColumnName(int col) {
        return colNames.get( col );
    }

    @JsonProperty
    public Class<?> getColumnClass(int columnIndex)
    {
        try {
            if ( getColumnCount() > 1 )
                {
                if ( columnIndex == FILESTBLMODEL_MODIFIEDDATE )
                    return Date.class;
                else if ( columnIndex == FILESTBLMODEL_SIZE )
                    return Long.class;
                else if ( columnIndex == FILESTBLMODEL_FOLDERTYPE )  //|| columnIndex == FILESTBLMODEL_ISLINK )
                    return Integer.class;
                else if ( columnIndex == FILESTBLMODEL_FILETYPE )
                    return Integer.class;
                }
            }
        catch ( Exception ex )
            {
            System.out.println( "column type exc:\n" );
            ex.printStackTrace();
            System.out.println( "\n" );
            }
        return String.class;
    }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */
    
    @JsonIgnore
    public void insertColAt( int col ) 
        {
        //System.out.println( "getValueAt row =" + row + "  col =" + col );
        try {
            colNames.add( col, " NEW " );

            for ( ArrayList<String> colList : data )
                {
                colList.add( col, "---" );
                }
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */    
    @JsonIgnore
    public void insertRowAt( int row, String Path ) 
        {
        //System.out.println( "getValueAt row =" + row + "  col =" + col );
        try {
            //System.out.println( "before add row table col count =" + this.getColumnCount() );
            ArrayList newRow = new ArrayList();
            newRow.add( FILETYPE_NORMAL );
            newRow.add( FOLDERTYPE_FOLDER  );
            newRow.add( Path );
            newRow.add( Calendar.getInstance().getTime() );
            newRow.add( (long) 0 );
            newRow.add( "" );
            newRow.add( "" );
            newRow.add( "---" );
            
            data.add( 0, newRow );
            //System.out.println( "after add row table col count =" + this.getColumnCount() );
            editableCells = new HashMap<String,Boolean>();
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */    
    @JsonIgnore
    public void replaceRowAt( int row, String Path ) 
        {
        //System.out.println( "getValueAt row =" + row + "  col =" + col );
        try {
            //System.out.println( "before add row table col count =" + this.getColumnCount() );
            ArrayList newRow = new ArrayList();
            newRow.add( FILETYPE_NORMAL );
            newRow.add( FOLDERTYPE_FOLDER  );
            newRow.add( Path );
            newRow.add( Calendar.getInstance().getTime() );
            newRow.add( (long) 0 );
            newRow.add( "" );
            newRow.add( "" );
            newRow.add( "---" );
            
            data.set( 0, newRow );
            //System.out.println( "after add row table col count =" + this.getColumnCount() );
            editableCells = new HashMap<String,Boolean>();
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */    
    @JsonProperty
    public static ArrayList getNewRow( String Path ) 
        {
        ArrayList newRow = new ArrayList();
        try {
            //System.out.println( "before add row table col count =" + this.getColumnCount() );
            newRow.add( FILETYPE_NORMAL );
            newRow.add( FOLDERTYPE_FOLDER  );
            newRow.add( Path );
            newRow.add( Calendar.getInstance().getTime() );
            newRow.add( (long) 0 );
            newRow.add( "" );
            newRow.add( "" );
            newRow.add( "---" );
            } 
        catch( Exception ex )
            {
            return null;
            }
        return newRow;
        }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */    
    @JsonIgnore
    public void deleteRowAt( int row ) 
        {
        //System.out.println( "delete row =" + row + "  col =" + col );
        try {
            //System.out.println( "before add row table col count =" + this.getColumnCount() );
            data.remove( row );
            //System.out.println( "after add row table col count =" + this.getColumnCount() );
            editableCells = new HashMap<String,Boolean>();
            } 
        catch( Exception ex )
            {
            return;
            }
        refresh();
        }
    
    @JsonIgnore
    public void refresh()
        {
        fireTableChanged(null);
        }

    @JsonIgnore
    @Override
    public boolean isCellEditable( int row, int col )  // custom isCellEditable function
        {
        //if ( editableCells.containsKey( row + "-" + col ) )
//        System.out.println( "cell " + row + ", " + col + " is edittable" );
        return editableCells.containsKey( row + "-" + col );
        }
    
    @JsonIgnore
    public void setCellEditable( int row, int col, boolean value )
        {
        if ( value )
            {   
            editableCells.put( row + "-" + col, true );
            System.out.println( "set edittable cell " + row + ", " + col );
            }
        else
            {
            editableCells.remove( row + "-" + col );
            }
        this.fireTableCellUpdated( row, col );
        }
    
//    //previewImportTbl.getTableHeader().addMouseListener(new         
//    class ColumnListener extends MouseAdapter() {
//      @Override
//      public void mouseClicked(MouseEvent mouseEvent) {
//        int index = previewImportTbl.convertColumnIndexToModel(previewImportTbl.columnAtPoint(mouseEvent.getPoint()));
//        if (index >= 0) {
//          System.out.println("Clicked on column " + index);
//        }
//      };
//    }
    
}
