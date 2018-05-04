/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.models;

import java.util.ArrayList;

/**
 *
 * @author stan
 */
public class CircularArrayList {
    
    private ArrayList<String> cal = new ArrayList<String>();
    
    int idx = -1;
    int addAt = -1;
    int highest = -1;
    int max = -1;
    
    public CircularArrayList( int size )
        {
        cal = new ArrayList<String>( size );
        for ( int i = 0; i < size; i++ )
            {
            cal.add( "" );
            }
        max = size;
        }
    
    public void add( String newPath )
        {
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
        System.out.println( "CircularArrayList ADD idx =" + idx+ "   addAt =" + addAt + "   highest =" + highest + "   max =" + max + "   newpath =" + newPath + "=" );
        cal.set( addAt, newPath );
        }
    
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
        System.out.println( "CircularArrayList BACK idx =" + idx+ "   addAt =" + addAt + "   highest =" + highest + "   max =" + max );
        return cal.get( idx );
        }
    
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
        System.out.println( "CircularArrayList FOR idx =" + idx+ "   addAt =" + addAt + "   highest =" + highest + "   max =" + max );
        return cal.get( idx );
        }
    
}
