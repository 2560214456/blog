package com.blog.entity;

import lombok.*;

//封装查询条件
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionQuery {
    private String title;
    private Long TypeId;
    private boolean recommend;
}
