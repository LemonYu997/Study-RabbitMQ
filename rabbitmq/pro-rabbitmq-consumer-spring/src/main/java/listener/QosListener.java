package listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumer 限流机制
 * 1、确保ack机制为手动确认
 * 2、listener-container配置属性
 *  prefetch=1，表示消费端每次从mq拉一条消息来消费，直到手动确认消息消费完毕后，才会继续拉取下一条消息
 * */
@Component  //声明spring bean，方便进行包扫描
public class QosListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //1、获取消息
        System.out.println(new String(message.getBody()));

        //2、处理业务逻辑

        //3、签收，暂时关掉手动确认
        //channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
