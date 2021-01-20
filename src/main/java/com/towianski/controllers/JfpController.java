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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;

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
    //private int controllerOnFsType = -1;  // tried this once. it did not work. maybe because controller is not singleton? did not look more
    
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

    //@PreAuthorize("hasPermission(#oldname, 'com.towianski.models.ServerUserFileRights', 'w')")
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
            //controllerOnFsType = Constants.FILESYSTEM_DOS;
            return Constants.FILESYSTEM_DOS;
            }
        logger.info("SYS_GET_FILESYS - POSIX");
        //controllerOnFsType = Constants.FILESYSTEM_POSIX;
        return Constants.FILESYSTEM_POSIX;
    }

    public ResultsData searchBtnAction( SearchModel searchModel )
        {
        SearchFiles searchAction = new SearchFiles();
        return searchAction.find( searchModel );
        }

    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.GET_FILE_SIZE, method = RequestMethod.PUT )
    public ResponseEntity<Long> getFileSize(@RequestParam("filename") String filename) {
        try {
            logger.info("GET_FILE_SIZE()");
            Path filepath = Paths.get( URLDecoder.decode( filename, "UTF-8" ) );
            if ( ! GlobalMemory.getSecUtils().hasPermission( filepath.getParent(), "r" ) )
                {
                logger.info( "Do not have Read permission on folder =" + filepath.getParent() );
                return new ResponseEntity<>( -1L, HttpStatus.UNAUTHORIZED );
                }
            BasicFileAttributes attr = Files.readAttributes( filepath, BasicFileAttributes.class );
            logger.info( "TomcatAppMonitor.run() GET_FILE_SIZE for filename =" + filename + "=    attr.size() = " + attr.size() );
            return new ResponseEntity<>( attr.size(), HttpStatus.OK );
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            }
        return new ResponseEntity<Long>( (long) -1, HttpStatus.OK );
        }
    
    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.GET_FILE_STAT, method = RequestMethod.PUT )
    public ResponseEntity<String> getFileStat(@RequestParam("filename") String filename) {
        try {
            logger.info("GET_FILE_STAT()");
            CommonFileAttributes cfa = new CommonFileAttributes();

            Path filepath = Paths.get( URLDecoder.decode( filename, "UTF-8" ) );
            if ( ! GlobalMemory.getSecUtils().hasPermission( filepath.getParent(), "r" ) )
                {
                logger.info( "Do not have Read permission on folder =" + filepath.getParent() );
                return new ResponseEntity<>( Rest.saveObjectToString( cfa ), HttpStatus.UNAUTHORIZED );
                }
            BasicFileAttributes attr = Files.readAttributes( filepath, BasicFileAttributes.class );
            cfa.setDirectory( attr.isDirectory() );
            logger.info( "GET_FILE_STAT for filename =" + filename + "=    attr.size() = " + attr.size() );
            logger.finer( "GET_FILE_STAT cfa =" + Rest.saveObjectToString(cfa) + "=" );
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

//    @PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
//    @RequestMapping( value = JfpRestURIConstants.GET_FILE, method = RequestMethod.GET )
//    public ResponseEntity<Resource> getFile( String filename ) throws IOException
//        {
//        logger.info( "GET_FILE filename =" + filename + "=" );
//        File file = new File( filename );
//        Path path = Paths.get( file.getAbsolutePath() );
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));   // bombs on outOfMemory for big files !
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//                                
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//        }
    
    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.GET_FILE, method = RequestMethod.GET )
    //public ResponseEntity<Resource> getFile( String filename ) throws IOException
    public void getFile( HttpServletResponse response, String filename ) throws IOException
