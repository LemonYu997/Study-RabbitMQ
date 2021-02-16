package consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer_HelloWorld {
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
        /*
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * 参数：
         *  1、queue：队列名称
         *  2、durable：是否持久化，持久化时MQ重启还在
         *  3、exclusive：
         *      是否独占。只能有一个消费者监听这个队列
         *      当Connection关闭时，是否删除队列
         *  4、autoDelete：是否自动删除，当没有Consumer时，自动删除掉
         *  5、arguments：参数信息
         * */
        //如果没有一个名字叫hello_world的队列，则会创建该队列，如果有则不会创建
        channel.queueDeclare("hello_world", true, false, false, null);
        //6、接收消息
        /*
         * basicConsume(String queue, boolean autoAck, Consumer callback)
         * 参数：
         *  1、queue：队列名称
         *  2、autoAck：是否自动确认
         *  3、callback：回调对象
         * */
        Consumer consumer = new DefaultConsumer(channel) {
            /**
             * 回调方法：当收到消息后，会自动执行该方法
             * 参数：
             *  1、consumerTag：标识
             *  2、envelope：获取一些信息，交换机、路由key。。
             *  3、properties：配置信息
             *  4、body：数据
             * */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumerTag：" + consumerTag);
                System.out.println("Exchange：" + envelope.getExchange());
                System.out.println("RoutingKey: "+ envelope.getRoutingKey());
                System.out.println("properties: " + properties);
                System.out.println("body: " + new String(body));
            }
        };
        channel.basicConsume("hello_world", true, consumer);

        //不需要关闭资源
    }

    /*
     * 输出：
     * consumerTag：amq.ctag-HsdPxcuxY9DtkPN4Ophu7A
     * Exchange：
     * RoutingKey: hello_world
     * properties: #contentHeader<basic>(content-type=null, content-encoding=null, headers=null, delivery-mode=null, priority=null, correlation-id=null, reply-to=null, expiration=null, message-id=null, timestamp=null, type=null, user-id=null, app-id=null, cluster-id=null)
     * body: hello rabbitmq~~
     * */
}
