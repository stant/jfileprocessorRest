/*
Copyright (C) 2019 Stan Towianski 
 */
package com.towianski.boot;

import com.towianski.utils.MyLogger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author stan
 */
public class CustomClassLoader extends ClassLoader {
 
    private static final MyLogger logger = MyLogger.getLogger( CustomClassLoader.class.getName() );

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        byte[] b = loadClassFromFile(name);
        return defineClass(name, b, 0, b.length);
    }
 
    private byte[] loadClassFromFile(String filename)  {
        logger.info( "filename.replace('.', File.separatorChar) + \".class\" =" + filename.replace('.', File.separatorChar) + ".class" + "=" );
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                filename.replace('.', File.separatorChar) + ".class");
        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = 0;
        try {
            while ( (nextValue = inputStream.read()) != -1 ) {
                byteStream.write(nextValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = byteStream.toByteArray();
        return buffer;
    }
}
