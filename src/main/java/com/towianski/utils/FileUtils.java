/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.models.ConnUserInfo;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.Sftp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class FileUtils
    {
    public static boolean exists( ConnUserInfo connUserInfo, String targetPath )
        {
        System.out.println("jfilewin Exists()" );
        System.out.println("jfilewin Exists()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
            Sftp sftp = null;
            sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
            return sftp.exists( targetPath.toString() );
            }
        else
            {
            System.out.println("local Exists()" );
            return Files.exists( Paths.get( targetPath ) );
            }
        }

    public static void createDirectory( ConnUserInfo connUserInfo, String targetPath )
        {
        System.out.println("jfilewin createDirectory()" );
        System.out.println("jfilewin createDirectory()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
            Sftp sftp = null;
            sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
            sftp.mkDir( targetPath.toString() );
            }
        else
            {
            System.out.println("local Exists()" );
            try
                {
                Files.createDirectory( Paths.get( targetPath ) );
                } catch (IOException ex)
                {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    public static void fileMove( ConnUserInfo connUserInfo, String sourcePath, String targetPath )
        {
        System.out.println("jfilewin FileMove()" );
        System.out.println("jfilewin FileMove()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
//            RestTemplate restTemplate = new RestTemplate();
            RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
            
            try {
//                 restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                
                MultiValueMap<String, String> params = new LinkedMultiValueMap();
//                String oldFile = URLEncoder.encode( sourcePath.toString(), "UTF-8" ); //.replace( "/", "|" );
//                String newFile = URLEncoder.encode( targetPath.toString(), "UTF-8" ); //.replace( "/", "|" );
//                param.put( "newname", newFile );
//                param.put( "oldname", oldFile );
                params.add( "newname", targetPath.toString() );
                params.add( "oldname", sourcePath.toString() );
//                HttpEntity<Object> requestEntity = new HttpEntity<Object>( params, headers );
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );
                HttpEntity<String> response = noHostVerifyRestTemplate.exchange( connUserInfo.getToUri() + JfpRestURIConstants.RENAME_FILE 
                                    , HttpMethod.PUT, requestEntity, String.class );  //, params );
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
            if ( Files.exists( Paths.get( sourcePath ) ) )
                {
                try
                    {
                    Files.move( Paths.get( sourcePath ), Paths.get( targetPath ) );
                    } 
                catch (IOException ex)
                    {
                    Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
