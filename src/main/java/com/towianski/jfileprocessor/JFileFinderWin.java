/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.towianski.boot.JFileProcessorVersion;
import com.towianski.models.Constants;
import com.towianski.chainfilters.ChainFilterOfBoolean;
import com.towianski.models.ResultsData;
import com.towianski.renderers.NumberRenderer;
import com.towianski.renderers.FormatRenderer;
import com.towianski.models.CyclingSpinnerListModel;
import com.towianski.models.FilesTblModel;
import com.towianski.listeners.MyMouseAdapter;
import com.towianski.chainfilters.ChainFilterOfNames;
import com.towianski.chainfilters.ChainFilterOfMaxDepth;
import com.towianski.chainfilters.ChainFilterOfSizes;
import com.towianski.chainfilters.ChainFilterOfDates;
import com.towianski.chainfilters.ChainFilterOfMaxFileCount;
import com.towianski.chainfilters.ChainFilterOfMaxFolderCount;
import com.towianski.chainfilters.ChainFilterOfMinDepth;
import com.towianski.chainfilters.ChainFilterOfPreVisitMaxDepth;
import com.towianski.chainfilters.ChainFilterOfPreVisitMinDepth;
import com.towianski.chainfilters.ChainFilterOfShowHidden;
import com.towianski.chainfilters.FilterChain;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.jfileprocess.actions.BackwardFolderAction;
import com.towianski.jfileprocess.actions.CopyAction;
import com.towianski.jfileprocess.actions.CutAction;
import com.towianski.jfileprocess.actions.DeleteAction;
import com.towianski.jfileprocess.actions.EnterAction;
import com.towianski.jfileprocess.actions.ForwardFolderAction;
import com.towianski.jfileprocess.actions.MsgBoxFrame;
import com.towianski.jfileprocess.actions.PasteAction;
import com.towianski.jfileprocess.actions.RenameAction;
import com.towianski.jfileprocess.actions.UpFolderAction;
import com.towianski.jfileprocess.actions.ProcessInThread;
import com.towianski.jfileprocess.actions.NewFolderAction;
import com.towianski.jfileprocess.actions.ScriptOnSelectedFilesAction;
import com.towianski.jfileprocess.actions.TomcatAppThread;
import com.towianski.listeners.MyFocusAdapter;
import com.towianski.listeners.MyRowSorterListener;
import com.towianski.listeners.ScriptMenuItemListener;
import com.towianski.models.CircularArrayList;
import com.towianski.models.ConnUserInfo;
import static com.towianski.models.Constants.FILESYSTEM_DOS;
import static com.towianski.models.Constants.PROCESS_STATUS_CANCEL_FILL;
import static com.towianski.models.Constants.PROCESS_STATUS_CANCEL_SEARCH;
import static com.towianski.models.Constants.PROCESS_STATUS_ERROR;
import static com.towianski.models.Constants.PROCESS_STATUS_FILL_CANCELED;
import static com.towianski.models.Constants.PROCESS_STATUS_FILL_COMPLETED;
import static com.towianski.models.Constants.PROCESS_STATUS_FILL_STARTED;
import static com.towianski.models.Constants.PROCESS_STATUS_SEARCH_CANCELED;
import static com.towianski.models.Constants.PROCESS_STATUS_SEARCH_COMPLETED;
import static com.towianski.models.Constants.PROCESS_STATUS_SEARCH_STARTED;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_BOTH;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_FILES_ONLY;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_FOLDERS_ONLY;
import static com.towianski.models.Constants.SHOWFILESFOLDERSCB_NEITHER;
import com.towianski.models.FileAssoc;
import com.towianski.models.FileAssocList;
import com.towianski.models.JfpConstants;
import com.towianski.models.ProgramMemory;
import com.towianski.models.SearchModel;
import com.towianski.renderers.EnumFolderIconCellRenderer;
import com.towianski.renderers.EnumIconCellRenderer;
import com.towianski.renderers.PathRenderer;
import com.towianski.renderers.TableCellListener;
import com.towianski.sshutils.JschSftpUtils;
import static com.towianski.utils.ClipboardUtils.getClipboardStringsList;
import static com.towianski.utils.ClipboardUtils.setClipboardContents;
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.FileAssocWin;
import com.towianski.utils.FileUtils;
import com.towianski.utils.GithubClient;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import com.towianski.utils.VersionCk;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerListModel;
import javax.swing.SwingWorker;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Stan Towianski - June 2015
 */
@ComponentScan("com.towianski.boot.server")
@SpringBootApplication
@Profile("client")
public class JFileFinderWin extends javax.swing.JFrame {

    private final static MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );

    Thread jfinderThread = null;
    JFileFinderSwingWorker jFileFinderSwingWorker = null;
    WatchDirSw watchDirSw = null;
    RestServerSw restServerSw = null;        
    ResultsData resultsData = null;
    JFileFinder jfilefinder = null;
    Color saveColor = null;
    ArrayList<String> copyPaths = new ArrayList<String>();
    ArrayList<String> deletePaths = new ArrayList<String>();
    String copyPathStartPath = null;
    private TableCellListener filesTblCellListener = null;
    Boolean isDoingCutFlag = false;
    Boolean countOnlyFlag = false;
    SwingWorker afterFillSwingWorker = null;
    String JfpHomeDir = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" );
    String JfpHomeTempDir = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + "temp" + System.getProperty( "file.separator" );
    File scriptsFile = new File( JfpHomeDir + "menu-scripts" );
    File scriptsOsFile = null;
    String hostAddress = findHostAddress( "?" );

    int tblColModelSizeLast = -1;
    boolean alreadySetColumnSizes = false;
    List <RowSorter.SortKey> sortKeysSaved = null;
    
    CircularArrayList pathsHistoryList = new CircularArrayList(50 );
    SavedPathsPanel savedPathReplacablePanel = new SavedPathsPanel( this );
//    HashMap<String,String> savedPathsHm = new HashMap<String,String>();
    HashMap<String,ListOfFilesPanel> listOfFilesPanelHm = new HashMap<String,ListOfFilesPanel>();
    static final String LIST_OF_FILES_SELECTED = "--Selected Items--";
    String[] data = { LIST_OF_FILES_SELECTED };
    DefaultComboBoxModel listOfFilesPanelsModel = new DefaultComboBoxModel(data);
//    DefaultComboBoxModel listOfFilesPanelsModel = new DefaultComboBoxModel();

    ConnUserInfo connUserInfo = new ConnUserInfo();
    int filesysType = Constants.FILESYSTEM_POSIX;
    ProgramMemory programMemory = null;
    FileAssocList fileAssocList = new FileAssocList();
    PrintStream console = System.out;            
    
    JFileFinderWin jFileFinderWin = this;
        
    /**
     * Creates new form JFileFinder
     */
    public JFileFinderWin() {

        System.out.println("*** JFileFinderWin() Constructor ***" );
        initComponents();

        start();
    }

    public JFileFinderWin( String startPath ) {

        System.out.println("*** JFileFinderWin() Constructor with starting Path ***" );
        initComponents();
        startingFolder.setText( startPath );

        start();
        searchBtn.doClick();
    }

    public void checkForUpdate() 
        {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date now = Calendar.getInstance().getTime();
            System.out.println( "checkForUpdates() today =" + dateFormat.format( now ) + "=    last checked =" + programMemory.getCheckForUpdateDate() + "=" );
            if ( dateFormat.format( now ).equals( programMemory.getCheckForUpdateDate() ) )
                return;

            programMemory.setCheckForUpdateDate( dateFormat.format( now ) );
            String latest = GithubClient.findLatestReleaseVersion();
            if ( VersionCk.isVersionHigher( JFileProcessorVersion.getVersion(), latest ) )
                {
//                newerVersionLbl.setText( "  newer version <a href=\"\">(" + latest + ")</a></html> " );
                newerVersionLbl.setText( " newer version (" + latest + ") " );
                newerVersionLbl.setToolTipText( "github" );
//                newerVersionLbl.setMinimumSize( new Dimension( 100, 23 ) );
                newerVersionLbl.setPreferredSize( new Dimension( 140, 23 ) );
                newerVersionLbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                Font font = newerVersionLbl.getFont();
                Map attributes = font.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                newerVersionLbl.setFont(font.deriveFont(attributes));
                newerVersionLbl.addMouseListener(new MouseAdapter()  
                    {  
                        public void mouseClicked(MouseEvent e)  
                        {  
                        System.out.println( "newerVersionLblMouseClicked" );
                        Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse( new URI( "https://github.com/stant/jfileprocessorRest/releases" ) );
                            } catch (URISyntaxException ex) {
                                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }  
                    }); 
                newerVersionLbl.validate();

                newerVersionSpLbl.setText( " (" + latest + ") " );
                newerVersionSpLbl.setToolTipText( "Softpedia" );
//                newerVersionSpLbl.setMinimumSize( new Dimension( 100, 23 ) );
                newerVersionSpLbl.setPreferredSize( new Dimension( 140, 23 ) );
                newerVersionSpLbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                newerVersionSpLbl.setFont(font.deriveFont(attributes));
                newerVersionSpLbl.addMouseListener(new MouseAdapter()  
                    {  
                        public void mouseClicked(MouseEvent e)  
                        {  
                        System.out.println( "newerVersionSpLblMouseClicked" );
                        Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse( new URI( "https://www.softpedia.com/get/File-managers/JFileProcessor.shtml" ) );
                            } catch (URISyntaxException ex) {
                                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }  
                    }); 
                newerVersionSpLbl.validate();
                }
            //programMemory.extractCurrentValues();
            Rest.saveObjectToFile( "ProgramMemory.json", programMemory );
            }
        catch (Exception ex) {
            ex.printStackTrace();
            }
        }
        
private static void addJarToClasspath(File file) {
    try {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
        }
    catch (Exception ex) {
        ex.printStackTrace();
        }
}
    
    public void start() 
        {
//        System.out.println( "enter start()" );
//        System.err.println( "enter start()" );
        this.setTitle( JFileProcessorVersion.getName() + " " + JFileProcessorVersion.getVersion() + " - Stan Towianski  (c) 2015-2019" );
        System.out.println( "java.version =" + System.getProperty("java.version") + "=" );

        addJarToClasspath( new File( "JfpLib.jar" ) );
        System.out.println( "======= java classpath(s) ======>" );
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        }
        System.out.println( "=================================" );
        
        System.out.println( "JfpHomeTempDir Directory =" + JfpHomeTempDir + "=" );
        System.out.println( "Scripts Directory =" + scriptsFile + "=" );

        connUserInfo = new ConnUserInfo( false, Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "", getRmtAskHttpsPort() );
        connUserInfo.setTo( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "", getRmtAskHttpsPort() );
        calcFilesysType();  // also sets in connUserInfo
        
        jSplitPane2.setLeftComponent( savedPathReplacablePanel );

        date2.setMyEnabled( false );
        date2Op.setEnabled( false );
        jTabbedPane1.setSelectedIndex( 2 );

        fileMgrMode.setSelected( true );
        fileMgrModeActionPerformed( null );
        useGlobPattern.setSelected( true );
        tabsLogicAndBtn.setSelected( true );
        saveColor = searchBtn.getBackground();
        this.addEscapeListener( this );
        filesTbl.addMouseListener( new MyMouseAdapter( jPopupMenu1, this, jScrollPane1 ) );
        jScrollPane1.addMouseListener( new MyMouseAdapter( jPopupMenu2, this, jScrollPane1 ) );
       // jScrollPane1.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        this.setLocationRelativeTo( getRootPane() );

        filesTblCellListener = new TableCellListener( filesTbl, filesTblCellChangedAction );
//        filesTbl.putClientProperty( "terminateEditOnFocusLost", Boolean.TRUE );

        filesTbl.addFocusListener( new MyFocusAdapter( filesTblCellListener, this ) );
        
        addkeymapstuff();

        // TEMP TAKEN OUT FOR X11 Testing in method itself
        String tmp = System.getProperty("java.io.tmpdir");
        if ( tmp.endsWith( System.getProperty("file.separator") ) )
            tmp = tmp.substring( 0, tmp.length() - 1);
        stdOutFile.setText( tmp + System.getProperty("file.separator") + "jfp-out-" + MyLogger.getNewLogDate() + ".log" );
        stdErrFile.setText( tmp + System.getProperty("file.separator") + "jfp-err-" + MyLogger.getNewLogDate() + ".log" );
        stdOutFilePropertyChange( null );
        stdErrFilePropertyChange( null );
        stdErrFilePropertyChange( null );
        
        startingFolder.requestFocus();
        
        jSplitPane2.setDividerLocation( 170 );  // 150
        System.out.println( "at start jSplitPane1.getLastDividerLocation() =" + jSplitPane1.getLastDividerLocation() );
        programMemory = readInProgramMemoryFromFile();

        readInBookmarks();
        fileAssocList = readInFileAssocList();

        checkForUpdate();
        setupReportIssueLink();

        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            scriptsOsFile = new File( JfpHomeDir + "menu-scripts" + System.getProperty( "file.separator" ) + "mac" );
            }
        else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            scriptsOsFile = new File( JfpHomeDir + "menu-scripts" + System.getProperty( "file.separator" ) + "windows" );
            }
        else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "linux" ) )
            {
            scriptsOsFile = new File( JfpHomeDir + "menu-scripts" + System.getProperty( "file.separator" ) + "linux" );
            }
        System.out.println( "read scriptsFile/menu-scripts from  =" + scriptsFile + "=" );
        System.out.println( "read OS scriptsOsFile/menu-scripts from  =" + scriptsOsFile + "=" );

        (new File( JfpHomeTempDir )).mkdirs();

        setLookAndFeel();
        this.setVisible(true);
//        System.out.println( "leaving start()" );
//        System.err.println( "leaving start()" );
        }
    
    public static void setLookAndFeel()
        {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                System.out.println( "StartApp info.getName() =" + info.getName() );
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    System.out.println( "StartApp setLookAndFeel =" + info.getName() );
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        }
    
    public ProgramMemory readInProgramMemoryFromFile()
        {
        System.out.println( "readInProgramMemoryFromFile()" );
        try {
            programMemory = (ProgramMemory) Rest.readObjectFromFile( "ProgramMemory.json", new TypeReference<ProgramMemory>(){} );
            if ( programMemory == null )
                {
                System.out.println( "readInProgramMemoryFromFile() Error reading json file" );
                programMemory = new ProgramMemory();
                programMemory.setJFileFinderWin( this );
                }
            else
                {
                System.out.println( "readInProgramMemoryFromFile() read in json ok" );
                programMemory.setJFileFinderWin( this );
                programMemory.infuseSavedValues();
                }
    //        if ( jFileFinderWin == null )
    //            return;
            return programMemory;
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        return new ProgramMemory();
        }
 
    public String findHostAddress( String host )
        {
        if ( host == null || host.equals( "?" ) || host.equals( "" ) )
            {
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getLocalHost();
                System.out.println("IP Address:- " + inetAddress.getHostAddress());
                System.out.println("Host Name:- " + inetAddress.getHostName());
                host = inetAddress.getHostAddress();
                }
            catch (UnknownHostException ex) {
                Logger.getLogger(ConnUserInfo.class.getName()).log(Level.SEVERE, null, ex);
                host = "?";
                }
            }
        return host;
        }
    
    public void addkeymapstuff()
    {
       EnterAction enterAction = new EnterAction( this );
       RenameAction renameAction = new RenameAction( this );
       DeleteAction deleteAction = new DeleteAction( this );
       UpFolderAction upFolderAction = new UpFolderAction( this );
       BackwardFolderAction backwardFolderAction = new BackwardFolderAction( this );
       ForwardFolderAction forwardFolderAction = new ForwardFolderAction( this );
       NewFolderAction newFolderAction = new NewFolderAction( this );
       CopyAction copyAction = new CopyAction( this );
       CutAction cutAction = new CutAction( this );
       PasteAction pasteAction = new PasteAction( this );
       
//       InputEvent.CTRL_MASK   works on linux and windows but not Mac.
//       using Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() works on mac and linux.
               
        InputMap inputMap = null;
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            inputMap = filesTbl.getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW );
            }
        else
            {
            inputMap = filesTbl.getInputMap();
            }
            
        ActionMap actionMap = filesTbl.getActionMap();
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "enterAction" );
        actionMap.put( "enterAction", enterAction );
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ), "renameAction" );
        actionMap.put( "renameAction", renameAction );
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ), "deleteAction" );
        actionMap.put( "deleteAction", deleteAction );
 
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK ), "deleteAction2" );
        actionMap.put( "deleteAction2", deleteAction );
        
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_BACK_SPACE, 0 ), "upFolderAction" );
        actionMap.put( "upFolderAction", upFolderAction );
 
        
        try {
            File execOnSelected = new File( JfpHomeDir + "menu-scripts" + System.getProperty( "file.separator" ) + "runCommandOnSelectedFiles.groovy" );
            //System.out.println( "execOnSelected =" + execOnSelected + "=" );
            if ( Files.exists( Paths.get( execOnSelected.toString() ) ) )
                {
                //System.out.println( "ADD execOnSelected Action" );
                ScriptOnSelectedFilesAction scriptOnSelectedFilesAction = new ScriptOnSelectedFilesAction( this, execOnSelected.getAbsolutePath().toString() );
                inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_X, InputEvent.ALT_MASK ), "scriptOnSelectedFilesAction" );
                actionMap.put( "scriptOnSelectedFilesAction", scriptOnSelectedFilesAction );
                }
            } 
        catch( Exception ex )
            {
            ex.printStackTrace();
            }

                
        System.out.println( "System.getProperty( \"os.name\" ) =" + System.getProperty( "os.name" ) + "=" );
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
            {
            System.out.println( "Mac specific keys !" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "newFolderAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "copyAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "cutAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "pasteAction" );
            }
        else
            {
            System.out.println( "non Mac specific keys" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK ), "newFolderAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK ), "copyAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_X, InputEvent.CTRL_MASK ), "cutAction" );
            inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK ), "pasteAction" );
            }
        
        actionMap.put( "newFolderAction", newFolderAction );
 
        actionMap.put( "copyAction", copyAction );
 
        actionMap.put( "cutAction", cutAction );
 
        actionMap.put( "pasteAction", pasteAction );


        //------- Add actions to Starting Folder to go thru history  -------
        InputMap pathInputMap = startingFolder.getInputMap();
        ActionMap pathActionMap = startingFolder.getActionMap();
 
        pathInputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, InputEvent.ALT_MASK ), "backwardFolderAction" );
        pathActionMap.put( "backwardFolderAction", backwardFolderAction );
 
        pathInputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, InputEvent.ALT_MASK ), "forwardFolderAction" );
        pathActionMap.put( "forwardFolderAction", forwardFolderAction );
    }

    public DefaultComboBoxModel getListPanelModel( String listname ) {
        if ( listOfFilesPanelHm.containsKey( listname ) )
            {
            return listOfFilesPanelHm.get( listname ).getModel();
            }
        return null;
    }

    public ListOfFilesPanel getListPanel( String listname ) {
        if ( listOfFilesPanelHm.containsKey( listname ) )
            {
            return listOfFilesPanelHm.get( listname );
            }
        return null;
    }

    public void setListPanelModel( String listname, DefaultComboBoxModel model ) {
        if ( listOfFilesPanelHm.containsKey( listname ) )
            {
            listOfFilesPanelHm.get( listname ).setModel( model );
            }
    }

    public DefaultComboBoxModel getListOfFilesPanelsModel() {
        return listOfFilesPanelsModel;
    }

    public SwingWorker takeAfterFillSwingWorker() {
        SwingWorker tmp = afterFillSwingWorker;
        afterFillSwingWorker = null;
        return tmp;
    }

    public void setAfterFillSwingWorker(SwingWorker afterFillSwingWorker) {
        this.afterFillSwingWorker = afterFillSwingWorker;
    }
    
    public void readInBookmarks() {
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        listModel.clear();
        listModel.addElement( "New Window" );
        listModel.addElement( "Trash" );
        savedPathReplacablePanel.getSavedPathsHm().put( "New Window", "New Window" );
        savedPathReplacablePanel.getSavedPathsHm().put( "Trash", DesktopUtils.getTrashFolder().toString() );
        File selectedFile = new File( DesktopUtils.getBookmarks().toString() );
        System.out.println( "Bookmarks File =" + selectedFile + "=" );
            
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileReader fr = new FileReader( selectedFile.getAbsoluteFile() );
            BufferedReader br = new BufferedReader(fr);

//            DefaultComboBoxModel thisListModel = (DefaultComboBoxModel) savedPathsList.getModel();
            int numItems = listModel.getSize();
            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
            
            String line = "";
            while ( ( line = br.readLine() ) != null )
                {
                System.out.println( "read line =" + line + "=" );
                int at = line.indexOf( "," );
                listModel.addElement( line.substring( 0, at ) );
                savedPathReplacablePanel.getSavedPathsHm().put( line.substring( 0, at ), line.substring( at + 1 ) );
                }
            //close BufferedWriter
            br.close();
            //close FileWriter 
            fr.close();
            }
        catch( Exception ex )
            {

            }
    }

    public FileAssocList readInFileAssocList() {
        System.out.println( "readInFileAssocList()" );
        try {
            fileAssocList = (FileAssocList) Rest.readObjectFromFile( "FileAssocList.json", new TypeReference<FileAssocList>(){} );
            if ( fileAssocList == null )
                {
                System.out.println( "readInFileAssocFromFile() Error reading json file" );
                fileAssocList = new FileAssocList();
                fileAssocList.addFileAssoc( new FileAssoc( JfpConstants.ASSOC_TYPE_SUFFIX, 
                        "", JfpConstants.MATCH_TYPE_GLOB, "**.war", 
                        "java -Dspring.profiles.active=warserver -jar " + JFileProcessorVersion.getFileName() + 
                                " --warfile=%f --path=/%F --port=8070 --warserver --logging.file=" + DesktopUtils.getTmpDir() + "%F.log",
                        "url:https://localhost:8070/jfp/sys/stop" ) );
                Rest.saveObjectToFile( "FileAssocList.json", fileAssocList );
                }
            else
                {
                System.out.println( "readInFileAssocFromFile() read in json ok" );
                //fileAssoc.setJFileFinderWin( this );
//                fileAssoc.infuseSavedValues();
                }
    //        if ( jFileFinderWin == null )
    //            return;
            return fileAssocList;
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        return new FileAssocList();
    }

    public void saveBookmarks() {
        File selectedFile = new File( DesktopUtils.getBookmarks().toString() );
        System.out.println( "Bookmarks File =" + selectedFile + "=" );
        
        try
            {
            if ( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile() );
            BufferedWriter bw = new BufferedWriter(fw);

            //DefaultListModel thisListModel = (DefaultListModel) savedPathsList.getModel();
            DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
            int numItems = listModel.getSize();
            System.out.println( "thisListModel.getSize() num of items =" + numItems + "=" );
            System.out.println( "savedPathReplacablePanel.getSavedPathsHm().size() =" + savedPathReplacablePanel.getSavedPathsHm().size() + "=" );
            
            //loop for jtable rows
            for( int i = 0; i < numItems; i++ )
                {
                if ( ! listModel.getElementAt( i ).toString().equals( "New Window" ) && 
                     ! listModel.getElementAt( i ).toString().equals( "Trash" ) )
                    {
                    System.out.println( "bookmark saving =" + listModel.getElementAt( i ).toString() + "=" );
                    bw.write( listModel.getElementAt( i ).toString() + "," + savedPathReplacablePanel.getSavedPathsHm().get( listModel.getElementAt( i ).toString() ) );
                    bw.write( "\n" );
                    }
                }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
            }
        catch( Exception ex )
            {
            ex.printStackTrace();
            }
        }
    
    public void removeListPanel( String listname ) {
        listOfFilesPanelsModel.removeElement( listname );
        listOfFilesPanelHm.remove( listname );
    }

    public JRadioButton getUseGlobPattern() {
        return useGlobPattern;
    }

    public void setUseGlobPattern(JRadioButton useGlobPattern) {
        this.useGlobPattern = useGlobPattern;
    }

    public JRadioButton getUseRegexPattern() {
        return useRegexPattern;
    }

    public void setUseRegexPattern(JRadioButton useRegexPattern) {
        this.useRegexPattern = useRegexPattern;
    }

    public String getStartingFolder() {
        return startingFolder.getText().trim();
    }

    public void setStartingFolder(String startingFolder) {
        this.startingFolder.setText( startingFolder );
    }

    public boolean getShowGroupFlag() {
        return showGroupFlag.isSelected();
    }

    public boolean getShowJustFilenameFlag() {
        return showJustFilenameFlag.isSelected();
    }

    public void setShowJustFilenameFlag(boolean showJustFilenameFlag) {
        this.showJustFilenameFlag.setSelected( showJustFilenameFlag );
        alreadySetColumnSizes = false;
    }

    public void setShowGroupFlag(boolean showGroupFlag) {
        this.showGroupFlag.setSelected( showGroupFlag );
    }

    public boolean isShowOwnerFlag() {
        return showOwnerFlag.isSelected();
    }

    public void setShowOwnerFlag(boolean showOwnerFlag) {
        this.showOwnerFlag.setSelected(  showOwnerFlag );
    }

    public boolean isShowPermsFlag() {
        return showPermsFlag.isSelected();
    }

    public void setShowPermsFlag(boolean showPermsFlag) {
        this.showPermsFlag.setSelected( showPermsFlag );
    }

    public boolean getShowHiddenFilesFlag() {
        return showHiddenFilesFlag.isSelected();
    }

    public void setShowHiddenFilesFlag(boolean showHiddenFilesFlag) {
        this.showHiddenFilesFlag.setSelected( showHiddenFilesFlag );
    }

    public boolean getFileMgrMode() {
        return fileMgrMode.isSelected();
    }

    public void setFileMgrMode(boolean bb) {
        this.fileMgrMode.setSelected( bb );
    }

    public int getFilesysType() {
        return filesysType;
    }

    public void setFilesysType(int filesysType) {
        this.filesysType = filesysType;
        switch( filesysType )
            {
            case Constants.FILESYSTEM_DOS:
                fsTypeLbl.setText( "win" );
                break;
            case Constants.FILESYSTEM_POSIX:
                fsTypeLbl.setText( "posix" );
                break;
            default:
                fsTypeLbl.setText( "?" );
                break;
            }
    }

    public void calcFilesysType()
        {
        if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            {
            setFilesysType( FILESYSTEM_DOS );
            }
        else
            {
            setFilesysType( Constants.FILESYSTEM_POSIX );
            }
        connUserInfo.setFromFilesysType( filesysType );
        connUserInfo.setToFilesysType( filesysType );
        }

    public Level getLogLevel() {
        return logLevelsLhm.get( logLevel.getSelectedItem() );
    }

    public void callSearchBtnActionPerformed(java.awt.event.ActionEvent evt)
        {   // FIXXX  I can get rid of this method !
//        searchBtnAction( evt );
        searchBtn.doClick();
        }
        
    public void clickConnectAndSearch(java.awt.event.ActionEvent evt)
        {
        rmtConnectBtn.doClick();
        }
            
    public void callRmtConnectBtnActionPerformed(java.awt.event.ActionEvent evt)
        {
        rmtConnectBtnActionPerformed( evt );
        }
            
    public void callRmtDisconnectBtnActionPerformed(java.awt.event.ActionEvent evt)
        {
        rmtDisconnectBtnActionPerformed( evt );
        }
            
    public void callDeleteActionPerformed(java.awt.event.ActionEvent evt)
        {
        DeleteActionPerformed( evt );
        }
            
    public void callEnterActionPerformed(java.awt.event.ActionEvent evt)
        {
        EnterActionPerformed( evt );
        }
                        
    public void callRenameActionPerformed(java.awt.event.ActionEvent evt)
        {
        RenameActionPerformed( evt );
        }
                        
    public void callUpFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        upFolderActionPerformed( evt );
        }
                        
    public void callBackwardFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        backwardFolderActionPerformed( evt );
        }
                        
    public void callForwardFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        forwardFolderActionPerformed( evt );
        }
                        
    public void callNewFolderActionPerformed(java.awt.event.ActionEvent evt)
        {
        NewFolderActionPerformed( evt );  // note capital N for what netbeans created
        }
                        
    public void callCopyActionPerformed(java.awt.event.ActionEvent evt)
        {
        CopyActionPerformed( evt );
        }
                        
    public void callCutActionPerformed(java.awt.event.ActionEvent evt)
        {
        CutActionPerformed( evt );
        }

    public void callPasteActionPerformed(java.awt.event.ActionEvent evt)
        {
        PasteActionPerformed( evt );
        }

    public void callFileMgrModeActionPerformed(java.awt.event.ActionEvent evt)
        {
        fileMgrModeActionPerformed( evt );
        }

    public void stopSearch() {
        jfilefinder.cancelSearch();
        }

    public void stopFill() {
        jfilefinder.cancelFill();
        }

    public void setSearchBtn( String text, Color setColor )
        {
        searchBtn.setText( text );
        searchBtn.setBackground( setColor );
        searchBtn.setOpaque(true);
        }

