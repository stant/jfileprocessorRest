/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

/**
 *
 * @author stan
 */
import com.towianski.utils.MyLogger;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// java -Dspring.profiles.active=server -jar JFileProcessor-1.8.2.war --warserver --warfile=/home/stan/Downloads/sample.war --path=/what --port=8074 --warserver

@ComponentScan({ "com.towianski.controllers" })
@SpringBootApplication
@Profile("warserver")
public class WarRun extends SpringBootServletInitializer {

    private static final MyLogger logger = MyLogger.getLogger( WarRun.class.getName() );

    static String warfile = "";
    static String contextPath = "";
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WarRun.class).web(  WebApplicationType.SERVLET );
    }

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(WarRun.class);
        logger.info( "Entered WarRun.main");

        String loggingFile = "";
        Properties properties = new Properties();

        for ( int i = 0; i < args.length; i++ )
            {
//            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            if ( args[i].toLowerCase().startsWith( "--warfile" ) )
                {
                warfile = args[i].substring( "--warfile=".length() );
                logger.info( "got warfile =" + warfile + "=" );
                }
            else if ( args[i].toLowerCase().startsWith( "-D" ) )
                {
                String[] tmp = args[i].substring( 2 ).split( "=" );
                System.setProperty( tmp[0], tmp[1] );
                logger.info( "got property =" + tmp[0] + "=" + tmp[1] + "=" );
                }
            else if ( args[i].toLowerCase().startsWith( "--path" ) )
                {
//                System.setProperty("server.servlet.context-path", args[i].substring( "--path=".length() ) );   //    <--- Will set embedded Spring Boot Tomcat
                contextPath = args[i].substring( "--path=".length() );
                logger.info( "got contextPath =" + contextPath + "=" );
                }
            else if ( args[i].toLowerCase().startsWith( "--port" ) )
                {
                properties.setProperty( "server.port", args[i].substring( "--port=".length() ) );
//                dir = args[i].substring( "--port=".length() );
                logger.info( "got server.port =" + args[i].substring( "--port=".length() ) + "=" );
                }
            else if ( args[i].toLowerCase().startsWith( "--logging.file" ) )
                {
                loggingFile = args[i].substring( "--logging.file=".length() );
                stdOutFilePropertyChange( loggingFile );
                stdErrFilePropertyChange( loggingFile );
                }
            }

//        properties.setProperty( "spring.resources.static-locations",
//                               "classpath:/home/stan/Downloads" );
//        properties.setProperty( "server.port", "8070" );
//        System.setProperty("server.servlet.context-path", "/prop");     <--- Will set embedded Spring Boot Tomcat
//        properties.setProperty( "spring.security.user.name", "stan" );
//        properties.setProperty( "spring.security.user.password", "stan" );
        logger.info( "Entered WarRun.main after set properties");
        app.setDefaultProperties(properties);
        logger.info( "Entered WarRun.main after call set props. before app.run");
        
        app.run(args);
        logger.info( "Entered WarRun.main after app.run()");
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        return new TomcatServletWebServerFactory() {
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                logger.info( "tomcat.getServer().getCatalinaBase() =" + tomcat.getServer().getCatalinaBase() + "=" );
                new File(tomcat.getServer().getCatalinaBase(), "/webapps").mkdirs();
    //            try {
    //                Files.copy( (new File( "/home/stan/Downloads/sample.war" ) ).toPath(), (new File( tomcat.getServer().getCatalinaBase() +"/webapp/sample.war") ).toPath());
    //            } catch (IOException ex) {
    //                logger.severeExc( ex );
    //            }
                try {
                    logger.info( "Entered ServletWebServerFactory servletContainer()");
                    Context context2 = tomcat.addWebapp( contextPath, new ClassPathResource("file:" + warfile).getFile().toString() );

                    context2.setParentClassLoader(getClass().getClassLoader());

    //                WebappLoader loader2 = new WebappLoader(Thread.currentThread().getContextClassLoader());
    //                WebappLoader loader3 = new WebappLoader(Thread.currentThread().getContextClassLoader());
    //                context2.setLoader(loader2);
    //                context3.setLoader(loader3);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return super.getTomcatWebServer(tomcat);
            }
        };
    }

