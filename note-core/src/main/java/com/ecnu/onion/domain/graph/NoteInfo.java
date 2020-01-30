package com.ecnu.onion.domain.graph;

import lombok.Builder;
import lombok.Data;

/**
 * @author onion
 * @date 2020/1/29 -3:47 下午
 */
@Data
@Builder
public class NoteInfo {
    private String noteId;
    private String title;
    private String publishTime;
}
