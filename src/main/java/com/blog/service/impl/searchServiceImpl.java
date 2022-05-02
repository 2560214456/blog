package com.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.rabbitMQ.rabbitMQMessage;
import com.blog.search.model.blogDocument;
import com.blog.search.repository.blogRepository;
import com.blog.service.ITBlogService;
import com.blog.service.searchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.PageAdapter;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class searchServiceImpl implements searchService {
    @Autowired
    blogRepository blogRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    ITBlogService blogService;

    @Autowired
    ElasticsearchRepository repository;

    /**
     * 同步数据到es中
     * @param records
     * @return
     */
    @Override
    public int initEsData(List<blogDocument> records) {
        if (records == null || records.isEmpty())
            return 0;
        blogRepository.saveAll(records);
        return records.size();
    }

    @Override
    public IPage<blogDocument> search(Page page, String keyword) {
        //jpa中的page的分页是从0开始的，MyBatisPlus的Page是从1开始。
        //所以这里获得的当前页需要减一
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
        //把MyBatisPlus的Page转换为Jap中的Page
        PageRequest pageable = PageRequest.of(current.intValue(), size.intValue());
        //多字段，分词器查询
        MultiMatchQueryBuilder multiMatchQueryBuilder =
                QueryBuilders.multiMatchQuery(keyword, "title", "nickname", "name");
        //执行查询
        org.springframework.data.domain.Page<blogDocument> documentPage =
                blogRepository.search(multiMatchQueryBuilder, pageable);

        // 结果信息 jap的Page转换为MyBatisPlus的Page
        //ocuments.getTotalElements() 总记录数
        IPage pageData = new Page(page.getCurrent(), page.getSize(), documentPage.getTotalElements());
        pageData.setRecords(documentPage.getContent());
        return pageData;
    }

    @Override
    public IPage<blogDocument> search1(Page page, String keyword) {
        Long current = page.getCurrent()-1;
        Long size = page.getSize();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", keyword))
                .should(QueryBuilders.matchQuery("nickname", keyword))
                .should(QueryBuilders.matchQuery("name", keyword));


        // 创建高亮查询
        NativeSearchQueryBuilder nativeSearchQuery = new NativeSearchQueryBuilder();
        nativeSearchQuery.withQuery(boolQueryBuilder);
        nativeSearchQuery.withHighlightFields(new HighlightBuilder.Field("title"),
                new HighlightBuilder.Field("nickname"),
                new HighlightBuilder.Field("name"));
        nativeSearchQuery.withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"));
        System.out.println();

        // 设置分页,页码要减1
        PageRequest of = PageRequest.of(current.intValue(), size.intValue());
        org.springframework.data.domain.Page<blogDocument> search =
                blogRepository.search(boolQueryBuilder, of);
        IPage pageData = new Page(page.getCurrent(), page.getSize(), search.getTotalElements());
        pageData.setRecords(search.getContent());
        return pageData;
    }

    @Override
    public void deleteById(Long id) throws IOException {
        DeleteRequest delete = new DeleteRequest("blog",id.toString());
        DeleteResponse delete1 = restHighLevelClient.delete(delete, RequestOptions.DEFAULT);
    }

    /**
     * 添加或修改
     * @param message
     */
    @Override
    public void sreateOrdateIndex(rabbitMQMessage message) {
        Long blogId = message.getId();
        blogDocument blogDocument = blogService.getBlogDocumentBuId(blogId);
        //如果es中没有就是添加，有就是修改
        blogRepository.save(blogDocument);
        log.info("es 索引更新成功 ---->  {}",blogDocument.toString());
    }

    /**
     * 删除
     * @param message
     */
    @Override
    public void removeIndex(rabbitMQMessage message) {
        Long blogId = message.getId();
        blogRepository.deleteById(blogId);
        log.info("es 删除成功 删除的博客id----->{}",blogId);
    }


}
