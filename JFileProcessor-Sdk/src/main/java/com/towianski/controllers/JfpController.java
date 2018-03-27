package com.towianski.controllers;

import com.towianski.models.JfpRestURIConstants;
import com.towianski.chainfilters.ChainFilterOfBoolean;
import com.towianski.chainfilters.ChainFilterOfMaxDepth;
import com.towianski.chainfilters.ChainFilterOfMinDepth;
import com.towianski.chainfilters.ChainFilterOfNames;
import com.towianski.chainfilters.ChainFilterOfPreVisitMaxDepth;
import com.towianski.chainfilters.ChainFilterOfPreVisitMinDepth;
import com.towianski.chainfilters.FilterChain;
import com.towianski.jfileprocessor.JFileFinder;
import com.towianski.models.Constants;
import com.towianski.models.FilesTblModel;
import com.towianski.models.ResultsData;
import com.towianski.utils.MyLogger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.swing.JOptionPane;

/**
 * Handles requests for the Employee service.
 */
@Controller
@EnableAutoConfiguration
//@Controller
public class JfpController {
	
    private final static MyLogger logger = MyLogger.getLogger( JfpController.class.getName() );

	//Map to store employees, ideally we should use database
//	Map<Integer, Employee> empData = new HashMap<Integer, Employee>();
//	Map<Integer, Employee> empData = TomcatApp.myRest.readProgramMemoryFile().empData;
//        JFileFinderWin jFileFinderWin = TomcatApp.getProgramMemory().getjFileFinderWin();
        
//	@RequestMapping(value = EmpRestURIConstants.DUMMY_EMP, method = RequestMethod.GET)
//	public @ResponseBody Employee getDummyEmployee() {
//		logger.info("Start getDummyEmployee");
//		Employee emp = new Employee();
//		emp.setId(9999);
//		emp.setName("Dummy");
//		emp.setCompany("noCompany");
//		emp.setCreatedDate(new Date());
//		empData.put(9999, emp);
//		return emp;
//	}
	
//	@RequestMapping(value = EmpRestURIConstants.GET_EMP, method = RequestMethod.GET)
//	public @ResponseBody Employee getEmployee(@PathVariable("id") int empId) {
//		logger.info("Start getEmployee. ID="+empId);
//		
//		return empData.get(empId);
//	}
	
	@RequestMapping( value = JfpRestURIConstants.SEARCH, method = RequestMethod.GET )
	public @ResponseBody FilesTblModel getFiles(@PathVariable("startingFolder") String startingFolder) {
            logger.info("Start getFiles.");
            return searchBtnAction( null );
	}

	@RequestMapping( value = JfpRestURIConstants.SYS_STOP, method = RequestMethod.GET )
	public @ResponseBody void stop() {
            logger.info("Stop server");

            System.exit(0);
	}

