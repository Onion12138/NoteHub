package com.ecnu.onion.domain.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author onion
 * @date 2020/2/28 -5:31 下午
 */
@Document(collection = "tag")
@Data
public class Tag {
    @Id
    private Integer id;
    private String value;
    private String label;
    private List<Tag> children;

}
