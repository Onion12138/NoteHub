package com.ecnu.onion.service;

import com.ecnu.onion.domain.Comment;
import com.ecnu.onion.vo.AnalysisVO;
import com.ecnu.onion.vo.NoteResponseVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
public interface NoteService {

    String publishNote(AnalysisVO analyze, Map<String, String> map);

    int updateNote(AnalysisVO analyze, Map<String, String> map);

    void deleteNote(String noteId);

    NoteResponseVO findOneNote(String email, String noteId);

    void changeAuthority(String noteId, String authority);

    String comment(Comment comment);

    void starOrHate(String type, String noteId, String email);

    void deleteComment(String noteId, String commentId);

    String uploadPicture(String noteId, MultipartFile file);
}
