/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server3;

/**
 *
 * 
 */
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.nio.charset.*;
import java.net.*;
import java.util.*;
import javax.script.*;
public class Nbs {
    public ScriptEngine jsEngine;

    public Nbs() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        jsEngine = mgr.getEngineByName("JavaScript");
    }
    static String open_file(String file_name) throws Exception {
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

    public void handle_tick(ArrayList readable, ArrayList writable, Hashtable scope)
    throws Exception {
         try {
            String code = open_file("tick.js");
            jsEngine.eval(code);
            Invocable invocableEngine = (Invocable) jsEngine;
            invocableEngine.invokeFunction("tick", readable, writable, scope);
         } catch (ScriptException ex) {
             ex.printStackTrace();
         }
    }

    public void handle_message(String message, SocketChannel client,
         ArrayList readable, ArrayList writable, Hashtable scope) throws Exception {
         try {
            String code = open_file("handle.js");
            jsEngine.eval(code);
            Invocable invocableEngine = (Invocable) jsEngine;
            invocableEngine.invokeFunction("handle", message, client, readable, writable, scope);
            //client.close();
           
         } catch (ScriptException ex) {
             ex.printStackTrace();
         }
        //if (message.startsWith("GET / HTTP")) {
         //   write(client, "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\n\r\nThis is the resῼponse yall");
         //   client.close();
       // }
    }
    static void write(SocketChannel client, String message) throws Exception{
        byte[] message_bytes = message.getBytes("UTF8");
        ByteBuffer b = ByteBuffer.allocate(message_bytes.length);
        byte[] arr = b.array();

        for (int i = 0; i < arr.length; i++) {
            arr[i] = message_bytes[i];
        }
        System.out.println(message + "--");
        client.write(b);
    }
    static String read(SocketChannel client) throws Exception {
          
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

    /**
     * @param args the command line arguments
     */
    public void start() throws Exception {
        Hashtable scope = new Hashtable();
        int counter = 0;
        int cycles_per_tick = 100000;
        
        // TODO code application logic here
        Selector acceptSelector = Selector.open();
        //Selector acceptSelector = SelectorProvider.provider().openSelector();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //InetAddress lh = InetAddress.getLocalHost();
	String lh = "localhost";
        int port = 80;
        InetSocketAddress isa = new InetSocketAddress(lh, port);
	ssc.socket().bind(isa);
        
        SelectionKey acceptKey = ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);
        int keysAdded = 0;

        while ((keysAdded = acceptSelector.select()) > 0) {
            ArrayList readable = new ArrayList();
            ArrayList writable = new ArrayList();
            Set readyKeys = acceptSelector.selectedKeys();
	    Iterator i = readyKeys.iterator();
	    // Walk through the ready keys collection and process date requests.
	    while (i.hasNext()) {
		SelectionKey sk = (SelectionKey)i.next();
                i.remove();
                if (sk.isAcceptable()) {
                    ServerSocketChannel nextReady = (ServerSocketChannel)sk.channel();
                    // Accept the date request and send back the date string
                    SocketChannel client = nextReady.accept();
                    //Socket s = channel.socket();
                    client.configureBlocking( false );
                    client.register( acceptSelector, SelectionKey.OP_READ|SelectionKey.OP_WRITE  );
                    
                }

                else if (sk.isReadable()) {
                    SocketChannel client = (SocketChannel) sk.channel();
                    //Socket s = client.socket();
                    //System.out.println(read(s.getInputStream())); //no can do here
                    //String message = read(client);
                    //handle_message(client, message);
                    readable.add(client);

                }
               
                if (sk.isValid() && sk.isWritable()) {
                    SocketChannel client = (SocketChannel) sk.channel();
                    writable.add(client);
                }
            }
            Iterator readableLoop = readable.iterator();
            while (readableLoop.hasNext()) {
                SocketChannel client = (SocketChannel) readableLoop.next();
                readableLoop.remove();
                String message = read(client);
                if (!message.equals("")) {
                    System.out.println("message is " + message);
                    handle_message(message, client, readable, writable, scope);
                }
            }
            if (counter % cycles_per_tick == 0) {
                //System.out.println("tick");
                handle_tick(readable, writable, scope);
            }
            counter++;
        }
    }
    public static void main(String[] args) throws Exception {
        Nbs nbs = new Nbs();
        nbs.start();
    }
}
