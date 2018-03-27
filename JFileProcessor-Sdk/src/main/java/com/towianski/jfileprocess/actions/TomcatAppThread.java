package com.towianski.jfileprocess.actions;

import com.towianski.models.JfpRestURIConstants;
import com.towianski.sshutils.JschSftpUtils;
import com.towianski.utils.Rest;
import java.io.*;
import org.springframework.web.client.RestTemplate;

/**
 * Example to watch a directory (or tree) for changes to files.
 * @author stan
 */
public class TomcatAppThread implements Runnable
    {
    String user = null;
    String passwd = null;
    String rmtHost = null;
    boolean cancelFlag = false;
    Object lockObj = null;
    String SERVER_URI = "http://" + rmtHost + ":8080";

    /**
     * Creates a WatchService and registers the given directory
     */
    public TomcatAppThread( String user, String passwd, String rmtHost )
        {
        this.user = user;
        this.passwd = passwd;
        this.rmtHost = rmtHost;
        this.lockObj = lockObj;
        SERVER_URI = "https://" + rmtHost + ":8443";
        }
    
    public void cancelRestServer()
        {
        System.out.println("TomcatAppThread set cancelFlag to true");
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        cancelFlag = true;
        System.out.println("TomcatAppThread exit cancelSearch()");
        }

    //    public RestTemplate sslNoCkRestTemplate()
//        {
//    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//
//SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
//        .loadTrustMaterial(null, acceptingTrustStrategy)
//        .build();
//
//SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//
//CloseableHttpClient httpClient = HttpClients.custom()
//        .setSSLSocketFactory(csf)
//        .build();
//
//HttpComponentsClientHttpRequestFactory requestFactory =
//        new HttpComponentsClientHttpRequestFactory();
//
//requestFactory.setHttpClient(httpClient);
//
//RestTemplate restTemplate = new RestTemplate(requestFactory);


//    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());
//
//      HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
//
//      RestTemplate template = new TestRestTemplate();
//      ((HttpComponentsClientHttpRequestFactory) template.getRequestFactory()).setHttpClient(httpClient);
      
    @Override
    public void run() {
        System.out.println( "entered TomcatAppThread run()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        RestTemplate restTemplate = new RestTemplate();
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
//        HttpComponentsClientHttpRequestFactory requestFactory =
//                new HttpComponentsClientHttpRequestFactory();
//
//        requestFactory.setHttpClient(httpClient);
//        RestTemplate noHostVerifyRestTemplate = new RestTemplate( requestFactory );
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

        cancelFlag = false;
        int downTimes = 99;
        JschSftpUtils jschSftpUtils = new JschSftpUtils();
        String response = null;
        boolean didFirstStart = false;
        
        while ( ! cancelFlag )
            {
            try
                {
                System.out.println( "TomcatAppThread.run() make rest /jfp/sys/ping call" );
                try
                    {
                    response = noHostVerifyRestTemplate.getForObject( SERVER_URI + JfpRestURIConstants.SYS_PING, String.class );
                    }
                catch( Exception exc )
                    {
                    System.out.println( "TomcatAppThread.run() ping threw Exception !!" );
                    response = null;
                    exc.printStackTrace();
                    }
                System.out.println( "TomcatAppThread.run() ping response =" + response + "=" );
                if ( ! cancelFlag && 
                    ( response == null || ! response.equalsIgnoreCase( "RUNNING" ) ) )
                    {
                    if ( ++downTimes > 5 )
                        {
//                        System.out.println( "RestServerSw.cancelRestServer() thread not null to make rest /jfp/sys/stop call" );
//                        restTemplate.getForObject(SERVER_URI + JfpRestURIConstants.SYS_STOP, String.class );

                        String[] mainCommand = System.getProperty("sun.java.command").split(" ");
//                        String jfpFilename = menuS mainCommand[0];
//                        System.out.println( "jfpFilename =" + jfpFilename + "=" );
//                        String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + "build" + System.getProperty( "file.separator" ) + "libs" + System.getProperty( "file.separator" );
                        String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" );
                        fpath = fpath.replace( "-Gui", "-Server" );
//                        System.out.println( "jfpFilename =" + jfpFilename + "=" );
                        String jfpFilename = "JFileProcessor-Server-1.5.11.jar";
                        System.out.println( "jfpFilename =" + jfpFilename + "=" );
                        
                        //jschSftpUtils.copyIfMissing( fpath + jfpFilename, user, passwd, rmtHost, jfpFilename );
                        jschSftpUtils.sftpIfDiff( fpath + jfpFilename, user, passwd, rmtHost, jfpFilename );
                        
                        System.out.println( "try jschSftpUtils   file =" + fpath + jfpFilename + "=   to remote =" + user + "@" + rmtHost + ":" + jfpFilename + "=" );
                        if ( ! cancelFlag )
                            {
                            jschSftpUtils.exec( user, passwd, rmtHost, "java -jar " + jfpFilename + " --logging.file=/tmp/jfp-springboot.logging" );
//java -jar your-spring.jar --security.require-ssl=true --server.port=8443 --server.ssl.key-store=keystore --server.ssl.key-store-password=changeit --server.ssl.key-password=changeit
                            }
                        System.out.println( "after start remote jfp server" );
                        Thread.sleep( 30000 );
                        downTimes = 0;
                        }
                    else
                        {
                        Thread.sleep( 1000 );
                        }
                    }
                else if ( didFirstStart ) // is running or first start
                    {
                    Thread.sleep( 4000 );
                    downTimes = 0;
                    }
                }
            catch( Exception exc )
                {
                exc.printStackTrace();
                }
            didFirstStart = true;
            } // while
        System.out.println( "Exiting TomcatAppThread run() - Done" );
        }

    public static void main(String[] args) throws IOException {
        // parse arguments
    }
}
