/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.towianski.jfileprocess.actions.TomcatAppThread;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.utils.Rest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class RestServerSw {
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    ConnUserInfo connUserInfo = null;
    Thread runThread = null;
    Thread tomcatAppPostThread = null;
    TomcatAppThread tomcatAppThread = null;
    static long count = 0;
    Object LockObj = "";
    
    public RestServerSw( ConnUserInfo connUserInfo )
        {
        this.connUserInfo = connUserInfo;
        }

    public RestTemplate timeoutRestTemplate( RestTemplateBuilder restTemplateBuilder )
        {
        return restTemplateBuilder
                .setConnectTimeout(100)
                .setReadTimeout(100)
                .build();
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

    public void cancelRestServer() 
        {
        System.out.println( "enter RestServerSw.cancelRestServer()" );
        if ( tomcatAppThread != null )
            {
            System.out.println( "RestServerSw.cancelRestServer() thread not null to make rest /jfp/sys/stop call" );
            tomcatAppThread.cancelRestServer();
            try
                {
//                RestTemplate restTemplate = timeoutRestTemplate( new RestTemplateBuilder() );
                RestTemplate restTemplate = Rest.createNoHostVerifyShortTimeoutRestTemplate();
                restTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_STOP, String.class );
                } 
            catch (Exception ex)
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println( "RestServerSw.cancelRestServer() - after rest stop call" );
            try
                {
                runThread.join();
                System.out.println( "RestServerSw.cancelRestServer() - after runThread.join()" );
                } 
            catch (InterruptedException ex)
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        if ( tomcatAppPostThread != null )
            {
            System.out.println( "RestServerSw.cancelRestServer() - before tomcatAppPostThread.join()" );
            if ( tomcatAppPostThread.isAlive() )
                {
                try
                    {
                    tomcatAppPostThread.join();
                    } 
                catch (InterruptedException ex)
                    {
                    Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            System.out.println( "RestServerSw.cancelRestServer() - after tomcatAppPostThread.join()" );
            }
        System.out.println( "exit RestServerSw.cancelRestServer()" );
        }

    public void actionPerformed(java.awt.event.ActionEvent evt) {                                         
//        if ( jFileFinderWin.searchBtn.getText().equalsIgnoreCase( Constants.PROCESS_STATUS_SEARCH_CANCELED ) )
        if ( 1 ==2  && ! connUserInfo.isConnectedFlag() )
            {
            this.cancelRestServer();
            }
        else
            {
            try {
                System.out.println( "RestServerSw doCmdBtnActionPerformed start" );
                System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//                RestServerSwingWorker = new TomcatAppSwingWorker( jFileFinderWin, this, startingPath );
//                tomcatApp = RestServerSwingWorker.getTomcatApp();
//                RestServerSwingWorker.execute();   //doInBackground();

//                TomcatAppPost tomcatAppPost = new TomcatAppPost( jFileFinderWin, LockObj );
//                tomcatAppPostThread = newThread( tomcatAppPost );
//                tomcatAppPostThread.setName( "watchdirPostThread=" + count );
//                tomcatAppPostThread.start();

                tomcatAppThread = new TomcatAppThread( connUserInfo.getToUser(), connUserInfo.getToPassword(), connUserInfo.getToHost() );
                runThread = newThread( tomcatAppThread );
                runThread.setName( "tomcatAppThread=" + count++ );
                runThread.start();
                waitUntilStarted();
                System.out.println( "RestServerSw after start watch thread, now exit actionPerformed" );
                } 
            catch (Exception ex) 
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }

    public void waitUntilStarted()
        {
        System.out.println( "entered RestServerSw waitUntilStarted()" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

//        RestTemplate restTemplate = new RestTemplate();

//        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
//        HttpComponentsClientHttpRequestFactory requestFactory =
//                new HttpComponentsClientHttpRequestFactory();
//
//        requestFactory.setHttpClient(httpClient);
//        RestTemplate noHostVerifyRestTemplate = new RestTemplate( requestFactory );

        RestTemplate noHostVerifyRestTemplate = null;
        try
            {
            System.out.println( "pause 12 seconds" );
            Thread.sleep( 12000 );
            noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
            } 
        catch (InterruptedException ex)
            {
            Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        boolean isRunning = false;
        int response = -9;
        int waitCount = 15;
        
        while ( response  <0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
            {
            System.out.println( "RestServerSw.run() make rest " + JfpRestURIConstants.SYS_GET_FILESYS + " call" );
            try
                {
                response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.SYS_GET_FILESYS, Integer.class );
                System.out.println( "RestServerSw.run() SYS_GET_FILESYS response =" + response );
                connUserInfo.setToFilesysType(response);
                System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
                }
            catch( Exception exc )
                {
                System.out.println( "RestServerSw.run() get filesys rest threw Exception !!" );
                exc.printStackTrace();
                response = -9;
                }
            System.out.println( "waitUntilStarted response =" + response + "=" );
            try
                {
                if ( response <0 || ! (response == Constants.FILESYSTEM_DOS || response == Constants.FILESYSTEM_POSIX) )
                    {
                    Thread.sleep( 4000 );
                    }
                } 
            catch (InterruptedException ex)
                {
                Logger.getLogger(RestServerSw.class.getName()).log(Level.SEVERE, null, ex);
                }

            if ( --waitCount < 1 )  break;
            }
        
        }
                
    public Thread newThread(final Runnable r) 
        {
        Thread thread = new Thread( r );
        thread.setName( "tomcatApp" + thread.getName());
        thread.setDaemon(true);
        return thread;
        }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TomcatAppSwFrame().setVisible(true);
//            }
//        });
    }
}
