package com.ecnu.onion.domain.log;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author onion
 * @date 2020/1/28 -12:01 下午
 */
@Data
@Builder
@Table(name = "search_log")
public class SearchLog {
    @Id
    private Long id;
    private String email;
    private String username;
    @Column(name = "search_date")
    private LocalDateTime search_date;
    private String content;
    private String api;
}
