import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Server {

    public static  void main(String[] args){
        chatServer chatServer=new chatServer();
        new Thread(()->{
            try{
                ServerSocket serverSocket=new ServerSocket(8000);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(new fuwu(chatServer,socket)).start();
                    //InetAddress inetAddress=serverSocket.getInetAddress();

                }
            }catch (IOException e){
                System.err.println(e);
            }
        }).start();

        //
        new Thread(()->{
            try{
                ServerSocket serverSocket1=new ServerSocket(8080);

                while (true) {
                    Socket socket1 =serverSocket1.accept();
                    //DataInputStream in1=new DataInputStream(socket1.getInputStream());
                    //String name= in1.readUTF();
                    //in1.close();
                    chatServer.addNameSocket(socket1);
                    //InetAddress inetAddress=serverSocket.getInetAddress();
                }
            }catch (IOException e){
                System.err.println(e);
            }
        }).start();
    }

}

class fuwu implements Runnable {
    private Socket socket;
    private Connection connection;
    private DataOutputStream out;
    private DataInputStream in;
    private PreparedStatement preparedStatement;//
    private boolean bb=false;
    private boolean stop=true;
    private chatServer chatServer;

    public fuwu(chatServer chatserver ,Socket socket) {
        this.chatServer=chatserver;
        this.socket = socket;
    }
    private void Login() throws IOException {
        String s=in.readUTF();
        String[] ss=s.split(" ");
        try {
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select password from id_name where ID="+ss[0]);
            if(resultSet.next()){
                //System.out.println(resultSet.getString(1));
                if(resultSet.getString(1).equals(ss[1])) {
                    out.writeUTF("密码正确");
                    bb=true;

                    System.out.println("密码正确");
                }
                else
                    out.writeUTF("密码错误");
            }
            else {
                out.writeUTF("没有此用户");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void getName() throws IOException {//不安全

        String id = in.readUTF();//id

        try {
            //System.out.println("查询数据库");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select name from id_name where ID=" + id);
            if (resultSet.next()) {
                String s = resultSet.getString(1);
                System.out.println(s);
                out.writeUTF(s);
            }
            Statement statemen = connection.createStatement();
            ResultSet resultSe = statemen.executeQuery("select fid from friend where ID=" + id);
            String fn = "";
            int i=0;
            while (resultSe.next()) {
                i++;
                String fid = resultSe.getString(1);
                //System.out.println("fid"+fid);
                fn += fid + "\n";
                Statement stateme = connection.createStatement();
                ResultSet rsultSet = stateme.executeQuery("select name from id_name where ID=" + fid);
                if (rsultSet.next())
                    fn += rsultSet.getString(1) + "\n";
                //System.out.println(fn);
            }
            if(i!=0){
                out.writeUTF(fn);
            }else
                out.writeUTF("没有好友");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void  Register() throws IOException {
        String s= in.readUTF();
        String[] ss=s.split(" ");
        try {
            Statement stateme = connection.createStatement();
            ResultSet rsultSet=stateme.executeQuery("select name from id_name where ID="+ss[0]);
            if(!rsultSet.next()){
                PreparedStatement preparedStatement3=connection.prepareStatement("insert into id_name values(?,?,0,?)");
                preparedStatement3.setString(1,ss[0]);
                preparedStatement3.setString(2,ss[1]);
                preparedStatement3.setString(3,ss[2]);
                preparedStatement3.executeUpdate();
                rsultSet=stateme.executeQuery("select name from id_name where ID="+ss[0]);
                if(rsultSet.next())
                    out.writeUTF("注册成功");
                out.writeUTF("注册失败");

            }else {
                out.writeUTF("注册失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void addFriend() throws IOException {
        //不安全 未检查好友是否存在
        String[] c=in.readUTF().split(" ");
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into friend values(" + c[0] + "," + c[1] + ")");
            out.writeUTF("成功");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void fMessage() throws IOException {
        String[] id = in.readUTF().split(" ");//fid id
        //System.out.println(id[0]+id[1]);
        String m = in.readUTF();
        //System.out.println(m);
        try {
            preparedStatement = connection.prepareStatement("insert into message values(?,?,?,?)");
            preparedStatement.setString(1, id[0]);
            preparedStatement.setString(2, id[1]);
            preparedStatement.setString(3, m);
            preparedStatement.setString(4, "nosee");
            //System.out.println("准备更新");
            preparedStatement.executeUpdate();
            //System.out.println("准备完成");
            //Statement statement=connection.createStatement();
            //statement.executeUpdate("insert into message values("+id[0]+","+id[1]+","+"\'"+m+"\'"+","+"nosee"+")" );
            //out.writeUTF("发送成功");

            if (chatServer.message(id[0],id[1],m)) {
                PreparedStatement preparedStatement2 = connection.prepareStatement("update message set see='see' where id=? and fid=?");
                preparedStatement2.setString(1, id[0]);
                preparedStatement2.setString(2, id[1]);
                preparedStatement2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void sMessage() throws IOException {
        String[] id=in.readUTF().split(" ");//id fid

        try {
            String m="";
            PreparedStatement preparedStatement1=connection.prepareStatement("select  outmessage from message where id=? and fid=? and see=?");
            preparedStatement1.setString(1,id[0]);
            preparedStatement1.setString(2,id[1]);
            preparedStatement1.setString(3,"nosee");
            ResultSet rsultSet=preparedStatement1.executeQuery();
            //ResultSet rsultSet=stateme.executeQuery("select  outmessage from id_name where id="+id[0]+"and fid="+id[1]+"and see=\'nosee\'");//and
            while (rsultSet.next()){
                m+=rsultSet.getString(1);
            }
            if(m.equals(""))
                out.writeUTF("no message on");
            else
                out.writeUTF(m);
            PreparedStatement preparedStatement2=connection.prepareStatement("update message set see='see' where id=? and fid=?");
            preparedStatement2.setString(1,id[0]);
            preparedStatement2.setString(2,id[1]);
            preparedStatement2.executeUpdate();
            //stateme.executeUpdate("update message set see=\'see\' where id="+id[0]+"and fid="+id[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            connection =SQLCon.getCon();
            while (stop) {
                int x = in.readInt();

                System.out.println(x);
                switch (x) {
                    case 10:Login();break;//登陆
                    case 20:getName();break;//获取名字 好友列表
                    case 30:Register();break;//注册
                    case 40:addFriend();break;//加好友
                    case 50:fMessage();break;
                    case 60:sMessage();break;
                    case 100:stop=false;break;//下线等一系列操作。。。
                    default:break;
                }
            }
        } catch (IOException o) {

        }
    }
}
class chatServer {
    private HashMap<String,Socket> idSocket = new HashMap<>();
    private HashMap<String,DataInputStream> inHashMap=new HashMap<>();
    private HashMap<String,DataOutputStream> outHashMap=new HashMap<>();
    public void addNameSocket(Socket socket) throws IOException {
        DataInputStream in=new DataInputStream(socket.getInputStream());
        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
        String id=in.readUTF();
        idSocket.put(id,socket);
        inHashMap.put(id,in);
        outHashMap.put(id,out);
    }
    public boolean message(String fid,String id,String m) throws IOException {
        if(inHashMap.containsKey(fid)){
            DataOutputStream out1=outHashMap.get(fid);
            DataInputStream in1=inHashMap.get(fid);
            out1.writeUTF(id);
            if(in1.readUTF().equals("can")){
                out1.writeUTF(m);
                return true;
            }
            return false;
        }
        else {

            return false;
        }
    }
}
class SQLCon {
    private  ArrayList<Connection> connections=null;
    private static SQLCon sqlCon=new SQLCon();
    private String password;
    private SQLCon()  {
        connections=new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(int i=0;i<10;i++){
            try {
                //cs
                Connection connection= DriverManager.getConnection("jdbc:mysql://localhost/javabook?characterEncoding=utf8&useSSL=true", "root", "xiequn110234qq");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static Connection getCon(){
        if(sqlCon.connections.size()>0) {
            Connection connection = sqlCon.connections.get(0);
            sqlCon.connections.remove(connection);
            return connection;
        }
        else{
            Connection con= null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost/javabook?characterEncoding=utf8&useSSL=true", "root", "xiequn110234qq");
                //System.out.println("连接成功");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return con;
        }
    }
    public static void removeCon(Connection con){
        sqlCon.connections.add(con);
    }
    public static void clearCon(){
        sqlCon.connections.clear();
    }
}