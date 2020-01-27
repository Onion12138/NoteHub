package com.ecnu.onion.service;

import com.ecnu.onion.domain.Note;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/27 -11:12 上午
 */
@Service
public class NoteServiceImpl implements NoteService {
    @Override
    public List<Note> findByAuthorEmail(String email) {
        return null;
    }
}
