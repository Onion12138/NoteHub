package com.ecnu.onion.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author onion
 * @date 2020/1/27 -9:48 上午
 */
@Data
public class Note {
    private String id;
    private String email;
    private String authorName;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate createTime;
    private String summary;
    private String keywords;
    private String title;
    private String tags;
}
