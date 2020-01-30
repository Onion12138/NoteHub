package com.ecnu.onion.service.impl;

import com.ecnu.onion.dao.NoteInfoDao;
import com.ecnu.onion.domain.entity.NoteInfo;
import com.ecnu.onion.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:13 下午
 */
@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteInfoDao noteInfoDao;

    @Override
    public List<NoteInfo> recommend(String noteId) {
        List<NoteInfo> recommendNotes = noteInfoDao.findNearestRelatedNotes(noteId);
        if (recommendNotes == null) {
            recommendNotes = new ArrayList<>();
        }
        recommendNotes.addAll( noteInfoDao.findNearbyRelatedNotes(noteId));
        return recommendNotes;
    }

    @Override
    public void updateNote(String oldNoteId, String newNoteId, String title) {
        noteInfoDao.updateNote(oldNoteId, newNoteId, title, LocalDate.now().toString());
    }

    @Override
    public String jumpToLatest(String noteId) {
        return noteInfoDao.jumpToLatest(noteId);
    }

    @Override
    public void rollback(String currentVersion, String rollbackVersion) {
        noteInfoDao.rollback(currentVersion, rollbackVersion, LocalDate.now().toString());
    }

    @Override
    public List<NoteInfo> historyVersion(String noteId) {
        return noteInfoDao.historyVersion(noteId);
    }

}
