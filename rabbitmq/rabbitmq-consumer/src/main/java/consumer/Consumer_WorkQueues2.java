package consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer_WorkQueues2 {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //2、设置参数
        factory.setHost("192.168.14.3");            //ip 默认值为localhost
        factory.setPort(5672);                      //端口 默认值：5672
        factory.setVirtualHost("/");                //虚拟机 默认值 /
        factory.setUsername("admin");               //用户 默认值 guest
        factory.setPassword("111111");              //密码
        //3、创建连接 Connection
        Connection connection = factory.newConnection();
        //4、创建Channel
        Channel channel = connection.createChannel();
        //5、创建队列（简单模式不需要交换机）
        channel.queueDeclare("work_queues", true, false, false, null);
        //6、接收消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("body: " + new String(body));
            }
        };
        channel.basicConsume("work_queues", true, consumer);

        //不需要关闭资源
    }

    /*
     * 输出：
        body: 1hello rabbitmq~~
        body: 3hello rabbitmq~~
        body: 5hello rabbitmq~~
        body: 7hello rabbitmq~~
        body: 9hello rabbitmq~~
     * */
}
