package com.ecnu.onion.controller;

import com.ecnu.onion.domain.Note;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author onion
 * @date 2020/1/27 -11:09 上午
 */
@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;
    @GetMapping("/search")
    public BaseResponseVO searchByKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1")Integer page) {
        Page<Note> notes = noteService.findByKeyword(keyword, page);
        return BaseResponseVO.success(notes);
    }
}
