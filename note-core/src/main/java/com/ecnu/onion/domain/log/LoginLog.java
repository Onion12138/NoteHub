package com.ecnu.onion.domain.log;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author onion
 * @date 2020/1/28 -12:02 下午
 */
@Data
@Builder
@Table(name = "login_log")
public class LoginLog {
    @Id
    private Long id;
    private String email;
    private String username;
    @Column(name = "login_time")
    private LocalDateTime loginTime;
}
