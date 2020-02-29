package com.ecnu.onion.controller;

import com.ecnu.onion.dao.NoteDao;
import com.ecnu.onion.domain.Note;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author onion
 * @date 2020/1/27 -11:09 上午
 */
@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;
    @Autowired
    private NoteDao noteDao;
    @PostMapping("/test")
    public BaseResponseVO test(@RequestBody Note note){
        noteDao.save(note);
        return BaseResponseVO.success(note);
    }
    @GetMapping("/byEmail")
    public BaseResponseVO searchByAuthorEmail(@RequestParam String email, @RequestParam(defaultValue = "1")Integer page) {
        Page<Note> notes = noteService.findByAuthorEmail(email, page);
        return BaseResponseVO.success(notes);
    }
    @GetMapping("/byKeyword")
    public BaseResponseVO searchByKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1")Integer page) {
        Page<Note> notes = noteService.findByKeyword(keyword, page);
        return BaseResponseVO.success(notes);
    }
    @GetMapping("/byTag")
    public BaseResponseVO searchByTag(@RequestParam String tag, @RequestParam(defaultValue = "1")Integer page) {
        Page<Note> notes = noteService.findByTag(tag, page);
        return BaseResponseVO.success(notes);
    }
}
