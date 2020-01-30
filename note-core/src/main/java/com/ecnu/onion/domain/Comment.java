package com.ecnu.onion.domain;

import lombok.Data;

/**
 * @author onion
 * @date 2020/1/30 -9:23 上午
 */
@Data
public class Comment {
    private String email;
    private String username;
    private String comment;
    private String replyTo;
}
