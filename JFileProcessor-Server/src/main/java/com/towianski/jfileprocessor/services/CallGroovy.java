/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.services;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

/**
 *
 * @author stan
 */
public class CallGroovy {
    
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
        System.out.println( "callGroovy.groovyScriptEngineRun() at 5" );
        }
    catch( Exception exc )
        {
        exc.printStackTrace();
        }
    }

}
