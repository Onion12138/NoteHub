package com.ecnu.onion.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author onion
 * @date 2020/2/29 -10:27 下午
 */
@Data
public class Title implements Serializable {
    private String label;
    private List<Title> children;
}
