package com.ecnu.onion.result;

import com.ecnu.onion.domain.entity.Group;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/24 -9:21 上午
 */

@QueryResult
@Data
public class GroupInfoResult {
    private String owner;
    private List<String> partner;
    private Group group;
}