//    public void resetSearchBtn() {
//        searchBtn.setText( "Search" );
//        searchBtn.setBackground( saveColor );
//        searchBtn.setOpaque(true);
//    }

    public void setResultsData( ResultsData resultsData )
        {
        this.resultsData = resultsData;
        }

    public void setNumFilesInTable()
        {
        NumberFormat numFormat = NumberFormat.getIntegerInstance();
        numFilesInTable.setText( numFormat.format( filesTbl.getModel().getRowCount() ) );
        }
    
    public void setProcessStatus( String text )
        {
        processStatus.setText(text);
        switch( text )
            {
            case PROCESS_STATUS_SEARCH_STARTED:  
                //processStatus.setBackground( Color.GREEN );
                //setSearchBtn( Constants.PROCESS_STATUS_CANCEL_SEARCH, Color.RED );
                setSearchBtn( Constants.PROCESS_STATUS_CANCEL_SEARCH, saveColor );
                setMessage( "" );
                break;
            case PROCESS_STATUS_FILL_STARTED:
                //processStatus.setBackground( Color.GREEN );
                //setSearchBtn( Constants.PROCESS_STATUS_CANCEL_FILL, Color.RED );
                setSearchBtn( Constants.PROCESS_STATUS_CANCEL_FILL, saveColor );
                break;
            case PROCESS_STATUS_SEARCH_CANCELED:
                //processStatus.setBackground( Color.YELLOW );
                setSearchBtn( Constants.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_SEARCH_COMPLETED:
                //processStatus.setBackground( saveColor );
                setSearchBtn( Constants.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_FILL_CANCELED:
                //processStatus.setBackground( Color.YELLOW );
                setSearchBtn( Constants.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_FILL_COMPLETED:
                //processStatus.setBackground( saveColor );
                setSearchBtn( Constants.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            case PROCESS_STATUS_ERROR:
                //processStatus.setBackground( Color.RED );
            System.out.println( "process status error !" );
                break;
            default:
                //processStatus.setBackground( saveColor );
                setSearchBtn( Constants.PROCESS_STATUS_SEARCH_READY, saveColor );
                break;
            }
        }

    public String getProcessStatus()
        {
        return processStatus.getText();
        }

    public String getMessage()
        {
        return message.getText();
        }

    public void setMessage( String text )
        {
        message.setText(text);
        }

    public ConnUserInfo getConnUserInfo() {
        return connUserInfo;
    }

    public void setConnUserInfo(ConnUserInfo connUserInfo) {
        this.connUserInfo = connUserInfo;
    }

    public String getRmtConnectBtn()
        {
        return rmtConnectBtn.getText().trim();
        }

    public void setRmtConnectBtn(String text)
        {
        this.rmtConnectBtn.setText(text);
        }

    public void setRmtConnectBtnBackground( Color color )
        {
        this.rmtConnectBtn.setBackground( color );
        }

    public void setRmtConnectBtnBackgroundReset()
        {
        this.rmtConnectBtn.setBackground( saveColor );
        }

    public String getRmtHost()
        {
        return rmtHost.getText().trim();
        }

    public void setRmtHost(String str)
        {
        this.rmtHost.setText(str);
        }

    public String getRmtPasswd()
        {
        return rmtPasswd.getText().trim();
        }

    public void setRmtPasswd(String str)
        {
        this.rmtPasswd.setText(str);
        }

    public String getRmtUser()
        {
        return rmtUser.getText().trim();
        }

    public void setRmtUser(String str)
        {
        this.rmtUser.setText(str);
        }

    public String getRmtSshPort()
        {
        return rmtSshPort.getText().trim();
        }

    public void setRmtSshPort(String str)
        {
        this.rmtSshPort.setText(str);
        }

    public String getRmtAskHttpsPort()
        {
        return rmtAskHttpsPort.getText();
        }

    public void setRmtAskHttpsPort(String rmtAskHttpsPort)
        {
        this.rmtAskHttpsPort.setText( rmtAskHttpsPort );
        }

    public void setRmtToUsingHttpsPortToolTip(String rmtToUsingHttpsPort)
        {
        this.rmtAskHttpsPort.setToolTipText( rmtToUsingHttpsPort );
        }

    public String getMyEditorCmd()
        {
        return myEditorCmd.getText().trim();
        }

    public void setMyEditorCmd(String myEditorCmd)
        {
        this.myEditorCmd.setText(myEditorCmd);
        }

    public String getStartConsoleCmd()
        {
        return startConsoleCmd.getText().trim();
        }

    public void setStartConsoleCmd(String startConsoleCmd)
        {
        this.startConsoleCmd.setText(startConsoleCmd);
        }

    public void setupReportIssueLink()
        {
        reportIssueLbl.setPreferredSize( new Dimension( 140, 23 ) );
        reportIssueLbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Font font = reportIssueLbl.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        reportIssueLbl.setFont(font.deriveFont(attributes));
        reportIssueLbl.addMouseListener(new MouseAdapter()  
            {  
                public void mouseClicked(MouseEvent e)  
                {  
                Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse( new URI( "https://github.com/stant/jfileprocessorRest/issues" ) );
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }  
            }); 
        }
    
    ActionListener menuActionListener = new ActionListener()
        {
        @Override
        public void actionPerformed(ActionEvent e) 
            {
            message.setText(e.getActionCommand());
            } 
        };
        
//            System.out.println( " am i running in EDT? =" + SwingUtilities.isEventDispatchThread() );
//            if (SwingUtilities.isEventDispatchThread())
//{
//    code.run();
//}
//else
//{
//    SwingUtilities.invokeLater(code);
//}

    Action filesTblCellChangedAction = new AbstractAction()
        {
        public void actionPerformed(ActionEvent e)
            {
            System.out.println( "doing filesTblCellChangedAction()" );
//            TableColumnModel tblColModel = filesTbl.getColumnModel();
//            System.out.println( "filesTblCellChangedAction() table col count 1=" + tblColModel.getColumnCount() );

            TableCellListener tcl = (TableCellListener)e.getSource();
            System.out.println("Row   : " + tcl.getRow());
            System.out.println("Column: " + tcl.getColumn());
            System.out.println("Old   : " + tcl.getOldValue());
            System.out.println("New   : " + tcl.getNewValue());
            String targetPathStr = tcl.getNewValue().toString().trim();
            Path targetPath = Paths.get( tcl.getNewValue().toString().trim() );
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            //if ( Files.exists( targetPath ) )
            if ( FileUtils.exists( connUserInfo, targetPathStr ) )
                {
                filesTblModel.setValueAt( tcl.getOldValue(), tcl.getRow(), tcl.getColumn() );
                JOptionPane.showMessageDialog( null, "That Folder name already exists!", "Error", JOptionPane.ERROR_MESSAGE );
                System.out.println( "That Folder name already exists! (" + targetPath + ")" );
                setProcessStatus( PROCESS_STATUS_ERROR );
                processStatus.setText( "Error" );
                filesTblModel.deleteRowAt( 0 );
                }
            else
                {
                try {
                    if ( tcl.getOldValue() == null )
                        {
                        System.out.println( "try to create dir target =" + targetPath + "=" );
                        //Files.createDirectory( targetPath );
                        //RenameActionPerformed( null );

                        if ( tcl.getFilesTblModelType() == FilesTblModel.FILESTBLMODEL_FILETYPE )
                            {
                            FileUtils.touch( connUserInfo, targetPathStr );
                            }
                        else
                            {
                            FileUtils.createDirectory( connUserInfo, targetPathStr );
                            }
                        }
                    else
                        {
                        String sourcePath = tcl.getOldValue().toString().trim();
                        FileUtils.fileMove( connUserInfo, sourcePath, targetPathStr );
//                        if ( Files.exists( sourcePath ) )
//                            {
//                            System.out.println( "try to move dir source =" + sourcePath + "=   target =" + targetPath + "=" );
//                            Files.move( sourcePath, targetPath );
//                            }
                        }
                    }
//                catch( AccessDeniedException ae )
//                    {
//                    setProcessStatus( PROCESS_STATUS_ERROR );
//                    processStatus.setText( "Error" );
//                    message.setText( "Access Denied Exception" );
//                    filesTblModel.deleteRowAt( 0 );
//                    }
                catch (Exception ex) 
                    {
                    System.out.println( "ex.getMessage() =" + ex.getMessage()+ "=" );
                    processStatus.setText( "Error" );
                    ex.printStackTrace();
                    message.setText( ex.getMessage() );
                    logger.log(Level.SEVERE, null, ex);
                    }
                }
            //filesTbl.setCellSelectionEnabled( false );
            setColumnSizes();
            }
        };

    public synchronized void stopDirWatcher()
        {
        if ( watchDirSw != null )
            {
            watchDirSw.cancelWatch();
            }
        }

    public synchronized void startDirWatcher()
        {
        if ( ! stopFileWatchTb.isSelected()  // if On/Auto
            && ( ! maxDepth.getText().trim().equals( "" ) )
            && ( maxDepth.getText().trim().equals( maxDepth.getText().trim() ) )   )  // don't watch on a searched list
            {
            if ( watchDirSw == null )
                {
                watchDirSw = new WatchDirSw( this, jFileFinderWin.pathsToNotWatch(), Paths.get( jFileFinderWin.getStartingFolder() ) );
                }
            watchDirSw.actionPerformed( jFileFinderWin.pathsToNotWatch(), Paths.get( jFileFinderWin.getStartingFolder() ) );
            }
        }

//    public boolean getSearchLock()
//        {
//        if ( searchLock.tryLock() )
//            {
//            // Got the lock
//            System.out.println( "got searchLock" );
//            return true;
////            try
////                {
////                System.out.println( "got searchLock" );
////                }
////            finally
////                {
////                // Make sure to unlock so that we don't cause a deadlock
////                searchLock.unlock();
////                }
//            }
//        else
//            {
//            // Someone else had the lock, abort
//            System.out.println( "could NOT get searchLock" );
//            }
//        return false;
//        }
//    
//    public void releaseSearchLock()
//        {
//        System.out.println( "JFileFinderSwingWorker.releaseSearchLock()" );
//        searchLock.unlock();
//        }
    
    public void searchBtnActionSwing( java.awt.event.ActionEvent evt )
    {
        System.out.println("jfilewin searchBtnActionSwing() searchBtn.getText() =" + searchBtn.getText() + "=" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        stopDirWatcher();

        if ( searchBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_SEARCH ) )
            {
            System.out.println( "hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
            this.stopSearch();
            //JOptionPane.showConfirmDialog( null, "at call stop search" );
            }
        else if ( searchBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_FILL ) )
            {
            System.out.println( "hit stop fill button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
            setProcessStatus( PROCESS_STATUS_FILL_CANCELED );
            this.stopFill();
            //JOptionPane.showConfirmDialog( null, "at call stop fill" );
            }
        else
            {
            //JOptionPane.showConfirmDialog( null, "at call do search" );
            try {
                String[] args = new String[3];
                args[0] = startingFolder.getText().trim();
                //int startingPathLength = (args[0].endsWith( System.getProperty( "file.separator" ) ) || args[0].endsWith( "/" ) ) ? args[0].length() - 1 : args[0].length();
                //args[0] = args[0].substring( 0, startingPathLength );
                if ( ! ( args[0].endsWith(System.getProperty( "file.separator" ) )
                         || args[0].endsWith( "/" ) ) ) 
                    {
                    args[0] += System.getProperty( "file.separator" );
                    }
                startingFolder.setText( args[0] );

                args[1] = useRegexPattern.isSelected() ? "-regex" : "-glob";
                args[2] = filePattern.getText().trim();
                
                if ( useGlobPattern.isSelected()
                        && ! ( args[0].endsWith(System.getProperty( "file.separator" ) )
                         || args[0].endsWith( "/" ) )
                        && ! args[2].startsWith( "**" )
                        && ! (args[2].startsWith( System.getProperty( "file.separator" ) ) || args[2].startsWith( "/" )) )
                    {
                    int result = JOptionPane.showConfirmDialog( null, 
                       "There is no file separator (/ or \\ or **) between starting folder and pattern. Do you want to insert one?"
                            ,null, JOptionPane.YES_NO_OPTION );
                    if ( result == JOptionPane.YES_OPTION )
                        {
                        filePattern.setText( System.getProperty( "file.separator" ) + args[2] );
                        args[2] = filePattern.getText();
                        }
                    }
                
                //------- save history of paths  -------
                pathsHistoryList.add( args[0] );
                System.out.println( "after pathsHistoryList()" );

                //public ChainFilterA( ChainFilterA nextChainFilter )
                //Long size1Long = Long.parseLong( size1.getText().trim() );
                System.out.println( "tabsLogic button.getText() =" + (tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText()) + "=" );
                FilterChain chainFilterList = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterFolderList = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterPreVisitFolderList = new FilterChain( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );

                try {
                    if ( ! filePattern.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of names!" );
                        ChainFilterOfNames chainFilterOfNames = new ChainFilterOfNames( args[1], (args[0] + args[2]).replace( "\\", "\\\\" ) );
                        if ( showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY )  ||
                             showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_BOTH ) )
                            {
                            chainFilterFolderList.addFilter( chainFilterOfNames );
                            }
                        if ( showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FILES_ONLY )  ||
                             showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_BOTH ) )
                            {
                            chainFilterList.addFilter( chainFilterOfNames );
                            }
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    System.out.println( "showFilesFoldersCb.getSelectedItem() =" + showFilesFoldersCb.getSelectedItem() + "=" );
                    if ( showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FILES_ONLY ) 
                         || showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        System.out.println( "add filter Boolean False for folders" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterFolderList.addFilter( chainFilterOfBoolean );
                        }
                    if ( showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY ) 
                         || showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        System.out.println( "add filter Boolean False for files" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterList.addFilter( chainFilterOfBoolean );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Boolean filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! size1.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of sizes!" );
                        ChainFilterOfSizes chainFilterOfSizes = new ChainFilterOfSizes( (String)size1Op.getSelectedItem(), size1.getText().trim(), ((String) sizeLogicOp.getValue()).trim(), (String)size2Op.getSelectedItem(), size2.getText().trim() );
                        chainFilterList.addFilter( chainFilterOfSizes );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Size filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( (Date) date1.getModel().getValue() != null )
                        {
                        System.out.println( "add filter of dates!" );
                        System.out.println( "selected date =" + (Date) date1.getModel().getValue() + "=" );
                        ChainFilterOfDates chainFilterOfDates = new ChainFilterOfDates( (String)date1Op.getSelectedItem(), (Date) date1.getModel().getValue(), ((String) dateLogicOp.getValue()).trim(), (String)date2Op.getSelectedItem(), (Date) date2.getModel().getValue() );
                        chainFilterList.addFilter( chainFilterOfDates );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Date filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! maxDepth.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of maxdepth!" );
                        System.out.println( "selected maxdepth =" + maxDepth.getText().trim() + "=" );
                        ChainFilterOfMaxDepth chainFilterOfMaxDepth = new ChainFilterOfMaxDepth( args[0], maxDepth.getText().trim() );
                        chainFilterFolderList.addFilter( chainFilterOfMaxDepth );
                        chainFilterList.addFilter( chainFilterOfMaxDepth );
                        ChainFilterOfPreVisitMaxDepth chainFilterOfPreVisitMaxDepth = new ChainFilterOfPreVisitMaxDepth( args[0], maxDepth.getText().trim() );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMaxDepth );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! stopFileCount.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of stopFileCount!" );
                        System.out.println( "selected stopFileCount =" + stopFileCount.getText().trim() + "=" );
                        ChainFilterOfMaxFileCount chainFilterOfMaxFileCount = new ChainFilterOfMaxFileCount( args[0], stopFileCount.getText().trim() );
                        chainFilterList.addFilter( chainFilterOfMaxFileCount );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in stopFileCount filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! stopFolderCount.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of stopFolderCount!" );
                        System.out.println( "selected stopFolderCount =" + stopFolderCount.getText().trim() + "=" );
                        ChainFilterOfMaxFolderCount chainFilterOfMaxFolderCount = new ChainFilterOfMaxFolderCount( args[0], stopFolderCount.getText().trim() );
                        chainFilterFolderList.addFilter( chainFilterOfMaxFolderCount );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in stopFolderCount filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                try {
                    if ( ! minDepth.getText().trim().equals( "" ) )
                        {
                        System.out.println( "add filter of minDepth!" );
                        System.out.println( "selected minDepth =" + minDepth.getText().trim() + "=" );
                        ChainFilterOfMinDepth chainFilterOfMinDepth = new ChainFilterOfMinDepth( args[0], minDepth.getText().trim() );
                        chainFilterFolderList.addFilter( chainFilterOfMinDepth );
                        chainFilterList.addFilter( chainFilterOfMinDepth );
                        ChainFilterOfPreVisitMinDepth chainFilterOfPreVisitMinDepth = new ChainFilterOfPreVisitMinDepth( args[0], minDepth.getText().trim() );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMinDepth );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }
                
                try {
                    if ( ! showHiddenFilesFlag.isSelected() )
                        {
                        System.out.println( "add filter of do not show hidden files!" );
                        ChainFilterOfShowHidden filter = new ChainFilterOfShowHidden( false );
                        chainFilterList.addFilter( filter );
                        chainFilterFolderList.addFilter( filter );
                        }
                    }
                catch( Exception ex )
                    {
                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return;
                    }

                // if it matters for speed I could pass countOnlyFlag to jFileFinder too and not create the arrayList of paths !
                jfilefinder = new JFileFinder( args[0], args[1], args[2], chainFilterList, chainFilterFolderList, chainFilterPreVisitFolderList );
                jFileFinderSwingWorker = new JFileFinderSwingWorker( this, jfilefinder, args[0], args[1], args[2], countOnlyFlag );
//                searchBtn.setText( "Stop" );
//                searchBtn.setBackground(Color.RED);
//                searchBtn.setOpaque(true);
//                searchBtn.setBorderPainted(false);
//                message.setText( "Search started . . ." );
                //setProcessStatus( PROCESS_STATUS_SEARCH_STARTED );
                System.out.println( "*************  jFileFinderSwingWorker.execute()  ****************" );
            
                jFileFinderSwingWorker.execute();   //doInBackground();
                //jfinderThread = new Thread( jfilefinder );
//                        jfinderThread.start();
//                        jfinderThread.join();
//                        searchBtn.setText( "Search" );
//                        searchBtn.setBackground( saveColor );
//                        searchBtn.setOpaque(true);
            } 
            catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    public void searchBtnAction( java.awt.event.ActionEvent evt )
        {
        System.out.println( "searchBtnAction() with connUserInfo =" + connUserInfo + "=" );
//        try
//        {
//            int x = 2/0;
//        }
//        catch( Exception ex)
//        {
//        System.out.println( "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^6" );
//        System.err.println( "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^6" );
//            ex.printStackTrace();
//            return;
//        }
        if ( connUserInfo.isConnectedFlag() )
            {
            searchBtnActionRest( evt );
            }
        else
            {
            searchBtnActionSwing( evt );
            }
        }

    public void searchBtnActionRest( java.awt.event.ActionEvent evt )
    {
        System.out.println("jfilewin searchBtnActionRest() searchBtn.getText() =" + searchBtn.getText() + "=" );
        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
        stopDirWatcher();

        SearchModel searchModel = extractSearchModel();
//        Rest.saveObjectToFile( "SearchModel.json", searchModel );
//        RestTemplate restTemplate = new RestTemplate();
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        //we can't get List<Employee> because JSON convertor doesn't know the type of
        //object in the list and hence convert it to default JSON object type LinkedHashMap
//        FilesTblModel filesTblModel = restTemplate.getForObject( SERVER_URI+JfpRestURIConstants.GET_FILES, FilesTblModel.class, SearchModel.class );

        System.out.println( "rest send searchModel =" + searchModel + "=" );

//        String response = restTemplate.postForEntity( "http://" + rmtHost.getText().trim() + ":8080" + JfpRestURIConstants.SEARCH, searchModel, String.class).getBody();
        String response = noHostVerifyRestTemplate.postForEntity( connUserInfo.getToUri() + JfpRestURIConstants.SEARCH, searchModel, String.class).getBody();
        System.out.println( "response =" + response + "=" );
        resultsData = Rest.jsonToObject( response, ResultsData.class );
        System.out.println( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );
        System.out.println( "resultsData.getFilesTblModel() =" + resultsData.getFilesTblModel().toString() );

        NumberFormat numFormat = NumberFormat.getIntegerInstance();
        String partialMsg = "";
        if ( resultsData.getSearchWasCanceled() )
            {
            jFileFinderWin.setProcessStatus( Constants.PROCESS_STATUS_SEARCH_CANCELED );
            partialMsg = " PARTIAL files list.";
            }
        else
            {
            jFileFinderWin.setProcessStatus( Constants.PROCESS_STATUS_SEARCH_COMPLETED );
            }
        jFileFinderWin.setMessage( "Matched " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) 
                + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.  Total "
                + numFormat.format( resultsData.getFilesVisited() ) + partialMsg );
        jFileFinderWin.setResultsData( resultsData );

        //jFileFinderWin.emptyFilesTable();

        if ( ! countOnlyFlag )
            {
            System.out.println( "call JFileFinderSwingWorker.fillTableModelSwingWorker.execute()" );
            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            //FillTableModelSwingWorker fillTableModelSwingWorker = new FillTableModelSwingWorker( jFileFinderWin, jfilefinder );
            //fillTableModelSwingWorker.execute();   //doInBackground();
            fillInFilesTable( resultsData.getFilesTblModel() );
            }

//        FilesTblModel filesTblModel = restTemplate.postForEntity( SERVER_URI+JfpRestURIConstants.SEARCH, searchModel, FilesTblModel.class).getBody();
//        System.out.println( "response filesTblModel =" + filesTblModel + "=" );
    }

    public SearchModel extractSearchModel()
        {
        System.out.println("jfilewin extractSearchModel() searchBtn.getText() =" + searchBtn.getText() + "=" );
        SearchModel searchModel = new SearchModel();

        searchModel.setStartingFolder( startingFolder.getText().trim() );
        
        searchModel.setPatternType( useRegexPattern.isSelected() ? "-regex" : "-glob" );
        searchModel.setFilePattern( filePattern.getText().trim() );
        
                String[] args = new String[3];
                args[0] = startingFolder.getText().trim();
                //int startingPathLength = (args[0].endsWith( System.getProperty( "file.separator" ) ) || args[0].endsWith( "/" ) ) ? args[0].length() - 1 : args[0].length();
                //args[0] = args[0].substring( 0, startingPathLength );
                if ( filesysType == Constants.FILESYSTEM_DOS &&
                        ! args[0].endsWith( "\\" ) )
                    {
                    args[0] += "\\";
                    }
                else if ( filesysType == Constants.FILESYSTEM_POSIX &&
                        ! args[0].endsWith( "/" ) )
                    {
                    args[0] += "/";
                    }
                startingFolder.setText( args[0] );

//                args[1] = useRegexPattern.isSelected() ? "-regex" : "-glob";
//                args[2] = filePattern.getText().trim();
//                
//                if ( useGlobPattern.isSelected()
//                        && ! ( args[0].endsWith(System.getProperty( "file.separator" ) )
//                         || args[0].endsWith( "/" ) )
//                        && ! args[2].startsWith( "**" )
//                        && ! (args[2].startsWith( System.getProperty( "file.separator" ) ) || args[2].startsWith( "/" )) )
//                    {
//                    int result = JOptionPane.showConfirmDialog( null, 
//                       "There is no file separator (/ or \\ or **) between starting folder and pattern. Do you want to insert one?"
//                            ,null, JOptionPane.YES_NO_OPTION );
//                    if ( result == JOptionPane.YES_OPTION )
//                        {
//                        filePattern.setText( System.getProperty( "file.separator" ) + args[2] );
//                        args[2] = filePattern.getText();
//                        }
//                    }
                
                //------- save history of paths  -------
                pathsHistoryList.add( args[0] );
                System.out.println( "after pathsHistoryList()" );
                
        searchModel.setTabsLogicType( tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
        searchModel.setShowFilesFoldersType( (String) showFilesFoldersCb.getSelectedItem() );
        System.out.println( "tabsLogic button.getText() =" + searchModel.getTabsLogicType() + "=" );

        searchModel.setSize1Op( (String)size1Op.getSelectedItem() );
        searchModel.setSize1( size1.getText().trim() );
        searchModel.setSizeLogicOp( ((String) sizeLogicOp.getValue()).trim() );
        searchModel.setSize2Op( (String)size2Op.getSelectedItem() );
        searchModel.setSize2( size2.getText().trim() );

        searchModel.setDate1Op( (String)date1Op.getSelectedItem() );
        searchModel.setDate1( (Date) date1.getModel().getValue() );
        searchModel.setDateLogicOp( ((String) dateLogicOp.getValue()).trim() );
        searchModel.setDate2Op( (String)date2Op.getSelectedItem() );
        searchModel.setDate2( (Date) date2.getModel().getValue() );

        searchModel.setMaxDepth( maxDepth.getText().trim() );
        searchModel.setMinDepth( minDepth.getText().trim() );

        searchModel.setStopFileCount( stopFileCount.getText().trim() );
        searchModel.setStopFolderCount( stopFolderCount.getText().trim() );

        searchModel.setShowHiddenFilesFlag( showHiddenFilesFlag.isSelected() );   
        
        return searchModel;
        }
        
    public void cleanup()
        {
        jfilefinder = null;
        }

    public void dumpThreads()
        {
        System.out.println( "\n---  entered JFileFinderWin.dumpThreads()  ---" );
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for ( Thread td : threadSet )
            {
            System.out.println( "td name =" + td.getName() + "  id =" + td.getId() );
            }
        System.out.println();
        System.gc();
        }
    
    public void emptyFilesTable()
        {
            if ( 1 == 1 ) return;
            
        System.out.println( "entered JFileFinderWin.emptyFilesTable()" );
        filesTbl.setModel( jfilefinder.emptyFilesTableModel( countOnlyFlag ) );
        setNumFilesInTable();
        }
    
    public void setColumnSizes()
        {
//        System.out.println( "entered setColumnSizes()" );
//        System.err.println( "entered setColumnSizes()" );
        try {
            TableColumnModel tblColModel = filesTbl.getColumnModel();
//            System.err.println( "setColumnSizes() table col count =" + tblColModel.getColumnCount() );
            if ( tblColModel.getColumnCount() < 2 )
                {
                return;
                }
//            System.err.println( "showJustFilenameFlag.isSelected() =" + showJustFilenameFlag.isSelected() );
//            System.err.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
            
            setColumnRenderers();

            tblColModel.getColumn(FilesTblModel.FILESTBLMODEL_FILETYPE ).setMaxWidth( programMemory.getTblColModelWidth( programMemory.TBLCOLMODEL_WIDTH_FILETYPE ) );

            tblColModel.getColumn(FilesTblModel.FILESTBLMODEL_FOLDERTYPE ).setMaxWidth( programMemory.getTblColModelWidth( programMemory.TBLCOLMODEL_WIDTH_FOLDERTYPE ) );

            if ( showJustFilenameFlag.isSelected() )
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setPreferredWidth( programMemory.getTblColModelWidth( programMemory.TBLCOLMODEL_WIDTH_PATH_SHORT ) );
                }
            else
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setPreferredWidth( programMemory.getTblColModelWidth( programMemory.TBLCOLMODEL_WIDTH_PATH_LONG ) );
                }

            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_MODIFIEDDATE ).setPreferredWidth( programMemory.getTblColModelWidth( programMemory.TBLCOLMODEL_WIDTH_MODIFIEDDATE ) );

            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_SIZE ).setCellRenderer( NumberRenderer.getIntegerRenderer() );
            
            if ( ! isShowOwnerFlag() )
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_OWNER ).setPreferredWidth( 0 );
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_OWNER ).setMinWidth( 0 );
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_OWNER ).setMaxWidth( 0 );
                }
            if ( ! getShowGroupFlag() )
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_GROUP ).setPreferredWidth( 0 );
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_GROUP ).setMinWidth( 0 );
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_GROUP ).setMaxWidth( 0 );
                }
            if ( ! isShowPermsFlag() )
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PERMS ).setPreferredWidth( 0 );
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PERMS ).setMinWidth( 0 );
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PERMS ).setMaxWidth( 0 );
                }

            alreadySetColumnSizes = true;
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            System.err.println( "Error in setColumnSizes()" );
            }
        }
        
    public void setColumnRenderers()
        {
//        System.out.println( "entered setColumnRenderers()" );
//        System.err.println( "entered setColumnRenderers()" );
        try {
            TableColumnModel tblColModel = filesTbl.getColumnModel();
//            System.err.println( "setColumnRenderers() table col count =" + tblColModel.getColumnCount() );
            if ( tblColModel.getColumnCount() < 2 )
                {
                return;
                }
//            System.err.println( "showJustFilenameFlag.isSelected() =" + showJustFilenameFlag.isSelected() );
//            System.err.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );

            tblColModel.getColumn(FilesTblModel.FILESTBLMODEL_FILETYPE ).setCellRenderer( new EnumIconCellRenderer() );

            tblColModel.getColumn(FilesTblModel.FILESTBLMODEL_FOLDERTYPE ).setCellRenderer( new EnumFolderIconCellRenderer() );

            if ( showJustFilenameFlag.isSelected() )
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setCellRenderer( new PathRenderer( connUserInfo.getToFilesysType() ) );
                }
            else
                {
                tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PATH ).setCellRenderer( new DefaultTableCellRenderer() );
                }

            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_MODIFIEDDATE ).setCellRenderer( FormatRenderer.getDateTimeRenderer() );

            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_SIZE ).setCellRenderer( NumberRenderer.getIntegerRenderer() );

            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_OWNER ).setCellRenderer( new DefaultTableCellRenderer() );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_GROUP ).setCellRenderer( new DefaultTableCellRenderer() );
            tblColModel.getColumn( FilesTblModel.FILESTBLMODEL_PERMS ).setCellRenderer( new DefaultTableCellRenderer() );
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            System.err.println( "Error in setColumnRenderers()" );
            }
        }
        
    public int tblColModelSizeViewed( int colSize )
        {
        if ( ! isShowOwnerFlag() )
            {
            colSize--;
            }
        if ( ! getShowGroupFlag() )
            {
            colSize--;
            }
        if ( ! isShowPermsFlag() )
            {
            colSize--;
            }
        return colSize;
        }

    public void fillInFilesTable( FilesTblModel filesTblModel )
        {
        System.out.println( "entered JFileFinderWin.fillInFilesTable()" );
        if ( filesTbl.getRowSorter() != null )
            {
            sortKeysSaved = (List<RowSorter.SortKey>) filesTbl.getRowSorter().getSortKeys();
            }

        filesTbl.getSelectionModel().clearSelection();
        if ( filesTblModel == null )
            {
//            System.out.println( "JFileFinderWin.fillInFilesTable()  passed fileTblModel is NULL" );
//            System.err.println( "JFileFinderWin.fillInFilesTable()  passed fileTblModel is NULL" );
            filesTblModel = jfilefinder.getFilesTableModel();
//            System.out.println( "JFileFinderWin.fillInFilesTable()  so got jfilefinder.getFilesTableModel() rows =" + filesTblModel.getRowCount() );
//            System.err.println( "JFileFinderWin.fillInFilesTable()  so got jfilefinder.getFilesTableModel() rows =" + filesTblModel.getRowCount() );
            }
//        else
//            {
//            System.out.println( "JFileFinderWin.fillInFilesTable()  passed fileTblModel is with rows =" + filesTblModel.getRowCount() );
//            System.err.println( "JFileFinderWin.fillInFilesTable()  passed fileTblModel is with rows =" + filesTblModel.getRowCount() );
//            }
        
//        System.out.println( "filesTblModel.getColumnCount() =" + filesTblModel.getColumnCount() + "   tblColModelSizeLast = " + tblColModelSizeLast );
//        System.out.println( "tblColModelSizeViewed( filesTblModel.getColumnCount() ) =" + tblColModelSizeViewed( filesTblModel.getColumnCount() ) + "   tblColModelSizeLast = " + tblColModelSizeLast );
//        System.err.println( "filesTblModel.getColumnCount() =" + filesTblModel.getColumnCount() + "   tblColModelSizeLast = " + tblColModelSizeLast );
//        System.err.println( "tblColModelSizeViewed( filesTblModel.getColumnCount() ) =" + tblColModelSizeViewed( filesTblModel.getColumnCount() ) + "   tblColModelSizeLast = " + tblColModelSizeLast );
        if ( tblColModelSizeViewed( filesTblModel.getColumnCount() ) != tblColModelSizeLast 
                || ! alreadySetColumnSizes )
            {
            filesTbl.setAutoCreateColumnsFromModel(true);
            tblColModelSizeLast = tblColModelSizeViewed( filesTblModel.getColumnCount() );
            }
        else
            {
            filesTbl.setAutoCreateColumnsFromModel( false );
            }
        
        filesTbl.setModel( filesTblModel );
        
        System.out.println( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );
        if ( filesTbl.getAutoCreateColumnsFromModel() && 
                ( resultsData.getFilesMatched() > 0 || resultsData.getFoldersMatched() > 0 
                  || filesTblModel.getColumnCount() > 0 ) )  // if we found files or have No Files Found to display !
            {
            setColumnSizes();
            }

        // debugging
//        int maxRows = filesTbl.getRowCount();
//        filesTblModel = (FilesTblModel) filesTbl.getModel();
//        System.out.println( "maxRows =" + maxRows + "=" );
//        //loop for jtable rows
//        for( int i = 0; i < maxRows; i++ )
//            {
//            Date tmp = (Date) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_MODIFIEDDATE );
//            System.out.println( "date string =" + tmp + "=" );
//            }
        
//        resultsData = null;  // to free up memory 
                
        // set up sorting
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>( filesTbl.getModel() );
        sorter.setSortsOnUpdates( false );

        //TableSorter<TableModel> sorter = new TableSorter<TableModel>( filesTblModel );
        filesTbl.setRowSorter( sorter );
        
        //int dirRowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );

        if ( filesTbl.getModel().getColumnCount() > 1 )
            {
            if ( sortKeysSaved == null || sortKeysSaved.size() < 1)
                {
                sortKeysSaved = new ArrayList<RowSorter.SortKey>();
                sortKeysSaved.add( new RowSorter.SortKey( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, SortOrder.DESCENDING ) );
                sortKeysSaved.add( new RowSorter.SortKey( FilesTblModel.FILESTBLMODEL_PATH, SortOrder.ASCENDING ) );
                }
            else if ( sortKeysSaved.size() < 2 )
                {
                //System.out.println("sortKeysSaved.size() = " + sortKeysSaved.size() );
                RowSorter.SortKey rsk = sortKeysSaved.get( 0 );
                sortKeysSaved = new ArrayList<RowSorter.SortKey>();
                sortKeysSaved.add( new RowSorter.SortKey( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, SortOrder.DESCENDING ) );
                sortKeysSaved.add( rsk );
                }
            sorter.setSortKeys( sortKeysSaved );
        
            //System.out.println("sortKeysSaved.size() = " + sortKeysSaved.size() );
            RowSorterListener rowSorterListener = new MyRowSorterListener( sorter, sortKeysSaved.get(1).getColumn(), sortKeysSaved.get(1).getSortOrder() );
            sorter.addRowSorterListener( rowSorterListener );
            }
        
        System.gc();        
        }

    public void desktopOpen( String filestr )
        {
        String rmtFile = filestr;
        File file = new File( filestr.replace( "\\", "/" ) );
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            System.out.println("Desktop is not supported");
            return;
            }
