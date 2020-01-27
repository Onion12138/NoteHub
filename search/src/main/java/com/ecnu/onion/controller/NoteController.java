package com.ecnu.onion.controller;

import com.ecnu.onion.domain.Note;
import com.ecnu.onion.service.NoteService;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/27 -11:09 上午
 */
@RestController
@RequestMapping("/search")
public class NoteController {
    @Autowired
    private NoteService noteService;
    @GetMapping("/authorEmail")
    public BaseResponseVO searchByAuthorEmail(String email) {
        List<Note> notes = noteService.findByAuthorEmail(email);
        return BaseResponseVO.success(notes);
    }
}
