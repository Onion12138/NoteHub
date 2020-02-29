package com.ecnu.onion.service;

import com.ecnu.onion.domain.Note;
import org.springframework.data.domain.Page;

/**
 * @author onion
 * @date 2020/1/27 -11:09 上午
 */
public interface NoteService {
    Page<Note> findByAuthorEmail(String email, int page);

    Page<Note> findByKeyword(String keyword, int page);

    Page<Note> findByTag(String tag, int page);

}
