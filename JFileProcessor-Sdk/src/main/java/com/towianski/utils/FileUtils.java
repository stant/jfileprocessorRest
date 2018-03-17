/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.models.ConnUserInfo;
import com.towianski.models.JfpRestURIConstants;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class FileUtils
    {

    public static void FileMove( ConnUserInfo connUserInfo, Path sourcePath, Path targetPath )
        {
        System.out.println("jfilewin FileMove()" );
        System.out.println("jfilewin FileMove()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
            RestTemplate restTemplate = new RestTemplate();
            try {
//                 restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                Map<String, String> param = new HashMap<String, String>();
                String oldFile = URLEncoder.encode( sourcePath.toString(), "UTF-8" ); //.replace( "/", "|" );
                String newFile = URLEncoder.encode( targetPath.toString(), "UTF-8" ); //.replace( "/", "|" );
                param.put("newname",newFile);
                param.put("oldname",oldFile);
                HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
                HttpEntity<String> response = restTemplate.exchange( "http://" + connUserInfo.getToHost() + ":8080" + JfpRestURIConstants.RENAME_FILE 
                                    , HttpMethod.PUT, requestEntity, String.class, param );
                System.out.println("jfilewin FileMove()  response =" + response + "=" );
                }
            catch( Exception exc )
                {
                exc.printStackTrace();
                }
            }
        else
            {
            System.out.println("jfilewin FileMove()" );
    //        Path sourcePath = Paths.get( tcl.getOldValue().toString().trim() );
            System.out.println( "try to move dir source =" + sourcePath + "=   target =" + targetPath + "=" );
            if ( Files.exists( sourcePath ) )
                {
                try
                    {
                    Files.move( sourcePath, targetPath );
                    } 
                catch (IOException ex)
                    {
                    Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
