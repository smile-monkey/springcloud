package com.technology.demo;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RabbitListener(queues = MQConfig.DEST_QUEUE)
public class TaskQueueConsumer {
    @RabbitHandler
    public void process(Order dto) throws InterruptedException {
        System.out.println("任务队列接受到消息:" + dto.toString() + ";消息发送时间:"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(dto.getCreateTime()) +"当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
        // 模拟处理任务
        Thread.sleep(500);
        System.out.println(dto.getOrderNo() + "任务处理完毕");
    }
}
