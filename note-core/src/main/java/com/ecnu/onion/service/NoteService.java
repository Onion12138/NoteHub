package com.ecnu.onion.service;

import com.ecnu.onion.vo.AnalysisVO;

import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
public interface NoteService {

    void publishNote(AnalysisVO analyze, Map<String, String> map);
}
