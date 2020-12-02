package com.towianski.controllers;

import com.towianski.boot.GlobalMemory;
import com.towianski.httpsutils.FileStorageException;
import com.towianski.httpsutils.UploadFileResponse;
import com.towianski.jfileprocessor.restservices.SearchFiles;
import com.towianski.jfileprocessor.restservices.CopyFiles;
import com.towianski.jfileprocessor.restservices.DeleteFiles;
import com.towianski.models.CommonFileAttributes;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.Constants;
import com.towianski.models.CopyModel;
import com.towianski.models.DeleteModel;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.models.ResultsData;
import com.towianski.models.SearchModel;
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.FileUtils;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Stan Towianski
 * 
 */
@RestController
@EnableAutoConfiguration
@Profile({"server|warserver"})
//@Profile("warserver")
public class JfpController {
	
    private static final MyLogger logger = MyLogger.getLogger( JfpController.class.getName() );

    // not working - @PreAuthorize("hasPermission(#searchModel.StartingFolder, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping(value = JfpRestURIConstants.SEARCH, method = RequestMethod.POST)
    public ResponseEntity<ResultsData> search(@RequestBody SearchModel searchModel) {

        logger.fine( "You entered SearchModel =" + Rest.saveObjectToString( searchModel ) );
        //ResultsData response = new ResultsData();
        //response.setMessage( "You entered SearchModel parm =" + parm );
        ResultsData response = searchBtnAction( searchModel );
        
        //logger.finest( "Rest Controller will return resultsData =" + Rest.saveObjectToString( response ) );
        //ResponseEntity<ResultsData> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        //logger.fine("rest before return Status Code: " + responseEntity.getStatusCode());	
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize( "isAuthenticated()" )
    @RequestMapping(value = JfpRestURIConstants.COPY, method = RequestMethod.POST)
    public ResponseEntity<ResultsData> copy(@RequestBody CopyModel copyModel) 
        {
        logger.info( "You entered CopyModel parm =" + copyModel );
        CopyFiles copyAction = new CopyFiles( copyModel );
        ResultsData response = copyAction.doInBackground();
        return new ResponseEntity<>(response, HttpStatus.OK);
        }

    @PreAuthorize( "isAuthenticated()" )
    @RequestMapping(value = JfpRestURIConstants.DELETE, method = RequestMethod.POST, consumes = "application/json" )
    public ResponseEntity<ResultsData> delete(@RequestBody DeleteModel deleteModel) 
        {
        //DeleteModel deleteModel = (DeleteModel) Rest.jsonToObject( deleteModelStr, new TypeReference<DeleteModel>(){} );
        //logger.info( "You entered deleteModel parm =" + deleteModel );
        logger.info( "You entered deleteModel parm =" + Rest.saveObjectToString( deleteModel ) );
        DeleteFiles deleteFiles = new DeleteFiles( deleteModel );

        ResultsData response = deleteFiles.doInBackground();
        return new ResponseEntity<>(response, HttpStatus.OK);
        }

    @PreAuthorize("hasPermission(#oldname, 'com.towianski.models.ServerUserFileRights', 'w')")
    //@PreAuthorize("@accessChecker.check('book', #bookinType)")
    @RequestMapping(value = JfpRestURIConstants.RENAME_FILE, method = RequestMethod.PUT)
//        public ResponseEntity<String> rename(@PathVariable("oldname") String oldname, @PathVariable("newname") String newname) 
    public ResponseEntity<String> rename(@RequestParam("oldname") String oldname, @RequestParam("newname") String newname)
        {
        logger.info( "rename oldname =" + oldname + "=   newname =" + newname + "=" );
        Path newpath = Paths.get( newname );
        if ( ! GlobalMemory.getSecUtils().hasPermission( newpath.getParent(), "w" ) )
            {
            logger.info( "Do not have Write permission on folder =" + newpath.getParent() );
            return new ResponseEntity<>( "not_ok", HttpStatus.UNAUTHORIZED );
            }
        try
            {
            FileUtils.fileMove( new ConnUserInfo(), oldname, newname );
            }
        catch (Exception ex)
            {
            logger.severeExc(ex );
            }
        return new ResponseEntity<>("ok", HttpStatus.OK);
        }

    @PreAuthorize("authentication.name == 'admin'" )
    @RequestMapping(value = JfpRestURIConstants.SYS_STOP, method = RequestMethod.GET)
    public @ResponseBody void stop() {
        logger.info("Stop server");

        System.exit(0);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = JfpRestURIConstants.SYS_PING, method = RequestMethod.GET)
    public @ResponseBody String ping() {
        logger.info("Ping server");

        return "RUNNING";
    }

    @PreAuthorize("permitAll")
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

    @PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.GET_FILE_SIZE, method = RequestMethod.PUT )
    public ResponseEntity<Long> getFileSize(@RequestParam("filename") String filename) {
        try {
            logger.info("GET_FILE_SIZE()");
            BasicFileAttributes attr = Files.readAttributes( Paths.get( URLDecoder.decode( filename, "UTF-8" ) ), BasicFileAttributes.class );
            logger.info( "TomcatAppMonitor.run() GET_FILE_SIZE for filename =" + filename + "=    attr.size() = " + attr.size() );
            return new ResponseEntity<>( attr.size(), HttpStatus.OK );
//            return attr.size();
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            }
        return new ResponseEntity<Long>( (long) -1, HttpStatus.OK );
        }
    
