package com.ecnu.onion.service;

import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.ModificationVO;
import com.ecnu.onion.vo.RegisterVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

/**
 * @author onion
 * @date 2020/1/27 -5:49 下午
 */
public interface UserService {

    void register(RegisterVO registerVO);

    void activate(String code);

    Map<String, Object> login(LoginVO loginVO);

    String uploadProfile(String email, MultipartFile file);

    void modifyPassword(String email, ModificationVO modificationVO);

    void sendCode(String email);

    void collectNote(String email, CollectNote collectNote);

    void chooseTags(String email, Set<String> tags);

    void cancelCollectNote(String email, String noteId);

    void addIndex(String email, String mindMap);

    void updateIndex(String email, Map<String, String> map);

    MindMap findOneIndex(String email, Integer num);

    void deleteIndex(String email, Integer num);
}
