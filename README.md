# jfileprocessor

Place to get the official versions (many sites are behind):
![github main](https://github.com/stant/jfileprocessorRest)
![github main](https://github.com/stant/jfileprocessorRest/releases)

made using Java 17    (tested 17, 21, 22)

WINDOWS OS: Java Swing programs have an issue with high DPI displays. I do not know of a good solution
and the problem even varies between Java versions. You will have to play with your "compatibility" settings
in windows to get a window size that is not too small. 
Here is some help:  https://superuser.com/questions/988379/how-do-i-run-java-apps-upscaled-on-a-high-dpi-display

I found installing jfp in c:\program files(x86) or whatever causes a problem. I put a temp folder
under there and windows puts more restrictive rights on it so I could not copy remote temp files to it
so I install elsewhere like c:\programs\jfp

MAC OS: any issues, let me know as I do not always have access to test on it.

Report Issues at:  ![github main](https://github.com/stant/jfileprocessorRest/issues)

This is a File and List Manager with search features: 

Does: Copy, Cut, Paste, Delete, New Folder,...

Search:
file finder by Name, Dates, or Sizes. MinDepth, MaxDepth.

You can Open, Edit, Copy filename, save list to a file.

Alt-Left, Alt-Right in Starting Folder: cycles thru previous search paths

has Bookmarks

* Works with Lists of Strings, which can be filenames.
Lets you save file lists to a "List Window" or a File.
You can add or subtract 1 list window to/from another.
Save the list window.
Read in a list to a window (adds items to existing list).

* has pretty good search ability.

```java
search automatically prepends your starting Folder to your search pattern
so if you are in a sub-folder it will only search there and below !   watch for this if you do not find what you think.

glob: *whatever*.{java,groovy}     find file containing whatever and ending in .java or .groovy

glob: **whatever*.{java,groovy}     find file containing whatever and ending in .java or .groovy in any sub-folder level.

regex: .*mod.*[.](java|groovy)      find file containing whatever and ending in .java or .groovy in any sub-folder level. note [.] as \. does not work.

changing "What counts for a Match" to "Folder Only"

regex: .*src/.*                     find folder any level below

glob:  *src/*                       find src folder at this level.

glob:  **src/*                      find src folder any level with one sub-folder like ...../src/com

glob:  **src/**                     find src folder any level with any sub-folders like ...../src......

glob:  */*/whatever                 find whatever folder at 3rd level below starting folder
```


Remote Host connection:  This uses sftp or https. 
the ssh server must have sftp turned on.
sftp used to be a hybrid where it also used https to do filter searches. I did away with this,
which makes that slower but it is only uses ssh now. Now sftp uses only an ssh server!
I also do not try to install and run a jfp/https server.
To use ssh or https you have to start a server to connect to.
You can run openssh on linux (maybe mac too?) 
and now windows (11 anyways) has it's own implementation of openssh server which seems to work well!
You have to find a web page to see how to set it up. Not too bad.

for https. This gives it ssl security but does not check the validity/certificate of the host itself. You probably would use this on a network you know/trust anyways.


Escape: closes windows

Shift-Escape: close main window

![main](https://user-images.githubusercontent.com/1928413/61573244-25d7e900-aa60-11e9-9fec-1d39faa306cb.png)

* icons you will see:

red circle with a line means that folder is inaccessible.
figure 8 on left indicates a linked file (linux and Mac)
diagonal red line means the link is invalid
The 15 in the total count is including the folder itself we are in.


![jfileprocess-1 4 9-search](https://user-images.githubusercontent.com/1928413/29250304-7ed32dea-800e-11e7-80dc-baefc0c47cb3.png)

* "Open Folder Containing Files" or "Open Terminal here" 

For each file you have selected, it will open a new window or a terminal
for the parent path of each.
If nothing is selected it uses the "Folder:" you are in.

![use-as-a-desktop-or-job-executor](https://user-images.githubusercontent.com/1928413/61573277-8d8e3400-aa60-11e9-8012-9db7b5928743.png)

* JFP now can use its own File Associations. You can add, edit, and delete them.
It lets you have 3 different types at this point. One for each type.

* I create default assocs for "*.war" files. If you execute a war file it will run it in a tomcat container.
Just double-click on one!

* Using an Exact filename for matching, you can create folders and in the folders create fake file names that you associate to a "job". Now when you double-click on the "file", it executes whatever you tell it to.
Above I created execs to start programs like Chrome, Firefox, Netbeans, etc...

Here is an example to call konsole to run a shell script:

    "F:/net2/programs/Desktop/jfpr-github-count" : {
      "assocType" : "F",
      "matchType" : "G",
      "matchPattern" : "/net2/programs/Desktop/jfpr-github-count",
      "exec" : "/usr/bin/konsole --noclose -e /net2/github/JacksonExample/jfpr.bat",
      "stop" : ""
    },

* for Https File Server

```java
Edit file: user home/.JFileProcessor/ServerUserFileRightsList.json
You define in here a user login with password. You define which folders (or maybe a file) it controls
You set the rights the user has for that folder. It can be a combo of "r", "w", "x"
If you give them w:   they can write, delete, read to that remote folder.
If you give them r:   they can read that remote folder.

then run server.sh or server.bat

In the file manager you select a Connection: https
   and use the user/pass in the rights file on the server to login

cat ServerUserFileRightsList.json 
{
  "serverUserFileRightsList" : [ {
    "user" : "stan",
    "path" : "/tmp",
    "rights" : "rwx"
  }, {
    "user" : "stan",
    "path" : "/net3",
    "rights" : "rwx"
  }, {
    "user" : "admin",
    "path" : "/tmp",
    "rights" : "rwx"
  } ]
}

Notes: It is simple still for the overall rights on the server system still apply.
when you run server.sh you are running it as a user on that system.
if you run server.sh as linux or windows or mac user stan
all files uploaded to it are created by user stan since that is what the server is running under.
so jfp might have 50 users defined with different folder rights, but on the server they are all user stan.
so if jfp user mike and stan have w rights to c:\Downloads, they can both remotely upload and delete files in that folder.

if you only gave mike w rights for c:\mike  and gave stan w rights for c:\stan  then they cannot see or delete each others files.
```

* Jfp App Config Files

```bash
[ .JFileProcessor]$ ls -l
total 28
-rw-r--r-- 1 stan stan 1245 Dec  2 09:08 Bookmarks.txt
-rw-r--r-- 1 stan stan 5502 Dec  2 09:08 FileAssocList.json
drwxr-xr-x 2 stan stan 4096 Nov 30 18:05 groovy-scripts
-rw-r--r-- 1 stan stan  168 Jan  5 18:54 JfpUserHm.json         files for defined users and passwords
-rw-r--r-- 1 stan stan  366 Dec  2 09:08 ProgramMemory.json
-rw-r--r-- 1 stan stan  325 Nov  1 23:27 ServerUserFileRightsList.json      for https file server: you define user, path, rights (r,w,x,rw...)
drwxr-xr-x 6 stan stan 4096 Nov  9 00:42 TrashFolder
```

* Where is uses Groovy script files from:

Jfp Install-Folder/   groovy   or   menu-scripts
user home/.JFileProcessor/groovy-scripts

groovy: folders for groovy scripts. 

menu-scripts: special groovy scripts folder. Place scripts in here to make them show up in the right-click "Scripts" menu.
This is a short cut. It will run the script without a code window on the files you have selected.
Most of the groovy scripts I include as examples write to the same file system-temp-folder/NewFile.txt so be sure to modify as needed.
You can output to a window or a file as you want to.

You can create Java Swing GUI windows/JFrames/forms to use in your groovy scripts. 
Put them into JfpLib.jar which gets loaded at startup.
import these classes in the groovy scripts to pop up a form to get info from user to run commands...
These scripts have good working example of how to call JFP java methods as needed to get files selected, etc...

Here is an example groovy code file:

```java
package com.towianski.testutils;

class Test {
}

 static void main(String[] args) {
        System.out.println( "entered Test.main()");
//        def test = new Test();

        // we are going to write output to this file !
        def outFile = new File( "/tmp/NewFile.txt" );
        outFile.write "";

        com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
        def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );
        System.out.println( "got codeProcessorPanel.jFileFinderWin.getStartingFolder() =" + codeProcessorPanel.jFileFinderWin.getStartingFolder() + "=" );

        System.out.println( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
        int numItems = defaultComboBoxModel.getSize();
        System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
        String str = "";
        def atFile = null;
        for( int i = 0; i < numItems; i++ )
            {
            str = defaultComboBoxModel.getElementAt( i ).toString();
            System.out.println( "check for other list index =" + i + "   str =" + str + "=" );

            if ( 0 == 1 )  // example for list as files
                {
    //            String fileContents = new File( str ).text
    //            outFile << fileContents;
    //            String cmd = "ls -l " + str;
                String cmd = "grep root " + str;
                def list = cmd.execute().text
                list.eachLine{
                    outFile << it;
                    }
                outFile << System.getProperty("line.separator") + "-------------------------------------" + System.getProperty("line.separator");
                }
            else    // example for list of string
                {
                // text after last "root"
                if ( ( matcher = str =~ /.*(root)(.*)/) )
                outFile << matcher[0][1] + " - " + matcher[0][2];
                }
            }
   }  

//}

//Test test = new Test();

//println test.sayHello() 
//println test.doIt() 
```

icons from:

http://www.iconarchive.com/show/diagram-free-icons-by-double-j-design/document-icon.html
http://www.iconarchive.com/show/snowish-icons-by-saki/Folder-documents-icon.html
http://www.iconarchive.com/show/small-n-flat-icons-by-paomedia/file-link-icon.html
http://www.iconarchive.com/show/folder-icons-by-delacro/Folder-Blank-icon.html
http://www.iconarchive.com/show/folder-icons-by-delacro/Folder-Upload-icon.html
http://www.iconarchive.com/show/plump-icons-by-zerode/Search-icon.html
