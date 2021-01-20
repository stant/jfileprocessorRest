package com.towianski.httpsutils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the file transfer from local to remote.
 *   $ CLASSPATH=.:../build javac ScpTo.java
 *   $ CLASSPATH=.:../build java ScpTo file1 user@remotehost:file2
 * You will be asked passwd. 
 * If everything works fine, a local file 'file1' will copied to
 * 'file2' on 'remotehost'.
 *
 */
import com.fasterxml.jackson.core.type.TypeReference;
import com.towianski.interfaces.getCancelFlag;
import com.towianski.jfileprocessor.CopyFrameSwingWorker;
import com.towianski.jfileprocessor.JFileFinderWin;
import com.towianski.models.CommonFileAttributes;
import com.towianski.models.ConnUserInfo;
import com.towianski.models.CopyCounts;
import com.towianski.models.FilesTblModel;
import com.towianski.models.JfpRestURIConstants;
import com.towianski.models.ResultsData;
import com.towianski.models.SearchModel;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class HttpsUtils
    {    
    private static final MyLogger logger = MyLogger.getLogger(HttpsUtils.class.getName() );
    RestTemplate noHostVerifyRestTemplate = null;
    String user = null;
    String password = null;
    ConnUserInfo connUserInfo = null;
    private boolean isConnected = false;
    private String message = "";
    private String uri = "";
            
    public HttpsUtils( String useFromOrTo, ConnUserInfo connUserInfo )
        {
        this.connUserInfo = connUserInfo;
        if ( useFromOrTo.equalsIgnoreCase( "TO" ) )
            {
            this.user = connUserInfo.getToUser();
            this.password = connUserInfo.getToPassword();
            this.uri = connUserInfo.getToUri();
            }
        else
            {
            this.user = connUserInfo.getFromUser();
            this.password = connUserInfo.getFromPassword();
            this.uri = connUserInfo.getFromUri();
            }
        logger.info( "HttpsUtils() constr with user =" + user + "=   password =" + password + "=    uri =" + uri + "=   for useFromOrTo =" + useFromOrTo );
        noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        }
    
// return "" if ok, or an error message
public String httpsIfDiff( String locFile, String user, String password, ConnUserInfo connUserInfo, String rmtFile )
    {
//    Https httpsConn = new Https( user, password, rhost, toSshPort );
//    if ( ! httpsConn.isConnected() )
//        {
//        return httpsConn.getMessage();
//        }
    
    String errMsg = "";
    //com.jcraft.jsch.ChannelSftp chanSftp = httpsConn.getChanSftp();
    boolean doCopy = false;
    
    try {
        logger.info( "httpsIfDiff() httpsPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );

//        URL url = new URL(FILE_URL);
//        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
//        httpConnection.setRequestMethod("HEAD");
//        long removeFileSize = httpConnection.getContentLengthLong();

        logger.info( "entered TomcatAppMonitor waitUntilStarted()" );
        logger.info( "on EDT? = " + javax.swing.SwingUtilities.isEventDispatchThread() );

        try {
            //if ( 1 == 1 || connUserInfo.getToUserHomeDir() == null || connUserInfo.getToUserHomeDir().equals( "" ) )
            {
                logger.info( "httpsIfDiff() CALL get user home =" + connUserInfo.getToUri() + JfpRestURIConstants.GET_USER_HOME + "=" );
                String tmp = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.GET_USER_HOME, String.class );
                logger.info( "httpsIfDiff() get user home =" + tmp + "=" );
                connUserInfo.setToUserHomeDir( tmp );
            }
            
            String rmtFilePath = URLEncoder.encode( connUserInfo.getToUserHomeDir() + "/" + rmtFile, "UTF-8" );
            logger.info( "TomcatAppMonitor.run() make rest " + connUserInfo.getToUri() + JfpRestURIConstants.GET_FILE_SIZE + 
                    rmtFilePath );
            //long rmtFileSize = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.GET_FILE_SIZE +
            //        URLEncoder.encode( connUserInfo.getToUserHomeDir() + rmtFile, "UTF-8" ), Long.class );
            
            //                 restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = Rest.getHeaders( user, password );
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                
            MultiValueMap<String, String> params = new LinkedMultiValueMap();
            params.add( "filename", rmtFilePath );
//                String oldFile = URLEncoder.encode( sourcePath.toString(), "UTF-8" ); //.replace( "/", "|" );
//                String newFile = URLEncoder.encode( targetPath.toString(), "UTF-8" ); //.replace( "/", "|" );
//                param.put( "newname", newFile );
//                param.put( "oldname", oldFile );
//                HttpEntity<Object> requestEntity = new HttpEntity<Object>( params, headers );

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );
            HttpEntity<Long> response = noHostVerifyRestTemplate.exchange( connUserInfo.getToUri() + JfpRestURIConstants.GET_FILE_SIZE 
                                    , HttpMethod.PUT, requestEntity, Long.class );  //, params );
            logger.info( "jfilewin FileMove()  response =" + response + "=" );

            long rmtFileSize = response.getBody();
            logger.info( "TomcatAppMonitor.run() GET_FILE_SIZE response =" + rmtFileSize );
            BasicFileAttributes attr = Files.readAttributes( Paths.get( locFile ), BasicFileAttributes.class );
//            sftpAttrs = chanSftp.stat( rmtFile );
//            sftpAttrs = new SftpATTRS();
            
            if ( rmtFileSize != attr.size() )
                {
                logger.info( "  -- file sizes diff so recopy over jar file." );
                doCopy = true;
                }
            else
                {
                logger.info( "  -- remote file size same so no recopy." );
                }
            } 
        catch (Exception exc)
            {
            Writer buffer = new StringWriter();
            PrintWriter pw = new PrintWriter(buffer);
            exc.printStackTrace(pw);
            logger.info( "Exception: " + buffer.toString() );
            doCopy = true;
            }
        if ( doCopy )
            {
            //chanSftp.put( locFile, rmtFile );
            }
        } 
    catch (Exception ex) 
        {
        logger.severeExc( ex );
        errMsg = ex.toString();
        }
    //sftp.close();
    return errMsg;
    }

//   NOT NEEDED SO FAR ...
//public boolean isRemoteDos( String user, String password, String rhost, int toSshPort )
//    {
//        if ( 1 == 1 )   return false;
//        // FIXXX
//    Https sftp = new Https( user, password, rhost, toSshPort );
//    if ( ! sftp.isConnected() )
//        {
//        return false;
//        }
//    
//    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
//    boolean isDos = false;
//    
//    try {
//        String testStr = chanSftp.pwd();
//        // keep string before 2nd / as drive: so /c/Users -> /c    /home/stan -> /home
//        int at = testStr.substring( 1 ).indexOf( "/" );
//        if ( at < 0 )  at = testStr.length();
//        testStr = testStr.substring( 0, at + 1 ) + "/Windows/System";
//        //logger.info( "remote getHome() =" + chanSftp.getHome() + "=" );
//        logger.info( "checking for remote windows path =" + testStr + "=" );
//        SftpATTRS sftpAttrs = chanSftp.stat( testStr );
//        if ( sftpAttrs != null )
//            {
//            isDos = true;
//            }
//        } 
//    catch (SftpException exc)
//        {
//        exc.printStackTrace();
//        }
//    sftp.close();
//    return isDos;
//    }
//
//public boolean isRemotePosix( String user, String password, String rhost, int toSshPort )
//    {
//    return ! isRemoteDos( user, password, rhost, toSshPort );
//    }

