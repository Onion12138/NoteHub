package com.ecnu.onion.controller;

import com.ecnu.onion.domain.Comment;
import com.ecnu.onion.domain.mongo.Note;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.utils.AuthUtil;
import com.ecnu.onion.vo.AnalysisVO;
import com.ecnu.onion.vo.BaseResponseVO;
import com.ecnu.onion.vo.NoteResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/starOrHate")
    public BaseResponseVO starOrHate(@RequestParam String type, @RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        noteService.starOrHate(type, noteId, email);
        return BaseResponseVO.success();
    }
    @PostMapping("/comment")
    public BaseResponseVO comment(@RequestBody Comment comment) {
        String commentId = noteService.comment(comment);
        return BaseResponseVO.success(commentId);
    }
    @GetMapping("/deleteComment")
    public BaseResponseVO deleteComment(@RequestParam String noteId, @RequestParam String commentId) {
        noteService.deleteComment(noteId, commentId);
        return BaseResponseVO.success();
    }
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
        int version = noteService.updateNote(analyze, map);
        return BaseResponseVO.success(version);
    }
    @PostMapping("/updateOverride")
    public BaseResponseVO updateOverride(@RequestParam Map<String, String> map) {
        MultiValueMap<String,Object> re = new LinkedMultiValueMap<>();
        re.put("content", Collections.singletonList(map.get("content")));
        AnalysisVO analyze = restTemplate.postForObject("http://localhost:5000/analyze", re, AnalysisVO.class);
        noteService.updateOverride(analyze, map);
        return BaseResponseVO.success();
    }

    @GetMapping("/historyVersion")
    public BaseResponseVO historyVersion(@RequestParam String noteId) {
        Note note = noteService.historyVersion(noteId);
        return BaseResponseVO.success(note);
    }

    @GetMapping("/rollback")
    public BaseResponseVO rollback(@RequestParam String noteId, @RequestParam Integer version) {
        noteService.rollback(noteId, version);
        return BaseResponseVO.success();
    }

    @PostMapping("/changeAuthority")
    public BaseResponseVO changeAuthority(@RequestParam String noteId, @RequestParam String authority) {
        noteService.changeAuthority(noteId, authority);
        return BaseResponseVO.success();
    }

    @PostMapping("/delete")
    public BaseResponseVO deleteNote(@RequestParam String noteId) {
        noteService.deleteNote(noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/findOne")
    public BaseResponseVO findNote(@RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        NoteResponseVO responseVO = noteService.findOneNote(email, noteId);
        return BaseResponseVO.success(responseVO);
    }

    @PostMapping("/uploadPicture")
    public BaseResponseVO uploadPicture(@RequestParam String noteId, @RequestParam MultipartFile file) {
        String uri = noteService.uploadPicture(noteId, file);
        return BaseResponseVO.success(uri);
    }

}
