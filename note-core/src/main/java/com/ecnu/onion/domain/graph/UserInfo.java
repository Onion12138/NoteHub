package com.ecnu.onion.domain.graph;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author onion
 * @date 2020/2/29 -9:50 上午
 */
@Data
@Builder
public class UserInfo implements Serializable {
    private String email;
    private String registerTime;
}
