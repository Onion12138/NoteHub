package com.ecnu.onion.controller;

import com.ecnu.onion.domain.Note;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/27 -11:09 上午
 */
@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;
    @GetMapping("/byEmail")
    public BaseResponseVO searchByAuthorEmail(@RequestParam String email, @RequestParam(defaultValue = "1")Integer page) {
        List<Note> notes = noteService.findByAuthorEmail(email, page);
        return BaseResponseVO.success(notes);
    }
    @GetMapping("/byName")
    public BaseResponseVO searchByAuthorName(@RequestParam String username, @RequestParam(defaultValue = "1")Integer page) {
        List<Note> notes = noteService.findByAuthorName(username, page);
        return BaseResponseVO.success(notes);
    }
    @GetMapping("/byKeyword")
    public BaseResponseVO searchByKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1")Integer page) {
        List<Note> notes = noteService.findByKeyword(keyword, page);
        return BaseResponseVO.success(notes);
    }
    @GetMapping("/byTag")
    public BaseResponseVO searchByTag(@RequestParam String tag, @RequestParam(defaultValue = "1")Integer page) {
        List<Note> notes = noteService.findByTag(tag, page);
        return BaseResponseVO.success(notes);
    }
    @GetMapping("/delete")
    public BaseResponseVO deleteNote(@RequestParam String noteId) {
        noteService.deleteNote(noteId);
        return BaseResponseVO.success();
    }
}
