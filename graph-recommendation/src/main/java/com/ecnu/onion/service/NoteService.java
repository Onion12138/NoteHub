package com.ecnu.onion.service;

import com.ecnu.onion.domain.entity.NoteInfo;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:12 下午
 */
public interface NoteService {
    List<NoteInfo> recommend(String noteId);

    void updateNote(String oldNoteId, String newNoteId, String title);

    String jumpToLatest(String noteId);

    void rollback(String currentVersion, String rollbackVersion);

    List<NoteInfo> historyVersion(String noteId);

    void deleteNote(String noteId);
}
