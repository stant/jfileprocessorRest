/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.listeners;

import com.towianski.models.FilesTblModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author stan
 */
public class MyRowSorterListener implements RowSorterListener {

    TableRowSorter<TableModel> sorter = null;
    int sortCol = 0;
    javax.swing.SortOrder sortOrder = SortOrder.DESCENDING;
    
    public MyRowSorterListener( TableRowSorter<TableModel> sorter, int sortCol, javax.swing.SortOrder sortOrder )
    {
        this.sorter = sorter;
        this.sortCol = sortCol;
        this.sortOrder = sortOrder;
    }
    
    @Override
    public void sorterChanged(RowSorterEvent evt) {
//        logger.info( "\nentered sorterChanged" );
//        for ( RowSorter.SortKey key : sorter.getSortKeys() )
//            {
//            logger.info( "sorterChanged sorter.getSortKeys().get(0).getColumn() = " + 
//                     key.getColumn() + "   sort order = " + key.getSortOrder() );
//            }
        RowSorter.SortKey sortKey = new RowSorter.SortKey( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, SortOrder.DESCENDING );
        int sortSize = sorter.getSortKeys().size();
//        logger.info( "sorterChanged sortSize = " + sortSize );
        if ( sortSize > 0 && sortKey.getColumn() != sorter.getSortKeys().get(0).getColumn() )
            {
            sorter.removeRowSorterListener(this);
            List<RowSorter.SortKey> newList = new ArrayList<>();
            newList.add( sortKey );
            RowSorter.SortKey newKey = sorter.getSortKeys().get(0);
            if ( sortSize == 2 )
                {
                if ( sortOrder == SortOrder.DESCENDING )
                    {
                    //logger.info( "same key Column() toggle to ASCENDING" );
                    newKey = new RowSorter.SortKey( newKey.getColumn(), SortOrder.ASCENDING );
                    }
                else
                    {
                    //logger.info( "same key Column() toggle to DESCENDING" );
                    newKey = new RowSorter.SortKey( newKey.getColumn(), SortOrder.DESCENDING );
                    }
                }
            else
                {
                newKey = new RowSorter.SortKey( newKey.getColumn(), SortOrder.ASCENDING );
                //logger.info( "Add newkey" );
                }
            newList.add( newKey );
            //logger.info( "sorterChanged ADD sortKey = " + sortKey );
            sorter.setSortKeys( newList );
            //logger.info( "final sorterChanged" );
//            for ( RowSorter.SortKey key : sorter.getSortKeys() )
//                {
//                logger.info( "sorterChanged key Column() = " + key.getColumn() + "   sort order = " + key.getSortOrder() );
//                }
            sorter.addRowSorterListener( new MyRowSorterListener( sorter, newKey.getColumn(), newKey.getSortOrder() ) );
            sorter.sort();
            }
//        else
//            {
//            logger.info( "sorterChanged SKIP add sortKey = " + sortKey );
//            }
    }

}
