

function handle(message, client, readable, writable, scope) {
    println("invoked¤¤o");
   
    if (message.substr(0,10) == "GET / HTTP") {
         util.write(client, "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\n\r\nThis is the respo☻nse yall");
         client.close();
     }
    
}

