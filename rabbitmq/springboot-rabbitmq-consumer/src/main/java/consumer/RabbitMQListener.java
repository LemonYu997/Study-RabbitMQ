package consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component  //普通类的注入
public class RabbitMQListener {

    //监听队列
    @RabbitListener(queues = "boot_queue")
    public void ListenerQueue(Message message) {
        //Message为org.springframework.amqp.core.Message
        //打印消息
        System.out.println(new String(message.getBody()));
    }
}