//        System.out.println( "filestr              =" + filestr + "=" );
//        System.out.println( "stdOutFile.getText() =" + stdOutFile.getText() + "=" );

        System.out.println( "do Desktop Open for filestr =" + filestr + "=" );
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( connUserInfo.isConnectedFlag()  &&   //rmtConnectBtn.getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) &&
                 ! stdOutFile.getText().equals( filestr ) && ! stdErrFile.getText().equals( filestr ) )
                {
                JschSftpUtils jschSftpUtils = new JschSftpUtils();
                jschSftpUtils.SftpGet( filestr, rmtUser.getText().trim(), rmtPasswd.getText().trim(), rmtHost.getText().trim(), rmtSshPort.getText().trim(), JfpHomeTempDir + file.getName() );
                file = new File( JfpHomeTempDir + file.getName() );
                }
            if ( file.exists() )
                {
                desktop.open( file );
                }
            if ( connUserInfo.isConnectedFlag()  &&   //rmtConnectBtn.getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) &&
                 ! stdOutFile.getText().equals( filestr ) && ! stdErrFile.getText().equals( filestr ) )
                {
                SftpReturnFileFrame sftpReturnFileFrame = new SftpReturnFileFrame( connUserInfo, JfpHomeTempDir + file.getName(), rmtUser.getText().trim(), rmtPasswd.getText().trim(), rmtHost.getText().trim(), rmtFile.toString() );
                sftpReturnFileFrame.validate();
                sftpReturnFileFrame.pack();
                sftpReturnFileFrame.setVisible(true);
//                
//                Process p = Runtime.getRuntime().exec("start /wait cmd /K " + file);
//                int exitVal = p.waitFor();
                }
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog( this, "Open not supported in this desktop", "Error", JOptionPane.ERROR_MESSAGE );
            }
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        }
        
    public void desktopEdit( String filestr )
        {
        String rmtFile = filestr;
        File file = new File( filestr.replace( "\\", "/" ) );
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            System.out.println("Desktop is not supported");
            return;
            }
         
        System.out.println( "do Desktop Edit" );
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( connUserInfo.isConnectedFlag()  &&   //rmtConnectBtn.getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) &&
                 ! stdOutFile.getText().equals( filestr ) && ! stdErrFile.getText().equals( filestr ) )
                {
                JschSftpUtils jschSftpUtils = new JschSftpUtils();
                jschSftpUtils.SftpGet( filestr, rmtUser.getText().trim(), rmtPasswd.getText().trim(), rmtHost.getText().trim(), rmtSshPort.getText().trim(), JfpHomeTempDir + file.getName() );
                file = new File( JfpHomeTempDir + file.getName() );
                }
            if ( file.exists() )
                {
                //desktop.edit( file );
                System.out.println( "start Cmd =" + myEditorCmd.getText() + " " + file.toString() + "=" );
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.exec( connUserInfo.isConnectedFlag() ? JfpHomeTempDir : getStartingFolder(), true, false, myEditorCmd.getText(), file.toString() );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
                }
            if ( connUserInfo.isConnectedFlag()  &&   //rmtConnectBtn.getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) &&
                 ! stdOutFile.getText().equals( filestr ) && ! stdErrFile.getText().equals( filestr ) )
                {
                SftpReturnFileFrame sftpReturnFileFrame = new SftpReturnFileFrame( connUserInfo, JfpHomeTempDir + file.getName(), rmtUser.getText().trim(), rmtPasswd.getText().trim(), rmtHost.getText().trim(), rmtFile.toString() );
                sftpReturnFileFrame.validate();
                sftpReturnFileFrame.pack();
                sftpReturnFileFrame.setVisible(true);
//                
//                Process p = Runtime.getRuntime().exec("start /wait cmd /K " + file);
//                int exitVal = p.waitFor();
                }
            }
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
//            JOptionPane.showMessageDialog( this, "Edit not supported in this desktop.\nWill try Open.", "Error", JOptionPane.ERROR_MESSAGE );
            desktopOpen( filestr );
            }
        }
        
    public void jfpExecOrStop( String cmd )
    {
        try {
            //System.out.println( "RenameActionPerformed evt.getSource() =" + evt.getSource() );
            if ( filesTbl.getSelectedRow() < 0 )
                {
                JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }
            int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
            System.out.println( "rename filesTbl.getSelectedRow() =" + filesTbl.getSelectedRow() + "   rowIndex = " + rowIndex );
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            String selectedPath = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
            System.out.println( "start webserver at selectedPath =" + selectedPath + "   rowIndex = " + rowIndex );
            
            String runCmdString = "";
            PathMatcher matcher = null;
//            System.out.println( "------- BEG DUMP FILE ASSOC LIST");
//            for(Map.Entry<String,FileAssoc> entry : fileAssocList.getFileAssocList().entrySet()) {
//                String key = entry.getKey();
//                FileAssoc value = entry.getValue();
//
//                System.out.println( "matched and got fa.getAssocType =" + value.getAssocType() + "=" );
//                System.out.println( "matched and got fa.getMatchPattern =" + value.getMatchPattern() + "=" );
//                System.out.println( "matched and got fa.exec =" + value.getExec() + "=" );
//                }
//            System.out.println( "------- END DUMP FILE ASSOC LIST");
            
            FileAssoc fa = fileAssocList.getFileAssoc(JfpConstants.ASSOC_TYPE_EXACT_FILE, selectedPath );
            if ( fa != null )
                {
                System.out.println( "matched and got ASSOC_TYPE_1_FILE fa.exec =" + fa.getExec() + "=" );
                if ( cmd.equalsIgnoreCase( "stop" ) )
                    runCmdString = fa.getStop();
                else
                    runCmdString = fa.getExec();
                }
            else
                {
                fa = fileAssocList.getFileAssoc( JfpConstants.ASSOC_TYPE_FILENAME, selectedPath );
                if ( fa != null )
                    {
                    System.out.println( "matched and got ASSOC_TYPE_FILENAME fa.exec =" + fa.getExec() + "=" );
                    if ( cmd.equalsIgnoreCase( "stop" ) )
                        runCmdString = fa.getStop();
                    else
                        runCmdString = fa.getExec();
                    }
                else
                    {
                    fa = fileAssocList.getFileAssoc(JfpConstants.ASSOC_TYPE_SUFFIX, selectedPath );
                    if ( fa != null )
                        {
                        System.out.println( "matched and got ASSOC_TYPE_SUFFIX fa.exec =" + fa.getExec() + "=" );
                        if ( cmd.equalsIgnoreCase( "stop" ) )
                            runCmdString = fa.getStop();
                        else
                            runCmdString = fa.getExec();
                        }
                    else
                        {
                        System.out.println( "No File Assoc Found" );
                        com.towianski.utils.FileAssocWin fileAssocWin = new FileAssocWin( "New", selectedPath, null );
                        fa = new FileAssoc( fileAssocWin.getAssocType(), fileAssocWin.getEditClass(), 
                                fileAssocWin.getMatchType(), fileAssocWin.getMatchPattern(), fileAssocWin.getExec(), fileAssocWin.getStop() );
                        System.out.println( "matched and got fa.getAssocType =" + fa.getAssocType() + "=" );
                        System.out.println( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
                        System.out.println( "matched and got fa.exec =" + fa.getExec() + "=" );
                        if ( cmd.equalsIgnoreCase( "stop" ) )
                            runCmdString = fa.getStop();
                        else
                            runCmdString = fa.getExec();
                        if ( fileAssocWin.isOkFlag() )
                            {
                            fileAssocList.addFileAssoc( fa );
                            Rest.saveObjectToFile( "FileAssocList.json", fileAssocList );
                            }
                        else
                            {
                            fileAssocWin = null;
                            return;
                            }
                        fileAssocWin = null;
                        }
                    }
                }
            
            ProcessInThread jp = new ProcessInThread();
            ArrayList<String> cmdList = new ArrayList<String>();
//            cmdList.add( "$JAVABIN" );
////            cmdList.add( "-cp" );
////            cmdList.add( "$CLASSPATH" );
//            cmdList.add( "-jar" );
//            cmdList.add( "jetty-runner-9.4.19.v20190610.jar" );
//            cmdList.add( "--path" );
//            cmdList.add( "/what" );
//            cmdList.add( "--port" );
//            cmdList.add( "8074" );
////            cmdList.add( "/home/stan/Downloads/sample.war" );

//            cmdList.add( "$JAVABIN" );
//            cmdList.add( "-Dspring.profiles.active=warserver" );
////            cmdList.add( "-cp" );
////            cmdList.add( "$CLASSPATH" );
//            cmdList.add( "-jar" );
//            cmdList.add( "JFileProcessor-1.8.2.war" );
//            cmdList.add( "--warfile=" + selectedPath );
//            cmdList.add( "--path=/" +  (selectedPath.indexOf( "/SampleWebApp.war" ) > 0 ? "swa" : "sample" ) );
//            cmdList.add( "--port=" + (selectedPath.indexOf( "/SampleWebApp.war" ) > 0 ? "8075" : "8060" ) );
//            cmdList.add( "--logging.file=" + (selectedPath.indexOf( "/SampleWebApp.war" ) > 0 ? "/tmp/swa.log" : "/tmp/sample.log" ) );
//            cmdList.add( "--warserver" );

//            int rc = jp.execJava2( cmdList, true, "--logging.file=" + "/tmp/webserver.log", "-dir=" + selectedPath );
//            int rc = jp.execJava2( cmdList, true );
            System.out.println( "runCmdString =" + runCmdString + "=" );
            runCmdString = runCmdString.replace( "%f", selectedPath );
            Path fpath = Paths.get( selectedPath );
            runCmdString = runCmdString.replace( "%F", fpath.getFileName().toString() );
            System.out.println( "runCmdString =" + runCmdString + "=" );

            String rcStr = "";
            if ( runCmdString.startsWith( "url:" ) )
                {
                try
                    {
                    RestTemplate restTemplate = Rest.createNoHostVerifyRestTemplate();
                    restTemplate.getForObject( runCmdString.substring( "url:".length() ), String.class );
                    } 
                catch (Exception ex)
                    {
                    Logger.getLogger(TomcatAppThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                rcStr = "ran " + runCmdString;
                }
            else
                {
                // split by spaces and assume that will work.
                String[] cmdAr = runCmdString.split( " " );
                cmdList = new ArrayList<String>(Arrays.asList( cmdAr )); 

                int rc = jp.execJava2( cmdList, true );
                if ( cmd.equalsIgnoreCase( "stop" ) )
                    rcStr = "stop returned code =" + rc + "=";
                else
                    rcStr = "start returned code =" + rc + "=";
                System.out.println( rcStr );
                }

            // Start a msgBox
            {
                final String tmp = rcStr;
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new MsgBoxFrame( tmp ).setVisible( true );
                    }
                });
            }
        
//                if ( selectedPath.toUpperCase().endsWith( ".GROOVY" ) )
//                    {
//                    openCodeWinPanel( this, selectedPath, null );
//                    openedFlag = true;
//                    }
//                }
//            if ( ! openedFlag )
//                {
//                openCodeWinPanel( this, null, null ).setTitle( "code" );
//                }
        } catch (IOException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println( "previewImportWin formWindow dispose()" );
                win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, KeyEvent.SHIFT_DOWN_MASK ),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }    
    
