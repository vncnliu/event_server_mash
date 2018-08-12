package top.vncnliu.event.server.mash.base;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Random;

/**
 * User: vncnliu
 * Date: 2018/8/6
 * Description:
 */
public class Utils {

    public static int init(DatagramChannel channel, String hostName) throws IOException {
        try {
            Random random = new Random();
            int port = 40000+random.nextInt(10000);
            channel.socket().bind(new InetSocketAddress(hostName,port));
            return port;
        } catch (BindException e){
            return init(channel,hostName);
        }
    }
}
