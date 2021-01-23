/*
Copyright (C) 2019 Stan Towianski 
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.towianski.utils.MyLogger;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author stan
 */
public class FileAssocList {
    
    private static final MyLogger logger = MyLogger.getLogger( FileAssocList.class.getName() );
    String version = "1.1";
    TreeMap<String,FileAssoc> fileAssocList = new TreeMap<String,FileAssoc>();
    ArrayList<FileAssoc> matchingFileAssocList = new ArrayList<FileAssoc>();

    
    public TreeMap<String, FileAssoc> getFileAssocList() {
        return fileAssocList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setFileAssocList(TreeMap<String, FileAssoc> fileAssocList) {
        this.fileAssocList = fileAssocList;
    }

    @JsonProperty
    public FileAssoc getFileAssoc( String key )
    {
        return fileAssocList.get(key);
    }

    @JsonProperty
    public void dumpFileAssocList()
        {
        logger.info( "Dump file assoc's :" );
        for( Map.Entry<String,FileAssoc> entry : fileAssocList.entrySet() ) 
            {
            String key = entry.getKey();
            FileAssoc fa = entry.getValue();

            logger.info( "" );
            logger.info( "fa.getAssocType =" + fa.getAssocType() + "=" );
            logger.info( "fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
            logger.info( "fa.getStartDir =" + fa.getStartDir() + "=" );
            logger.info( "fa.exec =" + fa.getExec() + "=" );
            logger.info( "fa.stop =" + fa.getStop() + "=" );
            }
        }

    @JsonProperty
    public ArrayList<FileAssoc> getFileAssocList( String selectedPath )
    {
        matchingFileAssocList = new ArrayList<FileAssoc>();
        Path fpath = Paths.get( selectedPath );
        PathMatcher matcher = null;

        logger.info( "\nsearch to match path =" + selectedPath + "=" );
        for( Map.Entry<String,FileAssoc> entry : fileAssocList.entrySet() ) {
            String key = entry.getKey();
            FileAssoc fa = entry.getValue();

            logger.finest( "Search thru:" );
            logger.finest( "fa.getAssocType =" + fa.getAssocType() + "=" );
            logger.finest( "fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
            logger.finest( "fa.getStartDir =" + fa.getStartDir() + "=" );
            logger.finest( "fa.exec =" + fa.getExec() + "=" );
            logger.finest( "fa.stop =" + fa.getStop() + "=" );

            if ( fa.getMatchType().equalsIgnoreCase( JfpConstants.MATCH_TYPE_REGEX) )
                {
                matcher = FileSystems.getDefault().getPathMatcher("regex:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //logger.info( "matching by regex =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            else
                {
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //logger.info( "matching by glob =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            if ( fpath.getFileName() != null && matcher.matches( fpath  ) )
                {
                logger.info( "FOUND match to add to List" );
                logger.info( "matched and got fa.getAssocType =" + fa.getAssocType() + "=" );
                logger.info( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
                logger.info( "matched and got fa.getStartDir =" + fa.getStartDir() + "=" );
                logger.info( "matched and got fa.exec =" + fa.getExec() + "=" );
                logger.info( "matched and got fa.stop =" + fa.getStop() + "=" );
                matchingFileAssocList.add( fa );
                }
            }
        return matchingFileAssocList;
    }

    @JsonProperty
    public FileAssoc getFileAssoc( String assocType, String selectedPath )
    {
        Path fpath = Paths.get( selectedPath );
        PathMatcher matcher = null;

        logger.info( "\nsearch for =" + assocType + ":" + selectedPath + "=" );
        for( Map.Entry<String,FileAssoc> entry : fileAssocList.entrySet() ) {
            String key = entry.getKey();
            FileAssoc fa = entry.getValue();

            if ( ! fa.getAssocType().equalsIgnoreCase(assocType) )
                continue;
            
            if ( fa.getMatchType().equalsIgnoreCase( JfpConstants.MATCH_TYPE_REGEX) )
                {
                matcher = FileSystems.getDefault().getPathMatcher("regex:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //logger.info( "matching by regex =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            else
                {
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //logger.info( "matching by glob =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            //logger.info( "\ntest chainfilterofNames =" + fpath + "=" );
            if ( fpath.getFileName() != null && matcher.matches( fpath  ) )
                {
                logger.info( "FOUND match" );
                logger.info( "matched and got fa.getAssocType =" + fa.getAssocType() + "=" );
                logger.info( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
                logger.info( "matched and got fa.getStartDir =" + fa.getStartDir() + "=" );
                logger.info( "matched and got fa.exec =" + fa.getExec() + "=" );
                logger.info( "matched and got fa.stop =" + fa.getStop() + "=" );
                return fa;
                }
            }
        return null;
    }
    
    @JsonIgnore
    public void addFileAssoc( FileAssoc fileAssoc )
    {
        fileAssocList.put( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern(), fileAssoc );
    }
    
    @JsonIgnore
    public void deleteFileAssoc( FileAssoc fileAssoc )
    {
        logger.info( "DELETE =" + fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() + "=" );
        fileAssocList.remove( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() );
    }
    
    @JsonIgnore
    public void moveFileAssoc( String sourcePath, String targetPath )
    {
        logger.info( "moveFileAssoc() source =" + sourcePath + "=  target =" + targetPath + "=" );
        FileAssoc fileAssoc = getFileAssoc( JfpConstants.ASSOC_TYPE_EXACT_FILE, sourcePath );

        if ( fileAssoc != null )
            {
            logger.info( "DELETE =" + fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() + "=" );
            fileAssocList.remove( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() );

            fileAssoc.setMatchPattern( targetPath );
            logger.info( "REPLACE TO =" + fileAssoc.getAssocType() + ":" + targetPath + "=" );
            fileAssocList.put( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern(), fileAssoc );
            }
    }
}
