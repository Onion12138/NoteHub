package com.ecnu.onion.service;

import com.ecnu.onion.domain.Note;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/27 -11:09 上午
 */
public interface NoteService {
    List<Note> findByAuthorEmail(String email, int page);

    List<Note> findByAuthorName(String username, int page);

    List<Note> findByKeyword(String keyword, int page);

    List<Note> findByTag(String tag, int page);

    void deleteNote(String noteId);
}
