package producer.rabbit.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置类
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "boot_topic_exchange";
    public static final String QUEUE_NAME = "boot_queue";

    //1、交换机
    @Bean("bootExchange")
    public Exchange bootExchange(){
        //创建topic类型交换机
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    //2、队列
    @Bean("bootQueue")
    public Queue bootQueue() {
        //创建队列
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    //3、队列和交换机的绑定关系 Binding
    /*
     * 1、知道哪个队列
     * 2、知道哪个交换机
     * 3、设置routing key
     *
     * @Qualifier注解用于注入指定Bean
     * */
    @Bean   //不需要注入，不需要写名字
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue queue, @Qualifier("bootExchange") Exchange exchange) {
        //绑定
        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();
    }
}
