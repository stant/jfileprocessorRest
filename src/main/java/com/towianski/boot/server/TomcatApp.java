package com.towianski.boot.server;

/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.towianski.boot.GlobalMemory;
import com.towianski.boot.GlobalMemoryServer;
import com.towianski.security.SecUtils;
import com.towianski.security.SecUtilsServer;
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.context.annotation.Bean;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;


//@DependsOn( { "customAuthenticationProvider", "securityConfig" } )
@DependsOn( { "authSecurityConfiguration" } )
@ComponentScan({ "com.towianski.controllers","com.towianski.httpsutils", "com.towianski.boot.server" })
@SpringBootApplication
@Profile("server")
public class TomcatApp {

    private static final MyLogger logger = MyLogger.getLogger( TomcatApp.class.getName() );

//    ServerUserFileRightsList serverUserFileRightsList = new ServerUserFileRightsList();
//    CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator();

//    @Bean
//    protected ServletContextListener listener() {
//        return new ServletContextListener() {
//
//            @Override
//            public void contextInitialized(ServletContextEvent sce) {
//                logger.info("ServletContext initialized");
//            }
//
//            @Override
//            public void contextDestroyed(ServletContextEvent sce) {
//                logger.info("ServletContext destroyed");
//            }
//
//        };
//    }

    @Bean
    public GlobalMemoryServer globalMemory() {
        logger.info( "GlobalMemoryServer() constructor" );
        return new GlobalMemoryServer();
    }

    @Bean
    public CustomPermissionEvaluator customPermissionEvaluatorNormBean() {
        logger.info( "CustomPermissionEvaluator() constructor" );
        CustomPermissionEvaluator customPermissionEvaluatorNormBean = new CustomPermissionEvaluator( serverUserFileRightsList() );
        return customPermissionEvaluatorNormBean;
    }

    @Bean
    public ServerUserFileRightsList serverUserFileRightsList() {
        logger.info( "ServerUserFileRights() constructor" );
        //return new ServerUserFileRightsListHolder();
        ServerUserFileRightsList serverUserFileRightsList = ServerUserFileRightsListHolder.readInServerUserFileRightsListFromFile();
        return serverUserFileRightsList;
        
//        return serverUserFileRightsList;
//        return ServerUserFileRightsList.readInServerUserFileRightsFromFileList();
//        ServerUserFileRightsList serverUserFileRightsList = new ServerUserFileRightsList();
//        return serverUserFileRightsList();
    }

    @Bean
    public SecUtilsServer secUtils() {
        logger.info( "SecUtilsServer() constructor" );
        return new SecUtilsServer();
    }


//    @Autowired
//    private CustomAuthenticationProvider customAuthenticationProvider;
    
    //ServerUserFileRights serverUserFileRights = null;

        // This will redirect http to https - skipping http
//@Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(redirectConnector());
//        return tomcat;
//    }
//
//    private Connector redirectConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        connector.setPort(8080);
//        connector.setSecure(false);
//        connector.setRedirectPort(8443);
//        return connector;
//    }
    
//    @Bean
//    public UserDetailsService userDetailsService() throws Exception {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("").password("")
//            .roles("USER").build());
//        return manager;
//    }

//        @Configuration
//        public class SecurityConfig 
//                        extends WebSecurityConfigurerAdapter {
//
//                @Override
//                protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
//                        auth.inMemoryAuthentication().withUser("").password("").roles("ACTUATOR");
//                }
//        }
    
    
//        @Configuration
//        public class SecurityConfig  extends WebSecurityConfigurerAdapter {
//
////            @Override
////            protected void configure(HttpSecurity http) throws Exception {
////                http.csrf().disable().authorizeRequests().anyRequest().permitAll();
// 
//            @Autowired
//            @Override
//            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//                auth.authenticationProvider( customAuthenticationProvider );  // new CustomAuthenticationProvider() ); //authProvider );
//            }    
//            
//            @Override
//            protected void configure( HttpSecurity http ) throws Exception {
//                new CustomAuthenticationProvider();
//                http.csrf().disable().authorizeRequests().antMatchers( "/jfp/sys/**", JfpRestURIConstants.SEARCH).permitAll()
//                        .anyRequest().authenticated();
//                
////                http.httpBasic().and().authorizeRequests().antMatchers( "/jfp/sys/**", JfpRestURIConstants.SEARCH).permitAll()
////                        .anyRequest().permitAll();
//
////                http.csrf().disable().authorizeRequests().anyRequest().permitAll();
//            }
//        }
        
//    	final static String KEYSTORE_PASSWORD = "s3cr3t";

