package com.blog.search.model;

import com.sun.javafx.beans.IDProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "blog",createIndex = true)
public class blogDocument {
    //博客信息
    @Id
    private Long id; // 博客id
    @Field(type = FieldType.Text,searchAnalyzer = "ik_smart",analyzer = "ik_max_word")
    private String title; //博客标题
    @Field(type = FieldType.Text,searchAnalyzer = "ik_smart",analyzer = "ik_max_word")
    private String description;//博客描述信息

    @Field(type = FieldType.Keyword)
    private Date createTime;//创建时间
    @Field(type = FieldType.Keyword)
    private Integer views;//浏览量
    @Field(type = FieldType.Keyword)
    private String flag;//标记
    @Field(type = FieldType.Keyword)
    private String firstPicture;//博客首图地址

    //表示是个关键词，不会被ik分词器处理
    @Field(type = FieldType.Keyword)
    private String nickname;//用户名
    @Field(type = FieldType.Keyword)
    private String avatar;//用户头像

    //表示是个关键词，不会被ik分词器处理
    @Field(type = FieldType.Keyword)
    private String name;//标签名

/*    //表示是个关键词，不会被ik分词器处理
    @Field(type = FieldType.Keyword)
    private String tagName; //分类名*/
}
