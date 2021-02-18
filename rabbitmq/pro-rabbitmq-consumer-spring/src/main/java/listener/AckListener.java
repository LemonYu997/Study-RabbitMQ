package listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumer ACK机制
 * 1、默认为自动签收，设置手动签收  acknowledge="manual"
 * 2、让监听器类实现ChannelAwareMessageListener（是MessageListener的子接口）
 * 3、如果消息成功处理，则调用channel的basicAck()签收
 * 4、如果消息处理失败，则调用channel的basicNack()拒绝签收，broker重新发送consumer
 * */
@Component  //声明spring bean，方便进行包扫描
public class AckListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        //获取消息的deliveryTag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1、接收消息
            System.out.println(new String(message.getBody()));
            //2、处理业务逻辑
            System.out.println("处理业务逻辑 ....");
            //模拟一个错误
            //int i = 3 / 0;

            //3、手动签收
            /*
             * 参数1为消息的deliveryTag，一个标签
             * 参数2为是否允许多条消息同时被签收
             * */
            channel.basicAck(deliveryTag, true);
        } catch (IOException e) {
            //e.printStackTrace();

            //4、出现异常拒绝签收
            //前两个参数同basicAck，参数3为requeue 重回队列，设为true则消息重新回到queue，broker会重新发送消息给消费端
            channel.basicNack(deliveryTag, true, true);

            //旧方法，只允许单条消息被签收，了解即可
            //channel.basicReject(deliveryTag, true);
        }
    }
}
