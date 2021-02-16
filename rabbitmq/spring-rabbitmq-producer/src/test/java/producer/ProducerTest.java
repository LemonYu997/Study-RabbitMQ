package producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {
    //1、注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testHelloWorld() {
        //2、发送消息
        /*
         * 简单模式发送：
         * 参数1为路由key，这里等于队列名称
         * 参数2消息内容
         * */
        rabbitTemplate.convertAndSend("spring_queue", "hello world spring.....");
    }

    /**
     * 发送fanout消息
     * */
    @Test
    public void testFanout() {
        //2、发送消息
        /*
         * 广播模式发送：
         * 参数1为交换机名称
         * 参数2为路由key，这里为""
         * 参数3为消息内容
         * */
        rabbitTemplate.convertAndSend("spring_fanout_exchange", "", "spring fanout....");
    }

    /**
     * 发送topic消息
     * */
    @Test
    public void testTopic() {
        //2、发送消息
        /*
         * 广播模式发送：
         * 参数1为交换机名称
         * 参数2为路由key
         * 参数3为消息内容
         * */
        //test.hehe.haha发送到了well中，因为*只能是一个单词，#可以有多个单词
        rabbitTemplate.convertAndSend("spring_topic_exchange", "test.hehe.haha", "spring topic....");
    }
}
