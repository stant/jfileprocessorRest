/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.controllers;

/**
 *
 * @author stan
 */

import com.towianski.httpsutils.FileStorageService;
import com.towianski.httpsutils.UploadFileResponse;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;

@RestController
//@Controller
@EnableAutoConfiguration
@Profile({"server|warserver"})
public class HttpsFileController {

    private static final Logger logger = LoggerFactory.getLogger(HttpsFileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/jfp/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(filename)
                .toUriString();
        System.out.println( "filename =" + filename + "=" );
        System.out.println( "fileDownloadUri =" + fileDownloadUri + "=" );
        System.out.println( "file.getContentType() =" + file.getContentType() + "=" );
        System.out.println( "file.getSize() =" + file.getSize() + "=" );
        
        return new UploadFileResponse(filename, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/jfp/downloadFile/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/jfp/getFile2")
    public ResponseEntity<Resource> getFile( String filename ) throws IOException
        {
        File file = new File( filename );
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
    
//    @GetMapping("/jfp/getFile/{filename:.+}")
//    public ResponseEntity<Resource> getFile(@PathVariable String filename, HttpServletRequest request) {
//        // Load file as Resource
//        Resource resource = fileStorageService.loadFileAsResource(filename);
//
//        // Try to determine file's content type
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//            
//            fileStorageService.getFileViaChannel(String urlStr, String file);
//        } catch (IOException ex) {
//            logger.info("Could not determine file type.");
//        }
//
//        // Fallback to the default content type if type could not be determined
//        if(contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }
    
//  @GetMapping("/files")
//  public ResponseEntity<List<FileInfo>> getListFiles() {
//    List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
//      String filename = path.getFileName().toString();
//      String url = MvcUriComponentsBuilder
//          .fromMethodName(HttpsFileController.class, "getFile", path.getFileName().toString()).build().toString();
//
//      return new FileInfo(filename, url);
//    }).collect(Collectors.toList());
//
//    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
//  }
}