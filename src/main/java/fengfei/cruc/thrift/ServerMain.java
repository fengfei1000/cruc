package fengfei.cruc.thrift;

import java.lang.reflect.Method;

public class ServerMain {

    public static void main(String args[]) {
        String rsMain = args == null || args.length == 0 ? null : args[0];
        System.out.println("args: rsMain=" + rsMain);
        ThriftServer relationServer = new ThriftServer( rsMain);
        try {
            relationServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            relationServer.shutdown();
        }
        Method method= null;
       
    }
}
