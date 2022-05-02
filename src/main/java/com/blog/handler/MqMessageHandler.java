package com.blog.handler;

import com.blog.config.rabbitmqConfig;
import com.blog.rabbitMQ.rabbitMQMessage;
import com.blog.service.searchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//mq消费者
@Slf4j
@RabbitListener(queues = rabbitmqConfig.BLOG_QUEUE_NAME)
@Component
public class MqMessageHandler {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    searchService searchService;

    @RabbitHandler
    public void handler(rabbitMQMessage message){
        log.info("mq 收到一条消息：{}",message.toString());
        switch (message.getType()){
            case rabbitMQMessage.CREATE_UPDATE:
                searchService.sreateOrdateIndex(message);
                break;
            case rabbitMQMessage.REMOVE:
                searchService.removeIndex(message);
                break;
            default:
                log.error("没找到对应的消息类型，请注意！！！{}",message.toString());
                break;

        }
    }
}
