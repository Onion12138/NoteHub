package com.ecnu.onion.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author onion
 * @date 2020/1/23 -10:40 上午
 */
@NodeEntity(label = "note")
@Data
@Builder
public class NoteInfo {
    @Id
    private String noteId;
    private String title;
    private String publishTime;
    private Boolean valid;
}
