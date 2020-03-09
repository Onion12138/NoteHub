package com.ecnu.onion.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author onion
 * @date 2020/1/23 -5:58 下午
 */
@NodeEntity(label = "group")
@Data
@Builder
public class Group {
    @Id
    @GeneratedValue
    private Long groupId;

    private String groupName;

    private String createTime;
}
