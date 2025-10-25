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

@Configuration
public class RabbitMQConfig {

    public static final String STOCK_FAILED_QUEUE = "stock-failed-queue";
    public static final String ORDER_PAYMENT_FAILED_QUEUE = "order-payment-failed-queue";
    public static final String PAYMENT_SUCCESS_QUEUE = "payment-success-queue";
    public static final String DELIVERY_COMPLETED_QUEUE = "delivery-completed-queue";
    public static final String ORDER_EXCHANGE = "order-exchange";
    public static final String ORDER_ROUTING_KEY = "order.created";
    public static final String STOCK_FAILED_ROUTING_KEY = "stock.failed";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String ORDER_PAYMENT_FAILED_ROUTING_KEY = "order.payment.failed";
    public static final String ORDER_CONFIRMED_ROUTING_KEY = "order.confirmed";
    public static final String DELIVERY_COMPLETED_ROUTING_KEY = "order.delivered";

    @Bean
    public Queue stockFailedQueue() {
        return QueueBuilder.durable(STOCK_FAILED_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", STOCK_FAILED_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue stockFailedDLQ() {
        return QueueBuilder.durable(STOCK_FAILED_QUEUE + ".dlq").build();
    }

    @Bean
    public Queue orderPaymentFailedQueue() {
        return QueueBuilder.durable(ORDER_PAYMENT_FAILED_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", ORDER_PAYMENT_FAILED_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue orderPaymentFailedDLQ() {
        return QueueBuilder.durable(ORDER_PAYMENT_FAILED_QUEUE + ".dlq").build();
    }


    @Bean
    public Queue paymentSuccessQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", PAYMENT_SUCCESS_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue paymentSuccessDLQ() {
        return QueueBuilder.durable(PAYMENT_SUCCESS_QUEUE + ".dlq").build();
    }

    @Bean
    public Queue deliverCompletedQueue() {
        return QueueBuilder.durable(DELIVERY_COMPLETED_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", DELIVERY_COMPLETED_ROUTING_KEY + ".dlq")
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
