package com.ecnu.onion.domain.search;

import lombok.Builder;
import lombok.Data;

/**
 * @author onion
 * @date 2020/1/29 -3:41 下午
 */
@Data
@Builder
public class NoteSearch {
    private String id;
    private String email;
    private String createTime;
    private String updateTime;
    private String summary;
    private String keywords;
    private String description;
    private String tag;
}
