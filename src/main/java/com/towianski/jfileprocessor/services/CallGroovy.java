/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.services;

import com.towianski.jfileprocess.actions.UpFolderAction;
import com.towianski.utils.MyLogger;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

/**
 *
 * @author stan
 */
public class CallGroovy {
    private static final MyLogger logger = MyLogger.getLogger( UpFolderAction.class.getName() );

    GroovyScriptEngine gse = null;
    String[] pathRoots = {};
    
public CallGroovy()
    {
    
    }
    
public CallGroovy( String[] pathRoots )
    {
    try {
        gse = new GroovyScriptEngine( pathRoots );
        }
    catch( Exception exc )
        {
        exc.printStackTrace();
        }
    }
    
public void groovyScriptEngineRun( String groovyFile, Binding binding ) 
//        throws IOException, ResourceException, ScriptException 
    {
    try {
        gse.run( groovyFile, binding );
        logger.info( "callGroovy.groovyScriptEngineRun() at 5" );
        }
    catch( Exception exc )
        {
        exc.printStackTrace();
        }
    }

}
