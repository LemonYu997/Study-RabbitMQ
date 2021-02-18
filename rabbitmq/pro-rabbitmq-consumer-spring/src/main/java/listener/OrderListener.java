package listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 延迟队列监听
 * */
@Component  //声明spring bean，方便进行包扫描
public class OrderListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        //获取消息的deliveryTag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1、接收消息
            System.out.println(new String(message.getBody()));
            //2、处理业务逻辑
            System.out.println("处理业务逻辑 ....");
            System.out.println("根据订单id查询其状态...");
            System.out.println("判断状态是否为支付成功...");
            System.out.println("取消订单，回滚库存...");

            //3、手动签收
            /*
             * 参数1为消息的deliveryTag，一个标签
             * 参数2为是否允许多条消息同时被签收
             * */
            channel.basicAck(deliveryTag, true);
        } catch (IOException e) {
            System.out.println("出现异常，拒绝接受");
            //4、拒绝签收，不重回队列，这样才能发送到死信队列中
            //参数3为requeue，是否重回队列，这里设为false
            channel.basicNack(deliveryTag, true, false);
        }
    }
}
