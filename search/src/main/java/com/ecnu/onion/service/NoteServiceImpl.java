package com.ecnu.onion.service;

import com.alibaba.fastjson.JSON;
import com.ecnu.onion.constant.MQConstant;
import com.ecnu.onion.domain.Note;
import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -11:12 上午
 */
@Slf4j
@Service
@RabbitListener(bindings = {
        @QueueBinding(value = @Queue(value = MQConstant.SEARCH_NOTE_QUEUE),
                exchange = @Exchange(value = MQConstant.EXCHANGE, type = "topic"))
})
public class NoteServiceImpl implements NoteService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private static final String INDEX = "note";
    private void verifyPage(int page) {
        if (page > 10 || page <= 0) {
            throw new CommonServiceException(ServiceEnum.PAGE_OVERFLOW);
        }
    }
    private List<Note> performSearchRequest(SearchSourceBuilder sourceBuilder) {
        SearchRequest request = new SearchRequest(INDEX);
        request.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<Note> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), Note.class));
                log.info("obj:{}", JSON.parseObject(hit.getSourceAsString()));
            }
            return res;
        } catch (Exception e) {
            throw new CommonServiceException(-1, e.getMessage());
        }
    }
    @Override
    public List<Note> findByAuthorEmail(String email, int page) {
        verifyPage(page);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.matchQuery("email", email));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder).from((page-1)*10).size(10).sort("createTime", SortOrder.DESC);
        return performSearchRequest(sourceBuilder);
    }

    @Override
    public List<Note> findByAuthorName(String username, int page) {
        verifyPage(page);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.matchQuery("authorName", username));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder).from((page-1)*10).size(10).sort("createTime", SortOrder.DESC);
        return performSearchRequest(sourceBuilder);
    }

    @Override
    public List<Note> findByKeyword(String keyword, int page) {
        verifyPage(page);
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword);
        queryBuilder.field("title", 5.0f);
        queryBuilder.field("summary", 1.0f);
        queryBuilder.field("keywords", 0.5f);
        queryBuilder.type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder).from((page-1)*10).size(10);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").field("keywords").field("summary");
        sourceBuilder.highlighter(highlightBuilder);
        SearchRequest request = new SearchRequest(INDEX);
        request.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<Note> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                Note note = JSON.parseObject(hit.getSourceAsString(), Note.class);
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields.containsKey("keywords")) {
                    note.setKeywords(highlightFields.get("keywords").getFragments()[0].toString());
                }
                if (highlightFields.containsKey("summary")) {
                    note.setSummary(highlightFields.get("summary").getFragments()[0].toString());
                }
                if (highlightFields.containsKey("title")) {
                    note.setTitle(highlightFields.get("title").getFragments()[0].toString());
                }
                res.add(note);
            }
            return res;
        } catch (Exception e) {
            throw new CommonServiceException(-1, e.getMessage());
        }
    }

    @Override
    public List<Note> findByTag(String tag, int page) {
        verifyPage(page);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.matchQuery("tags", tag));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder).from((page-1)*10).size(10).sort("createTime", SortOrder.DESC);
        return performSearchRequest(sourceBuilder);
    }

    @Override
    public void saveNote(Note note) {
        IndexRequest request = new IndexRequest(INDEX);
        request.id(note.getId());
        request.source(JSON.toJSONString(note), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonServiceException(-1, e.getMessage());
        }
    }

    @RabbitHandler
    private void synchronizeNote(String message) {
        Note note = JSON.parseObject(message, Note.class);
        IndexRequest request = new IndexRequest(INDEX);
        request.id(note.getId());
        request.source(JSON.toJSONString(note), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonServiceException(-1, e.getMessage());
        }
    }
}
