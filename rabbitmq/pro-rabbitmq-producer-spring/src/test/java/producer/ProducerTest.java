package producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Spring测试类，需要有下边两个注解
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {
    //注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 确认模式：消息发送给交换机，返回一个回调函数
     * 步骤：
     *      1、确认模式开启：ConnectionFactory中开启 publisher-confirms="true"
     *      2、在rabbitTemplate定义ConfirmCallback回调函数
     * */
    @Test
    public void testConfirm() {
        //定义回调函数
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * @param correlationData 相关配置信息
             * @param b ack，代表exchange是否成功收到了消息，true表示成功，false代表失败
             * @param s cause，失败原因
             * */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("confirm方法被执行了....");
                if(b) {
                    //接收成功
                    System.out.println("接收成功消息" + s);
                } else {
                    //接收失败
                    System.out.println("接收失败消息" + s);
                    //做一些处理，让消息再次发送
                }
            }
        });

        /*
         * 发送消息：
         * 参数1为交换机名称
         * 参数2为routing key
         * 参数3为消息内容
         * */
        rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "message confirm..");

        /*
         * 输出：
            confirm方法被执行了....
            接收成功消息null
         * */
    }


    /**
     * 回退模式：消息发送给交换机后，exchange路由到queue失败时会执行ReturnCallback
     * 步骤：
     *      1、开启回退模式
     *      2、设置ReturnCallback
     *      3、设置Exchange处理消息的模式
     *          3.1 如果消息没有路由到queue，则丢弃消息（默认）
     *          3.2 如果消息没有路由到queue，返回给消息发送方ReturnCallback
     * */
    @Test
    public void testReturn() {

        //设置交换机处理失败消息的模式
        rabbitTemplate.setMandatory(true);  //返回消息

        //设置ReturnCallack
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * @param message 消息对象
             * @param i replyCode 错误码
             * @param s replyText 错误信息
             * @param s1 exchange 交换机名称
             * @param s2 routingKey 路由键
             * */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                //默认情况下不执行，这里开启了交换机处理失败消息模式，所以会执行
                System.out.println("return执行了...");

                System.out.println(message);    //消息对象
                System.out.println(i);          //replyCode 错误码
                System.out.println(s);          //replyText 错误信息
                System.out.println(s1);         //exchange 交换机名称
                System.out.println(s2);         //routingKey 路由键

                //后续处理
            }
        });

        /*
         * 发送消息：
         * 参数1为交换机名称
         * 参数2为routing key 这里故意给一个错误routingKey来测试return模式
         * 参数3为消息内容
         * */
        rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm11", "message return..");

        /*
         * 输出：
            return执行了...
            (Body:'message return..' MessageProperties [headers={}, contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0])
            312
            NO_ROUTE
            test_exchange_confirm
            confirm11
         * */
    }

    @Test
    public void testSend() {
        /*
         * 发送消息：
         * 参数1为交换机名称
         * 参数2为routing key
         * 参数3为消息内容
         * */
        //一次发10条
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "message send..");
        }
    }

    /**
     * TTL：过期时间
     * 1、队列统一过期
     * 2、消息单独过期
     * 如果设置了消息的过期时间，也设置了队列的过期时间，以时间短的为准
     * 队列过期后，会将队列所有消息全部移除
     * 消息过期后，只有消息在队列顶端，才会判断其是否过期（移除掉）
     * */
    @Test
    public void testTtl() {

        //消息单独过期
        //添加新参数 MessagePostProcessor()，消息后处理对象，设置一些消息的参数信息
        rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl..", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //1、设置message的信息
                message.getMessageProperties().setExpiration("5000");   //消息的过期时间
                //2、返回该消息
                return message;
            }
        });


        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                //消息单独过期
                rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl..", new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //1、设置message的信息
                        message.getMessageProperties().setExpiration("5000");   //消息的过期时间
                        //2、返回该消息
                        return message;
                    }
                });
            } else {
                //其他没有过期的消息
                rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl..");
            }
        }
    }

    /**
     * 发送测试死信消息
     *  1、过期时间测试
     *  2、长度限制测试
     *  3、消息拒收测试
     * */
    @Test
    public void testDlx() {
        //1、测试过期时间的死信消息，TTL为10s
        //将消息发给正常交换机到正常队列test_queue_dlx中，由于设置了TTL为10s，观察10s后该消息是否会发到queue_dlx队列中
        //rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "这条消息会成为死信吗?");

        //2、测试长度限制后的死信消息，队列长度设为10（最多接收10条消息）
        //由于队列长度限制为10，所有test_queue_dlx中有10条，queue_dlx中有10条死信消息
        //由于TTL为10s，10s后，test_queue_dlx中的10条消息会变成死信消息发送到queue_dlx中
//        for (int i = 0; i < 20; i++) {
//            rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "这条消息会成为死信吗?");
//        }

        //3、测试消息拒收
        rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "这条消息会成为死信吗?");
    }

    /**
     * 延迟队列的代码实现
     * */
    @Test
    public void testDelay() throws InterruptedException {
        //1、发送订单消息，将来是在订单系统中，下单成功后，发送消息
        rabbitTemplate.convertAndSend("order_exchange", "order.message", "订单信息：id=1, time=2021.2.18");
        //2、打印倒计时10秒
        for (int i = 0; i < 10; i++) {
            System.out.println(i + "...");
            Thread.sleep(1000);
        }
    }
}
