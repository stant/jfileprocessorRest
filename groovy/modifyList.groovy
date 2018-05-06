package com.towianski.testutils;

// written by: Stan Towianski - August 2017

import javax.swing.DefaultComboBoxModel;

class Test {
}

 static void main(String[] args) 
    {
    System.out.println( "entered modifyList.main()");
//        def test = new Test();

    com.towianski.jfileprocessor.CodeProcessorPanel codeProcessorPanel = binding.getVariable( "codeProcessorPanel" );
    def defaultComboBoxModel = binding.getVariable( "defaultComboBoxModel" );

    System.out.println( "selected item =" + codeProcessorPanel.listOfLists.getSelectedItem() + "=" );
    int numItems = defaultComboBoxModel.getSize();
    System.out.println( "defaultComboBoxModel.getSize() num of items =" + numItems + "=" );
    String str = "";
    ArrayList<String> newCbList = new ArrayList<String>();
    for( int i = 0; i < numItems; i++ )
        {
        str = defaultComboBoxModel.getElementAt( i ).toString();
        //System.out.println( "check for other list index =" + i + "   str =" + str + "=" );

//        newCbList.add( str.replaceFirst( "^f\\:\\\\temp\\\\", "f\\:\\\\temp2\\\\" ) );
        // for example, change f:\temp\whatever.jpg to f:\temp2\whatever.jpg
        newCbList.add( str.replaceFirst( "temp", "temp2" ) );
        }
    System.out.println( "newCbList.size() =" + newCbList.size() + "=" );
    DefaultComboBoxModel newModel = new DefaultComboBoxModel( newCbList.toArray() );
    // actually replace the JList model with a new one of the modified strings !
    codeProcessorPanel.setListPanelModel( codeProcessorPanel.listOfLists.getSelectedItem(), newModel );
   }
