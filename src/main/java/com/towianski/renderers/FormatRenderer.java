/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.renderers;

import com.towianski.utils.MyLogger;
import java.text.Format;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;

/*
 *	Use a formatter to format the cell Object
 */
public class FormatRenderer extends DefaultTableCellRenderer
{
    private static final MyLogger logger = MyLogger.getLogger( FormatRenderer.class.getName() );
    private Format formatter;

    /*
     *   Use the specified formatter to format the Object
     */
    public FormatRenderer(Format formatter)
        {
//        System.err.println( "entered FormatRenderer() set formatter" );
        this.formatter = formatter;
        }

    public void setValue(Object value)
        {
        //  Format the Object before setting its value in the renderer

        try
            {
//            System.err.println( "entered FormatRenderer() setValue()" );
            if ( value instanceof Date )
                {
                if ( value != null )
                    value = formatter.format(value);
                else
                    value = formatter.format( new Date() ); //(Date)value);
                }
            else
                {
                value = formatter.format(value);
                }
            }
        catch(IllegalArgumentException e) { logger.info( "FormatRenderer() setValue() caught error." ); }
        catch(Exception e) { logger.info( "FormatRenderer() setValue() caught error." ); }

        super.setValue(value);
        }

    /*
     *  Use the default date/time formatter for the default locale
     */
    public static FormatRenderer getDefaultDateTimeRenderer()
    {
        return new FormatRenderer( DateFormat.getDateTimeInstance() );
    }

    /*
     *  Use the default date/time formatter for the default locale
     */
    public static FormatRenderer getDateTimeRenderer()
    {
        return new FormatRenderer( DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ) );
    }

    /*
     *  Use the default time formatter for the default locale
     */
    public static FormatRenderer getTimeRenderer()
    {
        return new FormatRenderer( DateFormat.getTimeInstance() );
    }
}
