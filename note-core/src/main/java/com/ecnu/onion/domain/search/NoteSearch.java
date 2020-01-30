package com.ecnu.onion.domain.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author onion
 * @date 2020/1/29 -3:41 下午
 */
@Data
@Builder
public class NoteSearch {
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
