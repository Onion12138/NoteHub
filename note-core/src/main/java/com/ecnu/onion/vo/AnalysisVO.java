package com.ecnu.onion.vo;

import com.ecnu.onion.domain.Title;
import lombok.Data;

/**
 * @author onion
 * @date 2020/1/29 -3:13 下午
 */
@Data
public class AnalysisVO {
    private int code;
    private String summary;
    private String keywords;
    private Title TitleTree;
}
