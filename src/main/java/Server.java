

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.gson.Gson;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ivan on 7/4/2019.
 */
public class Server extends Listener
{

    List<Connection> connectionList = Collections.synchronizedList(new ArrayList<>());
    static com.esotericsoftware.kryonet.Server server;
    Gson gson = new Gson();
    //static int port = 9001;
    public Server() throws IOException
    {
        server = new com.esotericsoftware.kryonet.Server(16384,4096);
        server.bind(9001);
        server.start();
        server.addListener(this);
        server.getKryo().register(String.class);
        System.out.println("Started");
    }
    public void connected (Connection c)
    {
        System.out.println(c.getRemoteAddressTCP());
        connectionList.add(c);
        File f = new File(c.getRemoteAddressTCP().getHostString()+" "+c.getID()+".txt");
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true))))
        {
            out.println(LocalTime.now().toString()+" | "+ "Initiated connection");
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    public void disconnected(Connection c)
    {
        connectionList.remove(c);
    }
    public void received(Connection c, Object p)
    {
        if(p instanceof String) {
            System.out.println(c.getRemoteAddressTCP() + ":" + p);
            if (((String) p).contains("\"blackbox\":true"))
            {
                System.out.println("Sending:"+p);
                connectionList.parallelStream().forEach(cl -> cl.sendTCP(p));
                c.sendTCP(p); // temp issues/1999/
            }
            File f = new File(c.getRemoteAddressTCP().getHostString() + " " + c.getID() + ".txt");
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)))) {
                out.println(LocalTime.now().toString() + " | " + p.toString());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
