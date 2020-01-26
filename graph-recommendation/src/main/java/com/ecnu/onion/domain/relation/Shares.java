package com.ecnu.onion.domain.relation;

import com.ecnu.onion.domain.entity.Group;
import com.ecnu.onion.domain.entity.NoteInfo;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * @author onion
 * @date 2020/1/23 -5:55 下午
 */
@RelationshipEntity(type = "relate")
@Data
@Builder
public class Shares {
    @Id
    @GeneratedValue
    private Long id;
    @StartNode
    private Group group;
    @EndNode
    private NoteInfo noteInfo;
}
