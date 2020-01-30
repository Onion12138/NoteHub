package com.ecnu.onion.vo;

import lombok.Data;

import java.util.Set;

/**
 * @author onion
 * @date 2020/1/29 -3:13 下午
 */
@Data
public class AnalysisVO {
    private Set<String> summary;
    private Set<String> keywords;
    private Set<String> languages;
    private Set<String> levelTitles;
}
