package server3;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.sql.*;
/**
 *
 * @author Aimee
 */
public class Util {
    public Connection conn;
    public Statement stat;
    public ResultSet rs;
    public void db_open() throws Exception{
         Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        stat = conn.createStatement();
    }
    public ResultSet db_query(String query) throws Exception {
       rs = stat.executeQuery(query + ";");
       return rs;
    }
    public void db_update(String query) throws Exception {
        
        stat.executeUpdate(query);

    }


    

   public void db_close() throws Exception {
        rs.close();
        conn.close();
   }

    public String open_file(String file_name) throws Exception {
       File fileDir = new File(file_name);
        BufferedReader in = new BufferedReader(
           new InputStreamReader(
             new FileInputStream(fileDir), "UTF8"));
        String str;
        StringBuffer ret = new StringBuffer();
        while ((str = in.readLine()) != null) {
            ret.append(str);
        }
        in.close();
        return ret.toString();
    }

    public void write(SocketChannel client, String message) throws Exception{
        byte[] message_bytes = message.getBytes("UTF8");
        ByteBuffer b = ByteBuffer.allocate(message_bytes.length);
        byte[] arr = b.array();

        for (int i = 0; i < arr.length; i++) {
            arr[i] = message_bytes[i];
        }
        System.out.println(message + "--");
        client.write(b);
    }
    public String read(SocketChannel client) throws Exception {

       ByteBuffer b = ByteBuffer.allocate(1024);
       try {
        int numb = client.read(b);
        if (numb == -1) {
            client.close();
            return "";
        }
       } catch (Exception e) {
           client.close();
           return "";
       }
       b.flip();
       String ret = new String(b.array(), "UTF8");
       System.out.println(":)");
       return ret;
    }



}