    public FilesTblModel searchBtnAction( java.awt.event.ActionEvent evt )
        {
            JFileFinder jfilefinder = null;

            //JOptionPane.showConfirmDialog( null, "at call do search" );
            try {
                String[] args = new String[3];
                args[0] = "/net2";  //startingFolder.getText().trim();

                if ( ! ( args[0].endsWith(System.getProperty( "file.separator" ) )
                         || args[0].endsWith( "/" ) ) ) 
                    {
                    args[0] += System.getProperty( "file.separator" );
                    }
//                startingFolder.setText( args[0] );

                args[1] = "-glob";  //useRegexPattern.isSelected() ? "-regex" : "-glob";
                args[2] = "";  //filePattern.getText().trim();
                
                if ( true  //useGlobPattern.isSelected()
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
//                        filePattern.setText( System.getProperty( "file.separator" ) + args[2] );
                        args[2] = System.getProperty( "file.separator" ) + args[2];  //filePattern.getText();
                        }
                    }
                
                //------- save history of paths  -------
//                pathsHistoryList.add( args[0] );
//                System.out.println( "after pathsHistoryList()" );

                //public ChainFilterA( ChainFilterA nextChainFilter )
                //Long size1Long = Long.parseLong( size1.getText().trim() );
//                System.out.println( "tabsLogic button.getText() =" + (tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText()) + "=" );
                FilterChain chainFilterList = new FilterChain( "And" );  //tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterFolderList = new FilterChain( "And" );  //tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );
                FilterChain chainFilterPreVisitFolderList = new FilterChain( "And" );  //tabsLogicAndBtn.isSelected() ? tabsLogicAndBtn.getText() : tabsLogicOrBtn.getText() );

                try {
                    if ( ! args[2].trim().equals( "" ) )
                        {
                        System.out.println( "add filter of names!" );
                        ChainFilterOfNames chainFilterOfNames = new ChainFilterOfNames( args[1], (args[0] + args[2]).replace( "\\", "\\\\" ) );
                        if ( false ) //showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY )  ||
//                             showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_BOTH ) )
                            {
                            chainFilterFolderList.addFilter( chainFilterOfNames );
                            }
                        if ( false )  //showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FILES_ONLY )  ||
//                             showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_BOTH ) )
                            {
                            chainFilterList.addFilter( chainFilterOfNames );
                            }
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return null;
                    }

                try {
//                    System.out.println( "showFilesFoldersCb.getSelectedItem() =" + showFilesFoldersCb.getSelectedItem() + "=" );
                    if ( false ) //showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FILES_ONLY ) 
//                         || showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        System.out.println( "add filter Boolean False for folders" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterFolderList.addFilter( chainFilterOfBoolean );
                        }
                    if ( false ) //showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_FOLDERS_ONLY ) 
//                         || showFilesFoldersCb.getSelectedItem().equals( SHOWFILESFOLDERSCB_NEITHER ) )
                        {
                        System.out.println( "add filter Boolean False for files" );
                        ChainFilterOfBoolean chainFilterOfBoolean = new ChainFilterOfBoolean( false );
                        chainFilterList.addFilter( chainFilterOfBoolean );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Boolean filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
                    return null;
                    }

//                try {
//                    if ( ! size1.getText().trim().equals( "" ) )
//                        {
//                        System.out.println( "add filter of sizes!" );
//                        ChainFilterOfSizes chainFilterOfSizes = new ChainFilterOfSizes( (String)size1Op.getSelectedItem(), size1.getText().trim(), ((String) sizeLogicOp.getValue()).trim(), (String)size2Op.getSelectedItem(), size2.getText().trim() );
//                        chainFilterList.addFilter( chainFilterOfSizes );
//                        }
//                    }
//                catch( Exception ex )
//                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Size filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
//                    }

//                try {
//                    if ( (Date) date1.getModel().getValue() != null )
//                        {
//                        System.out.println( "add filter of dates!" );
//                        System.out.println( "selected date =" + (Date) date1.getModel().getValue() + "=" );
//                        ChainFilterOfDates chainFilterOfDates = new ChainFilterOfDates( (String)date1Op.getSelectedItem(), (Date) date1.getModel().getValue(), ((String) dateLogicOp.getValue()).trim(), (String)date2Op.getSelectedItem(), (Date) date2.getModel().getValue() );
//                        chainFilterList.addFilter( chainFilterOfDates );
//                        }
//                    }
//                catch( Exception ex )
//                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Date filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
//                    }

                try {
                    String max = "4";
                    if ( ! max.equals( "" ) )
                        {
                        System.out.println( "add filter of maxdepth!" );
                        System.out.println( "selected maxdepth =" + max + "=" );
                        ChainFilterOfMaxDepth chainFilterOfMaxDepth = new ChainFilterOfMaxDepth( args[0], max );
                        chainFilterFolderList.addFilter( chainFilterOfMaxDepth );
                        chainFilterList.addFilter( chainFilterOfMaxDepth );
                        ChainFilterOfPreVisitMaxDepth chainFilterOfPreVisitMaxDepth = new ChainFilterOfPreVisitMaxDepth( args[0], max );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMaxDepth );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
                    }

//                try {
//                    if ( ! stopFileCount.getText().trim().equals( "" ) )
//                        {
//                        System.out.println( "add filter of stopFileCount!" );
//                        System.out.println( "selected stopFileCount =" + stopFileCount.getText().trim() + "=" );
//                        ChainFilterOfMaxFileCount chainFilterOfMaxFileCount = new ChainFilterOfMaxFileCount( args[0], stopFileCount.getText().trim() );
//                        chainFilterList.addFilter( chainFilterOfMaxFileCount );
//                        }
//                    }
//                catch( Exception ex )
//                    {
//                    JOptionPane.showMessageDialog( this, "Error in stopFileCount filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
//                    }
//
//                try {
//                    if ( ! stopFolderCount.getText().trim().equals( "" ) )
//                        {
//                        System.out.println( "add filter of stopFolderCount!" );
//                        System.out.println( "selected stopFolderCount =" + stopFolderCount.getText().trim() + "=" );
//                        ChainFilterOfMaxFolderCount chainFilterOfMaxFolderCount = new ChainFilterOfMaxFolderCount( args[0], stopFolderCount.getText().trim() );
//                        chainFilterFolderList.addFilter( chainFilterOfMaxFolderCount );
//                        }
//                    }
//                catch( Exception ex )
//                    {
//                    JOptionPane.showMessageDialog( this, "Error in stopFolderCount filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
//                    }
//
                try {
                    String min = "1";
                    if ( ! min.equals( "" ) )
                        {
                        System.out.println( "add filter of minDepth!" );
                        System.out.println( "selected minDepth =" + min + "=" );
                        ChainFilterOfMinDepth chainFilterOfMinDepth = new ChainFilterOfMinDepth( args[0], min );
                        chainFilterFolderList.addFilter( chainFilterOfMinDepth );
                        chainFilterList.addFilter( chainFilterOfMinDepth );
                        ChainFilterOfPreVisitMinDepth chainFilterOfPreVisitMinDepth = new ChainFilterOfPreVisitMinDepth( args[0], min );
                        chainFilterPreVisitFolderList.addFilter( chainFilterOfPreVisitMinDepth );
                        }
                    }
                catch( Exception ex )
                    {
//                    JOptionPane.showMessageDialog( this, "Error in Max Depth filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
                    }
                
//                try {
//                    if ( ! showHiddenFilesFlag.isSelected() )
//                        {
//                        System.out.println( "add filter of do not show hidden files!" );
//                        ChainFilterOfShowHidden filter = new ChainFilterOfShowHidden( false );
//                        chainFilterList.addFilter( filter );
//                        chainFilterFolderList.addFilter( filter );
//                        }
//                    }
//                catch( Exception ex )
//                    {
//                    JOptionPane.showMessageDialog( this, "Error in a Name filter", "Error", JOptionPane.ERROR_MESSAGE );
//                    setProcessStatus( PROCESS_STATUS_SEARCH_CANCELED );
//                    return;
//                    }

                // if it matters for speed I could pass countOnlyFlag to jFileFinder too and not create the arrayList of paths !
                jfilefinder = new JFileFinder( Constants.FILESYSTEM_POSIX /*getFilesysType()*/, args[0], args[1], args[2], chainFilterList, chainFilterFolderList, chainFilterPreVisitFolderList );
//                jFileFinderSwingWorker = new JFileFinderSwingWorker( this, jfilefinder, args[0], args[1], args[2], false );  //countOnlyFlag );
                System.out.println( "*************  jFileFinderSwingWorker.execute()  ****************" );
            
//                jFileFinderSwingWorker.execute();   //doInBackground();
            }
            catch (Exception ex) {
                ex.printStackTrace();
//                logger.log(Level.SEVERE, null, ex);
            }
           return getRecords( jfilefinder );
    }

