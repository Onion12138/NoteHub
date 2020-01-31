package com.ecnu.onion.domain.mongo;

import com.ecnu.onion.domain.Comment;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author onion
 * @date 2020/1/27 -8:30 上午
 */
@Data
@Builder
@Document(collection = "note")
public class Note implements Serializable {
    @Id
    private String id;
    private String authorEmail;
    private String authorName;
    private List<String> title;
    private Boolean authority;
    private String forkFrom;
    private List<LocalDateTime> createTime;
    private List<String> keywords;
    private List<String> languages;
    private List<String> levelTitles;
    private List<String> summary;
    private Integer stars;
    private Integer views;
    private Integer hates;
    private Integer forks;
    private Integer collects;
    private Integer version;
    private List<String> content;
    private Boolean valid;
    private List<Comment> comments;

}