//    public static void addHotKeysListener(final JTable table) {
//        ActionListener hotkeysListener = new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println( "addHotKeysListener hotkeys()" );
//                System.out.println( "addHotKeysListener hotkeys()" + e.get );
//                win.dispatchEvent( new WindowEvent( table, WindowEvent.WINDOW_CLOSING )); 
//                win.dispose();
//            }
//        };
//
//        table.registerKeyboardAction(hotkeysListener,
//                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
//                JComponent.WHEN_IN_FOCUSED_WINDOW);
//    }    

    public void copyOrCut()
    {
        if ( filesTbl.getSelectedRow() < 0 )
            {
            JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
 
        //copyPaths = new ArrayList<Path>();
        //ArrayList<String> filesList = new ArrayList<String>();
        
        // DESIGN NOTE:  first file on clipboard is starting/from path ! - 2nd is word cut or copy
        StringBuffer stringBuf = new StringBuffer();
        copyPathStartPath = startingFolder.getText().trim();
        //filesList.add( new File( copyPathStartPath ) );
        //filesList.add( copyPathStartPath );
        //filesList.add( ( isDoingCutFlag ? "CUT" : "COPY" ) );
        stringBuf.append( copyPathStartPath ).append( "?" );
        stringBuf.append( ( isDoingCutFlag ? "CUT" : "COPY" ) ).append( "?" );
        stringBuf.append( filesysType ).append( "?" );
        if ( connUserInfo.isConnectedFlag() )
            {
            stringBuf.append( Constants.PATH_PROTOCOL_SFTP + "?" + getRmtUser() + "?" + getRmtPasswd() + "?" + getRmtHost() + "?" + getRmtSshPort() ).append( "?" );
            }
        else
            {
            stringBuf.append( Constants.PATH_PROTOCOL_FILE + "???" + hostAddress + "?" + getRmtSshPort() ).append( "?" );
            }
        
        for( int row : filesTbl.getSelectedRows() )
            {
            int rowIndex = filesTbl.convertRowIndexToModel( row );
            //System.out.println( "add copy path  row =" + row + "   rowIndex = " + rowIndex );
            //System.out.println( "copy path  =" + ((String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) + "=" );
            //copyPaths.add( Paths.get( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            //filesList.add( new File( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            stringBuf.append( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ).append( "?" );
//            System.out.println( "add fpath =" + (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
            }   

//        ClipboardFiles clipboardFiles = new ClipboardFiles( filesList );
//        clipboard.setContents( clipboardFiles, clipboardFiles );  );        

        setClipboardContents( stringBuf.toString() );
        filesTbl.clearSelection();
    }
    
    public ListOfFilesPanel getListOfFilesPanel( String winName )
        {
        String ans = null;
        if ( winName == null )
            {
            ans = JOptionPane.showInputDialog( "List Name: ", "" );
            if ( ans == null )
                {
                return null;
                }
            }
        else
            {
            ans = winName;
            }
        
        ListOfFilesPanel listOfFilesPanel = null;
        int pmIdx = listOfFilesPanelsModel.getIndexOf( ans );
        if ( listOfFilesPanelsModel.getIndexOf( ans ) < 0 )
            {
            System.out.println( "create new listOfFilesPanel " + ans );
            listOfFilesPanelsModel.addElement( ans );
            listOfFilesPanel = new ListOfFilesPanel( this, ans, listOfFilesPanelsModel );
            listOfFilesPanel.setState ( JFrame.ICONIFIED );
            listOfFilesPanel.setTitle(ans);
            listOfFilesPanelHm.put( ans, listOfFilesPanel );
            
            listOfFilesPanel.pack();
            listOfFilesPanel.setVisible(true);
            listOfFilesPanel.setState ( JFrame.NORMAL );            
            }
        else
            {
            System.out.println( "found existing listOfFilesPanel at pmIdx =" + pmIdx );
            listOfFilesPanel = (ListOfFilesPanel) listOfFilesPanelHm.get( ans );
            }
        return listOfFilesPanel;
        }

    public void savePathsToFile( File selectedFile )
    {
        System.out.println( "File to save to =" + selectedFile + "=" );
        //Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        //String[] tt = { selectedFile.getPath() };
        //startingFolder.setText( selectedFile.getPath() );
        
        try
            {
            if( ! selectedFile.exists() )
                {
                selectedFile.createNewFile();
                }

            FileWriter fw = new FileWriter( selectedFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int maxRows = filesTbl.getRowCount();
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            
            //loop for jtable rows
            for( int i = 0; i < maxRows; i++ )
                {
                bw.write( (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) );
                bw.write( "\n" );
                }
            //close BufferedWriter
            bw.close();
            //close FileWriter 
            fw.close();
//            JOptionPane.showMessageDialog(null, "Saved to File");        
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
    }
    
    public void openPathsToList( ListOfFilesPanel listOfFilesPanel, String winName )
        {
        try
            {
            if ( listOfFilesPanel == null )
                {
                listOfFilesPanel = getListOfFilesPanel( winName );
                if ( listOfFilesPanel == null )
                    {
                    return;
                    }
                }
            int maxRows = filesTbl.getRowCount();
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            DefaultComboBoxModel listModel = (DefaultComboBoxModel) listOfFilesPanel.getModel();
            
            System.out.println( "add list path() num of items =" + maxRows + "=" );
            int numChanged = 0;
            
            //loop for jtable rows
            for( int i = 0; i < maxRows; i++ )
                {
//                    System.out.println( "check path =" + (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
                String str = (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH );
//                int foundAt = listModel.getIndexOf( str );
//                    System.out.println( "is found idx =" + foundAt + "=" );
//                if ( foundAt < 0 )
                    {
                    listModel.addElement( str );
                    numChanged ++;
                    }
                }
            listOfFilesPanel.setCount();
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        }

    public void openStringsToList( ListOfFilesPanel listOfFilesPanel, String winName, ArrayList<String> strList )
        {
        try
            {
            if ( listOfFilesPanel == null )
                {
                listOfFilesPanel = getListOfFilesPanel( winName );
                if ( listOfFilesPanel == null )
                    {
                    return;
                    }
                }
            int maxRows = strList.size();
            DefaultComboBoxModel listModel = (DefaultComboBoxModel) listOfFilesPanel.getModel();
            
            System.out.println( "add list path() num of items =" + maxRows + "=" );
            
            //loop for jtable rows
            for( String str : strList )
                {
                listModel.addElement( str );
                }
            listOfFilesPanel.setCount();
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        }

    public DefaultComboBoxModel getSelectedItemsAsComboBoxModel()
        {
        DefaultComboBoxModel listModel = new DefaultComboBoxModel();
        try
            {
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            
            for( int row : filesTbl.getSelectedRows() )
                {
                int rowIndex = filesTbl.convertRowIndexToModel( row );
                String str = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
                listModel.addElement( str );
//                System.out.println( "add fpath =" + (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
                }   
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        return listModel;
        }

    public CodeProcessorPanel openCodeWinPanel( JFileFinderWin jFileFinderWin, String selectedPath, String listOfFilesPanelName )
        {
        CodeProcessorPanel codeProcessorPanel = new CodeProcessorPanel( jFileFinderWin, selectedPath, listOfFilesPanelName );
        codeProcessorPanel.setState ( JFrame.ICONIFIED );

        codeProcessorPanel.pack();
        codeProcessorPanel.setVisible(true);
        codeProcessorPanel.setState ( JFrame.NORMAL );
        return codeProcessorPanel;
        }
        
    public ArrayList<String> pathsToNotWatch()
        {
        ArrayList<String> ignoreList = new ArrayList<String>();
            
        if ( stdOutFile.getText() != null && ! stdOutFile.getText().equals( "" ) )
            {
            ignoreList.add( stdOutFile.getText().trim() );
            }
        if ( stdErrFile.getText() != null && ! stdErrFile.getText().equals( "" ) )
            {
            ignoreList.add( stdErrFile.getText().trim() );
            }
        return ignoreList;
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        Copy = new javax.swing.JMenuItem();
        Cut = new javax.swing.JMenuItem();
        Paste = new javax.swing.JMenuItem();
        Delete = new javax.swing.JMenuItem();
        Rename = new javax.swing.JMenuItem();
        New = new javax.swing.JMenu();
        NewFolder = new javax.swing.JMenuItem();
        NewFile = new javax.swing.JMenuItem();
        copyFilename = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        openFile = new javax.swing.JMenuItem();
        jfpExec = new javax.swing.JMenuItem();
        jfpStop = new javax.swing.JMenuItem();
        myEdit = new javax.swing.JMenuItem();
        openFolderOfFiles = new javax.swing.JMenuItem();
        startCmdWin = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        savePathsToFile = new javax.swing.JMenuItem();
        openListWindow = new javax.swing.JMenuItem();
        readFileIntoListWin = new javax.swing.JMenuItem();
        openCodeWin = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        scriptsMenu = new javax.swing.JMenu();
        jfpExecEdit = new javax.swing.JMenuItem();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        Paste1 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        NewFolder1 = new javax.swing.JMenuItem();
        NewFile1 = new javax.swing.JMenuItem();
        startCmdWin1 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        savePathsToFile1 = new javax.swing.JMenuItem();
        openListWindow1 = new javax.swing.JMenuItem();
        readFileIntoListWin1 = new javax.swing.JMenuItem();
        openCodeWin1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        scriptsMenu1 = new javax.swing.JMenu();
        jSplitPane2 = new javax.swing.JSplitPane();
        replaceSavePathPanel = new javax.swing.JPanel();
        savedPathsListScrollPane = new javax.swing.JScrollPane();
        savedPathsList = new javax.swing.JList<>();
        addPath = new javax.swing.JButton();
        deletePath = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        rmtUser = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        rmtPasswd = new javax.swing.JPasswordField();
        jLabel19 = new javax.swing.JLabel();
        rmtHost = new javax.swing.JTextField();
        rmtConnectBtn = new javax.swing.JButton();
        rmtDisconnectBtn = new javax.swing.JButton();
        rmtForceDisconnectBtn = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        rmtSshPort = new javax.swing.JTextField();
        rmtAskHttpsPortLbl = new javax.swing.JLabel();
        rmtAskHttpsPort = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        fileMgrMode = new javax.swing.JCheckBox();
        startingFolder = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        leftHistory = new javax.swing.JButton();
        rightHistory = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        showHiddenFilesFlag = new javax.swing.JCheckBox();
        showJustFilenameFlag = new javax.swing.JCheckBox();
        logLevel = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        showOwnerFlag = new javax.swing.JCheckBox();
        showGroupFlag = new javax.swing.JCheckBox();
        showPermsFlag = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        tabsLogicAndBtn = new javax.swing.JRadioButton();
        tabsLogicOrBtn = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        useGlobPattern = new javax.swing.JRadioButton();
        useRegexPattern = new javax.swing.JRadioButton();
        filePattern = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        maxDepth = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        minDepth = new javax.swing.JTextField();
        showFilesFoldersCb = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        date1Op = new javax.swing.JComboBox();
        String[] andOrSpinModelList = { "", "And", "Or" };
        SpinnerListModel dateAndOrSpinModel = new CyclingSpinnerListModel( andOrSpinModelList );
        dateLogicOp = new javax.swing.JSpinner( dateAndOrSpinModel );
        date2Op = new javax.swing.JComboBox();
        date1 = new org.jdatepicker.impl.JDatePickerImpl();
        date2 = new org.jdatepicker.impl.JDatePickerImpl();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        size2 = new javax.swing.JTextField();
        size1Op = new javax.swing.JComboBox();
        size2Op = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        SpinnerListModel sizeAndOrSpinModel = new CyclingSpinnerListModel( andOrSpinModelList );
        sizeLogicOp = new javax.swing.JSpinner( sizeAndOrSpinModel );
        size1 = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        stopFileCount = new javax.swing.JTextField();
        stopFolderCount = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        myEditorLbl = new javax.swing.JLabel();
        myEditorCmd = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        startConsoleCmd = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        stdOutFile = new javax.swing.JFormattedTextField();
        stdErrFile = new javax.swing.JFormattedTextField();
        stopFileWatchTb = new javax.swing.JToggleButton();
        fileWatchLbl = new javax.swing.JLabel();
        dumpThreadListToLog = new javax.swing.JButton();
        webSiteLink = new javax.swing.JLabel();
        reportIssueLbl = new javax.swing.JLabel();
        testSshBtn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        searchBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesTbl = new javax.swing.JTable() {
            public void changeSelection(    int row, int column, boolean toggle, boolean extend)
            {
                super.changeSelection(row, column, toggle, extend);

                if (editCellAt(row, column))
                {
                    Component editor = getEditorComponent();
                    editor.requestFocusInWindow();
                }
            }};
            processStatus = new javax.swing.JLabel();
            message = new javax.swing.JLabel();
            numFilesInTable = new javax.swing.JLabel();
            upFolder = new javax.swing.JButton();
            countBtn = new javax.swing.JButton();
            logsBtn = new javax.swing.JButton();
            fsTypeLbl = new javax.swing.JLabel();
            newerVersionLbl = new javax.swing.JLabel();
            newerVersionSpLbl = new javax.swing.JLabel();
            botPanel = new javax.swing.JPanel();

            Copy.setText("Copy   (Ctrl-C)");
            Copy.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    CopyActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Copy);

            Cut.setText("Cut   (Ctrl-X)");
            Cut.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    CutActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Cut);

            Paste.setText("Paste   (Ctrl-P)");
            Paste.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    PasteActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Paste);

            Delete.setText("Delete   (Del or fn-Del)");
            Delete.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    DeleteActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Delete);

            Rename.setText("Rename   (F2)");
            Rename.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RenameActionPerformed(evt);
                }
            });
            jPopupMenu1.add(Rename);

            New.setText("New");

            NewFolder.setText("New Folder   (Ctrl-N)");
            NewFolder.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewFolderActionPerformed(evt);
                }
            });
            New.add(NewFolder);

            NewFile.setText("New File");
            NewFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewFileActionPerformed(evt);
                }
            });
            New.add(NewFile);

            jPopupMenu1.add(New);

            copyFilename.setText("Copy Filename to Clipboard");
            copyFilename.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    copyFilenameActionPerformed(evt);
                }
            });
            jPopupMenu1.add(copyFilename);
            jPopupMenu1.add(jSeparator1);

            openFile.setText("System Open File");
            openFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openFileActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openFile);

            jfpExec.setText("jfp Open/Run");
            jfpExec.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jfpExecActionPerformed(evt);
                }
            });
            jPopupMenu1.add(jfpExec);

            jfpStop.setText("jfp Stop");
            jfpStop.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jfpStopActionPerformed(evt);
                }
            });
            jPopupMenu1.add(jfpStop);

            myEdit.setText("my Edit File");
            myEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    myEditActionPerformed(evt);
                }
            });
            jPopupMenu1.add(myEdit);

            openFolderOfFiles.setText("Open Folder Containing File(s)");
            openFolderOfFiles.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openFolderOfFilesActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openFolderOfFiles);

            startCmdWin.setText("Open Terminal here");
            startCmdWin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startCmdWinActionPerformed(evt);
                }
            });
            jPopupMenu1.add(startCmdWin);
            jPopupMenu1.add(jSeparator2);

            savePathsToFile.setText("Save Paths to File");
            savePathsToFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    savePathsToFileActionPerformed(evt);
                }
            });
            jPopupMenu1.add(savePathsToFile);

            openListWindow.setText("Open List Window");
            openListWindow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openListWindowActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openListWindow);

            readFileIntoListWin.setText("Read File into List Window");
            readFileIntoListWin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    readFileIntoListWinActionPerformed(evt);
                }
            });
            jPopupMenu1.add(readFileIntoListWin);

            openCodeWin.setText("Open Code Window");
            openCodeWin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openCodeWinActionPerformed(evt);
                }
            });
            jPopupMenu1.add(openCodeWin);
            jPopupMenu1.add(jSeparator3);

            scriptsMenu.setText("Scripts");
            scriptsMenu.addMenuListener(new javax.swing.event.MenuListener() {
                public void menuSelected(javax.swing.event.MenuEvent evt) {
                    scriptsMenuMenuSelected(evt);
                }
                public void menuDeselected(javax.swing.event.MenuEvent evt) {
                }
                public void menuCanceled(javax.swing.event.MenuEvent evt) {
                }
            });
            jPopupMenu1.add(scriptsMenu);

            jfpExecEdit.setText("jfp File Assoc");
            jfpExecEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jfpExecEditActionPerformed(evt);
                }
            });
            jPopupMenu1.add(jfpExecEdit);

            Paste1.setText("Paste   (Ctrl-P)");
            Paste1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Paste1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(Paste1);

            jMenu1.setText("New");

            NewFolder1.setText("New Folder   (Ctrl-N)");
            NewFolder1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewFolder1ActionPerformed(evt);
                }
            });
            jMenu1.add(NewFolder1);

            NewFile1.setText("New File");
            NewFile1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewFile1ActionPerformed(evt);
                }
            });
            jMenu1.add(NewFile1);

            jPopupMenu2.add(jMenu1);

            startCmdWin1.setText("Open Terminal here");
            startCmdWin1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startCmdWin1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(startCmdWin1);
            jPopupMenu2.add(jSeparator4);

            savePathsToFile1.setText("Save Paths to File");
            savePathsToFile1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    savePathsToFile1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(savePathsToFile1);

            openListWindow1.setText("Open List Window");
            openListWindow1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openListWindow1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(openListWindow1);

            readFileIntoListWin1.setText("Read File into List Window");
            readFileIntoListWin1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    readFileIntoListWin1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(readFileIntoListWin1);

            openCodeWin1.setText("Open Code Window");
            openCodeWin1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openCodeWin1ActionPerformed(evt);
                }
            });
            jPopupMenu2.add(openCodeWin1);
            jPopupMenu2.add(jSeparator5);

            scriptsMenu1.setText("Scripts");
            scriptsMenu1.addMenuListener(new javax.swing.event.MenuListener() {
                public void menuSelected(javax.swing.event.MenuEvent evt) {
                    scriptsMenu1MenuSelected(evt);
                }
                public void menuDeselected(javax.swing.event.MenuEvent evt) {
                }
                public void menuCanceled(javax.swing.event.MenuEvent evt) {
                }
            });
            jPopupMenu2.add(scriptsMenu1);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle("JFileProcessor v1.6.0 - Stan Towianski  (c) 2015-2018");
            setIconImage(Toolkit.getDefaultToolkit().getImage( JFileFinderWin.class.getResource("/icons/jfp.png") ));
            setMinimumSize(new java.awt.Dimension(500, 179));
            addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowOpened(java.awt.event.WindowEvent evt) {
                    formWindowOpened(evt);
                }
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

            replaceSavePathPanel.setMinimumSize(new java.awt.Dimension(0, 0));
            replaceSavePathPanel.setPreferredSize(new java.awt.Dimension(170, 500));
            replaceSavePathPanel.setLayout(new java.awt.GridBagLayout());

            savedPathsListScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
            savedPathsListScrollPane.setPreferredSize(new java.awt.Dimension(170, 500));

            savedPathsList.setModel(new DefaultListModel() );
            savedPathsList.setPreferredSize(new java.awt.Dimension(120, 500));
            savedPathsList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    savedPathsListMouseClicked(evt);
                }
            });
            savedPathsListScrollPane.setViewportView(savedPathsList);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.5;
            replaceSavePathPanel.add(savedPathsListScrollPane, gridBagConstraints);

            addPath.setText("Add");
            addPath.setMargin(new java.awt.Insets(0, 0, 0, 0));
            addPath.setMaximumSize(new java.awt.Dimension(31, 23));
            addPath.setMinimumSize(new java.awt.Dimension(0, 0));
            addPath.setPreferredSize(new java.awt.Dimension(31, 23));
            addPath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addPathActionPerformed(evt);
                }
            });
            replaceSavePathPanel.add(addPath, new java.awt.GridBagConstraints());

            deletePath.setText("Delete");
            deletePath.setMargin(new java.awt.Insets(0, 0, 0, 0));
            deletePath.setMaximumSize(new java.awt.Dimension(75, 23));
            deletePath.setMinimumSize(new java.awt.Dimension(0, 0));
            deletePath.setPreferredSize(new java.awt.Dimension(75, 23));
            deletePath.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    deletePathActionPerformed(evt);
                }
            });
            replaceSavePathPanel.add(deletePath, new java.awt.GridBagConstraints());

            jSplitPane2.setLeftComponent(replaceSavePathPanel);

            jPanel10.setMinimumSize(new java.awt.Dimension(80, 172));
            jPanel10.setPreferredSize(new java.awt.Dimension(800, 500));
            jPanel10.setLayout(new java.awt.BorderLayout());

            topPanel.setMinimumSize(new java.awt.Dimension(60, 35));

            jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel17.setText("Remote User");
            jLabel17.setMaximumSize(new java.awt.Dimension(100, 25));
            jLabel17.setMinimumSize(new java.awt.Dimension(100, 25));
            jLabel17.setPreferredSize(new java.awt.Dimension(100, 25));
            topPanel.add(jLabel17);

            rmtUser.setMargin(new java.awt.Insets(2, 7, 0, 0));
            rmtUser.setMaximumSize(new java.awt.Dimension(100, 25));
            rmtUser.setMinimumSize(new java.awt.Dimension(100, 25));
            rmtUser.setPreferredSize(new java.awt.Dimension(100, 25));
            rmtUser.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtUserActionPerformed(evt);
                }
            });
            topPanel.add(rmtUser);

            jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel18.setText("Pass");
            jLabel18.setMaximumSize(new java.awt.Dimension(50, 25));
            jLabel18.setMinimumSize(new java.awt.Dimension(50, 25));
            jLabel18.setPreferredSize(new java.awt.Dimension(50, 25));
            topPanel.add(jLabel18);

            rmtPasswd.setMaximumSize(new java.awt.Dimension(80, 25));
            rmtPasswd.setMinimumSize(new java.awt.Dimension(80, 25));
            rmtPasswd.setPreferredSize(new java.awt.Dimension(80, 25));
            rmtPasswd.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtPasswdActionPerformed(evt);
                }
            });
            topPanel.add(rmtPasswd);

            jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel19.setText("Host");
            jLabel19.setMaximumSize(new java.awt.Dimension(50, 25));
            jLabel19.setMinimumSize(new java.awt.Dimension(50, 25));
            jLabel19.setPreferredSize(new java.awt.Dimension(50, 25));
            topPanel.add(jLabel19);

            rmtHost.setMargin(new java.awt.Insets(2, 7, 0, 0));
            rmtHost.setMaximumSize(new java.awt.Dimension(150, 25));
            rmtHost.setMinimumSize(new java.awt.Dimension(150, 25));
            rmtHost.setPreferredSize(new java.awt.Dimension(150, 25));
            rmtHost.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtHostActionPerformed(evt);
                }
            });
            topPanel.add(rmtHost);

            rmtConnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/connect-icon-16.png"))); // NOI18N
            rmtConnectBtn.setMaximumSize(new java.awt.Dimension(45, 25));
            rmtConnectBtn.setMinimumSize(new java.awt.Dimension(45, 25));
            rmtConnectBtn.setPreferredSize(new java.awt.Dimension(45, 25));
            rmtConnectBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtConnectBtnActionPerformed(evt);
                }
            });
            topPanel.add(rmtConnectBtn);

            rmtDisconnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/disconnect-icon-16.png"))); // NOI18N
            rmtDisconnectBtn.setMaximumSize(new java.awt.Dimension(45, 25));
            rmtDisconnectBtn.setMinimumSize(new java.awt.Dimension(45, 25));
            rmtDisconnectBtn.setPreferredSize(new java.awt.Dimension(45, 25));
            rmtDisconnectBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtDisconnectBtnActionPerformed(evt);
                }
            });
            topPanel.add(rmtDisconnectBtn);

            rmtForceDisconnectBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/disconnect-icon-16.png"))); // NOI18N
            rmtForceDisconnectBtn.setText("!!");
            rmtForceDisconnectBtn.setMaximumSize(new java.awt.Dimension(65, 25));
            rmtForceDisconnectBtn.setMinimumSize(new java.awt.Dimension(65, 25));
            rmtForceDisconnectBtn.setPreferredSize(new java.awt.Dimension(65, 25));
            rmtForceDisconnectBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtForceDisconnectBtnActionPerformed(evt);
                }
            });
            topPanel.add(rmtForceDisconnectBtn);

            jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel20.setText("SSH Port");
            topPanel.add(jLabel20);

            rmtSshPort.setText("22");
            rmtSshPort.setMinimumSize(new java.awt.Dimension(50, 25));
            rmtSshPort.setPreferredSize(new java.awt.Dimension(50, 25));
            rmtSshPort.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtSshPortActionPerformed(evt);
                }
            });
            topPanel.add(rmtSshPort);

            rmtAskHttpsPortLbl.setText("Https Port");
            topPanel.add(rmtAskHttpsPortLbl);

            rmtAskHttpsPort.setMinimumSize(new java.awt.Dimension(50, 25));
            rmtAskHttpsPort.setPreferredSize(new java.awt.Dimension(50, 25));
            rmtAskHttpsPort.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rmtAskHttpsPortActionPerformed(evt);
                }
            });
            topPanel.add(rmtAskHttpsPort);

            jPanel10.add(topPanel, java.awt.BorderLayout.PAGE_START);

            jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

            jPanel6.setMinimumSize(new java.awt.Dimension(60, 30));
            jPanel6.setPreferredSize(new java.awt.Dimension(400, 155));
            jPanel6.setLayout(new java.awt.GridBagLayout());

            fileMgrMode.setText("File Mgr Mode");
            fileMgrMode.setMaximumSize(new java.awt.Dimension(125, 25));
            fileMgrMode.setMinimumSize(new java.awt.Dimension(125, 25));
            fileMgrMode.setPreferredSize(new java.awt.Dimension(125, 25));
            fileMgrMode.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileMgrModeActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            jPanel6.add(fileMgrMode, gridBagConstraints);

            startingFolder.setToolTipText("History: Alt-Left, Alt-Right (Mac: option-Left, option-Right)");
            startingFolder.setMaximumSize(new java.awt.Dimension(200, 26));
            startingFolder.setMinimumSize(new java.awt.Dimension(200, 26));
            startingFolder.setPreferredSize(new java.awt.Dimension(200, 26));
            startingFolder.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startingFolderActionPerformed(evt);
                }
            });
            startingFolder.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    startingFolderKeyTyped(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.insets = new java.awt.Insets(2, 5, 4, 7);
            jPanel6.add(startingFolder, gridBagConstraints);

            jLabel1.setText("Folder: ");
            jLabel1.setMaximumSize(new java.awt.Dimension(50, 27));
            jLabel1.setMinimumSize(new java.awt.Dimension(50, 27));
            jLabel1.setPreferredSize(new java.awt.Dimension(50, 27));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 0);
            jPanel6.add(jLabel1, gridBagConstraints);

            jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/Search-icon-16.png"))); // NOI18N
            jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            jButton1.setMargin(new java.awt.Insets(2, 14, 2, 0));
            jButton1.setMaximumSize(new java.awt.Dimension(40, 23));
            jButton1.setMinimumSize(new java.awt.Dimension(40, 23));
            jButton1.setPreferredSize(new java.awt.Dimension(40, 23));
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 9;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
            jPanel6.add(jButton1, gridBagConstraints);

            leftHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Actions-go-previous-icon-16.png"))); // NOI18N
            leftHistory.setMinimumSize(new java.awt.Dimension(30, 23));
            leftHistory.setPreferredSize(new java.awt.Dimension(30, 23));
            leftHistory.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    leftHistoryActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            jPanel6.add(leftHistory, gridBagConstraints);

            rightHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Actions-go-next-icon-16.png"))); // NOI18N
            rightHistory.setMinimumSize(new java.awt.Dimension(30, 23));
            rightHistory.setPreferredSize(new java.awt.Dimension(30, 23));
            rightHistory.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rightHistoryActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            jPanel6.add(rightHistory, gridBagConstraints);

            jTabbedPane1.setMinimumSize(new java.awt.Dimension(390, 0));
            jTabbedPane1.setPreferredSize(new java.awt.Dimension(500, 400));

            jPanel5.setLayout(new java.awt.GridBagLayout());

            showHiddenFilesFlag.setText("Show Hidden Files");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel5.add(showHiddenFilesFlag, gridBagConstraints);

            showJustFilenameFlag.setSelected(true);
            showJustFilenameFlag.setText("Show Just Filename");
            showJustFilenameFlag.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showJustFilenameFlagActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
            jPanel5.add(showJustFilenameFlag, gridBagConstraints);

            logLevel.setModel(new javax.swing.DefaultComboBoxModel(logLevelsArrStr));
            logLevelsLhm.put( Level.OFF.toString(), Level.OFF );
            logLevelsLhm.put( Level.INFO.toString(), Level.INFO );
            logLevelsLhm.put( Level.WARNING.toString(), Level.WARNING );
            logLevelsLhm.put( Level.FINE.toString(), Level.FINE );
            logLevelsLhm.put( Level.FINER.toString(), Level.FINER );
            logLevelsLhm.put( Level.FINEST.toString(), Level.FINEST );
            logLevelsLhm.put( Level.SEVERE.toString(), Level.SEVERE );
            logLevelsLhm.put( Level.ALL.toString(), Level.ALL );
            logLevel.setSelectedItem( Level.OFF.toString() );
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel5.add(logLevel, gridBagConstraints);

            jLabel10.setText("Log Level: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel5.add(jLabel10, gridBagConstraints);

            showOwnerFlag.setText("Show Owner");
            showOwnerFlag.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showOwnerFlagActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel5.add(showOwnerFlag, gridBagConstraints);

            showGroupFlag.setText("Show Group");
            showGroupFlag.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showGroupFlagActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel5.add(showGroupFlag, gridBagConstraints);

            showPermsFlag.setText("Show Permissions");
            showPermsFlag.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    showPermsFlagActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel5.add(showPermsFlag, gridBagConstraints);

            jTabbedPane1.addTab("View", jPanel5);

            jPanel4.setLayout(new java.awt.GridBagLayout());

            buttonGroup2.add(tabsLogicAndBtn);
            tabsLogicAndBtn.setSelected(true);
            tabsLogicAndBtn.setText("And");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel4.add(tabsLogicAndBtn, gridBagConstraints);

            buttonGroup2.add(tabsLogicOrBtn);
            tabsLogicOrBtn.setText("Or");
            tabsLogicOrBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    tabsLogicOrBtnActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel4.add(tabsLogicOrBtn, gridBagConstraints);

            jLabel8.setText("This is the logic between the conditions on each Tab.");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
            jPanel4.add(jLabel8, gridBagConstraints);

            jTabbedPane1.addTab("Logical between Tabs", jPanel4);

            jPanel1.setLayout(new java.awt.GridBagLayout());

            buttonGroup1.add(useGlobPattern);
            useGlobPattern.setText("Glob pattern");
            useGlobPattern.setMaximumSize(new java.awt.Dimension(900, 23));
            useGlobPattern.setMinimumSize(new java.awt.Dimension(90, 23));
            useGlobPattern.setPreferredSize(new java.awt.Dimension(110, 23));
            useGlobPattern.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    useGlobPatternActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel1.add(useGlobPattern, gridBagConstraints);

            buttonGroup1.add(useRegexPattern);
            useRegexPattern.setText("Regex pattern");
            useRegexPattern.setMaximumSize(new java.awt.Dimension(900, 23));
            useRegexPattern.setMinimumSize(new java.awt.Dimension(100, 23));
            useRegexPattern.setPreferredSize(new java.awt.Dimension(120, 23));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 0);
            jPanel1.add(useRegexPattern, gridBagConstraints);

            filePattern.setMinimumSize(new java.awt.Dimension(150, 26));
            filePattern.setPreferredSize(new java.awt.Dimension(300, 26));
            filePattern.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filePatternActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
            jPanel1.add(filePattern, gridBagConstraints);

            jLabel6.setText("          ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 2.0;
            jPanel1.add(jLabel6, gridBagConstraints);

            maxDepth.setMinimumSize(new java.awt.Dimension(40, 23));
            maxDepth.setPreferredSize(new java.awt.Dimension(40, 23));
            maxDepth.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    maxDepthActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel1.add(maxDepth, gridBagConstraints);

            jLabel2.setText("Max Depth:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel1.add(jLabel2, gridBagConstraints);

            jLabel4.setText("Min Depth:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel1.add(jLabel4, gridBagConstraints);

            minDepth.setMinimumSize(new java.awt.Dimension(40, 23));
            minDepth.setPreferredSize(new java.awt.Dimension(40, 23));
            minDepth.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    minDepthActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel1.add(minDepth, gridBagConstraints);

            showFilesFoldersCb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Files & Folders", "Files Only", "Folders Only", "Neither" }));
            showFilesFoldersCb.setMinimumSize(new java.awt.Dimension(120, 26));
            showFilesFoldersCb.setPreferredSize(new java.awt.Dimension(120, 26));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.2;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
            jPanel1.add(showFilesFoldersCb, gridBagConstraints);

            jLabel9.setText("What counts for a Match: Files and/or Folders: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 0);
            jPanel1.add(jLabel9, gridBagConstraints);

            jTabbedPane1.addTab("Name", jPanel1);

            jPanel2.setAlignmentX(0.0F);
            jPanel2.setAlignmentY(0.0F);
            jPanel2.setLayout(new java.awt.GridBagLayout());

            jLabel5.setText("          ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 2.0;
            jPanel2.add(jLabel5, gridBagConstraints);

            date1Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel2.add(date1Op, gridBagConstraints);

            dateLogicOp.setFocusable(false);
            dateLogicOp.setMinimumSize(new java.awt.Dimension(29, 25));
            dateLogicOp.setPreferredSize(new java.awt.Dimension(70, 25));
            dateLogicOp.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    dateLogicOpStateChanged(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel2.add(dateLogicOp, gridBagConstraints);

            date2Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            date2Op.setEnabled(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel2.add(date2Op, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel2.add(date1, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel2.add(date2, gridBagConstraints);

            jButton2.setText("clear");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel2.add(jButton2, gridBagConstraints);

            jTabbedPane1.addTab("Dates", jPanel2);

            jPanel3.setLayout(new java.awt.GridBagLayout());

            size2.setEnabled(false);
            size2.setMinimumSize(new java.awt.Dimension(6, 23));
            size2.setPreferredSize(new java.awt.Dimension(110, 23));
            size2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    size2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel3.add(size2, gridBagConstraints);

            size1Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel3.add(size1Op, gridBagConstraints);

            size2Op.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<", "<=", "=", "!=", ">", ">=" }));
            size2Op.setEnabled(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel3.add(size2Op, gridBagConstraints);

            jLabel7.setText("          ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.3;
            jPanel3.add(jLabel7, gridBagConstraints);

            sizeLogicOp.setFocusable(false);
            sizeLogicOp.setMinimumSize(new java.awt.Dimension(29, 25));
            sizeLogicOp.setPreferredSize(new java.awt.Dimension(70, 25));
            sizeLogicOp.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    sizeLogicOpStateChanged(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPanel3.add(sizeLogicOp, gridBagConstraints);

            size1.setMinimumSize(new java.awt.Dimension(6, 23));
            size1.setPreferredSize(new java.awt.Dimension(110, 23));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
            jPanel3.add(size1, gridBagConstraints);

            jLabel14.setText("Limit File Count: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            jPanel3.add(jLabel14, gridBagConstraints);

            jLabel15.setText("Limit Folder Count: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel3.add(jLabel15, gridBagConstraints);

            stopFileCount.setPreferredSize(new java.awt.Dimension(110, 23));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel3.add(stopFileCount, gridBagConstraints);

            stopFolderCount.setPreferredSize(new java.awt.Dimension(110, 23));
            stopFolderCount.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    stopFolderCountActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel3.add(stopFolderCount, gridBagConstraints);

            jLabel16.setText("File Size: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel3.add(jLabel16, gridBagConstraints);

            jTabbedPane1.addTab("Numbers", jPanel3);

            jPanel9.setLayout(new java.awt.GridBagLayout());

            myEditorLbl.setText("My Editor");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(myEditorLbl, gridBagConstraints);

            myEditorCmd.setMinimumSize(new java.awt.Dimension(300, 24));
            myEditorCmd.setPreferredSize(new java.awt.Dimension(300, 24));
            myEditorCmd.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    myEditorCmdActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel9.add(myEditorCmd, gridBagConstraints);

            jLabel11.setText("Start Console command: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 0.3;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(jLabel11, gridBagConstraints);

            startConsoleCmd.setMinimumSize(new java.awt.Dimension(300, 24));
            startConsoleCmd.setPreferredSize(new java.awt.Dimension(300, 24));
            startConsoleCmd.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startConsoleCmdActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel9.add(startConsoleCmd, gridBagConstraints);

            jLabel12.setText("StdOut Log File: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 0.3;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(jLabel12, gridBagConstraints);

            jLabel13.setText("StdErr Log File: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(jLabel13, gridBagConstraints);

            stdOutFile.setMinimumSize(new java.awt.Dimension(300, 24));
            stdOutFile.setPreferredSize(new java.awt.Dimension(300, 24));
            stdOutFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    stdOutFileActionPerformed(evt);
                }
            });
            stdOutFile.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    stdOutFilePropertyChange(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel9.add(stdOutFile, gridBagConstraints);

            stdErrFile.setMinimumSize(new java.awt.Dimension(300, 24));
            stdErrFile.setPreferredSize(new java.awt.Dimension(300, 24));
            stdErrFile.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    stdErrFilePropertyChange(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel9.add(stdErrFile, gridBagConstraints);

            stopFileWatchTb.setText("Auto");
            stopFileWatchTb.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    stopFileWatchTbActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            jPanel9.add(stopFileWatchTb, gridBagConstraints);

            fileWatchLbl.setText("Watch File Changes:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            jPanel9.add(fileWatchLbl, gridBagConstraints);

            dumpThreadListToLog.setText("Dump Thread List to Log");
            dumpThreadListToLog.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    dumpThreadListToLogActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
            jPanel9.add(dumpThreadListToLog, gridBagConstraints);

            webSiteLink.setText("Web Site");
            webSiteLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            Font font = webSiteLink.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            webSiteLink.setFont(font.deriveFont(attributes));
            webSiteLink.addMouseListener(new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent e)
                    {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            desktop.browse( new URI( "https://github.com/stant/jfileprocessorRest" ) );
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 0;
                jPanel9.add(webSiteLink, gridBagConstraints);

                reportIssueLbl.setText("Report Issue");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 1;
                jPanel9.add(reportIssueLbl, gridBagConstraints);

                testSshBtn.setText("test ssh");
                testSshBtn.setEnabled(false);
                testSshBtn.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        testSshBtnActionPerformed(evt);
                    }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 0;
                jPanel9.add(testSshBtn, gridBagConstraints);

                jTabbedPane1.addTab("Settings", jPanel9);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 4.0;
                jPanel6.add(jTabbedPane1, gridBagConstraints);
                jTabbedPane1.getAccessibleContext().setAccessibleName("Name");

                jSplitPane1.setLeftComponent(jPanel6);

                jPanel7.setMinimumSize(new java.awt.Dimension(60, 90));
                jPanel7.setPreferredSize(new java.awt.Dimension(500, 400));
                jPanel7.setLayout(new java.awt.GridBagLayout());

                searchBtn.setText("Search");
                searchBtn.setMaximumSize(new java.awt.Dimension(120, 25));
                searchBtn.setMinimumSize(new java.awt.Dimension(120, 25));
                searchBtn.setPreferredSize(new java.awt.Dimension(120, 25));
                searchBtn.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        searchBtnActionPerformed(evt);
                    }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
                jPanel7.add(searchBtn, gridBagConstraints);

                jLabel3.setText("Files in Table:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
                jPanel7.add(jLabel3, gridBagConstraints);

                filesTbl.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {
                        {0, 0, "", new Date(), 0, "", "", ""}
                    },
                    new String [] {
                        "Type", "Dir", "File", "last Modified Time", "Size", "Owner", "Group", "Perms"

                    }
                ));
                jScrollPane1.setViewportView(filesTbl);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weighty = 1.0;
                jPanel7.add(jScrollPane1, gridBagConstraints);

                processStatus.setText(" ");
                processStatus.setMaximumSize(new java.awt.Dimension(999, 23));
                processStatus.setMinimumSize(new java.awt.Dimension(115, 23));
                processStatus.setOpaque(true);
                processStatus.setPreferredSize(new java.awt.Dimension(140, 25));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.gridwidth = 2;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
                jPanel7.add(processStatus, gridBagConstraints);

                message.setText(" ");
                message.setMaximumSize(new java.awt.Dimension(4, 25));
                message.setMinimumSize(new java.awt.Dimension(4, 25));
                message.setPreferredSize(new java.awt.Dimension(4, 25));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 0.5;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
                jPanel7.add(message, gridBagConstraints);

                numFilesInTable.setText("fffffffff");
                numFilesInTable.setMaximumSize(new java.awt.Dimension(100, 26));
                numFilesInTable.setMinimumSize(new java.awt.Dimension(100, 26));
                numFilesInTable.setPreferredSize(new java.awt.Dimension(100, 26));
                jPanel7.add(numFilesInTable, new java.awt.GridBagConstraints());

                upFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow/Folder-Upload-icon-16.png"))); // NOI18N
                upFolder.setText("Up");
                upFolder.setMaximumSize(new java.awt.Dimension(73, 25));
                upFolder.setMinimumSize(new java.awt.Dimension(73, 25));
                upFolder.setPreferredSize(new java.awt.Dimension(73, 25));
                upFolder.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        upFolderActionPerformed(evt);
                    }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
                jPanel7.add(upFolder, gridBagConstraints);

                countBtn.setText("Count");
                countBtn.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        countBtnActionPerformed(evt);
                    }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
                jPanel7.add(countBtn, gridBagConstraints);

                logsBtn.setText("logs");
                logsBtn.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        logsBtnActionPerformed(evt);
                    }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 12;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
                gridBagConstraints.weightx = 0.3;
                gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
                jPanel7.add(logsBtn, gridBagConstraints);

                fsTypeLbl.setText("       ");
                fsTypeLbl.setMaximumSize(new java.awt.Dimension(70, 23));
                fsTypeLbl.setMinimumSize(new java.awt.Dimension(70, 23));
                fsTypeLbl.setPreferredSize(new java.awt.Dimension(70, 23));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 13;
                gridBagConstraints.gridy = 0;
                jPanel7.add(fsTypeLbl, gridBagConstraints);

                newerVersionLbl.setMaximumSize(new java.awt.Dimension(200, 23));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 14;
                gridBagConstraints.gridy = 0;
                jPanel7.add(newerVersionLbl, gridBagConstraints);

                newerVersionSpLbl.setMaximumSize(new java.awt.Dimension(150, 23));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 15;
                gridBagConstraints.gridy = 0;
                jPanel7.add(newerVersionSpLbl, gridBagConstraints);

                jSplitPane1.setRightComponent(jPanel7);

                jPanel10.add(jSplitPane1, java.awt.BorderLayout.CENTER);

                botPanel.setPreferredSize(new java.awt.Dimension(10, 1));
                jPanel10.add(botPanel, java.awt.BorderLayout.PAGE_END);

                jSplitPane2.setRightComponent(jPanel10);

                getContentPane().add(jSplitPane2, java.awt.BorderLayout.CENTER);

                pack();
            }// </editor-fold>//GEN-END:initComponents

    private void startingFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startingFolderActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_startingFolderActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        countOnlyFlag = false;
        searchBtnAction( evt );
    }//GEN-LAST:event_searchBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "Select Starting Search Folder" );
        if ( startingFolder.getText().trim().equals( "" ) )
            {
            chooser.setCurrentDirectory( new java.io.File(".") );
            }
        else
            {
            chooser.setCurrentDirectory( new java.io.File( startingFolder.getText().trim() ) );
            }
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        File selectedFile = chooser.getSelectedFile();
        //Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        //String[] tt = { selectedFile.getPath() };
        startingFolder.setText( selectedFile.getPath() );
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void savePathsToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePathsToFileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled( true );
        chooser.setDialogTitle( "File to Save To" );
        if ( startingFolder.getText().trim().equals( "" ) )
            {
            chooser.setCurrentDirectory( new java.io.File(".") );
            }
        else
            {
            chooser.setCurrentDirectory( new java.io.File( startingFolder.getText().trim() ) );
            }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //
        // disable the "All files" option.
        //
        //chooser.setAcceptAllFileFilterUsed(false);
    
    if ( chooser.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        savePathsToFile( chooser.getSelectedFile() );
        }

    }//GEN-LAST:event_savePathsToFileActionPerformed

    private void useGlobPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useGlobPatternActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useGlobPatternActionPerformed

    private void tabsLogicOrBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabsLogicOrBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tabsLogicOrBtnActionPerformed

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
//        File selectedPath = new File( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
        System.out.println( "selected row String =" + (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
//        System.out.println( "selected row file =" + selectedPath );
        desktopOpen( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
    }//GEN-LAST:event_openFileActionPerformed

    private void myEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myEditActionPerformed
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
//        File selectedPath = new File( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
        //System.out.println( "selected row file =" + selectedPath );
        desktopEdit( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
    }//GEN-LAST:event_myEditActionPerformed

    private void copyFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyFilenameActionPerformed
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        String selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
        //System.out.println( "selected row file =" + selectedPath );
        StringSelection stringSelection = new StringSelection ( selectedPath );
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents (stringSelection, null);
    }//GEN-LAST:event_copyFilenameActionPerformed

    private void sizeLogicOpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeLogicOpStateChanged
        CyclingSpinnerListModel mod = (CyclingSpinnerListModel) sizeLogicOp.getModel();
        //System.out.println( "selected spinner value =" + ((String)mod.getValue()).trim() + "=" );
        if ( ((String)mod.getValue()).trim().equals( "" ) )
            {
            size2.setEnabled( false );
            size2Op.setEnabled( false );
            }
        else
            {
            size2.setEnabled( true );
            size2Op.setEnabled( true );
            }

    }//GEN-LAST:event_sizeLogicOpStateChanged

    private void filePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filePatternActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_filePatternActionPerformed

    private void size2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_size2ActionPerformed

    private void dateLogicOpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dateLogicOpStateChanged
        CyclingSpinnerListModel mod = (CyclingSpinnerListModel) dateLogicOp.getModel();
        //System.out.println( "selected spinner value =" + ((String)mod.getValue()).trim() + "=" );
        if ( ((String)mod.getValue()).trim().equals( "" ) )
            {
            date2.setMyEnabled( false );
            date2Op.setEnabled( false );
            }
        else
            {
            date2.setMyEnabled( true );
            date2Op.setEnabled( true );
            }

    }//GEN-LAST:event_dateLogicOpStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        date1.getModel().setValue( null );
        date2.getModel().setValue( null );
        dateLogicOp.setValue( "" );
    }//GEN-LAST:event_jButton2ActionPerformed

    private void upFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upFolderActionPerformed
//        Path path = Paths.get( startingFolder.getText().trim() );
//        int elements = path.getNameCount();
//        if ( elements > 1 )
//            {
//            startingFolder.setText( path.getRoot().toString() + path.subpath( 0, elements - 1).toString() );
//            }
//        else if ( elements > 0 )
//            {
//            startingFolder.setText( path.getRoot().toString() );
//            }
        String tmp = startingFolder.getText().trim();
        int at = tmp.endsWith( "\\" ) || tmp.endsWith( "/" ) ? tmp.length() - 2 : tmp.length() - 1;
        while ( at >= 0 )
            {
            if ( tmp.charAt( at ) == '/' || tmp.charAt( at ) == '\\' )
                {
                System.out.println( "found / or \\ at =" + at );
                break;
                }
            at --;
            }
        System.out.println( "end with at =" + at );
        if ( at >= 0 )
            {
            startingFolder.setText( tmp.substring( 0, at + 1 ) );
            }
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_upFolderActionPerformed

    private void backwardFolderActionPerformed(java.awt.event.ActionEvent evt) {                                         
        startingFolder.setText( pathsHistoryList.getBackward() );
//        searchBtnActionPerformed( null );
    }                                        

    private void forwardFolderActionPerformed(java.awt.event.ActionEvent evt) {                                         
            System.out.println( "forwardFolderActionPerformed" );
        startingFolder.setText( pathsHistoryList.getForward() );
//        searchBtnActionPerformed( null );
    }                                        

    private void fileMgrModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMgrModeActionPerformed
        alreadySetColumnSizes = false;
        if ( fileMgrMode.isSelected() )
            {
            fileMgrMode.setText( "File Mgr Mode" );
            minDepth.setText( "1" );
            maxDepth.setText( "1" );
            showJustFilenameFlag.setSelected( true );
            showFilesFoldersCb.setSelectedItem( SHOWFILESFOLDERSCB_BOTH );
            jSplitPane1.setDividerLocation( jSplitPane1.getMinimumDividerLocation() );
            filePattern.setText( "" );
            jPanel6.setBackground( Color.LIGHT_GRAY );
            }
        else
            {
            fileMgrMode.setText( "Search Mode" );
            minDepth.setText( "" );
            maxDepth.setText( "" );
            showJustFilenameFlag.setSelected( false );
            showFilesFoldersCb.setSelectedItem( SHOWFILESFOLDERSCB_FILES_ONLY );
            System.out.println( "jSplitPane1.getLastDividerLocation() =" + jSplitPane1.getLastDividerLocation() );

            if ( jSplitPane1.getLastDividerLocation() < 0 )
                {
                jSplitPane1.setDividerLocation( 170 );  // 150
                }
            else
                {
                jSplitPane1.setDividerLocation( jSplitPane1.getLastDividerLocation() );
                }
            jPanel6.setBackground( Color.ORANGE );
            }
    }//GEN-LAST:event_fileMgrModeActionPerformed

    private void startingFolderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startingFolderKeyTyped
                // TODO add your handling code here:
    }//GEN-LAST:event_startingFolderKeyTyped

    private void CopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CopyActionPerformed

        isDoingCutFlag = false;  // copy or cut starting?
        copyOrCut();
    }//GEN-LAST:event_CopyActionPerformed

    private void PasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasteActionPerformed
        try {
            System.out.println( "PasteActionPerformed" );
            String selectedPath = startingFolder.getText().trim();
            if ( filesTbl.getSelectedRow() >= 0 )
                {
                int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
                if ( rowIndex >= 0 )
                    {
                    selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
                    }
                if ( selectedPath.equals( "" ) || ! Files.isDirectory( Paths.get( selectedPath ) ) )
                    {
                    selectedPath = startingFolder.getText().trim();
                    }
                }
            CopyFrame copyFrame = new CopyFrame();
        // DESIGN NOTE:  first file on clipboard is starting/from path ! - 2nd is word cut or copy, 3rd is sftp://..login.. or file://
            //copyPaths = getClipboardFilesList();
            ArrayList<String> StringsList = getClipboardStringsList( "\\?" );
    
            if ( StringsList == null || StringsList.size() < 8 )
                {
                JOptionPane.showMessageDialog( this, "No Files selected to Copy.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }
            copyPathStartPath = StringsList.remove( 0 );
            String tmp = StringsList.remove( 0 );
            isDoingCutFlag = tmp.equalsIgnoreCase( "CUT" ) ? true : false;
            System.out.println( "read clipboard isDoingCutFlag =" + isDoingCutFlag );
            connUserInfo.setFromFilesysType( Integer.parseInt( StringsList.remove( 0 ) ) );
            System.out.println( "connUserInfo.getFromFilesysType() =" + connUserInfo.getFromFilesysType() ); 
            connUserInfo.setToFilesysType( filesysType );
            System.out.println( "connUserInfo.getToFilesysType() =" + connUserInfo.getToFilesysType() );
            connUserInfo.setFrom( StringsList.remove( 0 ), StringsList.remove( 0 ), StringsList.remove( 0 ), StringsList.remove( 0 ), StringsList.remove( 0 ) );
            System.out.println( "connUserInfo before set for copy:" + connUserInfo );
            if ( ! connUserInfo.isConnectedFlag() )
                {
                connUserInfo.setTo( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "", getRmtAskHttpsPort() );
                }
//            else
//                {
//                connUserInfo.setTo( Constants.PATH_PROTOCOL_SFTP, getRmtUser(), getRmtPasswd(), getRmtHost(), getRmtSshPort(), getRmtAskHttpsPort() );
//                }
            //System.out.println( "connUserInfo After set for copy:" + connUserInfo );
            
            if ( connUserInfo.getFromProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
                 connUserInfo.getToProtocol().equals( Constants.PATH_PROTOCOL_FILE ) &&
                 ! connUserInfo.getFromHost().equals( connUserInfo.getToHost() )   )
                {
                JOptionPane.showMessageDialog( this, "Cannot make a local copy between different systems/hosts.\nPlease use sftp", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }
            copyPaths.clear();
            for ( String fpath : StringsList )
                {
                copyPaths.add( fpath );
                System.out.println( "read clipboard fpath =" + fpath );
                }
            //if ( 1 == 1 ) return;
            copyFrame.setup( this, connUserInfo, isDoingCutFlag, copyPathStartPath, copyPaths, selectedPath );
            copyFrame.pack();
            copyFrame.setVisible( true );
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            }                     
    }//GEN-LAST:event_PasteActionPerformed

    private void Paste1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Paste1ActionPerformed
        PasteActionPerformed(evt);
    }//GEN-LAST:event_Paste1ActionPerformed

    private void savePathsToFile1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePathsToFile1ActionPerformed
        savePathsToFileActionPerformed(evt);
    }//GEN-LAST:event_savePathsToFile1ActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed
        
        if ( filesTbl.getSelectedRow() < 0 )
            {
            JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        
        deletePaths = new ArrayList<String>();
        //ArrayList<File> filesList = new ArrayList<File>();

        // Do not use clipboard for deletes. only allow per this app instance!
        copyPathStartPath = startingFolder.getText().trim();
        //filesList.add( new File( copyPathStartPath ) );

        System.out.println( "delete add selected paths:" );
        for( int row : filesTbl.getSelectedRows() )
            {
            int rowIndex = filesTbl.convertRowIndexToModel( row );
            //System.out.println( "add copy path  row =" + row + "   rowIndex = " + rowIndex );
            //System.out.println( "copy path  =" + ((String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) + "=" );
//            copyPaths.add( Paths.get( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
            deletePaths.add( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
            //System.out.println( "add filesList =" + copyPaths.get( copyPaths.size() - 1 ) );
            }   

        try {
            DeleteFrame deleteFrame = new DeleteFrame();
            if ( deletePaths == null || deletePaths.size() < 1 )
                {
                JOptionPane.showMessageDialog( this, "No Files selected to Delete.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }

            if ( evt != null  ) System.out.println( "evt.getModifiers() =" + evt.getModifiers() + "   ((evt.getModifiers() & InputEvent.SHIFT_MASK) != 0) = " + ((evt.getModifiers() & InputEvent.SHIFT_MASK) != 0) );
            if ( evt != null && (evt.getModifiers() & InputEvent.SHIFT_MASK) != 0 )
                {
                deleteFrame.setDeleteToTrashFlag( false );
                }
//            connUserInfo.setFrom( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "" );
//            if ( connUserInfo.isConnectedFlag() )
//                {
//                connUserInfo.setTo( Constants.PATH_PROTOCOL_SFTP, getRmtUser(), getRmtPasswd(), getRmtHost(), getRmtSshPort(), getRmtAskHttpsPort() );
//                }
//            else
//                {
//                connUserInfo.setTo( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "", getRmtAskHttpsPort() );
//                }
            deleteFrame.setup( this, connUserInfo, copyPathStartPath, deletePaths );
            deleteFrame.pack();
            deleteFrame.setVisible( true );
            } 
        catch (Exception ex) 
            {
            logger.log(Level.SEVERE, null, ex);
            } 
    }//GEN-LAST:event_DeleteActionPerformed

    private void NewFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewFolderActionPerformed
        //stopDirWatcher();
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        System.out.println( "filesTbl.getRowCount() = " + filesTbl.getRowCount() + "   filesTblModel.getColumnCount()  = " + filesTblModel.getColumnCount() );
        if ( filesTbl.getRowCount() < 2 && filesTblModel.getColumnCount() < 2 )
            {
            System.out.println( "call newfolderOnlyFilesTableModel( startingFolder.getText().trim() + \"New Folder\" )" );
            filesTblModel = jfilefinder.newfolderOnlyFilesTableModel( startingFolder.getText().trim() + "New Folder" );
            fillInFilesTable( filesTblModel );
            filesTblCellListener.skipFirstEditOn( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, startingFolder.getText().trim() + "New Folder" );
            filesTblModel.replaceRowAt( 0, startingFolder.getText().trim() + "New Folder" );
            }
        else
            {
            filesTblCellListener.skipFirstEditOn( FilesTblModel.FILESTBLMODEL_FOLDERTYPE, startingFolder.getText().trim() + "New Folder" );
            filesTblModel.insertRowAt( 0, startingFolder.getText().trim() + "New Folder" );
            }
            
        setColumnSizes();
//        filesTbl.setCellSelectionEnabled( true );
        filesTblModel.setCellEditable( 0, FilesTblModel.FILESTBLMODEL_PATH, true );
        //filesTbl.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
        //filesTbl.requestFocus();
        
//        filesTbl.editCellAt( 0, FilesTblModel.FILESTBLMODEL_PATH ); 
//        filesTbl.setSurrendersFocusOnKeystroke( true );	
//        filesTbl.transferFocus();
        filesTbl.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
//        filesTbl.getEditorCo‌​mponent().requestFocus();
        JTextComponent textComp = (JTextComponent) filesTbl.getEditorCo‌​mponent();
        int len = (startingFolder.getText().trim() + "New Folder").length();
        textComp.select( len - 10, len );
        //filesTbl.setCellSelectionEnabled( false );
        // Enhancement - make it go into new folder after change name !
    }//GEN-LAST:event_NewFolderActionPerformed

    private void RenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RenameActionPerformed
        //System.out.println( "RenameActionPerformed evt.getSource() =" + evt.getSource() );
        if ( filesTbl.getSelectedRow() < 0 )
            {
            JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        System.out.println( "rename filesTbl.getSelectedRow() =" + filesTbl.getSelectedRow() + "   rowIndex = " + rowIndex );
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        String selectedPath = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
        System.out.println( "rename selectedPath =" + selectedPath + "   rowIndex = " + rowIndex );

//        filesTbl.setCellSelectionEnabled( true );
        filesTblModel.setCellEditable( rowIndex, FilesTblModel.FILESTBLMODEL_PATH, true );
        filesTbl.changeSelection( filesTbl.getSelectedRow(), FilesTblModel.FILESTBLMODEL_PATH, false, false );        
        
        JTextComponent textComp = (JTextComponent) filesTbl.getEditorCo‌​mponent();
        int len = selectedPath.length();
        textComp.select( selectedPath.lastIndexOf( System.getProperty( "file.separator") ) + 1, len );
    }//GEN-LAST:event_RenameActionPerformed

    private void EnterActionPerformed(java.awt.event.ActionEvent evt) {                                       
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        //System.out.println( "converted rowIndex =" + rowIndex );
        String selectedPath = (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
//        Boolean isDir = (Boolean) filesTbl.getModel().getValueAt(rowIndex, FilesTblModel.FILESTBLMODEL_FOLDERTYPE );
        int folderType = (Integer) filesTbl.getModel().getValueAt(rowIndex, FilesTblModel.FILESTBLMODEL_FOLDERTYPE );
        //System.out.println( "selected row file =" + selectedPath );
//        System.out.println( "EnterActionPerformed selected folderType =" + folderType );
        if ( folderType == FilesTblModel.FOLDERTYPE_FOLDER ) // skipping no access folder for now !
            {
            this.setStartingFolder( selectedPath );
            //this.callSearchBtnActionPerformed( null );
            searchBtn.doClick();
            }
        else if ( folderType == FilesTblModel.FOLDERTYPE_FILE )
            {
            this.desktopOpen( selectedPath );
            }        
    }                                      

    private void showJustFilenameFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showJustFilenameFlagActionPerformed
        alreadySetColumnSizes = false;
    }//GEN-LAST:event_showJustFilenameFlagActionPerformed

    private void CutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CutActionPerformed
        
        isDoingCutFlag = true;  // copy or cut starting?
        copyOrCut();
    }//GEN-LAST:event_CutActionPerformed

    private void minDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minDepthActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_minDepthActionPerformed

    private void maxDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDepthActionPerformed
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_maxDepthActionPerformed

    private void countBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countBtnActionPerformed
        countOnlyFlag = true;
        searchBtnAction( evt );
    }//GEN-LAST:event_countBtnActionPerformed

    private void savedPathsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_savedPathsListMouseClicked
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        int index = savedPathsList.getSelectedIndex();
        String strPath = listModel.getElementAt(index).toString();
        if ( strPath.equals( "New Window" ) )
            {
            try {
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, true );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
            } catch (IOException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
            }
        else if ( strPath.equals( "Trash" ) )
            {
            try {
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, true, DesktopUtils.getTrashFolder().toString() );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
            } catch (IOException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
            }
        startingFolder.setText( savedPathReplacablePanel.getSavedPathsHm().get( strPath ) );
        searchBtnActionPerformed( null );
    }//GEN-LAST:event_savedPathsListMouseClicked

    private void deletePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePathActionPerformed
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        int index = savedPathsList.getSelectedIndex();
        String strPath = listModel.getElementAt(index).toString();
        System.out.println( "delete path() index =" + index + "   element =" + strPath + "=" );
        if ( strPath.equals( "New Window" ) || strPath.equals( "Trash" ) )
            {
            return;
            }
        savedPathReplacablePanel.getSavedPathsHm().remove( listModel.getElementAt(index).toString() );
        listModel.remove( index );
        
    }//GEN-LAST:event_deletePathActionPerformed

    private void addPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPathActionPerformed
        DefaultListModel listModel = (DefaultListModel) savedPathReplacablePanel.getSavedPathsList().getModel();
        System.out.println( "add path() startingFolder.getText() =" + startingFolder.getText() + "=" );
        String ans = JOptionPane.showInputDialog( "Name: ", Paths.get( startingFolder.getText() ).getFileName() );
        if ( ans == null )
            {
            return;
            }
        savedPathReplacablePanel.getSavedPathsHm().put( ans, startingFolder.getText() );
        System.out.println( "savedPathReplacablePanel.getSavedPathsHm().size() =" + savedPathReplacablePanel.getSavedPathsHm().size() + "=" );
        listModel.addElement( ans );
    }//GEN-LAST:event_addPathActionPerformed

    private void openListWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openListWindowActionPerformed
        try
            {
            ListOfFilesPanel listOfFilesPanel = getListOfFilesPanel( null );
            if ( listOfFilesPanel == null )
                {
                return;
                }
            int maxRows = filesTbl.getRowCount();
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();

            DefaultComboBoxModel listModel = (DefaultComboBoxModel) listOfFilesPanel.getModel();

            Object[] options = {"all files and folders in this window",
                    "selected files/folders",
                    "none"};

            int defSelect = filesTbl.getSelectedRowCount() > 0 ? 1 : 0;

            int reply = JOptionPane.showOptionDialog( this,
                "What do you want to add to your list ?",
                "Populate List",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[defSelect] );
//            int reply = JOptionPane.showConfirmDialog( null, "Add current files into list ? ", "List", JOptionPane.YES_NO_OPTION);
            System.out.println( "reply =" + reply + "=" );
            int numChanged = 0;
            if ( reply == JOptionPane.YES_OPTION )  // all files and folders
                {
                openPathsToList( listOfFilesPanel, null );
                }
            else if ( reply == JOptionPane.NO_OPTION )  // ADD selected Fitems
                {
                if ( filesTbl.getSelectedRow() < 0 )
                    {
                    JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
                    return;
                    }

                System.out.println( "filesTbl.getSelectedRowCount() = " + filesTbl.getSelectedRowCount() );
                for( int row : filesTbl.getSelectedRows() )
                    {
//                    System.out.println( "row = " + row );
                    int rowIndex = filesTbl.convertRowIndexToModel( row );
                    String str = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
//                    System.out.println( "add copy path  row =" + row + "   rowIndex = " + rowIndex );
                    int foundAt = listModel.getIndexOf( str );
                    if ( foundAt < 0 )
                        {
//                        System.out.println( "add fpath =" + str + "=" );
                        listModel.addElement( str );
                        numChanged ++;
                        }
                    }
//                JOptionPane.showMessageDialog( this, "Number of files added: " + numChanged );
                }
            listOfFilesPanel.setCount();
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }

    }//GEN-LAST:event_openListWindowActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        saveBookmarks();
        
        try
            {
            programMemory.extractCurrentValues();
            Rest.saveObjectToFile( "ProgramMemory.json", programMemory );
            Rest.saveObjectToFile( "FileAssocList.json", fileAssocList );

            if ( stdOutFile.getText() != null && ! stdOutFile.getText().equals( "" ) )
                {
                Files.deleteIfExists( Paths.get( stdOutFile.getText() ) );
                }
            if ( stdErrFile.getText() != null && ! stdErrFile.getText().equals( "" ) )
                {
                Files.deleteIfExists( Paths.get( stdErrFile.getText() ) );
                }
            }
        catch( Exception ex )
            {
            ex.printStackTrace();
            }
    }//GEN-LAST:event_formWindowClosing

    private void startCmdWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCmdWinActionPerformed
        try {
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();

            System.out.println( "first path = " + (String) filesTblModel.getValueAt( 0, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
            if ( filesTbl.getSelectedRows().length < 1  ||
                 ((String) filesTblModel.getValueAt( 0, FilesTblModel.FILESTBLMODEL_PATH )).trim().equals( "") )
                {
                System.out.println( "javaprocess.exec start new window getStartingFolder() =" + getStartingFolder() + "=   startConsoleCmd.getText() =" + startConsoleCmd.getText() + "=" );
    //            int rc = 0;
    //            rc = JavaProcess.exec( getStartingFolder(), startConsoleCmd.getText() );
                ProcessInThread jp = new ProcessInThread();
                int rc = jp.exec( getStartingFolder(), true, false, startConsoleCmd.getText() );
                System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
                }
            else
                {
                for( int row : filesTbl.getSelectedRows() )
                    {
                    int rowIndex = filesTbl.convertRowIndexToModel( row );
                    File file = new File( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
                    ProcessInThread jp = new ProcessInThread();
                    int rc = jp.exec( file.getParent(), true, false, startConsoleCmd.getText() );
                    System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
                    }            
                }
            } 
        catch (IOException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            } 
        catch (InterruptedException ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_startCmdWinActionPerformed

    private void startConsoleCmdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startConsoleCmdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startConsoleCmdActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
//        System.out.println( "Entered formWindowOpened()" );
//        System.err.println( "Entered formWindowOpened()" );
        setColumnRenderers();
        
        if ( startConsoleCmd.getText() == null || startConsoleCmd.getText().equals( "" ) )
            {
            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
                {
                startConsoleCmd.setText( "/usr/bin/open -n -a /Applications/Utilities/Terminal.app" );
                }
            else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
                {
                startConsoleCmd.setText( "cmd.exe /C start" );
                }
            else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "linux" ) )
                {
                if ( Files.exists( Paths.get( "/bin/konsole" ) ) )
                    {
                    startConsoleCmd.setText( "/bin/konsole" );
                    }
                else if ( Files.exists( Paths.get( "/usr/bin/gnome-terminal" ) ) )
                    {
                    startConsoleCmd.setText( "/usr/bin/gnome-terminal" );
                    }
                else if ( Files.exists( Paths.get( "/usr/bin/xterm" ) ) )
                    {
                    startConsoleCmd.setText( "/usr/bin/xterm" );
                    }
                }
            }
        if ( myEditorCmd.getText() == null || myEditorCmd.getText().equals( "" ) )
            {
            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
                {
                myEditorCmd.setText( "/usr/bin/open -n -a /Applications/TextEdit.app" );
                }
            else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
                {
                myEditorCmd.setText( "wordpad.exe" );
                }
            else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "linux" ) )
                {
                if ( Files.exists( Paths.get( "/bin/kwrite" ) ) )
                    {
                    myEditorCmd.setText( "/bin/kwrite" );
                    }
                else if ( Files.exists( Paths.get( "/usr/bin/gedit" ) ) )
                    {
                    myEditorCmd.setText( "/usr/bin/gedit" );
                    }
                }
            }
        
