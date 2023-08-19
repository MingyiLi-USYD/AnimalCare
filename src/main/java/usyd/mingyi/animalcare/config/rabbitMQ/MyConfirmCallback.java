package usyd.mingyi.animalcare.config.rabbitMQ;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyConfirmCallback implements RabbitTemplate.ConfirmCallback {
    public static final ConcurrentHashMap<String, Message> messages = new ConcurrentHashMap<>();

    public static final String Max_Retry = "maxRetry";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
    }
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //log.info("成功入队消息ID: {}",correlationData.getId());
        if(correlationData==null||!messages.contains(correlationData.getId())){
            return;
        }
        if (ack) {
            messages.remove(correlationData.getId());
        } else {
            retrySendMessage(correlationData);
        }
    }

    private void retrySendMessage(CorrelationData correlationData) {
        if (messages.contains(correlationData.getId())) {//其实可以不用判断是否包含,上面已经判断了,但是还是加上
            Message message = messages.get(correlationData.getId());
            MessageProperties properties = message.getMessageProperties();
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            AtomicInteger times = (AtomicInteger) headers.get(Max_Retry);
            if (times !=null&& times.get() > 0) {
                 headers.put(Max_Retry,times.decrementAndGet());
                 rabbitTemplate.send(properties.getReceivedExchange(),
                         properties.getReceivedRoutingKey(),
                         message);
            } else {
                // 超过最大重试次数 打日志等等 暂时就不写了
            }
            messages.remove(correlationData.getId());
        }
    }


}

