
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.towianski.jfileprocessor.JFileFinderWin;
import java.util.HashMap;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author stowians
 */
public class ProgramMemory {
    
    JFileFinderWin jFileFinderWin = null;
    boolean showHiddenFilesFlag = false;
    boolean showOwnerFlag = false;
    boolean showGroupFlag = false;
    boolean showPermsFlag = false;
    String startConsoleCmd = "";
    String myEditorCmd = "";
    String checkForUpdateDate = "";   // value is only here, which is better
        public static int TBLCOLMODEL_WIDTH_FILETYPE = 0;
        public static int TBLCOLMODEL_WIDTH_FOLDERTYPE = 1;
        public static int TBLCOLMODEL_WIDTH_PATH_SHORT = 2;
        public static int TBLCOLMODEL_WIDTH_PATH_LONG = 2;
        public static int TBLCOLMODEL_WIDTH_MODIFIEDDATE = 3;
        public static int TBLCOLMODEL_WIDTH_SIZE = 4;
        public static int TBLCOLMODEL_WIDTH_OWNER = 5;
        public static int TBLCOLMODEL_WIDTH_GROUP = 6;
        public static int TBLCOLMODEL_WIDTH_PERMS = 7;
    HashMap<Integer,Integer> tblColModelWidths = new HashMap<Integer,Integer>();
    
    public ProgramMemory()
        {
        System.out.println( "ProgramMemory constructor()" );
            
        setTblColModelWidth( TBLCOLMODEL_WIDTH_FILETYPE, 16 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_FOLDERTYPE, 16 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_PATH_SHORT, 300 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_PATH_LONG, 600 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_MODIFIEDDATE, 95 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_SIZE, 73 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_OWNER, 73 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_GROUP, 73 );
        setTblColModelWidth( TBLCOLMODEL_WIDTH_PERMS, 73 );
        }
    
    @JsonIgnore
    public void setJFileFinderWin( JFileFinderWin jFileFinderWin ) {
        this.jFileFinderWin = jFileFinderWin;
    }

//    public ProgramMemory( JFileFinderWin jFileFinderWin )
//        {
//        System.out.println( "ProgramMemory constructor()" );
//        }

    public boolean isShowHiddenFilesFlag() {
        return showHiddenFilesFlag;
    }

    public void setShowHiddenFilesFlag(boolean showHiddenFilesFlag) {
        this.showHiddenFilesFlag = showHiddenFilesFlag;
    }

    public boolean isShowOwnerFlag() {
        return showOwnerFlag;
    }

    public void setShowOwnerFlag(boolean showOwnerFlag) {
        this.showOwnerFlag = showOwnerFlag;
    }

    public boolean isShowGroupFlag() {
        return showGroupFlag;
    }

    public void setShowGroupFlag(boolean showGroupFlag) {
        this.showGroupFlag = showGroupFlag;
    }

    public boolean isShowPermsFlag() {
        return showPermsFlag;
    }

    public void setShowPermsFlag(boolean showPermsFlag) {
        this.showPermsFlag = showPermsFlag;
    }

    public String getCheckForUpdateDate() {
        return checkForUpdateDate;
    }

    public void setCheckForUpdateDate(String checkForUpdateDate) {
        this.checkForUpdateDate = checkForUpdateDate;
    }

    public String getStartConsoleCmd()
        {
        return startConsoleCmd;
        }

    public void setStartConsoleCmd(String startConsoleCmd)
        {
        this.startConsoleCmd = startConsoleCmd;
        }

    public String getMyEditorCmd()
        {
        return myEditorCmd;
        }

    public void setMyEditorCmd(String myEditorCmd)
        {
        this.myEditorCmd = myEditorCmd;
        }
    
    
    //---------
    @JsonIgnore
    public void infuseSavedValues()
        {
        System.out.println( "ProgramMemory infuseSavedValues()" );
        jFileFinderWin.setShowHiddenFilesFlag(showHiddenFilesFlag);
        jFileFinderWin.setShowOwnerFlag(showOwnerFlag);
        jFileFinderWin.setShowGroupFlag(showGroupFlag);
        jFileFinderWin.setShowPermsFlag(showPermsFlag);
        jFileFinderWin.setStartConsoleCmd( startConsoleCmd );
        jFileFinderWin.setMyEditorCmd( myEditorCmd );
        }
    
    //---------
    @JsonIgnore
    public void extractCurrentValues()
        {
        System.out.println( "ProgramMemory ExtractCurrentValues()" );
        try {
            this.showHiddenFilesFlag = jFileFinderWin.getShowHiddenFilesFlag();
            this.showOwnerFlag = jFileFinderWin.isShowOwnerFlag();
            this.showGroupFlag = jFileFinderWin.getShowGroupFlag();
            this.showPermsFlag = jFileFinderWin.isShowPermsFlag();
            this.startConsoleCmd = jFileFinderWin.getStartConsoleCmd();
            this.myEditorCmd = jFileFinderWin.getMyEditorCmd();
            
            TableColumnModel tblColModel = jFileFinderWin.getFilesTbl().getColumnModel();
            setTblColModelWidth( TBLCOLMODEL_WIDTH_FILETYPE, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_FILETYPE ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_FOLDERTYPE, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_FOLDERTYPE ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_PATH_SHORT, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_PATH_LONG, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_MODIFIEDDATE, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_MODIFIEDDATE ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_SIZE, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_SIZE ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_OWNER, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_OWNER ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_GROUP, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_GROUP ).getWidth() );
            setTblColModelWidth( TBLCOLMODEL_WIDTH_PERMS, tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PERMS ).getWidth() );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        System.out.println( "ProgramMemory showHiddenFilesFlag =" + showHiddenFilesFlag );
        System.out.println( "ProgramMemory showOwnerFlag =" + showOwnerFlag );
        }

        public HashMap<Integer,Integer> getTblColModelWidths()
            {
            return this.tblColModelWidths;
            }

        void setTblColModelWidths( HashMap<Integer,Integer> tblColModelWidths )
            {
            this.tblColModelWidths = tblColModelWidths;
            }

        @JsonIgnore
        public int getTblColModelWidth( int i )
            {
            return this.tblColModelWidths.get( i );
            }

        @JsonIgnore
        void setTblColModelWidth( int i, int value )
            {
            this.tblColModelWidths.put( i, value );
            }

}

