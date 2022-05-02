package com.blog.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
public class rabbitmqConfig implements Serializable {
    //交换机
    public static final String BLOG_EXCHANGE_NAME = "blog.exchange.name";
    //队列
    public static final String BLOG_QUEUE_NAME = "blog.queue.name";
    //路由Key
    public static final String ROUTINGKEY_KEY = "routingkey.key";
    //创建交换机
    @Bean
    public DirectExchange blogExchange(){
        return new DirectExchange(BLOG_EXCHANGE_NAME);
    }
    //创建队列
    @Bean
    public Queue blogQueue(){
        return new Queue(BLOG_QUEUE_NAME);
    }

    //队列通路由key绑定交换机
    @Bean
    public Binding queueBindingBlogExchangeName(Queue blogQueue,DirectExchange blogExchange){
        return BindingBuilder.bind(blogQueue).to(blogExchange).with(ROUTINGKEY_KEY);
    }
}
