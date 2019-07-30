/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocess.actions;

import com.towianski.boot.JFileProcessorVersion;
import com.towianski.utils.DesktopUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan
 */
public class ProcessInThread {

    public ProcessInThread() {}        

//    public static int execJava(Class klass, String... passArgs)
//            throws IOException, InterruptedException 
//        {
//        String javaHome = System.getProperty("java.home");
//        String javaBin = javaHome +
//                File.separator + "bin" +
//                File.separator + "java";
//        String classpath = System.getProperty("java.class.path");
//        String className = klass.getCanonicalName();
//
//        String[] runArgs = { javaBin, "-cp", classpath, className };
//        
//        String[] allArgs = Arrays.copyOf( runArgs, runArgs.length + passArgs.length);
//        System.arraycopy( passArgs, 0, allArgs, runArgs.length, passArgs.length );
//  
//        for ( String tmp : allArgs )
//            {
//            System.out.println( "(" + tmp + ") " );
//            }
//        ProcessBuilder builder = new ProcessBuilder( allArgs );
//
//        Process process = builder.start();
////        process.waitFor();
//        return process.exitValue();
//    }

    public int execJava2( ArrayList<String> cmdListArg, Boolean daemonFlag, String... passArgs)
            throws IOException, InterruptedException 
        {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String javaW = javaBin + "w";
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            javaBin += ".exe";
            javaW += ".exe";
            }
        String classpath = System.getProperty("java.class.path");
        ArrayList<String> cmdList = new ArrayList<String>();
        for ( String tmp : cmdListArg )
            {
            tmp = tmp.replace( "$JFP", JFileProcessorVersion.getFileName() );
            tmp = tmp.replace( "$JAVA", javaBin );
            tmp = tmp.replace( "$JAVAW", javaW );
            tmp = tmp.replace( "$CLASSPATH", classpath );
            tmp = tmp.replace( "$JFPHOMETMP", DesktopUtils.getJfpHomeTmpDir( false ) );
            cmdList.add( tmp );
            System.out.println( "cmdList tmp = " + tmp + "=" );
            }
        
        String[] runJarList = classpath.split( System.getProperty( "path.separator" ) );
        //System.out.println( "classpath = " + classpath + "=" );
        //System.out.println( "runJarList = " + runJarList );
        String runJar = null;
        for ( String tmp : runJarList )
            {
            //System.out.println( "runJar tmp = " + tmp + "=" );
            if ( tmp.indexOf( "JFileProcessor" ) >= 0 )
                {   
                runJar = tmp;
                break;
                }
            }
//        String className = klass.getCanonicalName();
//        String className = "com.towianski.boot.StartApp";
        
//        String[] runArgs = { javaBin, "-cp", classpath, className };
//        String[] runPosixArgs = { javaBin, "-Dserver.port=" + System.getProperty( "server.port", "8443" ), "-cp", classpath, "org.springframework.boot.loader.JarLauncher" };
//        String[] runWinArgs = { "powershell.exe", "Start-Process", "-FilePath", "\"" + javaBin + "-Dserver.port=" + System.getProperty( "server.port", "8443" ) + "-cp" + classpath + "org.springframework.boot.loader.JarLauncher" + "\"", "-Wait" };
//        String[] runPosixArgs = { javaBin, "-cp", classpath, "org.springframework.boot.loader.JarLauncher" };
//        String[] runWinArgs = { "powershell.exe", "Start-Process", "-FilePath", "\"" + javaBin + "-cp" + classpath + "org.springframework.boot.loader.JarLauncher" + "\"", "-Wait" };

//        String[] runArgs = System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) ? runWinArgs : runPosixArgs;
//        String[] runArgs = runPosixArgs;
        String[] runArgs = cmdList.toArray( new String[0] );
        
        String[] allArgs = Arrays.copyOf( runArgs, runArgs.length + passArgs.length);
        System.arraycopy( passArgs, 0, allArgs, runArgs.length, passArgs.length );
  
