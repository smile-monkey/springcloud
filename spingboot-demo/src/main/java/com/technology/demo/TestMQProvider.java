package com.technology.demo;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
public class TestMQProvider {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/testMQ")
    public void test() {
        // 模拟延迟消费

        // 先放入5个时间较长的消息到延迟队列
        for (int i = 0; i < 5; i++) {
            MessagePostProcessor processor = (message) -> {
                message.getMessageProperties().setExpiration(2 * 60 * 1000 + "");
                return message;
            };
            Order dto = new Order("SZ00" + i, new Date());
            rabbitTemplate.convertAndSend(MQConfig.DELAY_EXCHENGE, MQConfig.DELAY_QUEUE, dto, processor);
            System.out.println("消息" + dto.getOrderNo() + "加入队列,当前时间:" + System.currentTimeMillis());
        }

        // 再放入3个时间较短的消息到延迟队列
        for (int i = 0; i < 3; i++) {
            MessagePostProcessor processor = (message) -> {
                message.getMessageProperties().setExpiration(1000 + "");
                return message;
            };
            Order dto = new Order("SH00" + i, new Date());
            rabbitTemplate.convertAndSend(MQConfig.DELAY_EXCHENGE, MQConfig.DELAY_QUEUE, dto, processor);
            System.out.println("消息" + dto.getOrderNo() + "加入队列,当前时间:" + System.currentTimeMillis());
        }

        // 按道理应该是后面加入的消息先超时!!!
    }
}
