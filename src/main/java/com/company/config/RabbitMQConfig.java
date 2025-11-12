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
import static com.company.model.constant.RabbitConstant.ORDER_PAYMENT_FAILED_QUEUE;
import static com.company.model.constant.RabbitConstant.ORDER_PAYMENT_FAILED_ROUTING_KEY;
import static com.company.model.constant.RabbitConstant.PAYMENT_SUCCESS_QUEUE;
import static com.company.model.constant.RabbitConstant.PAYMENT_SUCCESS_ROUTING_KEY;
import static com.company.model.constant.RabbitConstant.STOCK_FAILED_QUEUE;
import static com.company.model.constant.RabbitConstant.STOCK_FAILED_ROUTING_KEY;

@Configuration
public class RabbitMQConfig {

    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Bean
    public Queue stockFailedQueue() {
        return QueueBuilder.durable(STOCK_FAILED_QUEUE)
                .withArgument(X_DEAD_LETTER_EXCHANGE, ORDER_EXCHANGE + ".dlx")
                .withArgument(X_DEAD_LETTER_ROUTING_KEY, STOCK_FAILED_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue stockFailedDLQ() {
        return QueueBuilder.durable(STOCK_FAILED_QUEUE + ".dlq").build();
    }

    @Bean
    public Queue orderPaymentFailedQueue() {
        return QueueBuilder.durable(ORDER_PAYMENT_FAILED_QUEUE)
                .withArgument(X_DEAD_LETTER_EXCHANGE, ORDER_EXCHANGE + ".dlx")
                .withArgument(X_DEAD_LETTER_ROUTING_KEY , ORDER_PAYMENT_FAILED_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue orderPaymentFailedDLQ() {
        return QueueBuilder.durable(ORDER_PAYMENT_FAILED_QUEUE + ".dlq").build();
    }


    @Bean
    public Queue paymentSuccessQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_QUEUE)
                .withArgument(X_DEAD_LETTER_EXCHANGE, ORDER_EXCHANGE + ".dlx")
                .withArgument(X_DEAD_LETTER_ROUTING_KEY , PAYMENT_SUCCESS_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue paymentSuccessDLQ() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_QUEUE + ".dlq").build();
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
    public Binding bindingStockFailedQueue(Queue stockFailedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(stockFailedQueue)
                .to(orderExchange)
                .with(STOCK_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingStockFailedDLQ(Queue stockFailedDLQ, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(stockFailedDLQ)
                .to(deadLetterExchange)
                .with(STOCK_FAILED_ROUTING_KEY + ".dlq");
    }

    @Bean
    public Binding bindingOrderPaymentFailedQueue(Queue orderPaymentFailedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderPaymentFailedQueue)
                .to(orderExchange)
                .with(ORDER_PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingOrderPaymentFailedDLQ(Queue orderPaymentFailedDLQ, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(orderPaymentFailedDLQ)
                .to(deadLetterExchange)
                .with(ORDER_PAYMENT_FAILED_ROUTING_KEY + ".dlq");
    }

    @Bean
    public Binding bindingPaymentSuccessQueue(Queue paymentSuccessQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(paymentSuccessQueue)
                .to(orderExchange)
                .with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding bindingPaymentSuccessDLQ(Queue paymentSuccessDLQ, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(paymentSuccessDLQ)
                .to(deadLetterExchange)
                .with(PAYMENT_SUCCESS_ROUTING_KEY + ".dlq");
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
