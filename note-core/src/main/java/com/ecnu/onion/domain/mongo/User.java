package com.ecnu.onion.domain.mongo;

import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
@Data
@Document(collection = "user")
@Builder
public class User {
    @Id
    private String email;
    private String username;
    private String password;
    private Boolean disabled;
    private String profileUrl;
    private LocalDate registerTime;
    private Set<String> interestedTags;
    private String activeCode;
    private String salt;
    private Boolean activated;
    private Set<CollectNote> collectNotes;
    private List<MindMap> collectIndexes;
}
