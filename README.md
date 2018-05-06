# jfileprocessor

This is a File and List Manager with search features: 

Does: Copy, Cut, Paste, Delete, New Folder,...

Search:
file finder by Name, Dates, or Sizes. MinDepth, MaxDepth.

You can Open, Edit, Copy filename, save list to a file.

Alt-Left, Alt-Right in Starting Folder: cycles thru previous search paths

has Bookmarks

Lets you save file lists to a "List Window" or a File.
You can add or subtract 1 list window to/from another.
Save the list window.
Read in a list to a window (adds items to existing list).

has pretty good search ability.

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


Remote Host connection:  This uses sftp and also https. 
the ssh server must have sftp turned on and this assumes port 22 for now.
it also uses https which assumes port 8443 for now. This gives it ssl security but does not check the validity/certificate of the host itself. You probably would use this on a network you know/trust anyways.

Escape: closes windows

Shift-Escape: close main window

![jfileprocess-1 4 9](https://user-images.githubusercontent.com/1928413/29250295-63377776-800e-11e7-93d8-53a006ddeb2d.png)

![jfileprocess-1 4 9-search](https://user-images.githubusercontent.com/1928413/29250304-7ed32dea-800e-11e7-80dc-baefc0c47cb3.png)


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