//            @RequestParam(defaultValue = DEFAULT_FILE_NAME) String filename) throws IOException
        {
        logger.info( "GET_FILE filename =" + filename + "=" );
        filename = URLDecoder.decode( filename, "UTF-8" );
        File file = new File( filename );
        Path filepath = Paths.get( URLDecoder.decode( filename, "UTF-8" ) );
        if ( ! GlobalMemory.getSecUtils().hasPermission( filepath.getParent(), "r" ) )
            {
            logger.info( "Do not have Read permission on folder =" + filepath.getParent() );
            return;  // new ResponseEntity<>( "not_ok", HttpStatus.UNAUTHORIZED );
            }
        //Path path = Paths.get( file.getAbsolutePath() );
        //ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//                                
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
        
        //MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, filename);
        //System.out.println("filename: " + filename);
        //System.out.println("mediaType: " + mediaType);
 
        response.setContentType( MediaType.APPLICATION_OCTET_STREAM_VALUE );  //mediaType.getType());
 
        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
 
        // Content-Length
        //response.setContentLength((int) file.length());
        response.addHeader( "X-jfp.file.length", ""  + file.length() );
 
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
 
        byte[] buffer = new byte[1024000];   // arbitrary sort of size
        int bytesRead = 0;
        while ((bytesRead = inStream.read(buffer)) != -1) 
            {
            logger.fine( "GET_FILE bytesRead =" + bytesRead + "=" );
            outStream.write(buffer, 0, bytesRead);
            }
        logger.info( "GET_FILE Done" );

        inStream.close();        
        logger.fine( "GET_FILE after ins close" );
        outStream.flush();
        logger.fine( "GET_FILE after out flush" );
        outStream.close();
        logger.fine( "GET_FILE after out close" );
        }
    
//    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String filename) {
//        // application/pdf
//        // application/xml
//        // image/gif, ...
//        String mineType = servletContext.getMimeType(filename);
//        try {
//            MediaType mediaType = MediaType.parseMediaType(mineType);
//            return mediaType;
//        } catch (Exception e) {
//            return MediaType.APPLICATION_OCTET_STREAM;
//        }
//    }

        // Using Webclient - for the future
//    @GetMapping( produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
//    public void downloadFile( HttpServletResponse response ) throws IOException
//    {
//        Flux<DataBuffer> dataStream = this.downloadFileUrl( );
//
//        // Streams the stream from response instead of loading it all in memory
//        DataBufferUtils.write( dataStream, response.getOutputStream() )
//                .map( DataBufferUtils::release )
//                .blockLast();
//    }

//    @GetMapping("/pdfFile")
//    public ResponseEntity<StreamingResponseBody> streamPdfFile() throws FileNotFoundException {
//      String filename = "Technicalsand.com sample data.pdf";
//      File file = ResourceUtils.getFile("classpath:static/" + filename);
//      StreamingResponseBody responseBody = outputStream -> {
//         Files.copy(file.toPath(), outputStream);
//      };
//      return ResponseEntity.ok()
//            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Downloaded_" + filename)
//            .contentType(MediaType.APPLICATION_PDF)
//            .body(responseBody);
//    }
    
    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'r')")
    @RequestMapping( value = JfpRestURIConstants.DOES_FILE_EXIST, method = RequestMethod.GET )
    public @ResponseBody Boolean doesFileExist( String filename ) throws IOException
        {
        filename = URLDecoder.decode( filename, "UTF-8" );
        logger.info( "DOES_FILE_EXIST filename =" + filename + "=" );
        File file = null;

        try {
            Path filepath = Paths.get( URLDecoder.decode( filename, "UTF-8" ) );
            if ( ! GlobalMemory.getSecUtils().hasPermission( filepath.getParent(), "r" ) )
                {
                logger.info( "Do not have Read permission on folder =" + filepath.getParent() );
                return false;
                }
            file = new File( filename );
            } 
        catch (Exception ex) 
            {
            return false;
            }

        return file.exists();
        }
    
    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.DOES_FILE_EXIST_AND_CAN_WRITE, method = RequestMethod.GET )
    public ResponseEntity<Boolean> doesFileExistAndCanWrite( String filename ) throws IOException
        {
        filename = URLDecoder.decode( filename, "UTF-8" );
        logger.info( "filename =" + filename + "=" );
        File file = null;

        try {
            if ( ! GlobalMemory.getSecUtils().hasPermission( Paths.get( filename ).getParent(), "w" ) )
                {
                logger.info( "Do not have Write permission on folder =" + Paths.get( filename ).getParent() );
                return new ResponseEntity<>( Boolean.FALSE, HttpStatus.UNAUTHORIZED );
                }

            file = new File( filename );
            } 
        catch (Exception ex) 
            {
            return new ResponseEntity<>( Boolean.FALSE, HttpStatus.BAD_REQUEST );
            }

        return new ResponseEntity<>( file.exists(), HttpStatus.OK );
        }

        
