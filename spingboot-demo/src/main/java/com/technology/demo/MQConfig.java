package com.technology.demo;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
    public static final String DELAY_EXCHENGE = "delayExchange";
    public static final String DELAY_QUEUE = "delayQueue";

    public static final String DEST_EXCHENGE = "destExchange";
    public static final String DEST_QUEUE = "destQueue";

    /**
     * 订单延迟消费 延迟队列配置
     */
    @Bean
    public DirectExchange commonDelayExchange() {
        return new DirectExchange(DELAY_EXCHENGE, true, false);
    }

    @Bean
    public Queue orderSysOvertimeDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEST_EXCHENGE);// 出现dead letter之后，重新发送到指定exchange
        arguments.put("x-dead-letter-routing-key", DEST_QUEUE);// 出现dead letter之后，重新按照指定的routing-key发送
        Queue queue = new Queue(DELAY_QUEUE, true, false, false, arguments);
        return queue;
    }

    @Bean
    @Resource
    public Binding commonDelayExchangeBindingOrderSysOvertimeDelayQueue(DirectExchange commonDelayExchange, Queue orderSysOvertimeDelayQueue) {
        return BindingBuilder.bind(orderSysOvertimeDelayQueue).to(commonDelayExchange).withQueueName();
    }

    /**
     * 订单延迟消费 任务队列配置
     */
    @Bean
    public DirectExchange commonTaskExchange() {
        return new DirectExchange(DEST_EXCHENGE, true, false);
    }

    @Bean
    public Queue orderSysOvertimeTaskQueue() {
        return new Queue(DEST_QUEUE, true, false, false);
    }

    @Bean
    @Resource
    public Binding commonTaskExchangeBindingOrderSysOvertimeTaskQueue(DirectExchange commonTaskExchange, Queue orderSysOvertimeTaskQueue) {
        return BindingBuilder.bind(orderSysOvertimeTaskQueue).to(commonTaskExchange).withQueueName();
    }




    public static final String DELAY_PLUGIN_EXCHENGE = "delayPluginExchange";
    public static final String DELAY_PLUGIN_MESSAGE_QUEUE = "delayPluginMessageQueue";

    @Bean
    public Exchange delayPluginExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return ExchangeBuilder.directExchange(DELAY_PLUGIN_EXCHENGE).durable(true).withArguments(args).build();
    }
    @Bean
    public Queue delayPluginMessageQueue() {
        return new Queue(DELAY_PLUGIN_MESSAGE_QUEUE, true, false, false);
    }
    @Bean
    @Resource
    public Binding delayPluginExchangeBindingDelayPluginMessageQueue(DirectExchange delayPluginExchange, Queue delayPluginMessageQueue) {
        return BindingBuilder.bind(delayPluginMessageQueue).to(delayPluginExchange).withQueueName();
    }

}
