package testit.model;

import com.towianski.jfileprocessor.JFileFinderWin;
import java.util.HashMap;
import java.util.Map;

public class ProgramMemory {

    //Map to store employees, ideally we should use database
    public Map<Integer, Employee> empData = new HashMap<Integer, Employee>();

    public JFileFinderWin jFileFinderWin = null; //new JFileFinderWin();

//        if ( args.length > 0 )
//            {
//            jffw.setStartingFolder( args[0] );
//            jffw.searchBtnAction( null );
//            }
//             static {
//        jFileFinderWin.setVisible(true);
//                 }

    public JFileFinderWin getjFileFinderWin()
        {
        return jFileFinderWin;
        }
}
