/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towianski.models.JfpRestURIConstants;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stan
 */
public class GithubClient {
    
//    private Client client;
//    private WebTarget userTarget;
//    private WebTarget userRepoTarget;
//    private WebTarget releaseCountTarget;

    public GithubClient() {
//        client = ClientBuilder.newClient();
//        userTarget = client.target("https://api.github.com/users/{username}");
//        userRepoTarget = client.target("https://api.github.com/users/{username}/repos");
//        releaseCountTarget = client.target("https://api.github.com/repos/{owner}/{repo}/releases");
    }
    
//    public String findUserByUsername(String username) {
//        Response res = userTarget
//                .resolveTemplate("username", username)
//                .request("application/json").get();
//        return res.readEntity(String.class);
//    }
        
//    public String findRepositoriesByUser(String username) {
//        Response res = userRepoTarget
//                .resolveTemplate("username", username )
//                .request("application/json").get();
//        return res.readEntity(String.class);
//    }
    
//    public String findReleaseCount( String owner, String repo ) {
//        Response res = releaseCountTarget
//                .resolveTemplate( "owner", owner )
//                .resolveTemplate( "repo", repo )
//                .request("application/json").get();
//        return res.readEntity(String.class);
//    }
    
    public static String findLatestReleaseVersion()
        {
        try {
            RestTemplate noHostVerifyRestTemplate = Rest.createNoHostVerifyRestTemplate();
            String response = noHostVerifyRestTemplate.getForEntity( "https://api.github.com" + JfpRestURIConstants.GET_LATEST_GITHUB_VERSION_NUMBER, String.class).getBody();
            System.out.println( "response =" + response + "=" );
            byte[] jsonBytes = response.getBytes(Charset.forName("UTF-8"));
            JsonNode rootNode = createJsonNodeFromBytes( jsonBytes );
//             printJsonNode( rootNode );
            return rootNode.get( "tag_name") != null ? rootNode.get( "tag_name").asText() : "0";
            } 
        catch (Exception ex) 
            {
            }
        return "0";
        }
    
    public static void main(String[] args) 
        {
        GithubClient githubClient = new GithubClient();
        //System.out.println( "find github user =" + githubClient.findUserByUsername( "stant" ) + "=" );

        //ConvJsonToMap convJsonToMap = new ConvJsonToMap();
        //convJsonToMap.prettyPrintBytes( githubClient.findUserByUsername( "stant" ).getBytes(Charset.forName("UTF-8")) );


//        JacksonTreeNodeExample jacksonTreeNodeExample = new JacksonTreeNodeExample();
//        //String findReleaseCountStr = githubClient.findReleaseCount( "opscode-cookbooks", "tomcat" );
//        String findReleaseCountStr = githubClient.findReleaseCount( args[0], args[1] );
//        System.out.println( "findReleaseCount =" + findReleaseCountStr + "=" );
//        byte[] jsonBytes = findReleaseCountStr.getBytes(Charset.forName("UTF-8"));
//        //convJsonToMap.prettyPrintBytes( jsonBytes );
//        JsonNode rootNode = jacksonTreeNodeExample.createJsonNodeFromBytes( jsonBytes );
//        //JsonNode rootNode = jacksonTreeNodeExample.createJsonNode();
//        jacksonTreeNodeExample.printJsonNode( rootNode );

//        String findReleaseCountStr = githubClient.findReleaseCount( args[0], args[1] );
//        System.out.println( "findReleaseCount =" + findReleaseCountStr + "=" );
//        byte[] jsonBytes = findReleaseCountStr.getBytes(Charset.forName("UTF-8"));
//        JsonNode rootNode = jacksonTreeNodeExample.createJsonNodeFromBytes( jsonBytes );
//        jacksonTreeNodeExample.printJsonNode( rootNode );
        }
    
    public static JsonNode createJsonNodeFromBytes( byte[] jsonBytes )
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            rootNode = mapper.readTree( jsonBytes );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GithubClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GithubClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rootNode;
    }
    
//    public JsonNode createJsonNode()
//    {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = null;
//
//        try {
//            BufferedReader fileReader = new BufferedReader(
//                    new FileReader( "F:\\data\\testfiles\\releases.json" ));
//            rootNode = mapper.readTree( fileReader );
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(JacksonTreeNodeExample.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(JacksonTreeNodeExample.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return rootNode;
//    }
    
    public static void printJsonNode( JsonNode rootNode )
    {
//        System.out.println( "assets[0].name = " + rootNode.get(0).get( "assets" ).get(0).get( "name" ).asText() );
//        System.out.println( "assets[0].name = " + rootNode.get(0).get( "assets" ).get(0).get( "download_count" ).asText() );
    try {
        System.out.println( "rootNode size = " + rootNode.size() );
            Iterator<String> fieldNameIterator = rootNode.fieldNames();
            while (fieldNameIterator.hasNext()) {
                String fieldName = fieldNameIterator.next();
                System.out.println( "rootNode fieldName =" + fieldName + "=    value =" + rootNode.get(fieldName).asText() + "=" );
                }

        for ( JsonNode jnode : rootNode )
            {
            System.out.println( "jnode -----------------------------------" );
            // Iterate over node's properties
            fieldNameIterator = jnode.fieldNames();
            while (fieldNameIterator.hasNext()) {
                String fieldName = fieldNameIterator.next();
                System.out.println( "jnode fieldName = " + fieldName + "=" );
                }
                
            if ( jnode.get( "name" ) != null )
                {
                System.out.println( "jnode = " + jnode.get( "name" ).asText() );
                }
            if ( jnode.get( "assets" ) != null )
                {
                for ( JsonNode asset : jnode.get( "assets" ) )
                    {
                    if ( jnode.get( "name" ) != null )
                        {
                        System.out.println( "    asset[name] = " + asset.get( "name" ).asText() );
                        }
                    //if ( jnode.get( "download_count" ) != null )
                        {
                        System.out.println( "    asset[download_count] = " + asset.get( "download_count" ).asText() );
                        }
                    }
                }
            }
        } 
    catch (Exception ex) 
        {
        ex.printStackTrace();
        }
    }
    
}
