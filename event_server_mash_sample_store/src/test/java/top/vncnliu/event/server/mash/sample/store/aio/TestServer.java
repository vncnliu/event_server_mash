package top.vncnliu.event.server.mash.sample.store.aio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;

/**
 * User: vncnliu
 * Date: 2018/7/30
 * Description:
 */
public class TestServer {

    @Test
    void testUdp(){
        try {
            DatagramChannel channel = DatagramChannel.open();
            //channel.bind(new InetSocketAddress())
            channel.socket().bind(new InetSocketAddress("192.168.1.155",10000));
            ByteBuffer buf = ByteBuffer.allocate(65535);
            while (true){
                buf.clear();
                channel.receive(buf);
                byte[] data = buf.array();
                String msg = new String(data).trim();
                System.out.println("======================================================");
                System.out.println(msg);
            }
        } catch (BindException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testSend(){
        try {
            DatagramChannel channel = DatagramChannel.open();
            //channel.bind(new InetSocketAddress())
            channel.socket().bind(new InetSocketAddress("192.168.1.155",10010));
            ByteBuffer buf = ByteBuffer.allocate(65535);
            for (int i = 0; i < 100; i++) {
                buf.put("2018-07-19 10:59:32.507 DEBUG 42719 --- [nioEventLoopGroup-9-34] c.m.n.handler.MjoysServerInboundHandler  : [ Server ] received : sysadmin.hz\t51522 ( MESSAGE_FLAG_SYS\tSYS_HEARTBEAT\t{\"portrait\":\"866624032972226\",\"uid\":\"wxid_7itlfyl95bw522\"} )\n".getBytes());
            }
            buf.flip();
            int byteSent = channel.send(buf, new InetSocketAddress("60.191.119.14", 10000));
            System.out.println(byteSent);
        } catch (BindException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        InputStream in = null;
        try
        {
            serverSocket = new ServerSocket(8080);
            int recvMsgSize = 0;
            byte[] recvBuf = new byte[1024];
            while(true){
                Socket clntSocket = serverSocket.accept();
                SocketAddress clientAddress = clntSocket.getRemoteSocketAddress();
                System.out.println("Handling client at "+clientAddress);
                in = clntSocket.getInputStream();
                while((recvMsgSize=in.read(recvBuf))!=-1){
                    byte[] temp = new byte[recvMsgSize];
                    System.arraycopy(recvBuf, 0, temp, 0, recvMsgSize);
                    System.out.println(new String(temp));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally{
            try{
                if(serverSocket!=null){
                    serverSocket.close();
                }
                if(in!=null){
                    in.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
