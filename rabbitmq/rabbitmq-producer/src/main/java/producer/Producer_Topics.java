package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发送消息
 * */
public class Producer_Topics {
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
        //5、创建交换机
        /*
         * exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
         * 参数：
         * 1、exchange：交换机名称
         * 2、type：交换机类型
         *       DIRECT("direct")：定向
         *       FANOUT("fanout")：扇形（广播），发送消息到每一个与之绑定队列
         *       TOPIC("topic")：通配符
         *       HEADERS("headers")：参数匹配
         * 3、durable：是否持久化
         * 4、autoDelete：是否自动删除
         * 5、internal：内部使用，一般false
         * 6、arguments：参数列表
         * */
        String exchangeName = "test_topic";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, false, null);
        //6、创建队列
        /*
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * 参数：
         *  1、queue：队列名称，没有叫该名称的队列的话就会自动创建
         *  2、durable：是否持久化，持久化时MQ重启还在
         *  3、exclusive：
         *      是否独占。只能有一个消费者监听这个队列
         *      当Connection关闭时，是否删除队列
         *  4、autoDelete：是否自动删除，当没有Consumer时，自动删除掉
         *  5、arguments：参数信息
         * */
        String queue1Name = "test_topic_queue1";
        String queue2Name = "test_topic_queue2";
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);
        //7、绑定队列和交换机
        /*
         * queueBind(String queue, String exchange, String routingKey)
         * 参数：
         * 1、queue：队列名称
         * 2、exchange：交换机名称
         * 3、routingKey：路由键，绑定规则
         * */
        //routing key：系统的名称.日志的级别
        //队列1需求：所有error级别的日志存入数据库，所有order系统的日志存入数据库
        channel.queueBind(queue1Name, exchangeName, "#.error");
        channel.queueBind(queue1Name, exchangeName, "order.*");
        //队列2：所有
        channel.queueBind(queue2Name, exchangeName, "*.*");
        //8、发送消息
        /*
         * basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
         * 参数：
         *  1、exchange：交换机名称
         *  2、routingKey：路由名称
         *  3、props：配置信息
         *  4、body：发送消息数据
         * */
        String body = "日志信息：张三调用了findAll方法...日志级别：info...";
        channel.basicPublish(exchangeName, "order.info", null, body.getBytes());
        //9、释放资源
        channel.close();
        connection.close();
    }
}
