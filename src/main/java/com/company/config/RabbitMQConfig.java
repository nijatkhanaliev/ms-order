package com.company.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.company.model.constant.RabbitConstant.DELIVERY_COMPLETED_QUEUE;
import static com.company.model.constant.RabbitConstant.DELIVERY_COMPLETED_ROUTING_KEY;
import static com.company.model.constant.RabbitConstant.ORDER_EXCHANGE;
import static com.company.model.constant.RabbitConstant.STOCK_EXCHANGE;
import static com.company.model.constant.RabbitConstant.STOCK_RESPONSE_QUEUE;
import static com.company.model.constant.RabbitConstant.STOCK_RESULT_ROUTING_KEY;

@Configuration
public class RabbitMQConfig {

    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Bean
    public Queue stockResponseQueue() {
        return QueueBuilder.durable(STOCK_RESPONSE_QUEUE)
                .withArgument(X_DEAD_LETTER_EXCHANGE, ORDER_EXCHANGE + ".dlx")
                .withArgument(X_DEAD_LETTER_ROUTING_KEY, STOCK_RESULT_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue stockResponseDLQ() {
        return QueueBuilder.durable(STOCK_RESPONSE_QUEUE + ".dlq").build();
    }


    @Bean
    public Queue deliverCompletedQueue() {
        return QueueBuilder.durable(DELIVERY_COMPLETED_QUEUE)
                .withArgument(X_DEAD_LETTER_EXCHANGE, ORDER_EXCHANGE + ".dlx")
                .withArgument(X_DEAD_LETTER_ROUTING_KEY , DELIVERY_COMPLETED_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue deliveryCompletedDLQ() {
        return QueueBuilder.durable(DELIVERY_COMPLETED_QUEUE + ".dlq").build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(ORDER_EXCHANGE + ".dlx");
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(STOCK_EXCHANGE);
    }

    @Bean
    public Binding bindingStockResponseQueue(Queue stockResponseQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(stockResponseQueue)
                .to(orderExchange)
                .with(STOCK_RESULT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingStockResponseDLQ(Queue stockResponseDLQ, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(stockResponseDLQ)
                .to(deadLetterExchange)
                .with(STOCK_RESULT_ROUTING_KEY + ".dlq");
    }


    @Bean
    public Binding bindingDeliveryCompletedQueue(Queue deliverCompletedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(deliverCompletedQueue)
                .to(orderExchange)
                .with(DELIVERY_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDeliveryCompletedDLQ(Queue deliveryCompletedDLQ, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deliveryCompletedDLQ)
                .to(deadLetterExchange)
                .with(DELIVERY_COMPLETED_ROUTING_KEY + ".dlq");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

        return rabbitTemplate;
    }
}