//        System.out.println( "leaving formWindowOpened()" );
//        System.err.println( "leaving formWindowOpened()" );
    }//GEN-LAST:event_formWindowOpened

    private void startCmdWin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCmdWin1ActionPerformed
        startCmdWinActionPerformed( null );
    }//GEN-LAST:event_startCmdWin1ActionPerformed

    private void stdOutFilePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stdOutFilePropertyChange
        try {
//            System.out.println( "stdOutFilePropertyChange() do nothing for now !" );
//            if ( 1 == 1 ) return;
            
            System.out.println( "stdOutFilePropertyChange() set to file =" + stdOutFile.getText() + "=" );
            if ( stdOutFile.getText() != null && ! stdOutFile.getText().equals( "" ) )
                {
                System.setOut(new PrintStream(new File( stdOutFile.getText() ) ) );
                }
            else
                {
                    //  should go to stderr but oh well for now
                System.setOut( console );
                }
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            }

    }//GEN-LAST:event_stdOutFilePropertyChange

    private void stdErrFilePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_stdErrFilePropertyChange
        try {
//            System.out.println( "stdErrFilePropertyChange() do nothing for now !" );
//            if ( 1 == 1 ) return;
            
            System.out.println( "stdErrFilePropertyChange() set to file =" + stdErrFile.getText() + "=" );
            if ( stdErrFile.getText() != null && ! stdErrFile.getText().equals( "" ) )
                {
                System.setErr(new PrintStream(new File( stdErrFile.getText() ) ) );
                }
            else
                {
                    //  should go to stderr but oh well for now
                System.setErr( console );
                }
            } 
        catch (Exception e) 
            {
            e.printStackTrace();
            }

    }//GEN-LAST:event_stdErrFilePropertyChange

    private void openCodeWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCodeWinActionPerformed
        try
            {
            int maxRows = filesTbl.getRowCount();
            boolean openedFlag = false;
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            
            //loop for jtable rows
            for( int row : filesTbl.getSelectedRows() )
                {
                int rowIndex = filesTbl.convertRowIndexToModel( row );
                //System.out.println( "add copy path  row =" + row + "   rowIndex = " + rowIndex );
                //System.out.println( "copy path  =" + ((String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) + "=" );
                //copyPaths.add( Paths.get( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
                //filesList.add( new File( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) ) );
                String selectedPath = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
                System.out.println( "open code fpath =" + (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
                
                if ( selectedPath.toUpperCase().endsWith( ".GROOVY" ) )
                    {
                    openCodeWinPanel( this, selectedPath, null );
                    openedFlag = true;
                    }
                }
            if ( ! openedFlag )
                {
                openCodeWinPanel( this, null, null ).setTitle( "code" );
                }
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }

    }//GEN-LAST:event_openCodeWinActionPerformed

    private void stdOutFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdOutFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stdOutFileActionPerformed

    private void rightHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightHistoryActionPerformed
        forwardFolderActionPerformed( null );
    }//GEN-LAST:event_rightHistoryActionPerformed

    private void leftHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftHistoryActionPerformed
        backwardFolderActionPerformed( null );
    }//GEN-LAST:event_leftHistoryActionPerformed

    private void stopFolderCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopFolderCountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stopFolderCountActionPerformed

    private void showOwnerFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOwnerFlagActionPerformed
        alreadySetColumnSizes = false;
        if ( isShowOwnerFlag() )
            {
            showGroupFlag.setSelected( true );
            showPermsFlag.setSelected( true );
            }
        else
            {
            showGroupFlag.setSelected( false );
            showPermsFlag.setSelected( false );
            }
    }//GEN-LAST:event_showOwnerFlagActionPerformed

    private void readFileIntoListWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readFileIntoListWinActionPerformed
        ListOfFilesPanel listOfFilesPanel = getListOfFilesPanel( null );
        if ( listOfFilesPanel == null )
            {
            return;
            }
        int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
        File selectedPath = new File( (String) filesTbl.getModel().getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
//        System.out.println( "selected row file =" + selectedPath );
        listOfFilesPanel.readFile( selectedPath );
    }//GEN-LAST:event_readFileIntoListWinActionPerformed

    private void scriptsMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_scriptsMenuMenuSelected
        
        System.out.println( "entered scriptsMenuMenuSelected" );
        scriptsMenu.removeAll();
        ArrayList<File> files = new ArrayList<File>(Arrays.asList( scriptsFile.listFiles() ));
        files.addAll( new ArrayList<File>(Arrays.asList( scriptsOsFile.listFiles() ) ) );
        if ( files == null )   return;
         
        for ( File file : files ) 
            {
            System.out.println(file.getName());
            if ( file.toString().endsWith( ".groovy" ) )
                {
                JMenuItem menuItem = null;
                if ( file.getName().equals( "runCommandOnSelectedFiles.groovy" ) )
                    menuItem = new JMenuItem( file.getName().substring( 0, file.getName().length() - 7 ) + "   (Alt-X)", null );
                else
                    menuItem = new JMenuItem( file.getName().substring( 0, file.getName().length() - 7 ), null );
                ScriptMenuItemListener listener = new ScriptMenuItemListener( this, file.getAbsolutePath().toString() );
                menuItem.addActionListener( listener );
                scriptsMenu.add( menuItem );
                }
            }
        
    }//GEN-LAST:event_scriptsMenuMenuSelected

    private void logsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logsBtnActionPerformed
        if ( stdOutFile.getText() != null && ! stdOutFile.getText().equals( "" ) )
            {
            desktopOpen( stdOutFile.getText() );
            }
        if ( stdErrFile.getText() != null && ! stdErrFile.getText().equals( "" ) )
            {
            desktopOpen( stdErrFile.getText() );
            }

    }//GEN-LAST:event_logsBtnActionPerformed

    private void stopFileWatchTbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopFileWatchTbActionPerformed
        if ( stopFileWatchTb.isSelected() )
            {
            stopFileWatchTb.setText( "Off" );
            stopDirWatcher();
            }
        else
            {
            stopFileWatchTb.setText( "Auto" );
            startDirWatcher();
            }
    }//GEN-LAST:event_stopFileWatchTbActionPerformed

    private void dumpThreadListToLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dumpThreadListToLogActionPerformed
        dumpThreads();   // to log
    }//GEN-LAST:event_dumpThreadListToLogActionPerformed

    private void openFolderOfFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFolderOfFilesActionPerformed
        try
            {
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            
            for( int row : filesTbl.getSelectedRows() )
                {
                int rowIndex = filesTbl.convertRowIndexToModel( row );
                File file = new File( (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH ) );
                try {
//                    int rc = JavaProcess.execJava( com.towianski.jfileprocessor.JFileFinderWin.class, file.getParent() );
                ProcessInThread jp = new ProcessInThread();
//                int rc = jp.execJava(com.towianski.jfileprocessor.JFileFinderWin.class, true, file.getParent() );
                ArrayList<String> cmdList = new ArrayList<String>(); 
                cmdList.add( "$JAVABIN" );
    //            cmdList.add( "-cp" );
    //            cmdList.add( "$CLASSPATH" );
                cmdList.add( "-jar" );
                cmdList.add( JFileProcessorVersion.getFileName() );
                cmdList.add( file.getParent() );
                
                int rc = jp.execJava2( cmdList, true );
                    System.out.println( "javaprocess.exec start new window rc = " + rc + "=" );
                    } 
                catch (IOException ex) 
                    {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                catch (InterruptedException ex) 
                    {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }   
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
    }//GEN-LAST:event_openFolderOfFilesActionPerformed

    private void testSshBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_testSshBtnActionPerformed
    {//GEN-HEADEREND:event_testSshBtnActionPerformed
        try
            {
            JschSftpUtils jschSftpUtils = new JschSftpUtils();
            
                try {
                    //System.out.println( "try scpTo   file =" + tmp + "=   to remote =" + DesktopUtils.getUserTmp() + System.getProperty( "file.separator") + tmp + "=" );
                    String fpath = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" );
                    String jfpFilename = JFileProcessorVersion.getFileName();
                    System.out.println( "set jfpFilename =" + jfpFilename + "=" );
                    System.out.println( "try jschSftpUtils   file =" + fpath + jfpFilename + "=   to remote =" + rmtUser.getText() + "@" + rmtHost + ":" + jfpFilename + "=" );

                    jFileFinderWin.setMessage( "copy server to remote" );
                    jschSftpUtils.sftpIfDiff( fpath + jfpFilename, rmtUser.getText(), rmtPasswd.getText(), rmtHost.getText(), 22, jfpFilename );
                    } 
                catch (Exception ex) 
                    {
                    Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
                    } 
//                break;  // only do 1 file
//                }   
//            scpTo.exec( "", "", "localhost" );
//            scpTo.execX11Forwarding( "", "", "localhost" );
//            scpTo.execX11( "", "", "stan.local" );
//            scpTo.execX11Forwarding( "rmt", "rmt", "192.168.56.102" );
//              scpTo.execX11( "rmt", "rmt", "192.168.56.102" );

//            scpTo.execX11Forwarding( "", "", "192.168.56.103" );
//            scpTo.execX11ForwardingOrig( "", "", "192.168.56.103" );
            jschSftpUtils.exec( rmtUser.getText(), rmtPasswd.getText(), rmtHost.getText(), connUserInfo, "/opt/jFileProcessor/server.sh" );
//              scpTo.execX11( "", "", "192.168.56.103" );
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        
    }//GEN-LAST:event_testSshBtnActionPerformed

    private void rmtHostActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rmtHostActionPerformed
    {//GEN-HEADEREND:event_rmtHostActionPerformed
        rmtConnectBtn.doClick();
    }//GEN-LAST:event_rmtHostActionPerformed

    private void rmtConnectBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rmtConnectBtnActionPerformed
    {//GEN-HEADEREND:event_rmtConnectBtnActionPerformed
        System.out.println("jfilewin rmtConnectBtnActionPerformed()" );
        if ( getRmtUser().trim().equals( "" ) || getRmtPasswd().trim().equals( "" ) ||  getRmtHost().trim().equals( "" ) )
            {
            JOptionPane.showMessageDialog( null, "Remote User, Password, Host cannot be blank", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        stopDirWatcher();

//        if ( rmtConnectBtn.getText().equalsIgnoreCase( PROCESS_STATUS_CANCEL_SEARCH ) )
//            {
//            System.out.println( "rmtConnectBtnActionPerformed() hit stop button, got rootPaneCheckingEnabled =" + rootPaneCheckingEnabled + "=" );
////            setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//            rmtConnectBtn.setText( Constants.PROCESS_STATUS_SEARCH_READY );
//            rmtConnectBtn.setBackground( saveColor );
//            rmtConnectBtn.setOpaque(true);
//            
////            this.stopSearch();
//            rmtForceDisconnectBtnActionPerformed( evt );
//            }
//        else
//            {
        System.out.println("jfilewin rmtConnectBtnActionPerformed() do Connect   restServerSw =" + restServerSw );
            try {
                if ( restServerSw != null )
                    {
                    if ( connUserInfo.isConnectedFlag() || rmtConnectBtn.getBackground() != saveColor )
                        {
//                        JOptionPane.showMessageDialog( null, "Please Disconnect first", "Error", JOptionPane.ERROR_MESSAGE );
                        rmtDisconnectBtn.doClick();
                        return;
                        }
                    }
                connUserInfo = new ConnUserInfo( false, Constants.PATH_PROTOCOL_SFTP, getRmtUser(), getRmtPasswd(), getRmtHost(), getRmtSshPort(), getRmtAskHttpsPort() );
                connUserInfo.setFrom( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "" );
                connUserInfo.setFromFilesysType( filesysType );
//                if ( restServerSw == null )
//                    {
                    restServerSw = new RestServerSw( connUserInfo, this );
//                    }        
//                rmtConnectBtn.setText( Constants.PROCESS_STATUS_CANCEL_SEARCH );
//                rmtConnectBtn.setBackground( Color.BLUE );
                restServerSw.actionPerformed(null);        

                
//                SftpConnectSwingWorker sftpConnectSwingWorker = new SftpConnectSwingWorker( this, restServerSw );
////                searchBtn.setText( "Stop" );
////                searchBtn.setBackground(Color.RED);
////                searchBtn.setOpaque(true);
////                searchBtn.setBorderPainted(false);
////                message.setText( "Search started . . ." );
//                //setProcessStatus( PROCESS_STATUS_SEARCH_STARTED );
//                System.out.println( "*************  jFileFinderSwingWorker.execute()  ****************" );
//            
//                sftpConnectSwingWorker.execute();   //doInBackground();
//                //jfinderThread = new Thread( jfilefinder );
////                        jfinderThread.start();
////                        jfinderThread.join();
////                        searchBtn.setText( "Search" );
////                        searchBtn.setBackground( saveColor );
////                        searchBtn.setOpaque(true);
                } 
            catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
                } 
//        }

    }//GEN-LAST:event_rmtConnectBtnActionPerformed

    private void rmtForceDisconnectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmtForceDisconnectBtnActionPerformed
        System.out.println("jfilewin rmtForceDisconnectBtnActionPerformed() do Disconnect Force" );
        if ( getRmtUser().trim().equals( "" ) || getRmtPasswd().trim().equals( "" ) ||  getRmtHost().trim().equals( "" ) )
            {
            JOptionPane.showMessageDialog( null, "Remote User, Password, Host cannot be blank", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        if ( restServerSw == null )
            {
            connUserInfo = new ConnUserInfo( false, Constants.PATH_PROTOCOL_SFTP, getRmtUser(), getRmtPasswd(), getRmtHost(), getRmtSshPort(), getRmtAskHttpsPort() );
            restServerSw = new RestServerSw( connUserInfo, this );
            }
        if ( restServerSw != null )
            {
            //rmtConnectBtn.setText( RMT_CONNECT_BTN_CONNECT );
            System.out.println( "call restServerSw.cancelRestServer()" );
            System.out.println( "stop with force" );
            restServerSw.cancelRestServer( true );  // do Force stop
            System.out.println( "after restServerSw.cancelRestServer()" );
            restServerSw = null;
//            rmtConnectBtn.setBackground( saveColor );
            rmtConnectBtn.setText( "" );
            }
        connUserInfo = new ConnUserInfo( false, Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "", getRmtAskHttpsPort() );
        connUserInfo.setFrom( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "" );
        calcFilesysType();  // also sets in connUserInfo
    }//GEN-LAST:event_rmtForceDisconnectBtnActionPerformed

    private void rmtSshPortActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rmtSshPortActionPerformed
    {//GEN-HEADEREND:event_rmtSshPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rmtSshPortActionPerformed

    private void rmtDisconnectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmtDisconnectBtnActionPerformed
        System.out.println("jfilewin rmtDisconnectBtnActionPerformed() do Disconnect" );
        if ( getRmtUser().trim().equals( "" ) || getRmtPasswd().trim().equals( "" ) ||  getRmtHost().trim().equals( "" ) )
            {
            JOptionPane.showMessageDialog( null, "Remote User, Password, Host cannot be blank", "Error", JOptionPane.ERROR_MESSAGE );
            return;
            }
        if ( restServerSw != null )
            {
            //rmtConnectBtn.setText( RMT_CONNECT_BTN_CONNECT );
            System.out.println( "call restServerSw.cancelRestServer()" );
            System.out.println( "stop regular" );
            restServerSw.cancelRestServer( false );   // no force stop
            System.out.println( "after restServerSw.cancelRestServer()" );
            restServerSw = null;
//            rmtConnectBtn.setBackground( saveColor );
            rmtConnectBtn.setText( "" );
            setRmtUser( "" );
            setRmtPasswd( "" );
            setRmtHost( "" );
            setRmtSshPort("" );
            setRmtAskHttpsPort("" );
            }
        connUserInfo = new ConnUserInfo( false, Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "", getRmtAskHttpsPort() );
        connUserInfo.setFrom( Constants.PATH_PROTOCOL_FILE, "", "", hostAddress, "" );
        calcFilesysType();  // also sets in connUserInfo
    }//GEN-LAST:event_rmtDisconnectBtnActionPerformed

    private void rmtPasswdActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rmtPasswdActionPerformed
    {//GEN-HEADEREND:event_rmtPasswdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rmtPasswdActionPerformed

    private void rmtUserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rmtUserActionPerformed
    {//GEN-HEADEREND:event_rmtUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rmtUserActionPerformed

    private void openCodeWin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCodeWin1ActionPerformed
        filesTbl.requestFocus();
        openCodeWinActionPerformed( null );
    }//GEN-LAST:event_openCodeWin1ActionPerformed

    private void scriptsMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_scriptsMenu1MenuSelected
        scriptsMenu1.removeAll();            
        ArrayList<File> files = new ArrayList<File>(Arrays.asList( scriptsFile.listFiles() ));
        files.addAll( new ArrayList<File>(Arrays.asList( scriptsOsFile.listFiles() ) ) );
        if ( files == null )   return;
         
        for ( File file : files ) 
            {
            //System.out.println( "is groovy menu file: " + file.getName() + "?" );
            if ( file.toString().endsWith( ".groovy" ) )
                {
                JMenuItem menuItem = new JMenuItem( file.getName().substring( 0, file.getName().length() - 7 ), null );
                ScriptMenuItemListener listener = new ScriptMenuItemListener( this, file.getAbsolutePath().toString() );
                menuItem.addActionListener( listener );
                scriptsMenu1.add( menuItem );
                }
            }

    }//GEN-LAST:event_scriptsMenu1MenuSelected

    private void readFileIntoListWin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readFileIntoListWin1ActionPerformed
        readFileIntoListWinActionPerformed( null );
    }//GEN-LAST:event_readFileIntoListWin1ActionPerformed

    private void openListWindow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openListWindow1ActionPerformed
        openListWindowActionPerformed( null );
    }//GEN-LAST:event_openListWindow1ActionPerformed

    private void myEditorCmdActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_myEditorCmdActionPerformed
    {//GEN-HEADEREND:event_myEditorCmdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_myEditorCmdActionPerformed

    private void rmtAskHttpsPortActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rmtAskHttpsPortActionPerformed
    {//GEN-HEADEREND:event_rmtAskHttpsPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rmtAskHttpsPortActionPerformed

    private void NewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewFileActionPerformed
        //stopDirWatcher();
        FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
        System.out.println( "filesTbl.getRowCount() = " + filesTbl.getRowCount() + "   filesTblModel.getColumnCount()  = " + filesTblModel.getColumnCount() );
        if ( filesTbl.getRowCount() < 2 && filesTblModel.getColumnCount() < 2 )
            {
            System.out.println( "call newfolderOnlyFilesTableModel( startingFolder.getText().trim() + \"New File\" )" );
            filesTblModel = jfilefinder.newfolderOnlyFilesTableModel( startingFolder.getText().trim() + "New File" );
            fillInFilesTable( filesTblModel );
            filesTblCellListener.skipFirstEditOn( FilesTblModel.FILESTBLMODEL_FILETYPE, startingFolder.getText().trim() + "New File" );
            filesTblModel.replaceRowAt( 0, startingFolder.getText().trim() + "New File" );
            }
        else
            {
            filesTblCellListener.skipFirstEditOn( FilesTblModel.FILESTBLMODEL_FILETYPE, startingFolder.getText().trim() + "New File" );
            filesTblModel.insertRowAt( 0, startingFolder.getText().trim() + "New File" );
            }
            
        setColumnSizes();
//        filesTbl.setCellSelectionEnabled( true );
        filesTblModel.setCellEditable( 0, FilesTblModel.FILESTBLMODEL_PATH, true );
        //filesTbl.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
        //filesTbl.requestFocus();
        
//        filesTbl.editCellAt( 0, FilesTblModel.FILESTBLMODEL_PATH ); 
//        filesTbl.setSurrendersFocusOnKeystroke( true );	
//        filesTbl.transferFocus();
        filesTbl.changeSelection( 0, FilesTblModel.FILESTBLMODEL_PATH, false, false );
//        filesTbl.getEditorCo‌​mponent().requestFocus();
        JTextComponent textComp = (JTextComponent) filesTbl.getEditorCo‌​mponent();
        int len = (startingFolder.getText().trim() + "New File").length();
        textComp.select( len - 8, len );
        //filesTbl.setCellSelectionEnabled( false );
        // Enhancement - make it go into new folder after change name !
    }//GEN-LAST:event_NewFileActionPerformed

    private void NewFolder1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewFolder1ActionPerformed
        NewFolderActionPerformed( evt );
    }//GEN-LAST:event_NewFolder1ActionPerformed

    private void NewFile1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewFile1ActionPerformed
        NewFileActionPerformed( evt );
    }//GEN-LAST:event_NewFile1ActionPerformed

    private void showGroupFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showGroupFlagActionPerformed
        alreadySetColumnSizes = false;
    }//GEN-LAST:event_showGroupFlagActionPerformed

    private void showPermsFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPermsFlagActionPerformed
        alreadySetColumnSizes = false;
    }//GEN-LAST:event_showPermsFlagActionPerformed

    private void jfpExecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jfpExecActionPerformed
        jfpExecOrStop( "exec" );
    }//GEN-LAST:event_jfpExecActionPerformed

    private void jfpExecEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jfpExecEditActionPerformed
        try {
            //System.out.println( "RenameActionPerformed evt.getSource() =" + evt.getSource() );
            if ( filesTbl.getSelectedRow() < 0 )
                {
                JOptionPane.showMessageDialog( this, "Please select an item first.", "Error", JOptionPane.ERROR_MESSAGE );
                return;
                }
            int rowIndex = filesTbl.convertRowIndexToModel( filesTbl.getSelectedRow() );
            System.out.println( "rename filesTbl.getSelectedRow() =" + filesTbl.getSelectedRow() + "   rowIndex = " + rowIndex );
            FilesTblModel filesTblModel = (FilesTblModel) filesTbl.getModel();
            String selectedPath = (String) filesTblModel.getValueAt( rowIndex, FilesTblModel.FILESTBLMODEL_PATH );
            System.out.println( "start webserver at selectedPath =" + selectedPath + "   rowIndex = " + rowIndex );
            
            String runCmdString = "";
//            System.out.println( "------- BEG DUMP FILE ASSOC LIST");
//            for(Map.Entry<String,FileAssoc> entry : fileAssocList.getFileAssocList().entrySet()) {
//                String key = entry.getKey();
//                FileAssoc value = entry.getValue();
//
//                System.out.println( "matched and got fa.getAssocType =" + value.getAssocType() + "=" );
//                System.out.println( "matched and got fa.getMatchPattern =" + value.getMatchPattern() + "=" );
//                System.out.println( "matched and got fa.exec =" + value.getExec() + "=" );
//                }
//            System.out.println( "------- END DUMP FILE ASSOC LIST");
            
            com.towianski.utils.FileAssocWin fileAssocWin = null;
            FileAssoc fa = fileAssocList.getFileAssoc(JfpConstants.ASSOC_TYPE_EXACT_FILE, selectedPath );
            if ( fa != null )
                {
                System.out.println( "matched and got ASSOC_TYPE_1_FILE fa.exec =" + fa.getExec() + "=" );
                fileAssocWin = new FileAssocWin( "Edit", selectedPath, fa );
                fa = new FileAssoc( fileAssocWin.getAssocType(), fileAssocWin.getEditClass(), 
                        fileAssocWin.getMatchType(), fileAssocWin.getMatchPattern(), fileAssocWin.getExec(), fileAssocWin.getStop() );
                }
            else
                {
                fa = fileAssocList.getFileAssoc( JfpConstants.ASSOC_TYPE_FILENAME, selectedPath );
                if ( fa != null )
                    {
                    System.out.println( "matched and got ASSOC_TYPE_FILENAME fa.exec =" + fa.getExec() + "=" );
                    fileAssocWin = new FileAssocWin( "Edit", selectedPath, fa );
                    fa = new FileAssoc( fileAssocWin.getAssocType(), fileAssocWin.getEditClass(), 
                            fileAssocWin.getMatchType(), fileAssocWin.getMatchPattern(), fileAssocWin.getExec(), fileAssocWin.getStop() );
                    }
                else
                    {
                    fa = fileAssocList.getFileAssoc(JfpConstants.ASSOC_TYPE_SUFFIX, selectedPath );
                    if ( fa != null )
                        {
                        System.out.println( "matched and got ASSOC_TYPE_SUFFIX fa.exec =" + fa.getExec() + "=" );
                        System.out.println( "matched and got ASSOC_TYPE_SUFFIX fa.stop =" + fa.getStop() + "=" );
                        fileAssocWin = new FileAssocWin( "Edit", selectedPath, fa );
                        fa = new FileAssoc( fileAssocWin.getAssocType(), fileAssocWin.getEditClass(), 
                                fileAssocWin.getMatchType(), fileAssocWin.getMatchPattern(), fileAssocWin.getExec(), fileAssocWin.getStop() );
                        }
                    else
                        {
                        System.out.println( "No File Assoc Found" );
                        fileAssocWin = new FileAssocWin( "New", selectedPath, null );
                        fileAssocWin.setTitle( "New" );
                        fa = new FileAssoc( fileAssocWin.getAssocType(), fileAssocWin.getEditClass(), 
                                fileAssocWin.getMatchType(), fileAssocWin.getMatchPattern(), fileAssocWin.getExec(), fileAssocWin.getStop() );
                        }
                    }
                }
            if ( fileAssocWin != null && fileAssocWin.isOkFlag() )
                {
                fileAssocList.addFileAssoc( fa );
                Rest.saveObjectToFile( "FileAssocList.json", fileAssocList );
                }
            fileAssocWin = null;
        } catch (Exception ex) {
            Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jfpExecEditActionPerformed

    private void jfpStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jfpStopActionPerformed
                jfpExecOrStop( "stop" );
    }//GEN-LAST:event_jfpStopActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        System.setProperty("sun.java2d.uiScale", "2.0");
