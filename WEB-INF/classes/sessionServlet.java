import BH.*;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*; 
import java.util.*;
import java.util.function.*;
import static java.util.Arrays.asList;
import java.text.DateFormat;
public class sessionServlet extends HttpServlet {
    private    List<String[]>   the_sessions;
    private    DateFormat df;


    public void init() throws ServletException  {
        the_sessions=new ArrayList<String[]>();
        df=DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
    {
        String sessionManagementString = getRandomString(); //create the session management string


        if ((!(req.getParameter("task")==null))&&(req.getParameter("task").trim().equals("deploy"))) {
            PrintWriter out = res.getWriter();
            out.println("<html>");
            out.println("<body>");
            out.println("<hr /><center><h1>sessionServlet Deployed</h1></center><hr />");
            out.println("</body>");
            out.println("</html>"); 
            return;
        }

        Consumer <String> forwardTo =(s) -> ForwardTo(s,req,res); //Create a lambda handler
        boolean is_first_visit=true;
        String[] this_session=new String[3]; //create a new string of elements for the session

        for(String[] a_session : the_sessions){

            if(a_session[0] != null){ //if the sessionManagementString is not null
                is_first_visit = false; //this is a pre-existing session
                this_session = a_session; //assign a_session to this_session
                break;
            }
        }

        //check the IP
        /*String ip = req.getRemoteAddr(); //get the IP address
        for (String [] a_session : the_sessions) { //for each session in the_sessions
            if (a_session[0].equals(ip)) {  //Found an active session
                is_first_visit=false; //set the flag to false to
                this_session=a_session; //assign a_session to this_session
                break;
            }
        }*/

        //End Session logic
        if((req.getParameter("name") != null) && (req.getParameter("name").equals("logout"))){
            String url = new String(""); //create an empty string for the URL forwarding
            HttpSession session = req.getSession(); //get the session
            Cookie[] cookies = req.getCookies(); //get all the cookies

            if(cookies != null) { //if the cookies isn't null
                for (Cookie cookie : cookies) {
                    cookie.setMaxAge(0); //actually delete the cookies
                    cookie.setPath("/"); //allow the entire application to access it
                    res.addCookie(cookie); //add the deleted cookie back to the browser
                }
            }

            the_sessions.remove(this_session); //remove this session
            req.setAttribute("thesessioncount", the_sessions.size()); //reset the size attribute
            session.invalidate(); //invalidate the session and unbind any object within the session
            forwardTo.accept("startSession.jsp"); //set the url back to the login page
            return;
        }

        if ((req.getParameter("task")==null)&&(!is_first_visit)) { //if this isn't the first visit
            the_sessions.remove(this_session); //remove the session from the list
            is_first_visit=true; // just used http://hoare.cs.umsl.edu/servlet/js_test/sessionServlet
        }

        //if this is
        req.setAttribute("thesessioncount",the_sessions.size()); //set the size of the session
        if (is_first_visit) { //if this is the first visit

            if (the_sessions.size()==10) { //if the_sessions size is equal to 30 **** change back to 10
                forwardTo.accept("noSessions.jsp");  //forward the page to the noSessions.jsp
                return;
            }

            //***replaced ip with sessionManagementString
            String[] new_session = {sessionManagementString, df.format(new Date()), "need a name"}; //****create a new session
            the_sessions.add(new_session); //add the new_session to the_sessions array list
            this_session=new_session; //assign new_session to this session

            //****set the attribute of the session management string
            req.setAttribute("sessionManagementString", this_session[0]);

            forwardTo.accept("startSession.jsp"); //forward the page to the startSession.jsp
            return;
        }

        String the_name="";
        String the_pw="";
        if (this_session[2].equals("need a name")) { //No name given yet

            the_name=req.getParameter("whoisit"); //get the "whoisit" parameter from the request
            the_pw=req.getParameter("passwd"); //get the "passwd" parameter from the request

            //if the name is null OR the length is 0 OR the checkPW comes back false
            if ((the_name==null)||(the_name.trim().length()==0)||checkPW(the_name,the_pw)) {

                the_sessions.remove(this_session); //remove the session from the the_sessions array list
                req.setAttribute("thesessioncount",the_sessions.size()); //set the size of the session
                forwardTo.accept("startSession.jsp"); //forward to the startSession.jsp
                return;  // didn't enter a name in startSession
            }
        }

        this_session[2]=the_name.trim(); //assign the name to this session
        req.setAttribute("thename", this_session[2]); //set the attribute in the session

        if (tooLong(this_session[1],df.format(new Date()))) {  //Has the session timed out?
            the_sessions.remove(this_session); //if so, remove the session
            forwardTo.accept("Expired.jsp"); //forward the page to Expired.jsp
            return;
        }
        else { //***Session has not timed out.
            this_session[1]=df.format(new Date()); //reset the last session activity time
            NotesBean thesenotes=new NotesBean(); //**** lock this

            final Object lock = req.getSession().getId().intern(); //create a lock for the object

            synchronized(lock) { //lock the object

                if (!req.getParameter("task").trim().equals("0")) {
                    thesenotes.setAll(req.getParameter("java_source"), Integer.parseInt(req.getParameter("version")));

                    if (req.getParameter("task").trim().equals("2")) {
                        thesenotes.setNotes(req.getParameter("notes"), req.getParameter("java_source"), Integer.parseInt(req.getParameter("version")));
                    }
                }
                req.setAttribute("thesessioncount", the_sessions.size()); //set the size of the session
                req.setAttribute("theBean", thesenotes); //set the bean **** must be synchronized
            }

            forwardTo.accept("getNotes.jsp");
            return;
        }


    }//end doGet

    boolean tooLong(String now,String then){
        //Check amount of time that passed
        return false;
    }
    boolean checkPW(String name,String password){
        //Check password against name
        return false;
    }

    public void log(String s){
        FileWriter fileWriter = null;
        try {
            String content =s+" at :"+new Date(System.currentTimeMillis()).toString()+"\n";
            File theLogFile = new File("/var/lib/tomcat/webapps/j-thompson/WEB-INF/classes/session.log");
            fileWriter = new FileWriter(theLogFile,true);
            fileWriter.write(content);
        } catch (IOException ex) {
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {

            }
        }

    }

    void ForwardTo(String s,HttpServletRequest req, HttpServletResponse res){
        RequestDispatcher rd= req.getRequestDispatcher(s);
        try {
            rd.forward(req, res);
        } catch (IOException|ServletException is) {
            log(" req from "+s+" not forwarded at ");
            try {
                throw is;
            } catch (Exception e) {
            }
        }
    }

    public void destroy()
    {
        log("The instance was destroyed");
    }

    public String getRandomString(){
        byte[] randbyte=new byte[10];
        Random rand  = new Random(System.currentTimeMillis());
        for (int idx = 0; idx <10; ++idx) {
            int randomInt = rand.nextInt(26); //0<=randomInt<26
            //System.out.println(randomInt);
            randbyte[idx]=(byte)(randomInt+65);  
        }
        try {
            String rs=new String(randbyte, "UTF-8");
            //System.out.println(rs);
            return rs;
        } catch (Exception e) {
            //System.out.println("bad string");
            return "bad";
        }
    }
}


