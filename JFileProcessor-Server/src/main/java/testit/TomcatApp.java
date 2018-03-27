package testit;

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

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import testit.model.ProgramMemory;
import testit.utils.Rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;


@Configuration
@ComponentScan       // This class must be a package level above the controllers !
@SpringBootApplication
public class TomcatApp {

    private static Log logger = LogFactory.getLog(TomcatApp.class);

    static public Rest myRest = new Rest();
    static ProgramMemory programMemory = null;

    public static void setProgramMemory(ProgramMemory programMemory) {
        TomcatApp.programMemory = programMemory;
    }

    static public ProgramMemory getProgramMemory() {
        return programMemory;
    }


    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                logger.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                logger.info("ServletContext destroyed");
            }

        };
    }
	
@Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
    
//    @Bean
//    public UserDetailsService userDetailsService() throws Exception {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("stan").password("peace")
//            .roles("USER").build());
//        return manager;
//    }

//        @Configuration
//        public class SecurityConfig 
//                        extends WebSecurityConfigurerAdapter {
//
//                @Override
//                protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
//                        auth.inMemoryAuthentication().withUser("stan").password("peace").roles("ACTUATOR");
//                }
//        }
    
    
        @Configuration
        public class SecurityConfig  extends WebSecurityConfigurerAdapter {

            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.csrf().disable().authorizeRequests().anyRequest().permitAll();
            }
        }
        
//    	final static String KEYSTORE_PASSWORD = "s3cr3t";

	static
	{
                System.out.println( "TomcatApp Static block" );

                disableSslVerification();
                
//		System.setProperty("javax.net.ssl.trustStore", "/home/stan/sslkeystore");
//		System.setProperty("javax.net.ssl.trustStorePassword", "jfp2018");
		System.setProperty("javax.net.ssl.keyStore",  "/home/stan/sslkeystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "jfp2018");

		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier() {

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
                                                System.out.println( "TomcatApp setDefaultHostnameVerifier. verify()" );
//						if (hostname.equals("localhost")) {
							return true;
//						}
//						return false;
					}
				});
	}

        
    private static void disableSslVerification() {
        try{
                System.out.println( "TomcatApp Static disableSslVerification" );
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
                System.out.println( "TomcatApp Static disableSslVerification - Done" );
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
        for ( int i = 0; i < args.length; i++ )
            {
            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            }
        logger.info( "** -Dwhatever =" + System.getProperty("whatever") + "=" );
        logger.info( "** -Dmyarg2 =" + System.getProperty("myarg2") + "=" );
        
        SpringApplication.run(TomcatApp.class, args);
        logger.info( "*** after SpringApplication.run(TomcatApp.class, args)" );

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

}
