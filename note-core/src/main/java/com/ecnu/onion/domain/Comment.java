package com.ecnu.onion.domain;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author onion
 * @date 2020/1/30 -9:23 上午
 */
@Data
public class Comment {
    private String commentId;
    private String replyEmail;
    private String content;
    private String parentCommentId;
    private LocalDate date;
}