//    public void copyIfMissing( String lfile, String user, String password, String rhost, String rfile )
//        {
//        //scpTo.copyTo( file.toString(), " ", "", "localhost", "/tmp/" + file.getName().toString() );
//        FileInputStream fis=null;
//
//        try
//            {
//            BasicFileAttributes attr = Files.readAttributes( Paths.get( lfile ), BasicFileAttributes.class );
//
//            logger.info( "try scpTo   user =" + user + "=   to password =" + password + "=" );
//            logger.info( "try scpTo   rhost =" + rhost + "=  to remoteFile =" + rfile + "=" );
//            logger.info( "try scpTo   lfilesize =" + attr.size() + "=   remote fsize =" + getRemoteFileSize( lfile, user, password, rhost, rfile ) + "=" );
//            if ( getRemoteFileSize( lfile, user, password, rhost, rfile ) == attr.size() )
//                {
//                logger.info( "files are same size to return" );
//                return;
//                }
//
//        //      String lfile=arg[0];
//        //      String user=remoteFile.substring(0, remoteFile.indexOf('@')).trim();
//        //      remoteFile=remoteFile.substring(remoteFile.indexOf('@')+1);
//        //      String host=remoteFile.substring(0, remoteFile.indexOf(':'));
//        //      String rfile=remoteFile.substring(remoteFile.indexOf(':')+1);
//
//            session=jsch.getSession(user, rhost, 22);
//            logger.info( "at 2" );
//
//            session.setPassword( password );
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking","no");
//            logger.info( "at 3" );
//            session.setConfig(config);
//
//              // username and password will be given via UserInfo interface.
//        //      UserInfo ui=new MyUserInfo();
//        //    logger.info( "at 3" );
//        //      session.setUserInfo(ui);
//
//            logger.info( "at 4" );
//            session.connect();
//            logger.info( "at 5" );
//
//            boolean ptimestamp = true;
//
//            // exec 'scp -t rfile' remotely
//            String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
//            logger.info( "at 6" );
//            Channel channel=session.openChannel("exec");
//            logger.info( "at 7" );
//            ((ChannelExec)channel).setCommand(command);
//            logger.info( "at 8" );
//
//            // get I/O streams for remote scp
//            OutputStream out=channel.getOutputStream();
//            logger.info( "at 9" );
//            InputStream in=channel.getInputStream();
//            logger.info( "at 10" );
//
//            channel.connect();
//            logger.info( "at 11" );
//
//            if(checkAck(in)!=0){
//                return;
//            }
//
//            File _lfile = new File(lfile);
//            logger.info( "at 12" );
//
//            if(ptimestamp){
//                command="T"+(_lfile.lastModified()/1000)+" 0";
//                // The access time should be sent here,
//                // but it is not accessible with JavaAPI ;-<
//                command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
//                logger.info( "at 13" );
//                out.write(command.getBytes()); out.flush();
//                logger.info( "at 14" );
//                if(checkAck(in)!=0){
//                    return;
//                    }
//                }
//
//              // send "C0644 filesize filename", where filename should not include '/'
//            long filesize=_lfile.length();
//            command="C0644 "+filesize+" ";
//            logger.info( "at 15" );
//            if(lfile.lastIndexOf('/')>0){
//                command+=lfile.substring(lfile.lastIndexOf('/')+1);
//            }
//            else{
//                command+=lfile;
//            }
//            command+="\n";
//            logger.info( "at 16" );
//            out.write(command.getBytes()); out.flush();
//            logger.info( "at 17" );
//            if(checkAck(in)!=0){
//                return;
//            }
//
//            // send a content of lfile
//            fis=new FileInputStream(lfile);
//            byte[] buf=new byte[1024];
//            logger.info( "at 17" );
//            while(true){
//                int len=fis.read(buf, 0, buf.length);
//                if(len<=0) break;
//                out.write(buf, 0, len); //out.flush();
//            }
//            logger.info( "at 18" );
//            fis.close();
//            fis=null;
//            // send '\0'
//            buf[0]=0; out.write(buf, 0, 1); out.flush();
//            if(checkAck(in)!=0){
//                return;
//            }
//            out.close();
//            logger.info( "at 19" );
//
//            channel.disconnect();
//            session.disconnect();
//
//            logger.info( "DONE." );
//            return;
//            }
//        catch(Exception e)
//            {
//            logger.info( e);
//            try{if(fis!=null)fis.close();}catch(Exception ee){}
//            }
//      }

//    public Session createSession( String user, String password, String rhost )
//        {
//        try
//            {
//            session = jsch.getSession( user, rhost, 22 );
//            logger.info( "at 2" );
//
//            session.setPassword( password );
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking","no");
//            logger.info( "at 3" );
//            session.setConfig(config);
//
//            // username and password will be given via UserInfo interface.
//            //      UserInfo ui=new MyUserInfo();
//            //    logger.info( "at 3" );
//            //      session.setUserInfo(ui);
//
//            logger.info( "at 4" );
//            session.connect();
//            } 
//        catch (JSchException ex)
//            {
//            java.util.logging.logger.severeExc( ex );
//            }
//        logger.info( "at 5" );
//        return session;
//        }
    
public CommonFileAttributes stat( String rmtFile ) throws UnsupportedEncodingException, Exception
    {
    try {
//        String rmtFilePath = URLEncoder.encode( connUserInfo.getToUserHomeDir() + "/" + rmtFile, "UTF-8" );
        String rmtFilePath = URLEncoder.encode( rmtFile, "UTF-8" );
        logger.finer( "HttpsUtils.stat() make rest " + connUserInfo.getWhichUsingUri() + JfpRestURIConstants.GET_FILE_STAT +
                rmtFilePath );

        HttpHeaders headers = Rest.getHeaders( user, password );
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        logger.finer( "HttpsUtils.stat() send user =" + user + "=     pass =" + password + "=" );

        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add( "filename", rmtFilePath );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );

        ResponseEntity<String> responseEntity = noHostVerifyRestTemplate.exchange( connUserInfo.getWhichUsingUri() + JfpRestURIConstants.GET_FILE_STAT
                , HttpMethod.PUT, requestEntity, String.class );
        logger.info("Status Code: " + responseEntity.getStatusCode());	
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if ( statusCodeValue == 401 )
            {
            throw new Exception( "Unauthorized" );
            }
        else if ( statusCodeValue != 200 )
            {
            throw new Exception( "Http Error" );
            }

        CommonFileAttributes cattr = (CommonFileAttributes) Rest.jsonToObject( responseEntity.getBody(), new TypeReference<CommonFileAttributes>() {} );
        logger.finest( "HttpsUtils.stat() GET_FILE_STAT cattr =" + cattr.toString() );
        return cattr;
        } 
    catch (Exception ex) 
        {
        logger.info( "HttpsUtils.stat() GET_FILE_STAT error =" + ex.getLocalizedMessage() );
        logger.log(Level.SEVERE, null, ex);
        throw ex;
        }
        //return null;
    }


public Boolean exists( String targetPath ) throws UnsupportedEncodingException
    {
    Boolean response = null;

    try
        {
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

//        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        String rmtFilePath = URLEncoder.encode( targetPath, "UTF-8" );
//        params.add( "filename", rmtFilePath );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( Rest.getHeaders( user, password ) );

//                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.DOES_FILE_EXIST, Boolean.class );
//                    HttpEntity request = new HttpEntity( Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() ) );

        // make an HTTP GET request with headers
        ResponseEntity<Boolean> responseEntity = noHostVerifyRestTemplate.exchange(
                connUserInfo.getToUri() + JfpRestURIConstants.DOES_FILE_EXIST + "?filename=" + rmtFilePath,
                HttpMethod.GET,
                requestEntity,
                Boolean.class
            );
        response = responseEntity.getBody();
        }
    catch( Exception exc )
        {
        logger.info( "HttpsUtils.exists() DOES_FILE_EXIST threw Exception !!" );
        logger.severeExc( exc );
        return false;
        }
    logger.info( "HttpsUtils.exists()  response =" + response + "=" );
    return response;
    }

public Boolean existsAndCanWrite( String targetPath ) throws UnsupportedEncodingException, Exception
    {
    Boolean response = null;
    int statusCodeValue = -1;
    
//    try
//        {
        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();

//        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        String rmtFilePath = URLEncoder.encode( targetPath, "UTF-8" );
//        params.add( "filename", rmtFilePath );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( Rest.getHeaders( user, password ) );

//                    response = noHostVerifyRestTemplate.getForObject( connUserInfo.getToUri() + JfpRestURIConstants.DOES_FILE_EXIST, Boolean.class );
//                    HttpEntity request = new HttpEntity( Rest.getHeaders( connUserInfo.getToUser(), connUserInfo.getToPassword() ) );

        // make an HTTP GET request with headers
        ResponseEntity<Boolean> responseEntity = noHostVerifyRestTemplate.exchange(
                connUserInfo.getToUri() + JfpRestURIConstants.DOES_FILE_EXIST_AND_CAN_WRITE + "?filename=" + rmtFilePath,
                HttpMethod.GET,
                requestEntity,
                Boolean.class
            );
        logger.info("Status Code: " + responseEntity.getStatusCode());	
        statusCodeValue = responseEntity.getStatusCodeValue();
        if ( statusCodeValue == 401 )
            {
            throw new Exception( "Unauthorized" );
            }
        else if ( statusCodeValue != 200 )
            {
            throw new Exception( "Http Error" );
            }

        response = responseEntity.getBody();
        logger.info( "response =" + response + "=" );
//        }
//    catch( Exception exc )
//        {
//        logger.info( "DOES_FILE_EXIST_AND_CAN_WRITE threw Exception !!" );
//        logger.severeExc( exc );
//        return false;
//        }
    return response;
    }

public boolean existsProbablyWRONGOLD( String path )
    {
    try {
        stat( path );
        return true;
        } 
    catch (Exception ex)
        {
        logger.severeExc( ex );
        }
    return false;
    }
    
