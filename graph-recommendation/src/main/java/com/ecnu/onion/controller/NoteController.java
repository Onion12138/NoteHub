package com.ecnu.onion.controller;

import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:13 下午
 */
@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @GetMapping("/recommend")
    public BaseResponseVO recommend(@RequestParam String noteId) {
        List<NoteInfo> noteInfos = noteService.recommend(noteId);
        return BaseResponseVO.success(noteInfos);
    }

    @GetMapping("/updateNote")
    public BaseResponseVO updateNote(@RequestParam String oldNoteId,
                                     @RequestParam String newNoteId,
                                     @RequestParam String title) {
        noteService.updateNote(oldNoteId, newNoteId, title);
        return BaseResponseVO.success();
    }

    @GetMapping("/jumpToLatest")
    public BaseResponseVO jumpToLatest(@RequestParam String noteId) {
        String latest = noteService.jumpToLatest(noteId);
        return BaseResponseVO.success(latest);
    }

    @GetMapping("/historyVersion")
    public BaseResponseVO historyVersion(@RequestParam String noteId) {
        List<NoteInfo> notes = noteService.historyVersion(noteId);
        return BaseResponseVO.success(notes);
    }

    @GetMapping("/rollback")
    public BaseResponseVO rollback(@RequestParam String currentVersion,
                                   @RequestParam String rollbackVersion) {
        noteService.rollback(currentVersion, rollbackVersion);
        return BaseResponseVO.success();
    }

    @GetMapping("/deleteNote")
    public BaseResponseVO deleteNote(@RequestParam String noteId) {
        noteService.deleteNote(noteId);
        return BaseResponseVO.success();
    }

}
