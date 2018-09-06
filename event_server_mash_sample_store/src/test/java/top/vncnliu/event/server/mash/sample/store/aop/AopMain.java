package top.vncnliu.event.server.mash.sample.store.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * User: vncnliu
 * Date: 2018/9/5
 * Description:
 */
public class AopMain implements AopI {

    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
        AopMain aopMain = new AopMain();
        aopMain.run();
    }

    public void run(){
        InvocationHandler handler = new MyInvocationHandler(new TestA(), this);
        AopI aopMain = (AopI) Proxy.newProxyInstance(AopMain.class.getClassLoader(),new Class[]{ AopI.class },handler);
        aopMain.doSomething();
    }

    @Override
    public void doSomething(){
        System.out.println("do in main");
        doSomthing2();
    }

    @Override
    public void doSomthing2(){
        System.out.println("do 2 in main");
    }

    class MyInvocationHandler implements InvocationHandler {

        private TestA testA;
        private AopMain aopMain;

        public MyInvocationHandler(TestA testA, AopMain aopMain) {
            this.testA = testA;
            this.aopMain = aopMain;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            testA.testADo();
            return method.invoke(aopMain,args);
        }
    }
}