public String mkDir( String rmtDir ) throws UnsupportedEncodingException
    {
    try {
       // String rmtDirPath = URLEncoder.encode( connUserInfo.getToUserHomeDir() + "/" + rmtDir, "UTF-8" );
        String rmtDirPath = URLEncoder.encode( rmtDir, "UTF-8" );
        logger.info( "HttpsUtils.mkdir() make rest " + connUserInfo.getWhichUsingUri() + JfpRestURIConstants.MKDIR +
                rmtDirPath );

        HttpHeaders headers = Rest.getHeaders( user, password );
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add( "dir", rmtDirPath );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );

        HttpEntity<String> response = noHostVerifyRestTemplate.exchange( connUserInfo.getWhichUsingUri() + JfpRestURIConstants.MKDIR
                , HttpMethod.PUT, requestEntity, String.class );
        logger.info( "HttpsUtils mkDir()  response =" + response + "=" );

        String ans = response.getBody();
        logger.info( "HttpsUtils.mkDir() MKDIR attr =" + ans );
        return ans;
        } 
    catch (IOException ex) 
        {
        java.util.logging.Logger.getLogger(HttpsUtils.class.getName()).log(Level.SEVERE, null, ex);
        throw ex;
        }
    }

// NOTE: This works now only because jfp deletes up the tree so the folder is empty before it delete a folder
// but calling this endpoint straight does not work to delete a folder tree.
public String rmDir( String rmtDir ) throws UnsupportedEncodingException
    {
    try {
        String rmtDirPath = URLEncoder.encode( rmtDir, "UTF-8" );
        logger.info( "HttpsUtils.rmdir() make rest " + connUserInfo.getWhichUsingUri() + JfpRestURIConstants.RMDIR +
                rmtDirPath );

        HttpHeaders headers = Rest.getHeaders( user, password );
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add( "dir", rmtDirPath );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );

        HttpEntity<String> response = noHostVerifyRestTemplate.exchange( connUserInfo.getWhichUsingUri() + JfpRestURIConstants.RMDIR
                , HttpMethod.PUT, requestEntity, String.class );
        logger.info( "HttpsUtils rmdir()  response =" + response + "=" );

        String ans = response.getBody();
        logger.info( "HttpsUtils.rmdir() =" + ans );
        return ans;
        } 
    catch (IOException ex) 
        {
        java.util.logging.Logger.getLogger(HttpsUtils.class.getName()).log(Level.SEVERE, null, ex);
        throw ex;
        }
    }

public String rm( String rmtFile ) throws UnsupportedEncodingException
    {
    try {
//        String rmtFilePath = URLEncoder.encode( connUserInfo.getToUserHomeDir() + "/" + rmtFile, "UTF-8" );
        String rmtFilePath = URLEncoder.encode( rmtFile, "UTF-8" );
        logger.info( "HttpsUtils.rm() make rest " + connUserInfo.getWhichUsingUri() + JfpRestURIConstants.RM +
                rmtFilePath );

        HttpHeaders headers = Rest.getHeaders( user, password );
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add( "filename", rmtFilePath );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );

        HttpEntity<String> response = noHostVerifyRestTemplate.exchange( connUserInfo.getWhichUsingUri() + JfpRestURIConstants.RM
                , HttpMethod.PUT, requestEntity, String.class );
        logger.info( "HttpsUtils rm()  response =" + response + "=" );

        String ans = response.getBody();
        logger.info( "HttpsUtils.rm() RM attr =" + ans );
        return ans;
        } 
    catch (IOException ex) 
        {
        java.util.logging.Logger.getLogger(HttpsUtils.class.getName()).log(Level.SEVERE, null, ex);
        throw ex;
        }
    }

public ArrayList<String> ls( String rmtFile ) throws UnsupportedEncodingException
    {
    ArrayList<String> pathsList = new ArrayList<>();
    ResultsData resultsData = null;

    try {
        SearchModel searchModel = new SearchModel();
        searchModel.setStartingFolder( rmtFile );
        searchModel.setFilePattern( "*" );
        searchModel.setPatternType( "-glob" );
        searchModel.setShowFilesFoldersType( "Files & Folders" );
        searchModel.setShowHiddenFilesFlag( true );
//        Rest.saveObjectToFile( "SearchModel.json", searchModel );
//        RestTemplate restTemplate = new RestTemplate();
//        RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
        //we can't get List<Employee> because JSON convertor doesn't know the type of
        //object in the list and hence convert it to default JSON object type LinkedHashMap
//        FilesTblModel filesTblModel = restTemplate.getForObject( SERVER_URI+JfpRestURIConstants.GET_FILES, FilesTblModel.class, SearchModel.class );

        logger.info( "rest send ls searchModel =" + Rest.saveObjectToString( searchModel ) + "=" );

//        String response = restTemplate.postForEntity( "http://" + rmtHost.getText().trim() + ":8080" + JfpRestURIConstants.SEARCH, searchModel, String.class).getBody();
        ResponseEntity<ResultsData> responseEntity = null;
        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//            // add basic authentication
//            headers.setBasicAuth( connUserInfo.getToUser(), connUserInfo.getToPassword() );
            HttpHeaders headers = Rest.getHeaders( user, password );

//            MultiValueMap<String, String> params = new LinkedMultiValueMap();
//            params.add( "filename", rmtFilePath );
//            HttpEntity<Object> requestEntity = new HttpEntity<Object>( params, headers );
            HttpEntity<SearchModel> requestEntity = new HttpEntity( searchModel, headers );

            logger.info("call url: " + connUserInfo.getWhichUsingUri() + JfpRestURIConstants.SEARCH );	
            responseEntity = noHostVerifyRestTemplate.postForEntity( connUserInfo.getWhichUsingUri() + JfpRestURIConstants.SEARCH, 
                                                                requestEntity, ResultsData.class );
            logger.info("Status Code: " + responseEntity.getStatusCode());	
            logger.info("Id: " + responseEntity.getBody() );
            }
        catch (Exception ex) 
            {
            logger.info( "postForEntity error on Search" );	
            ex.printStackTrace();
            }

        try {
            //logger.info( "response =" + response + "=" );
            //resultsData = Rest.jsonToObject( response, ResultsData.class );
            resultsData = responseEntity.getBody();
            logger.info( "response as resultsData =" + Rest.saveObjectToString( resultsData ) );
            logger.info( "resultsData.getFilesMatched() =" + resultsData.getFilesMatched() );
            //logger.info( "resultsData.getFilesTblModel() =" + resultsData.getFilesTblModel().toString() );
            } 
        catch (Exception ex) 
            {
            logger.info( "Error. No valid response from server" );
            java.util.logging.Logger.getLogger(JFileFinderWin.class.getName()).log(Level.SEVERE, null, ex);
            //jFileFinderWin.setMessage( "Error. No valid response from server" );
            //jFileFinderWin.setResultsData( new ResultsData() );
            return pathsList;
            }

        NumberFormat numFormat = NumberFormat.getIntegerInstance();
        logger.info( "Matched " + numFormat.format( resultsData.getFilesMatched() ) + " files and " + numFormat.format( resultsData.getFoldersMatched() ) 
                + " folders out of " + numFormat.format( resultsData.getFilesTested() ) + " files and " + numFormat.format( resultsData.getFoldersTested() ) + " folders.  Total "
                + numFormat.format( resultsData.getFilesVisited() ) );

        if ( resultsData.getFilesMatched() > 0 || resultsData.getFoldersMatched() > 0 )
            {
            FilesTblModel filesTblModel = resultsData.getFilesTblModel();
            int max = resultsData.getFilesTblModel().getRowCount();
            for ( int i = 0; i < max; i++ )
                {
                logger.info( "filesTblModel[" + i + ",path] =" + filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) + "=" );
                Path apath = Paths.get( (String) filesTblModel.getValueAt( i, FilesTblModel.FILESTBLMODEL_PATH ) );
                pathsList.add( apath.getFileName().toString() );
                }
            }
        }
    catch (Exception ex) 
        {
        java.util.logging.Logger.getLogger(HttpsUtils.class.getName()).log(Level.SEVERE, null, ex);
        throw ex;
        }
    return pathsList;
    }

public boolean isConnected()
    {
    return true;  //isConnected;
    }

public String getMessage()
    {
    return message;
    }

