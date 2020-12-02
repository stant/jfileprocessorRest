package com.towianski.models;

public class JfpRestURIConstants {

	public static final String SYS_STOP = "/jfp/sys/stop";
	public static final String SYS_PING = "/jfp/sys/ping";
	public static final String SYS_GET_FILESYS = "/jfp/sys/filesys";

        public static final String HTTPS_CONNECT = "/jfp/rest/httpsConnect";
        public static final String SEARCH = "/jfp/rest/search";
	public static final String COPY = "/jfp/rest/copy";
	public static final String DELETE = "/jfp/rest/delete";
	public static final String RM = "/jfp/rest/rm";     // rm file(s)
//	public static final String RENAME_FILE = "/jfp/rest/rename/oldname/{oldname}/newname/{newname}";
	public static final String RENAME_FILE = "/jfp/rest/rename";
	public static final String GET_LATEST_GITHUB_VERSION_NUMBER = "/repos/stant/jfileprocessorRest/releases/latest";
	public static final String GET_FILE_SIZE = "/jfp/rest/getFileSize";
	public static final String GET_FILE_STAT = "/jfp/rest/getFileStat";
	public static final String GET_USER_HOME = "/jfp/rest/getUserHome";
	public static final String GET_FILE = "/jfp/rest/getFile";
	public static final String SEND_FILE = "/jfp/rest/sendFile";
	public static final String SEND_FILES = "/jfp/rest/sendFiles";
	public static final String DOES_FILE_EXIST = "/jfp/rest/doesFileExist";
	public static final String MKDIR = "/jfp/rest/mkDir";
	public static final String RMDIR = "/jfp/rest/rmDir";
}
