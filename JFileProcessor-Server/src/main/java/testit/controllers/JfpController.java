package testit.controllers;

import com.towianski.jfileprocessor.restservices.SearchFiles;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.jfileprocessor.restservices.CopyFiles;
import com.towianski.jfileprocessor.restservices.DeleteFiles;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.CopyModel;
import com.towianski.models.DeleteModel;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.models.ResultsData;
import com.towianski.models.SearchModel;
import com.towianski.utils.FileUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import testit.TomcatApp;
import testit.model.Employee;

import java.util.*;
import java.util.logging.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Handles requests for the Employee service.
 */
@Controller
@EnableAutoConfiguration
//@Controller
public class JfpController {
	
	private static final Logger logger = LoggerFactory.getLogger(JfpController.class);

	//Map to store employees, ideally we should use database
//	Map<Integer, Employee> empData = new HashMap<Integer, Employee>();
	Map<Integer, Employee> empData = TomcatApp.myRest.readProgramMemoryFile().empData;
        JFileFinderWin jFileFinderWin = TomcatApp.getProgramMemory().getjFileFinderWin();
        
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
	
//	@RequestMapping(value = JfpRestURIConstants.GET_FILES, method = RequestMethod.GET)
//	public @ResponseBody FilesTblModel getFiles(@PathVariable("startingFolder") String startingFolder) {
//		logger.info("Start getFiles.");
//                return searchBtnAction( null );
//	}

        @RequestMapping(value = JfpRestURIConstants.SEARCH, method = RequestMethod.POST)
        public ResponseEntity<ResultsData> search(@RequestBody SearchModel parm) {

//            String response = "You entered SearchModel parm =" + parm;
            ResultsData response = searchBtnAction( parm );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @RequestMapping(value = JfpRestURIConstants.COPY, method = RequestMethod.POST)
        public ResponseEntity<ResultsData> copy(@RequestBody CopyModel copyModel) 
            {
            CopyFiles copyAction = new CopyFiles( copyModel );
            ResultsData response = copyAction.doInBackground();
            return new ResponseEntity<>(response, HttpStatus.OK);
            }

        @RequestMapping(value = JfpRestURIConstants.DELETE, method = RequestMethod.POST )
        public ResponseEntity<ResultsData> delete(@RequestBody DeleteModel deleteModel) 
            {
            DeleteFiles deleteFiles = new DeleteFiles( deleteModel );

            ResultsData response = deleteFiles.doInBackground();
            return new ResponseEntity<>(response, HttpStatus.OK);
            }

        @RequestMapping(value = JfpRestURIConstants.RENAME_FILE, method = RequestMethod.PUT)
//        public ResponseEntity<String> rename(@PathVariable("oldname") String oldname, @PathVariable("newname") String newname) 
        public ResponseEntity<String> rename(@RequestParam("oldname") String oldname, @RequestParam("newname") String newname)
            {
            logger.info( "rename oldname =" + oldname + "=   newname =" + newname + "=" );
            try
                {
                //            FileUtils.FileMove( Paths.get( oldname .replace( "|", "/" )), Paths.get( newname.replace( "|", "/" ) ) );
//                FileUtils.FileMove( new ConnUserInfo(), Paths.get( URLDecoder.decode( oldname, "UTF-8" ) ) , Paths.get( URLDecoder.decode( newname, "UTF-8" ) ) );
                FileUtils.fileMove( new ConnUserInfo(), oldname, newname );
                } 
//            catch (UnsupportedEncodingException ex)
            catch (Exception ex)
                {
                java.util.logging.Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
                }
            String response = "ok";
            return new ResponseEntity<>(response, HttpStatus.OK);
            }

//	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
//	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
//		logger.info("Start createEmployee.");
//		emp.setCreatedDate(new Date());
//		empData.put(emp.getId(), emp);
//		return emp;
//	}
	
//	@RequestMapping(value = EmpRestURIConstants.DELETE_EMP, method = RequestMethod.DELETE)
//	public @ResponseBody Employee deleteEmployee(@PathVariable("id") int empId) {
//		logger.info("Start deleteEmployee.");
//		Employee emp = empData.get(empId);
//		empData.remove(empId);
//		return emp;
//	}

//	@RequestMapping(value = "/saveToFile", method = RequestMethod.POST)
//	public @ResponseBody
//	ProgramMemory saveToFile() {
//		logger.info("saveToFile()");
//		TomcatApp.myRest.saveProgramMemoryFile( TomcatApp.getProgramMemory() );
////		return "saveToFile";
//		return TomcatApp.getProgramMemory();
//	}

	@RequestMapping(value = JfpRestURIConstants.SYS_STOP, method = RequestMethod.GET)
	public @ResponseBody void stop() {
            logger.info("Stop server");

            System.exit(0);
	}

	@RequestMapping(value = JfpRestURIConstants.SYS_PING, method = RequestMethod.GET)
	public @ResponseBody String ping() {
            logger.info("Ping server");

            return "RUNNING";
	}

	@RequestMapping( value = JfpRestURIConstants.SYS_GET_FILESYS, method = RequestMethod.GET )
	public @ResponseBody int getFilesys() {
            logger.info("SYS_GET_FILESYS()");
            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
                {
                logger.info("SYS_GET_FILESYS - DOS");
                return Constants.FILESYSTEM_DOS;
                }
            logger.info("SYS_GET_FILESYS - POSIX");
            return Constants.FILESYSTEM_POSIX;
	}

    public ResultsData searchBtnAction( SearchModel searchModel )
        {
        SearchFiles searchAction = new SearchFiles();
        return searchAction.find( searchModel );
        }

//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(JfpController.class, args );
//	}
}
