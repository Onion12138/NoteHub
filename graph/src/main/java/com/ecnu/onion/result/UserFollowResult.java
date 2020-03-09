package com.ecnu.onion.result;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * @author onion
 * @date 2020/2/28 -11:21 上午
 */
@QueryResult
@Data
public class UserFollowResult {
    private String email;
    private String followDate;
}
