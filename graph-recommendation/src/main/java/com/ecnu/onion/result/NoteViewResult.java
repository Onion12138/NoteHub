package com.ecnu.onion.result;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * @author onion
 * @date 2020/2/28 -11:32 上午
 */
@QueryResult
@Data
public class NoteViewResult {
    private String noteId;
    private String title;
    private String viewDate;
}
