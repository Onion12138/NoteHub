package com.ecnu.onion.domain.mongo;

import com.ecnu.onion.domain.Title;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private String description;
    private Boolean authority;
    private String forkFrom;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String tag;
    private String keywords;
    private String titles;
    private Title levelTitle;
    private String summary;
    private String content;
    private Boolean valid;
//    private List<Comment> comments;

}