/**  FIXXX
public void copyTo( Session session, String lfile, String rfile )
        {
        //scpTo.copyTo( file.toString(), " ", "", "localhost", "/tmp/" + file.getName().toString() );
        FileInputStream fis=null;

        try
            {
            boolean ptimestamp = true;

            // exec 'scp -t rfile' remotely
            String command="copyTo scp " + ( ptimestamp ? "-p" : "" ) + " -t " + rfile;
            logger.info( "copyTo at 6" );
            Channel channel=session.openChannel("exec");
            logger.info( "copyTo at 7" );
            ((ChannelExec)channel).setCommand(command);
            logger.info( "copyTo at 8" );

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            logger.info( "copyTo at 9" );
            InputStream in = channel.getInputStream();
            logger.info( "copyTo at 10" );

            channel.connect();
            logger.info( "copyTo at 11" );

            if ( checkAck( in ) !=0 )
                {
                return;
                }

            File _lfile = new File( lfile );
            logger.info( "copyTo at 12" );

            if ( ptimestamp )
                {
                command="T"+(_lfile.lastModified()/1000)+" 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
                logger.info( "copyTo at 13" );
                out.write(command.getBytes()); out.flush();
                logger.info( "copyTo at 14" );
                if ( checkAck( in ) !=0 )
                    {
                    return;
                    }
                }

              // send "C0644 filesize filename", where filename should not include '/'
            long filesize=_lfile.length();
            command="C0644 "+filesize+" ";
            logger.info( "copyTo at 15" );
            if ( lfile.lastIndexOf( '/' ) > 0 )
                {
                command+=lfile.substring( lfile.lastIndexOf( '/' ) + 1 );
                }
            else
                {
                command += lfile;
                }
            command += "\n";
            logger.info( "copyTo at 16" );
            out.write(command.getBytes()); out.flush();
            logger.info( "copyTo at 17" );
            if ( checkAck( in ) !=0 )
                {
                return;
                }

              // send a content of lfile
            fis=new FileInputStream(lfile);
            byte[] buf=new byte[ 4096 ];
            logger.info( "copyTo at 18" );
            while( true )
                {
                int len=fis.read(buf, 0, buf.length);
                if(len<=0) break;
                out.write(buf, 0, len); //out.flush();
                }
            logger.info( "copyTo at 19" );
            fis.close();
            fis=null;
            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();
            if(checkAck(in)!=0){
                return;
              }
            out.close();
            logger.info( "copyTo at 20" );

            channel.disconnect();
//            session.disconnect();

            logger.info( "copyTo DONE." );
            return;
            }
        catch(Exception e)
            {
            logger.severeExc(e);
            try
                {
                if (fis != null)
                    {
                    fis.close();
                    }
                } catch (Exception ee)
                    {
                    }
            }
      }

public void FilePut( String locFile, String user, String password, String rhost, String toSshPort, String rmtFile )
    {
    try {
        FilePut( locFile, user, password, rhost, Integer.parseInt( toSshPort ), rmtFile );
        }
    catch( Exception ex ) 
        {
        FilePut( locFile, user, password, rhost, 22, rmtFile );
        }
    }

public void FilePut( String locFile, String user, String password, String rhost, int toSshPort, String rmtFile )
    {
    Https sftp = new Https( user, password, rhost, toSshPort );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();

    try {
        //    sftpChannel.put("C:/source/local/path/file.zip", "/target/remote/path/file.zip");
        //logger.info( "SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        rmtFile = rmtFile.replace( "\\", "/" );
        logger.info( "SftpPut locFile =" + locFile + "=   to rmtFile =" + rmtFile + "=" );
        chanSftp.put( locFile, rmtFile );
    } catch (SftpException ex) {
        logger.severeExc( ex );
    }
    sftp.close();
    }

public void FileGet( String rmtFile, String user, String password, String rhost, String toSshPort, String locFile )
    {
    try {
        FileGet( rmtFile, user, password, rhost, Integer.parseInt( toSshPort ), locFile );
        }
    catch( Exception ex ) 
        {
        FileGet( rmtFile, user, password, rhost, 22, locFile );
        }
    }

public void FileGet( String rmtFile, String user, String password, String rhost, int toSshPort, String locFile )
    {
    Https sftp = new Https( user, password, rhost, toSshPort );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();

    try {
        logger.info( "SftpGet rmtFile =" + rmtFile + "=   to locFile =" + locFile + "=" );
        rmtFile = rmtFile.replace( "\\", "/" );
//        locFile = locFile.replace( "\\", "/" );
        logger.info( "SftpGet rmtFile =" + rmtFile + "=   to locFile =" + locFile + "=" );
*/

    public void getFileViaChannel(String source, String target) throws IOException {
        try {
            URL url = new URL( uri + "/jfp/downloadFile/" + source );     //JfpRestURIConstants.);
            logger.info( "getFileViaChannel uri =" + uri + "/   rmtFile =" + source + "=   to locFile =" + target + "=" );
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream( target );
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (Exception ex) {
           logger.info( "SftpGet Touch ERROR =" + ex );
           logger.severeExc( ex );
        }
//        sftp.close();
        }

    public void getFileViaRestTemplate( String source, String target ) throws IOException {
        try {
            logger.info( "getFileViaRestTemplate source =" + source + "=   target =" + target + "=" );

            // Optional Accept header
            RequestCallback requestCallback = request -> {
                    String base64Credentials = new String(Base64.encodeBase64( (user + ":" + password).getBytes()));
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getHeaders().add("Authorization", "Basic " + base64Credentials);
                    request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL) );
                            };
            // from HttpHeaders headers = Rest.getHeaders( user, password );

            // Streams the response instead of loading it all in memory
            ResponseExtractor<Void> responseExtractor = response -> {
                try {
                logger.info( "getFileViaRestTemplate save response body to target =" + target + "=" );
                Path path = Paths.get( target );
                long totBytesRead = 0;
                long fileLength = Long.parseLong( response.getHeaders().get( "X-jfp.file.length" ).get(0) );
                logger.info( "getFileViaRestTemplate get response header fileLength =" + fileLength + "=" );

                //ByteArrayInputStream bais = (ByteArrayInputStream) response.getBody();
                FileOutputStream fos = new FileOutputStream( path.toFile() );
                
                try {
                    byte[] buffer = new byte[102400];    
                    int bytesRead = 0;
                    while( bytesRead != -1 )  //|| totBytesRead >= fileLength )
                        {
                        bytesRead = response.getBody().read( buffer, 0, 102400 ); //-1, 0, or more
                        logger.finest( "getFileViaRestTemplate read buffer bytesRead =" + bytesRead + "=" );
                        if ( bytesRead > 0 )
                            {
                            fos.write( buffer, 0, bytesRead );
                            totBytesRead += bytesRead;
                            logger.finest( "getFileViaRestTemplate totBytesRead =" + totBytesRead + "=" );
                            // I have to find and stop at fileLength otherwise it seems to wait for a 1 minute readTimeout
                            // before it detects the end of file send!
                            if ( totBytesRead >= fileLength )
                                {
                                logger.finest( "getFileViaRestTemplate STOP at fileLength totBytesRead =" + totBytesRead + "=" );
                                break;
                                }
                            }
                        }
                    logger.finest( "getFileViaRestTemplate Done Reading/writing totBytesRead =" + totBytesRead + "=" );
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
                    logger.finest( "getFileViaRestTemplate start close file" );
                    if (fos != null) fos.close();
                    logger.finest( "getFileViaRestTemplate done close file" );
                    }
                }
            catch( Exception exc )
                {
                Writer buffer = new StringWriter();
                PrintWriter pw = new PrintWriter(buffer);
                exc.printStackTrace(pw);
                logger.info( "Exception: " + buffer.toString() );
                }
            return null;
            };
        
//          logger.info( "getFileViaRestTemplate uri =" + uri + JfpRestURIConstants.GET_FILE_SIZE + source + "=" );
//          noHostVerifyRestTemplate.execute(URI.create(uri + "/jfp/downloadFile/" + source), HttpMethod.GET, requestCallback, responseExtractor);
        
            String rmtFilePath = URLEncoder.encode( source, "UTF-8" );
//            logger.info( "TomcatAppMonitor.run() make rest " + connUserInfo.getToUri() + JfpRestURIConstants.GET_FILE_SIZE + 
//                    rmtFilePath );

//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            HttpHeaders headers = Rest.getHeaders( user, password );
                
            MultiValueMap<String, String> params = new LinkedMultiValueMap();
            params.add( "filename", rmtFilePath );
            //Map<String, String> params = new HashMap();
            //params.put( "filename", source );
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );
//            HttpEntity<Resource> response = noHostVerifyRestTemplate.exchange( uri + JfpRestURIConstants.GET_FILE
//                                    , HttpMethod.GET
                                    //requestEntity, Long.class );  //, params );

            //String getFileUrl = uri + JfpRestURIConstants.GET_FILE + "?filename=" + source;
            String getFileUrl = uri + JfpRestURIConstants.GET_FILE + "?filename=" + rmtFilePath;
            logger.info( "qqq call =" + getFileUrl + "=" );
            
