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
@RequestMapping("/graph/note")
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
                                     @RequestParam String newNoteId) {
        noteService.updateNote(oldNoteId, newNoteId);
        return BaseResponseVO.success();
    }

}
