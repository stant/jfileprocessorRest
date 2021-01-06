/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.httpsutils.HttpsUtils;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.Sftp;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class FileUtils
    {
    private static final MyLogger logger = MyLogger.getLogger( FileUtils.class.getName() );
    
    public static boolean exists( ConnUserInfo connUserInfo, String targetPath )
        {
        logger.info( "jfilewin Exists()" );
        logger.info( "connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
            if ( connUserInfo.isUsingSftp() )
                {
                Sftp sftp = null;
                //sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), connUserInfo.getToSshPortInt() );
                sftp = new Sftp( "TO", connUserInfo );
                return sftp.exists( targetPath.toString() );
                }
            else if ( connUserInfo.isUsingHttps() )
                {
                Boolean response = null;
                
                try
                    {
                    RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

                    MultiValueMap<String, String> params = new LinkedMultiValueMap();
                    //String rmtFilePath = URLEncoder.encode( targetPath, "UTF-8" );
                    //params.add( "filename", URLEncoder.encode( targetPath, "UTF-8" ) );
                    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() ) );

//                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.DOES_FILE_EXIST, Boolean.class );
//                    HttpEntity request = new HttpEntity( Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() ) );

                    // make an HTTP GET request with headers
                    ResponseEntity<Boolean> responseEntity = noHostVerifyRestTemplate.exchange(
                            connUserInfo.getToUri() + JfpRestURIConstants.DOES_FILE_EXIST + "?filename=" + URLEncoder.encode( targetPath, "UTF-8" ),
                            HttpMethod.GET,
                            requestEntity,
                            Boolean.class
                    );
                    response = responseEntity.getBody();
                    }
                catch( Exception exc )
                    {
                    logger.info( "FileUtils.exists() DOES_FILE_EXIST threw Exception !!" );
                    logger.severeExc( exc );
                    return false;
                    }
                logger.info( "jfilewin exists()  response =" + response + "=" );
                return response;
                }
            }
        else
            {
            logger.info( "local Exists()" );
            return Files.exists( Paths.get( targetPath ) );
            }
        return false;
        }

    public static void createDirectory( ConnUserInfo connUserInfo, String targetPath )
        {
        logger.info( "jfilewin createDirectory()" );
        logger.info( "jfilewin createDirectory()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
            if ( connUserInfo.isUsingSftp() )
                {
                //Sftp sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), connUserInfo.getToSshPortInt() );
                Sftp sftp = new Sftp( "TO", connUserInfo );
                sftp.mkDir( targetPath.toString() );
                }
            else if ( connUserInfo.isUsingHttps() )
                {
                try
                    {
                    logger.info( "mkdir =" + targetPath.toString() + "=" );
                    HttpsUtils httpsUtilsTar = new HttpsUtils( "TO", connUserInfo );
                    httpsUtilsTar.mkDir( targetPath );
                    logger.finer( "Directory created :: " + targetPath.toString() );
                    } 
                catch (Exception ex)
                    {
                    logger.severeExc( ex );
//                    processStatus = "Error";
//                    message = ex + ": " + destinationFolderStr;
//                    errorList.add( destinationFolderStr + " -> create remote Directory - ERROR " + ex );
                    return;
                    }
                }
            }
        else
            {
            logger.info( "local Exists()" );
            try
                {
                Files.createDirectory( Paths.get( targetPath ) );
                } 
            catch (IOException ex)
                {
                logger.severeExc( ex );
                }
            }
        }

    public static void fileMove( ConnUserInfo connUserInfo, String sourcePath, String targetPath )
            throws HttpClientErrorException
        {
        logger.info( "jfilewin FileMove()" );
        logger.info( "jfilewin FileMove()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
//            RestTemplate restTemplate = new RestTemplate();
            RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
            
            try {
                HttpHeaders headers = Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() );
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                
                MultiValueMap<String, String> params = new LinkedMultiValueMap();
//                String oldFile = URLEncoder.encode( sourcePath.toString(), "UTF-8" ); //.replace( "/", "|" );
//                String newFile = URLEncoder.encode( targetPath.toString(), "UTF-8" ); //.replace( "/", "|" );
                params.add( "newname", targetPath.toString() );
                params.add( "oldname", sourcePath.toString() );
//                HttpEntity<Object> requestEntity = new HttpEntity<Object>( params, headers );
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );
                HttpEntity<String> response = noHostVerifyRestTemplate.exchange( 
                            connUserInfo.getToUri() + JfpRestURIConstants.RENAME_FILE 
                            , HttpMethod.PUT, requestEntity, String.class );
                if ( response.getBody().matches( ".*\"status\".*401.*\"Unauthorized\".*" ) )
                    {
                    logger.info( "caught 401 Response !" );
                    throw new HttpClientErrorException( HttpStatus.UNAUTHORIZED );
                    }
                logger.info( "jfilewin FileMove()  response =" + response + "=" );
                }
            catch (HttpClientErrorException.Unauthorized httpExc )
                {
                logger.info( "caught 401" + httpExc.getLocalizedMessage() );
                throw httpExc;
                }
            catch( Exception exc )
                {
                exc.printStackTrace();
                }
            }
        else
            {
            logger.info( "jfilewin FileMove()" );
    //        Path sourcePath = Paths.get( tcl.getOldValue().toString().trim() );
            logger.info( "try to move dir source =" + sourcePath + "=   target =" + targetPath + "=" );
            if ( Files.exists( Paths.get( sourcePath ) ) )
                {
                try
                    {
                    Files.move( Paths.get( sourcePath ), Paths.get( targetPath ) );
                    } 
                catch (IOException ex)
                    {
                    logger.severeExc( ex );
                    }
                }
            }
        }
    
    public static void touch( ConnUserInfo connUserInfo, String path ) 
            throws IOException 
        {
        Path targetPath = Paths.get( path );
        logger.info( "jfilewin touch()  connUserInfo.isConnectedFlag() =" + connUserInfo.isConnectedFlag() + "=" );

        if ( connUserInfo.isConnectedFlag() )
            {
            if ( connUserInfo.isUsingSftp() )
                {
                //sftp = new Sftp( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost(), connUserInfo.getToSshPortInt() );
                Sftp sftp = new Sftp( "TO", connUserInfo );
                sftp.touch( targetPath.toString() );
                }
            else if ( connUserInfo.isUsingHttps() )
                {
                    // FIXXX
                }
            }
        else
            {
            if (Files.exists( targetPath )) 
                {
                Files.setLastModifiedTime( targetPath, FileTime.from(Instant.now()));
                }
            else
                {
                Files.createFile( targetPath );
                }
            }
        }
    
    public static String getFolderFromPath( String path ) 
            throws IOException 
        {
        logger.info( "path =" + path + "=" );
        String pathOrig = path;
        path = path.replace( "\\", "/" );
        Path targetPath = Paths.get( path );
        logger.finest( "targetPath =" + targetPath + "=" );
        logger.finest( "targetPath.getParent() =" + targetPath.getParent() + "=" );
        if ( pathOrig.indexOf( "\\" ) >= 0 )
            {
            return targetPath.getParent().toString().replace( "/", "\\"  );
            }
        return targetPath.getParent().toString();
        }
    
    public static String getFilenameFromPath( String path ) 
            throws IOException 
        {
        path = path.replace( "\\", "/" );
        Path targetPath = Paths.get( path );
        return targetPath.getFileName().toString();
        }

    }