//        System.out.println( "set sun.java2d.uiScale = 2.0" );
        startSwing( args );
    }

    public static void startSwing(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );        

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        for ( int i = 0; i < args.length; i++ )
            {
//            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            System.out.println( "** args [" + i + "] =" + args[i] + "=" );
            }

//        final JFileFinderWin jffw = new JFileFinderWin();
//        if ( args.length > 0 )
//            {
//            jffw.startingFolder.setText( args[0] );
//            jffw.searchBtnActionPerformed( null );
//            }
        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
////                new JFileFinderWin().setVisible(true);
//                jffw.setVisible(true);
//            }
//        });


            /*
    GraphicsEnvironment ge = GraphicsEnvironment
        .getLocalGraphicsEnvironment();
    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
    GraphicsConfiguration[] configurations = defaultScreen
        .getConfigurations();
    System.out.println("Default screen device: "
        + defaultScreen.getIDstring());
    for (int i = 0; i < configurations.length; i++) {
      System.out.println("  Configuration " + (i + 1));
      System.out.println("  " + configurations[i].getColorModel());
    }

    double scaleX = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
    double scaleY = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleY();
    System.out.println( "scaleX = " + scaleX + "   scaleY = " + scaleY );
    double scaleX = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().
    double scaleY = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleY();
            */
            
            
        EventQueue.invokeLater(() -> {
//            jFileFinderWin.setVisible(true);
            if ( args.length > 0 )
                {
                JFileFinderWin jFileFinderWin = new JFileFinderWin( args[0] );
//                jFileFinderWin.startingFolder.setText( args[0] );
//                jFileFinderWin.searchBtnActionPerformed( null );
                }
            else
                {
                JFileFinderWin jFileFinderWin = new JFileFinderWin();
                }
            
            /*
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            for (int j = 0; j < gs.length; j++) {
               GraphicsDevice gd = gs[j];
               GraphicsConfiguration[] gc =
               gd.getConfigurations();
               for (int i=0; i < gc.length; i++) {
                  JFrame f = new
                  JFrame(gs[j].getDefaultConfiguration());
                  Canvas c = new Canvas(gc[i]);
                  Rectangle gcBounds = gc[i].getBounds();
                  int xoffs = gcBounds.x;
                  int yoffs = gcBounds.y;
                  f.getContentPane().add(c);
                  f.setLocation((i*50)+xoffs, (i*60)+yoffs);
                  f.show();
               }
            }  
            */
            
    
                         
            });   
        
    }
    
    public static void startSpringBoot(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        MyLogger logger = MyLogger.getLogger( JFileFinderWin.class.getName() );        

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        for ( int i = 0; i < args.length; i++ )
            {
//            logger.info( "** args [" + i + "] =" + args[i] + "=" );
            System.out.println( "** args [" + i + "] =" + args[i] + "=" );
            }
        
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(JFileFinderWin.class).headless(false).run(args);
        ConfigurableApplicationContext context = new SpringApplicationBuilder( JFileFinderWin.class ) .profiles("client").web( WebApplicationType.NONE ).bannerMode(Banner.Mode.OFF).headless(false).run(args);

        EventQueue.invokeLater(() -> {
            JFileFinderWin jFileFinderWin = context.getBean(JFileFinderWin.class);
            jFileFinderWin.setVisible(true);
            if ( args.length > 0 )
                {
                jFileFinderWin.startingFolder.setText( args[0] );
                jFileFinderWin.searchBtnActionPerformed( null );
                }
            });   
        
        context.close();
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("Inside destroy method - " );
        }
   
    @Bean
    public RestTemplate noHostVerifyRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException 
        {
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
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Copy;
    private javax.swing.JMenuItem Cut;
    private javax.swing.JMenuItem Delete;
    private javax.swing.JMenu New;
    private javax.swing.JMenuItem NewFile;
    private javax.swing.JMenuItem NewFile1;
    private javax.swing.JMenuItem NewFolder;
    private javax.swing.JMenuItem NewFolder1;
    private javax.swing.JMenuItem Paste;
    private javax.swing.JMenuItem Paste1;
    private javax.swing.JMenuItem Rename;
    private javax.swing.JButton addPath;
    private javax.swing.JPanel botPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JMenuItem copyFilename;
    private javax.swing.JButton countBtn;
    private org.jdatepicker.impl.JDatePickerImpl date1;
    private javax.swing.JComboBox date1Op;
    private org.jdatepicker.impl.JDatePickerImpl date2;
    private javax.swing.JComboBox date2Op;
    private javax.swing.JSpinner dateLogicOp;
    private javax.swing.JButton deletePath;
    private javax.swing.JButton dumpThreadListToLog;
    private javax.swing.JCheckBox fileMgrMode;
    private javax.swing.JTextField filePattern;
    private javax.swing.JLabel fileWatchLbl;
    private javax.swing.JTable filesTbl;
    private javax.swing.JLabel fsTypeLbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem jfpExec;
    private javax.swing.JMenuItem jfpExecEdit;
    private javax.swing.JMenuItem jfpStop;
    private javax.swing.JButton leftHistory;
    //Level[] logLevelsArr = new Level [] { Level.ALL, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.INFO, Level.OFF, Level.SEVERE, Level.WARNING };
    String[] logLevelsArrStr = new String [] { Level.OFF.toString(), Level.SEVERE.toString(), Level.WARNING.toString(), Level.INFO.toString(), Level.CONFIG.toString(), Level.FINE.toString(), Level.FINER.toString(), Level.FINEST.toString(), Level.ALL.toString() };
    LinkedHashMap<String,Level> logLevelsLhm = new LinkedHashMap<String,Level>();
    private javax.swing.JComboBox logLevel;
    private javax.swing.JButton logsBtn;
    private javax.swing.JTextField maxDepth;
    private javax.swing.JLabel message;
    private javax.swing.JTextField minDepth;
    private javax.swing.JMenuItem myEdit;
    private javax.swing.JTextField myEditorCmd;
    private javax.swing.JLabel myEditorLbl;
    private javax.swing.JLabel newerVersionLbl;
    private javax.swing.JLabel newerVersionSpLbl;
    private javax.swing.JLabel numFilesInTable;
    private javax.swing.JMenuItem openCodeWin;
    private javax.swing.JMenuItem openCodeWin1;
    private javax.swing.JMenuItem openFile;
    private javax.swing.JMenuItem openFolderOfFiles;
    private javax.swing.JMenuItem openListWindow;
    private javax.swing.JMenuItem openListWindow1;
    private javax.swing.JLabel processStatus;
    private javax.swing.JMenuItem readFileIntoListWin;
    private javax.swing.JMenuItem readFileIntoListWin1;
    private javax.swing.JPanel replaceSavePathPanel;
    private javax.swing.JLabel reportIssueLbl;
    private javax.swing.JButton rightHistory;
    private javax.swing.JTextField rmtAskHttpsPort;
    private javax.swing.JLabel rmtAskHttpsPortLbl;
    private javax.swing.JButton rmtConnectBtn;
    private javax.swing.JButton rmtDisconnectBtn;
    private javax.swing.JButton rmtForceDisconnectBtn;
    private javax.swing.JTextField rmtHost;
    private javax.swing.JPasswordField rmtPasswd;
    private javax.swing.JTextField rmtSshPort;
    private javax.swing.JTextField rmtUser;
    private javax.swing.JMenuItem savePathsToFile;
    private javax.swing.JMenuItem savePathsToFile1;
    private javax.swing.JList<String> savedPathsList;
    private javax.swing.JScrollPane savedPathsListScrollPane;
    private javax.swing.JMenu scriptsMenu;
    private javax.swing.JMenu scriptsMenu1;
    protected javax.swing.JButton searchBtn;
    private javax.swing.JComboBox showFilesFoldersCb;
    private javax.swing.JCheckBox showGroupFlag;
    private javax.swing.JCheckBox showHiddenFilesFlag;
    private javax.swing.JCheckBox showJustFilenameFlag;
    private javax.swing.JCheckBox showOwnerFlag;
    private javax.swing.JCheckBox showPermsFlag;
    private javax.swing.JFormattedTextField size1;
    private javax.swing.JComboBox size1Op;
    private javax.swing.JTextField size2;
    private javax.swing.JComboBox size2Op;
    private javax.swing.JSpinner sizeLogicOp;
    private javax.swing.JMenuItem startCmdWin;
    private javax.swing.JMenuItem startCmdWin1;
    private javax.swing.JTextField startConsoleCmd;
    private javax.swing.JTextField startingFolder;
    private javax.swing.JFormattedTextField stdErrFile;
    private javax.swing.JFormattedTextField stdOutFile;
    private javax.swing.JTextField stopFileCount;
    private javax.swing.JToggleButton stopFileWatchTb;
    private javax.swing.JTextField stopFolderCount;
    private javax.swing.JRadioButton tabsLogicAndBtn;
    private javax.swing.JRadioButton tabsLogicOrBtn;
    private javax.swing.JButton testSshBtn;
    private javax.swing.JPanel topPanel;
    private javax.swing.JButton upFolder;
    private javax.swing.JRadioButton useGlobPattern;
    private javax.swing.JRadioButton useRegexPattern;
    private javax.swing.JLabel webSiteLink;
    // End of variables declaration//GEN-END:variables

    public JTable getFilesTbl() {
        return filesTbl;
    }
}
