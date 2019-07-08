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
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author stan
 */
public class FileAssocList {
    
    String version = "1.0";
    TreeMap<String,FileAssoc> fileAssocList = new TreeMap<String,FileAssoc>();

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
                matcher = FileSystems.getDefault().getPathMatcher("regex:" + fa.getMatchPattern());
                System.out.println( "matching by regex:" + fa.getMatchPattern() );
                }
            else
                {
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + fa.getMatchPattern());
                System.out.println( "matching by glob:" + fa.getMatchPattern() );
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
}
