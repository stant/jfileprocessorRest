/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.Constants;
import com.towianski.models.JfpRestURIConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class FileUtils
    {

    public static void FileMove( JFileFinderWin jFileFinderWin, java.awt.event.ActionEvent evt, Path sourcePath, Path targetPath )
        {
        System.out.println("jfilewin FileMove()" );
        System.out.println("jfilewin FileMove()  jFileFinderWin.getRmtConnectBtn().getText() =" + jFileFinderWin.getRmtConnectBtn().getText() + "=" );

        if ( jFileFinderWin.getRmtConnectBtn().getText().equalsIgnoreCase( Constants.RMT_CONNECT_BTN_CONNECTED ) )
            {
    //        SearchModel searchModel = extractSearchModel();
    //        Rest.saveObjectToFile( "SearchModel.json", searchModel );
            RestTemplate restTemplate = new RestTemplate();
            //we can't get List<Employee> because JSON convertor doesn't know the type of
            //object in the list and hence convert it to default JSON object type LinkedHashMap
    //        FilesTblModel filesTblModel = restTemplate.getForObject( SERVER_URI+JfpRestURIConstants.GET_FILES, FilesTblModel.class, SearchModel.class );

    //        System.out.println( "rest send searchModel =" + searchModel + "=" );

//            String response = restTemplate.putForEntity( "http://" + jFileFinderWin.getRmtHost() + ":8080" + JfpRestURIConstants.SEARCH, searchModel, String.class).getBody();
//            System.out.println( "response =" + response + "=" );
//            resultsData = Rest.jsonToObject( response, ResultsData.class );
//            System.out.println( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );
//            System.out.println( "resultsData.getFilesTblModel() =" + resultsData.getFilesTblModel().toString() );

//                final String endpoint = endpoint("index/{id}");
//
//                HttpHeaders requestHeaders = new HttpHeaders();
//                List <MediaType> mediaTypeList = new ArrayList<MediaType>();
//                mediaTypeList.add(MediaType.APPLICATION_JSON);
//                requestHeaders.setAccept(mediaTypeList);
//                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
//
//                // Create the HTTP PUT request,
//                ResponseEntity<Object> response = operations.exchange(endpoint, HttpMethod.PUT, requestEntity, null, id);
//                if (response == null) {
//                return false;
//                }
//                return HttpStatus.CREATED.equals(response.getStatusCode());
//                
//                ParameterizedTypeReference<List<MyBean>> myBean =
//                    new ParameterizedTypeReference<List<MyBean>>() {};
//
//                ResponseEntity<List<MyBean>> response =
//                    template.exchange("http://example.com",HttpMethod.GET, null, myBean);                
//                }
        
    //        FilesTblModel filesTblModel = restTemplate.postForEntity( SERVER_URI+JfpRestURIConstants.SEARCH, searchModel, FilesTblModel.class).getBody();
    //        System.out.println( "response filesTblModel =" + filesTblModel + "=" );
//            fillInFilesTable( resultsData.getFilesTblModel() );

//            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> param = new HashMap<String, String>();
            String oldFile = sourcePath.toString().replace( "/", "|" );
            String newFile = targetPath.toString().replace( "/", "|" );
            param.put("newname",newFile);
            param.put("oldname",oldFile);
            HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
            HttpEntity<String> response = restTemplate.exchange( "http://" + jFileFinderWin.getRmtHost() + ":8080" + JfpRestURIConstants.RENAME_FILE 
                                , HttpMethod.PUT, requestEntity, String.class, param );
            System.out.println("jfilewin FileMove()  response =" + response + "=" );
                }
            catch( Exception exc )
                {
                exc.printStackTrace();
                }
//            shops = response.getBody();
            }
        }
    
    public static void FileMove( Path sourcePath, Path targetPath )
        {
        System.out.println("jfilewin FileMove()" );
//        Path sourcePath = Paths.get( tcl.getOldValue().toString().trim() );
        if ( Files.exists( sourcePath ) )
            {
            System.out.println( "try to move dir source =" + sourcePath + "=   target =" + targetPath + "=" );
            try
                {
                Files.move( sourcePath, targetPath );
                } 
            catch (IOException ex)
                {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    
    }
