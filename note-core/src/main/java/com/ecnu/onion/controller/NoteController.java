package com.ecnu.onion.controller;

import com.ecnu.onion.api.AnalyzeAPI;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.AnalysisVO;
import com.ecnu.onion.vo.BaseResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -8:34 上午
 */
@RestController
@Slf4j
@RequestMapping("/note")
public class NoteController {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private NoteService noteService;
    @Autowired
    private AnalyzeAPI analyzeAPI;
    @PostMapping("/publish")
    public BaseResponseVO publishNote(@RequestParam Map<String, String> map) {
        log.info("{}",map.get("content"));
        MultiValueMap<String,Object> re = new LinkedMultiValueMap<>();
        re.put("content", Collections.singletonList(map.get("content")));
        AnalysisVO analyze = restTemplate.postForObject("http://localhost:5000/analyze", re, AnalysisVO.class);
        log.info("{}",analyze);
        noteService.publishNote(analyze, map);
        return BaseResponseVO.success();
    }
}
