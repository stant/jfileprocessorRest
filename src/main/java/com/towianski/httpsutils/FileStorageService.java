/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.httpsutils;

/**
 *
 * @author stan
 */
import com.towianski.utils.DesktopUtils;
import com.towianski.utils.MyLogger;
import java.io.FileOutputStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private static final MyLogger logger = MyLogger.getLogger( FileStorageService.class.getName() );

//    private final Path fileStorageLocation;

//    @Autowired
//    public FileStorageService(FileStorageProperties fileStorageProperties) {
//        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
//                .toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (Exception ex) {
//            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }

    public static void getFileViaChannel(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
    
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(filename.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = Paths.get( "/net2/tmp/" + Paths.get( filename ).getFileName().toString() );
            logger.info( "targetLocation =" + targetLocation.toString() + "=" );
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + filename + ". Please try again!", ex);
        }
    }

    public String storeFileToTmp(MultipartFile file) {
        // Normalize file name
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(filename.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            //Path targetLocation = this.fileStorageLocation.resolve(filename);
            Path targetLocation = Paths.get( DesktopUtils.getJfpHomeTmpDir( false ) ).resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + filename + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = Paths.get( DesktopUtils.getJfpHomeTmpDir( false ) ).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + filename, ex);
        }
    }
}
