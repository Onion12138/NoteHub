package com.ecnu.onion.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author onion
 * @date 2020/1/23 -10:40 上午
 */
@NodeEntity(label = "user")
@Data
@Builder
public class UserInfo {
    @Id
    private String email;
    private String username;
}
