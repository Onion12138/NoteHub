package com.ecnu.onion.vo;

import com.ecnu.onion.domain.Comment;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author onion
 * @date 2020/1/30 -12:13 下午
 */
@Data
public class NoteResponseVO implements Serializable {
    private String id;
    private String authorEmail;
    private String authorName;
    private String title;
    private Boolean authority;
    private String forkFrom;
    private LocalDateTime createTime;
    private Integer stars;
    private Integer views;
    private Integer hates;
    private Integer forks;
    private Integer collects;
    private String content;
    private Boolean valid;
    private List<Comment> comments;
}
