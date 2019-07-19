package com.towianski.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.towianski.models.ResultsData;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import static com.towianski.utils.DesktopUtils.getJfpConfigHome;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;

public class Rest {

    private final static MyLogger logger = MyLogger.getLogger( Rest.class.getName() );
    private static File saveReadJsonlockFile = getJfpConfigHome( "save-read-json-file-LOCK", "file", false );

    static File programMemoryFile = null;

    static {
        programMemoryFile = getJfpConfigHome( "ProgramMemory.json", "file", true );

        System.out.println( "Rest.saveObjectToFile() Lock  fileName =" + saveReadJsonlockFile + "=" );
        }

    static public Object readObjectFromFile( String fileName, TypeReference typeRef )
        {
        synchronized( saveReadJsonlockFile ) {
            System.out.println( "REST.readObjectFromFile()   fileName =" + fileName + "=" );
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
                    System.out.println( "REST.readObjectFromFile() read file =" + fromFile.toString() + " does not exist." );
                    return null;
                    }
    //            jsonData = Files.readAllBytes( Paths.get( System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName ) );
    //            System.out.println( "read file =" + fromFile.toString() + "= to get jsonData =" + jsonData.toString() + "=" );

                //create ObjectMapper instance
                ObjectMapper objectMapper = new ObjectMapper();

                //convert json string to object
                obj = objectMapper.readValue( fromFile, typeRef );
                //System.out.println( "REST.readObjectFromFile() ppobj.getShowHiddenFilesFlag() =" + ((typeRef)obj).isShowHiddenFilesFlag() + "=" );
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
                    System.out.println( "REST.readObjectFromFile() Error closing LOCK file!" );
                    e.printStackTrace();
                    }
                }

            System.out.println("Object\n" + jsonData);
            return obj;
        } //sync
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
                System.out.println("write Object JSON file =" + toFile );
                } 
            catch (Exception e) 
                {
                System.out.println( "REST.saveObjectToFile()   Error!" );
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
                    System.out.println( "REST.saveObjectToFile() Error closing LOCK file!" );
                    e.printStackTrace();
                    }
                }
        } // sync
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
        System.out.println("write JSON file = " + fileName );
    }

//    @Bean
    static public RestTemplate createNoHostVerifyRestTemplate() 
        //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException 
        {
        try //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException
            {
            System.out.println( "entered createNoHostVerifyRestTemplate()" );
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
            
            return new RestTemplate(requestFactory);
            }
        catch (NoSuchAlgorithmException ex)
            {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            } 
        catch (KeyStoreException ex)
            {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            } 
        catch (KeyManagementException ex)
            {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;
        }

//    @Bean
    static public RestTemplate createNoHostVerifyShortTimeoutRestTemplate() 
        //throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException 
        {
        try
            {
            System.out.println( "entered createNoHostVerifyShortTimeoutRestTemplate()" );
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
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            } 
        catch (KeyStoreException ex)
            {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            } 
        catch (KeyManagementException ex)
            {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;
        }
    
    static public RestTemplate timeoutRestTemplate( RestTemplateBuilder restTemplateBuilder )
        {
        return restTemplateBuilder
                .setConnectTimeout(100)
                .setReadTimeout(100)
                .build();
        }
    

}