//@Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
////                new File(tomcat.getServer().getCatalinaBase(), "webapp").mkdirs();
//                new File(tomcat.getServer().getCatalinaBase(), "/webapps").mkdirs();
////                try {
////                    Files.copy( (new File( "/home/stan/Downloads/sample.war" ) ).toPath(), (new File( tomcat.getServer().getCatalinaBase() +"/webapps/sample.war") ).toPath());
////                } catch (IOException ex) {
////                    logger.severeExc( ex );
////                }
//                try {
////                    tomcat.addWebapp("/ok", new ClassPathResource( "webapps/sample.war" ).getFile().toString());
////                    Context context = tomcat.addWebapp(tomcat.getHost(), "/hello", new File( "/home/stan/Downloads/sample.war" ).getAbsolutePath());
//                    tomcat.addWebapp(tomcat.getHost(), "/hello", new File( "/home/stan/Downloads/SampleWebApp.war" ).getAbsolutePath());
////                    WebappLoader loader = new WebappLoader(Thread.currentThread().getContextClassLoader());
////                    context.setLoader(loader);
//                } catch (Exception ex) {
//                    throw new IllegalStateException("Failed to add okm", ex);
//                }
//                return super.getTomcatWebServer(tomcat);
//            }
//    };
//        return tomcat;
//}

            
//@Bean
//public ServletWebServerFactory servletContainer() {
//    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//        @Override
//        protected void postProcessContext(Context context) {
//                tomcat.addWebapp("/foo", "/path/to/foo.war");
//                // Allow the webapp to load classes from your fat jar
//                context.setParentClassLoader(getClass().getClassLoader());
//
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//            securityConstraint.setUserConstraint("CONFIDENTIAL");
//            SecurityCollection collection = new SecurityCollection();
//            collection.addPattern("/*");
//            securityConstraint.addCollection(collection);
//            context.addConstraint(securityConstraint);
//        }
//    };
////    tomcat.addAdditionalTomcatConnectors(redirectConnector());
//    return tomcat;
//}
//
//private Connector redirectConnector() {
//    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//    connector.setScheme("http");
//    connector.setPort(8080);
//    connector.setSecure(false);
//    connector.setRedirectPort(8443);
//    return connector;
//}

//@Bean
//public ServletWebServerFactory servletContainer() {
//	TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//	tomcat.addAdditionalTomcatConnectors(createSslConnector());
//	return tomcat;
//}
//
//private Connector createSslConnector() {
//	Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//	Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//	try {
//		File keystore = new ClassPathResource("keystore").getFile();
//		File truststore = new ClassPathResource("keystore").getFile();
//		connector.setScheme("https");
//		connector.setSecure(true);
//		connector.setPort(8443);
//                connector.
//		protocol.setSSLEnabled(true);
//		protocol.setKeystoreFile(keystore.getAbsolutePath());
//		protocol.setKeystorePass("changeit");
//		protocol.setTruststoreFile(truststore.getAbsolutePath());
//		protocol.setTruststorePass("changeit");
//		protocol.setKeyAlias("apitester");
//		return connector;
//	}
//	catch (IOException ex) {
//		throw new IllegalStateException("can't access keystore: [" + "keystore"
//				+ "] or truststore: [" + "keystore" + "]", ex);
//	}
//}
    
        @Configuration
        public class SecurityConfig  extends WebSecurityConfigurerAdapter {

            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.csrf().disable().authorizeRequests().anyRequest().permitAll();
            }
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

        
/*
@Configuration
public class ServletConfig {
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            container.setPort(1089);
        });
    }
}
*/
        
}