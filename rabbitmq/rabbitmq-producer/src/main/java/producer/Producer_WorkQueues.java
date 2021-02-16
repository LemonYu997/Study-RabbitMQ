package producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发送消息
 * */
public class Producer_WorkQueues {
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
        //如果没有一个名字叫work_queues的队列，则会创建该队列，如果有则不会创建
        channel.queueDeclare("work_queues", true, false, false, null);
        //6、发送消息
        /*
         * basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
         * 参数：
         *  1、exchange：交换机名称，简单模式下交换机会使用默认的""
         *  2、routingKey：路由名称，使用默认交换机时要与队列名称一致
         *  3、props：配置信息
         *  4、body：发送消息数据
         * */
        for (int i = 0; i <= 10; i++) {
            String body = i + "hello rabbitmq~~";
            channel.basicPublish("", "work_queues", null, body.getBytes());
        }
        //7、释放资源
        channel.close();
        connection.close();
    }
}
