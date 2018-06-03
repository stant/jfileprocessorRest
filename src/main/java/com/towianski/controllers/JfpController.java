package com.towianski.controllers;

import com.towianski.jfileprocessor.restservices.SearchFiles;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Level;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Stan Towianski
 * 
 */
@Controller
@EnableAutoConfiguration
@Profile("server")
public class JfpController {
	
	private static final Logger logger = LoggerFactory.getLogger(JfpController.class);

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
