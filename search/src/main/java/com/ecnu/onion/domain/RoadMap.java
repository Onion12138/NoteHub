package com.ecnu.onion.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author onion
 * @date 2020/3/9 -11:11 下午
 */
@Data
@Document(indexName = "note", shards = 1, replicas = 0)
public class RoadMap {
    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;
    @Field(type = FieldType.Object, index = false)
    private MindMap mindMap;

}
