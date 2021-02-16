package producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import producer.rabbit.config.RabbitMQConfig;

//SpringBoot测试必须加的注解
@SpringBootTest
@RunWith(SpringRunner.class)
//单元测试类必须和启动类放在同一包目录结构下！！！！
public class ProducerTest {

    //1、注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSend() {
        /*
         * 参数1是交换机名称，这里直接调用静态常量
         * 参数2是路由key
         * 参数3是消息内容
         * */
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.haha", "boot mq hello...");
    }
}
