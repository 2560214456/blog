package com.blog.rabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class rabbitMQMessage implements Serializable {

    public static final String CREATE_UPDATE = "create_update";//更新
    public static final String REMOVE = "remove";//删除
    //博客id
    private Long id;
    //操作
    private String type;
}
