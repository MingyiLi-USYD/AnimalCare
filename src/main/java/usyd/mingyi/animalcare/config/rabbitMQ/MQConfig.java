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
    public static final String SYSTEM_EXCHANGE = "SYSTEM_EXCHANGE";
    public static final String MESSAGE = "Message";
    public static final String SERVICE = "Service";
    public static final String SYSTEM = "System";
    public static final String MESSAGE_A = "MessageA";
    public static final String MESSAGE_B = "MessageB";

    public static final String SERVICE_A = "ServiceA";
    public static final String SERVICE_B = "ServiceB";


    public static final String SYSTEM_A = "SystemA";
    public static final String SYSTEM_B = "SystemB";




    @Bean(MESSAGE_EXCHANGE)
    public TopicExchange messageExchange(){
        return new TopicExchange(MESSAGE_EXCHANGE,true,false);
    }
    @Bean(MESSAGE_A)
    public Queue queueMessageA() {
        return new Queue(MESSAGE_A, true);
    }

    @Bean(MESSAGE_B)
    public Queue queueMessageB() {
        return new Queue(MESSAGE_B, true);
    }

    @Bean
    public Binding bindingMessageA(@Qualifier(MESSAGE_A) Queue messageAQueue, @Qualifier(MESSAGE_EXCHANGE) TopicExchange messageExchange) {
        return BindingBuilder.bind(messageAQueue).to(messageExchange).with("#");
    }

    @Bean
    public Binding bindingMessageB(@Qualifier(MESSAGE_B) Queue messageBQueue, @Qualifier(MESSAGE_EXCHANGE) TopicExchange messageExchange) {
        return BindingBuilder.bind(messageBQueue).to(messageExchange).with("#");
    }



    @Bean(SERVICE_EXCHANGE)
    public TopicExchange serviceExchange(){
        return new TopicExchange(SERVICE_EXCHANGE,true,false);
    }
    @Bean(SERVICE_A)
    public Queue queueServiceA() {
        return new Queue(SERVICE_A, true);
    }

    @Bean(SERVICE_B)
    public Queue queueServiceB() {
        return new Queue(SERVICE_B, true);
    }

    @Bean
    public Binding binding3(@Qualifier(SERVICE_A) Queue serviceAQueue, @Qualifier(SERVICE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(serviceAQueue).to(topicExchange).with("#");
    }

    @Bean
    public Binding binding4(@Qualifier(SERVICE_B) Queue serviceBQueue, @Qualifier(SERVICE_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(serviceBQueue).to(topicExchange).with("#");
    }



    @Bean(SYSTEM_EXCHANGE)
    public TopicExchange systemExchange(){
        return new TopicExchange(SYSTEM_EXCHANGE,true,false);
    }

    @Bean(SYSTEM_A)
    public Queue queueSystemA() {
        return new Queue(SYSTEM_A, true);
    }

    @Bean(SYSTEM_B)
    public Queue queueSystemB() {
        return new Queue(SYSTEM_B, true);
    }

    @Bean
    public Binding bindingSystemA(@Qualifier(SYSTEM_A) Queue systemAQueue, @Qualifier(SYSTEM_EXCHANGE) TopicExchange systemExchange) {
        return BindingBuilder.bind(systemAQueue).to(systemExchange).with("#");
    }

    @Bean
    public Binding bindingSystemB(@Qualifier(SYSTEM_B) Queue systemBQueue, @Qualifier(SYSTEM_EXCHANGE) TopicExchange systemExchange) {
        return BindingBuilder.bind(systemBQueue).to(systemExchange).with("#");
    }

}


