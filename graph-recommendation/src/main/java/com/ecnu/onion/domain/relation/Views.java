package com.ecnu.onion.domain.relation;

import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.domain.entity.UserInfo;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * @author onion
 * @date 2020/1/23 -10:41 上午
 */
@RelationshipEntity(type = "view")
@Data
@Builder
public class Views {
    @Id
    @GeneratedValue
    private Long id;
    @StartNode
    private UserInfo start;
    @EndNode
    private NoteInfo end;
    private String viewDate;
}
