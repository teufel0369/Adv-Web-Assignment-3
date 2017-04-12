public class Session {
    public    String ip;
    public    String lastaccess;
    public    String name;
    public    String password;
    public    String securityString;
    public    int task;

    public Session(int begin){
        task=begin;
    }
    public Session(String the_ip,String the_la,String the_name,String the_pw,String the_ss,int the_task){
        ip=the_ip;             
        lastaccess=the_la;     
        name=the_name;           
        password=the_pw;       
        securityString=the_ss;
        task=the_task;

    }
}
