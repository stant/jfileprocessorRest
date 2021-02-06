/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.towianski.utils.MyLogger;
import java.util.ArrayList;

/**
 *
 * @author stan
 */
public class CircularArrayList {
    
    private static final MyLogger logger = MyLogger.getLogger( CircularArrayList.class.getName() );
    private ArrayList<String> cal = new ArrayList<String>();
    
    int idx = -1;
    int addAt = -1;
    int highest = -1;
    int max = -1;

    public CircularArrayList() { }
    
    public CircularArrayList( int size )
        {
        cal = new ArrayList<String>( size );
        for ( int i = 0; i < size; i++ )
            {
            cal.add( "" );
            }
        max = size;
        }
    
    @JsonIgnore
    public void add( String newPath )
        {
        if ( idx >= 0 )  // Don't add duplicate of last entry
            {
            if ( cal.get( idx ).equals( newPath ) )
                return;
            }
        
        addAt ++;
        if ( addAt >= max )
            {
            addAt = 0;
            }
        else
            {
            highest = addAt;
            }
        idx = addAt;
        logger.info( "CircularArrayList ADD idx =" + idx+ "   addAt =" + addAt + "   highest =" + highest + "   max =" + max + "   newpath =" + newPath + "=" );
        cal.set( addAt, newPath );
        }
    
    @JsonIgnore
    public String getBackward()
        {
        if ( --idx < 0 )
            {
            idx = highest;
            }
        if ( idx < 0 )
            {
            return "";
            }
        logger.info( "CircularArrayList BACK idx =" + idx+ "   addAt =" + addAt + "   highest =" + highest + "   max =" + max );
        return cal.get( idx );
        }
    
    @JsonIgnore
    public String getForward()
        {
        if ( ++idx > highest )
            {
            idx = 0;
            }
        if ( idx < 0 )
            {
            return "";
            }
        logger.info( "CircularArrayList FOR idx =" + idx+ "   addAt =" + addAt + "   highest =" + highest + "   max =" + max );
        return cal.get( idx );
        }

    public ArrayList<String> getCal() {
        return cal;
    }

    public void setCal(ArrayList<String> cal) {
        this.cal = cal;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getAddAt() {
        return addAt;
    }

    public void setAddAt(int addAt) {
        this.addAt = addAt;
    }

    public int getHighest() {
        return highest;
    }

    public void setHighest(int highest) {
        this.highest = highest;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    
}