        ProcessRunnable proc = new ProcessRunnable( null, allArgs );
        Thread bgThread = newThread( "ProcessInThread.execJava2", 0, daemonFlag, proc );
        bgThread.start();
        return proc.getExitValue();
    }

    public int execJavaString( String cmd, Boolean daemonFlag )
            throws IOException, InterruptedException 
        {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        ArrayList<String> cmdList = new ArrayList<String>();
//        for ( String tmp : cmdListArg )
//            {
//            tmp = tmp.replace( "$JAVA", javaBin );
//            tmp = tmp.replace( "$CLASSPATH", classpath );
//            cmdList.add( tmp );
//            System.out.println( "cmdList tmp = " + tmp + "=" );
//            }
        
//        String[] runJarList = classpath.split( System.getProperty( "path.separator" ) );
//        //System.out.println( "classpath = " + classpath + "=" );
//        //System.out.println( "runJarList = " + runJarList );
//        String runJar = null;
//        for ( String tmp : runJarList )
//            {
//            //System.out.println( "runJar tmp = " + tmp + "=" );
//            if ( tmp.indexOf( "JFileProcessor" ) >= 0 )
//                {   
//                runJar = tmp;
//                break;
//                }
//            }

  
        ProcessRunnableString proc = new ProcessRunnableString( null, cmd );
        Thread bgThread = newThread( "ProcessInThread.execJavaString", 0, daemonFlag, proc );
        bgThread.start();
        return proc.getExitValue();
    }

    public int execJava(Class klass, Boolean daemonFlag, String... passArgs)
            throws IOException, InterruptedException 
        {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String javaW = javaBin + "w";
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            javaBin += ".exe";
            javaW += ".exe";
            }
        String classpath = System.getProperty("java.class.path");
        String[] runJarList = classpath.split( System.getProperty( "path.separator" ) );
        //System.out.println( "classpath = " + classpath + "=" );
        //System.out.println( "runJarList = " + runJarList );
        String runJar = null;
        for ( String tmp : runJarList )
            {
            //System.out.println( "runJar tmp = " + tmp + "=" );
            if ( tmp.indexOf( "JFileProcessor" ) >= 0 )
                {   
                runJar = tmp;
                break;
                }
            }
//        String className = klass.getCanonicalName();
        String className = "com.towianski.boot.StartApp";
        
//        String[] runArgs = { javaBin, "-cp", classpath, className };
//        String[] runPosixArgs = { javaBin, "-Dserver.port=" + System.getProperty( "server.port", "8443" ), "-cp", classpath, "org.springframework.boot.loader.JarLauncher" };
//        String[] runWinArgs = { "powershell.exe", "Start-Process", "-FilePath", "\"" + javaBin + "-Dserver.port=" + System.getProperty( "server.port", "8443" ) + "-cp" + classpath + "org.springframework.boot.loader.JarLauncher" + "\"", "-Wait" };
        String[] runPosixArgs = { javaBin, "-cp", classpath, "org.springframework.boot.loader.JarLauncher" };
        String[] runWinArgs = { "powershell.exe", "Start-Process", "-FilePath", "\"" + javaBin + "-cp" + classpath + "org.springframework.boot.loader.JarLauncher" + "\"", "-Wait" };

//        String[] runArgs = System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) ? runWinArgs : runPosixArgs;
        String[] runArgs = runPosixArgs;
        
        String[] allArgs = Arrays.copyOf( runArgs, runArgs.length + passArgs.length);
        System.arraycopy( passArgs, 0, allArgs, runArgs.length, passArgs.length );
  
        ProcessRunnable proc = new ProcessRunnable( null, allArgs );
        Thread bgThread = newThread( "ProcessInThread.execJava", 0, daemonFlag, proc );
        bgThread.start();
        return proc.getExitValue();
    }

