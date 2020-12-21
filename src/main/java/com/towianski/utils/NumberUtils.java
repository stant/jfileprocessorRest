/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 *
 * @author s
 */
public class NumberUtils {

    private static final MyLogger logger = MyLogger.getLogger(NumberUtils.class.getName() );

public static String humanReadableByteCount(long bytes) 
    {
    try {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.3f %ciB", value / 1024.0, ci.current());
        }
    catch( Exception ex )
        {
        Writer buffer = new StringWriter();
        PrintWriter pw = new PrintWriter(buffer);
        ex.printStackTrace(pw);
        logger.info( "Exception: " + buffer.toString() );
        }
    return "err";
    }

}
