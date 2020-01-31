package com.ecnu.onion.controller;

import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.AnalysisVO;
import com.ecnu.onion.vo.BaseResponseVO;
import com.ecnu.onion.vo.NoteResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
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
    @PostMapping("/publish")
    public BaseResponseVO publishNote(@RequestParam Map<String, String> map) {
        log.info("{}",map.get("content"));
        MultiValueMap<String,Object> re = new LinkedMultiValueMap<>();
        re.put("content", Collections.singletonList(map.get("content")));
        AnalysisVO analyze = restTemplate.postForObject("http://localhost:5000/analyze", re, AnalysisVO.class);
        log.info("{}",analyze);
        String id = noteService.publishNote(analyze, map);
        return BaseResponseVO.success(id);
    }
    @PostMapping("/update")
    public BaseResponseVO updateNote(@RequestParam Map<String, String> map) {
        MultiValueMap<String,Object> re = new LinkedMultiValueMap<>();
        re.put("content", Collections.singletonList(map.get("content")));
        AnalysisVO analyze = restTemplate.postForObject("http://localhost:5000/analyze", re, AnalysisVO.class);
        String id = noteService.updateNote(analyze, map);
        return BaseResponseVO.success(id);
    }

    @GetMapping("/delete")
    public BaseResponseVO deleteNote(@RequestParam String noteId) {
        noteService.deleteNote(noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/findOne")
    public BaseResponseVO findNote(@RequestParam String noteId) {
        NoteResponseVO responseVO = noteService.findOneNote(noteId);
        return BaseResponseVO.success(responseVO);
    }
}
