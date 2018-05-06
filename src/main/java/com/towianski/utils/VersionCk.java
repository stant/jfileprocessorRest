/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**  
 *  written by: Stan Towianski - May 2018 
 * notes: I make assumption each of 3 version sections a.b.c is not longer then 4 digits: aaaa.bbbb.cccc-MODWORD1(-)modnum2
 * 5.10.13-release-1 becomes 0000500100013.501     6.0-snapshot becomes 0000600000000.100
 * MODWORD1 = -xyz/NotMatching, -SNAPSHOT, -ALPHA, -BETA, -RC, -RELEASE/nothing  return:  .0, .1, .2, .3, .4, .5
 * modnum2 = up to 2 digit/chars second version
 * */
public class VersionCk {

    public static boolean isVersionHigher( String baseVersion, String testVersion )
        {
        System.out.println( "versionToComparable( baseVersion ) =" + versionToComparable( baseVersion ) );
        System.out.println( "versionToComparable( testVersion ) =" + versionToComparable( testVersion ) + " is this higher ? " + (versionToComparable( testVersion ).compareTo( versionToComparable( baseVersion ) ) > 0) );
        return versionToComparable( testVersion ).compareTo( versionToComparable( baseVersion ) ) > 0;
        }

    //----  not worrying about += for something so small
    private static String versionToComparable( String version )
        {
//        System.out.println("version - " + version);
        String versionNum = version;
        int at = version.indexOf( '-' );
        if ( at >= 0 )
            versionNum = version.substring( 0, at );

        String[] numAr = versionNum.split( "\\." );
        String versionFormatted = "0";
        for ( String tmp : numAr )
            {
            versionFormatted += String.format( "%4s", tmp ).replace(' ', '0');
            }
        while ( versionFormatted.length() < 12 )  // pad out to aaaa.bbbb.cccc
            {
            versionFormatted += "0000";
            }
//        System.out.println( "converted min version =" + versionFormatted + "=   : " + versionNum );
        return versionFormatted + getVersionModifier( version, at );
        }

    //----  use order low to high: -xyz, -SNAPSHOT, -ALPHA, -BETA, -RC, -RELEASE/nothing  returns: 0, 1, 2, 3, 4, 5
    private static String getVersionModifier( String version, int at )
        {
//        System.out.println("version - " + version );
        String[] wordModsAr = { "-SNAPSHOT", "-ALPHA", "-BETA", "-RC", "-RELEASE" };        
        
        if ( at < 0 )
            return "." + wordModsAr.length + "00";   // make nothing = RELEASE level
        
        int i = 1;
        for ( String word : wordModsAr )
            {
            if ( ( at = version.toUpperCase().indexOf( word ) ) > 0 )
                return "." + i + getSecondVersionModifier( version.substring( at + word.length() ) );
            i++;
            }

        return ".000";
        }

    //----  add 2 chars for any number after first modifier.  -rc2 or -rc-2   returns 02
    private static String getSecondVersionModifier( String version )
        {
        System.out.println( "second modifier =" + version + "=" );
        Matcher m = Pattern.compile("(.*?)(\\d+).*").matcher( version );
//        if ( m.matches() )
//            System.out.println( "match ? =" + m.matches() + "=   m.group(1) =" + m.group(1) + "=   m.group(2) =" + m.group(2) + "=   m.group(3) =" + (m.groupCount() >= 3 ? m.group(3) : "x") );
//        else
//            System.out.println( "No match" );
        return m.matches() ? String.format( "%2s", m.group(2) ).replace(' ', '0') : "00";
        }
    
    public static void main(String[] args) 
        {
        checkVersion( "3.10.0", "3.4.0");
        checkVersion( "5.4.2", "5.4.1");
        checkVersion( "5.4.4", "5.4.5");
        checkVersion( "5.4.9", "5.4.12");
        checkVersion( "5.9.222", "5.10.12");
        checkVersion( "5.10.12", "5.10.12");
        checkVersion( "5.10.13", "5.10.14");
        checkVersion( "6.7.0", "6.8");
        checkVersion( "6.7", "2.7.0");
        checkVersion( "6", "6.3.1");
        checkVersion( "4", "4.0.0");
        checkVersion( "6.3.0", "6");
        checkVersion( "5.10.12-Alpha", "5.10.12-beTA");
        checkVersion( "5.10.13-release", "5.10.14-beta");
        checkVersion( "6.7.0", "6.8-snapshot");
        checkVersion( "6.7.1", "6.7.0-release");
        checkVersion( "6-snapshot", "6.0.0-beta");
        checkVersion( "6.0-snapshot", "6.0.0-whatthe");
        checkVersion( "5.10.12-Alpha-1", "5.10.12-alpha-2");
        checkVersion( "5.10.13-release-1", "5.10.13-release2");
        checkVersion( "10-rc42", "10.0.0-rc53");
        checkVersion( "1.6", "1.6.0-rc99");
        checkVersion( "1.6.0-rc99", "1.6.0");
        }

    private static void checkVersion(String baseVersion, String testVersion) 
        {
        System.out.println( "baseVersion - " + baseVersion );
        System.out.println( "testVersion - " + testVersion );
        System.out.println( "isVersionHigher = " + isVersionHigher( baseVersion, testVersion ) );
        System.out.println( "---------------");
        }
    
    }
