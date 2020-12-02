package com.towianski.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.towianski.models.ResultsData;

import java.io.File;
import java.io.IOException;
import static com.towianski.utils.DesktopUtils.getJfpConfigHome;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class Rest {

    private static final MyLogger logger = MyLogger.getLogger( Rest.class.getName() );
    private static File saveReadJsonlockFile = getJfpConfigHome( "save-read-json-file-LOCK", "file", false );

    static File programMemoryFile = null;

    static {
        programMemoryFile = getJfpConfigHome( "ProgramMemory.json", "file", true );

        logger.info( "Rest.saveObjectToFile() Lock  fileName =" + saveReadJsonlockFile + "=" );
        }

    static public Object readObjectFromFile( String fileName, TypeReference typeRef )
        {
        synchronized( saveReadJsonlockFile ) {
            logger.info( "REST.readObjectFromFile()   fileName =" + fileName + "=" );
            Object obj = null;
    //        ProgramMemory programMemory = new ProgramMemory();
            //read json file data to String
            byte[] jsonData = new byte[0];
            File fromFile = null;
            File lockFile = null;
            FileLock lock = null;
            FileChannel channel = null;

            try {
                lockFile = saveReadJsonlockFile;
                channel = new RandomAccessFile( lockFile, "rw" ).getChannel();

                // Use the file channel to create a lock on the file.
                // This method blocks until it can retrieve the lock.
                lock = channel.lock();

                fromFile = getJfpConfigHome( fileName, "file", false );
                if ( ! fromFile.exists() )
                    {
                    logger.info( "REST.readObjectFromFile() read file =" + fromFile.toString() + " does not exist." );
                    return null;
                    }
    //            jsonData = Files.readAllBytes( Paths.get( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName ) );
    //            logger.info( "read file =" + fromFile.toString() + "= to get jsonData =" + jsonData.toString() + "=" );

                //create ObjectMapper instance
                ObjectMapper objectMapper = new ObjectMapper();

                //convert json string to object
                obj = objectMapper.readValue( fromFile, typeRef );
                //logger.info( "REST.readObjectFromFile() ppobj.getShowHiddenFilesFlag() =" + ((typeRef)obj).isShowHiddenFilesFlag() + "=" );
                //JSON from file to Object
    //            Staff obj = mapper.readValue(new File("c:\\file.json"), Staff.class);            
                }
            catch (IOException e) 
                {
                e.printStackTrace();
                return null;
                }
            finally
                {
                try {
                    // Release the lock - if it is not null!
                    if( lock != null ) {
                        lock.release();
                    }

                    // Close the file
                    channel.close();

                    Files.deleteIfExists( lockFile.toPath() );
                    }
                catch (IOException e) 
                    {
                    logger.info( "REST.readObjectFromFile() Error closing LOCK file!" );
                    e.printStackTrace();
                    }
                }

            logger.info( "Object\n" + jsonData);
            return obj;
        } //sync
    }

    static public Object jsonToObject( String json, TypeReference typeRef )
        {
        Object obj = null;

        try {
            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object
            obj = objectMapper.readValue( json, typeRef );
            }
        catch (IOException e) 
            {
            e.printStackTrace();
            return null;
            }

        logger.info( "Object\n" + json);
        return obj;
        }

    static public ResultsData jsonToObject( String json, Object obj ) 
        {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ResultsData resultsData = objectMapper.readValue( json, ResultsData.class );
            return resultsData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void saveObjectToFile( String fileName, Object obj ) 
        {
        synchronized( saveReadJsonlockFile ) {
            File toFile = null;
            File lockFile = null;
            FileLock lock = null;
            FileChannel channel = null;

            try {
                lockFile = saveReadJsonlockFile;

                channel = new RandomAccessFile( lockFile, "rw" ).getChannel();

                // Use the file channel to create a lock on the file.
                // This method blocks until it can retrieve the lock.
                lock = channel.lock();

                toFile = getJfpConfigHome( fileName, "file", false );

                ObjectMapper objectMapper = new ObjectMapper();

                //configure Object mapper for pretty print
                objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true);

                //writing to console, can write to any output stream such as file
        //            toFile = new File( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName );

                //        StringWriter stringEmp = new StringWriter();
                objectMapper.writeValue( toFile, obj );
                logger.info( "write Object JSON file =" + toFile );
                } 
            catch (Exception e) 
                {
                logger.info( "REST.saveObjectToFile()   Error!" );
                e.printStackTrace();
                }
            finally
                {
                try {
                    // Release the lock - if it is not null!
                    if( lock != null ) {
                        lock.release();
                    }

                    // Close the file
                    channel.close();

                    Files.deleteIfExists( lockFile.toPath() );
                    }
                catch (IOException e) 
                    {
                    logger.info( "REST.saveObjectToFile() Error closing LOCK file!" );
                    e.printStackTrace();
                    }
                }
        } // sync
    }

    static public String saveObjectToString( Object obj ) 
        {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            //configure Object mapper for pretty print
            objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true);
            
            StringWriter stringWr = new StringWriter();
            objectMapper.writeValue( stringWr, obj );
            return stringWr.toString();
            }
        catch (IOException ex) 
            {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;
        }

    static public void saveStringToFile( String fileName, String outs ) 
        {
        //writing to console, can write to any output stream such as file
        try {
            File toFile = getJfpConfigHome( fileName, "file", false );
            PrintWriter out = new PrintWriter( toFile );

            out.println( outs );
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info( "write JSON file = " + fileName );
    }

//    @Bean
    static public RestTemplate createNoHostVerifyRestTemplate() 
        //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException 
        {
        try //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException
            {
            logger.info( "entered createNoHostVerifyRestTemplate()" );
            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
            
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            
            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
            
            HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
            
            requestFactory.setHttpClient(httpClient);
            
            requestFactory.setBufferRequestBody(false);     // Very Big. upload file was uselessly slow and this sped it up to download speed !
            
            RestTemplate rt = new RestTemplate(requestFactory);
         //   rt .setErrorHandler( new RestTemplateResponseErrorHandler() );   // to handle globally I think but I don't think I want this.
            return rt;
            //return new RestTemplate(requestFactory);
            }
        catch (NoSuchAlgorithmException ex)
            {
            logger.severeExc( ex );
            } 
        catch (KeyStoreException ex)
            {
            logger.severeExc( ex );
            } 
        catch (KeyManagementException ex)
            {
            logger.severeExc( ex );
            }
        return null;
        }

//    @Bean
    static public RestTemplate createNoHostVerifyShortTimeoutRestTemplate() 
        //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException 
        {
        try
            {
            logger.info( "entered createNoHostVerifyShortTimeoutRestTemplate()" );
            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
            
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout( 100 )
                    .setSocketTimeout( 100 )
                    .setConnectionRequestTimeout( 1000 )
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
            
            HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
            
            requestFactory.setHttpClient(httpClient);
            
            return new RestTemplate(requestFactory);
            }
        catch (NoSuchAlgorithmException ex)
            {
            logger.severeExc( ex );
            } 
        catch (KeyStoreException ex)
            {
            logger.severeExc( ex );
            } 
        catch (KeyManagementException ex)
            {
            logger.severeExc( ex );
            }
        return null;
        }
    
    static public RestTemplate timeoutRestTemplate( RestTemplateBuilder restTemplateBuilder )
        {
        return restTemplateBuilder
                .setConnectTimeout( Duration.ofMillis( 100 ) )
                .setReadTimeout( Duration.ofMillis( 100 ) )
                .build();
        }

//    @Bean
    static public CloseableHttpClient createNoHostVerifyShortTimeoutCloseableHttpClient() 
        //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException 
        {
        try
            {
            logger.info( "entered createNoHostVerifyShortTimeoutRestTemplate()" );
            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
            
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout( 100 )
                    .setSocketTimeout( 100 )
                    .setConnectionRequestTimeout( 1000 )
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
            
            return httpClient;
            }
        catch (NoSuchAlgorithmException ex)
            {
            logger.severeExc( ex );
            } 
        catch (KeyStoreException ex)
            {
            logger.severeExc( ex );
            } 
        catch (KeyManagementException ex)
            {
            logger.severeExc( ex );
            }
        return null;
        }    

        public static final String REST_SERVICE_URI = "http://localhost:8080/SecureRESTApiWithBasicAuthentication";
  
    /*
     * Add HTTP Authorization header, using Basic-Authentication to send user-credentials.
     */
    public static HttpHeaders getHeaders( String user, String password ){
        String base64Credentials = new String(Base64.encodeBase64( (user + ":" + password).getBytes()));
         
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Credentials);
        //headers.setBasicAuth( user, password );
        //logger.fine( "header Authorization =Basic " + base64Credentials + "=" );
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
}

