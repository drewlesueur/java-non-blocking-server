

function handle(message, client, readable, writable, scope) {
    println("invoked¤¤o");
    if (message.substr(0,3) == "GET") {
         var ret = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\n\r\n";
         util.write(client, ret);
         var parse = message.split("\r\n");
         var first = parse[0];
         var path = first.split(" ")[1];
         path = decodeURIComponent(path);
         
         util.db_open();
         util.db_update("insert into users (name) values ('Peter')");
         var rs = util.db_query("select * from users");
         while(rs.next()) {
            util.write(client, rs.getString("name"));
         }
         util.write(client, path +  "☻");
         client.close();
     }
}

function handle_login() {

}




