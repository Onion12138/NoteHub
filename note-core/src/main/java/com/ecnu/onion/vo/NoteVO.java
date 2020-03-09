package com.ecnu.onion.vo;

import com.ecnu.onion.domain.Title;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author onion
 * @date 2020/3/7 -11:49 上午
 */
@Data
public class NoteVO {
    private String id;
    private String description;
    private Boolean authority;
    private String forkFrom;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String tag;
    private String keywords;
    private Title levelTitle;
    private String summary;
    private String content;
    private UserVO user;
}
