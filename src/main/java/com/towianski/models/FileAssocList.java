/*
Copyright (C) 2019 Stan Towianski 
 */
package com.towianski.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    String version = "1.0";
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
    public ArrayList<FileAssoc> getFileAssocList( String selectedPath )
    {
        matchingFileAssocList = new ArrayList<FileAssoc>();
        Path fpath = Paths.get( selectedPath );
        PathMatcher matcher = null;

        System.out.println( "\nsearch to match path =" + selectedPath + "=" );
        for( Map.Entry<String,FileAssoc> entry : fileAssocList.entrySet() ) {
            String key = entry.getKey();
            FileAssoc fa = entry.getValue();

            if ( fa.getMatchType().equalsIgnoreCase( JfpConstants.MATCH_TYPE_REGEX) )
                {
                matcher = FileSystems.getDefault().getPathMatcher("regex:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //System.out.println( "matching by regex =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            else
                {
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //System.out.println( "matching by glob =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            if ( fpath.getFileName() != null && matcher.matches( fpath  ) )
                {
                System.out.println( "FOUND match to add to List" );
                System.out.println( "matched and got fa.getAssocType =" + fa.getAssocType() + "=" );
                System.out.println( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
                System.out.println( "matched and got fa.exec =" + fa.getExec() + "=" );
                System.out.println( "matched and got fa.stop =" + fa.getStop() + "=" );
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

        System.out.println( "\nsearch for =" + assocType + ":" + selectedPath + "=" );
        for( Map.Entry<String,FileAssoc> entry : fileAssocList.entrySet() ) {
            String key = entry.getKey();
            FileAssoc fa = entry.getValue();

            if ( ! fa.getAssocType().equalsIgnoreCase(assocType) )
                continue;
            
            if ( fa.getMatchType().equalsIgnoreCase( JfpConstants.MATCH_TYPE_REGEX) )
                {
                matcher = FileSystems.getDefault().getPathMatcher("regex:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //System.out.println( "matching by regex =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            else
                {
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + fa.getMatchPattern().replace( "\\", "\\\\") );
                //System.out.println( "matching by glob =" + fa.getMatchPattern().replace( "\\", "\\\\") + "=" );
                }
            //System.out.println( "\ntest chainfilterofNames =" + fpath + "=" );
            if ( fpath.getFileName() != null && matcher.matches( fpath  ) )
                {
                System.out.println( "FOUND match" );
                System.out.println( "matched and got fa.getAssocType =" + fa.getAssocType() + "=" );
                System.out.println( "matched and got fa.getMatchPattern =" + fa.getMatchPattern() + "=" );
                System.out.println( "matched and got fa.exec =" + fa.getExec() + "=" );
                System.out.println( "matched and got fa.stop =" + fa.getStop() + "=" );
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
        System.out.println( "DELETE =" + fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() + "=" );
        fileAssocList.remove( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() );
    }
    
    @JsonIgnore
    public void moveFileAssoc( String sourcePath, String targetPath )
    {
        System.out.println( "moveFileAssoc() source =" + sourcePath + "=  target =" + targetPath + "=" );
        FileAssoc fileAssoc = getFileAssoc( JfpConstants.ASSOC_TYPE_EXACT_FILE, sourcePath );

        if ( fileAssoc != null )
            {
            System.out.println( "DELETE =" + fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() + "=" );
            fileAssocList.remove( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern() );

            fileAssoc.setMatchPattern( targetPath );
            System.out.println( "REPLACE TO =" + fileAssoc.getAssocType() + ":" + targetPath + "=" );
            fileAssocList.put( fileAssoc.getAssocType() + ":" + fileAssoc.getMatchPattern(), fileAssoc );
            }
    }
}