    @PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.GET_FILE_STAT, method = RequestMethod.PUT )
    public ResponseEntity<String> getFileStat(@RequestParam("filename") String filename) {
        try {
            logger.info("GET_FILE_STAT()");
            BasicFileAttributes attr = Files.readAttributes( Paths.get( URLDecoder.decode( filename, "UTF-8" ) ), BasicFileAttributes.class );
            CommonFileAttributes cfa = new CommonFileAttributes();
            cfa.setDirectory( attr.isDirectory() );
            logger.info( "GET_FILE_STAT for filename =" + filename + "=    attr.size() = " + attr.size() );
            return new ResponseEntity<>( Rest.saveObjectToString( cfa ), HttpStatus.OK );
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            }
        return new ResponseEntity<String>( "", HttpStatus.OK );
        }
    
    @PreAuthorize( "isAuthenticated()" )
    @RequestMapping( value = JfpRestURIConstants.GET_USER_HOME, method = RequestMethod.GET )
    public @ResponseBody String getUserHome() {
        try {
            logger.info( "GET_USER_HOME  =" + DesktopUtils.getJfpHome(true).toString() );
            return DesktopUtils.getJfpHome(true).toString();
            } 
        catch (Exception ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            }
        return "";
        }

    @PreAuthorize("hasPermission(#fileName, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.GET_FILE, method = RequestMethod.GET )
    public ResponseEntity<Resource> getFile( String fileName ) throws IOException
        {
        logger.info( "fileName =" + fileName + "=" );
        File file = new File( fileName );
        Path path = Paths.get( file.getAbsolutePath() );
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
                                
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        }
    
    @PreAuthorize("hasPermission(#fileName, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.DOES_FILE_EXIST, method = RequestMethod.GET )
    public @ResponseBody Boolean doesFileExist( String fileName ) throws IOException
        {
        logger.info( "fileName =" + fileName + "=" );
        File file = null;

        try {
            file = new File( fileName );
            } 
        catch (Exception ex) 
            {
            return false;
            }

        return file.exists();
        }

        
//    @PostMapping("/jfp/uploadFile")
//    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
//        String fileName = fileStorageService.storeFile(file);
//
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(fileName)
//                .toUriString();    @RequestMapping( value = JfpRestURIConstants.GET_FILE, method = RequestMethod.GET )
//
//        System.out.println( "fileName =" + fileName + "=" );
//        System.out.println( "fileDownloadUri =" + fileDownloadUri + "=" );
//        System.out.println( "file.getContentType() =" + file.getContentType() + "=" );
//        System.out.println( "file.getSize() =" + file.getSize() + "=" );
//        
//        return new UploadFileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
//    }

    //@PreAuthorize("hasPermission(#fileName, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.SEND_FILE, method = RequestMethod.POST )
//    public ResponseEntity<UploadFileResponse> sendFile( MultipartFile source, String target ) throws IOException
    public ResponseEntity<UploadFileResponse> sendFile( @RequestParam("source") MultipartFile source, @RequestParam("target") String target ) throws IOException
        {
        logger.info( "entered sendFile() in jfpController" );
//        File file = new File( fileName.getName() );
//        Path path = Paths.get( file.getAbsolutePath() );
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//        String fileName2 = fileStorageService.storeFile(file);

        String fileName = StringUtils.cleanPath( source.getOriginalFilename());
        Path targetLocation = null;

        logger.info( "fileName =" + fileName + "=" );
        logger.info( "file.getContentType() =" + source.getContentType() + "=" );
        logger.info( "file.getSize() =" + source.getSize() + "=" );

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            //Path targetLocation = Paths.get( "/net2/tmp/" + Paths.get( fileName ).getFileName().toString() );
            targetLocation = Paths.get( target );
            logger.info( "targetLocation =" + targetLocation.toString() + "=" );
            
            logger.finest( "check dir rights for =" + targetLocation );
            if ( ! GlobalMemory.getSecUtils().hasPermission( targetLocation.getParent(), "w" ) )
                {
                logger.info( "Do not have Write permission on folder =" + targetLocation.getParent() );
                return new ResponseEntity<>( new UploadFileResponse(targetLocation.getFileName().toString(), targetLocation.getParent().toString(),
                source.getContentType(), source.getSize()), HttpStatus.UNAUTHORIZED );
                //throw new Exception( "https: \"" + "\" does not have folder permissions" );
                }
            
            Files.copy( source.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING );
            
            // extra stuff to fixxx dealing with extended attributes or something...
////            UserPrincipal owner = Files.getOwner( targetLocation );
////            System.out.println("Owner: " + owner);
////
////            System.out.println("-- lookup other user --");
////            FileSystem fileSystem = targetLocation.getFileSystem();
////            UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
////            UserPrincipal userPrincipal = service.lookupPrincipalByName("joe");
////            System.out.println("Found UserPrincipal: " + userPrincipal);
//
//            //changing owner
//            Files.setOwner(targetLocation, userPrincipal);

//            There are scenarios where the file attributes defined in the file system are not sufficient for your needs. Should you come across such a case and require to set your own attributes on a file, then the UserDefinedFileAttributeView interface will come in handy:
//
//            Path path = Paths.get("somefile");
//            UserDefinedFileAttributeView userDefView = Files.getFileAttributeView(
//              attribPath, UserDefinedFileAttributeView.class);
//            To retrieve the list of user defined attributes already defined for the file represented by the above view:
//
//            List<String> attribList = userDefView.list();
//            To set a user-defined attribute on the file, we use the following idiom:
//
//            String name = "attrName";
//            String value = "attrValue";
//            userDefView.write(name, Charset.defaultCharset().encode(value));
//            When you need to access the user defined attributes, you can loop over the attribute list returned by the view and inspect them using this idiom:
//
//            ByteBuffer attrValue = ByteBuffer.allocate(userView.size(attrName));
//            userDefView.read(attribName, attribValue);
//            attrValue.flip();
//            String attrValue = Charset.defaultCharset().decode(attrValue).toString();
//            To remove a user-defined attribute from the file, we simply call the delete API of the view:
//
//            userDefView.delete(attrName);


            //return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }

//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path(targetLocation.toString())
//                .toUriString(); 
//        logger.info( "fileDownloadUri =" + fileDownloadUri + "=" );
        
        return new ResponseEntity<>(new UploadFileResponse(targetLocation.getFileName().toString(), targetLocation.getParent().toString(),
                source.getContentType(), source.getSize()), HttpStatus.OK);

//        return new UploadFileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
        }

//    @PostMapping("/uploadMultipleFiles")
//    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) 
//        {
//        return Arrays.asList(files)
//                .stream()
//                .map(file -> uploadFile(file))
//                .collect(Collectors.toList());
//        }
    
    @PreAuthorize("hasPermission(#dir, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.MKDIR, method = RequestMethod.PUT )
    public ResponseEntity<String> mkDir(@RequestParam("dir") String dir) {

        String retMsg = "";
        Path dirPath = null;
        try {
            logger.info("MKDIR()");
            dirPath = Paths.get( URLDecoder.decode( dir, "UTF-8" ) );
            
            logger.finest( "check dir rights for =" + dirPath );
            if ( ! GlobalMemory.getSecUtils().hasPermission( dirPath.getParent(), "w" ) )
                {
                logger.info( "Do not have Write permission on folder so skip the whole folder =" + dirPath.getParent() );
                return new ResponseEntity<>( "Do not have Write permission on folder =" + dirPath.getParent(), HttpStatus.UNAUTHORIZED );
                //throw new Exception( "https: \"" + "\" does not have folder permissions" );
                }

            Files.createDirectory( dirPath );
            //BasicFileAttributes attr = Files.readAttributes( dirPath, BasicFileAttributes.class );
            logger.info( "MKDIR for dirPath =" + dirPath + "=" );
            return new ResponseEntity<>( retMsg, HttpStatus.OK );
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            retMsg = "Error creating directory (" + dirPath.toString() + ") - " + ex.getLocalizedMessage();
            }
        return new ResponseEntity<>( retMsg, HttpStatus.OK );
        }
    
    @PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.RM, method = RequestMethod.PUT )
    public ResponseEntity<String> rm(@RequestParam("filename") String filename) {

        String retMsg = "";
        Path filePath = null;
        try {
            logger.info("RM()");
            filePath = Paths.get( URLDecoder.decode( filename, "UTF-8" ) );
            Files.deleteIfExists( filePath );
            logger.info( "RM for filePath =" + filePath + "=" );
            retMsg = "deleted";
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            retMsg = "Error rm (" + filePath.toString() + ") - " + ex.getLocalizedMessage();
            }
        return new ResponseEntity<>( retMsg, HttpStatus.OK );
        }
    
    @PreAuthorize("hasPermission(#dir, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.RMDIR, method = RequestMethod.PUT )
    public ResponseEntity<String> rmDir(@RequestParam("dir") String dir) {

        String retMsg = "";
        Path dirPath = null;
        try {
            logger.info("RMDIR()");
            dirPath = Paths.get( URLDecoder.decode( dir, "UTF-8" ) );
            Files.deleteIfExists( dirPath );
            logger.info( "RMDIR for dirPath =" + dirPath + "=" );
            return new ResponseEntity<>( retMsg, HttpStatus.OK );
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            retMsg = "Error rmDir (" + dirPath.toString() + ") - " + ex.getLocalizedMessage();
            }
        return new ResponseEntity<>( retMsg, HttpStatus.OK );
        }

    @PreAuthorize("permitAll")
    @RequestMapping(value = JfpRestURIConstants.HTTPS_CONNECT, method = RequestMethod.GET)
    public @ResponseBody String httpsConnect() {
        logger.info("https_connect");

//        public void login(HttpServletRequest req, String user, String pass) { 
//    UsernamePasswordAuthenticationToken authReq
//      = new UsernamePasswordAuthenticationToken(user, pass);
//    Authentication auth = authManager.authenticate(authReq);
//    
//    SecurityContext sc = SecurityContextHolder.getContext();
//    sc.setAuthentication(auth);
//    HttpSession session = req.getSession(true);
//    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
//}
        return "OK";
    }
    
        
    @PreAuthorize("permitAll")
    public ArrayList<Object> getPermissions()
        {
        ArrayList<Object> rightsList = new ArrayList<Object>();
        rightsList.add( "all" );
        logger.info( "rightsList add All" );

        return rightsList;        
        }
}
