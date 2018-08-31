package top.vncnliu.event.server.mash.sample.store.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * User: vncnliu
 * Date: 2018/8/30
 * Description:
 */
public class TestJavaClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch main = new CountDownLatch(1);
        CountDownLatch childs = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() ->{
                try {
                    main.await();
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(12031));
                    String msg = "hello world "+finalI;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(msg.getBytes().length);
                    byteBuffer.put(msg.getBytes());
                    while (socket.isConnected()){
                        if(finalI ==3){
                            for (int j = 0; j < 100; j++) {
                                //Thread.sleep(1);
                                socket.getOutputStream().write(msg.getBytes());
                            }
                        } else {
                            socket.getOutputStream().write(msg.getBytes());
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        main.countDown();
        childs.await();
    }


}
