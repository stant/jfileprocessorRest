/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.boot.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.towianski.models.ServerUserFileRights;
import com.towianski.utils.MyLogger;
import com.towianski.utils.Rest;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author stan
 */
//@Component
//@Profile("server")
public class ServerUserFileRightsListHolder {
    
    private static final MyLogger logger = MyLogger.getLogger(ServerUserFileRightsListHolder.class.getName() );
	 
    public ServerUserFileRightsListHolder()
        {
        logger.info( "ServerUserFileRightsList() constuct" );
        }

    public static ServerUserFileRightsList readInServerUserFileRightsListFromFile()
        {
        logger.info( "readInServerUserFileRightsListFromFile()" );
        try {
            ServerUserFileRightsList obj = (ServerUserFileRightsList) Rest.readObjectFromFile("ServerUserFileRightsList.json", new TypeReference<ServerUserFileRightsList>(){} );
            if ( obj == null )
                {
                logger.info( "readInServerUserFileRightsListFromFile() Error reading json file" );
                obj = new ServerUserFileRightsList();
                if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
                    {
                    obj.getServerUserFileRightsList().add( new ServerUserFileRights( "stan", "test", "c:\\Downloads", "rwx" ) );
                    obj.getServerUserFileRightsList().add( new ServerUserFileRights( "admin", "test", "c:\\Downloads", "rwx" ) );
                    }
                else // posix
                    {
                    obj.getServerUserFileRightsList().add( new ServerUserFileRights( "stan", "test", "/tmp", "rwx" ) );
                    obj.getServerUserFileRightsList().add( new ServerUserFileRights( "stan", "test", "/tmp", "rwx" ) );
                    obj.getServerUserFileRightsList().add( new ServerUserFileRights( "admin", "test", "/tmp", "rwx" ) );
                    }
                
                Rest.saveObjectToFile( "ServerUserFileRightsList.json", obj );   // create example file is not existing

                //serverUserFileRights.setJFileFinderWin( this );
                }
            else
                {
                logger.info( "readInServerUserFileRightsListFromFile() read in json ok" );
                //serverUserFileRightsList = obj.getServerUserFileRightsList();
                //serverUserFileRights.setJFileFinderWin( this );
                //serverUserFileRights.infuseSavedValues();
                }
    //        if ( jFileFinderWin == null )
    //            return;
            return obj;
            }
        catch( Exception exc )
            {
            exc.printStackTrace();
            }
        return new ServerUserFileRightsList();
        }
        
    public static ServerUserFileRightsList useAllRightsForProfileClient()
        {
        logger.info( "readInServerUserFileRightsListFromFile()" );
        ServerUserFileRightsList obj = new ServerUserFileRightsList();
        obj.getServerUserFileRightsList().add( new ServerUserFileRights( "ALLOPEN", "", "", "rwx" ) );
        return obj;
        }
        
//    public static ArrayList<ServerUserFileRights> readInServerUserFileRightsFromFileList()
//        {
//        logger.info( "readInServerUserFileRightsFromFileList()" );
//        try {
//            ServerUserFileRightsList obj = (ServerUserFileRightsList) Rest.readObjectFromFile("ServerUserFileRightsList.json", new TypeReference<ServerUserFileRightsList>(){} );
//            if ( obj == null )
//                {
//                logger.info( "readInServerUserFileRightsFromFileList() Error reading json file" );
//                obj = new ServerUserFileRightsList();
//                obj.getServerUserFileRightsList().add( new ServerUserFileRights( "stan", "test", "/tmp", "rwx" ) );
//                obj.getServerUserFileRightsList().add( new ServerUserFileRights( "admin", "test", "/tmp", "rwx" ) );
//                
//                Rest.saveObjectToFile( "ServerUserFileRightsList.json", obj );
//
//                //serverUserFileRights.setJFileFinderWin( this );
//                }
//            else
//                {
//                logger.info( "readInServerUserFileRightsFromFileList() read in json ok" );
//                //serverUserFileRightsList = obj.getServerUserFileRightsList();
//                //serverUserFileRights.setJFileFinderWin( this );
//                //serverUserFileRights.infuseSavedValues();
//                }
//    //        if ( jFileFinderWin == null )
//    //            return;
//            return obj.getServerUserFileRightsList();
//            }
//        catch( Exception exc )
//            {
//            exc.printStackTrace();
//            }
//        return null;
//        }
        
//    public static ArrayList<ServerUserFileRights> readInServerUserFileRightsFromFileArrayList()
//        {
//        logger.info( "readInServerUserFileRightsFromFileArrayList()" );
//        try {
//            serverUserFileRightsList = (ArrayList<ServerUserFileRights>) Rest.readObjectFromFile("ServerUserFileRightsList.json", new TypeReference<ArrayList<ServerUserFileRights>>(){} );
//            if ( serverUserFileRightsList == null )
//                {
//                logger.info( "readInServerUserFileRightsFromFileArrayList() Error reading json file" );
//                serverUserFileRightsList = new ArrayList<ServerUserFileRights>();
//                serverUserFileRightsList.add( new ServerUserFileRights( "stan", "test", "/tmp", "rwx" ) );
//                serverUserFileRightsList.add( new ServerUserFileRights( "admin", "test", "/tmp", "rwx" ) );
//                
//                Rest.saveObjectToFile( "ServerUserFileRightsList.json", serverUserFileRightsList );
//
//                //serverUserFileRights.setJFileFinderWin( this );
//                }
//            else
//                {
//                logger.info( "readInServerUserFileRightsFromFileArrayList() read in json ok" );
//                //serverUserFileRights.setJFileFinderWin( this );
//                //serverUserFileRights.infuseSavedValues();
//                }
//    //        if ( jFileFinderWin == null )
//    //            return;
//            return serverUserFileRightsList;
//            }
//        catch( Exception exc )
//            {
//            exc.printStackTrace();
//            }
//        return serverUserFileRightsList;
//        }

}
