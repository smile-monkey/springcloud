package com.technology.demo;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RabbitListener(queues = MQConfig.DELAY_PLUGIN_MESSAGE_QUEUE)
public class TaskPluginQueueConsumer {
    @RabbitHandler
    public void process(Order dto) throws InterruptedException {
        System.out.println("插件任务队列接受到消息:" + dto.toString() + ";消息发送时间:"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(dto.getCreateTime()) +"当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
        // 模拟处理任务
        Thread.sleep(500);
        System.out.println("插件"+dto.getOrderNo() + "任务处理完毕");
    }
}
