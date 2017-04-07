import java.io.IOException;
import BH.*;
public class testNotesBean {
    public static void main(String[] args){
        new testNotesBean();
    }
    public testNotesBean(){
        NotesBean thebean = new NotesBean();
        thebean.setAll("littlefile.java",1);
        System.out.println(thebean.getThis_version());
        System.out.println(thebean.getNotes());
        System.out.println("--------------");
        thebean.setNotes("test2 little file","littlefile.java",1);
        System.out.println(thebean.getNotes());
        System.out.println("--------------");
        thebean.setAll("little.java",1);
        System.out.println(thebean.getVersion_id());

    }
}
