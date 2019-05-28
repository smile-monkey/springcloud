package com.technology.demo;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@RestController
public class TestMQProvider {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/testDelayMQ")
    public void test() {
        // 模拟延迟消费

        // 先放入5个时间较长的消息到延迟队列
        for (int i = 0; i < 1; i++) {
            MessagePostProcessor processor = (message) -> {
                message.getMessageProperties().setExpiration(2 * 60 * 1000 + "");
                return message;
            };
            Order dto = new Order("SZ00" + i, new Date());
            rabbitTemplate.convertAndSend(MQConfig.DELAY_EXCHENGE, MQConfig.DELAY_QUEUE, dto, processor);
            System.out.println("消息" + dto.getOrderNo() + "加入队列,当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
        }

        // 再放入3个时间较短的消息到延迟队列
        for (int i = 0; i < 3; i++) {
            MessagePostProcessor processor = (message) -> {
                message.getMessageProperties().setExpiration(1000 + "");
                return message;
            };
            Order dto = new Order("SH00" + i, new Date());
            rabbitTemplate.convertAndSend(MQConfig.DELAY_EXCHENGE, MQConfig.DELAY_QUEUE, dto, processor);
            System.out.println("消息" + dto.getOrderNo() + "加入队列,当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
        }

        // 按道理应该是后面加入的消息先超时!!! 延迟消息的排列规则应该是先进先出
    }

    private static DelayQueue<Message> queue = new DelayQueue<>();

    @RequestMapping("/testJVMDelayQueue")
    public void testJVMDelayQueue() throws InterruptedException {
        Message m1 = new Message(1, " world", 20000);
        queue.add(m1);
        Message m2 = new Message(2, " hello", 5000);
        queue.add(m2);
        Message m3 = new Message(3, " !!!", 10000);
        queue.add(m3);
        int amount = queue.size();
        while (amount > 0) {
            Message take = queue.take();
            System.out.println("消费消息id：" + take.getId() + " 消息体：" + take.getBody());
            amount--;
            Thread.sleep(1000);
        }
        System.out.println("线程执行完毕!");
    }

    @RequestMapping("/testDelayMQPlugin")
    public void testDelayQueuePlugin() {
        for (int i = 0; i < 1; i++) {
            MessagePostProcessor processor = (message) -> {
                message.getMessageProperties().setDelay(5000);
                return message;
            };
            Order dto = new Order("SZ00" + i, new Date());
            rabbitTemplate.convertAndSend(MQConfig.DELAY_PLUGIN_EXCHENGE, MQConfig.DELAY_PLUGIN_MESSAGE_QUEUE, dto, processor);
            System.out.println("消息" + dto.getOrderNo() + "加入队列,当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
        }


        // 再放入3个时间较短的消息到延迟队列
        for (int i = 0; i < 3; i++) {
            MessagePostProcessor processor = (message) -> {
                message.getMessageProperties().setExpiration(1000 + "");
                return message;
            };
            Order dto = new Order("SH00" + i, new Date());
            rabbitTemplate.convertAndSend(MQConfig.DELAY_PLUGIN_EXCHENGE, MQConfig.DELAY_PLUGIN_MESSAGE_QUEUE, dto, processor);
            System.out.println("消息" + dto.getOrderNo() + "加入队列,当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").format(new Date()));
        }
    }
}

class Message implements Delayed {
    private int id;
    private String body; // 消息内容
    private long excuteTime;// 延迟时长，这个是必须的属性因为要按照这个判断延时时长。

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public long getExcuteTime() {
        return excuteTime;
    }

    public Message(int id, String body, long delayTime) {
        this.id = id;
        this.body = body;
        this.excuteTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    public Message(int id, String body, Date excuteTime) {
        this.id = id;
        this.body = body;
        this.excuteTime = excuteTime.getTime();
    }

    // 自定义实现比较方法返回 1 0 -1三个参数      排序就是等第一个执行完了，然后看第二个有没有超时，没有继续等超时；
    @Override
    public int compareTo(Delayed delayed) {
        Message msg = (Message) delayed;
//        return Integer.valueOf(this.id) > Integer.valueOf(msg.id) ? 1 : (Integer.valueOf(this.id) < Integer.valueOf(msg.id) ? -1 : 0);
        if(this.excuteTime > msg.excuteTime){
            return 1;
        }else if(this.excuteTime == msg.excuteTime){
            return 0;
        }else{
            return -1;
        }
    }

    // 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.excuteTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }
}
