package com.ecnu.onion.vo;

import lombok.Data;

/**
 * @author onion
 * @date 2020/1/29 -10:31 上午
 */
@Data
public class NoteVO {
    private String authorEmail;
    private String authorName;
    private String title;
    private String content;
    private Integer types;
}
