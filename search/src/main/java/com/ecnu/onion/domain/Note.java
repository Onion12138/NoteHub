package com.ecnu.onion.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @author onion
 * @date 2020/1/27 -9:48 上午
 */
@Document(indexName = "note")
@Data
public class Note {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String email;
    @Field(type = FieldType.Keyword)
    private String authorName;
    @Field(type = FieldType.Date, index = false)
    private LocalDateTime createTime;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String summary;
    @Field(type = FieldType.Text, analyzer = "whitespace")
    private String keywords;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text, analyzer = "whitespace")
    private String tags;
}
