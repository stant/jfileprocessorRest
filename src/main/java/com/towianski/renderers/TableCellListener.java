/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import com.towianski.models.FilesTblModel;
import com.towianski.utils.MyLogger;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;

/*
 *  This class listens for changes made to the data in the table via the
 *  TableCellEditor. When editing is started, the value of the cell is saved
 *  When editing is stopped the new value is saved. When the old and new
 *  values are different, then the provided Action is invoked.
 *
 *  The source of the Action is a TableCellListener instance.
 */
public class TableCellListener implements PropertyChangeListener, Runnable
{
    private static final MyLogger logger = MyLogger.getLogger( TableCellListener.class.getName() );
    private JTable table;
    private Action action;

    private int row;
    private int column;
    private Object oldValue;
    private Object newValue;
    private Boolean onOffFlag = true;
    private String skipFirstPath = "";
    private Boolean doingCancel = false;
    private int filesTblModelType = -1;

	/**
	 *  Create a TableCellListener.
	 *
	 *  @param table   the table to be monitored for data changes
	 *  @param action  the Action to invoke when cell data is changed
	 */
	public TableCellListener(JTable table, Action action)
	{
		this.table = table;
		this.action = action;
		this.table.addPropertyChangeListener( this );
	}

	/**
	 *  Create a TableCellListener with a copy of all the data relevant to
	 *  the change of data for a given cell.
	 *
	 *  @param row  the row of the changed cell
	 *  @param column  the column of the changed cell
	 *  @param oldValue  the old data of the changed cell
	 *  @param newValue  the new data of the changed cell
	 */
	private TableCellListener(JTable table, int filesTblModelType, int row, int column, Object oldValue, Object newValue)
            {
            this.table = table;
            this.filesTblModelType = filesTblModelType;
            this.row = row;
            this.column = column;
            this.oldValue = oldValue;
            this.newValue = newValue;
            }

    public Boolean getOnOffFlag() {
        return onOffFlag;
    }

    public void setOnOffFlag(Boolean onOffFlag) {
        this.onOffFlag = onOffFlag;
        logger.info( "tbl cell editor SET flag() onOffFlag = " + this.onOffFlag );
    }

    public void skipFirstEditOn( int filesTblModelType, String skipFirstPath )
    {
        this.filesTblModelType = filesTblModelType;
        this.skipFirstPath = skipFirstPath;
    }
    
	/**
	 *  Get the column that was last edited
	 *
	 *  @return the column that was edited
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 *  Get the new value in the cell
	 *
	 *  @return the new value in the cell
	 */
	public Object getNewValue()
	{
		return newValue;
	}

	/**
	 *  Get the old value of the cell
	 *
	 *  @return the old value of the cell
	 */
	public Object getOldValue()
	{
		return oldValue;
	}

	/**
	 *  Get the row that was last edited
	 *
	 *  @return the row that was edited
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 *  Get the table of the cell that was changed
	 *
	 *  @return the table of the cell that was changed
	 */
	public JTable getTable()
	{
            return table;
	}

    public int getFilesTblModelType() {
        return filesTblModelType;
    }
        
        
//
//  Implement the PropertyChangeListener interface
//
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		//  A cell has started/stopped editing

