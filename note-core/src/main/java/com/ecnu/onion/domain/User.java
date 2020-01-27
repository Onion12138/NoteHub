package com.ecnu.onion.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
@Data
@Document(collection = "user")
public class User {
    @Id
    private String email;
    private String nickname;
    private String password;
    private boolean disabled;
    private String profileUrl;
    private LocalDateTime registerTime;
    private Set<String> interestedTags;
//    private LocalDateTime LastLoginTime;
//    private List LastIp;
//    private Integer downloads;
//    private Integer collects;
//    private Integer publishes;
//    private Integer role;
//    private Set<String> publishNoteId;
//    private Set<String> followNoteId;
//    private Set<String> downloadNoteId;



}
