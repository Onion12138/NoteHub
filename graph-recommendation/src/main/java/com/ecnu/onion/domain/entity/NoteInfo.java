package com.ecnu.onion.domain.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.io.Serializable;

/**
 * @author onion
 * @date 2020/1/23 -10:40 上午
 */
@NodeEntity(label = "note")
@Data
public class NoteInfo implements Serializable {
    @Id
    private String noteId;
    private String publishTime;
}