	static
            {
            logger.info( "TomcatApp Static block" );

            disableSslVerification();
                
////		System.setProperty("javax.net.ssl.trustStore", "/home/stan/sslkeystore");
////		System.setProperty("javax.net.ssl.trustStorePassword", "jfp2018");
//		System.setProperty("javax.net.ssl.keyStore",  "/home/stan/sslkeystore");
//		System.setProperty("javax.net.ssl.keyStorePassword", "jfp2018");

            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                    new javax.net.ssl.HostnameVerifier() 
                        {
                        public boolean verify(String hostname,
                                        javax.net.ssl.SSLSession sslSession) {
                                logger.info( "TomcatApp setDefaultHostnameVerifier. verify()" );
//						if (hostname.equals("localhost")) {
                                        return true;
//						}
//						return false;
                        }
                });
	}

        
    private static void disableSslVerification() {
        try{
                logger.info( "TomcatApp Static disableSslVerification" );
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
                logger.info( "TomcatApp Static disableSslVerification - Done" );
    }

//    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
//    {
//      public boolean verify(String s, SSLSession sslSession)
//      {
//        return true;
//      }
//    }

//    @Configuration
//    public class SecurityConfig {
//            @Bean
//            public UserDetailsService userDetailsService() {
//                    return (String username) -> {
//                            if("poef".equals(username))
//                                    return new User("poef", "fump", Collections.EMPTY_LIST);
//                            else
//                                    throw new UsernameNotFoundException("n/a");
//                    };
//            };	
//    }

    
    public static void main(String[] args) {
        logger.info( "*** before SpringApplication.run(TomcatApp.class, args)" );
        SpringApplication app;
        final ConfigurableApplicationContext context;
        String loggingFile = "";
        String loggingLevel = null;
        
        for ( int i = 0; i < args.length; i++ )
            {
            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            if ( args[i].toLowerCase().startsWith( "--logging.file" ) )
                {
                loggingFile = args[i].substring( "--logging.file=".length() );
                }
            else if ( args[i].toLowerCase().startsWith( "--loglevel" ) )
                {
                loggingLevel = args[i].substring( "--loglevel=".length() );
                }
            }
//        logger.info( "** -Dwhatever =" + System.getProperty("whatever") + "=" );
//        logger.info( "** -Dmyarg2 =" + System.getProperty("myarg2") + "=" );

//        SpringApplication.run(TomcatApp.class, args);
        context = new SpringApplicationBuilder(TomcatApp.class).profiles("server").run(args);
        
        //globalMemoryServer().setServerUserFileRightsList( serverUserFileRightsList() );
        //globalMemoryServer().setcustomPermissionEvaluatorNormBean( customPermissionEvaluatorNormBean );
        GlobalMemoryServer.setCustomPermissionEvaluatorNormBean( (context.getBean( CustomPermissionEvaluator.class )) );
        GlobalMemoryServer.setServerUserFileRightsList( (context.getBean( ServerUserFileRightsList.class )) );
        GlobalMemory.setSecUtils( (context.getBean( SecUtils.class )) );

        stdOutFilePropertyChange( loggingFile );
        stdErrFilePropertyChange( loggingFile );
 
        if ( loggingLevel != null )
            logger.setAllLoggerLevels( loggingLevel );  // for debugging
        
        logger.info( "serverUserFileRightsList Address = " + System.identityHashCode( context.getBean( ServerUserFileRightsList.class ) ) );

        //(context.getBean( ServerUserFileRightsList.class )).readInServerUserFileRightsFromFileList();
        
        String[] beanNames = context.getBeanDefinitionNames();

        logger.info( "\n---- top of List Of TomcatApp Spring Beans ----");
        for (String beanName : beanNames) {
            logger.info( "bean:" + beanName + " : " + context.getBean(beanName).getClass().toString());
        }
        logger.info( "---- End of List Of TomcatApp Spring Beans ----\n");

        logger.info( "*** after SpringApplication.run(TomcatApp.class, args)" );
        
        logger.info( "server using port =" + context.getEnvironment().getProperty("local.server.port") + "=" );

        /*
        JfpServerPanel serverPanel = new JfpServerPanel();
        //serverPanel.setVisible(true);
        
        // Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new JFileFinderWin().setVisible(true);
                serverPanel.setVisible(true);
            }
        });
          */
                  
//        final JFileFinderWin jffw = new JFileFinderWin();
//        if ( args.length > 0 )
//            {
//            jffw.setStartingFolder( args[0] );
//            jffw.searchBtnAction( null );
//            }
//        jffw.setVisible(true);
        
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
////                new JFileFinderWin().setVisible(true);
//                jffw.setVisible(true);
//            }
//        });
    }

    private static void stdOutFilePropertyChange( String stdOutFile )
    {
        try {
//            logger.info( "stdOutFilePropertyChange() do nothing for now !" );
//            if ( 1 == 1 ) return;
            
            logger.info( "stdOutFilePropertyChange() set to file =" + stdOutFile + "=" );
            if ( stdOutFile != null && ! stdOutFile.equals( "" ) )
                {
                System.setOut(new PrintStream(new File( stdOutFile ) ) );
                }
//            else
//                {
//                    //  should go to stderr but oh well for now
//                System.setOut( console );
//                }
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            }
    }                                         

    private static void stdErrFilePropertyChange( String stdErrFile )
    {
        try {
//            logger.info( "stdErrFilePropertyChange() do nothing for now !" );
//            if ( 1 == 1 ) return;
            
            logger.info( "stdErrFilePropertyChange() set to file =" + stdErrFile + "=" );
            if ( stdErrFile != null && ! stdErrFile.equals( "" ) )
                {
                System.setErr(new PrintStream(new File( stdErrFile ) ) );
                }
//            else
//                {
//                    //  should go to stderr but oh well for now
//                System.setErr( console );
//                }
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            }
    }                                         
    
}
