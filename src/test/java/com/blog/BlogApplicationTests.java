package com.blog;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.search.model.blogDocument;
import com.blog.search.repository.blogRepository;
import com.blog.service.ITBlogService;
import com.blog.service.searchService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@SpringBootTest
class BlogApplicationTests {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ITBlogService blogService;
    @Autowired
    searchService searchService;
    @Autowired
    blogRepository blogRepository;
    //保存文档
    @Test
    void contextLoads() throws IOException {
        blogDocument blog = new blogDocument(1L,"aaa","aaa",new Date(),1,"true","aa","aa","aa","aa");
        //创建请求
        IndexRequest request = new IndexRequest("blog");
        //设置请求规则
        request.id("1");
        request.source(JSON.toJSONString(blog), XContentType.JSON);
        //客户端发送请求
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(index.toString());
        System.out.println(index.status());
    }
    //删除文档
    @Test
    void deleteDocument() throws IOException {
        DeleteRequest delete = new DeleteRequest("blog","1");
        DeleteResponse delete1 = restHighLevelClient.delete(delete, RequestOptions.DEFAULT);
        System.out.println(delete1.status());
        System.out.println(delete1);
    }
    //把mysql中的数据同步到es中
    @Test
    void test(){
        int size = 100000;
        Page page = new Page();
        page.setSize(size);
        int total = 0;
        for (int i = 0; i < 100; i++) {
            page.setCurrent(i);
            IPage<blogDocument> page1 = blogService.findByAllAddES(page);
            int num = searchService.initEsData(page1.getRecords());
            total += num;
            if (page1.getRecords().size() < size)
                break;
        }
        System.out.println("ES同步成功，共:"+total+"条");


    }
    //搜索
    @Test
    void test1(){
        Page page = new Page(0,10);
        IPage<blogDocument> page1 = searchService.search(page,"shiro");
        System.out.println(page1.getRecords());
    }

    @Test
    void test2() throws IOException {
        Page page = new Page(2,2);
        searchService.search1(page,"张某某");
    }
    //删除
    @Test
    void test3() throws IOException {
        DeleteRequest delete = new DeleteRequest("user", "1");
        delete.timeout("1s");

        DeleteResponse delete1 = restHighLevelClient.delete(delete, RequestOptions.DEFAULT);
        System.out.println(delete1.status());
    }

}