    public FilesTblModel getRecords( JFileFinder jfilefinder ) 
        {
//        public ResultsData doInBackground() {
        System.out.println( "JFileFinderSwingWorker.doInBackground() before jfilefinder.run()" );
//        stopDirWatcher();
//        jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_STARTED );
//        System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
//        jfilefinder.run();  // this );
        //publish("Listing all text files under the directory: ");
//        return jfilefinder.getResultsData();
//    }
//
        try {
//            System.out.println( "entered JFileFinderSwingWorker.done()" );
//            System.out.println( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );
            ResultsData resultsData = jfilefinder.getResultsData();   //get();
            //System.out.println( "SwingWork.done() got ans =" + matchedPathsList + "=" );
            //jFileFinderWin.resetSearchBtn();
//            NumberFormat numFormat = NumberFormat.getIntegerInstance();
//            String partialMsg = "";
//            if ( resultsData.getSearchWasCanceled() )
//                {
//                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_CANCELED );
//                partialMsg = " PARTIAL files list.";
//                }
//            else
//                {
//                jFileFinderWin.setProcessStatus( jFileFinderWin.PROCESS_STATUS_SEARCH_COMPLETED );
//                }
//            jFileFinderWin.setMessage( "Matched " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) 
//                    + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.  Total "
//                    + numFormat.format( resultsData.getFilesVisited() ) + partialMsg );

            System.out.println( "exiting FillTableModelSwingWorker.done()" );
            
//            jFileFinderWin.releaseSearchLock();
            System.out.println( "exiting JFileFinderSwingWorker.done()" );
        }
        catch (Exception e) 
            {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {
                why = cause.getMessage();
            } else {
                why = e.getMessage();
            }
            System.out.println("Error retrieving file: " + why);
            e.printStackTrace();
            }
        return jfilefinder.getFilesTableModel();
        }


//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(JfpController.class, args );
//	}
}