//    @PostMapping("/jfp/uploadFile")
//    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
//        String filename = fileStorageService.storeFile(file);
//
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(filename)
//                .toUriString();    @RequestMapping( value = JfpRestURIConstants.GET_FILE, method = RequestMethod.GET )
//
//        System.out.println( "filename =" + filename + "=" );
//        System.out.println( "fileDownloadUri =" + fileDownloadUri + "=" );
//        System.out.println( "file.getContentType() =" + file.getContentType() + "=" );
//        System.out.println( "file.getSize() =" + file.getSize() + "=" );
//        
//        return new UploadFileResponse(filename, fileDownloadUri,
//                file.getContentType(), file.getSize());
//    }

//    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'w')")
//    @RequestMapping( value = JfpRestURIConstants.SEND_FILE, method = RequestMethod.POST )
////    public ResponseEntity<UploadFileResponse> sendFile( MultipartFile source, String target ) throws IOException
//    public ResponseEntity<UploadFileResponse> sendFile( @RequestParam("source") MultipartFile source, @RequestParam("target") String target ) throws IOException
//        {
//        logger.info( "entered sendFile() in jfpController" );
////        File file = new File( filename.getName() );
////        Path path = Paths.get( file.getAbsolutePath() );
////        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
////        String filename2 = fileStorageService.storeFile(file);
//
//        String filename = StringUtils.cleanPath( source.getOriginalFilename() );
//        Path targetLocation = null;
//
//        logger.info( "filename =" + filename + "=" );
//        logger.info( "file.getContentType() =" + source.getContentType() + "=" );
//        logger.info( "file.getSize() =" + source.getSize() + "=" );
//
//        try {
//            // Check if the file's name contains invalid characters
//            if(filename.contains("..")) {
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
//            }
//
//            // Copy file to the target location (Replacing existing file with the same name)
//            //Path targetLocation = Paths.get( "/net2/tmp/" + Paths.get( filename ).getFileName().toString() );
//            targetLocation = Paths.get( target );
//            logger.info( "targetLocation =" + targetLocation.toString() + "=" );
//            
//            logger.finest( "check dir rights for =" + targetLocation );
//            if ( ! GlobalMemory.getSecUtils().hasPermission( targetLocation.getParent(), "w" ) )
//                {
//                logger.info( "Do not have Write permission on folder =" + targetLocation.getParent() );
//                return new ResponseEntity<>( new UploadFileResponse(targetLocation.getFileName().toString(), targetLocation.getParent().toString(),
//                source.getContentType(), source.getSize()), HttpStatus.UNAUTHORIZED );
//                //throw new Exception( "https: \"" + "\" does not have folder permissions" );
//                }
//            
//            Files.copy( source.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING );  // FIXXX pass replace flag and not auto replace !
//            logger.info( "Done SendFile targetLocation =" + targetLocation.toString() + "=" );
//            
//            // extra stuff to fixxx dealing with extended attributes or something...
//////            UserPrincipal owner = Files.getOwner( targetLocation );
//////            System.out.println("Owner: " + owner);
//////
//////            System.out.println("-- lookup other user --");
//////            FileSystem fileSystem = targetLocation.getFileSystem();
//////            UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
//////            UserPrincipal userPrincipal = service.lookupPrincipalByName("joe");
//////            System.out.println("Found UserPrincipal: " + userPrincipal);
////
////            //changing owner
////            Files.setOwner(targetLocation, userPrincipal);
//
////            There are scenarios where the file attributes defined in the file system are not sufficient for your needs. Should you come across such a case and require to set your own attributes on a file, then the UserDefinedFileAttributeView interface will come in handy:
////
////            Path path = Paths.get("somefile");
////            UserDefinedFileAttributeView userDefView = Files.getFileAttributeView(
////              attribPath, UserDefinedFileAttributeView.class);
////            To retrieve the list of user defined attributes already defined for the file represented by the above view:
////
////            List<String> attribList = userDefView.list();
////            To set a user-defined attribute on the file, we use the following idiom:
////
////            String name = "attrName";
////            String value = "attrValue";
////            userDefView.write(name, Charset.defaultCharset().encode(value));
////            When you need to access the user defined attributes, you can loop over the attribute list returned by the view and inspect them using this idiom:
////
////            ByteBuffer attrValue = ByteBuffer.allocate(userView.size(attrName));
////            userDefView.read(attribName, attribValue);
////            attrValue.flip();
////            String attrValue = Charset.defaultCharset().decode(attrValue).toString();
////            To remove a user-defined attribute from the file, we simply call the delete API of the view:
////
////            userDefView.delete(attrName);
//
//
//            //return filename;
//        } catch (IOException ex) {
//            throw new FileStorageException("Could not store file " + filename + ". Please try again!", ex);
//        }
//
////        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
////                .path(targetLocation.toString())
////                .toUriString(); 
////        logger.info( "fileDownloadUri =" + fileDownloadUri + "=" );
//        
//        return new ResponseEntity<>(new UploadFileResponse(targetLocation.getFileName().toString(), targetLocation.getParent().toString(),
//                source.getContentType(), source.getSize()), HttpStatus.OK);
//
////        return new UploadFileResponse(filename, fileDownloadUri,
////                file.getContentType(), file.getSize());
//        }

    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.SEND_FILE, method = RequestMethod.POST )
