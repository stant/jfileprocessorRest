package com.towianski.boot;

import com.towianski.boot.server.TomcatApp;
import com.towianski.boot.server.WarRun;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.utils.MyLogger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

//@ Configuration
//@ComponentScan       // This class must be a package level above the controllers !
//@SpringBootApplication
public class StartApp //implements CommandLineRunner
{
    private final static MyLogger logger = MyLogger.getLogger( StartApp.class.getName() );
    
//    @Autowired
//     JFileFinderWin jFileFinderWin;
//    static boolean adjustScale = false;
    
    public static void main(String[] args) 
        {
        SpringApplication app;
        final ConfigurableApplicationContext context;
        boolean startServer = false;
        boolean startWebServer = false;
        boolean startWarServer = false;
        String loggingFile = "";
        
        // java - get screen size using the Toolkit class
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        if ( screenSize.getWidth() > 1920.0 )
//            {
//            //System.setProperty( "sun.java2d.uiScale", "2" );
//            adjustScale = true;
//            System.out.println( "Looks like a hi resolution screen so set uiScale = 2, which can still be overriden with passed arg." );
//            }
        
        for ( int i = 0; i < args.length; i++ )
            {
//            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            System.out.println( "** args [" + i + "] =" + args[i] + "=" );
            if ( args[i].equalsIgnoreCase( "--server" ) )
                {
                startServer = true;
                }
            else if ( args[i].equalsIgnoreCase( "--webserver" ) )
                {
                startWebServer = true;
                }
            else if ( args[i].equalsIgnoreCase( "--warserver" ) )
                {
                startWarServer = true;
                }
            }
        
        if ( startServer )
            {
            System.out.println( "*** START SERVER ***" );
            TomcatApp.main(args);
//            context = new SpringApplicationBuilder(TomcatApp.class).profiles("server").web(true).run(args);
//            app.setAdditionalProfiles("server");
//            args = (String[])ArrayUtils.add(args, "--spring.jpa.hibernate.ddl-auto=create");
//            context = app.run(args);
            } 
        else if ( startWebServer )
            {
            System.out.println( "*** START WEB SERVER ***" );
            }
        else if ( startWarServer )
            {
            System.out.println( "*** START WAR SERVER ***" );
            try {
                WarRun.main(args);
            } catch (Exception ex) {
                Logger.getLogger(StartApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        else
            {
            System.out.println( "*** START CLIENT ***" );
            
//            String[] allArgs = Arrays.copyOf( args, args.length + 2 );
//            String[] extraArgs = { "sun.java2d.uiScale", "2" };
//            if ( adjustScale )
//                {
//                System.arraycopy( extraArgs, 0, allArgs, args.length, extraArgs.length );
//                }
            
            JFileFinderWin.main( args );
            
//            context = new SpringApplicationBuilder( JFileFinderWin.class ) .profiles("client").web( WebApplicationType.NONE ).headless(false).run(args);
//
//            EventQueue.invokeLater(() -> {
//                JFileFinderWin jFileFinderWin = context.getBean(JFileFinderWin.class);
////                jFileFinderWin.setVisible(true);
//            });
//            EventQueue.invokeLater(new Runnable() 
//                {
//                public void run() {
//                    final JFileFinderWin jFileFinderWin = context.getBean(JFileFinderWin.class);
//                    jFileFinderWin.setVisible(true);
//                    }
//                });

//            setLookAndFeel();
//            context.close();
            }

//        app.setWebEnvironment(false);
//        app.setShowBanner(false);
//        app.addListeners(new ConfigurationLogger());

        // launch the app
//        ConfigurableApplicationContext context = app.run(args);

//        ProfileManager pm = new ProfileManager();
//        pm.getActiveProfiles();
        
        // finished so close the context
        }  

    @PreDestroy
    public void destroy() {
        System.out.println( "Inside destroy method - " );
        }
}


