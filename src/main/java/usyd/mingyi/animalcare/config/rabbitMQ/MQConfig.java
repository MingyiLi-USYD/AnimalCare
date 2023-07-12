package usyd.mingyi.animalcare.config.rabbitMQ;

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
    public static final String SERVICE_EXCHANGE = "SERVICE_EXCHANGE";

    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    public static final String QUEUE_D = "QD";

    @Bean(MESSAGE_EXCHANGE)
    public TopicExchange messageExchange(){
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

    @Bean
    public Binding binding2(@Qualifier(QUEUE_B) Queue QB,@Qualifier(MESSAGE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(QB).to(topicExchange).with("#");
    }


    @Bean(SERVICE_EXCHANGE)
    public TopicExchange serviceExchange(){
        return new TopicExchange(SERVICE_EXCHANGE,true,false);
    }
    @Bean(QUEUE_C)
    public Queue queueC() {
        return new Queue(QUEUE_C, true);
    }

    @Bean(QUEUE_D)
    public Queue queueD() {
        return new Queue(QUEUE_D, true);
    }

    @Bean
    public Binding binding3(@Qualifier(QUEUE_C) Queue QC,@Qualifier(SERVICE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(QC).to(topicExchange).with("#");
    }

    @Bean
    public Binding binding4(@Qualifier(QUEUE_D) Queue QD,@Qualifier(MESSAGE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(QD).to(topicExchange).with("#");
    }
}


