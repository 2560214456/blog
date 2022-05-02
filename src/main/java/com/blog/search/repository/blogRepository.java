package com.blog.search.repository;

import com.blog.search.model.blogDocument;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface blogRepository extends ElasticsearchRepository<blogDocument,Long> {

}
