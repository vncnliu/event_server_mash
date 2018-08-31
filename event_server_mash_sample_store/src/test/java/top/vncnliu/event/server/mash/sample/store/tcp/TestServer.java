package top.vncnliu.event.server.mash.sample.store.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * User: vncnliu
 * Date: 2018/8/30
 * Description:
 */
public class TestServer {
    public static void main(String[] args) {
        EventLoopGroup main = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap sbs = new ServerBootstrap();
        //sbs.group(main,worker).childHandler();
    }
}
