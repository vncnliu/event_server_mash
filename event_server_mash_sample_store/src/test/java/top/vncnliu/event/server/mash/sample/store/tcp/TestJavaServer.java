package top.vncnliu.event.server.mash.sample.store.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: vncnliu
 * Date: 2018/8/30
 * Description:
 */
public class TestJavaServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(12031));
        while(true){
            Socket socket = serverSocket.accept();
            new Thread(() ->{
                System.out.println("connected");
                try {
                    InputStream inputStream = socket.getInputStream();
                    byte[] msg = new byte[1];
                    while (inputStream.read(msg)!=-1){
                        /*try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        System.out.println(new String(msg));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