		if ("tableCellEditor".equals(e.getPropertyName()))
                    {
                    if (table.isEditing())
                        {
                        //logger.info( "propertyChange()  goto processEditingStarted()" );
                        processEditingStarted();
                        }
                    else
                        {
                        //logger.info( "propertyChange()  goto processEditingStopped()" );
                        processEditingStopped();
                        }
		}
	}

	/*
	 *  Save information of the cell about to be edited
	 */
	public void processEditingCanceled()
            {
            if (table.isEditing())
                {
                logger.info( "processEditingCanceled()" );
                doingCancel = true;
//                //logger.info( "tbl cell editor stopping() table.getEditingRow() = " + table.getEditingRow() );
//                row = table.convertRowIndexToModel( table.getEditingRow() );
//                //logger.info( "tbl cell editor stopping() row = " + row );
//                //logger.info( "tbl cell editor stopping() table.getEditingColumn() = " + table.getEditingColumn() );
//                column = table.convertColumnIndexToModel( table.getEditingColumn() );
//                //logger.info( "tbl cell editor stopping() column = " + column );
//
//                FilesTblModel filesTblModel = (FilesTblModel) table.getModel();
//                filesTblModel.setCellEditable( row, column, false );
                }
            }

	/*
	 *  Save information of the cell about to be edited
	 */
	private void processEditingStarted()
	{
            //  The invokeLater is necessary because the editing row and editing
            //  column of the table have not been set when the "tableCellEditor"
            //  PropertyChangeEvent is fired.
            //  This results in the "run" method being invoked

            //logger.info( "processEditingStarted()" );
            SwingUtilities.invokeLater( this );
	}
	/*
	 *  See above.
	 */
	@Override
	public void run()
	{
            logger.info( "tbl cell editor run()" );
            if ( table.getEditingRow() < 0 )
                {
                logger.info( "tbl cell editor run() SKIP" );
                return;
                }
            //logger.info( "tbl cell editor run() table.getEditingRow() = " + table.getEditingRow() );
            row = table.convertRowIndexToModel( table.getEditingRow() );
            //logger.info( "tbl cell editor run() row = " + row );
            //logger.info( "tbl cell editor run() table.getEditingColumn() = " + table.getEditingColumn() );
            column = table.convertColumnIndexToModel( table.getEditingColumn() );
            //logger.info( "tbl cell editor run() column = " + column );

            FilesTblModel filesTblModel = (FilesTblModel) table.getModel();
            //oldValue = table.getModel().getValueAt(row, column);
            oldValue = filesTblModel.getValueAt(row, column);
            newValue = null;
            doingCancel = false;
        }

	/*
	 *	Update the Cell history when necessary
	 */
	private void processEditingStopped()
	{
            logger.info( "processEditingStopped()" );
            //logger.info( "tbl cell editor stopping() table.getEditingRow() = " + table.getEditingRow() );
            row = table.convertRowIndexToModel( table.getEditingRow() );
            //logger.info( "tbl cell editor stopping() row = " + row );
            //logger.info( "tbl cell editor stopping() table.getEditingColumn() = " + table.getEditingColumn() );
            column = table.convertColumnIndexToModel( table.getEditingColumn() );
            //logger.info( "tbl cell editor stopping() column = " + column );

            //newValue = table.getModel().getValueAt(row, column);
            FilesTblModel filesTblModel = (FilesTblModel) table.getModel();
            newValue = filesTblModel.getValueAt(row, column);

//            logger.info( "tbl cell editor stopping() oldValue = " + oldValue + "=   newValue =" + newValue + "=" );
//            if ( ! onOffFlag && oldValue == null && newValue.equals( skipFirstPath ) )
//                {
//                logger.info( "tbl cell editor stopping() SKIP insert New Folder path" );
//                onOffFlag = true;
//                table.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
//                return;
//                }
//            logger.info( "tbl cell editor stopping() onOffFlag = " + onOffFlag );
//            if ( ! onOffFlag || ! filesTblModel.isCellEditable( row, column ) )
//                {
//                logger.info( "tbl cell editor stopping() SKIP not edittable cell or is OFF" );
//                return;
//                }

		//  The data has changed, invoke the supplied Action

            logger.info( "stopping() Old   : " + getOldValue());
            logger.info( "stopping() New   : " + getNewValue());

            if ( oldValue.equals( skipFirstPath ) )  // New Folder
                {
                //logger.info( "oldValue.equals( skipFirstPath )" );
                oldValue = null;
                }
            
            if ( newValue.equals( oldValue ) )
                {
                //logger.info( "newValue.equals( oldValue )" );
                filesTblModel.setCellEditable( row, column, false );
                }
            
            if ( ! newValue.equals( oldValue ) )
                   //   || newValue.equals( skipFirstPath )  // old and new == new file path
                {
                //logger.info( "oldValue.NOT equals( oldValue )   row =" + row + "   column =" + column );
                filesTblModel.setCellEditable( row, column, false );
                // i added but it is not right    oldValue = null;

                //  Make a copy of the data in case another cell starts editing
                //  while processing this change

                //logger.info( "doingCancel =" + doingCancel );
                if ( doingCancel )
                    {
                    filesTblModel.setValueAt( oldValue, getRow(), getColumn() );
                    }
                else
                    {
                    TableCellListener tcl = new TableCellListener(
                            getTable(), this.filesTblModelType, getRow(), getColumn(), getOldValue(), getNewValue());
                    //logger.info( "processEditingStopped() tcl tbl col count =" + filesTblModel.getColumnCount() );

                    ActionEvent event = new ActionEvent(
                            tcl,
                            ActionEvent.ACTION_PERFORMED,
                            "");
                    action.actionPerformed(event);
                    }
                }
	}
}
