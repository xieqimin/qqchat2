import javafx.application.Application;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Client extends Application{
    private DataInputStream in;
    private DataOutputStream out;
    private String myid;//
    private String myname;//
    private  Socket socket;
    private Stage sttage;
    private HashMap<String,ChatUI> ID_chatUI=new HashMap<String,ChatUI>();

    public void setIDchatUI(String fid,ChatUI chatUI) {
        ID_chatUI.put(fid,chatUI);
    }
    public ChatUI getIDchatUI(String fid){
        if(ID_chatUI.containsKey(fid))
        return ID_chatUI.get(fid);
        else return null;
    }
    public void removeIDchatUI(String fid){
        ID_chatUI.remove(fid);
    }
    public void closeAllIDchatUI(){
        for(HashMap.Entry<String,ChatUI> entry:ID_chatUI.entrySet()){
            entry.getValue().close();
        }
        ID_chatUI.clear();
    }
    public void close(){
        try {
            out.writeInt(100);
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getMyname() {
        return myname;
    }
    public String getMyid() {
        return myid;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            socket=new Socket("localhost",8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = out =new DataOutputStream(socket.getOutputStream());
        }catch (IOException ex){
            ex.printStackTrace();
        }
        LoginUI chatUI =new LoginUI(this);
        sttage=chatUI.getUI();
        sttage.show();

    }

    public String checkpass(String ID,String pass) throws IOException {
        out.writeInt(10);
        out.writeUTF(ID+" "+pass);
        String s=in.readUTF();
        if(s.equals("密码正确")){
            out.writeInt(20);
            out.writeUTF(ID);
            myid=ID;
            String name=in.readUTF();
            Friend friend=new Friend(this,name);
            myname=name;
            String c=in.readUTF();
            if(!c.equals("没有好友")) {
                String[] cc = c.split("\n");
                for (int x = 0; x < cc.length; x += 2) {
                    String n = cc[x + 1];
                    friend.addFriend(n, cc[x]);
                }
            }
            sttage=friend.getUI();
            friend.setReceiveMessage(new ReceiveMessage(this));
            sttage.show();
        }
        return s;
    }

    public void fMessage(String fid,String message){//发信息
        try {
            //cansmessage=false;
            out.writeInt(50);
            out.writeUTF(fid+" "+myid);
            out.writeUTF(message);
            //String s=in.readUTF();
            //没有验证
            //cansmessage=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sMessage(String fid,String id) throws IOException {//收信息 不安全
        out.writeInt(60);
        out.writeUTF(id + " " + fid);
        String m = in.readUTF();
        return m;
    }
    public boolean addFriend(String fid){
        try {
            //好友不存在
            out.writeInt(40);
            out.writeUTF(myid+" "+fid);
            String s= in.readUTF();
            if(s.equals("成功"))
                return true;
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void Close(){

    }
    public boolean Register(String id ,String name,String password){
        try {
            out.writeInt(30);
            out.writeUTF(id+" "+name+" "+password);
            String s= in.readUTF();
            if(s.equals("注册成功"))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;//
    }
    //版本二
}

class ReceiveMessage implements Runnable {
    private Client client;
    private boolean stop = true;

    public ReceiveMessage(Client client) {
        this.client = client;
    }

    public void setStop(boolean b) {
        stop = b;
    }

    @Override
    public void run() {

        //Socket serverSocket1=new ServerSocket(8080);
        Socket socket1 = null;
        DataInputStream in;
        DataOutputStream out;
        try {
            socket1 = new Socket("Localhost", 8080);
            in = new DataInputStream(socket1.getInputStream());
            out = new DataOutputStream(socket1.getOutputStream());
            out.writeUTF(client.getMyid());
            while (stop) {
                //in.wait();
                String i = in.readUTF();
                if (client.getIDchatUI(i) == null) {
                    out.writeUTF("cannot");
                } else {
                    out.writeUTF("can");
                    String m = in.readUTF();
                    //fx
                    client.getIDchatUI(i).addMessage(m);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}