//            noHostVerifyRestTemplate.execute( getFileUrl, HttpMethod.GET
//                            , requestCallback, responseExtractor, params);

            noHostVerifyRestTemplate.execute( URI.create(getFileUrl), HttpMethod.GET, requestCallback, responseExtractor );
            logger.info( "Done with REST call =" + getFileUrl + "=" );
            } 
        catch (Exception ex) {
           logger.info( "getFileViaRestTemplate ERROR =" + ex );
           logger.severeExc( ex );
        }
    }

    public void getFileViaRestTemplateSw( String source, String target, CopyFrameSwingWorker swingWorker, long numTested, getCancelFlag getCancelSearch ) throws IOException {
        try {
                //RequestCallback requestCallback = request -> request.getHeaders()
                //.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            // Optional Accept header
            RequestCallback requestCallback = request -> {
                    String base64Credentials = new String(Base64.encodeBase64( (user + ":" + password).getBytes()));
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getHeaders().add("Authorization", "Basic " + base64Credentials);
                    request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL) );
                            };
            // from HttpHeaders headers = Rest.getHeaders( user, password );

            // Streams the response instead of loading it all in memory
            ResponseExtractor<Void> responseExtractor = response -> {
                try {
                logger.info( "getFileViaRestTemplate save response body to target =" + target + "=" );
                Path path = Paths.get( target );
                long totBytesRead = 0;
                long dispAtBytes = 102400;
                long fileLength = Long.parseLong( response.getHeaders().get( "X-jfp.file.length" ).get(0) );
                logger.info( "getFileViaRestTemplate get response header fileLength =" + fileLength + "=" );
                
                //ByteArrayInputStream bais = (ByteArrayInputStream) response.getBody();
//                FileOutputStream fos = new FileOutputStream( path.toFile() );
                //BufferedInputStream inStream = new BufferedInputStream( response.getBody() );
                BufferedOutputStream outStream = new BufferedOutputStream( new FileOutputStream( path.toFile() ) );
                // a lot of time fooling around trying to make performance better. upload almost twice as long as download.
                
                try {
                    byte[] buffer = new byte[102400];    
                    int bytesRead = 0;
                    while(bytesRead != -1)
                        {
                        bytesRead = response.getBody().read( buffer, 0, 102400 ); //-1, 0, or more
                        logger.finest( "getFileViaRestTemplate read buffer bytesRead =" + bytesRead + "=" );
                        if ( bytesRead > 0 )
                            {
                            outStream.write( buffer, 0, bytesRead );
                            totBytesRead += bytesRead;
                            logger.finest( "getFileViaRestTemplate totBytesRead =" + totBytesRead + "=" );
                            if ( totBytesRead > dispAtBytes )
                                {
                                if ( swingWorker != null ) swingWorker.publish3( new CopyCounts( numTested, totBytesRead ) );
                                dispAtBytes = totBytesRead + 102400;
                                if ( getCancelSearch.apply() )
                                    {
                                    logger.finest( "got CANCEL" );
                                    throw new IOException( "Canceled" );
                                    }
                                }
                            // I have to find and stop at fileLength otherwise it seems to wait for a 1 minute readTimeout
                            // before it detects the end of file send!
                            if ( totBytesRead >= fileLength )
                                {
                                logger.finest( "getFileViaRestTemplate STOP at fileLength totBytesRead =" + totBytesRead + "=" );
                                break;
                                }
                            }
                        }
                    logger.finest( "getFileViaRestTemplate Done Reading/writing totBytesRead =" + totBytesRead + "=" );
                    if ( swingWorker != null ) swingWorker.publish3( new CopyCounts( numTested, totBytesRead ) );
                    outStream.flush();
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
                    //if (inStream != null) inStream.close();
                    if (outStream != null) outStream.close();
                    }
                }
            catch( Exception exc )
                {
                Writer buffer = new StringWriter();
                PrintWriter pw = new PrintWriter(buffer);
                exc.printStackTrace(pw);
                logger.info( "Exception: " + buffer.toString() );
                }
            return null;
            };
        
//          logger.info( "getFileViaRestTemplate uri =" + uri + JfpRestURIConstants.GET_FILE_SIZE + source + "=" );
//          noHostVerifyRestTemplate.execute(URI.create(uri + "/jfp/downloadFile/" + source), HttpMethod.GET, requestCallback, responseExtractor);
        
            String rmtFilePath = URLEncoder.encode( source, "UTF-8" );
//            logger.info( "TomcatAppMonitor.run() make rest " + connUserInfo.getToUri() + JfpRestURIConstants.GET_FILE_SIZE + 
//                    rmtFilePath );

//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            HttpHeaders headers = Rest.getHeaders( user, password );
                
            MultiValueMap<String, String> params = new LinkedMultiValueMap();
            params.add( "filename", rmtFilePath );
            //Map<String, String> params = new HashMap();
            //params.put( "filename", source );
//            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity( params, headers );
//            HttpEntity<Resource> response = noHostVerifyRestTemplate.exchange( uri + JfpRestURIConstants.GET_FILE
//                                    , HttpMethod.GET
                                    //requestEntity, Long.class );  //, params );

            //String getFileUrl = uri + JfpRestURIConstants.GET_FILE + "?filename=" + source;
            String getFileUrl = uri + JfpRestURIConstants.GET_FILE + "?filename=" + rmtFilePath;
            logger.info( "qqq call =" + getFileUrl + "=" );
            
//            noHostVerifyRestTemplate.execute( getFileUrl, HttpMethod.GET
//                            , requestCallback, responseExtractor, params);
                    
            noHostVerifyRestTemplate.execute( URI.create(getFileUrl), HttpMethod.GET, requestCallback, responseExtractor );
            logger.info( "Done with REST call =" + getFileUrl + "=" );
            } 
        catch (Exception ex) {
           logger.info( "getFileViaRestTemplate ERROR =" + ex );
           logger.severeExc( ex );
        }
    }
    
        // Using Webclient - for the future
