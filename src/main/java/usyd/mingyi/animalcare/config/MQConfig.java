package usyd.mingyi.animalcare.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MQConfig {
    public static final String MESSAGE_EXCHANGE = "MESSAGE_EXCHANGE";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";

    @Bean(MESSAGE_EXCHANGE)
    public TopicExchange topicExchange(){
        return new TopicExchange(MESSAGE_EXCHANGE,true,false);
    }
    @Bean(QUEUE_A)
    public Queue queueA() {
        return new Queue(QUEUE_A, true);
    }

    @Bean(QUEUE_B)
    public Queue queueB() {
        return new Queue(QUEUE_B, true);
    }

    @Bean
    public Binding binding1(@Qualifier(QUEUE_A) Queue QA,@Qualifier(MESSAGE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(QA).to(topicExchange).with("#");
    }

/*    @Bean
    public Binding binding2(@Qualifier(QUEUE_B) Queue QB,@Qualifier(MESSAGE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(QB).to(topicExchange).with("#");
    }*/
}


