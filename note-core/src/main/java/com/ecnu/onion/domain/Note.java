package com.ecnu.onion.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author onion
 * @date 2020/1/27 -8:30 上午
 */
@Data
@Document(collection = "note")
public class Note implements Serializable {
    @Id
    private String id;
    private String authorEmail;
    private String authorName;
    private String title;
    private Boolean authority;
    private LocalDate createTime;
    private LocalDate updateTime;
    private Set<String> keywords;
    private Set<String> languages;
    private Set<String> levelTitles;
    private String summary;
    private Integer stars;
    private Integer views;
    private Integer hates;
    private Integer downloads;
    private Integer follows;
    private String content;
    private Integer types;
}
