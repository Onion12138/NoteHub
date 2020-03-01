package com.ecnu.onion.service;

import com.ecnu.onion.domain.mongo.Note;
import com.ecnu.onion.vo.AnalysisVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
public interface NoteService {

    String publishNote(AnalysisVO analyze, Map<String, String> map);

    void updateNote(AnalysisVO analyze, Map<String, String> map);

    void deleteNote(String noteId);

    Note findOneNote(String email, String noteId);

    void changeAuthority(String noteId, String authority);

    void starOrHate(String type, String noteId, String email);

    String uploadPicture(String noteId, MultipartFile file);

    Map<Object, Object> getCounter(String noteId);
}