//    public ResponseEntity<UploadFileResponse> sendFile( MultipartFile source, String target ) throws IOException
//    public ResponseEntity<UploadFileResponse> sendFileSw( @RequestParam("source") MultipartFile source, @RequestParam("target") String target ) throws IOException
    //public void sendFileSw( HttpServletResponse response, HttpServletRequest request, String source, String target ) throws IOException
    public ResponseEntity<UploadFileResponse> sendFileSw( HttpServletResponse response, HttpServletRequest request, 
            @RequestParam("source") String source, @RequestParam("target") String target, @RequestParam("fileLength") String fileLengthStr  ) throws IOException
        {
        logger.info( "entered sendFileSw() in jfpController" );
//        File file = new File( filename.getName() );
//        Path path = Paths.get( file.getAbsolutePath() );
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//        String filename2 = fileStorageService.storeFile(file);

        String filename = StringUtils.cleanPath( URLDecoder.decode( source, "UTF-8" ) );  //StringUtils.cleanPath( source );  //.getOriginalFilename() );
        Path targetLocation = null;
        long fileLength = Long.parseLong( fileLengthStr );

        target = URLDecoder.decode( target, "UTF-8" );

        logger.info( "filename =" + filename + "=" );
        logger.info( "target =" + target + "=" );
        logger.info( "fileLength =" + fileLength + "=" );

//        if ( controllerOnFsType == Constants.FILESYSTEM_POSIX )  // because param comes on url line where / are not allowed so it comes over as \ converted.
//            {
//            target = target.replace( "\\", "/" );
//            logger.info( "posix target =" + target + "=" );
//            }

        try {
            // Check if the file's name contains invalid characters
            if(filename.contains("..")) 
                {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
                }

            // Copy file to the target location (Replacing existing file with the same name)
            //Path targetLocation = Paths.get( "/net2/tmp/" + Paths.get( filename ).getFileName().toString() );
            targetLocation = Paths.get( target );
            logger.info( "targetLocation =" + targetLocation.toString() + "=" );
            
            logger.finest( "check dir rights for =" + targetLocation );
            if ( ! GlobalMemory.getSecUtils().hasPermission( targetLocation.getParent(), "w" ) )
                {
                logger.info( "Do not have Write permission on folder =" + targetLocation.getParent() );
                //request.getInputStream().skip(fileLength);
                //logger.finest( "skipped reading filelength =" + fileLength );
                request.getInputStream().close();
                logger.finest( "close input stream to stop file send !" );
                return new ResponseEntity<>( new UploadFileResponse(targetLocation.getFileName().toString(), targetLocation.getParent().toString(),
                "source.getContentType()", 0), HttpStatus.UNAUTHORIZED );
                //throw new Exception( "https: \"" + "\" does not have folder permissions" );
                }
            else
                {
    //            Files.copy( source.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING );  // FIXXX pass replace flag and not auto replace !
    //                logger.info( "getFileViaRestTemplate save response body to target =" + target + "=" );
                Path path = Paths.get( target );
                long totBytesRead = 0;
                //long fileLength = Long.parseLong( request.getHeaders().get( "X-jfp.file.length" ).get(0) );
                logger.info( "sendFileSw() get response header fileLength =" + fileLength + "=" );

                //ByteArrayInputStream bais = (ByteArrayInputStream) response.getBody();
                FileOutputStream fos = new FileOutputStream( path.toFile() );
                BufferedOutputStream outStream = new BufferedOutputStream( fos );

                try {
                    byte[] buffer = new byte[102400]; //just arbitrary size   
                    int bytesRead = 0;
                    while(bytesRead != -1)
                        {
                        bytesRead = request.getInputStream().read( buffer, 0, 102400 ); //-1, 0, or more
                        logger.finest( "sendFileSw() read buffer bytesRead =" + bytesRead + "=" );
                        if ( bytesRead > 0 )
                            {
                            outStream.write( buffer, 0, bytesRead );
                            totBytesRead += bytesRead;
                            logger.finest( "sendFileSw() totBytesRead =" + totBytesRead + "=" );
                            // I have to find and stop at fileLength otherwise it seems to wait for a 1 minute readTimeout
                            // before it detects the end of file send!
                            if ( totBytesRead >= fileLength )
                                {
                                logger.fine( "sendFileSw() STOP at fileLength totBytesRead =" + totBytesRead + "=" );
                                break;
                                }
                            }
                        }
                    logger.fine( "sendFileSw() Done Reading/writing totBytesRead =" + totBytesRead + "=" );
                    }
                catch( Exception exc )
                    {
                    Writer buffer = new StringWriter();
                    PrintWriter pw = new PrintWriter(buffer);
                    exc.printStackTrace(pw);
                    logger.info( "Exception: " + buffer.toString() );
                    }
                finally 
                    {
                    outStream.flush();
                    if (outStream != null) outStream.close();
                    if (fos != null) fos.close();
                    }
                }
            }
        catch( Exception exc )
            {
            Writer buffer = new StringWriter();
            PrintWriter pw = new PrintWriter(buffer);
            exc.printStackTrace(pw);
            logger.info( "Exception: " + buffer.toString() );
            }
        logger.info( "Done sendFileSw() targetLocation =" + targetLocation.toString() + "=" );
            
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


            //return filename;

//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path(targetLocation.toString())
//                .toUriString(); 
//        logger.info( "fileDownloadUri =" + fileDownloadUri + "=" );
        
        logger.info( "targetLocation.getFileName().toString() =" + targetLocation.getFileName().toString() + "=" );
        logger.info( "targetLocation.getParent().toString() =" + targetLocation.getParent().toString() + "=" );
        logger.info( "fileLength =" + fileLength + "=" );
        return new ResponseEntity<>(new UploadFileResponse(targetLocation.getFileName().toString(), targetLocation.getParent().toString(),
                "source.getContentType()", fileLength ), HttpStatus.OK);

//        return new UploadFileResponse(filename, fileDownloadUri,
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
    
    //@PreAuthorize("hasPermission(#dir, 'com.towianski.models.ServerUserFileRights', 'w')")
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
    
    //@PreAuthorize("hasPermission(#filename, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.RM, method = RequestMethod.PUT )
    public ResponseEntity<String> rm(@RequestParam("filename") String filename) {

        String retMsg = "";
        Path filePath = null;
        try {
            logger.info("RM()");
            filePath = Paths.get( URLDecoder.decode( filename, "UTF-8" ) );
            logger.info( "RM for filePath =" + filePath + "=" );
            if ( ! GlobalMemory.getSecUtils().hasPermission( filePath.getParent(), "w" ) )
                {
                logger.info( "Do not have Write permission on folder =" + filePath.getParent() );
                return new ResponseEntity<>( "Do not have Write permission on folder =" + filePath.getParent(), HttpStatus.UNAUTHORIZED );
                }

            Files.deleteIfExists( filePath );
            logger.info( "RM done" );
            retMsg = "deleted";
            } 
        catch (IOException ex) 
            {
            Logger.getLogger(JfpController.class.getName()).log(Level.SEVERE, null, ex);
            retMsg = "Error rm (" + filePath.toString() + ") - " + ex.getLocalizedMessage();
            }
        return new ResponseEntity<>( retMsg, HttpStatus.OK );
        }
    
    //@PreAuthorize("hasPermission(#dir, 'com.towianski.models.ServerUserFileRights', 'w')")
    @RequestMapping( value = JfpRestURIConstants.RMDIR, method = RequestMethod.PUT )
    public ResponseEntity<String> rmDir(@RequestParam("dir") String dir) {

        String retMsg = "";
        Path dirPath = null;
        try {
            logger.info("RMDIR()");
            dirPath = Paths.get( URLDecoder.decode( dir, "UTF-8" ) );
            logger.info( "RMDIR for dirPath =" + dirPath + "=" );
            if ( ! GlobalMemory.getSecUtils().hasPermission( dirPath.getParent(), "w" ) )
                {
                logger.info( "Do not have Write permission on folder =" + dirPath.getParent() );
                return new ResponseEntity<>( "Do not have Write permission on folder =" + dirPath.getParent(), HttpStatus.UNAUTHORIZED );
                }

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