//    public static int exec( String startDir, String... passArgs ) 
//            throws IOException, InterruptedException 
//        {
//        String[] myargs =  { "" };
//        if ( passArgs[0].indexOf( " " ) >=0 )
//            {
//            myargs = passArgs[0].split( " " );
//            }
//        else
//            {
//            myargs = passArgs;
//            }
//
//        ProcessBuilder builder = null;
//        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
//            {
//            String[] dirArg =  { startDir };
//            String[] allArgs = Arrays.copyOf( myargs, myargs.length + 1 );
//            System.arraycopy( dirArg, 0, allArgs, myargs.length, 1 );
//
//            for ( String tmp : allArgs )
//                {
//                System.out.println( "(" + tmp + ") " );
//                }
//            builder = new ProcessBuilder( allArgs );
//            }
//        else
//            {
//            for ( String str : myargs )
//                {
//                System.out.println( "run cmd: (" + str + ") " );
//    //            cmd.add( str );
//                }
//            builder = new ProcessBuilder( myargs );
//
//            builder.directory( new File( startDir ) );
//            }
//        
//        Process process = builder.start();
////        IOThreadHandler outputHandler = new IOThreadHandler( process.getInputStream() );
////        outputHandler.start();
////        process.waitFor();
////        System.out.println(outputHandler.getOutput());
//        return process.exitValue();
//	}

    public int exec( String startDir, boolean daemonFlag, boolean waitFlag, String... passArgs ) 
            throws IOException, InterruptedException 
        {
        String[] myargs =  { "" };
        if ( passArgs[0].indexOf( " " ) >=0 && ! passArgs[0].startsWith( "\"" ) )
            {
            myargs = passArgs[0].split( " " );
            }
        else
            {
            myargs = passArgs;
            }

//        ProcessBuilder builder = null;
        ProcessRunnable proc = null;

        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            String[] dirArg =  { startDir };
            String[] allArgs = Arrays.copyOf( myargs, myargs.length + 1 );
            System.arraycopy( dirArg, 0, allArgs, myargs.length, 1 );

            for ( String tmp : allArgs )
                {
                System.out.println( "(" + tmp + ") " );
                }
//            builder = new ProcessBuilder( allArgs );
            proc = new ProcessRunnable( null, allArgs );
            }
        else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            for ( String str : myargs )
                {
                System.out.println( "run cmd: (" + str + ") " );
    //            cmd.add( str );
                }
//            builder = new ProcessBuilder( myargs );
            proc = new ProcessRunnable( startDir, myargs );
            }
        else
            {
            for ( String str : myargs )
                {
                System.out.println( "run cmd: (" + str + ") " );
    //            cmd.add( str );
                }
//            builder = new ProcessBuilder( myargs );
            proc = new ProcessRunnable( startDir, myargs );
            }
        
        Thread bgThread = newThread( "ProcessInThread.exec", 0, daemonFlag, proc );
        bgThread.start();
        if ( waitFlag )
            {
            System.out.println( "WAIT FOR thread command to join" );
            bgThread.join();
            }
        
//        IOThreadHandler outputHandler = new IOThreadHandler( process.getInputStream() );
//        outputHandler.start();
//        process.waitFor();
//        System.out.println(outputHandler.getOutput());
        return proc.getExitValue();
	}

    public static Thread newThread(String procName, long count, boolean daemonFlag, final Runnable r) 
        {
        Thread thread = new Thread( r );
        thread.setName( procName + "-" + thread.getName() + "-" + count );
        thread.setDaemon( daemonFlag );
        return thread;
        }

    public static void stopThread( Thread thd ) 
        {
        if ( thd != null )
            {
            thd.interrupt();
            System.out.println( "stopThread(" + thd.getName() + ") - before join()" );
            if ( thd.isAlive() )
                {
                try
                    {
                    thd.join();
                    } 
                catch (InterruptedException ex)
                    {
                    Logger.getLogger(ProcessInThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            System.out.println( "stopThread(" + thd.getName() + ") - after join()" );
            }
        }

//    private static class IOThreadHandler extends Thread 
//        {
//        private InputStream inputStream;
//        private StringBuilder output = new StringBuilder();
//
//        IOThreadHandler(InputStream inputStream) {
//            this.inputStream = inputStream;
//            }
//
//        public void run() 
//            {
//            Scanner br = null;
//            try {
//                br = new Scanner(new InputStreamReader(inputStream));
//                String line = null;
//                while (br.hasNextLine())
//                    {
//                    line = br.nextLine();
//                    output.append( "out-" + line + System.getProperty("line.separator"));
//                    }
//                }
//            finally 
//                {
//                br.close();
//                }
//            }
//
//        public StringBuilder getOutput() 
//            {
//            return output;
//            }
//        }

}