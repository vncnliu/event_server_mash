package top.vncnliu.event.server.mash.base;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Random;

/**
 * User: vncnliu
 * Date: 2018/8/6
 * Description:
 */
public class Utils {

    public static int init(DatagramChannel channel, Selector selector, String hostName) throws IOException {
        try {
            Random random = new Random();
            int port = 40000+random.nextInt(10000);
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(hostName,port));
            //Register the channel to manager and bind the event
            channel.register(selector,SelectionKey.OP_READ);
            return port;
        } catch (BindException e){
            return init(channel,selector,hostName);
        }
    }
}
