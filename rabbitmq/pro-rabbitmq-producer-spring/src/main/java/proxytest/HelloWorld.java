package proxytest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 测试HaProxy效果
 * */
public class HelloWorld {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //2、设置参数
        factory.setHost("192.168.14.3");    //IP    HaProxy的IP
        factory.setPort(5672);              //端口  HaProxy监听的端口
        //3、创建链接 Connection
        Connection connection = factory.newConnection();
        //4、创建channel
        Channel channel = connection.createChannel();
        //5、创建队列Queue
        channel.queueDeclare("hello_world", true, false, false, null);
        String body = "hello HaProxy~~";
        //6、发送消息
        channel.basicPublish("", "hello_world", null, body.getBytes());
        //7、释放资源
        channel.close();
        connection.close();

        System.out.println("send success...");
    }
}
