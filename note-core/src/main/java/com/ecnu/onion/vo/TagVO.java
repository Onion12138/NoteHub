package com.ecnu.onion.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author onion
 * @date 2020/3/6 -9:01 下午
 */
@Data
public class TagVO implements Serializable {
    private String label;
    private String value;
    private List<TagVO> children;
}
