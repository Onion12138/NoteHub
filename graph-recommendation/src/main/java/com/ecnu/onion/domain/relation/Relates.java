package com.ecnu.onion.domain.relation;

import com.ecnu.onion.domain.entity.NoteInfo;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * @author onion
 * @date 2020/1/23 -10:41 上午
 */
@RelationshipEntity(type = "relate")
@Data
@Builder
public class Relates {
    @Id
    @GeneratedValue
    private Long id;
    @StartNode
    private NoteInfo start;
    @EndNode
    private NoteInfo end;
    private long relatedTimes;
}