//    public Flux<DataBuffer> downloadFileUrl( ) throws IOException 
//        {
//        WebClient webClient = WebClient.create();
//
//        // Request service to get file data
//        return Flux<DataBuffer> fileDataStream = webClient.get()
//                .uri( this.fileUrl )
//                .accept( MediaType.APPLICATION_OCTET_STREAM )
//                .retrieve()
//                .bodyToFlux( DataBuffer.class );
//        }

    public void putFile( String source, String target ) throws IOException {
        try {
            // Optional Accept header
            RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

            // Streams the response instead of loading it all in memory
            ResponseExtractor<Void> responseExtractor = response -> {
                try {
                // Here I write the response to a file but do what you like
                    logger.info( "putFile save response body to target =" + target + "=" );
                Path path = Paths.get( target );

    //            StringBuilder sb = new StringBuilder();
    //            
    //            try {
    //                BufferedReader reader = 
    //                       new BufferedReader(new InputStreamReader(response.getBody()));
    //                String line = null;
    //
    //                while ((line = reader.readLine()) != null) {
    //                    sb.append(line);
    //                }
    //            }
    //            catch (IOException e) { e.printStackTrace(); }
    //            catch (Exception e) { e.printStackTrace(); }
    //
    //            logger.info("finalResult " + sb.toString());

                Files.copy(response.getBody(), path);
                }
            catch( Exception exc )
                {
                Writer buffer = new StringWriter();
                PrintWriter pw = new PrintWriter(buffer);
                exc.printStackTrace(pw);
                logger.info( "Exception: " + buffer.toString() );
                }
            return null;
            };
        
            //String rmtFilePath = URLEncoder.encode( source, "UTF-8" );

            //HttpHeaders headers = new HttpHeaders();
            HttpHeaders headers = Rest.getHeaders( user, password );
            //headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            logger.info( "putFile save response body to source =" + source + "=" );
            logger.info( "putFile save response body to target =" + target + "=" );

            MultiValueMap<String, Object> params = new LinkedMultiValueMap();
            params.add( "source", new FileSystemResource( Paths.get( source ) ) );
            params.add( "target", target );
            //Map<String, String> params = new HashMap();
            //params.put( "filename", source );
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity( params, headers );

            logger.info( "qqq call =" + uri + JfpRestURIConstants.SEND_FILE + "=" );
            ResponseEntity<String> response = noHostVerifyRestTemplate.postForEntity( uri + JfpRestURIConstants.SEND_FILE,
                                    requestEntity, String.class );

//            String getFileUrl = uri + JfpRestURIConstants.GET_FILE + "?filename=" + source;
            
//            noHostVerifyRestTemplate.execute( getFileUrl, HttpMethod.GET
//                            , requestCallback, responseExtractor, params);
                    
//            noHostVerifyRestTemplate.execute( URI.create(getFileUrl), HttpMethod.GET, requestCallback, responseExtractor );
            } 
        catch (Exception ex) {
            logger.info( "putFile ERROR =" + logger.getExceptionAsString(ex) );
            throw new IOException( "Unauthorized" );
        }
    }

    public void putFileSw( String source, String target, CopyFrameSwingWorker swingWorker, long numTested, getCancelFlag getCancelSearch ) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException 
        {
            //String rmtFilePath = URLEncoder.encode( source, "UTF-8" );

            //HttpHeaders headers = new HttpHeaders();
//            HttpHeaders headers = Rest.getHeaders( user, password );
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            logger.info( "putFileSw source =" + source + "=" );
            logger.info( "putFileSw target =" + target + "=" );

            try {
                Path path = Paths.get( source );

//            MultiValueMap<String, Object> params = new LinkedMultiValueMap();
//            params.add( "source", new FileSystemResource( Paths.get( source ) ) );
//            params.add( "target", target );
//            //Map<String, String> params = new HashMap();
//            //params.put( "filename", source );
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity( params, headers );
//
//            logger.info( "qqq call =" + uri + JfpRestURIConstants.SEND_FILE + "=" );
//            ResponseEntity<String> response = noHostVerifyRestTemplate.postForEntity( uri + JfpRestURIConstants.SEND_FILE,
//                                    requestEntity, String.class );
//            
//            String param = "value";

//            FileInputStream truststoreFile = new FileInputStream( ResourceUtils.getFile( "classpath:selfsigned.jks").getAbsolutePath() );
//            InputStream truststoreFile = new ClassPathResource("selfsigned.jks").getInputStream();
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
//            char[] trustorePassword = "jfp2020".toCharArray();
//            truststore.load(truststoreFile, trustorePassword);
//            trustManagerFactory.init(truststore);
//            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//            KeyManager[] keyManagers = {};//if you have key managers;

            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
            
//            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
//            
//            CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLSocketFactory(sslConnectionSocketFactory)
//                .build();
//            
//            HttpComponentsClientHttpRequestFactory requestFactory =
//                new HttpComponentsClientHttpRequestFactory();
//            
//            requestFactory.setHttpClient(httpClient);

            
//            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            //SSLContext sc = SSLContext.getInstance("TLSv1.2");
            // Init the SSLContext with a TrustManager[] and SecureRandom()
            //sslContext.init(null, trustCerts, new java.security.SecureRandom()); 

            //URLConnection connection = new URL( uri + JfpRestURIConstants.SEND_FILE ).openConnection();
            HttpsURLConnection sslConn = (HttpsURLConnection)new URL( uri + JfpRestURIConstants.SEND_FILE +
                    "?source=" + URLEncoder.encode( source, "UTF-8" ) +
                    "&target=" + URLEncoder.encode( target, "UTF-8" ) +
                    "&fileLength=" + URLEncoder.encode( path.toFile().length() + "", "UTF-8" )
                    ).openConnection();
            logger.info( "qqq call =" + uri + JfpRestURIConstants.SEND_FILE +
                    "?source=" + URLEncoder.encode( source, "UTF-8" ) +
                    "&target=" + URLEncoder.encode( target, "UTF-8" ) +
                    "&fileLength=" + URLEncoder.encode( path.toFile().length() + "", "UTF-8" ) 
                );

//            HttpsURLConnection sslConn = (HttpsURLConnection)new URL( uri + JfpRestURIConstants.SEND_FILE ).openConnection();
//            logger.info( "qqq call =" + uri + JfpRestURIConstants.SEND_FILE );

            //target = URLDecoder.decode( target, "UTF-8" );
            //logger.info( "target decoded =" + target + "=" );

            sslConn.setFixedLengthStreamingMode( path.toFile().length() );
//            sslConn.setChunkedStreamingMode( 1024000 ); //10MB chunk This ensures that any file (of any size) is streamed over a https connection, without internal buffering. 
            
            sslConn.setSSLSocketFactory(sslContext.getSocketFactory());
            //sslConn.setSSLSocketFactory( sslConnectionSocketFactory.getClass() );
            
            sslConn.setDoOutput(true);  //  implicitly sets the request method to POST
            sslConn.setRequestMethod("POST");
            sslConn.setReadTimeout(60 * 1000);
            sslConn.setConnectTimeout(5 * 1000);
            sslConn.setRequestProperty("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE );
            String base64Credentials = new String(Base64.encodeBase64( (user + ":" + password).getBytes()));         
            sslConn.setRequestProperty("Authorization", "Basic " + base64Credentials );
            sslConn.setRequestProperty("Connection", "close");

            //sslConn.setRequestProperty( "source", URLEncoder.encode( Paths.get( source ).toString(), "UTF-8" ) );
            //sslConn.setRequestProperty( "target", URLEncoder.encode( Paths.get( target ).toString(), "UTF-8" ) );
            //sslConn.setRequestProperty( "fileLength", URLEncoder.encode( path.toFile().length() + "", "UTF-8" ) );
            
//            OutputStream output = connection.getOutputStream();
//            PrintWriter writer = new PrintWriter(new OutputStreamWriter( output, "UTF-8"), true);
            
            sslConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    /** if it necessarry get url verfication */
                    //return HttpsURLConnection.getDefaultHostnameVerifier().verify("your_domain.com", session);
                    return true;
                }
            });
            //sslConn.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());

