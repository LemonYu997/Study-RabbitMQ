package listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-consumer.xml")
public class ConsumerTest {

    //一直开启，监听器类会自动执行
    @Test
    public void test() {
        while (true) {

        }
    }

    /*
     * 输出（没有进行手动确认，发送了10条消息，就收到了1条）：
        message send..
     * */
}