//            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName() );
//
//            // Content-Length
//            //response.setContentLength((int) file.length());
//            response.addHeader( "X-jfp.file.length", ""  + path.toFile().length() );

            // performance: what seems to matter is not buffering request or response streams
            // probably reading file with bigger buffer like 1mb instead of 100k
            
            OutputStream outStream = sslConn.getOutputStream();
            //BufferedOutputStream bufOutStream = new BufferedOutputStream( output );
            BufferedInputStream inStream = new BufferedInputStream( new FileInputStream( path.toFile() ) );
            
            byte[] buffer = new byte[1024000];
            int bytesRead = 0;
            long totBytesRead = 0;
            long dispAtBytes = 102400;
            while ((bytesRead = inStream.read( buffer )) != -1) 
                {
                logger.info( "SEND_FILESw bytesRead =" + bytesRead + "=" );
                outStream.write(buffer, 0, bytesRead);
                totBytesRead += bytesRead;
                logger.finest( "SEND_FILESw totBytesRead =" + totBytesRead + "=" );
                if ( totBytesRead > dispAtBytes )
                    {
                    if ( swingWorker != null ) swingWorker.publish3( new CopyCounts( numTested, totBytesRead ) );
                    dispAtBytes = totBytesRead + 102400;
                    outStream.flush();
                    if ( getCancelSearch.apply() )
                        {
                        logger.finest( "got CANCEL" );
                        throw new IOException( "Canceled" );
                        }
                    }
                }
            if ( swingWorker != null ) swingWorker.publish3( new CopyCounts( numTested, totBytesRead ) );
            logger.info( "SEND_FILESw Done" );

            inStream.close();        
            logger.fine( "SEND_FILESw after ins close" );
            outStream.flush();
            logger.fine( "SEND_FILESw after out flush" );
            outStream.close();
            logger.fine( "SEND_FILESw after out close" );

            // Request is lazily fired whenever you need to obtain information about response.
            int responseCode = ((HttpsURLConnection) sslConn).getResponseCode();
            logger.info( "SEND_FILESw responseCode =" + responseCode + "=" );
            if ( responseCode == 401 )
                {
                throw new IOException( "Unauthorized" );
                }
            else if ( responseCode != 200 )
                {
                throw new IOException( "Http Error responseCode = " + responseCode );
                }
            }
        catch (Exception ex) 
            {
            logger.info( "SEND_FILESw ERROR =" + logger.getExceptionAsString(ex) );
            throw new IOException( ex.getLocalizedMessage() );
            }
        }

    public String storeFile( MultipartFile source, String target ) 
        {
        // Normalize file name
        String filename = StringUtils.cleanPath( source.getOriginalFilename() );

        try {
            // Check if the file's name contains invalid characters
            if(filename.contains("..")) 
                {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
                }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = Paths.get( "/net2/tmp/" + Paths.get( filename ).getFileName().toString() );
            logger.info( "targetLocation =" + targetLocation.toString() + "=" );
            Files.copy( source.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
            }
        catch (IOException ex) 
            {
            throw new FileStorageException("Could not store file " + filename + ". Please try again!", ex);
            }
        }

/*
public boolean FileExists( String rmtFile, String user, String password, String rhost, int toSshPort )
    {
    Https sftp = new Https( user, password, rhost, toSshPort );

    try {
        logger.info( "SftpExists rmtFile =" + rmtFile + "=" );
        return sftp.exists( rmtFile );
        } 
    catch ( Exception ex )
        {
        logger.severeExc( ex );
        }
    sftp.close();
    return false;
    }

public long getRemoteFileSize( String lfile, String user, String password, String rhost, int toSshPort, String filename )
    {
    Https sftp = new Https( user, password, rhost, toSshPort );
    com.jcraft.jsch.ChannelSftp chanSftp = sftp.getChanSftp();
    SftpATTRS attrs = null;
    long fsize = -1;
    
    try {
        logger.info( "remote getHome() =" + chanSftp.getHome() + "=" );
        logger.info( "remote pwd =" + chanSftp.pwd() + "=" );
        logger.info( "look for filename =" + filename );
        attrs = chanSftp.stat( filename );
        logger.info( "got attrs =" );
        logger.info( attrs.toString() );
        fsize = attrs.getSize();
        }
    catch (Exception ex) 
        {
        ex.printStackTrace();
        }
    finally
        {
        sftp.close();
        }
    return fsize;
}

//public static String oldestFile() {
//    Vector list = null;
//    int currentOldestTime;
//    int nextTime = 2140000000; //Made very big for future-proofing
//    ChannelSftp.LsEntry lsEntry = null;
//    SftpATTRS attrs = null;
//    String nextName = null;
//    try {
//        list = Main.chanSftp.ls("*.xml");
//        if (list.isEmpty()) {
//            fileFound = false;
//        }
//        else {
//            lsEntry = (ChannelSftp.LsEntry) list.firstElement();
//            oldestFile = lsEntry.getFilename();
//            attrs = lsEntry.getAttrs();
//            currentOldestTime = attrs.getMTime();
//            for (Object sftpFile : list) {
//                lsEntry = (ChannelSftp.LsEntry) sftpFile;
//                nextName = lsEntry.getFilename();
//                attrs = lsEntry.getAttrs();
//                nextTime = attrs.getMTime();
//                if (nextTime < currentOldestTime) {
//                    oldestFile = nextName;
//                    currentOldestTime = nextTime;
//                }
//            }
//            attrs = chanSftp.lstat(Main.oldestFile);
//            long size1 = attrs.getSize();
//            logger.info( "-Ensuring file is not being written to (waiting 1 minute)");
//            Thread.sleep(60000); //Wait a minute to make sure the file size isn't changing
//            attrs = chanSftp.lstat(Main.oldestFile);
//            long size2 = attrs.getSize();
//            if (size1 == size2) {
//                logger.info( "-It isn't.");
//                fileFound = true;
//            }
//            else {
//                logger.info( "-It is.");
//                fileFound = false;
//            }
//        }
//    } catch (Exception ex) {ex.printStackTrace();}
//    return Main.oldestFile;
//}

public void exec( String user, String password, String rhost, ConnUserInfo connUserInfo, String runCmd )
    {
    try{
    logger.info( "try exec   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try exec   rhost =" + rhost + "=" );

    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    session=jsch.getSession(user, rhost, connUserInfo.getToSshPortInt() );
    logger.info( "exec at 2" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "exec at 3" );
    session.setConfig(config);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//    logger.info( "at 3" );
//      session.setUserInfo(ui);

    logger.info( "exec at 4" );
      session.connect();
    logger.info( "exec at 5" );

      
      String xhost="localhost";
      int xport=10;
      //String display=JOptionPane.showInputDialog("Enter display name", 
      //                                           xhost+":"+xport);
      //xhost=display.substring(0, display.indexOf(':'));
      //xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
      //session.setX11Host(xhost);
      //session.setX11Port(xport+6000);
      

//      String command=JOptionPane.showInputDialog("Enter command", 
////                                                 "/opt/jFileProcessor/server.sh");
//                                                 runCmd );

      String command = runCmd;
      Channel channel=session.openChannel("exec");
      command += "\n";
    logger.info( "exec at 6" );
    
    //channel.setPty(true);

      ((ChannelExec)channel).setCommand(command);
    logger.info( "exec at 7" );

      // X Forwarding
       //channel.setXForwarding(true);

//       channel.sendRequests();
       
      channel.setInputStream(System.in);
//      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
      ((ChannelExec)channel).setErrStream(System.err);

    InputStream in=channel.getInputStream();
    logger.info( "exec at 8" );

    channel.connect();
    logger.info( "exec at 9" );

    Pattern startedOnPortMsg = Pattern.compile( ".*Tomcat started on port\\(s\\): (\\d+) \\(https\\).*" );   // the pattern to search for
    Pattern portInUseMsg = Pattern.compile( ".*Address already in use.*" );   // the pattern to search for
    Pattern connectionRefusedUseMsg = Pattern.compile( ".*Connection refused: connect.*" );
    
    byte[] tmp=new byte[1024];
    while ( connUserInfo.getState() != ConnUserInfo.STATE_CANCEL )  // this does not seem to ever find CANCEL value !! ?
        {
        //logger.info( "connUserInfo.getToUsingHttpsPort() = " + connUserInfo.getToUsingHttpsPort() );
        //logger.info( "connUserInfo.getState() = " + connUserInfo.getState() );
        while(in.available()>0)
            {
            int i=in.read(tmp, 0, 1024);
            if(i<0)break;
            String rin = new String( tmp, 0, i );
            //System.out.print( "rmt stream ->" + rin );
            Matcher m1 = startedOnPortMsg.matcher( rin );
            Matcher m2 = portInUseMsg.matcher( rin );
            Matcher m3 = connectionRefusedUseMsg.matcher( rin );

            // if we find a match, get the group 
            if (m1.find())
                {
                // we're only looking for one group, so get it
                connUserInfo.setToUsingHttpsPort( m1.group(1) );
                // print the group out for verification
                logger.info( "got portUsed = " + connUserInfo.getToUsingHttpsPort() );
                logger.info( "got connUserInfo.getToUri() = " + connUserInfo.getToUri() );
                }
            else if (m2.find())
                {
                logger.info( "Port Already In Use !" );
                return;
                }
//            else if (m3.find())
//                {
//                logger.info( "Connection refused: connect !" );
//                return;
//                }
            }
        //System.out.print( "DONE rmt stream." );
        if ( channel.isClosed() )
            {
            if ( in.available() > 0 ) continue; 
            logger.info( "exit-status: "+channel.getExitStatus());
            break;
            }
        try{Thread.sleep(1000);} 
        catch(Exception ee)
            { 
            logger.info( "JschSftpUtils() sleep exception" );
            connUserInfo.setState( ConnUserInfo.STATE_CANCEL );
            break;
            }
      } // while
    
    logger.info( "exec at 10" );
      channel.disconnect();
      session.disconnect();
    logger.info( "exec at 11" );
    }
    catch(Exception e){
      logger.severeExc( e);
    }
    logger.info( "exec() Done" );
  }

public void execX11ForwardingOrig( String user, String password, String host )
    {
    String xhost="127.0.0.1";
//    String xhost="192.168.56.30";
    int xport=0;

    try{
//      String host=null;
//      if(arg.length>0){
//        host=arg[0];
//      }
//      else{
//        host=JOptionPane.showInputDialog("Enter username@hostname",
//                                         System.getProperty("user.name")+
//                                         "@localhost"); 
//      }
//      String user=host.substring(0, host.indexOf('@'));
      host=host.substring(host.indexOf('@')+1);

      Session session=jsch.getSession(user, host, 22);

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    config.put("ForwardX11Trusted","yes" );
    logger.info( "at 3" );
    session.setConfig(config);


    session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
      UserInfo ui=new MyUserInfo();
      session.setUserInfo(ui);
      session.connect();

      Channel channel=session.openChannel("shell");

      channel.setXForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);

      channel.connect();
    }
    catch(Exception e){
      logger.severeExc( e);
    }
}
  
public void execX11Forwarding( String user, String password, String rhost )
    {
//    String xhost="127.0.0.1";
    String xhost="localhost";
//    String xhost="192.168.56.1";
//    String defCommand = "tty;who;/net2/programs/jFileProcessor/run.sh";
    String defCommand = "tty;who;/opt/jFileProcessor/server.sh";
    int xport=0;

    try{
    logger.info( "try execX11Forwarding   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try execX11Forwarding   rhost =" + rhost + "=" );

    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    Session session=jsch.getSession(user, rhost, 22);
    logger.info( "at 2" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "at 3" );
    session.setConfig(config);

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//      session.setUserInfo(ui);

      session.connect();

      Channel channel=session.openChannel("exec");
    logger.info( "at 4" );

//      String command=JOptionPane.showInputDialog("Enter command", 
//                                                 "echo =$DISPLAY=; cd /net2/programs/jFileProcessor; /usr/bin/java -jar /net2/programs/jFileProcessor/JFileProcessor.jar ");
      String command=JOptionPane.showInputDialog( "Enter command", 
                                                  defCommand );
      
      command += "\n";
      ((ChannelExec)channel).setCommand(command);
    logger.info( "at 5" );
      
      channel.setXForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
    logger.info( "at 6" );

      channel.connect();
    logger.info( "at 7" );
    }
    catch(Exception e){
      logger.severeExc( e);
    }
  }

public void execX11Two( String user, String password, String rhost )
    {
//    String xhost="127.0.0.1";
    String xhost="localhost";
//    String xhost="192.168.56.1";
//    String defCommand = "tty;who;/net2/programs/jFileProcessor/run.sh";
    String defCommand = "tty;who;/opt/jFileProcessor/run.sh";
    int xport=0;

    try{
	    	
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	Session session=jsch.getSession(user, rhost, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

                
                session.connect();
	    	logger.info( "Connected");
	    	
	    	Channel channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(defCommand);

      
      channel.setXForwarding(true);
      
                channel.setInputStream(null);
	        ((ChannelExec)channel).setErrStream(System.err);
	        
	        InputStream in=channel.getInputStream();
	        channel.connect();
	        byte[] tmp=new byte[1024];
	        while(true){
	          while(in.available()>0){
	            int i=in.read(tmp, 0, 1024);
	            if(i<0)break;
	            System.out.print(new String(tmp, 0, i));
	          }
	          if(channel.isClosed()){
	            logger.info( "exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        channel.disconnect();
	        session.disconnect();
	        logger.info( "DONE");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }

  }

public void execX11( String user, String password, String rhost )
    {
//    String xhost="127.0.0.1";
    String xhost = "localhost";
//     String xhost = "192.168.27.50";
    int xport = 0;
//    int xport = 1;
//    String defCommand = "tty;who;/opt/jFileProcessor/run.sh";
//    String defCommand = "tty;who;/usr/bin/kwrite";
    String defCommand = "tty;who;/opt/jFileProcessor/run.sh";

    try{
    logger.info( "try execX11   user =" + user + "=   to password =" + password + "=" );
    logger.info( "try execX11   rhost =" + rhost + "=" );

    if ( user == null )
        {
        rhost=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 

        // username and password will be given via UserInfo interface.
//        UserInfo ui=new MyUserInfo();
//        session.setUserInfo(ui);
        }
      
    Session session=jsch.getSession(user, rhost, 22);
    logger.info( "at 1" );

    session.setPassword( password );

    Properties config = new Properties();
    config.put("StrictHostKeyChecking","no");
    logger.info( "at 2" );
    session.setConfig(config);
    logger.info( "at 2.5" );

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
    logger.info( "at 2.6" );
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

    logger.info( "execX11   xhost =" + xhost + "=   to (xport+6000) =" + (xport+6000) + "=" );
      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
//      UserInfo ui=new MyUserInfo();
//      session.setUserInfo(ui);

    session.connect();

      Channel channel=session.openChannel("shell");
    logger.info( "at 3" );

//      String command=JOptionPane.showInputDialog("Enter command", 
//                                                 "echo =$DISPLAY=; cd /net2/programs/jFileProcessor; /usr/bin/java -jar /net2/programs/jFileProcessor/JFileProcessor.jar ");
      String command=JOptionPane.showInputDialog( "Enter command", 
                                                  defCommand + " \n");

//      ((ChannelExec)channel).setCommand(command);
//    logger.info( "at 4" );
      
      channel.setXForwarding(true);

    StringBufferInputStream reader = new StringBufferInputStream(command + " \n");
        channel.setInputStream(reader);
//    channel.connect();

//    channel.setInputStream(System.in);
      channel.setOutputStream(System.out);
    logger.info( "at 5" );

//    channel.setPty(true);
    channel.connect();
    logger.info( "at 6" );
    }
    catch(Exception e){
      logger.severeExc( e);
    }
  }

//private void connect (String command) {    
//    Channel channel = session.getChannel("shell");
//    channel.setXForwarding(true);
//    StringBufferInputStream reader = new StringBufferInputStream(command + " \n");
//    // or use
//    // ByteArrayInputStream reader = new ByteArrayInputStream((command + " \n").getBytes());
//    channel.setInputStream(reader);
//    channel.setOutputStream(System.out);
//    channel.connect();
//    try {
//        Thread.sleep(1000); // give GUI time to come up
//    } catch (InterruptedException ex) {
//        // print message
//    }
//    //channel.disconnect();
//}

        //
	// Execute a command and return the result as a String
	// 
	// @param command
	//            the command to execute
	// @return the result as a String
	// @throws IOException
	//
//	public String executeCommand(String command) throws IOException {
//		ps.println(command);
//
//		int size = 1024;
//		final byte[] tmp = new byte[size];
//		final StringBuilder sb = new StringBuilder();
//
//		while (true) {
//			while (input.available() > 0) {
//				int i = input.read(tmp, 0, 1024);
//				if (i < 0) {
//					break;
//				}
//				sb.append(new String(tmp, 0, i));
//			}
//
//			final String output = sb.toString();
//			if (output.contains("object")) {
//				break;
//			}
//
//			if (channel.isClosed()) {
//				if (input.available() > 0) {
//					int i = input.read(tmp, 0, 1024);
//					sb.append(new String(tmp, 0, i));
//				}
//				break;
//			}
//
//			try {
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				LOG.error(e);
//			}
//		}
//
//		return sb.toString();
//	}


  static int checkAck(InputStream in) throws IOException{
    int b=in.read();
    // b may be 0 for success,
    //          1 for error,
    //          2 for fatal error,
    //          -1
    if(b==0) return b;
    if(b==-1) return b;

    if(b==1 || b==2){
      StringBuffer sb=new StringBuffer();
      int c;
      do {
	c=in.read();
	sb.append((char)c);
      }
      while(c!='\n');
      if(b==1){ // error
	System.out.print(sb.toString());
      }
      if(b==2){ // fatal error
	System.out.print(sb.toString());
      }
    }
    return b;
  }

  public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
    public String getPassword(){ return passwd; }
    public boolean promptYesNo(String str){
      Object[] options={ "yes", "no" };
      int foo=JOptionPane.showOptionDialog(null, 
             str,
             "Warning", 
             JOptionPane.DEFAULT_OPTION, 
             JOptionPane.WARNING_MESSAGE,
             null, options, options[0]);
       return foo==0;
    }
  
    String passwd;
    JTextField passwordField=(JTextField)new JPasswordField(20);

    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){ return true; }
    public boolean promptPassword(String message){
      Object[] ob={passwordField}; 
      int result=
	  JOptionPane.showConfirmDialog(null, ob, message,
					JOptionPane.OK_CANCEL_OPTION);
      if(result==JOptionPane.OK_OPTION){
	passwd=passwordField.getText();
	return true;
      }
      else{ return false; }
    }
    public void showMessage(String message){
      JOptionPane.showMessageDialog(null, message);
    }
    final GridBagConstraints gbc = 
      new GridBagConstraints(0,0,1,1,1,1,
                             GridBagConstraints.NORTHWEST,
                             GridBagConstraints.NONE,
                             new Insets(0,0,0,0),0,0);
    private Container panel;
    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo){
      panel = new JPanel();
      panel.setLayout(new GridBagLayout());

      gbc.weightx = 1.0;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.gridx = 0;
      panel.add(new JLabel(instruction), gbc);
      gbc.gridy++;

      gbc.gridwidth = GridBagConstraints.RELATIVE;

      JTextField[] texts=new JTextField[prompt.length];
      for(int i=0; i<prompt.length; i++){
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.weightx = 1;
        panel.add(new JLabel(prompt[i]),gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;
        if(echo[i]){
          texts[i]=new JTextField(20);
        }
        else{
          texts[i]=new JPasswordField(20);
        }
        panel.add(texts[i], gbc);
        gbc.gridy++;
      }

      if(JOptionPane.showConfirmDialog(null, panel, 
                                       destination+": "+name,
                                       JOptionPane.OK_CANCEL_OPTION,
                                       JOptionPane.QUESTION_MESSAGE)
         ==JOptionPane.OK_OPTION){
        String[] response=new String[prompt.length];
        for(int i=0; i<prompt.length; i++){
          response[i]=texts[i].getText();
        }
	return response;
      }
      else{
        return null;  // cancel
      }
    }
  }
  * **/
}
